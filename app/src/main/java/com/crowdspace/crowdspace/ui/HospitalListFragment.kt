package com.crowdspace.crowdspace.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentHospitalListBinding
import com.crowdspace.crowdspace.model.Hospital
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HospitalListFragment : Fragment() {

    companion object {
        const val TAG = "HospitalListFragment"
    }

    private lateinit var binding: FragmentHospitalListBinding
    private lateinit var adapter: HospitalAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hospital_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HospitalAdapter(HospitalAdapter.OnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHospitalFragment(it))
        })

        binding.hospitalList.adapter = adapter
        fetchHospitals()
    }


    private fun fetchHospitals() {
        val db = Firebase.firestore
        db.collection("hospitals")
            .get()
            .addOnSuccessListener { result ->
                adapter.data = result.toObjects(Hospital::class.java)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.w(HomeFragment.TAG, "Error getting documents.", exception)
            }
    }




}