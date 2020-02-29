package com.example.parkingapp

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingapp.models.Parqueo
import com.example.parkingapp.models.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.parking_spot.*
import java.io.Serializable

class VerInfoParqueo : AppCompatActivity() {

    val NUMBER_TAG = "numero"
    val SECTION_TAG = "seccion"
    val LOCATION_TAG = "location"
    val AVAILABLE_TAG = "available"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parking_spot)
        val parqueo = intent.extras!!.get("parqueo") as Parqueo
        val usuario = intent.extras!!.get("usuario") as Usuario
        this.num_parqueo.text = parqueo.numero
        this.seccion.text = parqueo.seccion
        this.establecimiento.text = parqueo.establecimiento

        if(!parqueo.disponible){
            this.disponible.setTextColor(
                Color.parseColor("#D80104")
            )
            this.disponible.text = "OCUPADO"
        }
        this.reservar.setOnClickListener {
            if(parqueo!!.disponible){
                reservarParqueo(parqueo, usuario)
            }else{
                Snackbar.make(
                        it,
                        "El parqueo no se encuentra disponible",
                        Snackbar.LENGTH_SHORT
                    )
                    .setAction("Action", null).show()
            }
        }

        if(!usuario.parqueo_reservado.isEmpty()){
            val data = usuario.parqueo_reservado.split("-")
            if(data[0].equals(parqueo.numero) && data[1].equals(parqueo.seccion)){
                this.cancelar.visibility = View.VISIBLE
                this.reservar.visibility = View.INVISIBLE
            }
        }

        this.cancelar.setOnClickListener {
            cancelarReserva(parqueo, usuario)
        }
    }

    fun cancelarReserva(parqueo: Parqueo, usuario: Usuario){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reservar")
        builder.setMessage("Desea cancelar la reserva del parqueo "
                + parqueo.numero
                + "?"
        )

        //builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(COLOR_I_WANT)

        builder.setPositiveButton("Si"){dialog, which ->

            FirebaseFirestore.getInstance()
                .collection("Parqueos")
                .document("${parqueo.numero}-${parqueo.seccion}")
                .update("disponible",true)

            FirebaseFirestore.getInstance()
                .collection("Usuarios")
                .whereEqualTo("username", usuario.username)
                .get()
                .addOnSuccessListener {
                    for(document in it)
                        FirebaseFirestore.getInstance()
                            .collection("Usuarios")
                            .document(document.id)
                            .update("parqueo_reservado", "")
                }

            Toast.makeText(this, "Reserva Cancelada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoggedActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            usuario.parqueo_reservado = ""
            intent.putExtra("usuario", usuario as Serializable)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.nothing,R.anim.slide_out_down)
        }
        builder.setNeutralButton("Cancelar"){dialog, which ->
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun reservarParqueo(parqueo:Parqueo, usuario:Usuario){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reservar")
        builder.setMessage("Desea reservar el parqueo "
                + parqueo.numero
                + "?"
        )

        //builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(COLOR_I_WANT)

        builder.setPositiveButton("Si"){dialog, which ->

            FirebaseFirestore.getInstance()
                .collection("Parqueos")
                .document("${parqueo.numero}-${parqueo.seccion}")
                .update("disponible",false)

            FirebaseFirestore.getInstance()
                .collection("Usuarios")
                .whereEqualTo("username", usuario.username)
                .get()
                .addOnSuccessListener {
                    for(document in it)
                    FirebaseFirestore.getInstance()
                        .collection("Usuarios")
                        .document(document.id)
                        .update("parqueo_reservado", "${parqueo.numero}-${parqueo.seccion}")
                }

            Toast.makeText(this, "Parqueo Reservado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoggedActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            usuario.parqueo_reservado = "${parqueo.numero}-${parqueo.seccion}"
            intent.putExtra("usuario", usuario as Serializable)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.nothing,R.anim.slide_out_down)
        }
        builder.setNeutralButton("Cancelar"){dialog, which ->
            Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.nothing,R.anim.slide_out_down)
    }
}