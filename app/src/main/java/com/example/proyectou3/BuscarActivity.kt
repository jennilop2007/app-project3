package com.example.proyectou3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectou3.databinding.ActivityBuscarBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class BuscarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuscarBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBuscarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBuscar.setOnClickListener {
            val codigo = binding.etCodigoBusqueda.text.toString()
            if (codigo.isNotEmpty()) {
                if (codigo.contains("/")) {
                    Toast.makeText(this, "El codigo no puede contener /", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                db.collection("tareas").document(codigo).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            binding.etTitulo.setText(document.getString("titulo"))
                            binding.etMateria.setText(document.getString("materia"))
                            binding.etFecha.setText(document.getString("fecha"))
                            binding.etDescripcion.setText(document.getString("descripcion"))
                            Toast.makeText(this, "Tarea encontrada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "No existe una tarea con ese código", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al buscar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Ingresa un código para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnActualizar.setOnClickListener {
            val codigo = binding.etCodigoBusqueda.text.toString()
            val titulo = binding.etTitulo.text.toString()
            val materia = binding.etMateria.text.toString()
            val fecha = binding.etFecha.text.toString()
            val descripcion = binding.etDescripcion.text.toString()

            if (codigo.isNotEmpty()) {
                if (titulo.isEmpty() || materia.isEmpty() || fecha.isEmpty() || descripcion.isEmpty()) {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val tareaActualizada = hashMapOf<String, Any>(
                    "codigo" to codigo,
                    "titulo" to titulo,
                    "materia" to materia,
                    "fecha" to fecha,
                    "descripcion" to descripcion
                )

                db.collection("tareas").document(codigo)
                    .update(tareaActualizada)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Primero busca una tarea por su código", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }
}