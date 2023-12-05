package com.example.cameraapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {
    private lateinit var btn: Button
    private var iv: ImageView? = null
    private var nmFile: String? = null
    var cameraStartForResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                prosesData(result.data)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.cameraButton)
        iv = findViewById(R.id.resultImage)
        btn.setOnClickListener(View.OnClickListener { view: View? ->
            if (ActivityCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
            } else {
                val it = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val imageFolder = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    "KameraApp"
                )
                imageFolder.mkdirs()
                val d = Date()
                val dateFormat =
                    SimpleDateFormat("MM-dd-yy hh-mm-ss")
                nmFile =
                    imageFolder.toString() + File.separator + dateFormat.format(d) + ".jpg"
                val image = File(nmFile)
                val uriSavedImage =
                    FileProvider.getUriForFile(
                        this@MainActivity,
                        "$packageName.provider",
                        image
                    )
                // Comment this line below to disable Put Extra Method //
                it.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
                cameraStartForResult.launch(it)
            }
        })
    }

    protected fun requestPermission() {
        val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permission, 1)
    }

    @Throws(IOException::class)
    protected fun prosesData(data: Intent?) {
        val bm: Bitmap

        val options = BitmapFactory.Options()
        options.inSampleSize = 2
        bm = BitmapFactory.decodeFile(nmFile, options)
        // End of Save Photo to Storage //
        iv!!.setImageBitmap(bm)
        Toast.makeText(this, "Data Telah Terload ke ImageView", Toast.LENGTH_SHORT).show()
    }

    protected fun getFileName(extension: String): String {
        val d = Date()
        val dateFormat = SimpleDateFormat("MM-dd-yy hh-mm-ss")
        return dateFormat.format(d) + extension
    }
}