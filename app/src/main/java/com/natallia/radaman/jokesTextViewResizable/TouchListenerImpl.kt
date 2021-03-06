package com.natallia.radaman.jokesTextViewResizable

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

class TouchListenerImpl(private val minWidth: Int, private val minHeight: Int) :
    View.OnTouchListener {

    private var originX = 0f
    private var originY = 0f
    private var secondOriginX = 0f
    private var secondOriginY = 0f
    private var lastDiffX = 0f
    private var lastDiffY = 0f

    private var originUp = false
    private var secondOriginUp = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        event.offsetLocation(event.rawX - event.x, event.rawY - event.y)

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                originUp = false
                secondOriginUp = false
                originX = view.x - event.getX(0)
                originY = view.y - event.getY(0)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                secondOriginX = view.x - event.getX(1)
                secondOriginY = view.y - event.getY(1)

                lastDiffX = Math.abs(secondOriginX - originX)
                lastDiffY = Math.abs(secondOriginY - originY)
            }
            MotionEvent.ACTION_MOVE -> { // a pointer was moved
                if (event.pointerCount == 2) {
                    val diffX = Math.abs(event.getX(1) - event.getX(0))
                    val diffY = Math.abs(event.getY(1) - event.getY(0))
                    var newWidth = (diffX * view.measuredWidth.toFloat() / lastDiffX).toInt()
                    var newHeight = (diffY * view.measuredHeight.toFloat() / lastDiffY).toInt()

                    if (newWidth > minWidth && newHeight > minHeight) {
                        val parentWidth = (view.parent as View).width
                        val parentHeight = (view.parent as View).height
                        val params = view.layoutParams

                        if (newWidth + view.x > parentWidth) {
                            newWidth = parentWidth - view.x.toInt()
                        }
                        if (newHeight + view.y > parentHeight) {
                            newHeight = parentHeight - view.y.toInt()
                        }

                        params.width = newWidth
                        params.height = newHeight
                        view.layoutParams = params
                        lastDiffX = diffX
                        lastDiffY = diffY
                    }
                } else if (!originUp && !secondOriginUp) {
                    var newX = event.getX(0) + originX
                    var newY = event.getY(0) + originY

                    if (newX < 0) {
                        newX = 0f
                    }
                    if (newY < 0) {
                        newY = 0f
                    }

                    if (newX + view.measuredWidth > (view.parent as View).width) {
                        newX = (view.parent as View).width.toFloat() - view.measuredWidth
                    }
                    if (newY + view.measuredHeight > (view.parent as View).height) {
                        newY = (view.parent as View).height.toFloat() - view.measuredHeight
                    }

                    view.x = newX
                    view.y = newY
                }
            }
            MotionEvent.ACTION_UP -> {
                originUp = true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                secondOriginUp = true
            }
        }

        return true
    }
}
