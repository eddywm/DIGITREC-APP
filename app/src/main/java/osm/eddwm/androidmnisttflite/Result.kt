package osm.eddwm.androidmnisttflite


class Result(result: FloatArray, val timeCost: Long) {

    val number: Int
    val probability: Float

    init {
        number = argmax(result)
        probability = result[number]
    }

    private fun argmax(probs: FloatArray): Int {
        var maxIdx = -1
        var maxProb = 0.0f
        for (i in probs.indices) {
            if (probs[i] > maxProb) {
                maxProb = probs[i]
                maxIdx = i
            }
        }
        return maxIdx
    }

}