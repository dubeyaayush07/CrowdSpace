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
import com.crowdspace.crowdspace.databinding.FragmentFormListBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Form
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FormListFragment : Fragment() {

    private lateinit var binding: FragmentFormListBinding
    private lateinit var adapter: FormAdapter
    private lateinit var collection: CollectionReference

    companion object {
        const val TAG = "FormListFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bottomNavigationView: BottomNavigationView =
            requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collection = Firebase.firestore.collection("businesses")
        val user = FirebaseAuth.getInstance().currentUser
        adapter = FormAdapter(FormAdapter.OnClickListener {
            collection.document(it.bid.toString()).get().addOnSuccessListener {
                val business = it.toObject(Business::class.java)
                findNavController().navigate(FormListFragmentDirections.actionFormListFragmentToQueueFragment(business!!))
            }
        })
        binding.formList.adapter = adapter
        val active = FormListFragmentArgs.fromBundle(requireArguments()).active
        fetchForms(user?.uid.toString(), active)
    }

    private fun fetchForms(uid: String, active: Boolean) {
        val db = Firebase.firestore
        db.collection("forms")
            .whereEqualTo("uid", uid)
            .whereEqualTo("active", active)
            .get().addOnSuccessListener { result ->
                adapter.data = result.toObjects(Form::class.java)
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

}