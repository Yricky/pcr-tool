package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication
import coil.util.CoilUtils
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * Retrofit2 创建服务
 */
object ApiUtil {


    private fun OkHttpClient.Builder.setSSL(): OkHttpClient.Builder {
        val client = this
        //初始 SSL
        val trustManagers: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustManagers, null)
        client.sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
        return client
    }

    /**
     * 带下载进度 client
     */
    fun downloadClientBuild(listener: DownloadListener): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(Interceptor {
                val originalResponse: Response = it.proceed(it.request())
                return@Interceptor originalResponse.newBuilder()
                    .body(DownloadResponseBody(originalResponse.body()!!, listener))
                    .build()
            })
            .retryOnConnectionFailure(true)
            .connectTimeout(360, TimeUnit.SECONDS)
            .writeTimeout(360, TimeUnit.SECONDS)
            .readTimeout(360, TimeUnit.SECONDS)
        return builder.setSSL().build()
    }


    /**
     * 创建 [OkHttpClient]
     */
    fun getClient(second: Long): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .cache(CoilUtils.createDefaultCache(MyApplication.context))
            .connectTimeout(second, TimeUnit.SECONDS)
            .writeTimeout(second, TimeUnit.SECONDS)
            .readTimeout(second, TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor(3))

        return builder.setSSL().build()
    }


    /**
     * 创建服务
     */
    fun <T> create(serviceClass: Class<T>, url: String, second: Long): T {
        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient(second))

        return builder.build().create(serviceClass)
    }

    /**
     * 创建自定义 [client] 服务
     */
    fun <T> createWithClient(serviceClass: Class<T>, url: String, client: OkHttpClient): T {
        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

        return builder.build().create(serviceClass)
    }

}

/**
 * 重试拦截器
 */
class RetryInterceptor(  //最大重试次数
    private var maxRetry: Int
) : Interceptor {
    private var retryNum = 0 //假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        while (!response.isSuccessful && retryNum < maxRetry) {
            response.close()
            retryNum++
            response = chain.proceed(request)
        }
        return response
    }
}

/**
 * Enables TLS v1.2 when creating SSLSockets.
 *
 *
 * For some reason, android supports TLS v1.2 from API 16, but enables it by
 * default only from API 20.
 * @link https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
 * @see SSLSocketFactory
 */
class Tls12SocketFactory(private val delegate: SSLSocketFactory) : SSLSocketFactory() {
    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
        return patch(delegate.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String?, port: Int): Socket {
        return patch(delegate.createSocket(host, port))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(
        host: String?,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ): Socket {
        return patch(delegate.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress?, port: Int): Socket {
        return patch(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket {
        return patch(delegate.createSocket(address, port, localAddress, localPort))
    }

    private fun patch(s: Socket): Socket {
        if (s is SSLSocket) {
            s.enabledProtocols = TLS_V12_ONLY
        }
        return s
    }

    companion object {
        private val TLS_V12_ONLY = arrayOf("TLSv1.2")
    }
}