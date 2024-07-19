package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemFromBinding
import com.example.chatapp.databinding.ItemToBinding
import com.ilmiddin1701.chatapp.models.MyMessage
import com.squareup.picasso.Picasso

class MsgAdapter(var rvAction: RvAction, val list: ArrayList<MyMessage>, val currentUserUid: String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_FROM = 1
    val TYPE_TO = 0

    inner class ToVh(var itemToBinding: ItemToBinding): RecyclerView.ViewHolder(itemToBinding.root){
        fun onBind(message: MyMessage){
            itemToBinding.tvDate.text = message.date
            var isClick = false
            if (message.image != null && message.text != "") {
                itemToBinding.card.visibility = View.VISIBLE
                itemToBinding.tvSms.visibility = View.VISIBLE
                itemToBinding.tvSms.text = message.text
                Picasso.get().load(message.image).into(itemToBinding.image)
            }else if (message.image != null && message.text!!.isBlank()){
                itemToBinding.card.visibility = View.VISIBLE
                itemToBinding.tvSms.visibility = View.GONE
                Picasso.get().load(message.image).into(itemToBinding.image)
            } else if (message.image == null && message.text != ""){
                itemToBinding.card.visibility = View.GONE
                itemToBinding.tvSms.visibility = View.VISIBLE
                itemToBinding.tvSms.text = message.text
                Picasso.get().load(message.image).into(itemToBinding.image)
            }
//            itemToBinding.root.setOnClickListener {
//                if (!isClick){
//                    isClick = true
//                    itemToBinding.tvDate.visibility = View.VISIBLE
//                }else{
//                    isClick = false
//                    itemToBinding.tvDate.visibility = View.GONE
//                }
//            }
            itemToBinding.image.setOnClickListener {
                rvAction.imageClick(message)
            }
        }
    }

    inner class FromVh(var itemFromBinding: ItemFromBinding): RecyclerView.ViewHolder(itemFromBinding.root){
        fun onBind(message: MyMessage){
            itemFromBinding.tvDate.text = message.date
            var isClick = false
            if (message.image != null && message.text != "") {
                itemFromBinding.card.visibility = View.VISIBLE
                itemFromBinding.tvSms.visibility = View.VISIBLE
                itemFromBinding.tvSms.text = message.text
                Picasso.get().load(message.image).into(itemFromBinding.image)
            }else if (message.image != null && message.text!!.isBlank()){
                itemFromBinding.card.visibility = View.VISIBLE
                itemFromBinding.tvSms.visibility = View.GONE
                Picasso.get().load(message.image).into(itemFromBinding.image)
            } else if (message.image == null && message.text != ""){
                itemFromBinding.card.visibility = View.GONE
                itemFromBinding.tvSms.visibility = View.VISIBLE
                itemFromBinding.tvSms.text = message.text
                Picasso.get().load(message.image).into(itemFromBinding.image)
            }
//            itemFromBinding.root.setOnClickListener {
//                if (!isClick){
//                    isClick = true
//                    itemFromBinding.tvDate.visibility = View.VISIBLE
//                }else{
//                    isClick = false
//                    itemFromBinding.tvDate.visibility = View.GONE
//                }
//            }
            itemFromBinding.image.setOnClickListener {
                rvAction.imageClick(message)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType==0){
            ToVh(ItemToBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            FromVh(ItemFromBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ToVh){
            holder.onBind(list[position])
        }else if (holder is FromVh){
            holder.onBind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].fromUID == currentUserUid) {
            TYPE_FROM
        } else {
            TYPE_TO
        }
    }

    interface RvAction {
        fun imageClick(message: MyMessage)
    }
}