package com.example.traveljournal.data.retrofit;

//import okhttp3.Call;
import com.example.traveljournal.data.model.request.LoginRequest;
import com.example.traveljournal.data.model.request.SignUpRequest;

//import com.example.traveljournal.data.model.Response.ContinentResponse;
import com.example.traveljournal.data.model.response.CountryResponse;
import com.example.traveljournal.data.model.response.JwtResponse;
import com.example.traveljournal.data.model.response.MessageResponse;
import com.example.traveljournal.data.model.response.PagedDescriptionResponse;
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    Call<List<PhotoDescriptionResponse>> getDescriptions(
            @Path("login") String login,
            @Path("country") String country);

    @GET("{login}/map/ocean")
    Call<List<PhotoDescriptionResponse>> getOceanPhotoDescriptions(@Path("login") String login);

    @GET("{login}/photo/description")
    Call<PagedDescriptionResponse> getPagedDescriptions(@Path("login") String login,
                                                        @Query("page") int page,
                                                        @Query("size") int size);

    @Multipart
    @POST("{login}/photo")
    Call<PhotoDescriptionResponse> addPhoto(@Path("login") String login,
                                            @Part MultipartBody.Part file);

    @PUT("{login}/photo/{photoId}/description")
    Call<PhotoDescriptionResponse> updateDescription(@Path("login") String login,
                                            @Path("photoId") String photoId,
                                            @Query("description") String description);
}
