package com.example.parkingapp.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingapp.ListarParqueosAdapter
import com.example.parkingapp.LoggedActivity
import com.example.parkingapp.R
import com.example.parkingapp.VerInfoParqueo
import com.example.parkingapp.models.Parqueo
import com.example.parkingapp.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.io.Serializable

class ParkingFrament : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_main,
            container,
            false
        )
        actualizar_parqueo(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_container.setOnRefreshListener {
            view.progress_bar.visibility = View.VISIBLE
            actualizar_parqueo(view)
            swipe_container.isRefreshing = false
        }
    }

    companion object{


        fun newInstance(usuario: Usuario):ParkingFrament{

            val fragment = ParkingFrament()
            val args = Bundle()

            args.putSerializable("usuario",usuario)

            fragment.arguments =  args
            return fragment

        }

    }

    fun actualizar_parqueo(view: View){
        FirebaseFirestore.getInstance()
            .collection("Parqueos")
            .get()
            .addOnSuccessListener { result ->
                if(!result.isEmpty) {
                    for (document in result) {
                        if(!document.get("disponible").toString().equals("true")) {
                            if(document.get("numero").toString().equals("001")&&document.get("seccion").toString().equals("A")){
                                view.A1.setBackgroundColor(Color.RED)
                            }
                            if(document.get("numero").toString().equals("002")&&document.get("seccion").toString().equals("A")){
                                view.A2.setBackgroundColor(Color.RED)
                            }
                            if(document.get("numero").toString().equals("001")&&document.get("seccion").toString().equals("B")){
                                view.B1.setBackgroundColor(Color.RED)
                            }
                            if(document.get("numero").toString().equals("002")&&document.get("seccion").toString().equals("B")){
                                view.B2.setBackgroundColor(Color.RED)
                            }
                        }else{
                            if(document.get("numero").toString().equals("001")&&document.get("seccion").toString().equals("A")){
                                view.A1.setBackgroundColor(Color.GREEN)
                            }
                            if(document.get("numero").toString().equals("002")&&document.get("seccion").toString().equals("A")){
                                view.A2.setBackgroundColor(Color.GREEN)
                            }
                            if(document.get("numero").toString().equals("001")&&document.get("seccion").toString().equals("B")){
                                view.B1.setBackgroundColor(Color.GREEN)
                            }
                            if(document.get("numero").toString().equals("002")&&document.get("seccion").toString().equals("B")){
                                view.B2.setBackgroundColor(Color.GREEN)
                            }
                        }
                    }
                }
                view.B3.setBackgroundColor(Color.GREEN)
                view.A3.setBackgroundColor(Color.GREEN)

                view.A1.setOnClickListener{
                    reserva("001-A", arguments!!.get("usuario") as Usuario)
                }
                view.A2.setOnClickListener{
                    reserva("002-A", arguments!!.get("usuario") as Usuario)
                }
                view.B1.setOnClickListener{
                    reserva("001-B", arguments!!.get("usuario") as Usuario)
                }
                view.B2.setOnClickListener{
                    reserva("002-B", arguments!!.get("usuario") as Usuario)
                }
                view.progress_bar.visibility = View.INVISIBLE

            }
    }

    fun reserva(parqueo:String,usuario:Usuario){
        val db= FirebaseFirestore.getInstance()
        db.collection("Parqueos")
            .document(parqueo)
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
                    val intent = Intent(context,VerInfoParqueo::class.java)
                    intent.putExtra("usuario",usuario as Serializable)
                    intent.putExtra("parqueo", parqueo as Serializable)
                    startActivity(intent)
                    activity!!.overridePendingTransition(R.anim.slide_in_up,R.anim.nothing)
                }
            }
    }
}