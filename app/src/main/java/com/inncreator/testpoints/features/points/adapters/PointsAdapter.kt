package com.inncreator.testpoints.features.points.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inncreator.testpoints.R
import com.inncreator.testpoints.domain.models.ProcessedPoint

class PointsAdapter(
    private val points: List<ProcessedPoint>,
    private val minWidth: Triple<Int, Int, Int>?
) :
    RecyclerView.Adapter<PointsAdapter.PointViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point, parent, false)
        return PointViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(points[position], minWidth)
    }

    override fun getItemCount(): Int = points.size

    class PointViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val xTextView: TextView = itemView.findViewById(R.id.text_x)
        private val yTextView: TextView = itemView.findViewById(R.id.text_y)
        private val counterTextView: TextView = itemView.findViewById(R.id.counter)


        @SuppressLint("DefaultLocale")
        fun bind(point: ProcessedPoint, minWidth: Triple<Int, Int, Int>?) {
            minWidth?.let {
                counterTextView.minWidth =
                    it.first + counterTextView.paddingStart + counterTextView.paddingEnd
                xTextView.minWidth = it.second + xTextView.paddingStart + xTextView.paddingEnd
                yTextView.minWidth = it.third + yTextView.paddingStart + yTextView.paddingEnd
            }

            xTextView.text = String.format("%.2f", point.x)
            yTextView.text = String.format("%.2f", point.y)
            counterTextView.text = point.counter.toString()
        }
    }
}