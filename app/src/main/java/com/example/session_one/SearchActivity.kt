package com.example.session_one

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import com.google.gson.Gson
import android.widget.TextView
import com.example.session_one.databinding.ActivitySearchPageBinding
import com.example.session_one.models.User
import com.google.android.material.button.MaterialButton

class SearchActivity : OnClickListener, AppCompatActivity() {

    private lateinit var searchRental: MaterialButton;
    private lateinit var binding: ActivitySearchPageBinding;
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewAll: TextView
    private var isLoggedIn: Boolean = false
    private var userType:String = ""
    private val navigationHelper = NavigationHelper()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySearchPageBinding.inflate(layoutInflater)

        this.sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        this.isLoggedIn = this.sharedPreferences.getString("active_user", "") != ""
        if (this.isLoggedIn == true) {
            this.userType = gson.fromJson(this.sharedPreferences.getString("active_user", ""), User::class.java ).email
}
        setContentView(this.binding.root)

        setSupportActionBar(this.binding.searchToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        searchRental = this.binding.searchRental
        viewAll = this.binding.viewAllListing

        searchRental.setOnClickListener(this)
        viewAll.setOnClickListener(this)

    }

    override fun onResume() {
        this.sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        this.isLoggedIn = this.sharedPreferences.getString("active_user", "") != ""
        super.onResume()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (this.isLoggedIn == true) {
            if (this.userType == "Tenant") {
                menuInflater.inflate(R.menu.tenant_menu, menu)

            } else {
                menuInflater.inflate(R.menu.shortlist_menu, menu)
            }

        }else{
            menuInflater.inflate(R.menu.logged_out_user, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {

            R.id.menuRentYourProperty -> {

                val navClass =
                    if (this.isLoggedIn) navigationHelper.classToNavigate("rent-your-own-property") else SignInActivity::class.java

                val intent = Intent(this@SearchActivity, navClass)
                intent.putExtra("intention", "rent-your-own-property")
                startActivity(intent)

            }
            R.id.logOut ->{
                val editor = sharedPreferences.edit()
                editor.remove("active_user").commit()
                val intent = Intent(this@SearchActivity,SignInActivity::class.java )
                startActivity(intent)
            }

            R.id.menuLogin -> {

                val intent = Intent(this@SearchActivity,  SignInActivity::class.java)
                startActivity(intent)

            }





        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {

        when (view?.id) {

            R.id.searchRental -> {

                val searchInput = binding.searchInput.text.toString().trim()

                if (searchInput == "") {

                    binding.showError.visibility = VISIBLE

                } else {

                    binding.showError.visibility = GONE

                    val intent = Intent(this@SearchActivity, ListingActivity::class.java)
                    intent.putExtra("searchQuery", searchInput)
                    startActivity(intent)
                }

            }

            R.id.viewAllListing -> {

                val intent = Intent(this@SearchActivity, ListingActivity::class.java)
                startActivity(intent)

            }



        }
    }
}