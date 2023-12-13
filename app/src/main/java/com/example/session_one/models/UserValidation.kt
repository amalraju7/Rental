package com.example.session_one.models

import java.util.regex.Pattern

class UserValidation {

    fun registrationCheck(email: String, fullName: String, password: String): String {

        var error = ""
        val regex: Pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]")
        val emailRegex: Pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
        val nameRegex: Pattern = Pattern.compile("^[a-zA-Z ]+\$")

        try {

            if (email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                error += "Please fill in all the required fields. "
            } else {

                if (password.trim().length < 4) {
                    error += "Password should have at least 4 characters. "
                }

                if (regex.matcher(fullName).find()) {
                    error += "Full name should not contain special characters. "
                }

                if (!nameRegex.matcher(fullName).matches()) {
                    error += "Full name should contain only letters and spaces. "
                }

                if (!emailRegex.matcher(email).matches()) {
                    error += "Please enter a valid email address. "
                }
            }

        } catch (e: Exception) {
            error += "Please enter only valid characters. "
        }

        return error

    }
}