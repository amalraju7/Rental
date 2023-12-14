package com.example.session_one

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.session_one.databinding.ActivityListsBinding
import com.example.session_one.models.ListingViewAdapter
import com.example.session_one.models.PropertyItem
import com.google.gson.Gson

class ListingActivity : AppCompatActivity() {

    private val gson = Gson()
    private lateinit var binding: ActivityListsBinding;
    private lateinit var recycler: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private var isLoggedIn: Boolean = false
    private val navigationHelper = NavigationHelper()

    private val listing : MutableList<PropertyItem> = mutableListOf(

        PropertyItem(

            image = "image",

            amount = "$7100",

            beds = 2,

            baths = 1,

            squareFoots = 0.3,

            address = "Scarborough North M1V1v1",

            province = "Ontario",

            codeName = "Barrie",

            availability = true,

            id = "56",

            propertyType = "House",

            description = ""

        ),
        PropertyItem(

            image = "image",

            amount = "$7200",

            beds = 2,

            baths = 1,

            squareFoots = 0.3,

            address = "Scarborough North M1V1v1",

            province = "Ontario",

            codeName = "Barrie",

            availability = true,

            id = "45",

            propertyType = "House",

            description = ""

        ),
        PropertyItem(

            image = "image",

            amount = "$7300",

            beds = 2,

            baths = 1,

            squareFoots = 0.3,

            address = "Scarborough North M1V1v1",

            province = "Ontario",

            codeName = "Barrie",

            availability = true,

            id = "32",

            propertyType = "House",

            description = ""

        ),
        PropertyItem(

            image = "image",

            amount = "$7400",

            beds = 2,

            baths = 1,

            squareFoots = 0.3,

            address = "Scarborough North M1V1v1",

            province = "Ontario",

            codeName = "Barrie",

            availability = true,

            id = "123243443",

            propertyType = "House",

            description = ""

        ),
        PropertyItem(

            image = "image",

            amount = "$7500",

            beds = 2,

            baths = 1,

            squareFoots = 0.3,

            address = "Scarborough North M1V1v1",

            province = "Ontario",

            codeName = "Barrie",

            availability = true,

            id = "13243443",

            propertyType = "House",

            description = ""

        ),
        PropertyItem(

            image = "image",

            amount = "$7600",

            beds = 2,

            baths = 1,

            squareFoots = 0.3,

            address = "Scarborough North M1V1v1",

            province = "Ontario",

            codeName = "Barrie",

            availability = true,

            id = "32434343",

            propertyType = "House",

            description = ""

        ),


    );

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityListsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)



        this.isLoggedIn = this.sharedPreferences.getString("active_user", "") != ""

        recycler = this.binding.recycler

        setSupportActionBar(this.binding.listingToolbar)


        val adapter = ListingViewAdapter(listing, resources, this.packageName) { item, position -> navigate("property" , item) }
        binding.recycler.setAdapter(adapter)
        binding.recycler.layoutManager = LinearLayoutManager(this)

        val searchQuery = intent.getStringExtra("searchQuery")

        if (!searchQuery.isNullOrEmpty()) {

            searchListings(searchQuery)

        } else {

            binding.recycler.adapter = adapter

        }

    }

    private fun searchListings(query: String) {
        val filteredList = listing.filter { property ->
            property.address.contains(query, ignoreCase = true) || property.province.contains(query, ignoreCase = true)
        }.toMutableList()

        val adapter: ListingViewAdapter


            adapter = ListingViewAdapter(filteredList, resources, this.packageName) { item, pos -> navigate("property", item) }


        binding.recycler.adapter = adapter
    }


    override fun onResume() {
        this.sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        this.isLoggedIn = this.sharedPreferences.getString("active_user", "") != ""
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId) {

            R.id.menuShortlist -> {

                navigate("shortlist", null )

            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun navigate (purpose: String, property : PropertyItem?) {

        val navClass = if ( this.isLoggedIn ) navigationHelper.classToNavigate(purpose) else SignInActivity::class.java
        val intent = Intent(this@ListingActivity, navClass)
        intent.putExtra("intention", purpose)
        if (property != null) intent.putExtra("selectedProperty", gson.toJson(property).toString())
        startActivity(intent)

    }

}