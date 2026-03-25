package com.example.donpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.donpan.clases.ProductoModel
class ProductosAdapter(
    private val productos: List<ProductoModel>,
    private val onAgregarClick: (ProductoModel) -> Unit // Función lambda para el clic
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    // 1. ViewHolder (Para mantener las vistas de un solo ítem)
    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tv_nombre_producto)
        val precio: TextView = view.findViewById(R.id.tv_precio)
        val botonAgregar: Button = view.findViewById(R.id.btn_agregar_carrito)

    }

    // 2. onCreateViewHolder (Infla el diseño del ítem)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    // 3. onBindViewHolder (Asigna datos a las vistas)
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre_producto
        holder.precio.text = "$${producto.precio}"

        holder.botonAgregar.setOnClickListener {
            onAgregarClick(producto)
        }
    }

    // 4. getItemCount
    override fun getItemCount() = productos.size
}