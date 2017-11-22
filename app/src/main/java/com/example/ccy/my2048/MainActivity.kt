package com.example.ccy.my2048

import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Window
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var score = 0
        private set

    var gameView :GameView? = null
    private val KEY_HIGH_SCORE = "high_score"
    private val KEY_SCORE = "score"
    val highScore: Int
        get() {

            val settings = PreferenceManager.getDefaultSharedPreferences(this)
            return settings.getInt(KEY_HIGH_SCORE, -1)
        }

    init {
        mainActivity = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        initView()


    }

    fun initView() {
        tv_score.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        tv_1.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        tv_2.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        tv_tip.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        tv_title.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        tv_highscore.typeface = Typeface.createFromAsset(resources.assets, "ClearSans-Bold.ttf")
        gameView = findViewById(R.id.gameView)
        iv_restart.setOnClickListener { gameView!!.startGame() }

        iv_undo.setOnClickListener {
            if (gameView!!.canUndo)
                gameView!!.undo()
        }
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in 0 until Constant.LINE) {
            for (j in 0 until Constant.LINE) {
                gameView!!.addCard(settings.getInt((j + i * Constant.LINE).toString(), 0), i, j)
            }
        }
        for (i in 0 until Constant.LINE) {
            for (j in 0 until Constant.LINE) {
                gameView!!.addTempCards(settings.getInt((j + i * Constant.LINE).toString() + "t", 0), i, j)
            }
        }
        gameView!!.canUndo = settings.getBoolean("cando", false)
        gameView!!.setTemp()
        score = settings.getInt(KEY_SCORE, -1)
        showScore()
        showHigScore()
    }


    override fun onPause() {
        super.onPause()
        println("OnPause")
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = settings.edit()
        for (i in 0 until Constant.LINE) {
            for (j in 0 until Constant.LINE) {
                editor.putInt((j + i * Constant.LINE).toString(), gameView!!.getCards(j, i).num)
            }
        }
        if (gameView!!.canUndo)
            for (i in 0 until Constant.LINE) {
                for (j in 0 until Constant.LINE) {
                    editor.putInt((j + i * Constant.LINE).toString() + "t", gameView!!.getTempCards(j, i).num)
                }
            }
        editor.putBoolean("cando", gameView!!.canUndo)
        editor.putInt(KEY_SCORE, score)
        editor.apply()
    }


    fun clearScore() {
        this.score = 0
        showScore()
    }

    fun showScore() {
        tv_score.text = score.toString()
    }

    fun addScore(s: Int) {
        score += s
        showScore()
        val maxScore = Math.max(score, highScore)
        saveHighScore(maxScore)
        showHigScore()
    }

    private fun saveHighScore(score: Int) {
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = settings.edit()
        editor.putInt(KEY_HIGH_SCORE, score)
        editor.apply()

    }

    fun showHigScore() {
        tv_highscore.text = highScore.toString()
    }

    companion object {
        var mainActivity: MainActivity? = null
    }


}
