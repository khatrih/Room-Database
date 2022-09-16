package com.example.onboardingtask

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.onboardingtask.databinding.ActivityMainBinding
import com.example.utils.showToast
import com.google.gson.Gson
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userDB: UserDB
    private var preferences: SharedPreferences? = null

    //    private lateinit var prefEditor: SharedPreferences.Editor
    private val password_patterns: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{8,}" +  // at least 4 characters
                "$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDB = UserDB.getInstance(applicationContext)

        binding.btnLogin.setOnClickListener {
            val email = binding.txtUserEmailLogin.text.toString().trim()
            val password = binding.txtUserPasswordLogin.text.toString().trim()
            val reg =
                """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!-_?&])(?=S+$).{8,}""".toRegex()

            if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.txtUserEmailLogin.error = getString(R.string.please_enter_valid_name)
                binding.txtUserEmailLogin.requestFocus()
                return@setOnClickListener
            } else if (password.isEmpty()) {
                if (!password_patterns.matcher(password).matches()) {
                    binding.txtUserPasswordLogin.error = "password is too weak"
                    binding.txtUserPasswordLogin.requestFocus()
                    return@setOnClickListener
                }
                binding.txtUserPasswordLogin.error = "please enter valid password"
                binding.txtUserPasswordLogin.requestFocus()
                return@setOnClickListener
            } else {
                userDB.userDao().validateName(binding.txtUserEmailLogin.text.toString().trim())
                    .observe(this) {
                        if (it != null && it.userEmail.equals(
                                binding.txtUserEmailLogin.text.toString().trim()
                            )
                        ) {
                            userDB.userDao().existingUserLogin(
                                binding.txtUserEmailLogin.text.toString().trim(),
                                binding.txtUserPasswordLogin.text.toString().trim()
                            ).observe(this) {
                                if (it != null && it.userPassword.equals(
                                        binding.txtUserPasswordLogin.text.toString().trim()
                                    )
                                ) {
                                    userDB.userDao()
                                        .getCurrentUser(binding.txtUserEmailLogin.text.toString())
                                        .observe(this, Observer {
                                            preferences =
                                                getSharedPreferences("user_list", MODE_PRIVATE)
                                            val prefEditor: SharedPreferences.Editor? =
                                                preferences?.edit()
                                            prefEditor?.putString(
                                                "users",
                                                it.toString()
                                            )
                                            prefEditor?.putString(
                                                "email",
                                                binding.txtUserEmailLogin.text.toString().trim()
                                            )
                                            prefEditor?.putString(
                                                "password",
                                                binding.txtUserPasswordLogin.text.toString().trim()
                                            )
                                            prefEditor?.apply()
                                        })

                                    showToast(this, "You have successfully login")
                                    startActivity(Intent(this, HomeActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        this,
                                        "wrong credentials please check again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Given details not registered", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.txtUserList.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val file = preferences?.getString("user_list", "")
        if (file != null) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
        }
    }
}