package com.example.donpan.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.donpan.R
import com.example.donpan.clases.DetalleItem

class BoletaAdapter(private val detalles: List<DetalleItem>) :
    RecyclerView.Adapter<BoletaAdapter.BoletaViewHolder>() {

    class BoletaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tv_boleta_item_nombre)
        val cantidad: TextView = view.findViewById(R.id.tv_boleta_item_cantidad)
        val precio: TextView = view.findViewById(R.id.tv_boleta_item_precio)
        val subtotal: TextView = view.findViewById(R.id.tv_boleta_item_subtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoletaViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_boleta_detalle, parent, false)
        return BoletaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoletaViewHolder, position: Int) {
        val item = detalles[position]

        holder.nombre.text = item.nombreProducto
        holder.cantidad.text = "Cant: ${item.cantidad}"
        holder.precio.text = "$${item.precioUnitario} c/u"
        holder.subtotal.text = "Total: $${item.subtotal}"
    }

    override fun getItemCount() = detalles.size
}