package cn.wthee.pcrtool.ui.tool.news

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.wthee.pcrtool.databinding.FragmentToolNewsDetailBinding
import cn.wthee.pcrtool.utils.BrowserUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 公告详情
 */
private const val REGION = "region"
private const val NEWSID = "news_id"
private const val URL = "url"

class NewsDetailDialogFragment : BottomSheetDialogFragment() {

    private var region = 0
    private var newsId = 0
    private var url = ""
    private lateinit var binding: FragmentToolNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            region = it.getInt(REGION)
            newsId = it.getInt(NEWSID)
            url = it.getString(URL) ?: ""
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolNewsDetailBinding.inflate(inflater, container, false)
        binding.apply {
            openBrowse.setOnClickListener {
                BrowserUtil.open(requireContext(), url)
            }
            fabTop.setOnClickListener {
                scrollView.smoothScrollTo(0, 0)
            }
            //设置
            webView.settings.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
                useWideViewPort = true //将图片调整到适合webView的大小
                loadWithOverviewMode = true // 缩放至屏幕的大小
                javaScriptCanOpenWindowsAutomatically = true
            }
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url!!)
                    return true
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (region == 2) {
                        //取消内部滑动
                        webView.loadUrl(
                            """
                            javascript:
                            $('#news-content').css('overflow','inherit');
                            $('.news-detail').css('top','0.3rem');
                            $('.top').css('display','none');
                        """.trimIndent()
                        )
                    }
                    if (region == 3) {
                        webView.loadUrl(
                            """
                            javascript:
                            $('.menu').css('display','none');
                            $('.story_container_m').css('display','none');                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
                            $('.title').css('display','none');
                            $('header').css('display','none');
                            $('footer').css('display','none');
                            $('aside').css('display','none');
                            $('.paging').css('display','none');
                        """.trimIndent()
                        )
                    }
                    if (region == 4) {
                        webView.loadUrl(
                            """
                            javascript:
                            $('#main_area').css('display','none');
                            $('.bg-gray').css('display','none');
                            $('.news_prev').css('display','none');
                            $('.news_next').css('display','none');
                            $('header').css('display','none');
                            $('footer').css('display','none');
                        """.trimIndent()
                        )
                    }
                    loading.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                    fabTop.show()
                }
            }
            //加载网页
            webView.loadUrl(url)
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(region: Int, newsId: Int, url: String) =
            NewsDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                    putInt(NEWSID, newsId)
                    putString(URL, url)
                }
            }
    }
}