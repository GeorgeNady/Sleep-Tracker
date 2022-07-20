package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ItemSleepHeaderBinding
import com.example.android.trackmysleepquality.databinding.ItemSleepNightGridBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_SLEEP = 1

class SleepNightAdapter(val clickListener: SleepNightCLickLinter) : ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallBack()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        ITEM_VIEW_TYPE_HEADER -> SleepHeaderViewHolder.from(parent)
        ITEM_VIEW_TYPE_SLEEP -> SleepNightViewHolder.from(parent)
        else -> throw ClassCastException("Unknown viewType $viewType")
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
        is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_SLEEP
    }

    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        adapterScope.launch {
            val twoHeadersList = listOf(DataHeader("header1"), DataHeader("header2"))
            val singleHeader = listOf(DataHeader("header"))
            val items = when(list) {
                null -> singleHeader
                else -> singleHeader + list.map { DataSleep(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SleepHeaderViewHolder -> {
                val item = getItem(position) as DataItem.Header
                holder.bind(item.headerMessage)
            }
            is SleepNightViewHolder -> {
                val item = getItem(position) as DataItem.SleepNightItem
                holder.bind(item.sleepNight, clickListener)
            }
        }
    }


}

class SleepHeaderViewHolder(
    private val binding: ItemSleepHeaderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(headerMessage: String) {
        binding.apply {
            bMessage = headerMessage
        }
    }

    companion object {
        fun from(parent: ViewGroup) : RecyclerView.ViewHolder = SleepHeaderViewHolder(
            ItemSleepHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }
}

class SleepNightViewHolder private constructor(
    private val binding: ItemSleepNightGridBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SleepNight, clickListener: SleepNightCLickLinter) {
        binding.apply {
            sleep = item
            this.clickListener = clickListener
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): RecyclerView.ViewHolder = SleepNightViewHolder(
            ItemSleepNightGridBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

}

class SleepNightDiffCallBack : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem) =
        oldItem == newItem
}

class SleepNightCLickLinter(val clickListener: (sleep: SleepNight) -> Unit) {
    fun onCLick(sleep:SleepNight) = clickListener(sleep)
}


private typealias DataHeader = DataItem.Header
private typealias DataSleep = DataItem.SleepNightItem

sealed class DataItem {
    data class Header(val headerMessage:String) : DataItem() {
        override val id get() = ITEM_VIEW_TYPE_HEADER
    }

    data class SleepNightItem(val sleepNight: SleepNight) : DataItem() {
        override val id get() = ITEM_VIEW_TYPE_SLEEP
    }

    abstract val id:Int
}

