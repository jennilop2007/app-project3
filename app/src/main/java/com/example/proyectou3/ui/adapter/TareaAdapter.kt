package com.example.proyectou3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectou3.data.Tarea
import com.example.proyectou3.databinding.ItemTareaBinding

class TareaAdapter(private val listaTareas: List<Tarea>) :
    RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    class TareaViewHolder(val binding: ItemTareaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TareaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = listaTareas[position]
        holder.binding.tvItemTitulo.text = tarea.titulo
        holder.binding.tvItemCodigo.text = tarea.codigo
        holder.binding.tvItemMateria.text = tarea.materia
        holder.binding.tvItemDescripcion.text = tarea.descripcion
        holder.binding.tvItemFecha.text = tarea.fecha
    }

    override fun getItemCount(): Int = listaTareas.size
}