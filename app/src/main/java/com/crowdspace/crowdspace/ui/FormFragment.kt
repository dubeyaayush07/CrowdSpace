


package com.crowdspace.crowdspace.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentFormBinding
import com.crowdspace.crowdspace.model.Business
import com.crowdspace.crowdspace.model.Form
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class FormFragment : Fragment() {

    private lateinit var binding: FragmentFormBinding
    private lateinit var business: Business

    private val IMAGE_UPLOAD_REQUEST_CODE: Int = 2003
    private var url: String = ""


    companion object {
        const val TAG = "FormFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        business = FormFragmentArgs.fromBundle(requireArguments()).selectedBusiness
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitBtn.setOnClickListener {
            if (checkPassed()) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null ) saveForm(userId)
            }
        }

        binding.upload.setOnClickListener {
            launchUpload()
        }
    }

    private fun launchUpload() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            startActivityForResult(this, IMAGE_UPLOAD_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_UPLOAD_REQUEST_CODE) {
                if (data != null && data.data != null) {
                    val uri = data.data
                    var user = FirebaseAuth.getInstance().currentUser
                    if (user != null && uri != null) {
                        uploadPhoto(uri, user.uid)
                    }
                }
            }
        } else {
            Snackbar.make(binding.root, "Operation Failed", Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    private fun uploadPhoto(uri: Uri, userId: String) {
        binding.submitBtn.isEnabled = false
        Snackbar.make(binding.root, "Uploading...", Snackbar.LENGTH_LONG)
                .show()

        val storageReference = FirebaseStorage.getInstance().reference

        val imageRef = storageReference.child("images/" + userId + "/" + uri.lastPathSegment)
        val uploadTask = imageRef.putFile(uri)
        binding.progressBar.visibility = View.VISIBLE

        uploadTask.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
            binding.progressBar.progress = progress.toInt()

        }.addOnSuccessListener {
            binding.progressBar.visibility = View.GONE
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                url = it.toString()
                binding.status.text = uri.lastPathSegment
                binding.submitBtn.isEnabled = true
                Snackbar.make(binding.root, "Uploaded", Snackbar.LENGTH_SHORT)
                        .show()

            }.addOnFailureListener {
                Snackbar.make(binding.root, "Photo Upload Failure", Snackbar.LENGTH_SHORT)
                        .show()
                binding.submitBtn.isEnabled = true
            }

        }.addOnFailureListener {
            Snackbar.make(binding.root, "Photo Upload Failure", Snackbar.LENGTH_SHORT)
                    .show()
            binding.submitBtn.isEnabled = true
        }
    }

    private fun checkPassed(): Boolean {
        when {
            binding.name.text.isNullOrBlank() -> {
                Snackbar.make(
                        binding.root,
                        "Name Required",
                        Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            binding.height.text.isNullOrBlank() -> {
                Snackbar.make(
                        binding.root,
                        "Height Required",
                        Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            binding.weight.text.isNullOrBlank() -> {
                Snackbar.make(
                        binding.root,
                        "Weight Required",
                        Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            binding.contactNumber.text.isNullOrBlank() -> {
                Snackbar.make(
                        binding.root,
                        "Contact Number Required",
                        Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            binding.condition.text.isNullOrBlank() -> {
                Snackbar.make(
                        binding.root,
                        "Condition Required",
                        Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            binding.visit.text.isNullOrBlank() -> {
                Snackbar.make(
                        binding.root,
                        "Days before visit required",
                        Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            else -> return true
        }

    }


    private fun saveForm(userId: String) {
        binding.submitBtn.isEnabled = false
        val collection = Firebase.firestore.collection("forms")
        val form = Form(
                uid = userId,
                bid = business.id,
                bName = business.name,
                name = binding.name.text.toString(),
                height = binding.height.text.toString(),
                weight = binding.weight.text.toString(),
                contact = binding.contactNumber.text.toString(),
                condition = binding.condition.text.toString(),
                url = url,
                visit = binding.visit.text.toString().toInt()
        )

        collection.add(form)
                .addOnSuccessListener {
                    val doc = Firebase.firestore.collection("businesses").document(business.id.toString())
                    doc.update("queue", FieldValue.arrayUnion(it.id)).addOnSuccessListener {
                        doc.get().addOnSuccessListener { ref ->
                            val result = ref.toObject(Business::class.java)
                            findNavController().navigate(FormFragmentDirections.actionFormFragmentToQueueFragment(result!!))
                        }
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, "Form submission failed", Snackbar.LENGTH_LONG)
                                .show()

                        binding.submitBtn.isEnabled = true
                    }
                }.addOnFailureListener {
                    Snackbar.make(binding.root, "Form submission failed", Snackbar.LENGTH_LONG)
                            .show()

                    binding.submitBtn.isEnabled = true
                }
    }


}