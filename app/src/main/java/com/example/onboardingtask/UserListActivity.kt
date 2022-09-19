package com.example.onboardingtask

import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onboardingtask.databinding.ActivityUserListBinding
import com.example.utils.gone
import com.example.utils.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class UserListActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var userList: List<UserModel>
    private lateinit var userDB: UserDB
    private lateinit var binding: ActivityUserListBinding
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backListenerUl.setOnClickListener(this)

        userDB = UserDB.getInstance(applicationContext)
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        userDB.userDao().getUsers().observe(this) {
            if (!it.isNullOrEmpty()) {
                userList = it
                adapter = UserAdapter(userList as ArrayList<UserModel>, this)
                binding.rvUser.adapter = adapter
                binding.noData.gone()
                binding.rvUser.visible()
                enableSwipeToDeleteAndUndo(it)
            } else {
                binding.noData.visible()
                binding.rvUser.gone()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_listener_ul -> finish()
        }
    }

    private fun enableSwipeToDeleteAndUndo(list: List<UserModel>) {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.getUser(position)
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        CoroutineScope(Dispatchers.Default).launch {
                            userDB.userDao()
                                .delete(adapter.getUser(viewHolder.layoutPosition))
                        }
                    }
                    ItemTouchHelper.RIGHT -> {
                        val intent = Intent(applicationContext, SignUpActivity::class.java)
                        CoroutineScope(Dispatchers.IO).launch {
                            intent.putExtra("true", true)
                            intent.putExtra("model", list[viewHolder.adapterPosition])
                            startActivity(intent)
                        }
                    }
                }
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(binding.rvUser)
    }
}
