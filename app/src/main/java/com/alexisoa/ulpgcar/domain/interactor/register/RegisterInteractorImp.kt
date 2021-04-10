package com.alexisoa.ulpgcar.domain.interactor.registerinteractor

import com.alexisoa.ulpgcar.data.repository.auth.AuthRepository
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User
import java.lang.Exception

class RegisterInteractorImp(private val repository: AuthRepository) : RegisterInteractor {
    override suspend fun signUpUser(email: String, password: String): Resource<User> {
        if (email.isNotEmpty() && password.isNotEmpty() ){
            return repository.checkSignUpUser(email, password)
        }
        throw Exception("Rellene todos los campos")
    }
}