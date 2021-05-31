package com.crowdspace.crowdspace.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crowdspace.crowdspace.CaptureActivity
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentQueueBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Form
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*


class QueueFragment : Fragment() {

    private lateinit var binding: FragmentQueueBinding
    private lateinit  var business: Business
    private lateinit var collection: CollectionReference
    private var formId: String = ""


    companion object {
        const val TAG = "QueueFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val bottomNavigationView: BottomNavigationView =
            requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_queue, container, false)
        business = QueueFragmentArgs.fromBundle(requireArguments()).selectedBusiness
        collection = Firebase.firestore.collection("businesses")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = FirebaseAuth.getInstance().currentUser?.uid
        Firebase.firestore.collection("forms")
                .whereEqualTo("bid", business.id)
                .whereEqualTo("uid", id)
                .whereEqualTo("active", true)
                .get().addOnSuccessListener {
                    val objects = it.toObjects(Form::class.java)
                    if (objects.size > 0) {
                        formId = objects[0].id.toString()
                    }
                    checkIndex(formId)
                }
        setup()


        binding.addBtn.setOnClickListener { view ->
            findNavController().navigate(QueueFragmentDirections.actionQueueFragmentToFormFragment(business))
        }

        binding.scanBtn.setOnClickListener {
            scanQRCode()
        }


        val docRef = collection.document(business.id.toString())
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists() && !formId.isBlank()) {
                business = snapshot.toObject(Business::class.java)!!
                checkIndex(formId)

            } else {
                Log.d(TAG, "Current data: null")
            }
        }


    }

    private fun setup() {
        binding.businessName.text = business.name

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
        if (businessId == business.id) {
            binding.scanBtn.visibility = View.GONE
            val doc = collection.document(business.id.toString())
            doc.update("status", "occupied").addOnFailureListener {
                binding.scanBtn.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(context, "Invalid Code", Toast.LENGTH_LONG).show()
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

        if (business.status == "closed") {
            binding.time.text = "Opening Time: ${business.till}"
        } else {
            binding.time.text = "Closing Time: ${business.till}"
        }


        // not found in queue
        if (index == -1) {
            if (business.status == "closed") {
                binding.index.text = "Not Found in Queue \nYou can register when the clinic opens"
            } else {
                binding.addBtn.visibility = View.VISIBLE
                binding.index.text = getString(R.string.not_found_in_queue)
            }
        } else {
            if (index != 0) binding.index.text = "$index People ahead of you\nApproximate waiting time ${index * business.avgTime!!} minutes"
            else if (business.status == "open") {
                binding.index.text = "It is your turn! Scan QR code with your phone and enter"
                binding.scanBtn.visibility = View.VISIBLE
            } else if (business.status == "occupied") {
                binding.index.text = "You are in!"
            } else {
                binding.index.text = "It is your turn! Please wait for the business to open again"
            }
        }
    }


}