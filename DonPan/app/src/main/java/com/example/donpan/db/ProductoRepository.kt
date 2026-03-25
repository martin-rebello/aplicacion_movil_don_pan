package com.example.donpan.db

import android.content.ContentValues
import android.database.Cursor
import com.example.unidad3sql.db.DatabaseHelper
import com.example.donpan.clases.ProductoModel



class ProductoRepository(private val dbHelper: DatabaseHelper) {


    private val TABLE_PRODUCTO = "producto"

    private val COLUMN_ID_PRODUCTO = "id_producto"

    private val COLUMN_NOMBRE_PRODUCTO = "nombre_producto"

    private val COLUMN_DESCRIPCION = "descripcion"

    private val COLUMN_PRECIO = "precio"

    private val COLUMN_IMAGEN = "imagen"

    // INSERT
    fun insertarProducto(nombre_producto: String, descripcion: String, precio: Int, imagen: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE_PRODUCTO, nombre_producto)
            put(COLUMN_DESCRIPCION, descripcion)
            put(COLUMN_PRECIO, precio)
            put(COLUMN_IMAGEN, imagen)
        }

        val resultado = db.insert(TABLE_PRODUCTO, null, values)
        db.close()
        return resultado
    }

    //DELETE
    fun eliminarProducto(nombre_producto: String): Int {
        val db = dbHelper.writableDatabase


        val filasEliminadas = db.delete(
            TABLE_PRODUCTO,
            "$COLUMN_NOMBRE_PRODUCTO = ?",
            arrayOf(nombre_producto)
        )

        db.close()
        return filasEliminadas // Retorna el número de filas eliminadas (Int)
    }

    //SELECT (todos los registros)
    fun obtenerProductos(): List<ProductoModel> {
        val lista = mutableListOf<ProductoModel>()
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM $TABLE_PRODUCTO ORDER BY $COLUMN_ID_PRODUCTO DESC"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id_producto = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUCTO))
                val nombre_producto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_PRODUCTO))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION))
                val precio = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRECIO))
                val imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN))

                lista.add(
                    ProductoModel(
                        id_producto,
                        nombre_producto,
                        descripcion,
                        precio,
                        imagen
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}
