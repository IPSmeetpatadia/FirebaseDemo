package com.ipsMeet.firebasedemo.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.adapter.PdfAdapter
import com.ipsMeet.firebasedemo.dataclass.UploadPdfDataClass
import com.ipsMeet.firebasedemo.dataclass.ViewPdfDataClass
import kotlinx.android.synthetic.main.popup_pdf_list.*
import kotlinx.android.synthetic.main.popup_pdf_list.view.*
import java.util.*

class DocumentListFragment : Fragment() {

    private lateinit var popupView: View
    private lateinit var alertDialog: AlertDialog
    private lateinit var pdfURI: Uri

    lateinit var recyclerView: RecyclerView

    private var listData = arrayListOf<ViewPdfDataClass>()
    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()

    lateinit var pdfLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.document_recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("User/$userID/pdf data")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ViewPdfDataClass::class.java)
                        listedData?.key = item.key.toString()
                        listData.add(listedData!!)

                        recyclerView.adapter = PdfAdapter(context!!.applicationContext, listData,
                        object : PdfAdapter.OnClickListener {
                            override fun downloadFile(fileName: String) {

                                val progressDialog = ProgressDialog(requireContext())
                                progressDialog.setMessage("Downloading File...")
                                progressDialog.setCancelable(false)
                                progressDialog.show()

                                val storageReference = FirebaseStorage.getInstance().reference.child("Documents/*$fileName")
                                storageReference.downloadUrl
                                    .addOnSuccessListener { uri: Uri ->
                                        val url = uri.toString()
                                        Log.d("URL", url)
                                        downloadFile(context, fileName, "pdf", DIRECTORY_DOWNLOADS, url)
                                        progressDialog.hide()
                                    }
                                    .addOnFailureListener {
                                        progressDialog.hide()
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })

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
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
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
            listData.clear()
            uploadPDF()

            val pdf = UploadPdfDataClass(
                fileName = popupView.pdf_name.text.toString(),
                pdf = pdfURI.buildUpon().build().toString()
            )

            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val key = database.child("pdf data").push().key.toString()

            dbRef.getReference("User/$userID").child("pdf data").child(key).setValue(pdf)
            alertDialog.dismiss()
        }
    }

    private fun uploadPDF() {
        val file = popupView.pdf_name.text.toString()
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading Data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReference("Documents/*$file")

        storageReference.putFile(pdfURI)
            .addOnSuccessListener {
                progressDialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun downloadFile(context: Context?, fileName: String, fileExtension: String, directoryDownloads: String?, url: String) {
        val downloadManager: DownloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request: DownloadManager.Request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, directoryDownloads, fileName + fileExtension)

        downloadManager.enqueue(request)
    }

    override fun onResume() {
        super.onResume()
        listData.clear()
    }

}