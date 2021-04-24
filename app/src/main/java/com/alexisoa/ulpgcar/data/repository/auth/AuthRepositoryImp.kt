package com.alexisoa.ulpgcar.data.repository.auth

import com.alexisoa.ulpgcar.valueobject.ProviderType
import com.alexisoa.ulpgcar.valueobject.Resource
import com.example.ulpgcarprototype.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImp: AuthRepository {
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()

    override suspend fun checkSignUpUser(email: String, password: String, age: String,
                                         fullName:String, genre : String): Resource<Boolean> {

        val documents = fsDatabase.collection("users").whereEqualTo("email", email).get().await()

        if(documents.size() > 0){
            throw Exception("Este E-mail ya está registrado.")
        }

        fsAuth.createUserWithEmailAndPassword(email, password).await()
        val u = fsAuth.currentUser ?: throw Exception("An error has ocurred.")

        u.sendEmailVerification().await()

        val tokenUser = u.uid
        fsDatabase.collection("users").document(tokenUser).set(hashMapOf( "fullName" to fullName,
                                                                        "email" to email,
                                                                        "age" to age,
                                                                        "genre" to genre))
        return Resource.Success(true)
    }

    override suspend fun checkSignInUserWithGoogle(authCredential: AuthCredential): Resource<User> {
        lateinit var user: User
        fsAuth.signInWithCredential(authCredential).await()

        val firebaseUser = fsAuth.currentUser
        if (firebaseUser != null){
            val documents = fsDatabase.collection("users").whereEqualTo("email", firebaseUser.email).get().await()

            if (documents.size() > 0)
                throw Exception("Este E-mail ya se ha registrado mediante otro proveedor.")

            val collectionUser = fsDatabase.collection("users").document(firebaseUser.uid)
            val doc = collectionUser.get().await()

            if(!doc.exists()){
                collectionUser.set(hashMapOf("name" to  firebaseUser.displayName,
                                            "email" to firebaseUser.email))
            }

            val name = firebaseUser.displayName
            val email: String? = firebaseUser.email
            user = User(email.toString(), name)
        }
        return Resource.Success(user)
    }

    override suspend fun checkSignInUser(email: String, password: String): Resource<User> {
        fsAuth.signInWithEmailAndPassword(email, password).await()
        val u = fsAuth.currentUser
        if(u != null && u.isEmailVerified){
            return Resource.Success(User(email, ProviderType.BASIC.toString()))
        }else{
            throw java.lang.Exception("El E-mail necesita ser verificado")
        }

    }

    override suspend fun checkResetPassword(email: String): Resource<Boolean> {
        fsAuth.sendPasswordResetEmail(email).await()
        return Resource.Success(true)
    }

    override suspend fun logOutUser(): Resource<Boolean> {
        fsAuth.signOut()
        return Resource.Success(true)
    }


}