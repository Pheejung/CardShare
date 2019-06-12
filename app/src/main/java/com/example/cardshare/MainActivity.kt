package com.example.cardshare

import android.Manifest
import android.content.Intent
import android.content.Intent.createChooser
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.Window
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHideTitleBar()
        setContentView(R.layout.activity_main)

        grantExternalStoragePermission()

        btnShare.setOnClickListener {
            SaveImage()?.let {ShareImage(it)}
        }

        btnClear.setOnClickListener {
            DeleteTextAndImageFile()
        }
    }

    private fun setHideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar!!.hide()
    }

    private fun grantExternalStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        } else {
            true
        }
    }

    fun SaveImage() : File {
        val b:Bitmap
        editText1.apply {
            isDrawingCacheEnabled = true
            buildDrawingCache(true)
            b = Bitmap.createBitmap(drawingCache)
            isDrawingCacheEnabled = false
            buildDrawingCache(false)
        }
        val file_path = Environment.getExternalStorageDirectory().absolutePath + "/test"
        var dir = File(file_path)
        if(!dir.exists())
            dir.mkdirs()

        var file = File(dir, "test.png")
        FileOutputStream(file).apply {
            b.compress(Bitmap.CompressFormat.PNG, 85,this)
            flush()
            close()
        }
        return file
    }

    fun ShareImage(f:File) {
        val uri = Uri.fromFile(f)

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share Cover Image"))
    }

    fun SelectBackground() {
        val items = arrayOf<CharSequence>("꽃", "고양이", "게임")
        val Images = intArrayOf(R.drawable.flower, R.drawable.cat, R.drawable.game)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("배경설정").setItems(items) {
            dialog, index -> editText1.setBackgroundResource(Images[index])
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun DeleteTextAndImageFile() {
        val file_path = Environment.getExternalStorageDirectory().absolutePath + "/test"
        var dir = File(file_path)
        File(dir, "test.png")?.apply {delete()}
        editText1.setText("")
        //editText1.text = ""
    }
}
