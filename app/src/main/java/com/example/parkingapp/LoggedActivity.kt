package com.example.parkingapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.example.parkingapp.models.Parqueo
import com.example.parkingapp.models.Usuario
import com.example.parkingapp.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
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

        filterNavBar.getHeaderView(0).nombre_usuario.text = usuario.username

        car_icon.setOnClickListener {
            this.drawer_layout.openDrawer(Gravity.LEFT)
        }

        filterNavBar.setNavigationItemSelectedListener{
            it.isChecked = true
            drawer_layout.closeDrawers()
            when (it.itemId){
                R.id.logout->{
                    val builder = android.app.AlertDialog.Builder(this)
                    builder.setTitle("Cerrar Sesión")
                    builder.setMessage("Desea cerrar sesión como ${usuario.username}?")
                    builder.setPositiveButton("Si"){dialog, which ->
                        val intent = Intent(this,MainActivity::class.java)
                        intent.flags = (
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                        Intent.FLAG_ACTIVITY_NEW_TASK )
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
                    }
                    builder.setNeutralButton("Cancelar"){dialog, which ->
                    }
                    val dialog: android.app.AlertDialog = builder.create()
                    dialog.show()

                }

            }
            true
        }

        findParkingSpot.setOnClickListener { view ->
            findParkingSpot.isEnabled = false
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
                            findParkingSpot.isEnabled = true
                        }
                    }
            }else{
                Snackbar.make(
                        view,
                        "No tienes un parqueo reservado",
                        Snackbar.LENGTH_SHORT
                    )
                    .setAction("Action", null).show()
                findParkingSpot.isEnabled = true
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.drawer_layout.openDrawer(GravityCompat.START)
        return true
    }

}