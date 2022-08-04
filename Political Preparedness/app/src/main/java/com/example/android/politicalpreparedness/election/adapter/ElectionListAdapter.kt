package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(from(parent))
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    //TODO: Add companion object to inflate ViewHolder (from)
    companion object {
        fun from(parent: ViewGroup): ViewholderElectionBinding {
            return ViewholderElectionBinding.inflate(LayoutInflater.from(parent.context))
        }
    }
}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(private val binding: ViewholderElectionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(election: Election) {
        binding.election = election
        binding.executePendingBindings()
    }
}

//TODO: Create ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {

    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

}

//TODO: Create ElectionListener
class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}