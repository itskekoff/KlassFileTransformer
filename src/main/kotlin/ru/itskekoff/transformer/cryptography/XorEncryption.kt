package ru.itskekoff.transformer.cryptography

import kotlin.experimental.xor

class XorEncryption {
    fun encrypt(dataBytes: ByteArray, key: String): ByteArray {
        val keyBytes = key.toByteArray()
        val encryptedBytes = ByteArray(dataBytes.size)

        for (i in dataBytes.indices) {
            encryptedBytes[i] = (dataBytes[i] xor keyBytes[i % keyBytes.size])
        }

        return encryptedBytes
    }
}