package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.databinding.ItemPvpLikedBinding
import cn.wthee.pcrtool.ui.tool.pvp.PvpResultDialogFragment
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.dp


class PvpLikedAdapter(
    private val isFloat: Boolean
) :
    ListAdapter<PvpLikedData, PvpLikedAdapter.ViewHolder>(PvpLikedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPvpLikedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemPvpLikedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PvpLikedData) {
            //设置数据
            binding.apply {
                if (data.type == 1) {
                    typeImg.visibility = View.VISIBLE
                } else {
                    typeImg.visibility = View.GONE
                }
                atkIds.text = data.atks
                defIds.text = data.defs
                type.text = data.type.toString()
                val adapter = PvpCharacterResultItemAdapter()
                likedCharacters.adapter = adapter
                adapter.submitList(data.getIds())
                val params = root.layoutParams as RecyclerView.LayoutParams
                val listParams = likedCharacters.layoutParams as ConstraintLayout.LayoutParams
                if (isFloat) {
                    params.marginStart = 1.dp
                    params.marginEnd = 1.dp
                    atk.visibility = View.GONE
                    def.visibility = View.GONE
                    listParams.matchConstraintPercentWidth = 1f
                } else {
                    params.marginStart = 6.dp
                    params.marginEnd = 6.dp
                    atk.visibility = View.VISIBLE
                    def.visibility = View.VISIBLE
                    listParams.matchConstraintPercentWidth = 0.85f
                }
                root.layoutParams = params
                likedCharacters.layoutParams = listParams
                //点击重新查询
                layoutLiked.setOnClickListener {
                    if (!isFloat) {
                        PvpResultDialogFragment.getInstance(data.defs).show(
                            ActivityUtil.instance.currentActivity!!.supportFragmentManager,
                            "pvp_result"
                        )
                    }
                }
            }
        }
    }
}

class PvpLikedDiffCallback : DiffUtil.ItemCallback<PvpLikedData>() {

    override fun areItemsTheSame(
        oldItem: PvpLikedData,
        newItem: PvpLikedData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PvpLikedData,
        newItem: PvpLikedData
    ): Boolean {
        return oldItem == newItem
    }
}
