package com.example.mycontacts

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    // ArrayList of class ItemsViewModel
    private val data =ArrayList<ContactModel>()
    private var adapter: CustomAdapter = CustomAdapter(data)
    private var recyclerview: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //println("hello world")
        setContentView(R.layout.activity_main)

        if(recyclerview==null) {
            recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
            recyclerview?.layoutManager = LinearLayoutManager(this)




           // for (i in 1..5) {
                //Log.w("TAG","Name :: $i")
            //data.add(ContactModel("Name# $i", "Phone# $i"))
            //}

            checkPermission()

            adapter = CustomAdapter(data)
            recyclerview?.adapter = adapter
        }
        //


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
                            
                            if(name!=null && number!=null) {
                                var model: ContactModel = ContactModel(name, number)
                                data.add(model)
                            }



                        }
                        phoneCursor.close()
                    }



                }

                cursor.close()
            }

            recyclerview?.layoutManager = LinearLayoutManager(this)
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