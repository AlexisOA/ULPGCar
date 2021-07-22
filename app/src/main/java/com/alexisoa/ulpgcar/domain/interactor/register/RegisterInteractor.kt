package com.alexisoa.ulpgcar.domain.interactor.register

import com.alexisoa.ulpgcar.valueobject.Resource

interface RegisterInteractor {

    suspend fun signUpUser(email: String, password: String, confirmPassword: String, fullName:String): Resource<Boolean>
}