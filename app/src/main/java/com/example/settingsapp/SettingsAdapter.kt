package com.example.settingsapp

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SettingsAdapter(
    private var items: List<SettingsItem>,
    private val onClick: (SettingsItem) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val root: android.view.View = view
        val iconBg: android.view.View = view.findViewById(R.id.iconBg)
        val iconEmoji: TextView = view.findViewById(R.id.iconEmoji)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val subtitleText: TextView = view.findViewById(R.id.subtitleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        holder.subtitleText.text = item.subtitle
        holder.iconEmoji.text = item.emoji

        val bg = holder.iconBg.background.mutate() as GradientDrawable
        bg.setColor(Color.parseColor(item.colorHex))

        val topMarginDp = if (item.isSectionStart && position != 0) 18 else 0
        val density = holder.root.resources.displayMetrics.density
        val lp = holder.root.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = (topMarginDp * density).toInt()
        holder.root.layoutParams = lp

        holder.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<SettingsItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
