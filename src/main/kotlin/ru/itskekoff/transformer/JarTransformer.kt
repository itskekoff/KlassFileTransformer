package ru.itskekoff.transformer

import ru.itskekoff.transformer.klass.parser.IClassFileParser
import ru.itskekoff.transformer.klass.parser.impl.ClassFileParser
import ru.itskekoff.transformer.klass.stream.IClassFileStream
import ru.itskekoff.transformer.klass.stream.impl.ClassFileStream
import ru.itskekoff.transformer.klass.writer.IClassFileWriter
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
            println("Loading files [/]")
            loadJar(args[0])

            println("Parsing files [*]")
            processFiles(
                ::ClassFileWriter,
                ::ClassFileParser
            )

            println("Writing output [/]")
            saveOutput(args[0].split(".jar")[0] + "-output.jar")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun loadJar(input: String) {
        val zip = ZipFile(input)

        zip.use { zipFile ->
            zipFile.entries().asSequence().forEach { entry ->
                try {
                    files[entry.name] = zipFile.getInputStream(entry).readAllBytes()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @JvmStatic
    fun processFiles(
        writerSupplier: () -> IClassFileWriter,
        parserSupplier: (IClassFileStream, IClassFileWriter) -> IClassFileParser
    ) {
        files.forEach { (name, data) ->
            if (isClassFileFormat(name, data)) {
                val classFileWriter = writerSupplier()
                val classFileParser = parserSupplier(ClassFileStream(data), classFileWriter)
                try {
                    println(name)
                    classFileParser.parseStream()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                files[name] = classFileWriter.byteArrayOutputStream.toByteArray()
            }
        }
    }

    @JvmStatic
    fun saveOutput(output: String) {
        ZipOutputStream(FileOutputStream(output)).use { zos ->
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
    }

    private fun isClassFileFormat(name: String, data: ByteArray): Boolean {
        return data.size >= 8 && name.endsWith(".class")
    }
}
