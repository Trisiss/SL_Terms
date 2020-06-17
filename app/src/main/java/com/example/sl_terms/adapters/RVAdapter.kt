package com.example.sl_terms.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sl_terms.ItemMoveCallback.ItemTouchHelperContract
import com.example.sl_terms.R
import com.example.sl_terms.models.Option
import kotlinx.android.synthetic.main.item.view.*
import java.util.*


class RVAdapter internal constructor(var options: List<Option>) : RecyclerView.Adapter<RVAdapter.OptionViewHolder>(), ItemTouchHelperContract {
    class OptionViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.cv
        var rowView: View = itemView
        var questionOption: TextView = itemView.question_option

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): OptionViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)
        return OptionViewHolder(v)
    }

    override fun onBindViewHolder(optionViewHolder: OptionViewHolder, i: Int) {
        optionViewHolder.questionOption.text = options[i].name
        optionViewHolder.cv.id = options[i].id
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(options, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(options, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: OptionViewHolder) {
        myViewHolder.cv.setCardBackgroundColor(Color.GRAY)
    }

    override fun onRowClear(myViewHolder: OptionViewHolder) {
        myViewHolder.cv.setCardBackgroundColor(Color.WHITE)
    }


}