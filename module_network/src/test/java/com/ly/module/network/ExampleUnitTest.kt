package com.ly.module.network

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRequest() {
//        val data = RequestBuilder<TestData>().test()
//        print("name=${data.name}")

//        val callback = object :HttpCallback<TestData>(){}
//        val data =callback.convertData("{\"name\":\"shily\"}")
//        print("name=${data?.name}")

//        val callback = object : RequestBuilder<List<TestData>>() {}
//        val data = callback.convertData1()
//        print("size=${data.size}")

//        val callback = object : ConvertResponseData<Unit>() {}
//        callback.convertData1()
    }

    @Test
    fun testDataClass() {
        val testData1 = TestData(name = "你好", age = TestData1(12))
        println(testData1)
        println(testData1.hashCode())
        println()

        val testData2 = TestData(name = "好", age = TestData1(12))
        println(testData2)
        println(testData2.hashCode())
        println()

        //copy对实例的属性是浅复制
        val copy = testData1.copy()
        println(copy == testData1)
        println(copy === testData1)

        println(copy.name == testData1.name)
        println(copy.name === testData1.name)

        println(copy.age == testData1.age)
        println(copy.age === testData1.age)
    }

    @Test
    fun testSupervisorJob() {
        runBlocking {
            val scope = CoroutineScope(SupervisorJob())

            scope.launch {
                delay(200)
                println("1111")
                throw IllegalArgumentException()
            }

            scope.launch {
                delay(400)
                println("22222")
            }

            delay(1000)
            println("done")
        }
    }
}