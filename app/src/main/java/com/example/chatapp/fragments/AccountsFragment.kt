package com.example.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.adapters.RvAdapter
import com.example.chatapp.databinding.FragmentAccountsBinding
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

private const val TAG = "AccountsFragment"
class AccountsFragment : Fragment() {

    private val binding by lazy { FragmentAccountsBinding.inflate(layoutInflater) }


    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var list : ArrayList<Users>

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseAuth = FirebaseAuth.getInstance()

        Picasso.get().load(firebaseAuth.currentUser?.photoUrl).into(binding.imgAdmin)
        binding.tvAdmin.text = firebaseAuth.currentUser?.displayName

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Users")

        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()
                val children = snapshot.children
                for (child in children) {
                    val users = child.getValue(Users::class.java)
                    if (users?.uid != firebaseAuth.uid) {
                        list.add(users!!)
                    }
                }
                binding.rv.adapter = RvAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }
}