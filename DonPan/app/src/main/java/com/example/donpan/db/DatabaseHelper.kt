package com.example.unidad3sql.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


open class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
         const val DATABASE_NAME = "usuario.db"
         const val DATABASE_VERSION = 1
         const val TABLE_USUARIO = "usuario"

         const val COLUMN_ID = "id"

         const val COLUMN_NOMBRE = "nombre"

         const val COLUMN_CORREO = "correo"

         const val COLUMN_CELULAR = "celular"

         const val COLUMN_DIRECCION = "direccion"

         const val COLUMN_CONTRASENA = "contrasena"

         const val COLUMN_ROL = "rol"

        //TABLA PRODUCTOS

         const val TABLE_PRODUCTO = "producto"

         const val COLUMN_ID_PRODUCTO = "id_producto"

         const val COLUMN_NOMBRE_PRODUCTO = "nombre_producto"

         const val COLUMN_DESCRIPCION = "descripcion"

         const val COLUMN_PRECIO = "precio"

         const val COLUMN_IMAGEN = "imagen"

        //TABLA PEDIDO

        const val TABLE_PEDIDO = "pedido"
        const val COL_PEDIDO_ID = "id_pedido"
        const val COL_PEDIDO_ID_USUARIO = "id_usuario"
        const val COL_PEDIDO_FECHA = "fecha_pedido"
        const val COL_PEDIDO_TOTAL = "total_pedido"

        //TABLA CARRITO

        const val TABLE_DETALLE_PEDIDO = "detalle_pedido"
        const val COL_DETALLE_ID = "id_detalle"
        const val COL_DETALLE_ID_PEDIDO = "id_pedido"
        const val COL_DETALLE_ID_PRODUCTO = "id_producto"
        const val COL_DETALLE_CANTIDAD = "cantidad"
        const val COL_DETALLE_PRECIO_UNITARIO = "precio_unitario"


    }

    //Se crea la tabla al ejecutar la app por primera vez
    override fun onCreate(db: SQLiteDatabase) {
        // Creación de tabla usuarios
        val createTable = """
            CREATE TABLE $TABLE_USUARIO(
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT NOT NULL,
                $COLUMN_CELULAR TEXT NOT NULL,
                $COLUMN_DIRECCION TEXT NOT NULL,
                $COLUMN_CONTRASENA TEXT NOT NULL,
                $COLUMN_ROL TEXT 
            )
        """.trimIndent()

        val insertarAdmin = """
            INSERT INTO $TABLE_USUARIO($COLUMN_NOMBRE,$COLUMN_CORREO,$COLUMN_CELULAR,$COLUMN_DIRECCION,$COLUMN_CONTRASENA,$COLUMN_ROL) VALUES(
            'admin','admin@gmail.com','2342342','Calle Admin','1234','admin'
            )
        """.trimIndent()
        db.execSQL(createTable)
        db.execSQL(insertarAdmin)

        // Creación de tabla productos
        val createTableProducto = """
            CREATE TABLE $TABLE_PRODUCTO(
                $COLUMN_ID_PRODUCTO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE_PRODUCTO TEXT NOT NULL,
                $COLUMN_DESCRIPCION TEXT NOT NULL,
                $COLUMN_PRECIO INTEGER NOT NULL,
                $COLUMN_IMAGEN TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableProducto)

        // Creación de tabla pedidos
        val CREATE_PEDIDO_TABLE = "CREATE TABLE $TABLE_PEDIDO (" +
                "$COL_PEDIDO_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_PEDIDO_ID_USUARIO INTEGER NOT NULL," +
                "$COL_PEDIDO_FECHA TEXT NOT NULL," +
                "$COL_PEDIDO_TOTAL INTEGER NOT NULL," +
                "FOREIGN KEY($COL_PEDIDO_ID_USUARIO) REFERENCES $TABLE_USUARIO($COLUMN_ID))"
        db.execSQL(CREATE_PEDIDO_TABLE)

        // Creación de tabla carrito
        val CREATE_DETALLE_PEDIDO_TABLE = "CREATE TABLE $TABLE_DETALLE_PEDIDO (" +
                "$COL_DETALLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_DETALLE_ID_PEDIDO INTEGER NOT NULL," +
                "$COL_DETALLE_ID_PRODUCTO INTEGER NOT NULL," +
                "$COL_DETALLE_CANTIDAD INTEGER NOT NULL," +
                "$COL_DETALLE_PRECIO_UNITARIO INTEGER NOT NULL," +
                "FOREIGN KEY($COL_DETALLE_ID_PEDIDO) REFERENCES $TABLE_PEDIDO($COL_PEDIDO_ID)," +
                "FOREIGN KEY($COL_DETALLE_ID_PRODUCTO) REFERENCES $TABLE_PRODUCTO($COLUMN_ID_PRODUCTO))"
        db.execSQL(CREATE_DETALLE_PEDIDO_TABLE)

    }

    //Si hay una nueva versión de la BD se dispara el trigger
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        onCreate(db)
    }

    // ==========================================================
    // DML: Insertar, Leer, Actualizar y Eliminar registros
    // ==========================================================

    //INSERT
    fun insertarUsuario(nombre: String, correo: String, celular: String, direccion: String, contrasena: String, rol: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_CORREO, correo)
            put(COLUMN_CELULAR, celular)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_CONTRASENA, contrasena)
            put(COLUMN_ROL, rol)
        }

        val resultado = db.insert(TABLE_USUARIO, null, values)
        db.close()
        return resultado // devuelve el id del registro insertado
    }


    //SELECT (todos los registros)
    fun obtenerUsuario(): List<Map<String, Any>> {
        val lista = mutableListOf<Map<String, Any>>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIO ORDER BY $COLUMN_ID DESC"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val correo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORREO))
                val celular = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CELULAR))
                val direccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECCION))
                val contrasena = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTRASENA))

                lista.add(
                    mapOf(
                        "id" to id,
                        "nombre" to nombre,
                        "correo" to correo,
                        "celular" to celular,
                        "direccion" to direccion,
                        "contrasena" to contrasena
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    //SELECT (uno por ID)
    fun obtenerUsuarioPorCorreoYContrasena(correo: String, contrasena: String): Map<String, Any>? {
        val db = readableDatabase
        // 1. Modificar la consulta SQL para buscar por Correo Y Contraseña
        val cursor = db.rawQuery(
            // Asegúrate de que COLUMN_CORREO y COLUMN_CONTRASENA sean los nombres correctos de tus columnas
            "SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_CORREO = ? AND $COLUMN_CONTRASENA = ?",
            // 2. Pasar los parámetros correo y contrasena al array de argumentos
            arrayOf(correo, contrasena)
        )

        var usuario: Map<String, Any>? = null
        if (cursor.moveToFirst()) {
            // Asumiendo que también tienes un COLUMN_ID
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
            // El correo y la contraseña que recibimos son los mismos que están en la base de datos
            val celular = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CELULAR))
            val direccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECCION))
            // 3. Obtener la contraseña como String (VARCHAR/TEXT)
            val contrasenaDB = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTRASENA))
            val rol = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROL))
            usuario = mapOf(
                "id" to id,
                "nombre" to nombre,
                "correo" to correo, // O cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORREO))
                "celular" to celular,
                "direccion" to direccion,
                "contrasena" to contrasenaDB,// Usar la contraseña obtenida (si la quieres devolver)
                "rol" to rol
            )
        }

        cursor.close()
        db.close()
        return usuario
    }

    //UPDATE
    fun actualizarUsuario(id: Int, nuevoCorreo: String, nuevaContrasena: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CORREO, nuevoCorreo)
            put(COLUMN_CONTRASENA, nuevaContrasena)
        }

        val filasAfectadas = db.update(
            TABLE_USUARIO,
            values,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return filasAfectadas
    }

    //DELETE
    fun eliminarUsuario(id: Int): Int {
        val db = writableDatabase
        val filasEliminadas = db.delete(
            TABLE_USUARIO,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return filasEliminadas
    }
}
