package com.example.session_one

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import com.example.session_one.Repository.PropertyItemRepository

import com.example.session_one.models.PropertyItem
import com.example.session_one.databinding.ActivityAddPropertyBinding
import com.example.session_one.helper.LocationHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddNewPropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPropertyBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val properties = mutableListOf<PropertyItem>()
    private lateinit var propertyItemRepository: PropertyItemRepository

    private lateinit var locationHelper: LocationHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.locationHelper = LocationHelper.instance
        this.locationHelper.checkPermissions(this)
        binding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        propertyItemRepository = PropertyItemRepository(applicationContext)

        val propertyTypeList: List<String> = listOf(
            "House",
            "Condos",
            "Apartment",
            "Basement"
        )

        setSupportActionBar(this.binding.toolbar)


        binding.spPropertyType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, propertyTypeList)


        binding.spPropertyType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }



        binding.btnAddListing.setOnClickListener {

            val selectedPosType: Int = binding.spPropertyType.selectedItemPosition
            val selectedType = propertyTypeList[selectedPosType]

            val selectedProvince = binding.etProvince.text.toString()

            val typeFromUI = selectedType
            val provinceFromUI = selectedProvince
            val bedsFromUI = binding.etBeds.text.toString()
            val bathsFromUI = binding.etBaths.text.toString()
            val squareFromUI = binding.etSquareFt.text.toString()

            val image = "house"
            val type = selectedType
            val price = binding.etPrice.text.toString()
            val address = binding.etAddress.text.toString()
            val province = selectedProvince
            val beds = binding.etBeds.text.toString().toIntOrNull() ?: 0
            val baths = binding.etBaths.text.toString().toIntOrNull() ?: 0
            val squareFt = binding.etSquareFt.text.toString().toDoubleOrNull() ?: 0.0
            val availability = true
            val latitude = binding.latitude.text.toString()
            val longitude = binding.longitude.text.toString()
            val postalCode = binding.etCode.text.toString()
            val description = binding.etDescription.text.toString()

            if (bedsFromUI.isEmpty() && bathsFromUI.isEmpty() && squareFromUI.isEmpty() && price.isEmpty() && address.isEmpty() && postalCode.isEmpty() && description.isEmpty()) {
                binding.etAddress.setHint("Address")
                binding.etAddress.setHintTextColor(Color.RED)
                binding.etBaths.setHint("Baths")
                binding.etBaths.setHintTextColor(Color.RED)
                binding.etBeds.setHint("Beds")
                binding.etBeds.setHintTextColor(Color.RED)
                binding.etDescription.setHint("Description")
                binding.etDescription.setHintTextColor(Color.RED)
                binding.etCode.setHint("Postal Code")
                binding.etCode.setHintTextColor(Color.RED)
                binding.etSquareFt.setHint("Square Ft")
                binding.etSquareFt.setHintTextColor(Color.RED)
                binding.etPrice.setHint("Price")
                binding.etPrice.setHintTextColor(Color.RED)
            } else if (address.isEmpty()) {
                binding.etAddress.setHint("Address")
                binding.etAddress.setHintTextColor(Color.RED)
            } else if (bathsFromUI.isEmpty()) {
                binding.etBeds.setHint("Baths")
                binding.etBeds.setHintTextColor(Color.RED)
            } else if (bedsFromUI.isEmpty()) {
                binding.etBeds.setHint("Beds")
                binding.etBeds.setHintTextColor(Color.RED)
            } else if (description.isEmpty()) {
                binding.etDescription.setHint("Description")
                binding.etDescription.setHintTextColor(Color.RED)
            } else if (postalCode.isEmpty()) {
                binding.etCode.setHint("Postal Code")
                binding.etCode.setHintTextColor(Color.RED)
            } else if (squareFromUI.isEmpty()) {
                binding.etSquareFt.setHint("Square Ft")
                binding.etSquareFt.setHintTextColor(Color.RED)
            } else if (price.isEmpty()) {
                binding.etPrice.setHint("Price")
                binding.etPrice.setHintTextColor(Color.RED)
            } else {


var latLng:LatLng?
                if(this.locationHelper.locationPermissionGranted){
                   latLng=locationHelper.performReverseGeocoding(address,applicationContext)
                }


                val property = PropertyItem(
                    image=  image,
                    amount = price,
                    beds=  beds,
                    baths =baths,
                    squareFoots = squareFt,
                    address = address,
                     province =province,
                   codeName = postalCode,
                   availability = availability,
                    propertyType = type,
                   description =  description,
                    latitude = latitude,
                    longitude = longitude
                )
                val existingProperties = loadPropertiesFromPreferences()
                existingProperties.add(property)

                this.propertyItemRepository.addPropertyToDB(property)


                val intent = Intent(this, RentYourPropertyActivity::class.java)
                intent.putExtra("successMessage", "PROPERTY ADDED SUCCESSFULLY!!")
                startActivity(intent)
            }
        }
        val onLocationReady = fun (latLng: LatLng){

            binding.latitude.setText(latLng?.latitude.toString())
            binding.longitude.setText(latLng?.longitude.toString())
            var address : Address? = locationHelper.performForwardGeocoding(this,latLng)
            binding.etAddress.setText(address?.featureName+" "+address?.thoroughfare)
            binding.etProvince.setText(address?.adminArea)
            binding.etCode.setText(address?.postalCode)
        }

        binding.etAddress.doAfterTextChanged{
            val address = binding.etAddress.text.toString()
            var latLng:LatLng? = null
            if(this.locationHelper.locationPermissionGranted){
                latLng=locationHelper.performReverseGeocoding(address,applicationContext)
            }
            if (latLng?.latitude != null){


            binding.latitude.setText(latLng?.latitude.toString())
            binding.longitude.setText(latLng?.longitude.toString())

            }
        }
        binding.etAddress.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!binding.latitude.text.isNullOrEmpty()){
                    var lat:String = binding.latitude.text.toString()
                    var long:String = binding.longitude.text.toString()
                    var latLng:LatLng = LatLng(lat.toDouble(),long.toDouble())
                    onLocationReady(latLng)
                }
            }
        }


        binding.button.setOnClickListener{
            locationHelper.getDeviceLocation(this, LocationServices.getFusedLocationProviderClient(this),onLocationReady)

        }
    }

    private fun loadPropertiesFromPreferences(): MutableList<PropertyItem> {
        val json = sharedPreferences.getString("properties", "")
        return if (json?.isNotEmpty() == true) {
            val gson = Gson()
            val type = object : TypeToken<List<PropertyItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }




    private fun generateUniqueId(): String {

        return System.currentTimeMillis().toString()
    }




    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
