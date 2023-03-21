package com.ipsMeet.firebasedemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.dataclass.ListDataClass
import kotlinx.android.synthetic.main.single_view_list.view.*

class ListAdapter(private val context: Context, private val listData: List<ListDataClass>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.singleView_list_name
            val organization: TextView = itemView.singleView_list_company
            val totalPurchase: TextView = itemView.singleView_list_totalPurchase
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_view_list, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.apply {
            name.text = listData[position].name
            organization.text = listData[position].organization
            totalPurchase.text = listData[position].totalPurchase.toString()
            itemView.isLongClickable = true
        }
    }
}