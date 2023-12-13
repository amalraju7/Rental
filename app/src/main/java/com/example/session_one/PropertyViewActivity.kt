package com.example.session_one

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.example.session_one.databinding.ActivityPropertyViewBinding
import com.example.session_one.models.PropertyItem
import com.example.session_one.models.User
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PropertyViewActivity : OnClickListener , AppCompatActivity() {

    private val gson = Gson()
    private var isItemInFavouriteList = false
    private var loggedInUser = ""
    private lateinit var userSharedPreferences: SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    private lateinit var binding : ActivityPropertyViewBinding
    private lateinit var purpose: String
    private lateinit var selectedProperty: String
    private var property: PropertyItem? = null
    private lateinit var saveButton: MaterialButton
    private lateinit var deleteButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityPropertyViewBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.userSharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        println(this.userSharedPreferences.getString("active_user", ""))
        this.loggedInUser = gson.fromJson( this.userSharedPreferences.getString("active_user", ""), User::class.java ).email

        this.sharedPreferences = getSharedPreferences("FAV_STORE", MODE_PRIVATE)
        this.sharedPrefEditor = this.sharedPreferences.edit()

        val favouriteListFromSharedPreferences = sharedPreferences.getString("${loggedInUser}_fav_list", "")
        val typeToken = object : TypeToken<List<PropertyItem>>() {}.type
        val favouriteList : List<PropertyItem>? = gson.fromJson<List<PropertyItem>>(favouriteListFromSharedPreferences, typeToken)


        this.saveButton = this.binding.addToShortList
        this.deleteButton = this.binding.removeFromShortList

        this.saveButton.setOnClickListener(this)
        this.deleteButton.setOnClickListener(this)

        val currentIntent = this@PropertyViewActivity.intent

        if (currentIntent != null) {

            println(currentIntent.getStringExtra("intention"))

            this.purpose = currentIntent.getStringExtra("intention").toString()
            this.selectedProperty = currentIntent.getStringExtra("selectedProperty").toString()

            if ( this.selectedProperty != "" && property != null) {

                property = gson.fromJson( this.selectedProperty, PropertyItem::class.java)

                this.isItemInFavouriteList = favouriteList?.find{it.id == property?.id} != null
                property?.image = "house"
                val drawableUri = resources.getIdentifier("@drawable/${property?.image}", "drawable", packageName)
                val drawableImage = resources.getDrawable(drawableUri, null)
                this.binding.propertyImage.setImageDrawable(drawableImage)

                val availability = if ( property?.availability == true ) "available" else "taken"

                this.binding.province.setText(property?.province.toString())
                this.binding.address.setText(property?.address.toString())

                    this.binding.propertyBaths.setText(property?.baths.toString())
                this.binding.propertyBeds.setText(property?.beds.toString())
                this.binding.propertySquareFoot.setText(property?.squareFoots.toString())
                this.binding.propertyType.setText(property?.propertyType.toString())
                this.binding.availability.setText(availability)
                this.binding.codeName.setText(property?.codeName.toString())

                val descriptionString = resources.getString(R.string.apartment_description)
                this.binding.description.setText("${property?.codeName.toString()}, $descriptionString")

                if ( this.isItemInFavouriteList ) {

                    saveButton.visibility = View.GONE

                } else {

                    deleteButton.visibility = View.GONE

                }

            }


        }

    }

    override fun onClick(view: View?) {

        val property = gson.fromJson( this.selectedProperty, PropertyItem::class.java)
        val favouriteListFromSharedPreferences = this.sharedPreferences.getString("${this.loggedInUser}_fav_list", "")
        val typeToken = object : TypeToken<List<PropertyItem>>() {}.type
        val favouriteList : List<PropertyItem>? = gson.fromJson<List<PropertyItem>>(favouriteListFromSharedPreferences, typeToken)
        var editableList = if ( favouriteList == null ) mutableListOf<PropertyItem>() else favouriteList?.toMutableList<PropertyItem>()

        when (view?.id) {

            R.id.addToShortList -> {

                editableList?.add(property)
                saveButton.visibility = View.GONE
                deleteButton.visibility = View.VISIBLE

            }

            R.id.removeFromShortList -> {

                editableList = editableList?.filter { it.id !=  property.id}?.toMutableList()
                deleteButton.visibility = View.GONE
                saveButton.visibility = View.VISIBLE

            }

        }

        this.sharedPrefEditor.putString("${loggedInUser}_fav_list", gson.toJson(editableList))
        this.sharedPrefEditor.apply()

    }

}