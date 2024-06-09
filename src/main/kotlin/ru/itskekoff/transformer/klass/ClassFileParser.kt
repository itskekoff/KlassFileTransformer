package ru.itskekoff.transformer.klass

import ru.itskekoff.transformer.cryptography.XorEncryption
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_CLASS
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_STRING
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_METHODTYPE
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_MODULE
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_PACKAGE
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_FIELDREF
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_INTERFACEMETHODREF
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_METHODREF
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_NAMEANDTYPE
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_DYNAMIC
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_INVOKEDYNAMIC
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_METHODHANDLE
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_INTEGER
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_FLOAT
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_LONG
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_DOUBLE
import ru.itskekoff.transformer.klass.ClassFileConstants.JVM_CONSTANT_UTF8
import ru.itskekoff.transformer.klass.stream.ClassFileReader
import ru.itskekoff.transformer.klass.writer.impl.ClassFileWriter
import java.io.IOException

class ClassFileParser(private val reader: ClassFileReader, private val writer: ClassFileWriter) {
    private val encryption = XorEncryption()
    private val encryptionKey = "я ебал жену обамы мне сосала дочка трампа у"
    private var codeAttributeNameIndex: Short = -1

    private fun parseConstantPoolEntries(constantPoolCount: Int) {
        try {
            var index = 1
            while (index < constantPoolCount) {
                val tag = reader.readByte()
                writer.writeTag(tag)
                when (tag) {
                    JVM_CONSTANT_CLASS,
                    JVM_CONSTANT_STRING,
                    JVM_CONSTANT_METHODTYPE,
                    JVM_CONSTANT_MODULE,
                    JVM_CONSTANT_PACKAGE -> {
                        writer.writeShort(reader.readShort())
                    }

                    JVM_CONSTANT_FIELDREF,
                    JVM_CONSTANT_INTERFACEMETHODREF,
                    JVM_CONSTANT_METHODREF,
                    JVM_CONSTANT_NAMEANDTYPE,
                    JVM_CONSTANT_DYNAMIC,
                    JVM_CONSTANT_INVOKEDYNAMIC -> {
                        val referenceIndex = reader.readShort()
                        val nameAndTypeIndex = reader.readShort()
                        writer.writeShort(referenceIndex)
                        writer.writeShort(nameAndTypeIndex)
                    }

                    JVM_CONSTANT_METHODHANDLE -> {
                        val referenceKind = reader.readByte()
                        val referenceIndex = reader.readShort()
                        writer.writeByte(referenceKind.toByte())
                        writer.writeShort(referenceIndex)
                    }

                    JVM_CONSTANT_INTEGER -> {
                        writer.writeInt(reader.readInt())
                    }

                    JVM_CONSTANT_FLOAT -> {
                        writer.writeFloat(reader.readFloat())
                    }

                    JVM_CONSTANT_LONG -> {
                        writer.writeLong(reader.readLong())
                        index++
                    }

                    JVM_CONSTANT_DOUBLE -> {
                        writer.writeDouble(reader.readDouble())
                        index++
                    }

                    JVM_CONSTANT_UTF8 -> {
                        var utfConstant = reader.readUTF()
                        if (utfConstant == "Code") {
                            codeAttributeNameIndex = index.toShort()
                        }
                        utfConstant = encryption.encrypt(utfConstant.toByteArray(), encryptionKey).toString()
                        writer.writeUTF(utfConstant)
                    }
                }
                index++
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun parseInterfaces(interfacesLength: Int) {
        if (interfacesLength > 0) {
            for (index in 0 until interfacesLength) {
                val interfaceIndex = reader.readShort()
                writer.writeShort(interfaceIndex)
            }
        }
    }

    @Throws(IOException::class)
    private fun parseMembers(count: Int, encryptBytecode: Boolean) {
        if (count > 0) {
            for (i in 0 until count) {
                val accessFlags = reader.readShort()
                val nameIndex = reader.readShort()
                val descriptorIndex = reader.readShort()
                writer.writeShort(accessFlags)
                writer.writeShort(nameIndex)
                writer.writeShort(descriptorIndex)
                val attributesCount = reader.readShort()
                writer.writeShort(attributesCount)
                for (attr in 0 until attributesCount) {
                    parseAttribute(encryptBytecode)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun parseFields(fieldsCount: Int, encryptBytecode: Boolean) {
        parseMembers(fieldsCount, encryptBytecode)
    }

    @Throws(IOException::class)
    private fun parseMethods(methodCounts: Int, encryptBytecode: Boolean) {
        parseMembers(methodCounts, encryptBytecode)
    }

    @Throws(IOException::class)
    private fun parseAttributes(attributesCount: Int, encryptBytecode: Boolean) {
        if (attributesCount > 0) {
            for (i in 0 until attributesCount) {
                parseAttribute(encryptBytecode)
            }
        }
    }

    @Throws(IOException::class)
    private fun parseAttribute(encryptBytecode: Boolean) {
        val attributeNameIndex = reader.readShort()
        writer.writeShort(attributeNameIndex)
        val attributeLength = reader.readInt()
        writer.writeInt(attributeLength)
        val attributeInfo = ByteArray(attributeLength)
        reader.readFully(attributeInfo)

        if (encryptBytecode && attributeNameIndex.toShort() == codeAttributeNameIndex) {
            val encryptedAttributeInfo = encryption.encrypt(attributeInfo, encryptionKey)
            writer.writeBytes(encryptedAttributeInfo)
        } else {
            writer.writeBytes(attributeInfo)
        }
    }


    @Throws(IOException::class)
    fun parseStream(encryptBytecode: Boolean) {
        reader.readInt() // magic bytes

        val magicBytes = listOf(0xCA.toByte(), 0xFE.toByte(), 0xBA.toByte(), 0xBE.toByte())

        for (byte in magicBytes) {
            writer.writeByte(byte)
        }

        writer.writeInt(reader.readInt()) // java version (minor/major)

        val constantPoolCount = reader.readShort()
        writer.writeShort(constantPoolCount)

        parseConstantPoolEntries(constantPoolCount)

        val flags = reader.readShort()
        writer.writeShort(flags)

        writer.writeShort(reader.readShort()) // this_class
        writer.writeShort(reader.readShort()) // super_class

        val interfacesLength = reader.readShort()
        writer.writeShort(interfacesLength)

        parseInterfaces(interfacesLength)

        val fieldsCount = reader.readShort()
        writer.writeShort(fieldsCount)

        parseFields(fieldsCount, encryptBytecode)

        val methodsCounts = reader.readShort()
        writer.writeShort(methodsCounts)

        parseMethods(methodsCounts, encryptBytecode)

        val attributesCount = reader.readShort()
        writer.writeShort(attributesCount)

        parseAttributes(attributesCount, encryptBytecode)

        reader.pushStream(writer.byteArrayOutputStream)
    }
}
