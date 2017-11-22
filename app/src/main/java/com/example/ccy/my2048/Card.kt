package com.example.ccy.my2048

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView


class Card(context: Context) : FrameLayout(context) {

    var num = 0
        set(num) {
            field = num
            if (num <= 4)
                label.setTextColor(resources.getColor(R.color.text_black))
            else
                label.setTextColor(resources.getColor(R.color.text_white))
            label.text = if(num == 0)"" else num.toString()

            when (num) {
                0 -> label.setBackgroundResource(R.drawable.cell_rectangle)
                2 -> label.setBackgroundResource(R.drawable.cell_rectangle_2)
                4 -> label.setBackgroundResource(R.drawable.cell_rectangle_4)
                8 -> label.setBackgroundResource(R.drawable.cell_rectangle_8)
                16 -> label.setBackgroundResource(R.drawable.cell_rectangle_16)
                32 -> label.setBackgroundResource(R.drawable.cell_rectangle_32)
                64 -> label.setBackgroundResource(R.drawable.cell_rectangle_64)
                128 -> label.setBackgroundResource(R.drawable.cell_rectangle_128)
                256 -> label.setBackgroundResource(R.drawable.cell_rectangle_256)
                512 -> label.setBackgroundResource(R.drawable.cell_rectangle_512)
                1024 -> label.setBackgroundResource(R.drawable.cell_rectangle_1024)
                2048 -> label.setBackgroundResource(R.drawable.cell_rectangle_2048)
            }
        }

    val label: TextView
    private val background: View

    init {
        var lp: FrameLayout.LayoutParams?
        background = View(getContext())
        lp = FrameLayout.LayoutParams(-1, -1)
        lp.setMargins(20, 20, 20, 20)
        background.setBackgroundResource(R.drawable.cell_rectangle)
        addView(background, lp)
        label = TextView(getContext())
        label.textSize = 32f
        label.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        label.gravity = Gravity.CENTER
        lp.gravity = Gravity.NO_GRAVITY
        addView(label, lp)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Card) return false
        return num == other.num
    }

}
