package com.ly.module.util

import com.ly.module.util.log.logDebug
import com.ly.module.util.log.logError
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by Lan Yang on 2020/8/21
 *
 * 消息摘要相关的工具类
 *
 * 消息摘要简述：
 * 它是一个唯一个消息或文本对应固定长度的值，它由一个单向Hash函数对消息进行作用而产生。如果消息在途中改变了，
 * 则接收者通过对收到消息的新产生的摘要与原摘要比较，就可知道消息是否被改变了；因此消息摘要保证了消息的完整性。
 * 消息摘要采用单向Hash函数将需加密的明文"摘要"成一串密文，这一串密文亦称为数字指纹(Finger Print)。
 * 它有固定的长度，且不同的明文摘要成密文，其结果总是不同的，而同样的明文其摘要必定一致。
 * 这样这串摘要便可成为验证明文是否是"真身"的"指纹"了。
 *
 * 消息摘要特点：
 * （1）唯一性：数据只要有一点改变，那么再通过消息摘要算法得到的摘要也会发生变化。虽然理论上有可能会发生碰撞，但是概率极其低。
 * （2）不可逆：消息摘要算法的密文无法被解密。
 * （3）不需要密钥，可使用于分布式网络。
 * （4）无论输入的明文有多长，计算出来的消息摘要的长度总是固定的。
 *
 * 消息摘要原理就是将需要摘要的数据作为参数，经过哈希函数(Hash)的计算，得到的散列值。
 *
 * 消息摘要算法包括 MD(Message Digest，消息摘要算法)、SHA(Secure Hash Algorithm，安全散列算法)、
 * MAC(Message AuthenticationCode，消息认证码算法)共3大系列，常用于验证数据的完整性，是数字签名算法的核心算法。
 * MD5和SHA1分别是MD、SHA算法系列中最有代表性的算法。如今，MD5已被发现有许多漏洞，从而不再安全。
 * SHA算法比MD算法的摘要长度更长，也更加安全。
 */
class MessageDigestUtil {
    companion object {

        /**
         * 使用SHA1算法获取[str]唯一的散列值
         */
        fun getSHAMessageDigest(str: String): String {
            return try {
                val digest = MessageDigest.getInstance("SHA1")
                digest.reset()
                digest.update(str.toByteArray(charset("UTF-8")))
                getHexString(digest.digest())
            } catch (e: NoSuchAlgorithmException) {
                logError("MessageDigestUtil-getSHAMessageDigest", "", e)
                str.hashCode().toString()
            }
        }

        /**
         * 将[bytes]数组转为十六进制表示的字符串
         */
        fun getHexString(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (i in bytes.indices) {
                val hex = Integer.toHexString(0xFF and bytes[i].toInt())
                if (hex.length == 1) {
                    sb.append('0')
                }
                sb.append(hex)
            }

            val value = sb.toString().toUpperCase(Locale.getDefault())
            logDebug("MessageDigestUtil-getHexString",value)
            return value
        }
    }
}