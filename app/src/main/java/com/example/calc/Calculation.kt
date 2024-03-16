package com.example.calc

import android.app.Activity
import android.content.Context
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.ArrayList
import java.util.Stack

class Calculation(private val context: Context) {

    private fun isNumber (char: Char): Boolean {
        if(char.digitToIntOrNull() != null){
            return true

        }else if (char=='(' || char==')'){
            return true
        }
        return false
    }

    fun calculate(): Any {
        return calculatePostfix(infixToPostfix(expressionList()))
    }

    fun expressionList(): ArrayList<String> {
        val textView = (context as? Activity)?.findViewById<TextView>(R.id.editTextText)
        val text = textView?.text
        val expression = ArrayList<String>()
        var number = ""
        if (text != null) {
            for (i in text.indices){
                if (isNumber(text[i]) && !((text[i] != '(') xor (text[i] != ')')) || text[i] == '.'){
                    number += (text[i])
                }
                else {
                    if (number.isNotEmpty()){
                        expression.add(number)
                        number = ""
                    }
                    expression.add(text[i].toString())
                }
            }
            if (number.isNotEmpty()){
                expression.add(number)
            }
        }
        return expression
    }

    fun infixToPostfix(infixExpression: ArrayList<String>): List<String> {
        val precedence = mapOf("+" to 1, "-" to 1, "x" to 2, "/" to 2, "%" to 2)
        val output = mutableListOf<String>()
        val stack = mutableListOf<String>()
        val exp = convertExpression(infixExpression)

        exp.forEach { token ->
            when {
                token.matches(Regex("\\d*\\.?\\d+")) -> output.add(token) // Operand with or without leading digits
                token == "(" -> stack.add(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        output.add(stack.removeAt(stack.lastIndex))
                    }
                    stack.removeAt(stack.lastIndex) // Remove "(" from stack
                }
                precedence.containsKey(token) -> {
                    while (stack.isNotEmpty() && stack.last() != "(" &&
                        precedence.getOrDefault(token, 0) <= precedence.getOrDefault(stack.last(), 0)
                    ) {
                        output.add(stack.removeAt(stack.lastIndex))
                    }
                    stack.add(token)
                }
            }
        }

        while (stack.isNotEmpty()) {
            output.add(stack.removeAt(stack.lastIndex))
        }

        return output
    }


    fun convertExpression(expression: ArrayList<String>): ArrayList<String> {
        val convertedExpression = ArrayList<String>()

        for (i in 0 until expression.size) {
            val current = expression[i]
            val next = if (i < expression.size - 1) expression[i + 1] else null

            convertedExpression.add(current)

            // Insert multiplication between a number and an opening parenthesis
            if (next != null && (current.matches(Regex("\\d")) && next == "(")) {
                convertedExpression.add("x")
            }

            // Insert multiplication between a closing parenthesis and a number
            if (next != null && (current == ")" && next.matches(Regex("\\d")))) {
                convertedExpression.add("x")
            }

            // Insert multiplication between adjacent parentheses
            if (next != null && (current == ")" && next == "(")) {
                convertedExpression.add("x")
            }
        }

        return convertedExpression
    }


    fun calculatePostfix(postfixList: List<String>): Any {
        val stack = Stack<BigDecimal>()

        try {
            for (token in postfixList) {
                when {
                    token.matches(Regex("-?\\d*\\.?\\d+")) -> stack.push(BigDecimal(token))
                    token.length == 1 -> {
                        val operand2 = stack.pop()
                        val operand1 = stack.pop()
                        val result = when (token[0]) {
                            '+' -> operand1 + operand2
                            '-' -> operand1 - operand2
                            'x' -> operand1 * operand2
                            '/' -> {
                                if (operand2.compareTo(BigDecimal.ZERO) == 0) {
                                    return "Error: Division by zero"
                                } else {
                                    operand1.divide(operand2, 10, RoundingMode.HALF_UP).stripTrailingZeros()
                                }
                            }
                            '%' -> operand1 % operand2
                            else -> return "Error: Invalid expression"
                        }
                        stack.push(result)
                    }
                    else -> return "Error: Invalid expression"
                }
            }

            if (stack.size != 1) {
                return "Error: Invalid expression"
            }

            return stack.pop().toPlainString()
        } catch (e: Exception) {
            return "Error"
        }
    }
}