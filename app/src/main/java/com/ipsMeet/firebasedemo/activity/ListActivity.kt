package com.ipsMeet.firebasedemo.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.ipsMeet.firebasedemo.R
import com.ipsMeet.firebasedemo.adapter.ListAdapter
import com.ipsMeet.firebasedemo.dataclass.ListDataClass
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.popup_add_list.view.*

class ListActivity : AppCompatActivity() {

    private lateinit var popupView: View
    private var listData = arrayListOf<ListDataClass>()
    var key = ""

    private lateinit var database: DatabaseReference
    private var dbRef = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        registerForContextMenu(recycler_list)
        recycler_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        database = Firebase.database.reference

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("User/$userID/list data")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val listedData = item.getValue(ListDataClass::class.java)
                        listData.add(listedData!!)
                        recycler_list.adapter = ListAdapter(this@ListActivity, listData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list_fragment, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_addList -> {
                val builder = AlertDialog.Builder(this)
                val inflater = LayoutInflater.from(this)
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
                        totalPurchase = 0)

                    val userID = FirebaseAuth.getInstance().currentUser!!.uid
                    key = database.child("list data").push().key.toString()

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

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater = this.menuInflater
        menuInflater.inflate(R.menu.menu_long_press, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_delete_item -> {
                val userID = FirebaseAuth.getInstance().currentUser!!.uid
                val dbRef = FirebaseDatabase.getInstance().getReference("User/$userID/list data")
                val key =

                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onContextItemSelected(item)
    }
}