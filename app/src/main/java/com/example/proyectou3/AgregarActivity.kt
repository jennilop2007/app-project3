package com.example.proyectou3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectou3.databinding.ActivityAgregarBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AgregarActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAgregarBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAgregarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnGuardar.setOnClickListener {
            val codigo = binding.etCodigo.text.toString()
            val titulo = binding.etTitulo.text.toString()
            val fecha = binding.etFecha.text.toString()
            val materia = binding.etMateria.text.toString()
            val descripcion = binding.etDescripcion.text.toString()

            if (codigo.isEmpty() || titulo.isEmpty() || fecha.isEmpty() ||
                materia.isEmpty() || descripcion.isEmpty()
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (codigo.contains("/")) {
                Toast.makeText(this, "El codigo no puede contener /", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tarea = hashMapOf(
                "codigo" to codigo,
                "titulo" to titulo,
                "fecha" to fecha,
                "materia" to materia,
                "descripcion" to descripcion
            )

            db.collection("tareas").document(codigo)
                .set(tarea)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Fallo en registrar", Toast.LENGTH_LONG).show()
                }
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }
}