package com.hyunkyung.cs4750.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    var currentIndex = 0
    var numberCorrect = 0
    var numberAnswered = 0
    var isCheater = false

    private val questionBank = listOf (
        Question(R.string.q1,true),
        Question(R.string.q2,false),
        Question(R.string.q3,true),
        Question(R.string.q4,false),
        Question(R.string.q5,false),
        Question(R.string.q6,true)
    )

    var statusBank = BooleanArray(questionBank.size)
    var cheatStatus = BooleanArray(questionBank.size)

    val currentQuestionText: Int get() = questionBank[currentIndex].textResId
    val currentQuestionAnswer: Boolean get() = questionBank[currentIndex].answer
    val currentQuestionStatus: Boolean get() = statusBank[currentIndex]
    val hasCheated: Boolean get() = cheatStatus[currentIndex]

    fun moveToPrev() {
        if (currentIndex - 1 < 0) { currentIndex = questionBank.size-1 }
        else { currentIndex-- }
    }
    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun markCorrect() {
        numberCorrect++
        Log.d(TAG,"question $currentIndex correct")
    }
    fun markAnswered() {
        statusBank[currentIndex] = true
        numberAnswered++
        Log.d(TAG,"question $currentIndex marked done, $numberAnswered answered")
    }
    fun markCheated() {
        cheatStatus[currentIndex] = true
        statusBank[currentIndex] = true
        numberAnswered++
        Log.d(TAG, "question $currentIndex marked cheated and wrong")
    }

    fun checkDone(): Boolean {
        return (numberAnswered == questionBank.size)
    }

    fun calculateScore():String {
        return "%.2f".format((numberCorrect.toFloat() / numberAnswered) * 100)
    }


    // sets all questions back to unanswered
    // utility for easier testing, just to be able to mark all questions as undone without restarting the app
    fun restart() {
        for(i in questionBank.indices) {
            statusBank[i] = false
            cheatStatus[i] = false
        }
        numberCorrect = 0
        numberAnswered = 0
        isCheater = false
        Log.d(TAG,"$numberCorrect correct, $numberAnswered answered")
    }

}