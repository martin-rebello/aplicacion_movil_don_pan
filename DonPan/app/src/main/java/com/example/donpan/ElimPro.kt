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

class ElimPro : AppCompatActivity() {



    private lateinit var DB: DatabaseHelper
    private lateinit var PB: ProductoRepository



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.delete_pro)



        DB = DatabaseHelper(this)
        PB = ProductoRepository(DB)

        val ini = DB.writableDatabase





        val et_nombre = findViewById<EditText>(R.id.nombre_pro_EditText2)

        val btn_dele = findViewById<Button>(R.id.btn_delete_db)



        btn_dele.setOnClickListener {

            val nombre = et_nombre.text.toString().trim()

            val resultado = PB.eliminarProducto(nombre)

            if (resultado == 1) {
                Toast.makeText(this, "Producto Eliminado Exitosamente", Toast.LENGTH_SHORT).show()
                val intento = Intent(this, CrudPro::class.java)
                startActivity(intento)
            } else {
                Toast.makeText(this, "Error: No se pudo eliminar el producto a la DB.", Toast.LENGTH_LONG).show()
            }
        }
    }
}