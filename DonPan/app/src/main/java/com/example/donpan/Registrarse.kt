package com.example.donpan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unidad3sql.db.DatabaseHelper
import com.google.android.material.textfield.TextInputLayout


//Integrantes: Martin Rebello y Maximiliano Opazo
class Registrarse : AppCompatActivity() {

    private lateinit var DB: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrarse)

        DB = DatabaseHelper(this)
        val ini = DB.writableDatabase


        val et_nombre = findViewById<EditText>(R.id.nombre_rgtro_EditText)
        val et_correo = findViewById<EditText>(R.id.correo_rgtro_EditText)
        val et_numero = findViewById<EditText>(R.id.numero_rgtro_EditText)
        val et_direccion = findViewById<EditText>(R.id.direccion_rgtro_EditText)
        val et_contrasena = findViewById<EditText>(R.id.password_rgtro_EditText)
        val btn_rgtro = findViewById<Button>(R.id.rgtro_Button)

        btn_rgtro.setOnClickListener {
            val nombre = et_nombre.text.toString().trim()
            val celular = et_numero.text.toString().trim()
            val direccion = et_direccion.text.toString().trim()
            val correo = et_correo.text.toString().trim()
            val contrasena = et_contrasena.text.toString().trim()
            val rol = "cliente"

            DB.insertarUsuario(nombre,correo,celular,direccion,contrasena,rol)

            val intento = Intent(this, Login::class.java)
            startActivity(intento)
            Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show()
        }

    }
}
