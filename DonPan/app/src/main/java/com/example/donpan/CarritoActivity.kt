package com.example.donpan

import android.content.ContentValues.TAG
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
import com.example.donpan.db.*
import com.example.unidad3sql.db.DatabaseHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CarritoActivity : AppCompatActivity() {

    // Inicializaciones necesarias
    private lateinit var rvCarrito: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var carritoAdapter: CarritoAdapter

    // Repositorios
    private lateinit var DB: DatabaseHelper
    private lateinit var pedidoRepo: PedidoRepository

    private var carritoItems = mutableListOf<CarritoItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // 1. Inicializar la DB
        DB = DatabaseHelper(this)

        pedidoRepo = PedidoRepository(DB, this)

        // 2. Referencias a la UI
        rvCarrito = findViewById(R.id.rv_carrito_items)
        tvTotal = findViewById(R.id.tv_total_carrito)
        val btnPagar: Button = findViewById(R.id.btn_pagar) // <-- Botón de finalizar compra

        // 3. Recuperar los datos del carrito (JSON)
        val carritoJson = intent.getStringExtra("CARRITO_LISTA_JSON")
        if (carritoJson != null) {
            val type = object : TypeToken<MutableList<CarritoItemModel>>() {}.type
            carritoItems = Gson().fromJson(carritoJson, type)
        }

        // 4. Configurar RecyclerView y Adapter
        carritoAdapter = CarritoAdapter(carritoItems,
            onCantidadChanged = { item ->
                actualizarResumen()
            }
        )
        rvCarrito.layoutManager = LinearLayoutManager(this)
        rvCarrito.adapter = carritoAdapter

        // 5. CONFIGURACIÓN DEL BOTÓN FINALIZAR COMPRA
        btnPagar.setOnClickListener {
            iniciarCheckout()
        }

        // 6. Mostrar el resumen inicial
        actualizarResumen()
    }

    private fun iniciarCheckout() {
        if (carritoItems.isEmpty()) {
            Toast.makeText(this, "No hay productos en el carrito para finalizar la compra.", Toast.LENGTH_SHORT).show()
            return
        }

        val total = carritoItems.sumOf { it.producto.precio * it.cantidad }

        // Llama a la función que guarda la orden y retorna el ID del pedido
        val nuevoPedidoId = pedidoRepo.finalizarCompra(carritoItems, total)
        Log.d(TAG, "Se reciben estos datos: $nuevoPedidoId")
        if (nuevoPedidoId > 0) {
            // ÉXITO: Transfiere el control a la BoletaActivity
            Toast.makeText(this, "¡Compra finalizada! Pedido #$nuevoPedidoId", Toast.LENGTH_LONG).show()

            // Borrar la lista local para evitar errores si el usuario vuelve
            carritoItems.clear()

            // Navegar a la Activity de Boleta/Confirmación
            val intentBoleta = Intent(this, BoletaActivity::class.java).apply {
                putExtra("ID_PEDIDO_FINALIZADO", nuevoPedidoId)
            }
            startActivity(intentBoleta)
            finish() // Cierra CarritoActivity para que no se pueda volver atrás

        } else {
            Toast.makeText(this, "Error al guardar el pedido en la base de datos.", Toast.LENGTH_LONG).show()
        }
    }

    private fun actualizarResumen() {
        val total = carritoItems.sumOf { it.producto.precio * it.cantidad }
        tvTotal.text = "Total a Pagar: $$total"
        carritoAdapter.notifyDataSetChanged()
    }
}