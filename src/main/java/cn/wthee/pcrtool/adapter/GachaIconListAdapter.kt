package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.enums.PageType
import cn.wthee.pcrtool.ui.common.ContainerFragment
import cn.wthee.pcrtool.utils.Constants
import coil.load


class GachaListAdapter(
    private val manager: FragmentManager
) : ListAdapter<Int, GachaListAdapter.ViewHolder>(GachaListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uid: Int) {
            //设置数据
            binding.apply {
                val itemParams = root.layoutParams
                itemParams.width = RecyclerView.LayoutParams.WRAP_CONTENT
                root.layoutParams = itemParams
                //角色图片
                val picUrl = Constants.UNIT_ICON_URL + (uid + 30) + Constants.WEBP
                pic.load(picUrl) {
                    placeholder(R.drawable.unknown_gray)
                    error(R.drawable.error)
                }
                //角色名
                name.visibility = View.GONE
                pic.setOnClickListener {
                    ContainerFragment.getInstance(uid, PageType.CAHRACTER_SKILL).show(
                        manager,
                        "skill"
                    )
                }
            }
        }
    }

}

private class GachaListDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }
}