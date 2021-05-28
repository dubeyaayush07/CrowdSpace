package com.crowdspace.crowdspace.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.crowdspace.crowdspace.R
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
                object : OnBackPressedCallback(true /* enabled by default */) {
                    override fun handleOnBackPressed() {
                        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
                    }
                }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        val pref = findPreference<Preference>("preference_key")
        pref?.setOnPreferenceClickListener {
            signOut()
            return@setOnPreferenceClickListener true
        }
    }


    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToMainFragment())
    }


}