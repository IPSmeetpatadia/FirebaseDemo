package com.ipsMeet.firebasedemo.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.ipsMeet.firebasedemo.R
import kotlinx.android.synthetic.main.popup_pdf_list.view.*
import java.util.*


class DocumentListFragment : Fragment() {

    private lateinit var popupView: View
    lateinit var alertDialog: AlertDialog
    lateinit var pdfURI: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.document_recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        view.findViewById<FloatingActionButton>(R.id.btn_uploadPDF).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(context)
            popupView = inflater.inflate(R.layout.popup_pdf_list, null)
            builder.setView(popupView)

            alertDialog = builder.create()
            alertDialog.show()

            popupView.findViewById<ImageView>(R.id.pdf_img).setOnClickListener {
                selectPDF()
            }

            popupView.btnPdfCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    private fun selectPDF() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            pdfURI = data?.data!!
            Log.d("PDF name", data.toString())

            getFileName(pdfURI)
        }
    }

    @SuppressLint("Range")
    private fun getFileName(pdfURI: Uri) {
        var result: String? = null

        if (pdfURI.scheme.equals("content")) {
            val cursor = requireActivity().contentResolver.query(pdfURI, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = pdfURI.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        popupView.pdf_name.setText(result.toString())

        popupView.btnUploadPdf.setOnClickListener {
            uploadPDF(result!!)
            alertDialog.dismiss()
        }
    }

    private fun uploadPDF(result: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading Data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReference("Documents/*$result")

        storageReference.putFile(pdfURI)
            .addOnSuccessListener {
                progressDialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
            }
    }

}