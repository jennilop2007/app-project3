package com.example.proyectou3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectou3.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_LOCKOUT ||
                        errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT
                    ) {
                        Toast.makeText(
                            applicationContext,
                            "Demasiados intentos fallidos. Entrando con tu contraseña.",
                            Toast.LENGTH_LONG
                        ).show()
                        val intento = Intent(this@MainActivity, PanelActivity::class.java)
                        startActivity(intento)
                        finish()
                    } else if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_CANCELED
                    ) {
                        val intento = Intent(this@MainActivity, PanelActivity::class.java)
                        startActivity(intento)
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Authentication error: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intento = Intent(this@MainActivity, PanelActivity::class.java)
                    startActivity(intento)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        binding.btnIngresar.setOnClickListener {
            val correo = binding.etCorreo.text.toString()
            val contra = binding.etContra.text.toString()

            if (correo.isEmpty() || contra.isEmpty()) {
                Toast.makeText(this, "Completa correo y contrasena", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(correo, contra).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (puedeUsarBiometria()) {
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            val intento = Intent(this@MainActivity, PanelActivity::class.java)
                            startActivity(intento)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Error al iniciar sesion: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.tvRegistro.setOnClickListener {
            val intento = Intent(this, RegistrarActivity::class.java)
            startActivity(intento)
        }
    }

    private fun puedeUsarBiometria(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "Este telefono no tiene sensor de huella.", Toast.LENGTH_LONG)
                    .show()
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "El sensor no esta disponible por ahora.", Toast.LENGTH_LONG)
                    .show()
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    this,
                    "No hay huellas registradas. Agrega una en Ajustes.",
                    Toast.LENGTH_LONG
                ).show()
                false
            }

            else -> {
                Toast.makeText(this, "La biometria no esta disponible.", Toast.LENGTH_LONG).show()
                false
            }
        }
    }
}