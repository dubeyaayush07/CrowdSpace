package com.crowdspace.crowdspace.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentQueueBinding
import com.crowdspace.crowdspace.model.Business
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class QueueFragment : Fragment() {

    private lateinit var binding: FragmentQueueBinding
    private  var business: Business? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_queue, container, false)
        val bottomNavigationView: BottomNavigationView =
                requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        business = QueueFragmentArgs.fromBundle(requireArguments()).selectedBusiness
        val user = FirebaseAuth.getInstance().currentUser
        check(user?.uid.toString())

        binding.addBtn.setOnClickListener { view ->
            val doc = Firebase.firestore.collection("businesses")
                    .document(business!!.id.toString())
            doc.update("queue", FieldValue.arrayUnion(user?.uid)).addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    business = it.toObject(Business::class.java)
                    check(user?.uid.toString())
                }
            }
        }



    }

    private fun check(id: String) {
        val index = business!!.queue!!.indexOfFirst {
            it == id
        }

        if (index == -1) {
            binding.addBtn.visibility = View.VISIBLE

        } else {
            binding.addBtn.visibility = View.GONE
            binding.index.text = (index + 1).toString()
        }
    }


}