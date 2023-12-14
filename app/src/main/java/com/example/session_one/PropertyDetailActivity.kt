package com.example.session_one

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.session_one.Repository.PropertyItemRepository
import com.example.session_one.databinding.ActivityDetailBinding
import com.example.session_one.models.ListingViewAdapter
import com.example.session_one.models.PropertyItem
import com.google.gson.Gson

class PropertyDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private val gson = Gson()
    private lateinit var purpose: String
    private lateinit var selectedProperty: String
    private lateinit var sharedPreferences: SharedPreferences
    private var property: PropertyItem? = null
    private lateinit var listingAdapter: ListingViewAdapter
    private lateinit var propertyItemRepository: PropertyItemRepository
    private var existingProperties: MutableList<PropertyItem> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        propertyItemRepository = PropertyItemRepository(applicationContext)

        sharedPreferences = getSharedPreferences("propertyPrefs", MODE_PRIVATE)
        setContentView(this.binding.root)

        val currentIntent = this.intent

        if (currentIntent != null) {

            println(currentIntent.getStringExtra("intention"))

            this.purpose = currentIntent.getStringExtra("intention").toString()
            this.selectedProperty = currentIntent.getStringExtra("selectedProperty").toString()

            if ( this.selectedProperty.isNotEmpty()) {

                property = gson.fromJson( this.selectedProperty, PropertyItem::class.java)



                val availability = if ( property?.availability == true ) "available" else "taken"

                this.binding.province.setText(property?.province.toString())
                this.binding.address.setText(property?.address.toString())
                this.binding.propertyType.setText(property?.propertyType.toString())

this.binding.price.setText("$"+property?.amount.toString())
                this.binding.availability.setText(availability)
                this.binding.codeName.setText(property?.codeName.toString())

                this.binding.propertyBeds.setText(property?.beds.toString())
                this.binding.propertyBaths.setText(property?.baths.toString())
                this.binding.propertySquareFoot.setText(property?.squareFoots.toString())



                this.binding.description.setText(" ${property?.description.toString()}")


                val propertyJson = intent.getStringExtra("selectedProperty")
                property = Gson().fromJson(propertyJson, PropertyItem::class.java)

                val position = intent.getIntExtra("selectedPropertyPosition", -1)


                binding.btnDelete.setOnClickListener {

                    onDeleteButtonClicked(property)

                    val intent = Intent(this, RentYourPropertyActivity::class.java)
                    intent.putExtra("successMessage", "PROPERTY DELETED SUCCESSFULLY!!")
                    startActivity(intent)
                }


                binding.btnEdit.setOnClickListener {
                    val intent = Intent(this, EditPropertyActivity::class.java)
                    intent.putExtra("selectedProperty", Gson().toJson(property))
                    startActivity(intent)

                }

            }


        }
    }

    private fun onDeleteButtonClicked(propertyToDelete: PropertyItem?) {
        propertyItemRepository.deleteProperty(propertyToDelete)
    }




}