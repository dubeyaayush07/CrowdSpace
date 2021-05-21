package com.crowdspace.crowdspace.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentHomeBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Hospital
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HospitalAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
                object : OnBackPressedCallback(true /* enabled by default */) {
                    override fun handleOnBackPressed() {
                        requireActivity().finish()
                    }
                }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.VISIBLE

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val once = sharedPref.getBoolean(getString(R.string.once), false)
        if (!once) {
            checkProfile(sharedPref)
        }

        val tabLayout = binding.tabLayout

        val adapter = PagerAdapter(this)
        binding.pager.adapter = adapter

        TabLayoutMediator(tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = "Hospitals"
                1 -> tab.text = "Appointments"
            }
        }.attach()


    }


    private fun checkProfile(sharedPref: SharedPreferences) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        Firebase.firestore.collection("profiles")
                .whereEqualTo("uid", uid).get().addOnSuccessListener {
                    if (it.isEmpty) {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
                        with (sharedPref.edit()) {
                            putBoolean(getString(R.string.once), true)
                            apply()
                        }
                    }
                }
    }

    class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)
            return if (position == 0) HospitalListFragment()
            else AppointmentListFragment()
        }
    }

}