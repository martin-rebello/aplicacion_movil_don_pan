package com.example.donpan

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unidad3sql.db.DatabaseHelper
import com.google.android.material.textfield.TextInputLayout


//Integrantes: Martin Rebello y Maximiliano Opazo
class CrudPro : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crud_producto)

        val btn_agregar= findViewById<Button>(R.id.btn_agregar)
        val btn_elim= findViewById<Button>(R.id.btn_eliminar)
        val btn_volver= findViewById<Button>(R.id.btn_volver_login)

        btn_agregar.setOnClickListener {

            val intento = Intent(this, AgrePro::class.java)
            startActivity(intento)

        }
        btn_elim.setOnClickListener {
            val intento = Intent(this, ElimPro::class.java)
            startActivity(intento)
        }
        btn_volver.setOnClickListener {
            val intento = Intent(this, Login::class.java)
            startActivity(intento)
        }
    }
}
