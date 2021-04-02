package com.crowdspace.crowdspace.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.crowdspace.crowdspace.CaptureActivity
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentQueueBinding
import com.crowdspace.crowdspace.model.Business
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator


class QueueFragment : Fragment() {

    private lateinit var binding: FragmentQueueBinding
    private  var business: Business? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_queue, container, false)
        val bottomNavigationView: BottomNavigationView =
                requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        business = QueueFragmentArgs.fromBundle(requireArguments()).selectedBusiness
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser
        checkIndex(user?.uid.toString())

        binding.addBtn.setOnClickListener { view ->
            val doc = Firebase.firestore.collection("businesses").document(business!!.id.toString())
            doc.update("queue", FieldValue.arrayUnion(user?.uid)).addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    business = it.toObject(Business::class.java)
                    checkIndex(user?.uid.toString())
                }
            }
        }

        binding.scanBtn.setOnClickListener {
            scanQRCode()
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
        Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
        if (result != null) {
            if (result.contents == null) Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
            else Toast.makeText(context, result.contents, Toast.LENGTH_LONG).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkIndex(id: String) {
        val index = business!!.queue!!.indexOfFirst {
            it == id
        }

        if (index == -1) {
            binding.addBtn.visibility = View.VISIBLE
            binding.index.text = getString(R.string.not_found_in_queue)

        } else {
            binding.addBtn.visibility = View.GONE
            if (index == 0) {
                binding.index.text = "It is your turn! Scan QR code with your phone and enter"
                binding.scanBtn.visibility = View.VISIBLE
            }
            else binding.index.text = "$index People ahead of you"
        }
    }


}