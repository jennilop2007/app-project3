package com.example.proyectou3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectou3.databinding.ActivityRegistrarBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
class RegistrarActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnRegistrar.setOnClickListener {
            val correo = binding.etCorreo.text.toString()
            val pass = binding.etContra.text.toString()
            val confirPass = binding.etConfirmarContra.text.toString()

            when {
                correo.isEmpty() || pass.isEmpty() || confirPass.isEmpty() ->
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()

                !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                    Toast.makeText(this, "Correo no valido", Toast.LENGTH_SHORT).show()

                pass.length < 6 ->
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()

                pass != confirPass ->
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

                else -> registrarUsuario(correo, pass)
            }
        }

        binding.tvIniciaSesion.setOnClickListener {
            finish()
        }
    }

    public fun registrarUsuario(correo: String, password: String){
        auth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val user = auth.currentUser
                Toast.makeText(baseContext, "Authentication is successful", Toast.LENGTH_SHORT,).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT,).show()
            }
        }
    }
}