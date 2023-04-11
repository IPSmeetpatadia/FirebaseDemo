package com.ipsMeet.firebasedemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.dataclass.ViewImgListDataClass

class ImgListAdapter(val context: Context, private val profileList: List<ViewImgListDataClass>)
    : RecyclerView.Adapter<ImgListAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.list_img)
        val name: TextView = itemView.findViewById(R.id.list_name)
        val email: TextView = itemView.findViewById(R.id.list_email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_view_img_list, parent, false)
        return ProfileViewHolder(view)
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.apply {
            Glide.with(context).load(profileList[position].img.toUri()).into(img)
            name.text = profileList[position].name
            email.text = profileList[position].email
        }
    }
}