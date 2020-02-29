package com.example.parkingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.parkingapp.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class MainActivity : AppCompatActivity() {

    val NUMBER_TAG = "numero"
    val SECTION_TAG = "seccion"
    val LOCATION_TAG = "location"
    val AVAILABLE_TAG = "available"
    val location = "UNITEG TGU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        btn_LogIn.setOnClickListener {
            var encontrado = false
            this.progress_bar.visibility = View.VISIBLE
            if(this.error.isVisible)
                this.error.visibility = View.INVISIBLE
            val context = this
            FirebaseFirestore.getInstance()
                .collection("Usuarios")
                .whereEqualTo("username", ET_usuario.text.toString())
                .whereEqualTo("password", ET_contra.text.toString())
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        login(
                            Usuario(
                                document.get("username").toString(),
                                document.get("password").toString(),
                                document.get("parqueo_reservado").toString()
                            ), context
                        )
                        encontrado = true
                    }
                    if (result.isEmpty) {
                        this.error.visibility = View.VISIBLE
                        this.progress_bar.visibility = View.INVISIBLE
                    }

                }.addOnFailureListener { exception ->
                    val myToast = Toast.makeText(
                        applicationContext,
                        "Inicio de Sesión Fallido: " + ET_usuario.text.toString()
                        ,
                        Toast.LENGTH_SHORT
                    )
                    myToast.setGravity(Gravity.LEFT, 200, 200)
                    myToast.show()
                }
        }
    }

    fun login(usuario: Usuario, context: Context) {
        animation(usuario, Intent(context, LoggedActivity::class.java))
    }

    fun animation(usuario: Usuario, intent: Intent) {

        this.progress_bar.visibility = View.GONE

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.duration = 300

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                ET_usuario.setVisibility(View.GONE)
                UsuarioInput.visibility = View.GONE

                ET_contra.setVisibility(View.GONE)
                ContraInput.visibility = View.GONE

                btn_LogIn.setVisibility(View.GONE)

                error.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
        })

        val animSlide = AnimationUtils.loadAnimation(
            getApplicationContext(),
            R.anim.logo_slide_center
        )
        this.dinant_logo.startAnimation(animSlide)

        ET_usuario.startAnimation(fadeOut)
        UsuarioInput.startAnimation(fadeOut)
        ET_contra.startAnimation(fadeOut)
        ContraInput.startAnimation(fadeOut)
        btn_LogIn.startAnimation(fadeOut)
        if (error.isVisible) {
            error.startAnimation(fadeOut)
        }

        val fadeOut3 = AnimationUtils.loadAnimation(
            getApplicationContext(),
            R.anim.fade_out_slow
        )

        fadeOut3.startOffset = 1000

        fadeOut3.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                dinant_logo.setVisibility(View.GONE)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("usuario", usuario)
                ET_contra.setText("")
                ET_usuario.setText("")
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.nothing)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
        })

        animSlide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                dinant_logo.startAnimation(fadeOut3)
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
        })
    }

}