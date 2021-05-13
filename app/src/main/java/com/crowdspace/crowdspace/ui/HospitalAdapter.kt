package com.crowdspace.crowdspace.ui

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
import com.crowdspace.crowdspace.model.Hospital

class HospitalAdapter(private val onClickListener: OnClickListener): RecyclerView.Adapter<HospitalAdapter.ViewHolder>() {

    var data = listOf<Hospital>()
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
        val hospitalName: TextView = itemView.findViewById(R.id.hospital_name)
        val hospitalAddress: TextView = itemView.findViewById(R.id.addresss_txt)
        val hospitalImgView: ImageView = itemView.findViewById(R.id.hospital_img)
        val viewBtn: Button = itemView.findViewById(R.id.view_btn)


        fun bind(item: Hospital, onClickListener: OnClickListener) {
            hospitalName.text = item.name
            hospitalAddress.text = item.address

            item.photoUrl?.let {
                val imgUri = it.toUri().buildUpon().scheme("https").build()
                Glide.with(hospitalImgView.context)
                        .load(imgUri)
                        .apply(
                                RequestOptions()
                                        .error(R.drawable.ic_broken_image))
                        .into(hospitalImgView)
            }

            viewBtn.setOnClickListener {
                onClickListener.onClick(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_hospital, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class OnClickListener(val clickListener: (hospital: Hospital) -> Unit) {
        fun onClick(hospital: Hospital) = clickListener(hospital)
    }


}