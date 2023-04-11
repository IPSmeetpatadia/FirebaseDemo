package com.ipsMeet.firebasedemo.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.adapter.ListAdapter
import com.ipsMeet.firebasedemo.dataclass.ListDataClass
import com.ipsMeet.firebasedemo.dataclass.ViewListDataClass
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.popup_add_list.view.*

class ListFragment : Fragment() {

    private lateinit var popupView: View
    private var listData = arrayListOf<ViewListDataClass>()
    var key = ""

    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerForContextMenu(recycler_fragment_list)
        recycler_fragment_list.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        database = Firebase.database.reference

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("User/$userID/list data")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ViewListDataClass::class.java)
                        listedData?.key = item.key.toString()
                        listData.add(listedData!!)
                        recycler_fragment_list.adapter = ListAdapter(context!!.applicationContext, listData,
                        object : ListAdapter.OnItemClick{
                            override fun onClick(listDataClass: String) {
                                key = listDataClass
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_addList -> {
                val builder = AlertDialog.Builder(requireContext())
                val inflater = LayoutInflater.from(context)
                popupView = inflater.inflate(R.layout.popup_add_list, null)
                builder.setView(popupView)

                val alertDialog = builder.create()
                alertDialog.show()

                popupView.btnSaveData.setOnClickListener {
                    listData.clear()
                    Log.d("Field Value", FieldValue.increment(1).toString())
                    val list = ListDataClass(
                        name = popupView.addList_edtxt_name.text.toString(),
                        organization = popupView.addList_edtxt_organization.text.toString(),
                        address = popupView.addList_edtxt_address.text.toString(),
                        email = popupView.addList_edtxt_email.text.toString(),
                        phone = popupView.addList_edtxt_phone.text.toString(),
                        0)

                    val userID = FirebaseAuth.getInstance().currentUser!!.uid
                    val key = database.child("list data").push().key.toString()

                    dbRef.getReference("User/$userID").child("list data").child(key).setValue(list)
                    alertDialog.dismiss()
                }

                popupView.btn_addList_cancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo? ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater = requireActivity().menuInflater
        menuInflater.inflate(R.menu.menu_long_press, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_total_count -> {

            }
            R.id.menu_delete_item -> {
                val userID = FirebaseAuth.getInstance().currentUser!!.uid
                FirebaseDatabase.getInstance().getReference("User/$userID/list data").child(key).removeValue()
                listData.clear()
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onContextItemSelected(item)
    }

}