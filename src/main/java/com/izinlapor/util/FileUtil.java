package com.izinlapor.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUtil {

    private static final String STORAGE_DIR = "user_data";

    public static String saveImage(File sourceFile, String subDirectory) throws IOException {
        // Create base storage directory if not exists
        String projectDir = System.getProperty("user.dir");
        Path storagePath = Paths.get(projectDir, STORAGE_DIR, subDirectory);
        
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        // Generate unique filename to prevent overwrites
        String originalFilename = sourceFile.getName();
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        
        String newFilename = UUID.randomUUID().toString() + extension;

        // Copy file
        Path destinationPath = storagePath.resolve(newFilename);
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return STORAGE_DIR + "/" + subDirectory + "/" + newFilename;
    }

    public static String getFullPath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return null;
        
        File file = new File(relativePath);
        if (file.isAbsolute() && file.exists()) {
            return relativePath;
        }

        return Paths.get(System.getProperty("user.dir"), relativePath).toString();
    }
}
