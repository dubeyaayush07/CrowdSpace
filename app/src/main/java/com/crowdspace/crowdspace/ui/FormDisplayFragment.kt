package com.crowdspace.crowdspace.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.databinding.FragmentFormDisplayBinding
import com.crowdspace.crowdspace.model.Form
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class FormDisplayFragment : Fragment() {

    private lateinit var binding: FragmentFormDisplayBinding
    private lateinit var form: Form


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form_display, container, false)
        form = FormDisplayFragmentArgs.fromBundle(requireArguments()).selectedForm
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.name.text = form.name
        binding.condition.text = form.condition
        binding.days.text = form.visit.toString()
        binding.height.text = form.height
        binding.weight.text = form.weight

        if (form.url != null && !form.url.isNullOrBlank()) {
            binding.openDocument.visibility = View.VISIBLE
            binding.openDocument.setOnClickListener {
                context?.let { it1 -> downloadAndOpenImage(it1, form.url.toString()) }
            }

        }
    }

    private fun downloadAndOpenImage(context: Context, url: String) {
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        val localFile = File(context.getExternalFilesDir(null)?.absolutePath.toString() + ref.name)

        binding.progress.visibility = View.VISIBLE

        if (localFile.exists()) {
            openImage(context, localFile)
            binding.progress.visibility = View.GONE
        }
        else {
            ref.getFile(localFile).addOnSuccessListener {
                openImage(context, localFile)
                binding.progress.visibility = View.GONE
            }.addOnProgressListener {
                val prog = (100.0 * it.bytesTransferred) / it.totalByteCount
                binding.progress.progress = prog.toInt()
            }.addOnFailureListener {
                binding.progress.visibility = View.GONE
                Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show()
            }
        }


    }


    private fun openImage(context: Context, file: File) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
        )
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent)
    }


}