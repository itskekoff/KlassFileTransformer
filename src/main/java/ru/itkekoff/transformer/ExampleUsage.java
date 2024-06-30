package ru.itkekoff.transformer;

import ru.itskekoff.transformer.JarTransformer;
import ru.itskekoff.transformer.klass.parser.impl.ClassFileParser;
import ru.itskekoff.transformer.klass.writer.impl.ClassFileWriter;

/**
 * @author itskekoff
 * @since 14:08 of 30.06.2024
 */
public class ExampleUsage {
    public static void main(String[] args) {
        JarTransformer.loadJar("input.jar");
        JarTransformer.processFiles(ClassFileWriter::new, ClassFileParser::new);
        JarTransformer.saveOutput("input-out.jar");
    }
}
