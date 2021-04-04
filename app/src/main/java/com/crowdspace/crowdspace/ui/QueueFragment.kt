package com.crowdspace.crowdspace.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crowdspace.crowdspace.CaptureActivity
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentQueueBinding
import com.crowdspace.crowdspace.model.Business
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator


class QueueFragment : Fragment() {

    private lateinit var binding: FragmentQueueBinding
    private var business: Business? = null
    private lateinit var collection: CollectionReference
    private var userId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_queue, container, false)
        val bottomNavigationView: BottomNavigationView =
                requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        business = QueueFragmentArgs.fromBundle(requireArguments()).selectedBusiness
        collection = Firebase.firestore.collection("businesses")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        setup()
        checkIndex(userId)

        binding.addBtn.setOnClickListener { view ->
            val doc = collection.document(business!!.id.toString())
            doc.update("queue", FieldValue.arrayUnion(userId)).addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    business = it.toObject(Business::class.java)
                    checkIndex(userId)
                }
            }
        }

        binding.scanBtn.setOnClickListener {
            scanQRCode()
        }

    }

    private fun setup() {

        binding.businessName.text = business?.name

        business?.photoUrl?.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
            Glide.with(binding.businessImg.context)
                    .load(imgUri)
                    .apply(
                            RequestOptions()
                                    .error(R.drawable.ic_broken_image))
                    .into(binding.businessImg)
        }

    }

    private fun scanQRCode() {
        val integrator = IntentIntegrator.forSupportFragment(this).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt("Scanning Code")
        }
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
            else verify(result.contents)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun verify(businessId: String) {
        if (businessId == business?.id) {
            val doc = collection.document(business!!.id.toString())
            doc.update("status", "occupied").addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    business = it.toObject(Business::class.java)
                    checkIndex(userId)
                }
            }
        }
    }

    private fun checkIndex(id: String) {

        if (business == null) return

        val index = business!!.queue!!.indexOfFirst {
            it == id
        }

        binding.addBtn.visibility = View.GONE
        binding.scanBtn.visibility = View.GONE

        binding.status.text = business?.status
        binding.queueSize.text = "Queue Size ${business?.queue?.size.toString()}"


        // not found in queue
        if (index == -1) {
            binding.addBtn.visibility = View.VISIBLE
            binding.index.text = getString(R.string.not_found_in_queue)

        } else {
            if (index != 0) binding.index.text = "$index People ahead of you"
            else if (business?.status == "open") {
                binding.index.text = "It is your turn! Scan QR code with your phone and enter"
                binding.scanBtn.visibility = View.VISIBLE
            } else if (business?.status == "occupied") {
                binding.index.text = "You are in!"
            } else {
                binding.index.text = "It is your turn! Please wait for the business to open again"
            }
        }
    }


}