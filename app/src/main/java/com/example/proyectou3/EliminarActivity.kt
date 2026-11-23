package com.example.proyectou3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectou3.databinding.ActivityEliminarBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class EliminarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEliminarBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEliminarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBuscar.setOnClickListener {
            val codigo = binding.etCodigoEliminar.text.toString()
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Ingresa un código para buscar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (codigo.contains("/")) {
                Toast.makeText(this, "El codigo no puede contener /", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("tareas").document(codigo).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.tvTituloDetalle.text = document.getString("titulo") ?: "---"
                        binding.tvMateriaDetalle.text = document.getString("materia") ?: "---"
                        binding.tvFechaDetalle.text = document.getString("fecha") ?: "---"
                        binding.tvDescripcionDetalle.text = document.getString("descripcion") ?: "---"
                        Toast.makeText(this, "Tarea encontrada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No existe una tarea con ese código", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al buscar la tarea", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnEliminar.setOnClickListener {
            val codigo = binding.etCodigoEliminar.text.toString()
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Ingresa el código de la tarea a eliminar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (codigo.contains("/")) {
                Toast.makeText(this, "El codigo no puede contener /", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que quieres eliminar la tarea $codigo?")
                .setPositiveButton("Eliminar") { _, _ ->

            db.collection("tareas").document(codigo).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
                    binding.etCodigoEliminar.setText("")
                    binding.tvTituloDetalle.text = "---"
                    binding.tvMateriaDetalle.text = "---"
                    binding.tvFechaDetalle.text = "---"
                    binding.tvDescripcionDetalle.text = "---"
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al eliminar la tarea", Toast.LENGTH_SHORT).show()
                }
        }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }
}