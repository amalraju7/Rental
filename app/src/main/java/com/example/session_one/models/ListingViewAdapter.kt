package com.example.session_one.models

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.session_one.R

class ListingViewAdapter(

    private var listing: MutableList<PropertyItem> ,

    private var resources: Resources,

    private var packageName: String,

    val onClick: (PropertyItem, position:Int) -> Unit,


) : RecyclerView.Adapter<ListingViewAdapter.ListingViewHolder>() {


    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {


        println(listing[position].image)

        val beds = holder.itemView.findViewById<TextView>(R.id.propertyBeds);
        val baths = holder.itemView.findViewById<TextView>(R.id.propertyBaths);
        val squareFoot = holder.itemView.findViewById<TextView>(R.id.propertySquareFoot);
        val address = holder.itemView.findViewById<TextView>(R.id.propertyAddress);
        val province = holder.itemView.findViewById<TextView>(R.id.propertyProvince);
        val amount = holder.itemView.findViewById<TextView>(R.id.propertyAmount)
        val image = holder.itemView.findViewById<ImageView>(R.id.propertyImage)

        beds.text = listing[position].beds.toString()
        baths.text = listing[position].baths.toString()
        squareFoot.text = listing[position].squareFoots.toString()
        address.text = listing[position].address
        province.text = listing[position].province
        amount.text = listing[position].amount

        if ( listing[position].image.isNotEmpty() ) {

            val drawableUri = resources.getIdentifier("@drawable/${listing[position].image}", "drawable", packageName)
            val drawableImage = resources.getDrawable(drawableUri, null)
            image.setImageDrawable(drawableImage)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.property_item, parent, false)
        return ListingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listing.size
    }


    inner class ListingViewHolder(itemView: View): RecyclerView.ViewHolder (itemView) {

        init {

            val view = itemView.rootView;

            view.setOnClickListener{

                onClick(listing[adapterPosition], adapterPosition)

            }

        }

    }

}