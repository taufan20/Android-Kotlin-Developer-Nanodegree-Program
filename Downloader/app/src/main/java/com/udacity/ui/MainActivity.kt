package com.udacity.ui

import android.app.DownloadManager
import android.app.DownloadManager.COLUMN_STATUS
import android.app.DownloadManager.EXTRA_DOWNLOAD_ID
import android.app.DownloadManager.STATUS_SUCCESSFUL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.R
import com.udacity.custom.ButtonState
import com.udacity.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedUrl: String = ""
    private var selectedFile: String = ""

    private lateinit var notificationManager: NotificationManager

    companion object {
        private const val URL_LOADAPP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                custom_button.buttonState = ButtonState.Completed

                val query = DownloadManager.Query()
                query.setFilterById(intent.getLongExtra(EXTRA_DOWNLOAD_ID, 0))

                val manager =
                    context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                val cursor: Cursor = manager.query(query)
                if (cursor.moveToFirst() && cursor.count > 0) {
                    val status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))

                    notificationManager.sendNotification(
                        selectedFile,
                        status == STATUS_SUCCESSFUL,
                        applicationContext
                    )
                }
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            onCustomButtonClicked()
        }

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

    }

    private fun onCustomButtonClicked() {
        when (radio_group.checkedRadioButtonId) {
            R.id.radio_1 -> {
                selectedFile = radio_1.text.toString()
                selectedUrl = URL_GLIDE
            }
            R.id.radio_2 -> {
                selectedFile = radio_2.text.toString()
                selectedUrl = URL_LOADAPP
            }
            R.id.radio_3 -> {
                selectedFile = radio_2.text.toString()
                selectedUrl = URL_RETROFIT
            }
            else -> {
                Toast.makeText(this, getString(R.string.select_file_message), Toast.LENGTH_SHORT).show()
            }
        }

        if (radio_group.checkedRadioButtonId != -1) {
            custom_button.buttonState = ButtonState.Loading
            download()
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }


    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Downloader App"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

}
