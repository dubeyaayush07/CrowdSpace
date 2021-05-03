package com.crowdspace.crowdspace.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.model.Form
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.CollectionReference

class FormAdapter(private val onClickListener: FormAdapter.OnClickListener) : RecyclerView.Adapter<FormAdapter.ViewHolder>() {
    var data = listOf<Form>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, onClickListener)
    }


    override fun getItemCount(): Int = data.size

    class ViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

        val conditionTxt: TextView = itemView.findViewById(R.id.condition)
        val hospitalNameTxt: TextView = itemView.findViewById(R.id.hospital_name)
        val fileChip: Chip = itemView.findViewById(R.id.file)
        val hospitalChip: Chip = itemView.findViewById(R.id.visit_hospital)



        fun bind(item: Form, onClickListener: FormAdapter.OnClickListener) {
            conditionTxt.text = item.condition
            hospitalNameTxt.text = item.bName
            if (!item.url.isNullOrBlank()) {
                fileChip.visibility = View.VISIBLE
                fileChip.setOnClickListener {
                    Intent(Intent.ACTION_VIEW, Uri.parse(item.url)).apply {
                        it.context.startActivity(this)
                    }
                }
            } else fileChip.visibility = View.GONE

            hospitalChip.setOnClickListener {
                onClickListener.onClick(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_form, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class OnClickListener(val clickListener: (form: Form) -> Unit) {
        fun onClick(form: Form) = clickListener(form)
    }
}