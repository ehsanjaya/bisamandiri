package com.ehsanjaya.bisamandiri.utilites

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotateBitmap(rotationDegrees: Int, isFrontCamera: Boolean): Bitmap {
    val matrix = Matrix().apply {
        postRotate(-rotationDegrees.toFloat())

        if (isFrontCamera) {
            postScale(1f, -1f)
        } else {
            postScale(-1f, -1f)
        }
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.setRotationBitmap(isFrontCamera: Boolean): Bitmap {
    val matrix = Matrix().apply {
        postRotate(90f)
        if (isFrontCamera) {
            postScale(1f, -1f)
        }
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
