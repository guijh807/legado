/*
 * Copyright (C) 2015 tyrantgit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.legado.app.ui.widget.anima.explosion_field

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import java.util.*


class ExplosionField : View {

    private var customDuration = ExplosionAnimator.DEFAULT_DURATION
    private var idPlayAnimationEffect = 0
    private var mZAnimatorListener: OnAnimatorListener? = null
    private var mOnClickListener: View.OnClickListener? = null

    private val mExplosions = ArrayList<ExplosionAnimator>()
    private val mExpandInset = IntArray(2)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {

        Arrays.fill(mExpandInset, Utils.dp2Px(32))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (explosion in mExplosions) {
            explosion.draw(canvas)
        }
    }

    fun playSoundAnimationEffect(id: Int) {
        this.idPlayAnimationEffect = id
    }

    fun setCustomDuration(customDuration: Long) {
        this.customDuration = customDuration
    }

    fun addActionEvent(ievents: OnAnimatorListener) {
        this.mZAnimatorListener = ievents
    }


    fun expandExplosionBound(dx: Int, dy: Int) {
        mExpandInset[0] = dx
        mExpandInset[1] = dy
    }

    @JvmOverloads
    fun explode(bitmap: Bitmap?, bound: Rect, startDelay: Long, view: View? = null) {
        val currentDuration = customDuration
        val explosion = ExplosionAnimator(this, bitmap!!, bound)
        explosion.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mExplosions.remove(animation)
                if (view != null) {
                    view.scaleX = 1f
                    view.scaleY = 1f
                    view.alpha = 1f
                    view.setOnClickListener(mOnClickListener)//set event

                }
            }
        })
        explosion.startDelay = startDelay
        explosion.duration = currentDuration
        mExplosions.add(explosion)
        explosion.start()
    }

    @JvmOverloads
    fun explode(view: View, restartState: Boolean? = false) {

        val r = Rect()
        view.getGlobalVisibleRect(r)
        val location = IntArray(2)
        getLocationOnScreen(location)
        //        getLocationInWindow(location);
        //        view.getLocationInWindow(location);
        r.offset(-location[0], -location[1])
        r.inset(-mExpandInset[0], -mExpandInset[1])
        val startDelay = 100
        val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(150)
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {

            internal var random = Random()

            override fun onAnimationUpdate(animation: ValueAnimator) {
                view.translationX = (random.nextFloat() - 0.5f) * view.width.toFloat() * 0.05f
                view.translationY = (random.nextFloat() - 0.5f) * view.height.toFloat() * 0.05f
            }
        })

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                if (idPlayAnimationEffect != 0)
                    MediaPlayer.create(context, idPlayAnimationEffect).start()
            }

            override fun onAnimationEnd(animator: Animator) {
                if (mZAnimatorListener != null) {
                    mZAnimatorListener!!.onAnimationEnd(animator, this@ExplosionField)
                }
            }

            override fun onAnimationCancel(animator: Animator) {
                Log.i("PRUEBA", "CANCEL")
            }

            override fun onAnimationRepeat(animator: Animator) {
                Log.i("PRUEBA", "REPEAT")
            }
        })

        animator.start()
        view.animate().setDuration(150).setStartDelay(startDelay.toLong()).scaleX(0f).scaleY(0f)
            .alpha(0f).start()
        if (restartState!!)
            explode(Utils.createBitmapFromView(view), r, startDelay.toLong(), view)
        else
            explode(Utils.createBitmapFromView(view), r, startDelay.toLong())

    }

    fun clear() {
        mExplosions.clear()
        invalidate()
    }

    override fun setOnClickListener(mOnClickListener: View.OnClickListener?) {
        this.mOnClickListener = mOnClickListener
    }

    companion object {

        fun attach2Window(activity: Activity): ExplosionField {
            val rootView = activity.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
            val explosionField = ExplosionField(activity)
            rootView.addView(
                explosionField, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            return explosionField
        }
    }


}
