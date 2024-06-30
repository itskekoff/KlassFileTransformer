package ru.itskekoff.transformer.klass.parser

import java.io.IOException

interface IClassFileParser {
    fun parseConstantPoolEntries(constantPoolCount: Int)
    @Throws(IOException::class)
    fun parseInterfaces(interfacesLength: Int)
    @Throws(IOException::class)
    fun parseMembers(count: Int)
    @Throws(IOException::class)
    fun parseFields(fieldsCount: Int)
    @Throws(IOException::class)
    fun parseMethods(methodCounts: Int)
    @Throws(IOException::class)
    fun parseAttributes(attributesCount: Int)
    @Throws(IOException::class)
    fun parseStream()
}