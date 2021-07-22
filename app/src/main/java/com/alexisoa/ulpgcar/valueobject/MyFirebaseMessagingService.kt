package com.alexisoa.ulpgcar.valueobject

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.presentation.MainActivity
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import kotlin.random.Random

class MyFirebaseMessagingService: FirebaseMessagingService()  {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage!!.data.isNotEmpty()){
            val title = remoteMessage.data["titulo"]
            val detail = remoteMessage.data["detalle"]
            createNotificationChannel(title,detail)
        }
    }

    private fun createNotificationChannel(title:String?, detail: String?) {
        val id = "mensaje"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, "nuevo", importance).apply {
                enableLights(true)
                setShowBadge(true)
            }
            nm.createNotificationChannel(mChannel)
        }

        try {
            builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(detail)
                .setStyle(NotificationCompat.BigTextStyle().bigText(detail))
                .setContentIntent(notificationClick(title))
                .setSmallIcon(setIcon(title))

            val randon = Random
            val notificationRandom = randon.nextInt(8000)
            nm.notify(notificationRandom,builder.build())
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    private fun setIcon(title: String?): Int {
        when(title){
            "Mensaje" -> {
                return R.drawable.ic_message_24
            }
            "Reserva" -> {
                return R.drawable.ic_directions_car_24
            }
            "Solicitud" -> {
                return R.drawable.ic_notification_important_24
            }
            "Reseña" -> {
                return R.drawable.ic_rate_review_24
            }
            else-> return R.drawable.ic_notification_important_24
        }
    }

    fun notificationClick(title: String?) : PendingIntent{

        val fragment = setFragment(title)
        val arguments = Bundle().apply {
            putString("notification" , "yes")
        }
        return NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(fragment)
            .createPendingIntent()
    }

    private fun setFragment(title: String?): Int {
        when(title){
            "Mensaje" -> {
                prefs.saveNotiChat("yes")
                return R.id.navigation_chat
            }
            "Reserva" -> {
                println("RESERVAAAAAAAAAAA")
                prefs.saveNotiReserve("yes")
                return R.id.navigation_travels
            }
            "Solicitud" -> {
                println("SOLICITUUUUUUU")
                prefs.saveNotiRequest("yes")
                return R.id.navigation_travels
            }
            "Reseña" -> {
                println("RESEÑAAAAA")
                prefs.saveNotiReviews("yes")
                return R.id.navigation_profile
            }
            else-> return R.id.navigation_search
        }
    }
}