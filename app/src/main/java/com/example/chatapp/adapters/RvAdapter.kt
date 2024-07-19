package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemAccountsBinding
import com.example.chatapp.models.Users
import com.squareup.picasso.Picasso

class RvAdapter(val list: ArrayList<Users>, val rvAction: RvAction) : RecyclerView.Adapter<RvAdapter.Vh>() {

    inner class Vh(private val itemAccountsBinding: ItemAccountsBinding) : RecyclerView.ViewHolder(itemAccountsBinding.root) {
        fun onBind(users: Users) {
            Picasso.get().load(users.imgLink).into(itemAccountsBinding.imgUser)
            itemAccountsBinding.tvName.text = users.name
            itemAccountsBinding.tvEmail.text = users.email
            itemAccountsBinding.root.setOnClickListener {
                rvAction.onClick(users)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemAccountsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    interface RvAction{
        fun onClick(users: Users)
    }

}