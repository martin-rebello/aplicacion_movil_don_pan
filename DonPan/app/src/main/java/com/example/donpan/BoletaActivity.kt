package com.example.donpan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.donpan.clases.PedidoBoleta
import com.example.donpan.db.PedidoRepository
import com.example.donpan.ui.BoletaAdapter
import com.example.unidad3sql.db.DatabaseHelper
import com.example.donpan.clases.UsuarioDatos

class BoletaActivity : AppCompatActivity() {

    private lateinit var DB: DatabaseHelper
    private lateinit var pedidoRepo: PedidoRepository
    private var pedidoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boleta)

        // 1. Inicialización de Repositorios
        DB = DatabaseHelper(this)
        pedidoRepo = PedidoRepository(DB, this)

        // 2. Obtener el ID del pedido
        pedidoId = intent.getLongExtra("ID_PEDIDO_FINALIZADO", -1)

        if (pedidoId == -1L) {
            Toast.makeText(this, "Error: No se encontró el ID del pedido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 3. Cargar la información completa de la boleta desde la DB
        val boleta: PedidoBoleta? = pedidoRepo.obtenerPedidoFinal(pedidoId)

        if (boleta != null) {

            val usuario: UsuarioDatos? = pedidoRepo.obtenerDatosUsuario(boleta.idUsuario)

            // Pasamos tanto la boleta como los datos del usuario a la función de muestra
            mostrarBoleta(boleta, usuario)
        } else {
            Toast.makeText(this, "Error al cargar los detalles del pedido #$pedidoId", Toast.LENGTH_LONG).show()
        }

        // 4. Configurar botón Volver
        findViewById<Button>(R.id.btn_volver_inicio).setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarBoleta(boleta: PedidoBoleta, usuario: UsuarioDatos?) {
        val tvInfo: TextView = findViewById(R.id.tv_boleta_info)
        val tvTotal: TextView = findViewById(R.id.tv_boleta_total)
        val rvItems: RecyclerView = findViewById(R.id.rv_boleta_items)

        // Referencias a los nuevos TextViews de usuario
        val tvNombreCliente: TextView = findViewById(R.id.tv_nombre_cliente)
        val tvCorreoCliente: TextView = findViewById(R.id.tv_correo_cliente)
        val tvDireccionCliente: TextView = findViewById(R.id.tv_direccion_cliente)

        // Mostrar información del encabezado del pedido
        tvInfo.text = "Pedido N°: ${boleta.idPedido}\nFecha: ${boleta.fecha}"
        tvTotal.text = "TOTAL PAGADO: $${boleta.total}"

        //  Mostrar datos del usuario
        if (usuario != null) {
            tvNombreCliente.text = "Nombre Cliente: ${usuario.nombre}"
            tvCorreoCliente.text = "Correo: ${usuario.correo}"
            tvDireccionCliente.text = "Dirección: ${usuario.direccion}"
        } else {
            tvNombreCliente.text = "Cliente Desconocido (ID: ${boleta.idUsuario})"
            tvCorreoCliente.text = ""
            tvDireccionCliente.text = ""
        }

        // Configurar lista de detalles
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = BoletaAdapter(boleta.items)
    }
}