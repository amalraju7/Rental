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
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.session_one.Repository.UserRepository

import com.example.session_one.databinding.ActivitySignupBinding
import com.example.session_one.models.UserValidation
import com.example.session_one.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class SignUpActivity : OnClickListener, AppCompatActivity() {

    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    private var isLoggedIn: Boolean = false
    private val navigationHelper = NavigationHelper()
    private lateinit var purpose: String
    private lateinit var selectedProperty: String
    private lateinit var binding: ActivitySignupBinding
    private lateinit var errorText: TextView
    private lateinit var login: LinearLayout
    private lateinit var register: MaterialButton
    private  lateinit var firebaseAuth : FirebaseAuth
    private lateinit var userRepository: UserRepository
    private lateinit var  userList : List<User>
    private var selectedText: String = "Landlord"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        this.sharedPrefEditor = this.sharedPreferences.edit()
        this.isLoggedIn = this.sharedPreferences.getString("active_user", "") != ""

        this.errorText = this.binding.errorText
        errorText.visibility = View.GONE

        val currentIntent = this@SignUpActivity.intent

        if (currentIntent != null) {

            println(currentIntent.getStringExtra("selectedProperty"))

            this.purpose = currentIntent.getStringExtra("intention").toString()
            this.selectedProperty = currentIntent.getStringExtra("selectedProperty").toString()

        }
        this.firebaseAuth = FirebaseAuth.getInstance()
        this.userRepository = UserRepository(applicationContext)



        this.binding.usertype.setOnCheckedChangeListener { radioGroup, selectedRadioButtonID ->
            val selectedOption : RadioButton = findViewById(selectedRadioButtonID)
            selectedText = selectedOption.text.toString()




        }
        userRepository.getAllUsers(retrieveAllUsers)
        this.login = this.binding.login
        login.setOnClickListener(this)

        this.register = this.binding.register
        register.setOnClickListener(this)

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

            R.id.login -> {

                finish()

            }

            R.id.register -> {

                val error = checkErrors()

                if ( !error ) {






                    if ( userList !== null ) {

                        var identicalUser : User? =  userList.find {it.email == this.binding.email.text.toString().trim()}

                        if ( identicalUser !== null ) {

                            errorText.visibility = View.VISIBLE

                            errorText.setPadding(20, 20, 20, 20);

                            errorText.setBackgroundColor( Color.argb(30,222, 63, 63))

                            errorText.setText("User already exists")

                            return

                        }

                    }



                    val newUser = User(

                        email = this.binding.email.text.toString(),

                        fullName = this.binding.fullName.text.toString(),

                        password = this.binding.password.text.toString(),

                                userType = selectedText

                    )

                    createAccount(user = newUser)

                    val gson = Gson()






                    this.sharedPrefEditor.putString("active_user", gson.toJson(newUser))

                    this.sharedPrefEditor.apply()

                    val intent = Intent(this@SignUpActivity, navigationHelper.classToNavigate(this.purpose))

                    intent.putExtra("intention", this.purpose)

                    intent.putExtra("selectedProperty", this.selectedProperty)

                    startActivity(intent)


                }

            }

        }

    }

    private fun createAccount(user: User) {
//        SignUp using FirebaseAuth

        this.firebaseAuth
            .createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(this){task ->
                userRepository.addUserToDB(user)

                if (task.isSuccessful){
                    //create user document with default profile info



                    Log.d("TAG", "createAccount: User account successfully create with email ${user.email}")
//                    saveToPrefs(email, password)

                }else{
                    Log.d("TAG", "createAccount: Unable to create user account : ${task.exception}", )
                    Toast.makeText(this@SignUpActivity, "Account creation failed", Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun checkErrors() : Boolean {

        val userValidation = UserValidation()

        val error = userValidation.registrationCheck(

            email = this.binding.email.text.toString(),

            password = this.binding.password.text.toString(),

            fullName = this.binding.fullName.text.toString()

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