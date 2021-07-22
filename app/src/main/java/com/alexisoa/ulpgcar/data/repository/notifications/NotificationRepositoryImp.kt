package com.alexisoa.ulpgcar.data.repository.notifications

import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.valueobject.Resource
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import org.json.JSONException
import org.json.JSONObject

class NotificationRepositoryImp : NotificationRepository {
    private val fsDatabase = FirebaseFirestore.getInstance()

    override suspend fun sendNotificationFromRepo(title:String, detail:String, myRequest: RequestQueue, userUid:String): Resource<Boolean> {
        val userRef = fsDatabase.collection("users").document(userUid).get().await()
        val tokenUser = userRef.get("appToken")
        val json = JSONObject()
        try {
            json.put("to", tokenUser)
            val notification = JSONObject()
            notification.put("titulo", title)
            notification.put("detalle", detail)
            json.put("data", notification)
            val URL = "https://fcm.googleapis.com/fcm/send"
            val request = object : JsonObjectRequest(Method.POST, URL, json, null, null){

                override fun getHeaders(): Map<String, String> {
                    val header = HashMap<String,String>()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=AAAAcXRP4m8:APA91bEp26nEKWusrbOzTiYbBRxqluvPeGK7OlnWd5nvB5rUgknmTp5A6rRj09wpFkVhhTl558SYLFODcvZmVbW5L2O8EMe-9A6RDiyjPE5ek7dJiGoGEaoSm5vyeNAvkUVhRZLNOt5P"
                    return header
                }
            }

            myRequest.add(request)
            return Resource.Success(true)
        }catch (e:JSONException){
            return Resource.Success(false)
        }
    }

    override suspend fun sendNotificationGroupFromRepo(title: String, detail: String, myRequest: RequestQueue, travelId: String): Resource<Boolean> {
        val userRef = fsDatabase.collection("users")
        val travelRef = userRef.document(prefs.getUID()).collection("advertisement").document(travelId).get().await()
        val finished = travelRef.get("finished")
        if(finished as Boolean){
            println("/*************************** AQUI VA LA NOTIFICACION DE FINALIZACION /************************************")
            val query = userRef.document(prefs.getUID()).collection("advertisement").document(travelId).collection("requests").whereEqualTo("statusReserve", "Aceptada").get().await()
            sendNotificationToGroupByFilter(query, title, detail, myRequest)
        }else{
            println("/*************************** AQUI VA LA NOTI DE ELIMINACION /************************************")
            val query = userRef.document(prefs.getUID()).collection("advertisement").document(travelId).collection("requests").get().await()
            sendNotificationToGroupByFilter(query, title, detail, myRequest)
        }
        return Resource.Success(true)
    }

    private suspend fun sendNotificationToGroupByFilter(query: QuerySnapshot, title: String, detail: String, myRequest: RequestQueue) {
        for (document in query.documents) {
            sendNotificationFromRepo(title, detail, myRequest, document.id)
        }
    }
}