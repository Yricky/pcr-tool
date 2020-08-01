package cn.wthee.pcrtool.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdd
import cn.wthee.pcrtool.databinding.ItemEquipmentDropDetailBinding
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.GlideUtil


class EquipmentDropDetailAdapter(private val eid: Int) :
    ListAdapter<EquipmentIdWithOdd, EquipmentDropDetailAdapter.ViewHolder>(OddDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentDropDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), eid)
    }

    inner class ViewHolder(private val binding: ItemEquipmentDropDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(equip: EquipmentIdWithOdd, eid: Int) {
            //设置数据
            binding.apply {
                //装备名称
                odd.text = equip.odd.toString() + "%"
                //加载装备图片
                val picUrl = EQUIPMENT_URL + equip.eid + WEBP
                GlideUtil.load(picUrl, itemPic, R.drawable.error, null)
                if (eid == equip.eid) {
                    odd.setTextColor(Color.RED)
                }
            }
        }
    }

}

class OddDiffCallback : DiffUtil.ItemCallback<EquipmentIdWithOdd>() {

    override fun areItemsTheSame(
        oldItem: EquipmentIdWithOdd,
        newItem: EquipmentIdWithOdd
    ): Boolean {
        return oldItem.eid == newItem.eid
    }

    override fun areContentsTheSame(
        oldItem: EquipmentIdWithOdd,
        newItem: EquipmentIdWithOdd
    ): Boolean {
        return oldItem == newItem
    }
}