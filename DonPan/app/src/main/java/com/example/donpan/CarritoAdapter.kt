package com.example.donpan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.donpan.clases.CarritoItemModel

class CarritoAdapter(
    private val items: MutableList<CarritoItemModel>,
    private val onCantidadChanged: (CarritoItemModel) -> Unit // Callback al cambiar cantidad
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tv_nombre_carrito)
        val precioUnitario: TextView = view.findViewById(R.id.tv_precio_unitario)
        val cantidad: TextView = view.findViewById(R.id.tv_cantidad_carrito)
        val subtotal: TextView = view.findViewById(R.id.tv_subtotal_item)
        val btnRestar: Button = view.findViewById(R.id.btn_restar_cantidad)
        val btnSumar: Button = view.findViewById(R.id.btn_sumar_cantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = items[position]

        holder.nombre.text = item.producto.nombre_producto
        holder.precioUnitario.text = "Precio unitario: $${item.producto.precio}"
        holder.cantidad.text = item.cantidad.toString()
        holder.subtotal.text = "Subtotal: $${item.producto.precio * item.cantidad}"

        // Lógica para aumentar cantidad
        holder.btnSumar.setOnClickListener {
            item.cantidad++
            onCantidadChanged(item)
        }

        // Lógica para restar cantidad
        holder.btnRestar.setOnClickListener {
            if (item.cantidad > 1) {
                item.cantidad--
                onCantidadChanged(item)
            } else {

                items.removeAt(position)
                onCantidadChanged(item)

            }
        }
    }

    override fun getItemCount() = items.size
}