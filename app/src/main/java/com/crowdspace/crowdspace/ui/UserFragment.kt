package com.crowdspace.crowdspace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import soup.neumorphism.NeumorphCardView


class UserFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
                object : OnBackPressedCallback(true /* enabled by default */) {
                    override fun handleOnBackPressed() {
                        findNavController().navigate(UserFragmentDirections.actionUserFragmentToHomeFragment())
                    }
                }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val bottomNavigationView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<NeumorphCardView>(R.id.cardPunch).setOnClickListener {
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToProfileFragment())

        }

        view.findViewById<NeumorphCardView>(R.id.cardWeight).setOnClickListener {
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToFormListFragment(true))
        }

        view.findViewById<NeumorphCardView>(R.id.cardYoga).setOnClickListener {
            findNavController().navigate(UserFragmentDirections.actionUserFragmentToFormListFragment(false))
        }
    }


}