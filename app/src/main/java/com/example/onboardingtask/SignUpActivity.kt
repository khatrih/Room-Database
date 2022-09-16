package com.example.onboardingtask

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.onboardingtask.databinding.ActivitySignUpBinding
import com.example.utils.getPathFromUri
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {
    private var imageUri: String? = null
    private val myCalendar: Calendar = Calendar.getInstance()
    private lateinit var userDB: UserDB
    private lateinit var binding: ActivitySignUpBinding
    private var isUpdate = false
    private lateinit var userModel: UserModel
    private val password_patterns: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{8,}" +  // at least 4 characters
                "$"
    )

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val date = OnDateSetListener { _, year, month, day ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateLabel()
            binding.userAge.text = calculateAge(day, month, year).toEditable()
        }
        binding.userAge.setOnClickListener {
            DatePickerDialog(
                this@SignUpActivity,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        binding.backListener.setOnClickListener {
            finish()
        }

        userDB = UserDB.getInstance(applicationContext)
        isUpdate = intent.getBooleanExtra("true", false)
        if (isUpdate) {
            binding.btnSignup.text = "update"
            userModel = intent.getSerializableExtra("model") as UserModel
            binding.txtUserFirstNameSignup.text = userModel.firstName!!.toEditable()
            binding.txtUserLastNameSignup.text = userModel.lastName!!.toEditable()
            binding.txtUserEmailSignup.text = userModel.userEmail!!.toEditable()
            binding.txtUserPasswordSignup.text = userModel.userPassword!!.toEditable()
            binding.txtUserMobileSignup.text = userModel.userMobile!!.toEditable()
            binding.userAge.text = userModel.age!!.toEditable()
            val bmp: Bitmap = BitmapFactory.decodeFile(userModel.image)
            imageUri = getImageUri(this, bmp)?.let { getPathFromUri(this, it) }
            binding.ivIconSu.setImageURI(getImageUri(this, bmp))
        }

        binding.btnSignup.setOnClickListener {
            val firstName = binding.txtUserFirstNameSignup.text.toString().trim()
            val lastName = binding.txtUserLastNameSignup.text.toString().trim()
            val email = binding.txtUserEmailSignup.text.toString().trim()
            val password = binding.txtUserPasswordSignup.text.toString().trim()
            val mobile = binding.txtUserMobileSignup.text.toString().trim()
            val age = binding.userAge.text.toString().trim()

            val reg =
                """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!-_?&])(?=S+$).{8,}""".toRegex()

            if (firstName == "") {
                binding.txtUserFirstNameSignup.error = "please enter valid name"
                binding.txtUserFirstNameSignup.requestFocus()
                return@setOnClickListener
            } else if (lastName == "") {
                binding.txtUserLastNameSignup.error = "please enter valid name"
                binding.txtUserLastNameSignup.requestFocus()
                return@setOnClickListener
            } else if ((email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                binding.txtUserEmailSignup.error = "please enter valid email"
                binding.txtUserEmailSignup.requestFocus()
                return@setOnClickListener
            } else if (mobile.isEmpty()) {
                if (Patterns.PHONE.matcher(mobile).matches()) {
                    binding.txtUserMobileSignup.error = "please enter valid mobile number"
                    binding.txtUserMobileSignup.requestFocus()
                    return@setOnClickListener
                }
                binding.txtUserMobileSignup.error = "please check your number"
                binding.txtUserMobileSignup.requestFocus()
                return@setOnClickListener
            } else if (password.isEmpty()) {
                if (!password_patterns.matcher(password).matches()) {
                    binding.txtUserPasswordSignup.error = "password is too weak"
                    binding.txtUserPasswordSignup.requestFocus()
                    return@setOnClickListener
                }
                binding.txtUserPasswordSignup.error = "please enter valid password"
                binding.txtUserPasswordSignup.requestFocus()
                return@setOnClickListener
            } else if (age.isNullOrEmpty()) {
                binding.userAge.error = "please enter valid age"
                binding.userAge.requestFocus()
                return@setOnClickListener
            } else if (age < 18.toString()) {
                binding.userAge.error = "Your not eligible for sign up"
                binding.userAge.requestFocus()
                return@setOnClickListener
            } else if (imageUri == null) {
                Toast.makeText(this, "please insert your image", Toast.LENGTH_SHORT).show()
                binding.ivIconSu.requestFocus()
                return@setOnClickListener
            }

            if (isUpdate) {
                binding.btnSignup.text = getString(R.string.update)
                CoroutineScope(Dispatchers.IO).launch {
                    userDB.userDao().updateUser(
                        UserModel(
                            userModel.uId,
                            binding.txtUserFirstNameSignup.text.toString().trim(),
                            binding.txtUserLastNameSignup.text.toString().trim(),
                            binding.txtUserEmailSignup.text.toString().trim(),
                            binding.txtUserPasswordSignup.text.toString().trim(),
                            binding.txtUserMobileSignup.text.toString().trim(),
                            binding.userAge.text.toString().trim(),
                            imageUri.toString()
                        )
                    )
                }
                Toast.makeText(this, "user updated", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    userDB.userDao().createUser(
                        UserModel(
                            null,
                            binding.txtUserFirstNameSignup.text.toString().trim(),
                            binding.txtUserLastNameSignup.text.toString().trim(),
                            binding.txtUserEmailSignup.text.toString().trim(),
                            binding.txtUserPasswordSignup.text.toString().trim(),
                            binding.txtUserMobileSignup.text.toString().trim(),
                            binding.userAge.text.toString().trim(),
                            imageUri.toString()
                        )
                    )
                }
                Toast.makeText(this, "successfully added ", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        binding.txtSignIn.setOnClickListener {
            finish()
        }
//        if (!binding.ivIconSu.setImageURI(imageUri))
        binding.openGallery.setOnClickListener {
            checkPermission(WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }
    }

    private fun calculateAge(day: Int, month: Int, year: Int): String {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.set(year, month, day);
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }

    private fun updateLabel() {
        val myFormat = "MM/dd/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.userAge.text = dateFormat.format(myCalendar.time).toEditable()!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 200) {
            if (data?.data != null) {
                imageUri = getPathFromUri(this, data.data!!)
                binding.ivIconSu.setImageURI(data.data)
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage, "Title", null
        )
        return Uri.parse(path)
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@SignUpActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this@SignUpActivity, arrayOf(permission), requestCode)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@SignUpActivity, "Camera Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@SignUpActivity, "Camera Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this@SignUpActivity, "Storage Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun calculateDate() {
        val getDate = binding.userAge.text.toString()

    }
}