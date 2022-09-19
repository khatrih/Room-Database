package com.example.onboardingtask

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.onboardingtask.databinding.ActivitySignUpBinding
import com.example.utils.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : BaseActivity(), View.OnClickListener {
    private var imageUri: String? = null
    private val myCalendar: Calendar = Calendar.getInstance()
    private lateinit var userDB: UserDB
    private lateinit var binding: ActivitySignUpBinding
    private var isUpdate = false
    private lateinit var userModel: UserModel
    private val password_patterns: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{4,}" +                // at least 4 characters
                "$"
    )

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userAge.setOnClickListener(this)

        binding.backListener.setOnClickListener(this)

        userDB = UserDB.getInstance(applicationContext)
        isUpdate = intent.getBooleanExtra("true", false)
        if (isUpdate) {
            binding.btnSignup.text = getString(R.string.update)
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

        binding.btnSignup.setOnClickListener(this)

        binding.txtSignIn.setOnClickListener(this)

        binding.openGallery.setOnClickListener(this)

        if (imageUri != null) {
            binding.removeGallery.visible()
            binding.openGallery.gone()
        }
    }

    override fun onClick(v: View?) {
        val i: Int? = v?.id
        when (i) {
            R.id.btn_signup -> addUpdateInfo()
            R.id.txt_sign_in -> finish()
            R.id.open_gallery -> {
                checkPermission(WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)

            }
            R.id.back_listener -> finish()
            R.id.user_age -> {
                DatePickerDialog(
                    this@SignUpActivity,
                    date(),
                    myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }
    }

    private fun setModel(): UserModel {
        val id: Int?
        if (isUpdate) id = userModel.uId else id = null
        return UserModel(
            id,
            binding.txtUserFirstNameSignup.text.toString().trim(),
            binding.txtUserLastNameSignup.text.toString().trim(),
            binding.txtUserEmailSignup.text.toString().trim(),
            binding.txtUserPasswordSignup.text.toString().trim(),
            binding.txtUserMobileSignup.text.toString().trim(),
            binding.userAge.text.toString().trim(),
            imageUri.toString()
        )
    }

    private fun date(): OnDateSetListener {
        val date = OnDateSetListener { _, year, month, day ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateLabel()
            binding.userAge.text = calculateAge(day, month, year).toEditable()
        }
        return date
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

    private fun addUpdateInfo() {
        val firstName = binding.txtUserFirstNameSignup.text.toString().trim()
        val lastName = binding.txtUserLastNameSignup.text.toString().trim()
        val email = binding.txtUserEmailSignup.text.toString().trim()
        val password = binding.txtUserPasswordSignup.text.toString().trim()
        val mobile = binding.txtUserMobileSignup.text.toString().trim()
        val age = binding.userAge.text.toString().trim()

        if (firstName.isEmpty()) {
            binding.txtUserFirstNameSignup.error = getString(R.string.edittext_empty_check)
            binding.txtUserFirstNameSignup.requestFocus()
            return
        } else if (lastName.isEmpty()) {
            binding.txtUserLastNameSignup.error = getString(R.string.edittext_empty_check)
            binding.txtUserLastNameSignup.requestFocus()
            return
        } else if ((email.isEmpty())) {
            binding.txtUserEmailSignup.error = getString(R.string.edittext_empty_check)
            binding.txtUserEmailSignup.requestFocus()
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtUserEmailSignup.error = getString(R.string.please_enter_valid_name)
            binding.txtUserEmailSignup.requestFocus()
            return
        } else if (mobile.isEmpty()) {
            binding.txtUserMobileSignup.error = getString(R.string.edittext_empty_check)
            binding.txtUserMobileSignup.requestFocus()
            return
        }/* else if (Patterns.PHONE.matcher(mobile).matches()) {
            binding.txtUserMobileSignup.error = getString(R.string.valid_pnohe_number)
            binding.txtUserMobileSignup.requestFocus()
            return
        }*/ else if (password.isEmpty()) {
            binding.txtUserPasswordSignup.error = getString(R.string.edittext_empty_check)
            binding.txtUserPasswordSignup.requestFocus()
            return
        } else if (!password_patterns.matcher(password).matches()) {
            binding.txtUserPasswordSignup.error = getString(R.string.weak_password)
            binding.txtUserPasswordSignup.error = getString(R.string.password_structure)
            binding.txtUserPasswordSignup.requestFocus()
            return
        } else if (age.isEmpty()) {
            binding.userAge.error = getString(R.string.edittext_empty_check)
            binding.userAge.requestFocus()
            return
        } else if (age < 18.toString()) {
            binding.userAge.error = getString(R.string.edittext_empty_check)
            binding.userAge.requestFocus()
            return
        } else if (imageUri == null) {
            Toast.makeText(this, getString(R.string.edittext_empty_check), Toast.LENGTH_SHORT)
                .show()
            binding.ivIconSu.requestFocus()
            return
        }

        if (isUpdate) {
            binding.btnSignup.text = getString(R.string.update)
            CoroutineScope(Dispatchers.IO).launch {
                userDB.userDao().updateUser(setModel())
            }
            showToast(this, "user updated")
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                userDB.userDao().createUser(setModel())
            }
            showToast(this, "successfully added ")
        }
        finish()
    }

}

/*private fun checkPermission(permission: String, requestCode: Int) {
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
            showToast(this, "Gallery Permission Granted")
        } else {
            showToast(this, "Gallery Permission Denied")
        }
    } else if (requestCode == STORAGE_PERMISSION_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showToast(this, "Storage Permission Granted")
        } else {
            showToast(this, "Storage Permission Denied")
        }
    }
}*/
//add
/*UserModel(
                            null,
                            binding.txtUserFirstNameSignup.text.toString().trim(),
                            binding.txtUserLastNameSignup.text.toString().trim(),
                            binding.txtUserEmailSignup.text.toString().trim(),
                            binding.txtUserPasswordSignup.text.toString().trim(),
                            binding.txtUserMobileSignup.text.toString().trim(),
                            binding.userAge.text.toString().trim(),
                            imageUri.toString()
                        )*/
//update
/*UserModel(
                            userModel.uId,
                            binding.txtUserFirstNameSignup.text.toString().trim(),
                            binding.txtUserLastNameSignup.text.toString().trim(),
                            binding.txtUserEmailSignup.text.toString().trim(),
                            binding.txtUserPasswordSignup.text.toString().trim(),
                            binding.txtUserMobileSignup.text.toString().trim(),
                            binding.userAge.text.toString().trim(),
                            imageUri.toString()
                        )*/