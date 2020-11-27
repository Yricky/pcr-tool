package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.data.view.all
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.databinding.FragmentCharacterAttrInfoBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.ui.main.EquipmentViewModel
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.UID
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch


class CharacterAttrFragment : Fragment() {

    companion object {
        fun getInstance(uid: Int): CharacterAttrFragment {
            val fragment = CharacterAttrFragment()
            val bundle = Bundle()
            bundle.putInt(UID, uid)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var uid = 0
    private lateinit var binding: FragmentCharacterAttrInfoBinding
    private lateinit var attrAdapter: CharacterAttrAdapter
    private var selRank = 2
    private var selRatity = 1
    private var maxStar = 5
    private var lv = 85

    private val sharedEquipViewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }
    private val characterAttrViewModel =
        InjectorUtil.providePromotionViewModelFactory().create(CharacterAttrViewModel::class.java)

    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = CharacterPagerFragment.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterAttrInfoBinding.inflate(inflater, container, false)
        //点击事件
        setListener()
        //数据监听
        setObserve()
        //加载icon
        var id = uid
        id += if (CharacterListFragment.r6Ids.contains(id)) 60 else 30
        val picUrl = Constants.UNIT_ICON_URL + id + Constants.WEBP
        binding.icon.load(picUrl) {
            error(R.drawable.unknown_gray)
            placeholder(R.drawable.unknown_gray)
        }
        characterAttrViewModel.getMaxRankAndRarity(uid)
        return binding.root
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            //头像点击查看掉落
            icon.setOnClickListener {
                lifecycleScope.launch {
                    if (sharedCharacterViewModel.getDrops(uid).isNotEmpty()) {
                        CharacterDropDialogFragment.getInstance(uid).show(
                            parentFragmentManager, "character_drop"
                        )
                    } else {
                        ToastUtil.short("无掉落信息~")
                    }
                }
            }
            //等级点击事件
            level.setOnClickListener {
                levelSeekBar.also {
                    it.visibility = if (it.visibility == View.VISIBLE)
                        View.GONE
                    else
                        View.VISIBLE
                }
            }
            //等级滑动条
            levelSeekBar.addOnSliderTouchListener(object :
                Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                    lv = slider.value.toInt()
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    loadData()
                }

            })
            levelSeekBar.addOnChangeListener { slider, _, _ ->
                lv = slider.value.toInt()
                level.text = lv.toString()
            }
        }
    }

    private fun setObserve() {
        //角色基本信息
//        sharedCharacterViewModel.getCharacter(uid)
//        sharedCharacterViewModel.character.observe(viewLifecycleOwner, {
//            binding.name.text = it.name
//        })
        //获取角色最大Rank后，加载数据
        characterAttrViewModel.maxData.observe(viewLifecycleOwner, { r ->
            selRank = r[0]
            selRatity = r[1]
            maxStar = r[1]
            lv = r[2]
            binding.apply {
                level.text = lv.toString()
                levelSeekBar.valueFrom = 1.0f
                levelSeekBar.valueTo = lv.toFloat()
                levelSeekBar.value = lv.toFloat()
                loadData()
                setRank(selRank)
                setRatity(selRatity)
                rankEquip.rankAdd.setOnClickListener {
                    if (selRank != r[0]) {
                        selRank++
                        if (selRank == r[0]) {
                            it.isEnabled = false
                        } else {
                            rankEquip.rankReduce.isEnabled = true
                        }
                        setRank(selRank)
                        loadData()
                    }
                }
                rankEquip.rankReduce.setOnClickListener {
                    if (selRank != Constants.CHARACTER_MIN_RANK) {
                        selRank--
                        if (selRank == 2) {
                            it.isEnabled = false
                        } else {
                            rankEquip.rankAdd.isEnabled = true
                        }
                        setRank(selRank)
                        loadData()
                    }
                }
            }
        })
        //专武
        sharedEquipViewModel.getUniqueEquipInfos(uid)
        sharedEquipViewModel.uniqueEquip.observe(viewLifecycleOwner, {
            binding.uniqueEquip.apply {
                if (it != null) {
                    binding.uniqueEquip.root.visibility = View.VISIBLE
                    val picUrl = Constants.EQUIPMENT_URL + it.equipmentId + Constants.WEBP
                    itemPic.load(picUrl) {
                        placeholder(R.drawable.unknown_gray)
                        error(R.drawable.unknown_gray)
                    }
                    //描述
                    titleDes.text =
                        getString(R.string.unique_equip_name, it.equipmentName, it.maxLevel)
                    desc.text = it.getDesc()
                    //属性词条
                    val adapter = EquipmentAttrAdapter()
                    attrs.adapter = adapter
                    adapter.submitList(it.attr.allNotZero())
                } else {
                    binding.uniqueEquip.root.visibility = View.GONE
                }

            }
        })
        //角色装备
        val equipPics = arrayListOf(
            binding.rankEquip.pic6,
            binding.rankEquip.pic5,
            binding.rankEquip.pic4,
            binding.rankEquip.pic3,
            binding.rankEquip.pic2,
            binding.rankEquip.pic1
        )
        characterAttrViewModel.equipments.observe(viewLifecycleOwner, {
            it.forEachIndexed { index, equip ->
                equipPics[index].apply {
                    //加载装备图片
                    val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                    this.load(picUrl) {
                        error(R.drawable.unknown_gray)
                        placeholder(R.drawable.unknown_gray)
                    }
                    //点击跳转
                    setOnClickListener {
                        if (equip.equipmentId != Constants.UNKNOW_EQUIP_ID) {
                            EquipmentDetailsDialogFragment.getInstance(equip)
                                .show(parentFragmentManager, "details")
                        }
                    }
                }
            }
        })
        //角色属性
        attrAdapter = CharacterAttrAdapter()
        binding.charcterAttrs.adapter = attrAdapter
        characterAttrViewModel.sumInfo.observe(viewLifecycleOwner, {
            attrAdapter.submitList(it.all()) {
                attrAdapter.notifyDataSetChanged()
            }
        })
        //角色剧情属性
        val storyAttrAdapter = CharacterAttrAdapter()
        binding.charcterStoryAttrs.adapter = storyAttrAdapter
        characterAttrViewModel.storyAttrs.observe(viewLifecycleOwner, {
            storyAttrAdapter.submitList(it.allNotZero()) {
                attrAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun loadData() {
        characterAttrViewModel.getCharacterInfo(uid, selRank, selRatity, lv)
    }

    //设置rank
    private fun setRank(num: Int) {
        binding.rankEquip.apply {
            rank.text = num.toString()
            rank.setTextColor(getRankColor(num))
            rankTitle.setTextColor(getRankColor(num))
        }
    }

    //设置星级
    private fun setRatity(num: Int) {
        StarUtil.show(
            binding.root.context,
            binding.starts,
            num,
            maxStar,
            50,
            object : StarUtil.OnSelect {
                override fun select(index: Int) {
                    selRatity = index + 1
                    loadData()
                }
            })
    }

    //rank 颜色
    private fun getRankColor(rank: Int): Int {
        val color = when (rank) {
            in 2..3 -> R.color.color_rank_2_3
            in 4..6 -> R.color.color_rank_4_6
            in 7..10 -> R.color.color_rank_7_10
            in 11..17 -> R.color.color_rank_11_17
            in 18..99 -> R.color.color_rank_18
            else -> {
                R.color.color_rank_2_3
            }
        }
        return ResourcesUtil.getColor(color)
    }
}
