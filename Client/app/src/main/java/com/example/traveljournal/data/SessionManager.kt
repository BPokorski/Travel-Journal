package com.example.traveljournal.data

import android.content.Context
import android.content.SharedPreferences
import com.example.traveljournal.R
import com.example.traveljournal.data.model.response.PhotoDataResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context:Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_LOGIN = "login"
        const val COUNTRY_NAME = "country_name"
        const val ADDRESS = "address"
        const val PHOTO_DATA = "photo_data"
        const val SUB_ADMIN_AREA = "sub_admin_area"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun saveLogin(login: String) {
        val editor = prefs.edit()
        editor.putString(USER_LOGIN, login)
        editor.apply()
    }

    fun saveCountryName(name: String) {
        val editor = prefs.edit()
        editor.putString(COUNTRY_NAME, name)
        editor.apply()
    }

    fun saveSubAdminArea(subAdmin: String) {
        val  editor = prefs.edit()
        editor.putString(SUB_ADMIN_AREA, subAdmin)
        editor.apply()
    }

    fun savePhotoData(photoData: PhotoDataResponse) {
        val  editor = prefs.edit()
        var gson = Gson()
        var json:String = gson.toJson(photoData)
        editor.putString(PHOTO_DATA, json)
        editor.apply()
    }
    fun savePlaceAddress(address: String) {
        val editor = prefs.edit()
        editor.putString(ADDRESS, address)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun fetchLogin(): String? {
        return prefs.getString(USER_LOGIN, null)
    }

    fun fetchPhotoData():PhotoDataResponse? {
        var gson = Gson()
        var json: String? = prefs.getString(PHOTO_DATA, "")
        return gson.fromJson(json, object : TypeToken<PhotoDataResponse>() {}.type)
    }

    fun fetchCountryName():String? {
        return prefs.getString(COUNTRY_NAME, null)
    }
    fun fetchAddress():String? {
        return prefs.getString(ADDRESS, null)
    }

    fun fetchSubAdmin():String? {
        return prefs.getString(SUB_ADMIN_AREA, null)
    }

    fun removeSavedData() {
       prefs.edit().clear().apply()
    }
    fun removeSingleItem(keyName:String) {
        prefs.edit().remove(keyName).apply()
    }
}