package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.HeaderBinding
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<DataItem, RecyclerView.ViewHolder>(ElectionDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(title: String, list: List<Election>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header(title))
                else -> listOf(DataItem.Header(title)) + list.map { DataItem.ElectionItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ElectionViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ElectionViewHolder -> {
                val item = getItem(position) as DataItem.ElectionItem
                holder.bind(item.election, clickListener)
            }
            is TextViewHolder -> {
                val item = getItem(position) as DataItem.Header
                holder.bind(item.title)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.ElectionItem -> ITEM_VIEW_TYPE_ITEM
        }
    }


}

//TODO: Create ElectionViewHolder
class ElectionViewHolder(private val binding: ViewholderElectionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Election, clickListener: ElectionListener) {
        binding.election = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }


    //TODO: Add companion object to inflate ViewHolder (from)
    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ViewholderElectionBinding.inflate(layoutInflater, parent, false)

            return ElectionViewHolder(binding)
        }
    }

}


class TextViewHolder(private val binding: HeaderBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(title: String) {
        binding.title = title
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): TextViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = HeaderBinding.inflate(layoutInflater, parent, false)
            return TextViewHolder(binding)
        }
    }
}

//TODO: Create ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<DataItem>() {

    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

}

//TODO: Create ElectionListener
class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}


sealed class DataItem {

    abstract val id: Int

    data class ElectionItem(val election: Election) : DataItem() {
        override val id: Int
            get() = election.id

    }

    data class Header(val title: String) : DataItem() {

        override val id: Int
            get() = Int.MIN_VALUE

    }

}