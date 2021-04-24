package com.alexisoa.ulpgcar.data.repository.auth

import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User
import com.google.firebase.auth.AuthCredential

interface AuthRepository {
    suspend fun checkSignUpUser(email: String, password: String, age: String, fullName:String, genre : String): Resource<Boolean>
    suspend fun checkSignInUserWithGoogle(authCredential: AuthCredential): Resource<User>
    suspend fun checkSignInUser(email: String, password: String): Resource<User>
    suspend fun checkResetPassword(email: String): Resource<Boolean>
    suspend fun logOutUser(): Resource<Boolean>
}