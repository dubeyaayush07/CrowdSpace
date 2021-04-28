package com.crowdspace.crowdspace.ui

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.MainFragmentDirections
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentProfileBinding
import com.crowdspace.crowdspace.model.Profile
import com.crowdspace.crowdspace.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var uid: String
    private lateinit var collection: CollectionReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val bottomNavigationView: BottomNavigationView =
            requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        collection = Firebase.firestore.collection("profiles")
        collection.whereEqualTo("uid", uid).get().addOnSuccessListener {
            if (it.isEmpty) {
                binding.saveBtn.setOnClickListener {
                    saveProfile()
                }
            } else {
                val pro = it.toObjects(Profile::class.java)[0]
                setup(pro)
                binding.saveBtn.setOnClickListener {
                    updateProfile(pro.id.toString())
                }
            }
        }
    }

    private fun saveProfile() {
        val profile = Profile(
                name = binding.name.text.toString(),
                height = binding.height.text.toString(),
                weight = binding.weight.text.toString(),
                bloodGroup = binding.blood.text.toString(),
                uid = uid
        )
        collection.add(profile).addOnSuccessListener {
            requireActivity().onBackPressed()
        }.addOnFailureListener {
            Snackbar.make(
                    binding.root,
                    "Failed to save your Profile",
                    Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun setup(profile: Profile) {
        binding.name.setText(profile.name.toString())
        binding.height.setText(profile.height.toString())
        binding.weight.setText(profile.weight.toString())
        binding.blood.setText(profile.bloodGroup.toString())
    }

    private fun updateProfile(id: String) {
        collection.document(id).update("name",  binding.name.text.toString(),
                "height", binding.height.text.toString(),
                "weight", binding.weight.text.toString(),
                "bloodGroup", binding.blood.text.toString())
                .addOnSuccessListener {
                    Snackbar.make(
                            binding.root,
                            "Profile Saved",
                            Snackbar.LENGTH_LONG
                    ).show()
                }.addOnFailureListener() {
                    Snackbar.make(
                            binding.root,
                            "Failed to save your Profile",
                            Snackbar.LENGTH_LONG
                    ).show()
                }
    }


}