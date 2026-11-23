package com.example.proyectou3

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectou3.data.Tarea
import com.example.proyectou3.databinding.ActivityPanelBinding
import com.example.proyectou3.ui.adapter.TareaAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class PanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanelBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()

        binding = ActivityPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvTareas.layoutManager = LinearLayoutManager(this)

        binding.btnAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarActivity::class.java))
        }

        binding.btnBuscar.setOnClickListener {
            startActivity(Intent(this, BuscarActivity::class.java))
        }

        binding.btnEliminar.setOnClickListener {
            startActivity(Intent(this, EliminarActivity::class.java))
        }


        binding.btnCerrarSesion.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) {
            cargarTareas()
        }
    }

    private fun cargarTareas() {
        db.collection("tareas").get().addOnSuccessListener { result ->
            val listaTareas = mutableListOf<Tarea>()

            for (doc in result) {
                listaTareas.add(
                    Tarea(
                        codigo = doc.getString("codigo") ?: doc.id,
                        titulo = doc.getString("titulo") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        fecha = doc.getString("fecha") ?: "",
                        materia = doc.getString("materia") ?: ""
                    )
                )
            }

            binding.rvTareas.adapter = TareaAdapter(listaTareas)
        }
    }
}