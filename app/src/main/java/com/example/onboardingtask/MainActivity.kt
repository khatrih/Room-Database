package com.example.onboardingtask

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.onboardingtask.databinding.ActivityMainBinding
import com.example.utils.showToast
import com.google.gson.Gson
import java.util.regex.Pattern

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userDB: UserDB
    private val password_patterns: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{4,}" +                // at least 4 characters
                "$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDB = UserDB.getInstance(applicationContext)

        binding.btnLogin.setOnClickListener(this)

        binding.txtSignUp.setOnClickListener(this)

        binding.ivUserList.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> userLogin()
            R.id.txt_sign_up -> startActivity(Intent(this, SignUpActivity::class.java))
            R.id.iv_user_list -> startActivity(Intent(this, UserListActivity::class.java))
        }
    }

    private fun userLogin() {
        val email = binding.txtUserEmailLogin.text.toString().trim()
        val password = binding.txtUserPasswordLogin.text.toString().trim()

        if (email.isEmpty()) {
            binding.txtUserEmailLogin.error = getString(R.string.edittext_empty_check)
            binding.txtUserEmailLogin.requestFocus()
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtUserEmailLogin.error = getString(R.string.please_enter_valid_name)
            binding.txtUserEmailLogin.requestFocus()
            return
        } else if (password.isEmpty()) {
            binding.txtUserPasswordLogin.error = getString(R.string.edittext_empty_check)
            binding.txtUserPasswordLogin.requestFocus()
            return
        } else if (!password_patterns.matcher(password).matches()) {
            binding.txtUserPasswordLogin.error = getString(R.string.weak_password)
            binding.txtUserPasswordLogin.error = getString(R.string.password_structure)
            binding.txtUserPasswordLogin.requestFocus()
            return
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
                                    .observe(this) {
                                        val preferences =
                                            getSharedPreferences("user_list", MODE_PRIVATE)
                                        val prefEditor: SharedPreferences.Editor? =
                                            preferences.edit()
                                        val gson = Gson()
                                        prefEditor?.putString("MyObject", gson.toJson(it))
                                        prefEditor?.putBoolean("checkScreen", true)
                                        prefEditor?.apply()
                                    }
                                showToast(this, "You have successfully login")
                                startActivity(Intent(this, HomeActivity::class.java))
                            } else {
                                showToast(this, "wrong credentials please check again")
                            }
                        }
                    } else {
                        showToast(this, "Given details not registered")
                    }
                }
        }
    }
}