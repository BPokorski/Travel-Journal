package com.example.traveljournal.data.retrofit;

import com.example.traveljournal.data.model.request.LoginRequest;
import com.example.traveljournal.data.model.request.SignUpRequest;
import com.example.traveljournal.data.model.response.CountryResponse;
import com.example.traveljournal.data.model.response.JwtResponse;
import com.example.traveljournal.data.model.response.MessageResponse;
import com.example.traveljournal.data.model.response.PagedPhotoDataResponse;
import com.example.traveljournal.data.model.response.PhotoDataResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TravelJournalService {

    @POST("user/signin")
    Call<JwtResponse> authenticateUser(@Body LoginRequest loginRequest);

    @POST("user/signup")
    Call<MessageResponse> registerUser(@Body SignUpRequest signUpRequest);

    @GET("{login}/map")
    Call<List<CountryResponse>> getCountriesInContinent(@Path("login") String login);

    @GET("{login}/map/{country}")
    Call<List<PhotoDataResponse>> getPhotoData(
            @Path("login") String login,
            @Path("country") String country);

    @GET("{login}/map/ocean")
    Call<List<PhotoDataResponse>> getOceanPhotoData(@Path("login") String login);

    @GET("{login}/photo/photodata")
    Call<PagedPhotoDataResponse> getPagedPhotoData(@Path("login") String login,
                                                   @Query("page") int page,
                                                   @Query("size") int size);

    @Multipart
    @POST("{login}/photo")
    Call<PhotoDataResponse> addPhoto(@Path("login") String login,
                                     @Part MultipartBody.Part file);

    @PUT("{login}/photo/{photoId}/description")
    Call<PhotoDataResponse> updateDescription(@Path("login") String login,
                                              @Path("photoId") String photoId,
                                              @Query("description") String description);
}
