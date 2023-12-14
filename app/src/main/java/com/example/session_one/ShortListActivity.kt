package com.example.session_one

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.session_one.Repository.PropertyItemRepository
import com.example.session_one.databinding.ActivityShortListBinding
import com.example.session_one.models.ListingViewAdapter
import com.example.session_one.models.PropertyItem
import com.example.session_one.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShortListActivity : AppCompatActivity() {

    private var gson = Gson()
    private val navigationHelper = NavigationHelper()
    private var loggedInUser = ""
    private var favouriteList = mutableListOf<PropertyItem>()
    private lateinit var propertyItemRepository: PropertyItemRepository

    private lateinit var userSharedPreferences: SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ListingViewAdapter

    private lateinit var binding: ActivityShortListBinding
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityShortListBinding.inflate(layoutInflater)

        setContentView(this.binding.root)
        this.propertyItemRepository= PropertyItemRepository(applicationContext)

        this.userSharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        this.loggedInUser = gson.fromJson(
            this.userSharedPreferences.getString("active_user", ""),
            User::class.java
        ).email

        println(loggedInUser)

        this.sharedPreferences = getSharedPreferences("FAV_STORE", MODE_PRIVATE)

        val favouriteListFromSharedPreferences =
            sharedPreferences.getString("${loggedInUser}_fav_list", "")

        val typeToken = object : TypeToken<MutableList<PropertyItem>>() {}.type

        this.favouriteList =
            if (favouriteListFromSharedPreferences?.isEmpty() == true) mutableListOf() else
                gson.fromJson(favouriteListFromSharedPreferences, typeToken)

        setSupportActionBar(this.binding.shortListToolbar)

        supportActionBar?.setDisplayShowTitleEnabled(true)

        this.recycler = this.binding.recycler

        this.adapter = ListingViewAdapter(
            favouriteList,
            resources,
            this.packageName
        ) { item, pos -> navigate("property", item) }

        binding.recycler.setAdapter(this.adapter)

        this.recycler.layoutManager = LinearLayoutManager(this)

        this.recycler.adapter = this.adapter




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shortlist_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {

            R.id.menuRentYourProperty -> {

                navigate("rent-your-own-property", null)

            }

            R.id.logOut -> {



                val editor = sharedPreferences.edit()
                editor.remove("active_user").commit()
                val intent = Intent(this@ShortListActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {

//        this.sharedPreferences = getSharedPreferences("FAV_STORE", MODE_PRIVATE)
//
//        val favouriteListFromSharedPreferences =
//            sharedPreferences.getString("${loggedInUser}_fav_list", "")
//
//        val typeToken = object : TypeToken<MutableList<PropertyItem>>() {}.type
//
//        this.favouriteList =
//            if (favouriteListFromSharedPreferences?.isEmpty() == true) mutableListOf() else
//                gson.fromJson(favouriteListFromSharedPreferences, typeToken)
//
//        this.adapter = ListingViewAdapter(
//            favouriteList,
//            resources,
//            this.packageName
//        ) { item, pos -> navigate("property", item) }
//
//        this.recycler.adapter = this.adapter
        super.onResume()
        propertyItemRepository.retrieveAllShortlist()

        propertyItemRepository.allPropertyItems.observe(this, androidx.lifecycle.Observer { propertyList ->
            if(propertyList != null){
                //clear the existing list to avoid duplicate records
              favouriteList.clear()


                for (property in propertyList){
                    Log.e(TAG, "onResume: Property : ${property}", )
                    if (!favouriteList.contains(property)) {
                        favouriteList.add(property)
                        adapter.notifyDataSetChanged()
                    }
                }
            } })


//        super.onResume()
    }

    private fun navigate(purpose: String, property: PropertyItem?) {

        val navClass = navigationHelper.classToNavigate(purpose)
        val intent = Intent(this@ShortListActivity, navClass)
        intent.putExtra("intention", purpose)
        if (property != null) intent.putExtra("selectedProperty", gson.toJson(property).toString())
        startActivity(intent)

    }

}