package com.example.session_one.Repository



import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.session_one.models.PropertyItem
import com.example.session_one.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.gson.Gson

//Controller
class PropertyItemRepository(private val context : Context) {
    private val TAG = this.toString();


    //get an instance of firestore database
    private val db = Firebase.firestore
    private val gson = Gson()
    private val COLLECTION_PROPERTIES = "Properties";
    private val COLLECTION_USERS = "Users";
    private val COLLECTION_SHORTLIST="shortlist"
    private val FIELD_IMAGE = "image";
    private val FIELD_AMOUNT = "amount"
    private val FIELD_BEDS = "beds";
    private val FIELD_BATHS = "baths";
    private val FIELD_SQUARE_FOOTS = "squareFoots"
    private val FIELD_ADDRESS = "address";
    private val FIELD_PROVINCE = "province";
    private val FIELD_CODE_NAME = "codeName"
    private val FIELD_AVAILABILITY = "availability";
    private val FIELD_ID = "id";
    private val FIELD_LATITUDE = "latitude";
    private val FIELD_LONGITUDE = "longitude";
    private val FIELD_PROPERTY_TYPE = "propertyType"
    private val FIELD_DESCRIPTION = "description"
    var allPropertyItems: MutableLiveData<List<PropertyItem>> =
        MutableLiveData<List<PropertyItem>>()

    private var loggedInUserEmail = ""
    private lateinit var sharedPrefs: SharedPreferences

    init {
        sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        if (sharedPrefs.contains("active_user")) {
            loggedInUserEmail = gson.fromJson( this.sharedPrefs.getString("active_user", ""), User::class.java ).email
        }
    }

    fun addPropertyToDB(newPropertyItem: PropertyItem) {

        try {
            val data: MutableMap<String, Any> = HashMap();

            data[FIELD_IMAGE] = newPropertyItem.image;
            data[FIELD_AMOUNT] = newPropertyItem.amount
            data[FIELD_BEDS] = newPropertyItem.beds
            data[FIELD_BATHS] = newPropertyItem.baths
            data[FIELD_SQUARE_FOOTS] = newPropertyItem.squareFoots;
            data[FIELD_ID] = newPropertyItem.id
            data[FIELD_ADDRESS] = newPropertyItem.address
            data[FIELD_PROVINCE] = newPropertyItem.province
            data[FIELD_CODE_NAME] = newPropertyItem.codeName
            data[FIELD_AVAILABILITY] = newPropertyItem.availability
            data[FIELD_DESCRIPTION] = newPropertyItem.description
            data[FIELD_PROPERTY_TYPE] = newPropertyItem.propertyType
            data[FIELD_LATITUDE] = newPropertyItem.latitude
            data[FIELD_LONGITUDE] = newPropertyItem.longitude

            //for adding document to nested collection
//                db.collection(COLLECTION_USERS)
//                    .document(loggedInUserEmail)
//                    .collection(COLLECTION_EXPENSES)
//                    .add(data)
//                    .addOnSuccessListener { docRef ->
//                        Log.d(
//                            TAG,
//                            "addExpenseToDB: Document successfully added with ID : ${docRef.id}"
//                        )
//                    }
//                    .addOnFailureListener { ex ->
//                        Log.e(
//                            TAG,
//                            "addExpenseToDB: Exception ocurred while adding a document : $ex",
//                        )
//                    }

            //for adding document to root level collection
            db.collection(COLLECTION_USERS).document(loggedInUserEmail).collection(COLLECTION_PROPERTIES).document(newPropertyItem.id)
                .set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "addExpenseToDB: Document successfully added with ID : ${docRef}")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "addExpenseToDB: Exception ocurred while adding a document : $ex",)
                }

