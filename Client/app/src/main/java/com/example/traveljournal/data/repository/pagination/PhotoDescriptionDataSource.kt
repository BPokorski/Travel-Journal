package com.example.traveljournal.data.repository.pagination

import android.content.Context
import androidx.paging.PageKeyedDataSource
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.model.response.PagedDescriptionResponse
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse
import com.example.traveljournal.data.retrofit.ServiceGenerator
import com.example.traveljournal.data.retrofit.TravelJournalService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotoDescriptionDataSource(context: Context):PageKeyedDataSource<Int, PhotoDescriptionResponse>() {
    //the size of a page that we want
    val PAGE_SIZE = 3

    //we will start from the first page which is 0
    private val FIRST_PAGE = 0

    private var sessionManager = SessionManager(context)
    private var travelJournalService: TravelJournalService = ServiceGenerator
            .createService(TravelJournalService::class.java, context)
    private var login = sessionManager.fetchLogin()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, PhotoDescriptionResponse>) {

        var call: Call<PagedDescriptionResponse> = travelJournalService.getPagedDescriptions(login, FIRST_PAGE, PAGE_SIZE)

        call.enqueue(object : Callback<PagedDescriptionResponse?> {
            override fun onResponse(call: Call<PagedDescriptionResponse?>, response: Response<PagedDescriptionResponse?>) {
                if (response.isSuccessful) {
                    callback.onResult(response.body()?.descriptions!!, null, FIRST_PAGE + 1)

                }
            }

            override fun onFailure(call: Call<PagedDescriptionResponse?>, t: Throwable) {
//                Toast.makeText(context, "Internet issues. Try again", Toast.LENGTH_SHORT).show()
//                jwtResponse.value = Resource.error(t.message, null)
//                t.printStackTrace();
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoDescriptionResponse>) {
        var call: Call<PagedDescriptionResponse> = travelJournalService.getPagedDescriptions(login, params.key, PAGE_SIZE)

        call.enqueue(object : Callback<PagedDescriptionResponse?> {
            override fun onResponse(call: Call<PagedDescriptionResponse?>, response: Response<PagedDescriptionResponse?>) {
                var adjacentKey = if (params.key > 1) params.key - 1 else null
                if (response.isSuccessful) {
                    callback.onResult(response.body()?.descriptions!!, adjacentKey)

                }
            }

            override fun onFailure(call: Call<PagedDescriptionResponse?>, t: Throwable) {
//                Toast.makeText(context, "Internet issues. Try again", Toast.LENGTH_SHORT).show()
//                jwtResponse.value = Resource.error(t.message, null)
//                t.printStackTrace();
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoDescriptionResponse>) {
        var call: Call<PagedDescriptionResponse> = travelJournalService.getPagedDescriptions(login, params.key, PAGE_SIZE)

        call.enqueue(object : Callback<PagedDescriptionResponse?> {
            override fun onResponse(call: Call<PagedDescriptionResponse?>, response: Response<PagedDescriptionResponse?>) {

                if (response.isSuccessful) {
                    var adjacentKey = if (params.key < response.body()?.totalPages!!) params.key + 1 else null
                    callback.onResult(response.body()?.descriptions!!, adjacentKey)

                }
            }

            override fun onFailure(call: Call<PagedDescriptionResponse?>, t: Throwable) {
//                Toast.makeText(context, "Internet issues. Try again", Toast.LENGTH_SHORT).show()
//                jwtResponse.value = Resource.error(t.message, null)
//                t.printStackTrace();
            }
        })
    }
}