package com.alexisoa.ulpgcar.domain.interactor.login

import android.util.Patterns
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.auth.AuthRepository
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.AuthCredential
import java.lang.Exception

class LoginInteractorImp(private val repository: AuthRepository) : LoginInteractor{
    override suspend fun signInUser(email: String, password: String): Resource<User> {
        if (email.isNotEmpty() && password.isNotEmpty() ){
            return repository.checkSignInUser(email, password)
        }
        throw Exception("Rellene todos los campos")
    }

    override suspend fun signInUserWithGoogle(authCredential: AuthCredential): Resource<User> = repository.checkSignInUserWithGoogle(authCredential)

    override suspend fun resetPass(email: String): Resource<Boolean> {
        if (email.isNotEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return  repository.checkResetPassword(email)
        }
        throw Exception("Ingrese un correo válido")
    }

    override suspend fun Logout(): Resource<Boolean> = repository.logOutUser()
}