//            db.collection(COLLECTION_EXPENSES)
//                .document("test1@gmail.com")
//                .set(data).addOnSuccessListener {  }
//                .addOnFailureListener{}

        } catch (ex: java.lang.Exception) {
            Log.d(
                TAG,
                "addExpenseToDB: Couldn't perform insert on Expenses collection due to exception $ex"
            )


        }
    }

    fun retrieveAllExpenses() {
     if (loggedInUserEmail.isNotEmpty()) {
        try {
            db.collection(COLLECTION_USERS).document(loggedInUserEmail).collection(COLLECTION_PROPERTIES)
                .addSnapshotListener(EventListener { result, error ->
                    if (error != null) {
                        Log.e(
                            TAG,
                            "retrieveAllExpenses: Listening to Expenses collection failed due to error : $error",
                        )
                        return@EventListener
                    }

                    if (result != null) {
                        Log.d(
                            TAG,
                            "retrieveAllExpenses: Number of documents retrieved : ${result.size()}"
                        )

                        val tempList: MutableList<PropertyItem> = ArrayList<PropertyItem>()

                        for (docChanges in result.documentChanges) {

                            val currentDocument: PropertyItem =
                                docChanges.document.toObject(PropertyItem::class.java)
                            Log.d(TAG, "retrieveAllExpenses: currentDocument : $currentDocument")

                            when (docChanges.type) {
                                DocumentChange.Type.ADDED -> {
                                    //do necessary changes to your local list of objects
                                    tempList.add(currentDocument)
                                }

                                DocumentChange.Type.MODIFIED -> {


                                }

                                DocumentChange.Type.REMOVED -> {

                                }
                            }
                        }//for
                        Log.d(TAG, "retrieveAllExpenses: tempList : $tempList")
                        //replace the value in allExpenses

                        allPropertyItems.postValue(tempList)

                    } else {
                        Log.d(TAG, "retrieveAllExpenses: No data in the result after retrieving")
                    }
                })


        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "retrieveAllExpenses: Unable to retrieve all expenses : $ex",)
        }
        }else{
            Log.e(TAG, "retrieveAllExpenses: Cannot retrieve expenses without user's email address. You must sign in first.", )
        }
    }

