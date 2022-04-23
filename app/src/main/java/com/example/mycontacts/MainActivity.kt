package com.example.mycontacts

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    // ArrayList of class ItemsViewModel
    private val data =ArrayList<ContactModel>()
    private var adapter: CustomAdapter = CustomAdapter(data)
    private var recyclerview: RecyclerView? = null
    private val db = DBHelper(this, null)
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //println("hello world")
        setContentView(R.layout.activity_main)


        if(db.getProfilesCount()>0){
            val cursor = db.getName()

            // moving the cursor to first position and
            // appending value in the text view
            cursor!!.moveToFirst()
            data.add(ContactModel(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("phone_number"))))
            //Name.append(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)) + "\n")
            //Age.append(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)) + "\n")

            // moving our cursor to next
            // position and appending values
            while(cursor.moveToNext()){
                //Name.append(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)) + "\n")
                //Age.append(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)) + "\n")
                data.add(ContactModel(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("phone_number"))))
            }

            // at last we close our cursor
            cursor.close()

            if(recyclerview==null) {
                recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
                recyclerview?.layoutManager = LinearLayoutManager(this)

                data.sortBy { it.name }
                adapter = CustomAdapter(data)
                recyclerview?.adapter = adapter

                Toast.makeText(this,"Total contacts: " + db.getProfilesCount(),Toast.LENGTH_SHORT).show()
            }
        }
        else {
            if(recyclerview==null) {
                recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
                recyclerview?.layoutManager = LinearLayoutManager(this)

                checkPermission()

                adapter = CustomAdapter(data)
                recyclerview?.adapter = adapter

                //Log.w("TAG","Total contacts: " + db.getProfilesCount())

                Toast.makeText(this,"Total contacts: " + db.getProfilesCount(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.READ_CONTACTS),100)
        }
        else {
            getContacts()
        }
    }

    @SuppressLint("Range")
    private fun getContacts() {

        val uri: Uri = ContactsContract.Contacts.CONTENT_URI

        //val sort : String = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"

        val cursor: Cursor? =contentResolver.query(uri,null,null,null,null)
        //val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC"
        if(cursor != null)
        {
            if (cursor.count > 0)
            {
                while(cursor.moveToNext())
                {
                    val id: String? = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name: String? = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    val selection: String = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?"

                    val phoneCursor:Cursor? = contentResolver.query(uriPhone,null,selection,
                        arrayOf(id),null)

                    if(phoneCursor!=null)
                    {
                        if(phoneCursor.moveToNext())
                        {
                            val number: String = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            //Log.e("TAG","Name :: $name")
                            //Log.e("TAG", "Phone no :: $number")
                            
                            if(name!=null) {
                                var model: ContactModel = ContactModel(name, number)
                                data.add(model)
                                db.addName(name,number)
                            }

                        }
                        phoneCursor.close()
                    }

                }

                cursor.close()
            }

            recyclerview?.layoutManager = LinearLayoutManager(this)
            data.sortBy { it.name }
            adapter = CustomAdapter(data)
            recyclerview?.adapter = adapter


        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            getContacts()
        }
        else
        {
            //Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show()
            checkPermission()
        }
    }
}