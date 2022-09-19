package com.example.onboardingtask

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onboardingtask.databinding.ItemUserBinding

class UserAdapter(private val userList: ArrayList<UserModel>, var context: Context) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userModel = userList[position]
        "${userModel.firstName} ${userModel.lastName}".also { holder.binding.name.text = it }
        holder.binding.email.text = userModel.userEmail
        holder.binding.mobile.text = userModel.userMobile
        "${userModel.age} y/o".also { holder.binding.age.text = it }

        if (userModel.image.isNotEmpty()) {
            val bmp: Bitmap = BitmapFactory.decodeFile(userModel.image)
            holder.binding.ivUserImg.setImageBitmap(bmp)
        }
        //holder.binding.ivUserImg.setImageResource(R.drawable.ic_launcher_foreground)
        //val uri: Uri = Uri.parse(userModel.image)
        /*Picasso.get().load(userModel.image).placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.binding.ivUserImg)*/
    }

    fun getUser(position: Int): UserModel = userList.get(position)

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

}