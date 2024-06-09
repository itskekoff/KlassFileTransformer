package ru.itskekoff.transformer

import ru.itskekoff.transformer.klass.ClassFileParser
import ru.itskekoff.transformer.klass.stream.impl.ClassFileStream
import ru.itskekoff.transformer.klass.writer.impl.ClassFileWriter
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.system.exitProcess

object JarTransformer {
    private val files = ConcurrentHashMap<String, ByteArray>()

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            println("java -jar protect.jar input.jar")
            exitProcess(-1)
        }
        try {
            val zip = ZipFile(args[0])

            println("Loading files [/]")

            zip.use { zipFile ->
                zipFile.entries().asSequence().forEach { entry ->
                    try {
                        files[entry.name] = zipFile.getInputStream(entry).readAllBytes()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            println("Parsing files [*]")

            files.forEach { (name, data) ->
                if (isClassFileFormat(name, data)) {
                    val classFileWriter = ClassFileWriter()
                    val classFileParser = ClassFileParser(ClassFileStream(data), classFileWriter)
                    try {
                        println(name)
                        classFileParser.parseStream(true)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    files[name] = classFileWriter.byteArrayOutputStream.toByteArray()
                }
            }

            println("Writing output [/]")

            ZipOutputStream(FileOutputStream(args[0].split(".jar")[0] + "-output.jar")).use { zos ->
                zos.setLevel(9)
                files.forEach { (name, data) ->
                    try {
                        zos.putNextEntry(ZipEntry(name))
                        zos.write(data)
                        zos.closeEntry()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isClassFileFormat(name: String, data: ByteArray): Boolean {
        return data.size >= 8 && name.endsWith(".class")
    }
}
