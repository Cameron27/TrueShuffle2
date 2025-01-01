package com.csalisbury.trueshuffle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val CLIENT_ID = "c4a1a641314d4d99b9df5304449a6c8d"
        private const val REDIRECT_URL = "com.csalisbury.trueshuffle://callback"
        private const val REQUEST_CODE = 1259
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                    val sp = getSharedPreferences("spotify", MODE_PRIVATE)
                    val edit = sp.edit()
                    edit.putString("token", response.accessToken)
                    edit.putLong("expires", System.currentTimeMillis() / 1000 + response.expiresIn)
                    edit.apply()

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