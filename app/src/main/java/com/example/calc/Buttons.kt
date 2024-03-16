package com.example.calc

import android.app.Activity
import android.content.Context
import android.widget.Button
import android.widget.TextView
import java.util.*
import java.math.BigDecimal
import java.math.RoundingMode

class Buttons(private val context: Context) {
    private val buttonIds = intArrayOf(
        R.id.button, R.id.button2, R.id.button3, R.id.button4,
        R.id.button5, R.id.button6, R.id.button7, R.id.button8,
        R.id.button9, R.id.button10, R.id.button11, R.id.button12,
        R.id.button13, R.id.button14, R.id.button15, R.id.button16,
        R.id.button17, R.id.button18, R.id.button0, R.id.leftbracket, R.id.rightbracket
    )
    // checks if given char is number
    private fun isNumber (char: Char): Boolean {
        if(char.digitToIntOrNull() != null){
            return true

        }else if (char=='(' || char==')'){
            return true
        }
        return false
    }
    // checks last written char
    private fun check (): Int {
        val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
        val text = textView?.text
        if (text != null) {
            if(text.isNotEmpty()){
                val lastchar = text.last()
                if (isNumber(lastchar)){
                    return 0
                }
            }
        }
        return 1
    }

    fun press(){
        for (buttonId in buttonIds) {
            val button = (context as? Activity)?.findViewById<Button>(buttonId)
            button?.setOnClickListener {
                // Handle button click
                val buttonText = button.text.toString()
                val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
                val text = textView?.text
                if (text != null) {
                    if (buttonText=="Delete" && text.isNotEmpty()){
                        delete()
                    }
                    else if (buttonText=="AC" && text.isNotEmpty()){
                        val newText = text.substring(0, 0)
                        textView.text = newText
                        if (newText.isEmpty()){
                            val zero = newText.plus("0")
                            textView.text = zero
                        }
                    }
                    // Digit + digit
                    else if ((check()==0 && isNumber(buttonText.last())) && (numberCheck() || !isNumber(buttonText.last()))){
                        if (isNumber(buttonText.last()) && zeroRule(buttonText.last())){
                            display(buttonText)
                        }
                        else if (!isNumber(buttonText.last())){
                            display(buttonText)
                        }
                    }
                    // Operator and other + digit
                    else if ((check()==1 && isNumber(buttonText.last())) && (numberCheck() || !isNumber(buttonText.last()))){
                        if (isNumber(buttonText.last()) && zeroRule(buttonText.last())){
                            display(buttonText)
                        }
                        else if (!isNumber(buttonText.last())){
                            display(buttonText)
                        }
                    }
                    // Digit + operator and other
                    else if (check()==0 && !isNumber(buttonText.last())){
                        if(buttonText=="." && pointCheck()){
                            display(buttonText)
                        }else if (!isNumber(buttonText.last()) && buttonText!="."){
                            display((buttonText))
                        }
                    }
                    // Point rule.
                    else if (buttonText=="."){
                        if (pointCheck()){
                            display((buttonText))
                        }
                    }
                }

            }
        }
    }
    fun pressequal(){
        val button = (context as? Activity)?.findViewById<Button>(R.id.button17)
        button?.setOnClickListener {
            val calculation = Calculation(context)
            display("=" + calculation.calculate().toString())
        }
    }
    private fun display(buttonText: String){
        val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
        val text = textView?.text
        if (textView != null && !(buttonText=="Delete" || buttonText=="AC" || buttonText=="=")) {
            val newText = text.toString().plus(buttonText)
            textView.text = newText
            textView.scrollTo(0, textView.height)
        }
    }
    private fun numberCheck(): Boolean {
        var limit = 19
        val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
        val text = textView?.text
        if (text != null) {
            for (i in text.length - 1 downTo  0){
                if (isNumber(text[i])){
                    limit--
                }
                if (limit==0){
                    return false
                }
                if (!isNumber(text[i]) && text[i] != '.'){
                    return true
                }
            }
        }
        return true
    }

    private fun pointCheck(): Boolean {
        val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
        val text = textView?.text
        if (text != null) {
            for (g in text.length-1 downTo 0){
                val ch = text[g]
                if (!isNumber(ch) && ch=='.'){
                    return false
                }
                else if (!isNumber(ch) && ch!='.'){
                    return true
                }
                else if (ch=='(' || ch==')'){
                    return true
                }
            }
        }
        return true
    }


    private fun zeroRule(input: Char): Boolean {
        val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
        val text = textView?.text
        if (text != null) {
            for (i in text.length - 1 downTo  0){
                val char = text[i]
                if (isNumber(char) && char!='0' || !isNumber(char)){
                    return true
                }
                else if (char=='0'){
                    for (j in text.length-1 downTo 0){
                        val ch =text[j]
                        if (ch=='.'){
                            return true
                        }
                        else if (!isNumber(ch)){
                            return false
                        }
                        else if (ch=='0' && isNumber(input) && input!='0'){
                            if (text.length==1){
                                val newText = text.substring(0, (text.length) - 1)
                                textView.text = newText
                                return true
                            }
                            for (k in text.length-1 downTo 0){
                                val cha = text[k]
                                if (cha=='.' || (isNumber(cha) && cha!='0')){
                                    return true
                                }else if (!isNumber(cha)){
                                    return false
                                }
                            }
                            return false
                        }
                    }
                }
            }
        }
        return false
    }

    private fun delete(){
        val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
        val text = textView?.text
        val newText = text?.substring(0, (text.length) - 1)
        if (textView != null) {
            textView.text = newText
        }
        if (newText != null) {
            if (newText.isEmpty()){
                val zero = newText.toString().plus("0")
                textView.text = zero
            }
        }
    }
}
