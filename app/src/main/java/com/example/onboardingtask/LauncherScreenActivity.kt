package com.example.onboardingtask

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.onboardingtask.databinding.ActivityLauncherScreenBinding

class LauncherScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences("user_list", MODE_PRIVATE)
        //val file = preferences.getString("MyObject", null)
        Handler(Looper.getMainLooper()).postDelayed({
            if (preferences.getBoolean("checkScreen",false)) {
                startActivity(Intent(applicationContext, HomeActivity::class.java))
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 2000)


    }
}

