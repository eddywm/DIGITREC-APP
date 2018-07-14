package osm.eddwm.androidmnisttflite

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*


class Classifier @Throws(IOException::class)
constructor(activity: Activity) {

    private val mInterpreter: Interpreter
    private val mImgData: ByteBuffer?
    private val mImagePixels = IntArray(DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH)
    private val mResult = Array(1) { FloatArray(CATEGORY_COUNT) }

    init {
        mInterpreter = Interpreter(loadModelFile(activity))

        mImgData = ByteBuffer.allocateDirect(
                4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH * DIM_PIXEL_SIZE)
        mImgData!!.order(ByteOrder.nativeOrder())
    }

    fun classify(bitmap: Bitmap): Result {
        convertBitmapToByteBuffer(bitmap)
        val startTime = SystemClock.uptimeMillis()
        mImgData?.let { mInterpreter.run(it, mResult) }
        val endTime = SystemClock.uptimeMillis()
        val timeCost = endTime - startTime
        Log.v(LOG_TAG, "run(): result = " + Arrays.toString(mResult[0])
                + ", timeCost = " + timeCost)
        return Result(mResult[0], timeCost)
    }


    private fun loadModelFile(activity: Activity): MappedByteBuffer {

        val fileDescriptor: AssetFileDescriptor = activity.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        if (mImgData == null) {
            return
        }
        mImgData.rewind()

        bitmap.getPixels(mImagePixels, 0, bitmap.width, 0, 0,
                bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until DIM_IMG_SIZE_WIDTH) {
            for (j in 0 until DIM_IMG_SIZE_HEIGHT) {
                val `val` = mImagePixels[pixel++]
                mImgData.putFloat(convertToGreyScale(`val`))
            }
        }
    }

    private fun convertToGreyScale(color: Int): Float {
        return ((color shr 16 and 0xFF) + (color shr 8 and 0xFF) + (color and 0xFF)).toFloat() / 3.0f / 255.0f
    }

    companion object {
        private val LOG_TAG = "Classifier_Logging"

        val MODEL_PATH = "mnist.tflite"

        private val DIM_BATCH_SIZE = 1
        val DIM_IMG_SIZE_HEIGHT = 28
        val DIM_IMG_SIZE_WIDTH = 28
        private val DIM_PIXEL_SIZE = 1
        private val CATEGORY_COUNT = 10
    }
}