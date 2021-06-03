package com.crowdspace.crowdspace.ui

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crowdspace.crowdspace.R
import com.crowdspace.crowdspace.model.Business
import com.google.android.material.chip.ChipGroup

class BusinessAdapter(private val onClickListener: OnClickListener): RecyclerView.Adapter<BusinessAdapter.ViewHolder>() {

    var data = listOf<Business>()
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
        val businessName: TextView = itemView.findViewById(R.id.business_name)
        val viewBtn: Button = itemView.findViewById(R.id.view_btn)
        val status: TextView = itemView.findViewById(R.id.status)
        val size: TextView = itemView.findViewById(R.id.queueSize)
        val waitingTime: TextView = itemView.findViewById(R.id.textView9)
        val waitingTimeTxt: TextView = itemView.findViewById(R.id.textView8)
        val time: TextView = itemView.findViewById(R.id.time)

        fun bind(item: Business, onClickListener: OnClickListener) {
            businessName.text = item.name
            status.text = item.status
            size.text = "Queue Size ${item.queue?.size.toString()}"


            if (item.status == "closed") {
                time.text = "Opening Time\n${item.till}"
                waitingTime.visibility = View.INVISIBLE
                waitingTimeTxt.visibility = View.INVISIBLE
            } else {
                time.text = "Closing Time\n${item.till}"
                waitingTime.visibility = View.VISIBLE
                waitingTimeTxt.visibility = View.VISIBLE
                waitingTime.text = "${item.queue?.size?.times(item.avgTime!!.toInt())} Minutes"
            }


            viewBtn.setOnClickListener {
                onClickListener.onClick(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_business, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class OnClickListener(val clickListener: (business: Business) -> Unit) {
        fun onClick(business: Business) = clickListener(business)
    }


}