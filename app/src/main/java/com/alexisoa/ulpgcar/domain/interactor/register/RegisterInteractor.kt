package com.alexisoa.ulpgcar.domain.interactor.register

import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User

interface RegisterInteractor {

    suspend fun signUpUser(email: String, password: String, confirmPassword: String, age: String, fullName:String, genre : String): Resource<Boolean>
}