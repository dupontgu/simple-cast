package com.dupontgu.simplecast

import android.content.Context
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.MediaIntentReceiver
import com.google.android.gms.cast.framework.media.NotificationOptions

// Instantiated by the system, appears unused
@Suppress("unused")
class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(appContext: Context): CastOptions {

        val buttonActions = listOf(MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK, MediaIntentReceiver.ACTION_STOP_CASTING)
        val compatButtonActionsIndices = intArrayOf(0, 1)
        // Builds a notification with the above actions.
        // Tapping on the notification opens an Activity with class VideoBrowserActivity.
        val notificationOptions = NotificationOptions.Builder()
                .setActions(buttonActions, compatButtonActionsIndices)
                .setTargetActivityClassName(MainActivity::class.java.name)
                .build()

        val mediaOptions = CastMediaOptions.Builder()
                .setNotificationOptions(notificationOptions)
                .build()

        return CastOptions.Builder()
                .setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
                .setCastMediaOptions(mediaOptions)
                .build()
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? = null
}
