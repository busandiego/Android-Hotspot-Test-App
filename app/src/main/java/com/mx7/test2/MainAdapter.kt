package com.mx7.test2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mx7.test2.hotspotmanager.ClientScanResult

private const val TAG = "MainAdapter"
class MainAdapter(var list: MutableList<String>, var context: Context) :
    RecyclerView.Adapter<MainAdapter.MainAdapterHolder>() {

    lateinit var listener: RecyclerViewItemClickListener

    fun setItemClickListener(listener: RecyclerViewItemClickListener) {
        this.listener = listener
    }

    class MainAdapterHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)) {

        val ipAddrView: TextView = itemView.findViewById(R.id.item_text_tv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapterHolder {


        return MainAdapterHolder(parent)
    }

    override fun onBindViewHolder(holder: MainAdapterHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: >>>> on bind ${position}, ")
        Log.d(TAG, "list[position]: >>>> on bind ${list[position]}, ")

        holder.ipAddrView.text = list[position]
        holder.ipAddrView.setOnClickListener {
             val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://${list[position]}"))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}