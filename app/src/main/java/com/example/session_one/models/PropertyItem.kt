package com.example.session_one.models

import java.io.Serializable
import java.util.UUID

class PropertyItem(
    val id: String = UUID.randomUUID().toString(),
    var image: String = "",
    val amount: String = "",
    val beds: Int = 0,
    val baths: Int =0,
    val squareFoots: Double= 0.0,
    val address: String ="",
    val province: String="",
    val codeName: String="",
    val availability: Boolean=true,
    val latitude:String="",
    val longitude:String="",
    val propertyType: String="",
    val description: String=""

                   ): Serializable {


}