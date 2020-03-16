package com.example.parkingapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingapp.ListarParqueosAdapter
import com.example.parkingapp.LoggedActivity
import com.example.parkingapp.R
import com.example.parkingapp.models.Parqueo
import com.example.parkingapp.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.listar_parqueos_frag.*

class ListarParqueos : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.listar_parqueos_frag,
            container,
            false
        )
        cargarParqueos()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_container.setOnRefreshListener {
            this.progress_bar.visibility = View.VISIBLE
            cargarParqueos()
            swipe_container.isRefreshing = false
        }
    }

    companion object{
        fun newInstance(usuario: Usuario):ListarParqueos{
            val fragment = ListarParqueos()
            val args = Bundle()
            args.putSerializable("usuario",usuario)
            fragment.arguments =  args
            return fragment
        }
    }
    fun cargarParqueos(){
        //progress_bar.visibility = View.VISIBLE
        FirebaseFirestore.getInstance()
            .collection("Parqueos")
            .get()
            .addOnSuccessListener { result ->
                if(!result.isEmpty) {
                    val parqueos = ArrayList<Parqueo>()
                    for (document in result) {
                        parqueos.add(
                            Parqueo(
                                document.get("disponible").toString().toBoolean(),
                                document.get("numero").toString(),
                                document.get("seccion").toString(),
                                document.get("establecimiento").toString(),
                                document.get("codigo_reserva").toString()
                            )
                        )
                    }
                    val layoutManager = LinearLayoutManager(this.activity)
                    layoutManager.orientation = LinearLayoutManager.VERTICAL
                    recycler.layoutManager = layoutManager
                    val adapter = ListarParqueosAdapter(
                        this.activity as Context,
                        parqueos,
                        recycler,
                        this.activity as LoggedActivity,
                        arguments?.getSerializable("usuario") as Usuario
                    )
                    recycler.adapter = adapter
                }
                progress_bar.visibility = View.INVISIBLE
            }
    }
}