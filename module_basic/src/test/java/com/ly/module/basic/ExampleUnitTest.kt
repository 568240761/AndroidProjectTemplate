package com.ly.module.basic

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testIterator() {
        val numbers = mutableListOf("one", "two", "three", "four")
        val mutableIterator = numbers.iterator()

        while (mutableIterator.hasNext()) {
            mutableIterator.next()
            mutableIterator.remove()
            println("After removal: $numbers")
        }
    }
}