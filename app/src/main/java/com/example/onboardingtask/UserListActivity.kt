package com.example.onboardingtask

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onboardingtask.databinding.ActivityUserListBinding
import com.example.utils.gone


class UserListActivity : AppCompatActivity() {
    private lateinit var userDB: UserDB
    private lateinit var binding: ActivityUserListBinding
    var coordinatorLayout: CoordinatorLayout? = null
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backListenerUl.setOnClickListener {
            finish()
        }

        userDB = UserDB.getInstance(applicationContext)
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        userDB.userDao().getUsers().observe(this) {
            if (!it.isNullOrEmpty()) {
                val userList = it
                adapter = UserAdapter(userList as ArrayList<UserModel>, this)
                binding.rvUser.adapter = adapter
                binding.noData.visibility = View.GONE
                binding.rvUser.visibility = View.VISIBLE

                /*val swipeGestures = object : SwipeGestures(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
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
                                    intent.putExtra("model", it[viewHolder.adapterPosition])
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }*/


                /*val touchListener = ItemTouchHelper(swipeGestures)
                touchListener.attachToRecyclerView(binding.rvUser)*/

                enableSwipeToDeleteAndUndo()
            } else {
                binding.noData.visibility = View.VISIBLE
                binding.rvUser.gone()
            }
        }
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                //val item: String = adapter.getData().get(position)
                adapter.getUser(position)
//                val snackbar: Snackbar = Snackbar
//                    .make(
//                        coordinatorLayout!!,
//                        "Item was removed from the list.",
//                        Snackbar.LENGTH_LONG
//                    )
//                snackbar.setAction("UNDO", View.OnClickListener {
//                    adapter.restoreItem(item, position)
//                    recyclerView.scrollToPosition(position)
//                })
//                snackbar.setActionTextColor(Color.YELLOW)
//                snackbar.show()
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(binding.rvUser)
    }

}