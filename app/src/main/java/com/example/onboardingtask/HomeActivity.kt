package com.example.onboardingtask

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onboardingtask.databinding.ActivityHomeBinding
import com.example.utils.showToast
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private val accept = "application/json"
    private val contentType = "application/json"
    private val authorization =
        "Bearer 2d29e9b750eb0c822aa99f5bce491a2c18017a025b1dc0a01d86c6ec4015bee7"
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userList: ArrayList<ApiUserModel>
    private lateinit var preferences: SharedPreferences

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
                t.printStackTrace()
                showToast(this@HomeActivity, "Fail to get the data..")
            }
        })
        preferences = getSharedPreferences("user_list", MODE_PRIVATE)
        val json: String = preferences.getString("MyObject", "").toString()
        val obj = Gson().fromJson(json, UserModel::class.java)
        binding.email.text = obj.userEmail
        "${obj.firstName} ${obj.lastName}".also { binding.name.text = it }
        binding.mobile.text = obj.userMobile
        binding.ivImage.setImageURI(Uri.parse(obj.image))

        binding.logoutListener.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.logout_listener -> {
                showToast(applicationContext, "logout")
                val editor = preferences.edit()
                editor.clear().apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

}