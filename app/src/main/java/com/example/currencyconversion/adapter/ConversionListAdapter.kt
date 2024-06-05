package com.example.currencyconversion.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconversion.R
import com.example.currencyconversion.models.EndResult

class ConversionListAdapter(private val resultList: List<EndResult>) : RecyclerView.Adapter<ConversionListAdapter.ConversionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversionListAdapter.ConversionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversion, parent, false)
        return ConversionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ConversionListAdapter.ConversionViewHolder,
        position: Int
    ) {
        val endResult = resultList[position]
        holder.bind(endResult)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ConversionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyTextView: TextView = itemView.findViewById(R.id.currencyTextView)
        val resultTextView: TextView = itemView.findViewById(R.id.resultTextView)

        fun bind(endResult: EndResult) {
            currencyTextView.text = endResult.currency
            resultTextView.text = endResult.result?.toString() ?: "N/A"
        }
    }
}