//    fun filterExpenses(amount : Double, persons : Int){
//        if (loggedInUserEmail.isNotEmpty()) {
//            try{
//                db.collection(COLLECTION_USERS)
//                    .document(loggedInUserEmail)
//                    .collection(COLLECTION_EXPENSES)
//                    .whereGreaterThan(FIELD_CHECK_AMOUNT, amount)
//                    .whereLessThan(FIELD_PERSONS, persons)
//                    .addSnapshotListener(EventListener { result, error ->
//                        //check for result or errors and update UI accordingly
//                        if (error != null){
//                            Log.e(TAG,
//                                "filterExpenses: Listening to Expenses collection failed due to error : $error", )
//                            return@EventListener
//                        }
//
//                        if (result != null){
//                            Log.d(TAG, "filterExpenses: Number of documents retrieved : ${result.size()}")
//
//                            val tempList : MutableList<Expense> = ArrayList<Expense>()
//
//                            for (docChanges in result.documentChanges){
//
//                                val currentDocument : Expense = docChanges.document.toObject(Expense::class.java)
//                                Log.d(TAG, "filterExpenses: currentDocument : $currentDocument")
//
//                                //do necessary changes to your local list of objects
//                                tempList.add(currentDocument)
//                            }//for
//                            Log.d(TAG, "filterExpenses: tempList : $tempList")
//                            //replace the value in allExpenses
//                            allPropertyItems.postValue(tempList)
//
//                        }else{
//                            Log.d(TAG, "filterExpenses: No data in the result after retrieving")
//                        }
//                    })
//            }
//            catch (ex : java.lang.Exception){
//                Log.e(TAG, "filterExpenses: Unable to filter expenses : $ex", )
//            }
//        }
//    }
//
    fun updateProperty(propertyToUpdate : PropertyItem){
        val data: MutableMap<String, Any> = HashMap();


    data[FIELD_AMOUNT] = propertyToUpdate.amount
    data[FIELD_BEDS] = propertyToUpdate.beds
    data[FIELD_BATHS] = propertyToUpdate.baths
    data[FIELD_SQUARE_FOOTS] = propertyToUpdate.squareFoots;
    data[FIELD_ADDRESS] = propertyToUpdate.address
    data[FIELD_PROVINCE] = propertyToUpdate.province
    data[FIELD_CODE_NAME] = propertyToUpdate.codeName
    data[FIELD_AVAILABILITY] = propertyToUpdate.availability
    data[FIELD_DESCRIPTION] = propertyToUpdate.description
    data[FIELD_PROPERTY_TYPE] = propertyToUpdate.propertyType
    Log.d(TAG,propertyToUpdate.id)

        try{

                db.collection(COLLECTION_USERS).document(loggedInUserEmail).collection(COLLECTION_PROPERTIES)
                .document(propertyToUpdate.id)
                .update(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "updateExpense: Document updated successfully : $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "updateExpense: Failed to update document : $ex", )
                }
        }
        catch (ex : Exception){
            Log.e(TAG, "updateExpense: Unable to update expense due to exception : $ex", )
        }
    }

    fun deleteExpense(propertyToDelete : PropertyItem?){
        try{

                db.collection(COLLECTION_PROPERTIES)
                .document((propertyToDelete as PropertyItem).id)
                .delete()
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "updateExpense: Document deleted successfully : $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "updateExpense: Failed to delete document : $ex", )
                }
        }
        catch (ex : Exception){
            Log.e(TAG, "updateExpense: Unable to delete expense due to exception : $ex", )
        }
    }

    fun shortlistProperty(propertyItem: PropertyItem){
        try{
            val data : MutableMap<String, Any> = HashMap()
            data[FIELD_IMAGE] = propertyItem.image;
            data[FIELD_AMOUNT] = propertyItem.amount
            data[FIELD_BEDS] = propertyItem.beds
            data[FIELD_BATHS] = propertyItem.baths
            data[FIELD_SQUARE_FOOTS] = propertyItem.squareFoots;
            data[FIELD_ID] = propertyItem.id
            data[FIELD_ADDRESS] = propertyItem.address
            data[FIELD_PROVINCE] = propertyItem.province
            data[FIELD_CODE_NAME] = propertyItem.codeName
            data[FIELD_AVAILABILITY] = propertyItem.availability
            data[FIELD_DESCRIPTION] = propertyItem.description
            data[FIELD_PROPERTY_TYPE] = propertyItem.propertyType
            data[FIELD_LATITUDE] = propertyItem.latitude
            data[FIELD_LONGITUDE] = propertyItem.longitude

            db.collection(COLLECTION_USERS)
                .document(loggedInUserEmail)
                .collection(COLLECTION_SHORTLIST)
                .document(propertyItem.id)
                .set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "Property added to shortlist in firebase: Document successfully added with ID : ${docRef}")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "Can not add property to shortlist in firebase: Exception ocurred while adding a document : $ex",)
                }


        }catch (ex : Exception){
            Log.e(TAG, "addUserToDB: Couldn't add user document $ex", )
        }

    }

    fun deleteShortlist(propertyItem: PropertyItem){
        try{
            db.collection(COLLECTION_USERS)
                .document(loggedInUserEmail)
                .collection(COLLECTION_SHORTLIST)
                .document(propertyItem.id)
                .delete()
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "updateExpense: Document deleted successfully of id: ${propertyItem.id} : $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "updateExpense: Failed to delete document : $ex", )
                }
        }
        catch (ex : Exception){
            Log.e(TAG, "updateExpense: Unable to delete expense due to exception : $ex", )
        }
    }

    fun retrieveAllShortlist(){
        if (loggedInUserEmail.isNotEmpty()) {
            try{
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserEmail)
                    .collection(COLLECTION_SHORTLIST)
                    .addSnapshotListener(EventListener{ result, error ->
                        if (error != null){
                            Log.e(TAG,
                                "retrieveAllExpenses: Listening to Expenses collection failed due to error : $error", )
                            return@EventListener
                        }

                        if (result != null){
                            Log.d(TAG, "retrieveAllExpenses: Number of documents retrieved : ${result.size()}")

                            val tempList : MutableList<PropertyItem> = ArrayList<PropertyItem>()

                            for (docChanges in result.documentChanges){

                                val currentDocument : PropertyItem = docChanges.document.toObject(PropertyItem::class.java)
                                Log.d(TAG, "retrieveAllExpenses: currentDocument : $currentDocument")

                                when(docChanges.type){
                                    DocumentChange.Type.ADDED -> {
                                        //do necessary changes to your local list of objects
                                        tempList.add(currentDocument)
                                    }
                                    DocumentChange.Type.MODIFIED -> {

                                    }
                                    DocumentChange.Type.REMOVED -> {

                                    }
                                }
                            }//for
                            Log.d(TAG, "retrieveAllExpenses: tempList : $tempList")
                            //replace the value in allExpenses

                            allPropertyItems.postValue(tempList)

                        }else{
                            Log.d(TAG, "retrieveAllExpenses: No data in the result after retrieving")
                        }
                    })


            }
            catch (ex : java.lang.Exception){
                Log.e(TAG, "retrieveAllExpenses: Unable to retrieve all expenses : $ex", )
            }
        }else{
            Log.e(TAG, "retrieveAllExpenses: Cannot retrieve expenses without user's email address. You must sign in first.", )
        }
    }


}
