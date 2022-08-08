package com.rx.starfang

import java.util.*

class MultiBaseNotation(private val baseList: MutableList<Int>?= null, private var negativeBaseList: MutableList<Int>? = null) {

    /*
    A23.035 = 10(12) 2(3) 3(4) . 0(2) 3(7) 5(8)
    = 10*(3*4*1) + 2*(4*1) + 3*(1) + 0/2 + 3/(2*7) + 5/(2*7*8)
    -> bases = {4,3,12};
    -> digits = {3,2,10}
    -> negative-bases = { 2,7,8 }
    -> decimal point digits = { 0,3,5 }
     */

    /*
    A23.035 = 10(12) 2(3) 3(4) . 0(2) 3(7) 5(8)
    = 10*(3*4*1) + 2*(4*1) + 3*(1) + 0/2 + 3/(2*7) + 5/(2*7*8)
    -> bases = {4,3,12};
    -> digits = {3,2,10}
    -> negative-bases = { 2,7,8 }
    -> decimal point digits = { 0,3,5 }
     */


    /*
    params digits : 12325 => {5,2,3,2,1}
     */
    @Throws(NullPointerException::class, IndexOutOfBoundsException::class)
    fun getIntegerValue(digits: IntArray): Int {
        if (baseList == null) throw NullPointerException()
        if (digits.size > baseList!!.size) throw IndexOutOfBoundsException()
        var sumOfProduct = 0
        for (i in digits.indices) {
            sumOfProduct += digits[i] * productOfBases(0, i)
        }
        return sumOfProduct
    }

    /*
    params digits : 12325 => {5,2,3,2,1}
    decimalPointDigits : .1352 => {1,3,5,2} -1 -2 -3 -4
     */
    @Throws(NullPointerException::class, IndexOutOfBoundsException::class)
    fun getValue(digits: IntArray, decimalPointDigits: IntArray): Double {
        val integerValue = getIntegerValue(digits)
        if (negativeBaseList == null) throw NullPointerException()
        if (decimalPointDigits.size > negativeBaseList!!.size) throw IndexOutOfBoundsException()
        var negativeSumOfProduct = 0.0
        val negativeStartIndex = -1 * decimalPointDigits.size
        for (ni in negativeStartIndex..-1) { // ni : -4 -3 -2 -1
            negativeSumOfProduct += decimalPointDigits[-1 * ni - 1].toDouble() / productOfBases(
                ni,
                0
            ).toDouble()
        }
        return integerValue.toDouble() + negativeSumOfProduct
    }

    // 2, 3, 4
    // pod 0 = 1
    // pod 1 = 2
    // pod 2 = 2 * 3
    // pod 3 = 2 * 3 * 4
    @Throws(IndexOutOfBoundsException::class, NullPointerException::class)
    private fun productOfBases(beginIndex: Int, endIndex: Int): Int {
        if (beginIndex > endIndex) throw IndexOutOfBoundsException("beginIndex must equals or be smaller than endIndex")
        if (beginIndex < 0 && negativeBaseList == null) throw IndexOutOfBoundsException("negativeMultiBaseSystem is not set, beginIndex($beginIndex) must be greater than or equals 0")
        val figures = baseList!!.size
        if (endIndex > figures) throw IndexOutOfBoundsException("endIndex(" + endIndex + ") must be smaller than " + figures + 1)
        var product = 1
        if (negativeBaseList != null && beginIndex < 0) {
            val negativeBeginIndex = -1 * beginIndex - 1
            val negativeFigures = negativeBaseList!!.size
            if (negativeBeginIndex >= negativeFigures) throw IndexOutOfBoundsException("beginIndex(" + beginIndex + ") must be bigger than " + -1 * (negativeFigures + 1))
            for (i in negativeBeginIndex downTo 0) {
                product *= negativeBaseList!![i]
            }
            for (i in 0 until endIndex) {
                product *= baseList!![i]
            }
        } // if negativeMultiBaseSystem != null && beginIndex < 0
        else {
            for (i in beginIndex until endIndex) {
                product *= baseList!![i]
            }
        } // else : calculate only positive section
        return product
    }


    @Throws(NullPointerException::class, IndexOutOfBoundsException::class)
    private fun getPositiveBaseDigitsInCase(caseIndex: Int): IntArray {
        val digits = IntArray(baseList!!.size)
        for (i in digits.indices) {
            digits[i] = caseIndex % productOfBases(0, i + 1) / productOfBases(0, i)
        }
        /*
        n = 0 * pod(3) + d[2] * pod(2) + d[1] * pod(1) + d[0] * pod(0);
        d[2] = n % pos(3) / pod(2)
        d[1] = n % pod(2) / pod(1)
        d[0] = n % pos(1) / pos(0)

        d[i] = n % pod(i+1) /  pod(i)
         */
        return digits
    }


    @Throws(NullPointerException::class, IndexOutOfBoundsException::class)
    fun getPositiveBaseDigitsCombination(nocBoundary: Int): List<IntArray>? {
        if (baseList == null) throw NullPointerException("please set multi-base system")
        val noc = productOfBases(0, baseList!!.size)
        if (noc > nocBoundary) return null
        val combinationList: MutableList<IntArray> = ArrayList()
        for (caseIndex in 0 until noc) {
            combinationList.add(getPositiveBaseDigitsInCase(caseIndex))
        }
        return combinationList
    }

}