package com.crowdspace.crowdspace.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentControlCenterBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Form
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ControlCenterFragment : Fragment() {

    var white = 0xFFFFFFFF
    var black = 0xFF000000
    val width = 500

    private lateinit var binding: FragmentControlCenterBinding
    private lateinit  var business: Business
    private lateinit var formCollection: CollectionReference
    private lateinit var businessCollection: CollectionReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_center, container, false)
        val bottomNavigationView: BottomNavigationView =
                requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        business = ControlCenterFragmentArgs.fromBundle(requireArguments()).selectedBusiness

        formCollection = Firebase.firestore.collection("forms")
        businessCollection = Firebase.firestore.collection("businesses")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        update()


        binding.openBtn.setOnClickListener {
            pickDateTime("open", "Set Closing Time")
        }

        binding.closeBtn.setOnClickListener {
            pickDateTime("closed", "Set Opening Time")
        }

        binding.next.setOnClickListener {
            val doc = businessCollection.document(business.id.toString())
            val uid = business.queue?.get(0)
            hideButtons()
            doc.update("queue", FieldValue.arrayRemove(uid), "status", "open").addOnSuccessListener {
                formCollection.document(uid.toString()).update("active", false)
            }.addOnFailureListener {
                Toast.makeText(context, "Operation Failed Please Try Again", Toast.LENGTH_LONG).show()
                update()
            }
        }

        binding.viewQueue.setOnClickListener {
            findNavController().navigate(ControlCenterFragmentDirections.actionControlCenterFragmentToQueueFormsFragment(business))
        }

        binding.addOffline.setOnClickListener {
            findNavController().navigate(ControlCenterFragmentDirections.actionControlCenterFragmentToOfflineFormFragment(business))
        }

        val docRef = businessCollection.document(business.id.toString())
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(QueueFragment.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                business = snapshot.toObject(Business::class.java)!!
                update()

            } else {
                Log.d(QueueFragment.TAG, "Current data: null")
            }
        }



        try {
            val bitmap = encodeAsBitmap(business?.id.toString())
            binding.shareQr.setImageBitmap(bitmap)
            binding.shareQr.setOnClickListener {
                var uri = bitmap?.let { it1 -> saveImage(it1) }
                shareQr(uri)
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }

    private fun pickDateTime(status: String, title: String) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        val dialog = DatePickerDialog(requireContext(), android.R.style.Theme_Material_Light_Dialog, { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                val till = SimpleDateFormat("HH:mm:ss EEE dd MMM ").format(pickedDateTime.time)
                hideButtons()
                val doc = businessCollection.document(business.id.toString())
                doc.update("status", status, "till", till).addOnFailureListener {
                    Toast.makeText(context, "Operation Failed Please Try Again", Toast.LENGTH_LONG).show()
                    update()
                }
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay)

        dialog.setTitle(title)
        dialog.show()
    }

    private fun setup() {
        binding.businessName.text = business.name
    }

    private fun update() {

        binding.status.text = business.status
        binding.queueSize.text = "Queue Size ${business?.queue?.size.toString()}"
        hideButtons()

        if (business.status == "closed") {
            binding.currentPatient.text = "Open clinic to see the current patient"
            binding.time.text = "Opening Time: ${business.till}"
            binding.openBtn.visibility = View.VISIBLE

        } else {
            if (business.queue?.size == 0) {
                binding.closeBtn.visibility = View.VISIBLE
                binding.time.text = "Closing Time: ${business.till}"
                binding.addOffline.visibility = View.VISIBLE
                binding.currentPatient.text = "No patient in the queue"
            } else {
                setUser()
            }
        }
    }

    private fun setUser() {
        val uid = business.queue?.get(0).toString()
        formCollection.document(uid).get()
                .addOnSuccessListener {
                    val form = it.toObject(Form::class.java)
                    if (form != null) {
                        binding.closeBtn.visibility = View.VISIBLE
                        binding.time.text = "Closing Time: ${business.till}"
                        binding.addOffline.visibility = View.VISIBLE
                        binding.next.visibility = View.VISIBLE
                        binding.contactNumber.visibility = View.VISIBLE
                        binding.currentPatient.text = "Current Patient: ${form.name}"
                        binding.contactNumber.text = "Contact Number: ${form.contact}"
                        binding.viewInfo.visibility = View.VISIBLE
                        binding.viewInfo.setOnClickListener {
                            findNavController().navigate(ControlCenterFragmentDirections.actionControlCenterFragmentToFormDisplayFragment(form))
                        }
                    } else {
                        binding.currentPatient.text = "Unable to fetch patient"
                    }

                }
                .addOnFailureListener {
                    binding.currentPatient.text = "Unable to fetch patient"
                }


    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(str: String?): Bitmap? {
        val result: BitMatrix = try {
            MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, null)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = (if (result[x, y]) black else white).toInt()
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h)
        return bitmap
    }

    private fun shareQr(uri: Uri?) {
        if (uri != null) {
            var intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/png"
            intent = Intent.createChooser(intent, "Share Qr code")
            startActivity(intent)
        }
    }

    private fun saveImage(bitmap: Bitmap): Uri? {
        val imagesFolder: File = File(context?.cacheDir, "codes")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_code.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
             uri = FileProvider.getUriForFile(
                     requireContext(),
                     context?.applicationContext?.packageName + ".provider",
                     file
             )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    private fun hideButtons() {
        binding.openBtn.visibility = View.GONE
        binding.closeBtn.visibility = View.GONE
        binding.next.visibility = View.GONE
        binding.viewInfo.visibility = View.GONE
        binding.contactNumber.visibility = View.GONE
        binding.addOffline.visibility = View.GONE
    }


}