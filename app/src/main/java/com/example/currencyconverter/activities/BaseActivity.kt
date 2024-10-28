package com.example.currencyconverter.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    val API_KEY_SYMBOLS: String = "fed9c701bd707d0d7f66b99d00aba9f9"
    val API_KEY_CONVERT: String = "Basic bG9kZXN0YXI6cHVnc25heA=="
    fun cropCenterBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val x = (bitmap.width - 40) / 2
        val y = (bitmap.height - 40) / 2
        val cropBitmap: Bitmap = Bitmap.createBitmap(bitmap, x, y, 40, 40)
        return Bitmap.createScaledBitmap(cropBitmap, width, height, true)
    }
}