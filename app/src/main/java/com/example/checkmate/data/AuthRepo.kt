package com.example.checkmate.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class AuthRepo {
    var user: FirebaseUser?=null

    fun updateUser(){
        user = Firebase.auth.currentUser
    }
}
