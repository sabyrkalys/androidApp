package com.example.settingsapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SettingsAdapter(
    private val items: List<SettingsItem>,
    private val onClick: (SettingsItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    class ViewHolder(val titleText: TextView) : RecyclerView.ViewHolder(titleText)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        holder.titleText.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
