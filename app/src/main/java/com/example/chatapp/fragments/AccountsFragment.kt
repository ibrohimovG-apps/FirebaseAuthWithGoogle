package com.example.chatapp.fragments

import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.adapters.RvAdapter
import com.example.chatapp.databinding.FragmentAccountsBinding
import com.example.chatapp.models.Users
import com.example.chatapp.utils.MySharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

private const val TAG = "AccountsFragment"
class AccountsFragment : Fragment(),RvAdapter.RvAction {

    private val binding by lazy { FragmentAccountsBinding.inflate(layoutInflater) }


    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var list : ArrayList<Users>

    private lateinit var firebaseAuth: FirebaseAuth


    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseAuth = FirebaseAuth.getInstance()

        registerForContextMenu(binding.imgAdmin)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        Picasso.get().load(firebaseAuth.currentUser?.photoUrl).into(binding.imgAdmin)
        binding.tvAdmin.text = firebaseAuth.currentUser?.displayName

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Accounts")

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
                binding.rv.adapter = RvAdapter(list, this@AccountsFragment)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.my_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logOut -> {
                googleSignInClient.signOut()
                firebaseAuth.signOut()
                MySharedPreferences.init(requireContext())
                val sharedList = MySharedPreferences.sharedList
                sharedList.clear()
                MySharedPreferences.sharedList = sharedList
                findNavController().popBackStack()
                findNavController().navigate(R.id.authFragment)
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onClick(users: Users) {
        findNavController().navigate(
            R.id.chatFragment,
            bundleOf("keyUser" to users, "currentUserUID" to firebaseAuth.uid.toString(), "currentUserPhotoUrl" to firebaseAuth.currentUser?.photoUrl.toString())
        )
    }
}