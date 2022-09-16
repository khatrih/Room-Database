package com.example.onboardingtask

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onboardingtask.databinding.ActivityHomeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private val accept = "application/json"
    private val contentType = "application/json"
    private val authorization =
        "Bearer 2d29e9b750eb0c822aa99f5bce491a2c18017a025b1dc0a01d86c6ec4015bee7"
    private lateinit var binding: ActivityHomeBinding
    lateinit var userList: ArrayList<ApiUserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userApi = RetrofitHelper.INSTANCE().create(UsersInterface::class.java)
        binding.rvApiUser.layoutManager = LinearLayoutManager(this)
        /*GlobalScope.launch {
            try {
                val result = userApi.getApiUsers(accept, contentType, authorization)
                //Log.d("TAG", "api calling: ${result.body().toString()}")
                if (result.isSuccessful) {
                    val adapter = UserApiAdapter(result as ArrayList<ApiUserModel>)
                    binding.rvApiUser.adapter = adapter
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        result.errorBody().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.e("Error", it) }
            }
        }*/

        userList = ArrayList()
        val Call: Call<ArrayList<ApiUserModel>?>? =
            userApi.getApiUsers(accept, contentType, authorization)

        Call!!.enqueue(object : Callback<ArrayList<ApiUserModel>?> {
            override fun onResponse(
                call: Call<ArrayList<ApiUserModel>?>,
                response: Response<ArrayList<ApiUserModel>?>
            ) {
                if (response.isSuccessful) {
                    userList = response.body()!!
                }
                val adapter = UserApiAdapter(userList)
                binding.rvApiUser.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<ApiUserModel>?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Fail to get the data..", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}