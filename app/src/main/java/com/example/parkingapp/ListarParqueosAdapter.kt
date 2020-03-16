package com.example.parkingapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingapp.models.Parqueo
import com.example.parkingapp.models.Usuario
import kotlinx.android.synthetic.main.parqueo_card.view.*
import java.io.Serializable

class ListarParqueosAdapter(
    val context: Context,
    val parqueos: ArrayList<Parqueo>,
    val recyclerView: RecyclerView,
    val activity: LoggedActivity,
    val usuario: Usuario
) :   RecyclerView.Adapter<ListarParqueosAdapter.MyViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.parqueo_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parqueos.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val parqueo = parqueos[position]
        holder.setData(parqueo, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var currentParkingSpot: Parqueo? = null
        var currentPosition: Int = 0
        init{
            itemView.setOnClickListener {
                itemView.isEnabled = false
                val intent = Intent(context, VerInfoParqueo::class.java)
                intent.putExtra("parqueo", currentParkingSpot as Serializable)
                intent.putExtra("usuario", usuario as Serializable)
                context.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_up,R.anim.nothing)
                itemView.isEnabled = true
            }

        }
        fun setData(parqueo : Parqueo?, position: Int){
            itemView.numero.text = "${parqueo!!.numero}-${parqueo!!.seccion}"
            if (!parqueo!!.disponible)
                itemView.ocupado.visibility = View.VISIBLE
            this.currentParkingSpot = parqueo
            this.currentPosition = position
        }
    }
}

