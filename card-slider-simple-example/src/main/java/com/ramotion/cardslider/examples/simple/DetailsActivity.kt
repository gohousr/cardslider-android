package com.ramotion.cardslider.examples.simple

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView

import com.ramotion.cardslider.examples.simple.utils.DecodeBitmapTask

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class DetailsActivity : AppCompatActivity(), DecodeBitmapTask.Listener {

  private var imageView: ImageView? = null
  private var decodeBitmapTask: DecodeBitmapTask? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_details)

    val smallResId = intent.getIntExtra(BUNDLE_IMAGE_ID, -1)
    if (smallResId == -1) {
      finish()
      return
    }

    imageView = findViewById<View>(R.id.image) as ImageView
    imageView!!.setImageResource(smallResId)

    imageView!!.setOnClickListener { super@DetailsActivity.onBackPressed() }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      loadFullSizeBitmap(smallResId)
    } else {
      window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {

        private var isClosing = false

        override fun onTransitionPause(transition: Transition) {}
        override fun onTransitionResume(transition: Transition) {}
        override fun onTransitionCancel(transition: Transition) {}

        override fun onTransitionStart(transition: Transition) {
          if (isClosing) {
            addCardCorners()
          }
        }

        override fun onTransitionEnd(transition: Transition) {
          if (!isClosing) {
            isClosing = true

            removeCardCorners()
            loadFullSizeBitmap(smallResId)
          }
        }
      })
    }
  }

  override fun onPause() {
    super.onPause()

    if (isFinishing && decodeBitmapTask != null) {
      decodeBitmapTask!!.cancel(true)
    }
  }

  private fun addCardCorners() {
    val cardView = findViewById<View>(R.id.card) as CardView
    cardView.radius = 25f
  }

  private fun removeCardCorners() {
    val cardView = findViewById<View>(R.id.card) as CardView
    ObjectAnimator.ofFloat(cardView, "radius", 0f).setDuration(50).start()
  }

  private fun loadFullSizeBitmap(smallResId: Int) {
    val bigResId: Int
    when (smallResId) {
      R.drawable.p1 -> bigResId = R.drawable.p1_big
      R.drawable.p2 -> bigResId = R.drawable.p2_big
      R.drawable.p3 -> bigResId = R.drawable.p3_big
      R.drawable.p4 -> bigResId = R.drawable.p4_big
      R.drawable.p5 -> bigResId = R.drawable.p5_big
      else -> bigResId = R.drawable.p1_big
    }

    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getRealMetrics(metrics)

    val w = metrics.widthPixels
    val h = metrics.heightPixels

    decodeBitmapTask = DecodeBitmapTask(resources, bigResId, w, h, this)
    decodeBitmapTask!!.execute()
  }

  override fun onPostExecuted(bitmap: Bitmap) {
    imageView!!.setImageBitmap(bitmap)
  }

  companion object {

    internal val BUNDLE_IMAGE_ID = "BUNDLE_IMAGE_ID"
  }

}
