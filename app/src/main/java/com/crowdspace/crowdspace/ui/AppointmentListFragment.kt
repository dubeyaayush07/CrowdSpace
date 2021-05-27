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
import com.crowdspace.crowdspace.databinding.FragmentAppointmentListBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Form
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AppointmentListFragment : Fragment() {

    private lateinit var binding: FragmentAppointmentListBinding
    private lateinit var adapter: FormAdapter
    private lateinit var collection: CollectionReference

    companion object {
        const val TAG = "AppointmentListFragment"
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collection = Firebase.firestore.collection("businesses")
        val user = FirebaseAuth.getInstance().currentUser
        adapter = FormAdapter(FormAdapter.OnClickListener {
            collection.document(it.bid.toString()).get().addOnSuccessListener {
                val business = it.toObject(Business::class.java)
               findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToQueueFragment(business!!))
            }
        })
        binding.formList.adapter = adapter
        fetchForms(user?.uid.toString(), true)
    }

    private fun fetchForms(uid: String, active: Boolean) {
        val db = Firebase.firestore
        db.collection("forms")
            .whereEqualTo("uid", uid)
            .whereEqualTo("active", active)
            .get().addOnSuccessListener { result ->
                    binding.progressBar.visibility = View.GONE
                    adapter.data = result.toObjects(Form::class.java)
            }.addOnFailureListener { exception ->
                Log.w(FormListFragment.TAG, "Error getting documents.", exception)
            }
    }


}