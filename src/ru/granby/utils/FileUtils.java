package ru.granby.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    public static String getFileContent(String path) throws IOException {
        return String.join("\n", Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8));
    }
}
