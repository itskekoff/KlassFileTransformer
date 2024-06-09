package ru.itskekoff.transformer.klass.writer.impl

import ru.itskekoff.transformer.klass.writer.IClassFileWriter
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

class ClassFileWriter : IClassFileWriter {
    override val byteArrayOutputStream = ByteArrayOutputStream()
    private val writer = DataOutputStream(byteArrayOutputStream)

    @Throws(IOException::class)
    override fun writeByte(b: Byte) {
        writer.writeByte(b.toInt())
    }

    @Throws(IOException::class)
    override fun writeShort(v: Int) {
        writer.writeShort(v)
    }

    @Throws(IOException::class)
    override fun writeInt(v: Int) {
        writer.writeInt(v)
    }

    @Throws(IOException::class)
    override fun writeLong(v: Long) {
        writer.writeLong(v)
    }

    @Throws(IOException::class)
    override fun writeFloat(v: Float) {
        writer.writeFloat(v)
    }

    @Throws(IOException::class)
    override fun writeDouble(v: Double) {
        writer.writeDouble(v)
    }

    @Throws(IOException::class)
    override fun writeUTF(string: String) {
        val bytes = string.toByteArray(Charsets.UTF_8)
        writer.writeShort(bytes.size)
        writer.write(bytes)
    }

    @Throws(IOException::class)
    override fun writeBytes(bytes: ByteArray) {
        writer.write(bytes)
    }

    @Throws(IOException::class)
    override fun writeTag(tag: Int) {
        writer.writeByte(tag)
    }
}
