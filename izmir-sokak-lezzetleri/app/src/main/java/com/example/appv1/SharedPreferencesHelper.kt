package com.example.appv1

import android.content.Context


class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


    fun saveEmail(email: String) {
        with(sharedPreferences.edit()) {
            putString("email", email)
            apply()
        }
    }

    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    // Email değerini silme
    fun clearEmail() {
        with(sharedPreferences.edit()) {
            remove("email")
            apply()
        }
    }
}
