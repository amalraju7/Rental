package com.example.session_one

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.TextView
import com.example.session_one.Repository.UserRepository
import com.example.session_one.databinding.ActivitySigninBinding
import com.example.session_one.models.UserValidation
import com.example.session_one.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SignInActivity : OnClickListener , AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var errorText: TextView
    private lateinit var register: LinearLayout
    private lateinit var login: MaterialButton
    private lateinit var purpose: String
    private lateinit var selectedProperty: String
    private var isLoggedIn: Boolean = false

    private val gson = Gson()
    private  lateinit var firebaseAuth : FirebaseAuth
    private lateinit var userRepository: UserRepository
    private  var  userList : List<User>? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private val navigationHelper = NavigationHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        this.sharedPrefEditor = this.sharedPreferences.edit()
        this.isLoggedIn = this.sharedPreferences.getString("active_user", "") != ""

        this.errorText = this.binding.errorText
        errorText.visibility = View.GONE
        this.firebaseAuth = FirebaseAuth.getInstance()
        this.userRepository = UserRepository(applicationContext)

        val currentIntent = this@SignInActivity.intent
        userRepository.getAllUsers(retrieveAllUsers)
        if (currentIntent != null) {

            println(currentIntent.getStringExtra("intention"))

            this.purpose = currentIntent.getStringExtra("intention").toString()
            this.selectedProperty = currentIntent.getStringExtra("selectedProperty").toString()

        }

        this.binding.register.setOnClickListener(this)
        this.binding.login.setOnClickListener(this)

    }

    override fun onResume() {
        this.isLoggedIn = this.sharedPreferences.getString("active_user", "") != ""
        super.onResume()
        if ( isLoggedIn ) finish()
    }

    val  retrieveAllUsers = fun(users:List<User>){
        userList = users
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.register -> {

                val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                intent.putExtra("intention", this.purpose)
                if ( this.selectedProperty.isNotEmpty() ) intent.putExtra("selectedProperty", this.selectedProperty)
                startActivity(intent)

                return

            }

            R.id.login -> {

                val error = checkErrors()

                if ( !error ) {



                    if ( userList !== null ) {

                        var identicalUser : User? =  userList?.find {

                             it.email == this.binding.email.text.toString() &&

                             it.password == this.binding.password.text.toString()

                            }

                        if ( identicalUser !== null ) {

                            errorText.visibility = View.GONE

                            this.sharedPrefEditor.putString("active_user", gson.toJson(identicalUser))

                            this.sharedPrefEditor.apply()

                            val intent = Intent(this@SignInActivity, navigationHelper.classToNavigate(this.purpose))

                            intent.putExtra("intention", this.purpose)

                            intent.putExtra("selectedProperty", this.selectedProperty)

                            startActivity(intent)

                            return

                        }

                    }

                    errorText.visibility = View.VISIBLE

                    errorText.setPadding(20, 20, 20, 20);

                    errorText.setBackgroundColor( Color.argb(30,222, 63, 63))

                    errorText.setText("Login failed, please ensure your credentials are correct")

                }

            }

        }
    }

    private fun checkErrors() : Boolean {

        val userValidation = UserValidation()

        val error = userValidation.registrationCheck(

            email = this.binding.email.text.toString(),

            password = this.binding.password.text.toString(),

            fullName = "Fion James"

        );

        if ( error.isNotEmpty() ) {

            errorText.visibility = View.VISIBLE

            errorText.setPadding(20, 20, 20, 20);

            errorText.setBackgroundColor( Color.argb(30,222, 63, 63))

        } else {

            errorText.visibility = View.GONE

            errorText.setPadding(0, 0, 0, 0);

        }

        errorText.setText(error);

        println(error)

        return error.isNotEmpty()

    }

}