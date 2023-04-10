package com.ipsMeet.firebasedemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.dataclass.ViewPdfDataClass

class PdfAdapter(val context: Context, private val pdfList: List<ViewPdfDataClass>, private val downloadPdf: OnClickListener) : RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {

    class PdfViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val pdfName: TextView = itemView.findViewById(R.id.single_pdf_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_pdf_list, parent, false)
        return PdfViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.apply {
            pdfName.text = pdfList[position].fileName

            itemView.setOnClickListener {
                downloadPdf.downloadFile(pdfList[position].fileName)
            }
        }
    }

    interface OnClickListener {
        fun downloadFile(fileName: String)
    }

}