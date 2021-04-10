package com.alexisoa.ulpgcar.data.repository.auth

import com.alexisoa.ulpgcar.valueobject.ProviderType
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImp:
    AuthRepository {

    override suspend fun checkSignUpUser(email: String, password: String): Resource<User> {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
        val user = User(email, ProviderType.BASIC.toString())
        return Resource.Success(user)
    }

    override suspend fun checkSignInUser(email: String, password: String): Resource<User> {
        println("Estoy en el firebaseRepo")
        print(email)
        print(" ")
        print(password)
        println("adios")
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        val user = User(email, ProviderType.BASIC.toString())
        return Resource.Success(user)
    }

    override suspend fun logOutUser(): Resource<Boolean> {
        FirebaseAuth.getInstance().signOut()
        return Resource.Success(true)
    }


}