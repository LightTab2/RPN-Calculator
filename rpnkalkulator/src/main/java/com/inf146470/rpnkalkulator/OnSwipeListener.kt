package com.inf146470.rpnkalkulator

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.atan2

open class OnSwipeListener(ctx: Context) : View.OnTouchListener
{
    private val gestureDetector : GestureDetector = GestureDetector(ctx, GestureHandler())

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    inner class GestureHandler : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean
        {
            if (abs(e1.x - e2.x) < 150.0)
                return false

            val angle = ((atan2((e1.y - e2.y).toDouble(), (e2.x - e1.x).toDouble()) + Math.PI) * 180f / Math.PI + 180f) % 360f
            if ((angle >= 0f && angle < 45f) || (angle >= 315f && angle < 360f))
            {
                onSwipeRight()
                return true
            }
            else if (angle >= 135f && angle < 225f)
            {
                onSwipeLeft()
                return false
            }
            return false
        }
    }
}