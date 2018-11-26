package com.tutsplus.facecontourdetector

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user_input.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_GO) {
                Picasso.get()
                    .load(user_input.text.toString())
                    .into(photo)
                true
            }
            false
        }

        Picasso.get()
            .load("https://i.imgur.com/6l1pm2S.jpg")
            .into(photo)

        action_button.setOnClickListener {

            val detectorOptions =
                    FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(
                            FirebaseVisionFaceDetectorOptions.ALL_CONTOURS
                        ).build()

            val detector = FirebaseVision.getInstance()
                                         .getVisionFaceDetector(detectorOptions)

            val visionImage = FirebaseVisionImage.fromBitmap(
                (photo.drawable as BitmapDrawable).bitmap
            )

            detector.detectInImage(visionImage).addOnSuccessListener {

                it.forEach {
                    val contour = it.getContour(FirebaseVisionFaceContour.FACE)
                    contour.points.forEach {
                        println("Point at ${it.x}, ${it.y}")
                    }

                    val mutableBitmap =
                        (photo.drawable as BitmapDrawable).bitmap.copy(
                            Bitmap.Config.ARGB_8888, true
                        )

                    val canvas = Canvas(mutableBitmap)

                    val myPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    myPaint.color = Color.parseColor("#99ff0000")

                    val path = Path()
                    path.moveTo(contour.points[0].x, contour.points[0].y)
                    contour.points.forEach {
                        path.lineTo(it.x, it.y)
                    }
                    path.close()

                    canvas.drawPath(path, myPaint)

                    photo.setImageBitmap(mutableBitmap)
                }
            }
        }
    }
}
