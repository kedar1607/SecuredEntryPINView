package com.example.dottedsecureentrypinview.extentions

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation



fun View.scaleUpAnimation(widthTo: Int, heightTo: Int) {
    measure(widthTo, heightTo)
    val height = measuredHeight
    val width = measuredWidth
    layoutParams.height = 0
    layoutParams.width = 0
    visibility = View.VISIBLE
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            layoutParams.height = if (interpolatedTime == 1f)
                heightTo
            else
                (height * interpolatedTime).toInt()

            layoutParams.width = if (interpolatedTime == 1f)
                widthTo
            else
                (width * interpolatedTime).toInt()
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = (((widthTo+heightTo) / 2) / context.resources.displayMetrics.density).toInt().toLong()
    startAnimation(a)
}

fun View.changeVisibilityWithScaleAnimation(show: Boolean, widthTo: Int, heightTo: Int){
    if (show) {
        if(visibility == View.GONE) scaleUpAnimation(widthTo, heightTo)
    } else {
        if(visibility == View.VISIBLE) visibility = View.GONE
    }
}