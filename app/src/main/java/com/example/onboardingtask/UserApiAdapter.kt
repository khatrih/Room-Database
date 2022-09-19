package com.example.onboardingtask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onboardingtask.databinding.ItemApiUserBinding


class UserApiAdapter(private val mList: ArrayList<ApiUserModel>) :
    RecyclerView.Adapter<UserApiAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemApiUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemApiUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ApiUserModel = mList.get(position)
        holder.binding.tvUsersName.text = model.name
        holder.binding.tvUsersEmail.text = model.email
        holder.binding.tvUsersGender.text = model.gender
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}