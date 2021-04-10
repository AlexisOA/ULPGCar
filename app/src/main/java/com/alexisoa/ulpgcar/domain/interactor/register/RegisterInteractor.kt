package com.alexisoa.ulpgcar.domain.interactor.registerinteractor

import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User

interface RegisterInteractor {

    suspend fun signUpUser(email: String, password: String): Resource<User>
}