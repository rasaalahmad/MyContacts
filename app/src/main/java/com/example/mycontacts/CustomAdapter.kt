package com.example.mycontacts
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<ContactModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var model: ContactModel = mList[position]
        holder.tv_name.text = model.name
        holder.tv_number.text = model.number
        //holder.position=position
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tv_name: TextView = itemView.findViewById(R.id.tv_name)
        val tv_number: TextView = itemView.findViewById(R.id.tv_number)
       val ibtn:ImageButton=itemView.findViewById(R.id.imageButton)

        init {
            ibtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(tv_number.text.toString())))
                //val tv:TextView = findViewById(R.id.tv_number)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                Log.d("Tag", "textview: ${tv_number.text.toString()}")
                ItemView.context.startActivity(intent)
            }
        }

        fun setPosition(pos: Int)
        {
            position=pos
        }
    }
}