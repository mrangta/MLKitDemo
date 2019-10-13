package com.example.textrecognition

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    val PICK_REQUEST = 1
    val REQEST_READ_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val uploadButton: Button = findViewById<Button>(R.id.upload)
        val detectButton: Button = findViewById<Button>(R.id.detect)

        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"


            if (checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQEST_READ_EXTERNAL_STORAGE )
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_REQUEST)
        }

        detectButton.setOnClickListener {
            Toast.makeText(applicationContext, "Detecting ...", Toast.LENGTH_SHORT).show()

            val image: FirebaseVisionImage
            try {

                // create an firebase vision image object from the image uri selected by user
                val drawable = findViewById<ImageView>(R.id.imageView).drawable
                if(drawable != null) {
                    var bitmap = drawable.toBitmap()
                    image = FirebaseVisionImage.fromBitmap(bitmap)

                    if(image == null) {
                        Log.i("MCC", "Image could not be converted to bitmap")
                    }

                    val textView = findViewById<TextView>(R.id.showText)
                    val text = ObjectDetector(image, textView).analyze()
                } else {
                    Toast.makeText(applicationContext, "Please select an image first ...", Toast.LENGTH_SHORT)
                }
            } catch (e: IOException) {
                Log.e("MCC", e.message)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQEST_READ_EXTERNAL_STORAGE) {
            if(!(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(applicationContext, "Permission needed to access images", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                val path:String? = getPathFromURI(data!!.data!!)
                val file = File(path!!)
                val fileUri = Uri.fromFile(file)
                findViewById<ImageView>(R.id.imageView).setImageURI(fileUri)
            }
        }
    }

    fun getPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }
}
