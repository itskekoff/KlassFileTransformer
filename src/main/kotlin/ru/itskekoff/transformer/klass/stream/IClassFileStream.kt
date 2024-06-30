package ru.itskekoff.transformer.klass.stream

import java.io.ByteArrayOutputStream
import java.io.IOException


interface IClassFileStream {
    fun readByte(): Int
    fun readShort(): Int
    fun readInt(): Int
    fun readLong(): Long
    fun readFloat(): Float
    fun readDouble(): Double
    fun readUTF(): String
    @Throws(IOException::class)
    fun readFully(buffer: ByteArray)
    @Throws(IOException::class)
    fun hasMoreBytes(): Boolean
    fun pushStream(outputStream: ByteArrayOutputStream)
}