package com.example.donpan.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.donpan.clases.CarritoItemModel
import com.example.donpan.clases.DetalleItem
import com.example.donpan.clases.PedidoBoleta
import com.example.donpan.clases.UsuarioDatos
import com.example.unidad3sql.db.DatabaseHelper
import com.example.unidad3sql.db.DatabaseHelper.Companion.COLUMN_ID_PRODUCTO
import com.example.unidad3sql.db.DatabaseHelper.Companion.COLUMN_NOMBRE_PRODUCTO
import com.example.unidad3sql.db.DatabaseHelper.Companion.TABLE_PRODUCTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoRepository(private val dbHelper: DatabaseHelper, private val context: Context) {

    private val TABLE_PEDIDO = "pedido"

    private val COL_PEDIDO_ID = "id_pedido"
    private val COL_PEDIDO_ID_USUARIO = "id_usuario"
    private val COL_PEDIDO_FECHA = "fecha_pedido"
    private val COL_PEDIDO_TOTAL = "total_pedido"

    private val TABLE_DETALLE_PEDIDO = "detalle_pedido"

    private val COL_DETALLE_ID = "id_detalle"
    private val COL_DETALLE_ID_PEDIDO = "id_pedido"
    private val COL_DETALLE_ID_PRODUCTO = "id_producto"
    private val COL_DETALLE_CANTIDAD = "cantidad"
    private val COL_DETALLE_PRECIO_UNITARIO = "precio_unitario"



    // Asumimos que el ID del usuario actual está disponible
    private fun getCurrentUserId(): Int {
        val sharedPref = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

        return sharedPref.getInt("CURRENT_USER_ID", -1)
    }

    // Función principal para finalizar la compra
    fun finalizarCompra(itemsCarrito: List<CarritoItemModel>, total: Int): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        var pedidoId: Long = -1

        try {
            // 1. REGISTRAR EL ENCABEZADO DEL PEDIDO (TABLE_PEDIDO)
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val valuesPedido = ContentValues().apply {
                put(COL_PEDIDO_ID_USUARIO, getCurrentUserId())
                put(COL_PEDIDO_FECHA, fechaActual)
                put(COL_PEDIDO_TOTAL, total)
            }
            pedidoId = db.insert(TABLE_PEDIDO, null, valuesPedido)

            if (pedidoId > 0) {
                // 2. REGISTRAR EL DETALLE DEL PEDIDO (TABLE_DETALLE_PEDIDO)
                for (item in itemsCarrito) {
                    val valuesDetalle = ContentValues().apply {
                        put(COL_DETALLE_ID_PEDIDO, pedidoId)
                        put(COL_DETALLE_ID_PRODUCTO, item.producto.id_producto)
                        put(COL_DETALLE_CANTIDAD, item.cantidad)
                        put(COL_DETALLE_PRECIO_UNITARIO, item.producto.precio)
                    }
                    val detalleResult = db.insert(TABLE_DETALLE_PEDIDO, null, valuesDetalle)
                    if (detalleResult == -1L) {
                        throw Exception("Error al insertar detalle del pedido.")
                    }
                }

                db.setTransactionSuccessful() // Confirma la transacción
            }

        } catch (e: Exception) {

            pedidoId = -1 // Indica que falló
        } finally {
            db.endTransaction()
            db.close()
        }
        return pedidoId // Retorna el ID del pedido creado
    }

    fun obtenerPedidoFinal(pedidoId: Long): PedidoBoleta? {
        val db = dbHelper.readableDatabase
        var pedidoBoleta: PedidoBoleta? = null
        val detalles = mutableListOf<DetalleItem>()

        // 1. Obtener Encabezado del Pedido
        val queryPedido = "SELECT $COL_PEDIDO_ID_USUARIO, $COL_PEDIDO_FECHA, $COL_PEDIDO_TOTAL FROM $TABLE_PEDIDO WHERE $COL_PEDIDO_ID = ?"
        val cursorPedido: Cursor = db.rawQuery(queryPedido, arrayOf(pedidoId.toString()))

        var idUsuario = 0 // Inicializar a 0 o un valor seguro
        var fecha = ""
        var total = 0

        if (cursorPedido.moveToFirst()) {

            idUsuario = cursorPedido.getInt(cursorPedido.getColumnIndexOrThrow(COL_PEDIDO_ID_USUARIO))
            fecha = cursorPedido.getString(cursorPedido.getColumnIndexOrThrow(COL_PEDIDO_FECHA))
            total = cursorPedido.getInt(cursorPedido.getColumnIndexOrThrow(COL_PEDIDO_TOTAL))
        }
        cursorPedido.close()

        if (fecha.isNotEmpty()) {
            // 2. Obtener Detalles del Pedido usando JOIN con la tabla PRODUCTO
            val queryDetalle = "SELECT d.$COL_DETALLE_CANTIDAD, d.$COL_DETALLE_PRECIO_UNITARIO, p.$COLUMN_NOMBRE_PRODUCTO " +
                    "FROM $TABLE_DETALLE_PEDIDO d " +
                    "JOIN $TABLE_PRODUCTO p ON d.$COL_DETALLE_ID_PRODUCTO = p.$COLUMN_ID_PRODUCTO " +
                    "WHERE d.$COL_DETALLE_ID_PEDIDO = ?"

            val cursorDetalle: Cursor = db.rawQuery(queryDetalle, arrayOf(pedidoId.toString()))

            if (cursorDetalle.moveToFirst()) {
                do {
                    val nombre = cursorDetalle.getString(cursorDetalle.getColumnIndexOrThrow(COLUMN_NOMBRE_PRODUCTO))
                    val cantidad = cursorDetalle.getInt(cursorDetalle.getColumnIndexOrThrow(COL_DETALLE_CANTIDAD))
                    val precioUnitario = cursorDetalle.getInt(cursorDetalle.getColumnIndexOrThrow(COL_DETALLE_PRECIO_UNITARIO))

                    detalles.add(DetalleItem(nombre, cantidad, precioUnitario))
                } while (cursorDetalle.moveToNext())
            }
            cursorDetalle.close()

            // 3. Crear el modelo de la Boleta
            pedidoBoleta = PedidoBoleta(pedidoId, idUsuario, fecha, total, detalles)
        }

        db.close()
        return pedidoBoleta
    }

    fun obtenerDatosUsuario(userId: Int): UsuarioDatos? {
        val db = dbHelper.readableDatabase
        var usuarioDatos: UsuarioDatos? = null

        // Usamos las constantes del DatabaseHelper
        val TABLE_USUARIO = DatabaseHelper.TABLE_USUARIO
        val COLUMN_ID = DatabaseHelper.COLUMN_ID
        val COLUMN_NOMBRE = DatabaseHelper.COLUMN_NOMBRE
        val COLUMN_CORREO = DatabaseHelper.COLUMN_CORREO
        val COLUMN_CELULAR = DatabaseHelper.COLUMN_CELULAR
        val COLUMN_DIRECCION = DatabaseHelper.COLUMN_DIRECCION

        val query = "SELECT $COLUMN_NOMBRE, $COLUMN_CORREO, $COLUMN_CELULAR, $COLUMN_DIRECCION FROM $TABLE_USUARIO WHERE $COLUMN_ID = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
            val correo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORREO))
            val celular = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CELULAR))
            val direccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECCION))

            usuarioDatos = UsuarioDatos(nombre, correo, celular, direccion)
        }
        cursor.close()
        db.close()
        return usuarioDatos
    }
}

