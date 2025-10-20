package com.misfinanzas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GastoAdapter(
    private var lista: List<Gasto>,
    private val onItemClick: (Gasto) -> Unit
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    inner class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        val ivIcono: ImageView = itemView.findViewById(R.id.ivIcono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = lista[position]
        holder.tvDescripcion.text = gasto.descripcion?.takeIf { it.isNotBlank() } ?: gasto.categoria
        holder.tvFecha.text = gasto.fecha
        holder.tvMonto.text = "-$${"%.2f".format(gasto.monto)}"
        holder.tvMonto.setTextColor(Color.RED)
        holder.ivIcono.setImageResource(R.drawable.ic_launcher_foreground)

        holder.itemView.setOnClickListener {
            onItemClick(gasto)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Gasto>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}