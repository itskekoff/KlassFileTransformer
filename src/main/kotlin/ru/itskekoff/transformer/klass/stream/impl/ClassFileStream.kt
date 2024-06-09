package ru.itskekoff.transformer.klass.stream.impl

import ru.itskekoff.transformer.klass.stream.ClassFileReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.IOException

class ClassFileStream(private val buf: ByteArray) : ClassFileReader {
    private val reader = DataInputStream(ByteArrayInputStream(buf))

    override fun readByte(): Int = reader.read()

    override fun readShort(): Int = reader.readShort().toInt()

    override fun readInt(): Int = reader.readInt()

    override fun readLong(): Long = reader.readLong()

    override fun readFloat(): Float = reader.readFloat()

    override fun readDouble(): Double = reader.readDouble()

    override fun readUTF(): String {
        val length = reader.readUnsignedShort()
        val array = ByteArray(length)
        reader.readFully(array)
        return String(array, Charsets.UTF_8)
    }

    override fun readFully(buffer: ByteArray) {
        reader.readFully(buffer)
    }

    override fun hasMoreBytes(): Boolean {
        return reader.available() > 0
    }

    @Throws(IOException::class)
    override fun pushStream(outputStream: ByteArrayOutputStream) {
        outputStream.write(buf)
    }
}
