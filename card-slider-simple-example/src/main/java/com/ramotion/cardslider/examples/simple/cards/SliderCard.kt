package com.ramotion.cardslider.examples.simple.cards

import android.graphics.Bitmap
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView

import com.ramotion.cardslider.examples.simple.R
import com.ramotion.cardslider.examples.simple.utils.DecodeBitmapTask

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView

class SliderCard(itemView: View) : RecyclerView.ViewHolder(itemView), DecodeBitmapTask.Listener {

  private val imageView: ImageView

  private var task: DecodeBitmapTask? = null

  init {
    imageView = itemView.findViewById<View>(R.id.image) as ImageView
  }

  internal fun setContent(@DrawableRes resId: Int) {
    if (viewWidth == 0) {
      itemView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
          itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)

          viewWidth = itemView.width
          viewHeight = itemView.height
          loadBitmap(resId)
        }
      })
    } else {
      loadBitmap(resId)
    }
  }

  internal fun clearContent() {
    if (task != null) {
      task!!.cancel(true)
    }
  }

  private fun loadBitmap(@DrawableRes resId: Int) {
    task = DecodeBitmapTask(itemView.resources, resId, viewWidth, viewHeight, this)
    task!!.execute()
  }

  override fun onPostExecuted(bitmap: Bitmap) {
    imageView.setImageBitmap(bitmap)
  }

  companion object {

    private var viewWidth = 0
    private var viewHeight = 0
  }

}
