package com.example.appfirebasefirestore

import com.example.appfirebasefirestore.User
import com.google.firebase.firestore.FirebaseFirestore

class UserFunctions {
    val db = FirebaseFirestore.getInstance()

    fun addUser(user: User) {
        db.collection("users") // "users" is the collection name
            .document(user.uid) // Use UID as document ID for easy retrieval
            .set(user)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->

            }
    }

    fun getUser(uid: String) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)

                } else {

                }
            }
            .addOnFailureListener { e ->

            }
    }

    fun getAllUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<User>()
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