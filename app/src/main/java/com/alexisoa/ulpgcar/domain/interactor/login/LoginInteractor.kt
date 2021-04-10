package com.example.loginprototype.domain.interactor.logininteractor

import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User


interface LoginInteractor {

    suspend fun signInUser(email: String, password: String): Resource<User>
    suspend fun Logout(): Resource<Boolean>


}