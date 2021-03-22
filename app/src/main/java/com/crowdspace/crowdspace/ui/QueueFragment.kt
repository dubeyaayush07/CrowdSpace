package com.crowdspace.crowdspace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentQueueBinding
import com.crowdspace.crowdspace.model.Business
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class QueueFragment : Fragment() {

    private lateinit var binding: FragmentQueueBinding
    private lateinit var business: Business

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
//        val businessDocument = Firebase.firestore.collection("businesses")
//            .document(business.id.toString()).update()
    }


}