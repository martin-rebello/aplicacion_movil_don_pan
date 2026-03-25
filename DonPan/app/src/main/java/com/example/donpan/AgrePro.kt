package com.example.donpan



import android.content.Intent

import android.os.Bundle

import android.widget.Button

import android.widget.EditText

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.example.donpan.db.ProductoRepository

import com.example.unidad3sql.db.DatabaseHelper

import com.google.android.material.textfield.TextInputLayout





//Integrantes: Martin Rebello y Maximiliano Opazo

class AgrePro : AppCompatActivity() {



    private lateinit var DB: DatabaseHelper
    private lateinit var PB: ProductoRepository



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.agre_pro)



        DB = DatabaseHelper(this)
        PB = ProductoRepository(DB)

        val ini = DB.writableDatabase





        val et_nombre = findViewById<EditText>(R.id.nombre_pro_EditText)

        val et_descripcion = findViewById<EditText>(R.id.descripcion_EditText)

        val et_precio = findViewById<EditText>(R.id.precio_EditText)

        val et_imagen = findViewById<EditText>(R.id.imagen_EditText)

        val btn_agre = findViewById<Button>(R.id.btn_agregar_db)



        btn_agre.setOnClickListener {

            val nombre = et_nombre.text.toString().trim()

            val descripcion = et_descripcion.text.toString().trim()

            val precioString = et_precio.text.toString().trim()

            val imagen = et_imagen.text.toString().trim()

            val precioInt = precioString.toInt()



            val resultado = PB.insertarProducto(nombre, descripcion, precioInt, imagen)

            if (resultado != -1L) {
                Toast.makeText(this, "Producto Agregado Exitosamente", Toast.LENGTH_SHORT).show()
                val intento = Intent(this, CrudPro::class.java)
                startActivity(intento)
            } else {
                Toast.makeText(this, "Error: No se pudo agregar el producto a la DB.", Toast.LENGTH_LONG).show()
            }

            Toast.makeText(this, "Producto Agregado Exitosamente", Toast.LENGTH_SHORT).show()

        }



    }

}