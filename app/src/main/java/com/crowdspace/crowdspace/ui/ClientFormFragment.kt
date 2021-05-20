package com.crowdspace.crowdspace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentClientFormBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Hospital
import com.crowdspace.crowdspace.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ClientFormFragment : Fragment() {

    private lateinit var binding: FragmentClientFormBinding
    private lateinit var hospital: Hospital

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_form, container, false)
        hospital = ClientFormFragmentArgs.fromBundle(requireArguments()).selectedHospital
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitBtn.setOnClickListener {
            if (checkPassed()) {
                saveClinic()
            }
        }
    }

    private fun saveClinic() {
        binding.submitBtn.isEnabled = false

        val email = binding.doctorEmailId.text.toString()

        Firebase.firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    val id = it.documents[0].toObject(User::class.java)?.uid
                    val business = Business(
                        name = binding.doctorName.text.toString(),
                        hospitalId = hospital.id,
                        adminId = id
                    )

                    Firebase.firestore.collection("businesses").add(business)
                        .addOnSuccessListener {
                            findNavController().navigate(ClientFormFragmentDirections.actionClientFormFragmentToHospitalFragment(hospital))
                        }.addOnFailureListener {
                            Snackbar.make(
                                binding.root,
                                "Operation Failed Please Try Again",
                                Snackbar.LENGTH_LONG
                            ).show()
                            binding.submitBtn.isEnabled = true
                        }

                } else {
                    Snackbar.make(
                        binding.root,
                        "Invalid Email Id",
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.submitBtn.isEnabled = true
                }
            }.addOnFailureListener {
                Snackbar.make(
                    binding.root,
                    "Invalid Email Id",
                    Snackbar.LENGTH_LONG
                ).show()
                binding.submitBtn.isEnabled = true
            }
    }

    private fun checkPassed(): Boolean {
        when {
            binding.doctorName.text.isNullOrBlank() -> {
                Snackbar.make(
                    binding.root,
                    "Name Required",
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            binding.doctorEmailId.text.isNullOrBlank() -> {
                Snackbar.make(
                    binding.root,
                    "Email Id Required",
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            else -> return true
        }

    }


}