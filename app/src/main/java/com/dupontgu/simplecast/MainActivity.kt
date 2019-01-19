package com.dupontgu.simplecast

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadOptions
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.*
import kotlinx.android.synthetic.main.activity_main.*

internal const val TAG = "MAIN"
internal const val STREAM_ADDRESS_KEY = "streamAddressKey"

class MainActivity : AppCompatActivity() {
    private lateinit var castSessionManager: SessionManager
    private val sessionManagerListener: SessionManagerListener<Session> by lazy { SessionManagerListenerImpl() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        castSessionManager = CastContext.getSharedInstance(this).sessionManager
    }

    override fun onResume() {
        super.onResume()
        castSessionManager.addSessionManagerListener(sessionManagerListener)
        // if the field is blank and we have a saved address, populate the field with it
        if (streamLocationField.text.isNullOrBlank()) {
            getSavedStreamAddress().takeIf { !it.isNullOrBlank() }?.let { streamLocationField.setText(it) }
        }
    }

    override fun onPause() {
        super.onPause()
        castSessionManager.removeSessionManagerListener(sessionManagerListener)
    }

    fun startCast() {
        val streamLocation = streamLocationField.text.takeIf { !it.isNullOrBlank() }?.toString() ?: run {
            Toast.makeText(this@MainActivity,
                    "Please enter valid stream address and restart your cast session",
                    Toast.LENGTH_LONG)
                    .show()
            return
        }

        val castSession = castSessionManager.currentCastSession ?: run {
            Toast.makeText(this, "CastSession is null", Toast.LENGTH_LONG).show()
            return
        }

        val audioMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK)
        Log.d(TAG, "Starting cast for: $streamLocation")
        saveStreamAddress(streamLocation)
        val mediaInfo = MediaInfo.Builder(streamLocation)
                .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                .setContentType("audio/aac")
                .setMetadata(audioMetadata)
                .build()
        castSession.remoteMediaClient.load(mediaInfo, MediaLoadOptions.Builder().build())
    }

    private fun saveStreamAddress(address: String) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(STREAM_ADDRESS_KEY, address).apply()
    }

    private fun getSavedStreamAddress(): String? {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(STREAM_ADDRESS_KEY, null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, menu, R.id.media_route_menu_item)
        return true
    }

    private inner class SessionManagerListenerImpl : SessionManagerListener<Session> {
        override fun onSessionStarting(session: Session) {
            Log.d(TAG, "Cast onSessionStarting")
        }

        override fun onSessionStarted(session: Session, sessionId: String) {
            Log.d(TAG, "Cast onSessionStarted")
            invalidateOptionsMenu()
            startCast()
        }

        override fun onSessionStartFailed(session: Session, i: Int) {
            Log.d(TAG, "Cast onSessionStartFailed")
        }

        override fun onSessionEnding(session: Session) {
            Log.d(TAG, "Cast onSessionEnding")
        }

        override fun onSessionResumed(session: Session, wasSuspended: Boolean) {
            Log.d(TAG, "Cast onSessionResumed")
            invalidateOptionsMenu()
        }

        override fun onSessionResumeFailed(session: Session, i: Int) {
            Log.d(TAG, "Cast onSessionResumeFailed")
        }

        override fun onSessionSuspended(session: Session, i: Int) {
            Log.d(TAG, "Cast onSessionSuspended")
        }

        override fun onSessionEnded(session: Session, error: Int) {
            Log.d(TAG, "Cast onSessionEnded")
        }

        override fun onSessionResuming(session: Session, s: String) {
            Log.d(TAG, "Cast onSessionResuming")
        }
    }
}


