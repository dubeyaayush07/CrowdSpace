package com.crowdspace.crowdspace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentControlCenterBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ControlCenterFragment : Fragment() {

    private lateinit var binding: FragmentControlCenterBinding
    private var business: Business? = null
    private lateinit var userCollection: CollectionReference
    private lateinit var businessCollection: CollectionReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_center, container, false)
        val bottomNavigationView: BottomNavigationView =
                requireActivity().findViewById(R.id.bottomNavView)
        bottomNavigationView.visibility = View.GONE
        business = ControlCenterFragmentArgs.fromBundle(requireArguments()).selectedBusiness

        userCollection = Firebase.firestore.collection("users")
        businessCollection = Firebase.firestore.collection("businesses")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        update()


        binding.openBtn.setOnClickListener {
            val doc = businessCollection.document(business!!.id.toString())
            doc.update("status", "open").addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    business = it.toObject(Business::class.java)
                    update()
                }
            }
        }

        binding.closeBtn.setOnClickListener {
            val doc = businessCollection.document(business!!.id.toString())
            doc.update("status", "closed").addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    business = it.toObject(Business::class.java)
                    update()
                }
            }
        }

        binding.next.setOnClickListener {
            val doc = businessCollection.document(business!!.id.toString())
            val uid = business?.queue?.get(0)
            doc.update("queue", FieldValue.arrayRemove(uid), "status", "open").addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    business = it.toObject(Business::class.java)
                    update()
                }
            }
        }


    }

    private fun setup() {
        binding.businessName.text = business?.name

        business?.photoUrl?.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
            Glide.with(binding.businessImg.context)
                    .load(imgUri)
                    .apply(
                            RequestOptions()
                                    .error(R.drawable.ic_broken_image))
                    .into(binding.businessImg)
        }

    }

    private fun update() {
        if (business == null) return

        binding.status.text = business?.status
        binding.queueSize.text = "Queue Size ${business?.queue?.size.toString()}"

        binding.openBtn.visibility = View.GONE
        binding.closeBtn.visibility = View.GONE
        binding.next.visibility = View.GONE


        if (business?.status == "closed") {
            binding.currentPatient.text = "Open clinic to see the current patient"
            binding.openBtn.visibility = View.VISIBLE

        } else {
            binding.closeBtn.visibility = View.VISIBLE
            if (business?.queue?.size == 0) {
                binding.currentPatient.text = "No patient in the queue"
            } else {
                binding.next.visibility = View.VISIBLE
                setUser()
            }
        }
    }

    private fun setUser() {
        val uid = business?.queue?.get(0)
        userCollection.whereEqualTo("uid", uid).get()
                .addOnSuccessListener {
                    val name = it.documents.get(0).toObject(User::class.java)?.name
                    binding.currentPatient.text = "Current Patient: $name"
                }
                .addOnFailureListener {
                    binding.currentPatient.text = "Unable to fetch patient"
                }
    }

}