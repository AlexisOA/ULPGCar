package com.alexisoa.ulpgcar.domain.interactor.login

import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.AuthCredential


interface LoginInteractor {
    suspend fun signInUser(email: String, password: String): Resource<User>
    suspend fun signInUserWithGoogle(authCredential: AuthCredential): Resource<User>
    suspend fun resetPass(email: String): Resource<Boolean>
    suspend fun Logout(): Resource<Boolean>
}