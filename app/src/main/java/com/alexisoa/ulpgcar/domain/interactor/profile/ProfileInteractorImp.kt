package com.alexisoa.ulpgcar.domain.interactor.profile

import android.net.Uri
import com.alexisoa.ulpgcar.data.model.User
import com.alexisoa.ulpgcar.data.repository.profile.ProfileRepository
import com.alexisoa.ulpgcar.valueobject.Resource

class ProfileInteractorImp(private val repository: ProfileRepository):ProfileInteractor {
    override suspend fun uploadImageInteractor(uri: Uri): Resource<Boolean> {
        return repository.saveImageRepo(uri)
    }

    override suspend fun getDataInteractor(uid: String): Resource<User> {
        return repository.getDataRepo(uid)
    }

    override suspend fun saveDescriptionInteractor(description: String): Resource<Boolean> {
        return repository.saveDescriptionRepo(description)
    }

    override suspend fun changePasswordInteractor(password: String): Resource<Boolean> {
        return repository.changePasswordRepo(password)
    }
}