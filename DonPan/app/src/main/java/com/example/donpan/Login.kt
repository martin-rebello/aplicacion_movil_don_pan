package com.example.donpan

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unidad3sql.db.DatabaseHelper
import com.google.android.material.textfield.TextInputLayout


//Integrantes: Martin Rebello y Maximiliano Opazo
class Login : AppCompatActivity() {

    private lateinit var DB: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        DB = DatabaseHelper(this)
        val ini = DB.writableDatabase

        val et_correo = findViewById<EditText>(R.id.correo_EditText)
        val et_contrasena = findViewById<EditText>(R.id.password_EditText)

        val lgn= findViewById<Button>(R.id.loginButton)

        lgn.setOnClickListener {
            val correo = et_correo.text.toString().trim()
            val contrasena = et_contrasena.text.toString().trim()

            val check = DB.obtenerUsuarioPorCorreoYContrasena(correo,contrasena)

            if (check != null){

                val userId = check["id"] as? Int // Obtenemos el ID del usuario

                if (userId != null) {
                    // Creamos o abrimos un archivo de preferencias llamado "USER_SESSION"
                    val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

                    // Guardamos el ID del usuario con la clave "CURRENT_USER_ID"
                    with (sharedPref.edit()) {
                        putInt("CURRENT_USER_ID", userId)
                        apply()
                    }
                }


                val rol = check["rol"] as? String

                if(rol == "admin"){
                    // 1. Inicia CrudPro
                    val intento = Intent(this, CrudPro::class.java)
                    startActivity(intento)
                } else {
                    // 2. Si el usuario existe pero NO es admin, inicia MainActivity
                    val intento = Intent(this, MainActivity::class.java)
                    startActivity(intento)
                }
                finish()
            } else {
                // 3. Si el usuario no existe
                Toast.makeText(this, "Correo o Contrasena son invalidos", Toast.LENGTH_SHORT).show()
            }
        }

        val rgtro= findViewById<Button>(R.id.registrarseButton)
        rgtro.setOnClickListener {
            val intento = Intent(this, Registrarse::class.java)
            startActivity(intento)
        }
    }
}
