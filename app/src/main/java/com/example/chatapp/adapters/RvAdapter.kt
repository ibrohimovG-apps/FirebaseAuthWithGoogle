package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemRvBinding
import com.example.chatapp.models.Users
import com.squareup.picasso.Picasso

class RvAdapter(val list: ArrayList<Users>) : RecyclerView.Adapter<RvAdapter.Vh>() {

    inner class Vh(private val itemRvBinding: ItemRvBinding) : RecyclerView.ViewHolder(itemRvBinding.root) {
        fun onBind(users: Users) {
            Picasso.get().load(users.imgLink).into(itemRvBinding.imgUser)
            itemRvBinding.tvName.text = users.name
            itemRvBinding.tvEmail.text = users.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }
}