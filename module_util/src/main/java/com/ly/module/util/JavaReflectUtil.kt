package com.ly.module.util

import java.lang.reflect.*

/**
 * Created by Lan Yang on 2020/8/31
 *
 * 当在Kotlin中使用反射时，有两种不同的反射API，分别是标准的Java反射API和Kotlin反射API。
 * 因为Kotlin类会被编译成普通的Java字节码，所以Java反射API可以访问Kotlin类信息。
 * 通过Kotlin反射API，你可以访问那些在Java世界里不存在的概念，诸如属性和可空类型，Kotlin反射API并不是Java反射API的替代方案。
 * 同时，Kotlin反射API没有仅局限于Kotlin类，可以使用同样的API访问用任何JVM语言写的类。
 *
 * 该反射工具类使用的是Java反射API
 */
class JavaReflectUtil {

    companion object {

        /**
         * 循环向上转型, 获取对象的 DeclaredField
         *
         * @param object    对象实例
         * @param filedName 属性名称
         * @return 属性实例
         */
        fun getDeclaredField(`object`: Any, filedName: String): Field? {
            var superClass: Class<*>? = `object`.javaClass
            while (superClass != null) {
                try {
                    return superClass.getDeclaredField(filedName)
                } catch (e: NoSuchFieldException) {
                    superClass = superClass.superclass

                    if (superClass == null)
                        e.printStackTrace()
                }
            }
            return null
        }

        /**
         * 使[field]变为可访问
         *
         * @param field 属性
         */
        fun makeAccessible(field: Field) {

            if (!Modifier.isPublic(field.modifiers)) {
                field.isAccessible = true
            }

        }

        /**
         * 直接设置对象属性值,忽略private/protected修饰符
         *
         * @param object    对象实例
         * @param fieldName 属性名称
         * @param value     字段值
         * @throws IllegalArgumentException 没有找到该字段时,抛出该异常
         */
        fun setFieldValue(
            `object`: Any,
            fieldName: String,
            value: Any
        ) {

            val field = getDeclaredField(`object`, fieldName)
                ?: throw IllegalArgumentException("在[$`object`]中没有找到字段[$fieldName]")

            makeAccessible(field)

            try {
                field.set(`object`, value)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }

        /**
         * 循环向上转型, 获取对象的DeclaredMethod
         *
         * @param object         对象实例
         * @param methodName     方法名称
         * @param parameterTypes 方法参数类型
         * @return 方法实例
         */
        fun getDeclaredMethod(
            `object`: Any,
            methodName: String,
            parameterTypes: Array<Class<*>>
        ): Method? {
            var superClass: Class<*>? = `object`.javaClass

            while (superClass != null) {
                try {
                    return superClass.getDeclaredMethod(methodName, *parameterTypes)
                } catch (e: NoSuchMethodException) {
                    superClass = superClass.superclass

                    if (superClass == null)
                        e.printStackTrace()
                }
            }

            return null
        }

        /**
         * 直接调用对象方法, 而忽略修饰符(private, protected)
         *
         * @param object         对象实例
         * @param methodName     方法名称
         * @param parameterTypes 方法参数类型
         * @param parameters     方法参数值
         * @return 对象方法执行后的结果
         * @throws IllegalArgumentException 没有找到该方法时,抛出该异常
         */
        fun invokeMethod(
            `object`: Any,
            methodName: String,
            parameterTypes: Array<Class<*>>,
            parameters: Array<Any>
        ): Any? {

            val method = getDeclaredMethod(`object`, methodName, parameterTypes)
                ?: throw IllegalArgumentException("在[$`object`]中没有找到方法[$methodName]")

            method.isAccessible = true

            try {
                return method.invoke(`object`, *parameters)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

            return null
        }
    }

    /**
     * 获得定义Class时声明的父类的某个泛型参数类型
     *
     * @param clazz 子类对应的Class对象
     * @param index 子类继承父类时传入的泛型的索引,从0开始
     * @return 泛型参数类型
     */
    fun getSuperClassGenericType(clazz: Class<*>, index: Int): Class<*> {

        val genType = clazz.genericSuperclass as? ParameterizedType ?: return Any::class.java

        val params = genType.actualTypeArguments

        if (index >= params.size || index < 0) {
            return Any::class.java
        }

        return if (params[index] !is Class<*>) {
            Any::class.java
        } else {
            params[index] as Class<*>
        }

    }
}