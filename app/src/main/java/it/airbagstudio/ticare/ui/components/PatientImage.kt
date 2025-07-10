package it.airbagstudio.ticare.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import it.airbagstudio.ticare.BuildConfig
import it.airbagstudio.ticare.R
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.io.File
import java.security.SecureRandom
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.X509Certificate

data class ImageRequestData(
    val url: String,
    val token: String
)
fun getUnsafeOkHttpClient(): OkHttpClient {
    // Create a trust manager that does not validate certificate chains
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

        override fun checkClientTrusted(
            p0: Array<out java.security.cert.X509Certificate?>?,
            p1: String?
        ) {
        }

        override fun checkServerTrusted(
            p0: Array<out java.security.cert.X509Certificate?>?,
            p1: String?
        ) {
        }

        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate?>? {
            return arrayOf()
        }
    })

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    // Create an ssl socket factory with our all-trusting manager
    val sslSocketFactory = sslContext.socketFactory

    return OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }.dispatcher(Dispatcher().apply {
            maxRequests = 2

        }).build()
}
/*
val okHttpClient = OkHttpClient.Builder()
    .dispatcher(Dispatcher().apply {
        maxRequests = 2

    })
    .build()
*/
@Composable
fun PatientImage(code: String, photo: String, requestData: ImageRequestData,isOnline: Boolean = true) {

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(56.dp)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = "",
            modifier = Modifier.padding(4.dp)
        )
        if (photo.isNotBlank()) {
            Image(
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = getPainter(code,photo,requestData,isOnline),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }
    }



}
@Composable
private fun getPainter(code: String, photo: String, requestData: ImageRequestData,isOnline: Boolean): Painter{
    val context = LocalContext.current
    if (isOnline){
        val url = "${requestData.url}/cases/case/image?cod=${Uri.encode(code)}&photo=${Uri.encode(photo)}"
        val authTimestampHeader = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .addHeader("Authorization", "Bearer ${requestData.token}")
            .addHeader("api-version", BuildConfig.API_VERSION)
            .addHeader("auth-timestamp", authTimestampHeader)
            .build()
        val imageLoader = ImageLoader.Builder(LocalContext.current)
            .okHttpClient(getUnsafeOkHttpClient())
            .build()
        return rememberAsyncImagePainter(
                model = imageRequest,
        imageLoader = imageLoader
        )
    }else{
        val dir = context.getDir("images", Context.MODE_PRIVATE)
        val photoFile = File(dir, Uri.encode(photo))
        return rememberAsyncImagePainter(model = photoFile)
    }

}