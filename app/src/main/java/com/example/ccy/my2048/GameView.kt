package com.example.ccy.my2048

import android.app.AlertDialog
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import java.util.*


class GameView : FrameLayout {

    var canUndo = true
    private val cards = ArrayList<Card>()
    private val cardList = LinkedList<Array<Array<TempCard?>>>()
    private val bufferCards = Array(4) { Array<TempCard?>(4) { null } }
    private val nextCards = Array<Array<TempCard?>>(4) { arrayOfNulls(4) }
    private val temp = Array<Array<TempCard?>>(4) { arrayOfNulls(4) }
    private val cardMaps = Array<Array<Card?>>(4) { arrayOfNulls(4) }
    private val emptyPoints = ArrayList<Point>()

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initGameView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initGameView()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width = 0
        var height = heightSize

        if (widthMode == MeasureSpec.EXACTLY) {
            println("MeasureSpec.EXACTLY ")
            width = widthSize
            println(width)
            Constant.CARD_WIDTH = (width - 40) / 4
            println(Constant.CARD_WIDTH)
            height = width
        }
        setMeasuredDimension(width, height)
    }

    constructor(context: Context) : super(context) {
        initGameView()
    }

    private fun initGameView() {

        setBackgroundResource(R.drawable.background_rectangle)
        initCards()
        setOnTouchListener(object : View.OnTouchListener {
            var startX = 0f
            var startY = 0f
            var offSetX = 0f
            var offSetY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x
                        startY = event.y
                    }
                    MotionEvent.ACTION_UP -> {
                        offSetX = event.x - startX
                        offSetY = event.y - startY
                        if (Math.abs(offSetX) > Math.abs(offSetY)) {
                            if (offSetX < -5.0f) {
                                swipeLeft()

                            } else if (offSetX > 5.0f) {
                                swipeRight()
                            }
                        } else {
                            if (offSetY < -5.0f) {
                                swipeUp()

                            } else if (offSetY > 5.0f) {
                                swipeDown()
                            }
                        }
                    }
                }
                return true
            }
        })
    }


    fun addCard(num: Int, i: Int, j: Int) {
        val c = Card(context)
        addView(c)
        val lp = FrameLayout.LayoutParams(Constant.CARD_WIDTH, Constant.CARD_WIDTH)
        c.num = num
        lp.leftMargin = j * Constant.CARD_WIDTH + 20
        lp.topMargin = i * Constant.CARD_WIDTH + 20
        c.layoutParams = lp
        cardMaps[j][i] = c
    }

    fun addTempCards(num: Int, i: Int, j: Int) {
        val t = TempCard(num)
        temp[j][i] = t

    }

    fun startGame() {
        MainActivity.Companion.mainActivity!!.clearScore()

        for (i in cardMaps.indices) {
            for (j in 0 until cardMaps[0].size) {
                cardMaps[i][j]!!.num = 0
            }
        }
        canUndo = false
        addRandomNum()
        addRandomNum()
        setCardS()
    }

    private fun swipeLeft() {
        var marge = false
        for (y in 0..3) {
            var x = 0
            while (x < 4) {
                for (x1 in x + 1..3) {
                    if (cardMaps[x1][y]!!.num > 0) {
                        if (cardMaps[x][y]!!.num <= 0) {
                            createMoveAnim(cardMaps[x1][y]!!, cardMaps[x][y]!!)
                            cardMaps[x][y]!!.num = cardMaps[x1][y]!!.num
                            cardMaps[x1][y]!!.num = 0
                            x--
                            marge = true

                        } else if (cardMaps[x][y]!! == cardMaps[x1][y]) {
                            cardMaps[x][y]!!.num = cardMaps[x][y]!!.num * 2
                            cardMaps[x1][y]!!.num = 0
                            scaleToMax(cardMaps[x][y]!!)
                            MainActivity.mainActivity!!.addScore(cardMaps[x][y]!!.num)
                            marge = true
                        }
                        break
                    }
                }
                x++
            }
        }
        if (marge) {
            canUndo = true
            addRandomNum()
            setCardS()
            checkGameOver()
        }
    }


    private fun swipeRight() {
        var marge = false
        for (y in 0..3) {
            var x = 3
            while (x >= 0) {
                for (x1 in x - 1 downTo 0) {
                    if (cardMaps[x1][y]!!.num > 0) {
                        if (cardMaps[x][y!!]!!.num <= 0) {
                            createMoveAnim(cardMaps[x1][y]!!, cardMaps[x][y]!!)
                            cardMaps[x][y]!!.num = cardMaps[x1][y]!!.num
                            cardMaps[x1][y]!!.num = 0
                            x++
                            marge = true
                        } else if (cardMaps[x][y]!! == (cardMaps[x1][y])) {
                            cardMaps[x][y]!!.num = cardMaps[x][y]!!.num * 2
                            cardMaps[x1][y]!!.num = 0
                            scaleToMax(cardMaps[x][y]!!)
                            MainActivity.mainActivity!!.addScore(cardMaps[x][y]!!.num)
                            marge = true
                        }
                        break
                    }
                }
                x--
            }
        }
        if (marge) {
            canUndo = true
            addRandomNum()
            setCardS()
            checkGameOver()
        }

    }

    private fun swipeDown() {

        var marge = false
        for (x in 0..3) {
            var y = 3
            while (y >= 0) {
                for (y1 in y - 1 downTo 0) {
                    if (cardMaps[x][y1]!!.num > 0) {
                        if (cardMaps[x][y]!!.num <= 0) {
                            createMoveAnim(cardMaps[x][y1]!!, cardMaps[x][y]!!)
                            cardMaps[x][y]!!.num = cardMaps[x][y1]!!.num
                            cardMaps[x][y1]!!.num = 0
                            y++
                            marge = true
                        } else if (cardMaps[x][y]!!.equals(cardMaps[x][y1])) {
                            cardMaps[x][y]!!.num = cardMaps[x][y]!!.num * 2
                            cardMaps[x][y1]!!.num = 0
                            scaleToMax(cardMaps[x][y]!!)
                            MainActivity.mainActivity!!.addScore(cardMaps[x][y]!!.num)
                            marge = true
                        }
                        break
                    }
                }
                y--
            }
        }

        if (marge) {
            canUndo = true
            addRandomNum()
            setCardS()
            checkGameOver()
        }

    }

    private fun swipeUp() {
        var marge = false
        for (x in 0..3) {
            var y = 0
            while (y < 4) {
                for (y1 in y + 1..3) {
                    if (cardMaps[x][y1]!!.num > 0) {
                        if (cardMaps[x][y]!!.num <= 0) {
                            createMoveAnim(cardMaps[x][y1]!!, cardMaps[x][y]!!)
                            cardMaps[x][y]!!.num = cardMaps[x][y1]!!.num
                            cardMaps[x][y1]!!.num = 0
                            y--
                            marge = true

                        } else if (cardMaps[x][y]!!.equals(cardMaps[x][y1])) {
                            cardMaps[x][y]!!.num = cardMaps[x][y]!!.num * 2
                            cardMaps[x][y1]!!.num = 0
                            scaleToMax(cardMaps[x][y]!!)
                            MainActivity.mainActivity!!.addScore(cardMaps[x][y]!!.num)
                            marge = true
                        }
                        break
                    }
                }
                y++
            }
        }
        if (marge) {
            canUndo = true
            addRandomNum()
            setCardS()
            checkGameOver()
        }
    }

    private fun addRandomNum() {
        emptyPoints.clear()
        for (y in 0..3) {
            (0..3).filter { cardMaps[it][y]!!.num <= 0 }.mapTo(emptyPoints) { Point(it, y) }
        }
        val point = emptyPoints.removeAt((Math.random() * emptyPoints.size).toInt())
        cardMaps[point.x][point.y]!!.num = if (Math.random() > 0.1) 2 else 4
        createScaleTo1(cardMaps[point.x][point.y]!!)
    }

    private fun checkGameOver() {
        ALL@ for (y in 0..3) {
            (0..3).filter {
                (cardMaps[it][y]!!.num == 0 ||
                        it > 0 && cardMaps[it][y]!! == (cardMaps[it - 1][y]) ||
                        it < 3 && cardMaps[it][y]!! == (cardMaps[it + 1][y])
                        || y > 0 && cardMaps[it][y]!! == (cardMaps[it][y - 1])
                        || y < 3 && cardMaps[it][y]!! == (cardMaps[it][y + 1]))
            }.forEach { return }
        }
        AlertDialog.Builder(context).setCancelable(false).setTitle("游戏结束").setMessage("你当前的分数是" + MainActivity.mainActivity!!.score + "\n" + "历史最高分" + MainActivity.mainActivity!!.highScore).setPositiveButton("重来"
        ) { _, _ -> startGame() }.show()

    }

    fun undo() {
        canUndo = false
        for (i in 0..3) {
            for (j in 0..3) {
                cardMaps[j][i]!!.num = bufferCards[j][i]!!.num
            }
        }
        for (i in 0..3) {
            for (j in 0..3) {
                nextCards[j][i]!!.num = cardMaps[j][i]!!.num
            }
        }
    }

    fun setCardS() {
        for (i in nextCards.indices) {
            for (j in 0 until nextCards[0].size) {
                bufferCards[j][i]!!.num = nextCards[j][i]!!.num
            }
        }
        for (i in nextCards.indices) {
            for (j in 0 until nextCards[0].size) {
                nextCards[j][i]!!.num = cardMaps[j][i]!!.num
            }
        }

    }

    fun getCards(i: Int, j: Int) = cardMaps[i][j]!!


    fun setTemp() {
        cardList.add(temp)
    }

    fun getTempCards(i: Int, j: Int) = cardList.peek()[i][j]!!


    fun initCards() {
        for (i in nextCards.indices) {
            for (j in 0 until nextCards[0].size) {
                val card = TempCard(0)
                bufferCards[i][j] = card
            }
        }
        for (i in nextCards.indices) {
            for (j in 0 until nextCards[0].size) {
                val card = TempCard(0)
                nextCards[i][j] = card
            }
        }
    }

    fun createMoveAnim(from: Card, to: Card) {

        to.label.visibility = View.INVISIBLE
        val c = getCard(from.num)
        val lp = FrameLayout.LayoutParams(Constant.CARD_WIDTH, Constant.CARD_WIDTH)
        lp.leftMargin = from.x.toInt()
        lp.topMargin = from.y.toInt()
        c.layoutParams = lp

        val ta = TranslateAnimation(0f, (to.x - from.x), 0f, to.y - from.y)
        ta.duration = 100
        ta.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                recycleCard(c)
                to.label.visibility = View.VISIBLE
            }
        })
        c.startAnimation(ta)
    }

    private fun getCard(num: Int): Card {
        val c: Card
        if (cards.size > 0) {
            c = cards.removeAt(0)
        } else {
            c = Card(context)
            addView(c)
        }
        c.visibility = View.VISIBLE
        c.num = num
        return c
    }

    private fun recycleCard(c: Card) {
        c.visibility = View.INVISIBLE
        c.animation = null
        cards.add(c)
    }

    fun createScaleTo1(target: Card) {
        val sa = ScaleAnimation(0.0f, 1f, 0.0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        sa.duration = 100
        target.animation = null
        target.startAnimation(sa)

    }

    fun scaleToMax(target: Card) {
        target.animation = null
        target.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale))
    }


}