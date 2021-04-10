package com.example.loginprototype.domain.interactor.logininteractor

import com.alexisoa.ulpgcar.data.repository.auth.AuthRepository
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User
import java.lang.Exception

class LoginInteractorImp(private val repository: AuthRepository) : LoginInteractor{
    override suspend fun signInUser(email: String, password: String): Resource<User> {
        if (email.isNotEmpty() && password.isNotEmpty() ){
            return repository.checkSignInUser(email, password)
        }
        throw Exception("Rellene todos los campos")
    }

    override suspend fun Logout(): Resource<Boolean> = repository.logOutUser()


}