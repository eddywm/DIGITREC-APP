package osm.eddwm.androidmnisttflite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import java.io.IOException
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mClassifier: Classifier? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "CHARACTER RECOGNITION"
        init();

        btnClear.setOnClickListener {
            fpv_paint.clear()
            tvPredictionResult.setText(R.string.empty)
            tvProbability.setText(R.string.empty)
        }

        btnDetectObject.setOnClickListener {
            if (mClassifier == null) {
                Log.e(LOG_TAG, "onDetectClick(): Classifier is not initialized")
            } else if (fpv_paint.isEmpty) {
                Toast.makeText(this, R.string.non_digit_input, Toast.LENGTH_SHORT).show()

            }

            val image = fpv_paint.exportToBitmap(
                    Classifier.DIM_IMG_SIZE_WIDTH, Classifier.DIM_IMG_SIZE_HEIGHT)
            // The model is trained on images with black background and white font
            val inverted = ImageUtil.invert(image)
            val result = mClassifier!!.classify(inverted)
            renderResult(result)

        }
    }


    private fun init() {
        try {
            mClassifier = Classifier(this@MainActivity)
        } catch (e: IOException) {
            Toast.makeText(this, R.string.failed_to_create_classifier, Toast.LENGTH_LONG).show()
            Log.e(LOG_TAG, "init(): Failed to create tflite model", e)
        }

    }

    private fun renderResult(result: Result) {
        tvPredictionResult.text = result.number.toString()
        tvProbability.text = result.probability.toString()
    }

    companion object {

         val LOG_TAG = "MNIST_APP_LOGGING"

    }

}
