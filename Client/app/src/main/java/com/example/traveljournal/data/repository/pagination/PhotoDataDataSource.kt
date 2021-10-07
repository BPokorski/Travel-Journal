package com.example.traveljournal.data.repository.pagination

import android.content.Context
import androidx.paging.PageKeyedDataSource
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.model.response.PagedPhotoDataResponse
import com.example.traveljournal.data.model.response.PhotoDataResponse
import com.example.traveljournal.data.retrofit.ServiceGenerator
import com.example.traveljournal.data.retrofit.TravelJournalService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotoDataDataSource(context: Context):PageKeyedDataSource<Int, PhotoDataResponse>() {
    //the size of a page that we want
    val PAGE_SIZE = 3

    //we will start from the first page which is 0
    private val FIRST_PAGE = 0

    private var sessionManager = SessionManager(context)
    private var travelJournalService: TravelJournalService = ServiceGenerator
            .createService(TravelJournalService::class.java, context)
    private var login = sessionManager.fetchLogin()
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, PhotoDataResponse>) {

        var call: Call<PagedPhotoDataResponse> = travelJournalService.getPagedPhotoData(login, FIRST_PAGE, PAGE_SIZE)

        call.enqueue(object : Callback<PagedPhotoDataResponse?> {
            override fun onResponse(call: Call<PagedPhotoDataResponse?>, response: Response<PagedPhotoDataResponse?>) {
                if (response.isSuccessful) {
                    callback.onResult(response.body()?.data!!, null, FIRST_PAGE + 1)

                }
            }

            override fun onFailure(call: Call<PagedPhotoDataResponse?>, t: Throwable) {
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoDataResponse>) {
        var call: Call<PagedPhotoDataResponse> = travelJournalService.getPagedPhotoData(login, params.key, PAGE_SIZE)

        call.enqueue(object : Callback<PagedPhotoDataResponse?> {
            override fun onResponse(call: Call<PagedPhotoDataResponse?>, response: Response<PagedPhotoDataResponse?>) {
                var adjacentKey = if (params.key > 1) params.key - 1 else null
                if (response.isSuccessful) {
                    callback.onResult(response.body()?.data!!, adjacentKey)

                }
            }

            override fun onFailure(call: Call<PagedPhotoDataResponse?>, t: Throwable) {
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoDataResponse>) {
        var call: Call<PagedPhotoDataResponse> = travelJournalService.getPagedPhotoData(login, params.key, PAGE_SIZE)

        call.enqueue(object : Callback<PagedPhotoDataResponse?> {
            override fun onResponse(call: Call<PagedPhotoDataResponse?>, response: Response<PagedPhotoDataResponse?>) {

                if (response.isSuccessful) {
                    var adjacentKey = if (params.key < response.body()?.totalPages!!) params.key + 1 else null
                    callback.onResult(response.body()?.data!!, adjacentKey)
                }
            }

            override fun onFailure(call: Call<PagedPhotoDataResponse?>, t: Throwable) {
            }
        })
    }
}