package com.example.session_one

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.session_one.Repository.PropertyItemRepository
import com.example.session_one.databinding.ActivityPropertyListBinding
import com.example.session_one.models.ListingViewAdapter
import com.example.session_one.models.PropertyItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PropertyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPropertyListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var listingAdapter: ListingViewAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val properties = mutableListOf<PropertyItem>()
    private lateinit var propertyItemRepository: PropertyItemRepository
    private lateinit var propertyArrayList: ArrayList<PropertyItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.searchToolbar)


        recyclerView = binding.rvPropertyList




        propertyArrayList = ArrayList()
        listingAdapter = ListingViewAdapter(propertyArrayList, resources, this.packageName) { item, position ->
            navigateToPropertyDetails(item, position)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listingAdapter

        listingAdapter.notifyDataSetChanged()
        propertyItemRepository = PropertyItemRepository(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        propertyItemRepository.retrieveAllExpenses()

        propertyItemRepository.allPropertyItems.observe(this, androidx.lifecycle.Observer { expenseList ->
            if(expenseList != null){
                //clear the existing list to avoid duplicate records
                propertyArrayList.clear()
                propertyArrayList.addAll(expenseList)
                listingAdapter.notifyDataSetChanged()

//                for (expense in expenseList){
//                    Log.e(TAG, "onResume: Expense : ${expense}", )
//                    if (!expenseArrayList.contains(expense)) {
//                        expenseArrayList.add(expense)
//                        expenseAdapter.notifyDataSetChanged()
//                    }
//                }
            } })
    }



    private fun navigateToPropertyDetails(property: PropertyItem?, position:Int) {
        val intent = Intent(this, PropertyDetailActivity::class.java)
        intent.putExtra("selectedProperty", Gson().toJson(property))
        intent.putExtra("selectedPropertyPosition", position)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}