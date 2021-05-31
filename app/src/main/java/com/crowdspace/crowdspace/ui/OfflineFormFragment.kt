package com.crowdspace.crowdspace.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentOfflineFormBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Form
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class OfflineFormFragment : Fragment() {


    private lateinit var  binding: FragmentOfflineFormBinding
    private lateinit var business: Business

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offline_form, container, false)
        business = OfflineFormFragmentArgs.fromBundle(requireArguments()).selectedBusiness
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitBtn.setOnClickListener {
            if (checkPassed()) {
                saveForm()
            }
        }

    }

    private fun checkPassed(): Boolean {
        when {
            binding.name.text.isNullOrBlank() -> {
                Snackbar.make(
                    binding.root,
                    "Name Required",
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            binding.number.text.isNullOrBlank() -> {
                Snackbar.make(
                    binding.root,
                    "Height Required",
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            else -> return true
        }

    }


    private fun saveForm() {
        binding.submitBtn.isEnabled = false
        val collection = Firebase.firestore.collection("forms")
        val form = Form(
            uid = binding.number.toString(),
            bid = business.id,
            bName = business.name,
            name = binding.name.text.toString()
        )

        collection.add(form)
            .addOnSuccessListener {
                val doc = Firebase.firestore.collection("businesses").document(business.id.toString())
                doc.update("queue", FieldValue.arrayUnion(it.id)).addOnSuccessListener {
                    doc.get().addOnSuccessListener { ref ->
                        val result = ref.toObject(Business::class.java)
                        findNavController().navigate(OfflineFormFragmentDirections.actionOfflineFormFragmentToControlCenterFragment(result!!))
                    }
                }.addOnFailureListener {
                    Snackbar.make(binding.root, "Form submission failed", Snackbar.LENGTH_LONG)
                        .show()

                    binding.submitBtn.isEnabled = true
                }
            }.addOnFailureListener {
                Snackbar.make(binding.root, "Form submission failed", Snackbar.LENGTH_LONG)
                    .show()

                binding.submitBtn.isEnabled = true
            }
    }





}