package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.udacity.ui.DetailActivity


private val NOTIFICATION_ID = 0
val EXTRA_SELECTED_LINK = "EXTRA_SELECTED_LINK"
val EXTRA_DOWNLOAD_STATUS = "EXTRA_DOWNLOAD_STATUS"

fun NotificationManager.sendNotification(link: String, status: Boolean, context: Context) {

    val contentIntent = Intent(context, DetailActivity::class.java)
    contentIntent.putExtras(bundleOf(
        EXTRA_SELECTED_LINK to link,
        EXTRA_DOWNLOAD_STATUS to status
    ))

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.notification_channel_id)
    )

        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_description))
        .addAction(
            R.drawable.ic_cloud_download,
            context.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())

}