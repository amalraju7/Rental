package com.example.session_one

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.session_one.databinding.ActivityRentYourPropertyBinding

class RentYourPropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRentYourPropertyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRentYourPropertyBinding.inflate(layoutInflater)
        setContentView(this.binding.root)



        binding.btnAddNewProperty.setOnClickListener {
            val intent = Intent(this, AddNewPropertyActivity::class.java)
            startActivity(intent)
        }

        binding.btnViewListings.setOnClickListener {
            val intent = Intent(this, PropertyListActivity::class.java)
            startActivity(intent)
        }


    }
}