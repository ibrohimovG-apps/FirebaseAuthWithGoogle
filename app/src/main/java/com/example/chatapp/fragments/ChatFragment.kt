package com.example.chatapp.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.chatapp.adapters.MsgAdapter
import com.example.chatapp.databinding.FragmentChatBinding
import com.example.chatapp.models.Users
import com.example.chatapp.utils.MySharedPreferences
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ilmiddin1701.chatapp.models.MyMessage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class ChatFragment : Fragment(),MsgAdapter.RvAction {

    private val binding by lazy { FragmentChatBinding.inflate(layoutInflater) }

    //realtime database
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    //cloud storage
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var currentUserUID: String
    private lateinit var msgAdapter: MsgAdapter

    private var imgURL = ""
    private var uri: Uri? = null
    private lateinit var list: ArrayList<MyMessage>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().window.statusBarColor = Color.parseColor("#035C84")

        currentUserUID = arguments?.getString("currentUserUID").toString()
        val users = arguments?.getSerializable("keyUser") as Users

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Messages")

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReference("images")

        list = ArrayList()
        binding.title.text = users.name
        val toUserPhotoUrl = users.imgLink
        Picasso.get().load(toUserPhotoUrl).into(binding.userImage)
        msgAdapter = MsgAdapter(this, list, currentUserUID)
        binding.rv.adapter = msgAdapter

        binding.apply {
            btnBack.setOnClickListener { findNavController().popBackStack() }
            btnAttach.setOnClickListener { getImageContent.launch("image/*") }

            btnX.setOnClickListener {
                uri = null
                sendImageLayout.visibility = View.GONE
            }

            btnSend.setOnClickListener {
                val text = edtMessage.text.toString()
                if (uri != null && text.isNotBlank()) {

                    binding.btnX.isEnabled = false
                    binding.btnSend.isEnabled = false
                    binding.edtMessage.isEnabled = false
                    binding.progress.visibility = View.VISIBLE

                    val m = System.currentTimeMillis()
                    val task = storageReference.child(m.toString()).putFile(uri!!)
                    task.addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            val downloadURL = it.metadata?.reference?.downloadUrl
                            downloadURL?.addOnSuccessListener { imageURL ->
                                imgURL = imageURL.toString()
                                val message = MyMessage(text, users.uid, currentUserUID, imgURL, getDate())
                                function2(users, message)
                            }
                        }
                    }
                    task.addOnFailureListener {
                        Toast.makeText(context, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } else if (uri == null && text.isNotBlank()) {
                    val message = MyMessage(text, users.uid, currentUserUID, getDate())
                    function2(users, message)

                } else if (uri != null && text.isBlank()) {

                    binding.btnX.isEnabled = false
                    binding.btnSend.isEnabled = false
                    binding.edtMessage.isEnabled = false
                    binding.progress.visibility = View.VISIBLE

                    val m = System.currentTimeMillis()
                    val task = storageReference.child(m.toString()).putFile(uri!!)
                    task.addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            val downloadURL = it.metadata?.reference?.downloadUrl
                            downloadURL?.addOnSuccessListener { imageURL ->
                                imgURL = imageURL.toString()
                                val message = MyMessage("", users.uid, currentUserUID, imgURL, getDate())
                                function2(users, message)
                            }
                        }
                    }
                    task.addOnFailureListener {
                        Toast.makeText(context, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            MySharedPreferences.init(requireContext())
            databaseReference.child(currentUserUID).child("messages").child(users.uid ?: "")
                .addValueEventListener(object : ValueEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        list.clear()
                        val children = snapshot.children
                        for (child in children) {
                            val message = child.getValue(MyMessage::class.java)
                            if (message != null) {
                                list.add(message)
                            }
                        }
                        if (list.isNotEmpty() && users.uid !in MySharedPreferences.sharedList) {
                            val sharedList = MySharedPreferences.sharedList
                            sharedList.add(users.uid!!)
                            MySharedPreferences.sharedList = sharedList
                        }
                        msgAdapter.notifyDataSetChanged()
                        binding.rv.scrollToPosition(list.size - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    }
                })

        }

        return binding.root
    }

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        uri = it

        binding.sendImage.setImageURI(uri)
        binding.sendImageLayout.visibility = View.VISIBLE
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("HH:mm\ndd.MM.yyyy")
        return simpleDateFormat.format(date)
    }

    private fun function2(users: Users, message: MyMessage) {
        val key = databaseReference.push().key!!
        databaseReference.child(users.uid!!).child("messages").child(currentUserUID)
            .child(key).setValue(message)

        databaseReference.child(currentUserUID).child("messages").child(users.uid!!)
            .child(key).setValue(message)

        uri = null
        binding.btnX.isEnabled = true
        binding.btnSend.isEnabled = true
        binding.edtMessage.isEnabled = true
        binding.edtMessage.text.clear()
        binding.progress.visibility = View.GONE
        binding.sendImageLayout.visibility = View.GONE
    }

    override fun imageClick(message: MyMessage) {

//        findNavController().navigate(
//            R.id.imageViewFragment,
//            bundleOf("imageDetail" to message),
//            navOption.build()
//        )

    }
}