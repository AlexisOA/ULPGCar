package com.alexisoa.ulpgcar.data.repository.auth

import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.valueobject.ProviderType
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.util.*

class AuthRepositoryImp: AuthRepository {
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()

    override suspend fun checkSignUpUser(email: String, password: String,
                                         fullName:String): Resource<Boolean> {

        val documents = fsDatabase.collection("users").whereEqualTo("email", email).get().await()

        if(documents.size() > 0){
            throw Exception("Este E-mail ya está registrado en el sistema.")
        }

        fsAuth.createUserWithEmailAndPassword(email, password).await()
        val u = fsAuth.currentUser ?: throw Exception("An error has ocurred.")

        u.sendEmailVerification().await()

        val tokenUser = u.uid
        val appToken = FirebaseMessaging.getInstance().token.await()
        val urlImageDefault = "https://firebasestorage.googleapis.com/v0/b/ulpgcar.appspot.com/o/ProfileImages%2FprofileDefault.png?alt=media&token=89155ebe-7595-4a99-95bf-958115e33313"
        fsDatabase.collection("users").document(tokenUser).set(hashMapOf( "fullName" to fullName,
                                                                        "email" to email,
                                                                        "uid" to tokenUser,
                                                                        "imageUrl" to urlImageDefault,
                                                                        "provider" to "Email",
                                                                        "dateRegister" to Calendar.getInstance().timeInMillis,
                                                                        "appToken" to appToken,
                                                                        "description" to "Mi descripción"
                                                                        ))
        return Resource.Success(true)
    }

    override suspend fun checkSignInUserWithGoogle(authCredential: AuthCredential): Resource<User> {
        fsAuth.signInWithCredential(authCredential).await()

        val firebaseUser = fsAuth.currentUser
        if (firebaseUser != null){
            val documents = fsDatabase.collection("users").whereEqualTo("email", firebaseUser.email).get().await()

            if (documents.size() > 0){
                for (document in documents){
                    if (document.get("provider")!!.equals("Email")) throw Exception("Este E-mail ya se ha registrado mediante otro proveedor.")
                }
            }
                //throw Exception("Este E-mail ya se ha registrado mediante otro proveedor.")

            val refUser = fsDatabase.collection("users").document(firebaseUser.uid).get().await()
            //val refUser = collectionUser.get().await()

            if(!refUser.exists()){
                val appToken = FirebaseMessaging.getInstance().token.await()
                fsDatabase.collection("users").document(firebaseUser.uid).set(hashMapOf(
                    "fullName" to  firebaseUser.displayName,
                    "email" to firebaseUser.email,
                    "uid" to firebaseUser.uid,
                    "imageUrl" to firebaseUser.photoUrl.toString(),
                    "provider" to "Google",
                    "dateRegister" to Calendar.getInstance().timeInMillis,
                    "appToken" to appToken,
                    "description" to "Mi descripción"
                ))
            }
            val userRef = fsDatabase.collection("users").document(firebaseUser.uid).get().await()
            val user = userRef.toObject(User::class.java)!!
            return Resource.Success(user)
        }else{
            throw java.lang.Exception("Error al verificar su cuenta de google, vuelva a intentarlo más tarde.")
        }

    }

    override suspend fun checkSignInUser(email: String, password: String): Resource<User> {
        fsAuth.signInWithEmailAndPassword(email, password).await()
        val u = fsAuth.currentUser
        if(u != null && u.isEmailVerified){
            val refUser = fsDatabase.collection("users").document(u.uid).get().await()
            val user = refUser.toObject(User::class.java)!!
            return Resource.Success(user)
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