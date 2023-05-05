package com.adz.mlimivisual

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adz.mlimivisual.models.AppointmentModel


class MyAdapter(private val userList : ArrayList<AppointmentModel>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]

        holder.desc.text = currentitem.description
        holder.location.text = currentitem.location
        holder.num.text = currentitem.number

    }

    override fun getItemCount(): Int {

        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val desc : TextView = itemView.findViewById(R.id.description)
        val location : TextView = itemView.findViewById(R.id.location)
        val num : TextView = itemView.findViewById(R.id.number)

    }

}
