package cn.wthee.pcrtool.ui.tool.pvp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.provider.Settings.canDrawOverlays
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterAdapter
import cn.wthee.pcrtool.adapters.viewpager.PvpCharacterPagerAdapter
import cn.wthee.pcrtool.data.view.PvpCharacterData
import cn.wthee.pcrtool.data.view.getDefault
import cn.wthee.pcrtool.databinding.FragmentToolPvpBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.io.Serializable


class PvpFragment : Fragment() {

    companion object {
        var selects = getDefault()
        var character1 = listOf<PvpCharacterData>()
        var character2 = listOf<PvpCharacterData>()
        var character3 = listOf<PvpCharacterData>()
        lateinit var progressBar: ProgressBar
        lateinit var selectedAdapter: PvpCharacterAdapter
    }

    private lateinit var binding: FragmentToolPvpBinding
    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolPvpBinding.inflate(inflater, container, false)
        progressBar = binding.pvpProgressBar
        //已选择角色
        loadDefault()
        //角色页面 绑定tab viewpager
        lifecycleScope.launch {
            character1 = viewModel.getCharacterByPosition(1)
            character2 = viewModel.getCharacterByPosition(2)
            character3 = viewModel.getCharacterByPosition(3)
            setPager()
        }
        //监听
        setListener()

        return binding.root
    }

    private fun setListener() {
        binding.apply {
            pvpSearch.setOnClickListener {
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    PvpResultDialogFragment().show(parentFragmentManager, "pvp")
                }
            }
            pcrfan.setOnClickListener {
                //从其他浏览器打开
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(getString(R.string.url_pcrdfans_com))
                startActivity(
                    Intent.createChooser(
                        intent,
                        "访问：${getString(R.string.url_pcrdfans_com)}"
                    )
                )
            }
            //设置头部
            toolPvp.apply {
                toolIcon.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.ic_pvp))
                toolTitle.text = getString(R.string.tool_pvp)
                rightIcon.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.ic_float))
                rightIcon.visibility = View.VISIBLE
            }
            //悬浮窗
            toolPvp.rightIcon.setOnClickListener {
                //检查是否已经授予权限
                if (!canDrawOverlays(requireContext())) {
                    //若未授权则请求权限
                    getOverlayPermission()
                } else {
                    val intent =
                        Intent(requireActivity().applicationContext, PvpService::class.java)
                    requireActivity().stopService(intent)
                    intent.putExtra("character1", character1 as Serializable)
                    intent.putExtra("character2", character2 as Serializable)
                    intent.putExtra("character3", character3 as Serializable)
                    requireActivity().startService(intent)
                    //退回桌面
                    val home = Intent(Intent.ACTION_MAIN)
                    home.addCategory(Intent.CATEGORY_HOME)
                    startActivity(home)
                }
            }
        }
    }

    private fun setPager() {
        binding.pvpPager.offscreenPageLimit = 3
        binding.pvpPager.adapter = PvpCharacterPagerAdapter(requireActivity(), false)
        TabLayoutMediator(
            binding.tablayoutPosition,
            binding.pvpPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.position_1)
                }
                1 -> {
                    tab.text = getString(R.string.position_2)
                }
                2 -> {
                    tab.text = getString(R.string.position_3)
                }
            }
        }.attach()
    }

    //已选择角色
    private fun loadDefault() {
        selectedAdapter = PvpCharacterAdapter(false, requireActivity())
        binding.selectCharacters.adapter = selectedAdapter
        selectedAdapter.submitList(selects)
        selectedAdapter.notifyDataSetChanged()
    }

    //请求悬浮窗权限
    private fun getOverlayPermission() {
        val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + requireActivity().packageName)
        startActivityForResult(intent, 0)
    }

}