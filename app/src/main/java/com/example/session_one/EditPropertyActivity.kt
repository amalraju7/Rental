package com.example.session_one

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.session_one.Repository.PropertyItemRepository

import com.example.session_one.models.PropertyItem
import com.example.session_one.databinding.ActivityEditPropertyBinding
import com.example.session_one.helper.LocationHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.UUID

class EditPropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPropertyBinding
    private lateinit var originalProperty: PropertyItem
    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var propertyItemRepository: PropertyItemRepository

    private lateinit var locationHelper: LocationHelper

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        sharedPreferences = getSharedPreferences("propertyPrefs", Context.MODE_PRIVATE)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.locationHelper = LocationHelper.instance
        this.locationHelper.checkPermissions(this)
        binding = ActivityEditPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        propertyItemRepository = PropertyItemRepository(applicationContext)

        val originalPropertyJson = intent.getStringExtra("selectedProperty")
        originalProperty = gson.fromJson(originalPropertyJson, PropertyItem::class.java)

           val propertyTypeList: List<String> = listOf(
            "House",
            "Condos",
            "Apartment",
            "Room"
        )
        val propertyTypeAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, propertyTypeList)
        binding.spType.adapter = propertyTypeAdapter


        val selectedTypePosition = propertyTypeList.indexOf(originalProperty.id)
        binding.spType.setSelection(selectedTypePosition)

        binding.etBeds.setText(originalProperty.beds.toString())
        binding.etBaths.setText(originalProperty.baths.toString())
        binding.etAddress.setText(originalProperty.address)
        binding.etProvince.setText(originalProperty.province.toString())
        binding.etCode.setText(originalProperty.codeName)
        binding.etSquareFt.setText(originalProperty.squareFoots.toString())
        binding.etPrice.setText(originalProperty.amount.toString())
        binding.etDesc.setText(originalProperty.description.toString())
        binding.cbAvailable.isChecked = originalProperty.availability

        binding.btnUpdate.setOnClickListener {
            val updatedProperty = createUpdatedProperty()
            propertyItemRepository.updateProperty(updatedProperty)
            val resultIntent = Intent(this,PropertyListActivity::class.java)
            resultIntent.putExtra("updatedProperty", Gson().toJson(updatedProperty))

            setResult(Activity.RESULT_OK, resultIntent)
            startActivity(resultIntent)
            finish()


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
            var latLng: LatLng? = null
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
                    var latLng: LatLng = LatLng(lat.toDouble(),long.toDouble())
                    onLocationReady(latLng)
                }
            }
        }

        binding.button.setOnClickListener{
            locationHelper.getDeviceLocation(this, LocationServices.getFusedLocationProviderClient(this),onLocationReady)

        }
    }

    private fun createUpdatedProperty(): PropertyItem {

        val propertyTypeList: List<String> = listOf(
            "House",
            "Condos",
            "Apartment",
            "Room"
        )
        return PropertyItem(
            originalProperty.id,
            originalProperty.image,
            binding.etPrice.text.toString(),
            binding.etBeds.text.toString().toInt(),
            binding.etBaths.text.toString().toInt(),
            binding.etSquareFt.text.toString().toDouble(),
            binding.etAddress.text.toString(),
            binding.etProvince.text.toString(),
            binding.etCode.text.toString(),
            binding.cbAvailable.isChecked,
            binding.latitude.text.toString(),
            binding.longitude.text.toString(),
            propertyTypeList[binding.spType.selectedItemPosition],
            binding.etDesc.text.toString()
        )
    }
}
