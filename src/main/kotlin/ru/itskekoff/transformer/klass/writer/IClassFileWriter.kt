package ru.itskekoff.transformer.klass.writer

import java.io.ByteArrayOutputStream
import java.io.IOException

interface IClassFileWriter {
    val byteArrayOutputStream: ByteArrayOutputStream

    @Throws(IOException::class)
    fun writeByte(b: Byte)

    @Throws(IOException::class)
    fun writeShort(v: Int)

    @Throws(IOException::class)
    fun writeInt(v: Int)

    @Throws(IOException::class)
    fun writeLong(v: Long)

    @Throws(IOException::class)
    fun writeFloat(v: Float)

    @Throws(IOException::class)
    fun writeDouble(v: Double)

    @Throws(IOException::class)
    fun writeUTF(string: String)

    @Throws(IOException::class)
    fun writeBytes(bytes: ByteArray)

    @Throws(IOException::class)
    fun writeTag(tag: Int)
}