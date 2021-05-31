package com.crowdspace.crowdspace.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.formatDate
import com.crowdspace.crowdspace.model.Form

class QueueFormsAdapter() : RecyclerView.Adapter<QueueFormsAdapter.ViewHolder>() {
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
        holder.bind(item)
    }


    override fun getItemCount(): Int = data.size

    class ViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.name)
        val time: TextView = itemView.findViewById(R.id.time)

        fun bind(item: Form) {
            name.text = item.name
            time.text = formatDate(item.timeStamp)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_queue_form, parent, false)
                return ViewHolder(view)
            }
        }
    }
}