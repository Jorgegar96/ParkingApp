package com.example.parkingapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.parkingapp.models.Parqueo
import com.example.parkingapp.models.Usuario
import com.example.parkingapp.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class LoggedActivity : AppCompatActivity() {
    val NUMBER_TAG = "numero"
    val SECTION_TAG = "seccion"
    val LOCATION_TAG = "location"
    val AVAILABLE_TAG = "available"
    val location = "UNITEG TGU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val usuario = intent.extras!!.get("usuario") as Usuario
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, usuario)
        val viewPager: ViewPager = findViewById(R.id.view_pager1)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val findParkingSpot: FloatingActionButton = findViewById(R.id.findParkingSpot)


        findParkingSpot.setOnClickListener { view ->
            /*
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

             */


            if(!usuario.parqueo_reservado.isEmpty()){
                FirebaseFirestore.getInstance()
                    .collection("Parqueos")
                    .document("${usuario.parqueo_reservado}")
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null){
                            val parqueo = Parqueo(
                                document.get("disponible").toString().toBoolean(),
                                document.get("numero").toString(),
                                document.get("seccion").toString(),
                                document.get("establecimiento").toString(),
                                document.get("codigo_reserva").toString()
                            )
                            val intent = Intent(this@LoggedActivity,VerInfoParqueo::class.java)
                            intent.putExtra("usuario",usuario as Serializable)
                            intent.putExtra("parqueo", parqueo as Serializable)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_up,R.anim.nothing)
                        }
                    }
            }else{
                Snackbar.make(
                        view,
                        "No tienes un parqueo reservado",
                        Snackbar.LENGTH_SHORT
                    )
                    .setAction("Action", null).show()
            }

        }
    }
}