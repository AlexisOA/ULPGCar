package com.alexisoa.ulpgcar.data.repository.profile

import android.net.Uri
import androidx.core.net.toFile
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class ProfileRepositoryImp:ProfileRepository {
    private var storage = FirebaseStorage.getInstance()
    private val fsAuth = FirebaseAuth.getInstance()
    private val fsDatabase = FirebaseFirestore.getInstance()
    override suspend fun saveImageRepo(uri: Uri): Resource<Boolean> {
        var storageRef = storage.reference
        if (uri != null){
            val uidRandom = UUID.randomUUID().toString()
            var ref:StorageReference = storageRef.child("ProfileImages/" + uidRandom)
            ref.putFile(uri).await()
            val refdownload = ref.downloadUrl.await()
            fsDatabase.collection("users").document(fsAuth.currentUser.uid).update("imageUrl", refdownload.toString()).await()
            prefs.saveUrlImage(refdownload.toString())
            return Resource.Success(true)
        }
        return Resource.Success(false)
    }

    override suspend fun getDataRepo(uid: String): Resource<User> {
        val userRef = fsDatabase.collection("users").document(uid).get().await()
        val user = userRef.toObject(User::class.java)!!
        return Resource.Success(user)
    }

    override suspend fun saveDescriptionRepo(description: String): Resource<Boolean> {
        fsDatabase.collection("users").document(fsAuth.currentUser.uid).update("description", description).await()
        return Resource.Success(true)
    }

    override suspend fun changePasswordRepo(email: String): Resource<Boolean> {
        fsAuth.sendPasswordResetEmail(email).await()
        return Resource.Success(true)
    }


}