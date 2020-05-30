package com.example.sl_terms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sl_terms.R
import com.example.sl_terms.adapters.RVAdapter.PersonViewHolder
import com.example.sl_terms.models.Option
import kotlinx.android.synthetic.main.item.view.*

class RVAdapter internal constructor(var options: List<Option>) : RecyclerView.Adapter<PersonViewHolder>() {
    class PersonViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.cv
        var questionOption: TextView = itemView.question_option

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PersonViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item, viewGroup, false)
        return PersonViewHolder(v)
    }

    override fun onBindViewHolder(personViewHolder: PersonViewHolder, i: Int) {
        personViewHolder.questionOption.text = options[i].name
    }

    override fun getItemCount(): Int {
        return options.size
    }

}