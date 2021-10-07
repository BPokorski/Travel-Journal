package com.example.traveljournal.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.traveljournal.data.model.request.LoginRequest
import com.example.traveljournal.data.model.request.SignUpRequest
import com.example.traveljournal.data.model.response.*
import com.example.traveljournal.data.retrofit.ServiceGenerator
import com.example.traveljournal.data.retrofit.TravelJournalService
import com.example.traveljournal.utils.StringUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(context: Context) {
    private var travelJournalService: TravelJournalService = ServiceGenerator
            .createService(TravelJournalService::class.java,context)
    private val stringUtils: StringUtils = StringUtils()

    fun signIn(login: String, password: String): LiveData<Resource<JwtResponse?>?> {
        var jwtResponse: MutableLiveData<Resource<JwtResponse?>?> = MutableLiveData()
        var loginRequest = LoginRequest()
        loginRequest.login = login
        loginRequest.password = password
        var call: Call<JwtResponse> = travelJournalService.authenticateUser(loginRequest)

        call.enqueue(object : Callback<JwtResponse?> {
            override fun onResponse(call: Call<JwtResponse?>, response: Response<JwtResponse?>) {
                if (response.isSuccessful) {
                    var body = response.body()
                    jwtResponse.value = Resource.success(body)
                } else {
                    if (response.code() == 400) {
                        var gson = Gson()
                        var errorResponse = gson.fromJson<ErrorResponse>(
                            response.errorBody()?.string(),
                            object : TypeToken<ErrorResponse>() {}.type
                        )
                        jwtResponse.value = Resource.error(errorResponse.message, null)
                    } else if (response.code() == 401) {
                        jwtResponse.value = Resource.error("Invalid user or password", null)
                    }
                }
            }

            override fun onFailure(call: Call<JwtResponse?>, t: Throwable) {
                jwtResponse.value = Resource.error(t.message, null)
            }
        })
        return jwtResponse
    }

    fun signUp(login: String, email: String, password: String): MutableLiveData<Resource<MessageResponse?>> {
        var messageResponse: MutableLiveData<Resource<MessageResponse?>> = MutableLiveData()
        var signUpRequest = SignUpRequest()

        signUpRequest.login = login
        signUpRequest.email = email
        signUpRequest.password = password

        var call: Call<MessageResponse> = travelJournalService.registerUser(signUpRequest)

        call.enqueue(object : Callback<MessageResponse?> {
            override fun onResponse(
                call: Call<MessageResponse?>,
                response: Response<MessageResponse?>
            ) {
                if (response.isSuccessful) {
                    var body = response.body()
                    messageResponse.value = Resource.success(body)
                } else {
                    if (response.code() == 400) {
                        var gson = Gson()
                        var errorResponse = gson.fromJson<ErrorResponse>(
                            response.errorBody()?.string(),
                            object : TypeToken<ErrorResponse>() {}.type
                        )
                        messageResponse.value = Resource.error(errorResponse.message, null)
                    }
                }
            }

            override fun onFailure(call: Call<MessageResponse?>, t: Throwable) {
                messageResponse.value = Resource.error(t.message, null)
            }
        })
        return messageResponse
    }

    fun getCountries(login: String): MutableLiveData<Resource<List<CountryResponse?>?>> {
        var countries: MutableLiveData<Resource<List<CountryResponse?>?>> = MutableLiveData()
        val call: Call<List<CountryResponse>?>? = travelJournalService.getCountriesInContinent(login)
        call?.enqueue(object : Callback<List<CountryResponse>?> {
            override fun onResponse(
                call: Call<List<CountryResponse>?>,
                response: Response<List<CountryResponse>?>
            ) {
                if (response.body() != null) {
                    var body = response.body()
                    countries.value = Resource.success(body)

                } else {
                    countries.value = Resource.error("Server problems. Try Again", null)
                }
            }

            override fun onFailure(call: Call<List<CountryResponse>?>, t: Throwable) {
                countries.value = Resource.error(t.message, null)
                t.printStackTrace();
            }
        })
        return countries
    }

    fun getCountryPhotoData(login: String, country: String): MutableLiveData<Resource<List<PhotoDataResponse>?>> {
        var photoData: MutableLiveData<Resource<List<PhotoDataResponse>?>> =  MutableLiveData()
        var lowercaseCountry = stringUtils.connectorChanger(
            stringUtils.toLowerCaseConverter(country),
            " ",
            "-"
        )
        val call: Call<List<PhotoDataResponse>?>? = travelJournalService.getPhotoData(
            login,
            lowercaseCountry
        )

        call?.enqueue(object : Callback<List<PhotoDataResponse>?> {
            override fun onResponse(
                    call: Call<List<PhotoDataResponse>?>,
                    response: Response<List<PhotoDataResponse>?>
            ) {
                if (response.body() != null) {
                    var body = response.body()
                    photoData.value = Resource.success(body)

                } else {
                    photoData.value = Resource.error("Server problems. Try Again", null)
                }
            }

            override fun onFailure(call: Call<List<PhotoDataResponse>?>, t: Throwable) {
                photoData.value = Resource.error("Server problems. Try Again", null)
                t.printStackTrace();
            }
        })
        return photoData
    }

    fun getOceanPhotoData(login: String): MutableLiveData<Resource<List<PhotoDataResponse>?>> {
        var photoData: MutableLiveData<Resource<List<PhotoDataResponse>?>> =  MutableLiveData()

        val call: Call<List<PhotoDataResponse>?>? = travelJournalService.getOceanPhotoData(
                login
        )

        call?.enqueue(object : Callback<List<PhotoDataResponse>?> {
            override fun onResponse(
                    call: Call<List<PhotoDataResponse>?>,
                    response: Response<List<PhotoDataResponse>?>
            ) {
                if (response.isSuccessful) {
                    var body = response.body()
                    photoData.value = Resource.success(body)
                } else {
                    photoData.value = Resource.error(response.message(), null)
                }
            }

            override fun onFailure(call: Call<List<PhotoDataResponse>?>, t: Throwable) {
                photoData.value = Resource.error("Server problems. Try Again", null)
                t.printStackTrace();
            }
        })
        return photoData
    }

    fun addPhoto(login: String?, multipartBody: MultipartBody.Part): MutableLiveData<Resource<PhotoDataResponse?>?> {
        var photoData: MutableLiveData<Resource<PhotoDataResponse?>?> = MutableLiveData()
        val call: Call<PhotoDataResponse> = travelJournalService.addPhoto(
            login,
            multipartBody
        )

        call.enqueue(object : Callback<PhotoDataResponse?> {
            override fun onResponse(
                    call: Call<PhotoDataResponse?>,
                    response: Response<PhotoDataResponse?>
            ) {
                if (response.isSuccessful) {
                    var body = response.body()
                    photoData.value = Resource.success(body)
                } else {
                    photoData.value = Resource.error("Server problems. Try again", null)
                }
            }
            override fun onFailure(call: Call<PhotoDataResponse?>, t: Throwable) {
                photoData.value = Resource.error(t.message, null)
            }
        })
        return photoData
    }

    fun updateDescription(login: String?, photoId: String?, description: String?): MutableLiveData<Resource<PhotoDataResponse?>?> {
        var photoData: MutableLiveData<Resource<PhotoDataResponse?>?> = MutableLiveData()
        val call: Call<PhotoDataResponse> = travelJournalService.updateDescription(login, photoId, description)

        call.enqueue(object : Callback<PhotoDataResponse?> {
            override fun onResponse(
                    call: Call<PhotoDataResponse?>,
                    response: Response<PhotoDataResponse?>
            ) {
                if (response.isSuccessful) {
                    var body = response.body()
                    photoData.value = Resource.success(body)
                } else {
                    photoData.value = Resource.error("Server problems. Try again", null)
                }
            }

            override fun onFailure(call: Call<PhotoDataResponse?>, t: Throwable) {
                photoData.value = Resource.error(t.message, null)
            }
        })
        return photoData
    }
}