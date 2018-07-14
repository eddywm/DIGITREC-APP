package osm.eddwm.androidmnisttflite

import android.graphics.*


object ImageUtil {

    private val INVERT = ColorMatrix(
            floatArrayOf(-1f, 0f, 0f, 0f, 255f, 0f, -1f, 0f, 0f, 255f, 0f, 0f, -1f, 0f, 255f, 0f, 0f, 0f, 1f, 0f))

    private val COLOR_FILTER = ColorMatrixColorFilter(INVERT)

    fun invert(image: Bitmap): Bitmap {
        val inverted = Bitmap.createBitmap(image.width, image.height,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(inverted)
        val paint = Paint()
        paint.colorFilter = COLOR_FILTER
        canvas.drawBitmap(image, 0f, 0f, paint)
        return inverted
    }

}