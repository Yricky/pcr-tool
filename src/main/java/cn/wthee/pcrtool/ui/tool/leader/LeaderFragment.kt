package cn.wthee.pcrtool.ui.tool.leader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterLeaderAdapter
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolLeaderBinding
import cn.wthee.pcrtool.enums.Response
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class LeaderFragment : Fragment() {

    private lateinit var binding: FragmentToolLeaderBinding
    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolLeaderBinding.inflate(inflater, container, false)
        job = MainScope().launch {
            val list = MyAPIRepository.getLeader()
            if (list.status == Response.SUCCESS) {
                val adapter = CharacterLeaderAdapter()
                binding.listLevel.adapter = adapter
                adapter.submitList(list.data) {
                    binding.loading.root.visibility = View.GONE
                }
            } else if (list.status == Response.FAILURE) {
                ToastUtil.short(list.message)
            }
        }
        //设置头部
        binding.toolLeader.apply {
            toolIcon.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.ic_leader))
            toolTitle.text = getString(R.string.tool_leader)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}