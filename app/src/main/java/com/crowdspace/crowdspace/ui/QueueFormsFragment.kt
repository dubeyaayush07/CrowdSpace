package com.crowdspace.crowdspace.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentQueueFormsBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Form
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class QueueFormsFragment : Fragment() {

    private lateinit var binding: FragmentQueueFormsBinding
    private lateinit var business: Business
    private lateinit var adapter: QueueFormsAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_queue_forms, container, false)
        business = QueueFormsFragmentArgs.fromBundle(requireArguments()).selectedBusiness
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = QueueFormsAdapter()
        binding.formList.adapter = adapter
        fetchForms()
    }

    private fun fetchForms() {
        val db = Firebase.firestore
        db.collection("forms")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .whereEqualTo("bid", business.id)
                .whereEqualTo("active", true)
                .get().addOnSuccessListener { result ->
                    binding.progressBar.visibility = View.GONE
                    adapter.data = result.toObjects(Form::class.java)
                }.addOnFailureListener { exception ->
                    Log.w(FormListFragment.TAG, "Error getting documents.", exception)
                }
    }

}