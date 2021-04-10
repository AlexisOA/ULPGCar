package com.alexisoa.ulpgcar.data.repository.auth

import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User

interface AuthRepository {
    suspend fun checkSignUpUser(email: String, password: String): Resource<User>
    suspend fun checkSignInUser(email: String, password: String): Resource<User>
    suspend fun logOutUser(): Resource<Boolean>


}