package com.example.donpan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.donpan.clases.CarritoItemModel
import com.example.donpan.clases.ProductoModel
import com.example.donpan.db.ProductoRepository
import com.example.unidad3sql.db.DatabaseHelper
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var DB: DatabaseHelper
    private lateinit var productoRepo: ProductoRepository
    private lateinit var btnVerCarrito: Button

    // Lista mutable para guardar los CarritoItemModel
    private val carritoItems = mutableListOf<CarritoItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        DB = DatabaseHelper(this)
        productoRepo = ProductoRepository(DB)

        // Referencia al botón del carrito
        btnVerCarrito = findViewById(R.id.btn_ver_carrito)

        // Listener para abrir el Carrito
        btnVerCarrito.setOnClickListener {
            abrirCarrito()
        }

        // 1. Obtener productos y configurar el RecyclerView...
        val productos = productoRepo.obtenerProductos()
        val recyclerView: RecyclerView = findViewById(R.id.rv_productos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ProductosAdapter(productos) { productoAgregado ->
            agregarProductoAlCarrito(productoAgregado)
        }
        recyclerView.adapter = adapter

        // Actualiza el texto inicial del botón
        actualizarBotonCarrito()
    }

    // =========================================================
    // NUEVA FUNCIÓN: Abrir Carrito
    // =========================================================
    private fun abrirCarrito() {
        if (carritoItems.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Convertir la lista de objetos a una cadena JSON
        val carritoJson: String = Gson().toJson(carritoItems)

        // 2. Crear el Intent y adjuntar el JSON
        val intent = Intent(this, CarritoActivity::class.java).apply {
            putExtra("CARRITO_LISTA_JSON", carritoJson)
        }

        // 3. Iniciar la Activity
        startActivity(intent)
    }

    // =========================================================
    // Lógica del Carrito
    // =========================================================
    private fun agregarProductoAlCarrito(producto: ProductoModel) {
        val existingItem = carritoItems.find { it.producto.id_producto == producto.id_producto }

        if (existingItem != null) {
            existingItem.cantidad++
            Toast.makeText(this, "Añadida una unidad más de ${producto.nombre_producto}", Toast.LENGTH_SHORT).show()
        } else {
            carritoItems.add(CarritoItemModel(producto, 1))
            Toast.makeText(this, "${producto.nombre_producto} añadido al carrito!", Toast.LENGTH_SHORT).show()
        }

        // Llama a la función para actualizar el texto del botón
        actualizarBotonCarrito()
    }

    // Función para actualizar el texto del botón del carrito
    private fun actualizarBotonCarrito() {
        val totalItems = carritoItems.sumOf { it.cantidad }
        btnVerCarrito.text = "Ver Carrito ($totalItems items)"
    }

    // Opcional: Si el usuario vuelve de CarritoActivity y ha hecho cambios
    // (Ej: ha eliminado ítems), puedes necesitar refrescar la lista.
    override fun onResume() {
        super.onResume()

        actualizarBotonCarrito()
    }
}

