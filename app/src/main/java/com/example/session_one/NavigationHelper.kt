package com.example.session_one

class NavigationHelper {
    fun classToNavigate (purpose: String) : Class<*> {
        when (purpose) {
            "rent-your-own-property" -> {
                return RentYourPropertyActivity::class.java
            }
            "shortlist" -> {
                return ShortListActivity::class.java
            }
            else -> {
                return PropertyViewActivity::class.java
            }
        }
    }
}