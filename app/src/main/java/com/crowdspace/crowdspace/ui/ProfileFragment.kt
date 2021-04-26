package com.crowdspace.crowdspace.ui

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentProfileBinding
import com.crowdspace.crowdspace.model.Profile
import com.crowdspace.crowdspace.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: User
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
        user = ProfileFragmentArgs.fromBundle(requireArguments()).user
        collection = Firebase.firestore.collection("profiles")
        if (!user.profileId.isNullOrBlank()) {
            collection.document(user.profileId!!).get().addOnSuccessListener {
                setup(it.toObject(Profile::class.java)!!)
            }

            binding.saveBtn.setOnClickListener {
                updateProfile(user.profileId.toString())
            }
        } else {
            binding.saveBtn.setOnClickListener {
                saveProfile()
            }
        }

    }

    private fun saveProfile() {
        val profile = Profile(
                name = binding.name.text.toString(),
                height = binding.height.text.toString(),
                weight = binding.weight.text.toString(),
                bloodGroup = binding.blood.text.toString()
        )
        collection.add(profile).addOnSuccessListener {
            Firebase.firestore.collection("users").document(user.id.toString())
                    .update("profileId", it.id).addOnSuccessListener {
                        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
                    }.addOnFailureListener {
                        Snackbar.make(
                                binding.root,
                                "Failed to save your Profile",
                                Snackbar.LENGTH_LONG
                        ).show()
                    }
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
        val profile = Profile(
                name = binding.name.text.toString(),
                height = binding.height.text.toString(),
                weight = binding.weight.text.toString(),
                bloodGroup = binding.blood.text.toString()
        )
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