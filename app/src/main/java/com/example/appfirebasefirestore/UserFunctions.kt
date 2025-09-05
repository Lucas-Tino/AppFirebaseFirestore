package com.example.appfirebasefirestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserFunctions {
    var userList: MutableList<User> = mutableListOf<User>();


    val db = FirebaseFirestore.getInstance()

    fun addUser(user: HashMap<String, String>, uid: String) {
        db.collection("users").document(uid)
            .set(user)
    }

    fun getUser(uid: String) {
        /* db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                gottenUser = documentSnapshot.toObject(User::class.java)!!
            }
            .addOnFailureListener { e ->
            }
         */
    }

    fun getAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList = mutableListOf<User>()
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                }
            }
            .addOnFailureListener { e ->

            }
    }

    fun updateUser(uid: String, updates: Map<String, Any>) {
        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }

    fun deleteUser(uid: String) {
        db.collection("users").document(uid)
            .delete()
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }
}