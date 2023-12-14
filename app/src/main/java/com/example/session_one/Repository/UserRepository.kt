package com.example.session_one.Repository

import android.content.Context
import android.util.Log
import com.example.session_one.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserRepository(private val context: Context) {
    private val TAG = this.toString()
    private val db = Firebase.firestore

    private val COLLECTION_USERS = "Users"
    private val FIELD_EMAIL = "email"
    private val FIELD_PASSWORD = "password"
    private val FIELD_FULL_NAME = "fullName"
    private val FIELD_USER_TYPE = "userType"




    fun addUserToDB(newUser : User){
        try{
            val data : MutableMap<String, Any> = HashMap()
            
            data[FIELD_EMAIL] = newUser.email
            data[FIELD_PASSWORD] = newUser.password
            data[FIELD_USER_TYPE] = newUser.userType
            data[FIELD_FULL_NAME] = newUser.fullName
            
            db.collection(COLLECTION_USERS)
                .document(newUser.email)
                .set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "addUserToDB: User document successfully created with ID $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "addUserToDB: Unable to create user document due to exception : $ex", )
                }
            
        }catch (ex : Exception){
            Log.e(TAG, "addUserToDB: Couldn't add user document $ex", )
        }
    }

    fun getAllUsers(callback: (List<User>) -> Unit ) {
        val auth = FirebaseAuth.getInstance()

        // Get all users from Firebase Authentication
        db.collection(COLLECTION_USERS).get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<User>()

                for (document in querySnapshot.documents) {

                    val email = document.getString("email")
                    val fullName = document.getString("fullName")
                    val password = document.getString("password")
                    val userType = document.getString("userType")

                    if (email != null) {
                        val userData = User(email,fullName!!,password!!,userType!!)
                        userList.add(userData)
                    }
                }
                callback(userList)


            }
            .addOnFailureListener { ex ->
                Log.e(TAG, "addPropertyToDB: Exception ocurred while adding a document : $ex",)
            }
    }


}