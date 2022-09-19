package com.example.onboardingtask

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.GET

interface UsersInterface {
    @GET("v2/users")
    fun getApiUsers(
        @Header("Accept") Accept: String?,
        @Header("Content-Type") Content_Type: String?,
        @Header("Authorization") Authorization: String?
    ): Call<ArrayList<ApiUserModel>?>?
}