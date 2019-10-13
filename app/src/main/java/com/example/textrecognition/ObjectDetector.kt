package com.example.textrecognition;

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

class ObjectDetector(image: FirebaseVisionImage, textView: TextView) {

    val fVImage = image
    val tView = textView

    fun analyze() {
        // initialize the firebase vision text recognizer
        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer

        // pass the image for processing
        val result = detector.processImage(fVImage)
            .addOnSuccessListener { firebaseVisionText ->
                // Task completed successfully
                Log.i("MCC", "Successfully Parsed: " + firebaseVisionText.text)
                if(firebaseVisionText.text != "") {
                    tView.setText(firebaseVisionText.text)
                } else {
                    tView.setText("No Text Found")
                }            }
            .addOnFailureListener {
                // Task failed with an exception
                Log.e("MCC", it.localizedMessage)
                tView.setText("No Text Found")
            }
/*
        if(result.isSuccessful) {
            resultText = result.result!!.text
            for (block in result.result!!.textBlocks) {
                val blockText = block.text
                val blockConfidence = block.confidence
                val blockLanguages = block.recognizedLanguages
                val blockCornerPoints = block.cornerPoints
                val blockFrame = block.boundingBox
                for (line in block.lines) {
                    val lineText = line.text
                    val lineConfidence = line.confidence
                    val lineLanguages = line.recognizedLanguages
                    val lineCornerPoints = line.cornerPoints
                    val lineFrame = line.boundingBox
                    for (element in line.elements) {
                        val elementText = element.text
                        val elementConfidence = element.confidence
                        val elementLanguages = element.recognizedLanguages
                        val elementCornerPoints = element.cornerPoints
                        val elementFrame = element.boundingBox
                    }
                }
            }
        }
*/

    }

}
