package com.example.traveljournal.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.traveljournal.data.model.request.LoginRequest
import com.example.traveljournal.data.model.request.SignUpRequest
import com.example.traveljournal.data.model.response.*
import com.example.traveljournal.data.retrofit.ServiceGenerator
import com.example.traveljournal.data.retrofit.TravelJournalService
import com.example.traveljournal.utils.StringUtills
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(context: Context) {

    private var travelJournalService: TravelJournalService = ServiceGenerator
            .createService(TravelJournalService::class.java,context)
    private val stringUtils: StringUtills = StringUtills()

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
//                Toast.makeText(context, "Internet issues. Try again", Toast.LENGTH_SHORT).show()
                jwtResponse.value = Resource.error(t.message, null)
//                t.printStackTrace();
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


                        System.out.println(errorResponse.message)
                        messageResponse.value = Resource.error(errorResponse.message, null)
//                        System.out.println(gson.fromJson(response.errorBody()?.string(), ErrorResponse()))
                    }

                    System.out.println(response.code())
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
                    System.out.println(response.code())
                }
            }

            override fun onFailure(call: Call<List<CountryResponse>?>, t: Throwable) {
                countries.value = Resource.error(t.message, null)
                t.printStackTrace();
            }
        })
        return countries
    }

    fun getCountryPhotoDescriptions(login: String, country: String): MutableLiveData<Resource<List<PhotoDescriptionResponse>?>> {
        var descriptions: MutableLiveData<Resource<List<PhotoDescriptionResponse>?>> =  MutableLiveData()
        var lowercaseCountry = stringUtils.connectorChanger(
            stringUtils.toLowerCaseConverter(country),
            " ",
            "-"
        )
        val call: Call<List<PhotoDescriptionResponse>?>? = travelJournalService.getDescriptions(
            login,
            lowercaseCountry
        )

        call?.enqueue(object : Callback<List<PhotoDescriptionResponse>?> {
            override fun onResponse(
                call: Call<List<PhotoDescriptionResponse>?>,
                response: Response<List<PhotoDescriptionResponse>?>
            ) {
                if (response.body() != null) {
                    var body = response.body()
                    descriptions.value = Resource.success(body)

                } else {
                    descriptions.value = Resource.error("Server problems. Try Again", null)
                    System.out.println(response.code())
                    System.out.println(response.code())
                }
            }

            override fun onFailure(call: Call<List<PhotoDescriptionResponse>?>, t: Throwable) {
                descriptions.value = Resource.error("Server problems. Try Again", null)
//                System.out.println(response.code())
                t.printStackTrace();
            }
        })
        return descriptions
    }

    fun getOceanPhotoDescriptions(login: String): MutableLiveData<Resource<List<PhotoDescriptionResponse>?>> {
        var descriptions: MutableLiveData<Resource<List<PhotoDescriptionResponse>?>> =  MutableLiveData()

        val call: Call<List<PhotoDescriptionResponse>?>? = travelJournalService.getOceanPhotoDescriptions(
                login
        )

        call?.enqueue(object : Callback<List<PhotoDescriptionResponse>?> {
            override fun onResponse(
                    call: Call<List<PhotoDescriptionResponse>?>,
                    response: Response<List<PhotoDescriptionResponse>?>
            ) {
                if (response.isSuccessful) {
                    var body = response.body()
                    System.out.println("Got ocean descriptions")
                    System.out.println(body?.size)
                    descriptions.value = Resource.success(body)

                } else {
                    descriptions.value = Resource.error(response.message(), null)
                }
            }

            override fun onFailure(call: Call<List<PhotoDescriptionResponse>?>, t: Throwable) {
                descriptions.value = Resource.error("Server problems. Try Again", null)
//                System.out.println(response.code())
                t.printStackTrace();
            }
        })
        System.out.println("Got data")
        return descriptions
    }

    fun addPhoto(login: String?, multipartBody: MultipartBody.Part): MutableLiveData<Resource<PhotoDescriptionResponse?>?> {

        var photoDescription: MutableLiveData<Resource<PhotoDescriptionResponse?>?> = MutableLiveData()
        val call: Call<PhotoDescriptionResponse> = travelJournalService.addPhoto(
            login,
            multipartBody
        )

        call.enqueue(object : Callback<PhotoDescriptionResponse?> {
            override fun onResponse(
                call: Call<PhotoDescriptionResponse?>,
                response: Response<PhotoDescriptionResponse?>
            ) {
                if (response.isSuccessful) {
                    var body = response.body()
                    photoDescription.value = Resource.success(body)
                } else {
                    System.out.println("EMPTY RESOURCE")
                    photoDescription.value = Resource.error("Server problems. Try again", null)
                }

            }

            override fun onFailure(call: Call<PhotoDescriptionResponse?>, t: Throwable) {

                System.out.println("SERWER?!")
                System.out.println(t.message)
                System.out.println(t.cause)
                photoDescription.value = Resource.error(t.message, null)
            }
        })
        return photoDescription
    }

    fun updateDescription(login: String?, photoId: String?, description: String?): MutableLiveData<Resource<PhotoDescriptionResponse?>?> {
        var photoDescription: MutableLiveData<Resource<PhotoDescriptionResponse?>?> = MutableLiveData()
        val call: Call<PhotoDescriptionResponse> = travelJournalService.updateDescription(login, photoId, description)

        call.enqueue(object : Callback<PhotoDescriptionResponse?> {
            override fun onResponse(
                    call: Call<PhotoDescriptionResponse?>,
                    response: Response<PhotoDescriptionResponse?>
            ) {
                if (response.isSuccessful) {
                    var body = response.body()
                    photoDescription.value = Resource.success(body)
                } else {
                    photoDescription.value = Resource.error("Server problems. Try again", null)
                }

            }

            override fun onFailure(call: Call<PhotoDescriptionResponse?>, t: Throwable) {

                System.out.println(t.message)
                System.out.println(t.cause)
                photoDescription.value = Resource.error(t.message, null)
            }
        })
        return photoDescription
    }
}