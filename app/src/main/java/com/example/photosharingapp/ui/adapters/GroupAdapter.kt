package com.example.photosharingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photosharingapp.data.repository.Group
import com.example.photosharingapp.databinding.ItemGroupBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupAdapter(private val onGroupClick: (Group) -> Unit) :
    ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onGroupClick(getItem(adapterPosition))
            }
        }

        fun bind(group: Group) {
            binding.tvGroupName.text = group.name
            binding.tvGroupType.text = if (group.isEvent) "Tipo: Evento" else "Tipo: No Evento"
            val date = Date(group.creationTime)
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvCreationTime.text = "Creado: ${formatter.format(date)}"
        }
    }
}

class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem == newItem
    }
}