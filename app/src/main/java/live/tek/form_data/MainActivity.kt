package live.tek.form_data

import android.content.ContentResolver
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import live.tek.form_data.network.FileUtil
import live.tek.form_data.network.ServiceBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Multipart
import java.io.File
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private val REQUEST_GALLERY = 2121
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.btnOpenGallery)
        imageView = findViewById<ImageView>(R.id.imgSelected)

        button.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "open gallery"), REQUEST_GALLERY)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                val uri = data?.data
                try {
                    uri?.let {
                        setImage(it)
                    }

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                uri?.let {
                    doRequest(it)
                }

            }
        }
    }

    private fun setImage(uri: Uri) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            imageView.setImageBitmap(bitmap)

        } else {
            @Suppress("DEPRECATION") val bitmap =
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imageView.setImageBitmap(bitmap)

        }
    }

    private fun doRequest(uri: Uri) {
        val file = File(FileUtil.getPath(this, uri))
        val requestFile = file.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val serviceBuilder = ServiceBuilder.myApi
        serviceBuilder.uploadImage(body).enqueue(object : retrofit2.Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                val responseCode = response.code()
                if (responseCode == 200) {
                    Toast.makeText(this@MainActivity, "Uploaded !", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed due ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}