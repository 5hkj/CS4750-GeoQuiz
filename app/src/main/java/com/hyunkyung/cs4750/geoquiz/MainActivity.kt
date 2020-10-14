package com.hyunkyung.cs4750.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_CORRECT = "numCorrect"
private const val KEY_ANSWERED = "numAnswered"
private const val KEY_STATUS = "statusBank"
private const val CHEAT_STATUS = "cheatStatus"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    private lateinit var restartButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX) ?: 0
        quizViewModel.currentIndex = currentIndex
        val numberCorrect = savedInstanceState?.getInt(KEY_CORRECT) ?: 0
        quizViewModel.numberCorrect = numberCorrect
        val numberAnswered = savedInstanceState?.getInt(KEY_ANSWERED) ?: 0
        quizViewModel.numberAnswered = numberAnswered
        val statusBank = savedInstanceState?.getBooleanArray(KEY_STATUS) ?: BooleanArray(quizViewModel.statusBank.size)
        quizViewModel.statusBank = statusBank

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        restartButton = findViewById(R.id.restart_button)

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }
        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
        }
        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatButton.setOnClickListener {
            //TODO: start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
            quizViewModel.markCheated()
        }
        restartButton.setOnClickListener {
            restart()
        }
        
        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (resultCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause() called")
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG,"onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX,quizViewModel.currentIndex)
        savedInstanceState.putInt(KEY_CORRECT,quizViewModel.numberCorrect)
        savedInstanceState.putInt(KEY_ANSWERED,quizViewModel.numberAnswered)
        savedInstanceState.putBooleanArray(KEY_STATUS,quizViewModel.statusBank)
        savedInstanceState.putBooleanArray(CHEAT_STATUS,quizViewModel.cheatStatus)
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        var messageResId = 0

        if (quizViewModel.hasCheated) { messageResId = R.string.judgment_toast }
        else if (!quizViewModel.currentQuestionStatus) {
            if (userAnswer == correctAnswer) {
                messageResId = R.string.correct_toast
                quizViewModel.markCorrect()
            }
            else { messageResId = R.string.incorrect_toast }
            quizViewModel.markAnswered()
        }
        else { messageResId = R.string.already_answered }

        val answerToast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        answerToast.setGravity(Gravity.TOP,0,0)
        answerToast.show()
        if (quizViewModel.checkDone()) {
            calculateScore()
        }
    }

    private fun calculateScore() {
        val percentageScore = quizViewModel.calculateScore()
        val scoreToastText = "${getString(R.string.score_toast)} $percentageScore%."
        val scoreToast = Toast.makeText(this,scoreToastText,Toast.LENGTH_LONG)
        scoreToast.setGravity(Gravity.CENTER,0,0)
        scoreToast.show()
    }


    // added for testing
    // sets all questions back to unanswered
    // and correctAnswers and numberAnswered to zero
    private fun restart() {
        quizViewModel.restart()
        val restartToast = Toast.makeText(this,"Progress restarted.",Toast.LENGTH_SHORT)
        restartToast.setGravity(Gravity.CENTER,0,0)
        restartToast.show()
    }
}