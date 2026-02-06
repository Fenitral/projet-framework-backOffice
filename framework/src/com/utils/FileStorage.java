package com.utils;

import java.nio.file.*;
import java.io.*;
import java.util.UUID;

public class FileStorage {

    private static String uploadDir;
    public static void init(String basePath) {
        uploadDir = basePath;
    }

    public static String save(byte[] data, String original) throws IOException {

        if (uploadDir == null)
            throw new IllegalStateException("FileStorage not initialized");

        Files.createDirectories(Paths.get(uploadDir));

        String filename = UUID.randomUUID() + "_" + original;
        Path target = Paths.get(uploadDir, filename);

        Files.write(target, data);

        return target.toString();
    }
}