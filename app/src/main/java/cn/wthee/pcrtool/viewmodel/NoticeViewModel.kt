package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 通知 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class NoticeViewModel @Inject constructor(private val apiRepository: MyAPIRepository) :
    ViewModel() {

    /**
     * 应用更新
     * -1：获取中
     * 0：无更新
     * 1：有更新
     */
    val updateApp = MutableLiveData(-1)


    /**
     * 通知公告
     */
    fun getNotice() = flow {
        val data = apiRepository.getNotice().data
        data?.let {
            emit(it)
        }
    }

    /**
     * 更新校验
     */
    fun check() {
        viewModelScope.launch {
            try {
                val version = apiRepository.getAppUpdateNotice()
                if (version.status == 0 && version.data != null && version.data == true) {
                    updateApp.postValue(1)
                } else {
                    updateApp.postValue(0)
                }
            } catch (e: Exception) {
                updateApp.postValue(0)
            }
        }
    }
}
