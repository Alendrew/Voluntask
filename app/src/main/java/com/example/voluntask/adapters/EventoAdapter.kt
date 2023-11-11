package com.example.voluntask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.request.RequestOptions
import com.example.voluntask.R
import com.example.voluntask.databinding.ResItemEventoBinding
import com.example.voluntask.models.Evento

class EventoAdapter(private val onItemClicked: (Evento) -> Unit) : Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: ResItemEventoBinding
    private var items : List<Evento> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ResItemEventoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = binding.root
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is VideoViewHolder -> {
                holder.bind(items[position], onItemClicked)
            }
        }

    }

    fun setEventoList(items : List<Evento>) {
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    class VideoViewHolder constructor(
        itemView: View
    ):RecyclerView.ViewHolder(itemView){
        private val binding: ResItemEventoBinding = ResItemEventoBinding.bind(itemView)

        fun bind(evento: Evento, onItemClicked: (Evento) -> Unit){
            binding.name.text = evento.nome
            binding.descricao.text = evento.descricao

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.baseline_auto_awesome_24)
                .error(R.drawable.baseline_error_24)

            itemView.setOnClickListener{
                onItemClicked(evento)
            }

        }
    }
}
