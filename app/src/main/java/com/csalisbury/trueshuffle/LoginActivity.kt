package com.csalisbury.trueshuffle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.csalisbury.trueshuffle.services.SpotifyApiService
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var apiService: SpotifyApiService

    companion object {
        private const val CLIENT_ID = "c4a1a641314d4d99b9df5304449a6c8d"
        private const val REDIRECT_URL = "com.csalisbury.trueshuffle://callback"
        private const val REQUEST_CODE = 1259
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login_btn).setOnClickListener {
            login()
        }
    }

    private fun login() {
        val request =
            AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URL)
                .setShowDialog(true)
                .setScopes(
                    arrayOf(
                        "playlist-read-private",
                        "playlist-modify-public",
                        "playlist-modify-private"
                    )
                )
                .build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    apiService.setToken(response.accessToken)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                else -> {
                    val text = findViewById<TextView>(R.id.error_txt)
                    text.text = getString(R.string.unexpected_response_type, response.type)
                }
            }
        }
    }
}