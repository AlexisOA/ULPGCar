package com.alexisoa.ulpgcar.domain.interactor.register

import com.alexisoa.ulpgcar.data.repository.auth.AuthRepository
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User
import java.lang.Exception

class RegisterInteractorImp(private val repository: AuthRepository) : RegisterInteractor {
    override suspend fun signUpUser(email: String, password: String, confirmPassword: String, age: String, fullName:String, genre : String): Resource<Boolean> {
        if (password.equals(confirmPassword)){
            return repository.checkSignUpUser(email, password, age, fullName, genre)
        }
        throw Exception("La contraseñas no coinciden.")
    }
}