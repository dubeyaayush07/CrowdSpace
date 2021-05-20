package com.crowdspace.crowdspace.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentHospitalBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Hospital
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HospitalFragment : Fragment() {

    private lateinit var binding: FragmentHospitalBinding
    private lateinit var hospital: Hospital
    private lateinit var adapter: BusinessAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hospital, container, false)
        hospital = HospitalFragmentArgs.fromBundle(requireArguments()).selectedHospital
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView: BottomNavigationView =
                requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        val user = FirebaseAuth.getInstance().currentUser
        adapter = BusinessAdapter(BusinessAdapter.OnClickListener {
            if (it.adminId == user?.uid) {
                findNavController().navigate(HospitalFragmentDirections.actionHospitalFragmentToControlCenterFragment(it))
            } else {
                findNavController().navigate(HospitalFragmentDirections.actionHospitalFragmentToQueueFragment(it))
            }
        })

        binding.businessList.adapter = adapter
        binding.addClinicBtn.setOnClickListener {
            findNavController().navigate(HospitalFragmentDirections.actionHospitalFragmentToClientFormFragment(hospital))
        }
        setup(user?.uid.toString())
        fetchBusinesses()
    }

    private fun setup(id: String) {
        hospital.photoUrl?.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
            Glide.with(binding.hospitalImg.context)
                    .load(imgUri)
                    .apply(
                            RequestOptions()
                                    .error(R.drawable.ic_broken_image))
                    .into(binding.hospitalImg)
        }

        binding.hospitalName.text = hospital.name
        if (id == hospital.adminId) {
            binding.addClinicBtn.visibility = View.VISIBLE
        }
    }

    private fun fetchBusinesses() {
        val db = Firebase.firestore
        db.collection("businesses")
                .whereEqualTo("hospitalId", hospital.id)
                .get()
                .addOnSuccessListener { result ->
                    adapter.data = result.toObjects(Business::class.java)
                }
                .addOnFailureListener { exception ->
                    Log.w(HomeFragment.TAG, "Error getting documents.", exception)
                }
    }


}