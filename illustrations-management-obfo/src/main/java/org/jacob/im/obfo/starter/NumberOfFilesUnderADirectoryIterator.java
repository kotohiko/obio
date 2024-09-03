package org.jacob.im.obfo.starter;

import org.jacob.im.common.IMCommonConsoleInputReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/**
 * @author Jacob Suen
 * @since 22:08 Sep 01, 2024
 */
public class NumberOfFilesUnderADirectoryIterator {

    public static void main(String[] args) {
        // Input your path here
        try (BufferedReader in = IMCommonConsoleInputReader.consoleReader()) {
            var directory = new File(in.readLine());
            if (directory.exists() && directory.isDirectory()) {
                int fileCount = listAllFiles(directory);
                System.out.println("Total files: " + fileCount);
            } else {
                System.out.println("The specified path does not exist or is not a directory.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int listAllFiles(File directory) {
        int fileCount = 0;
        var files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println(file.getAbsolutePath());
                    fileCount++;
                } else if (file.isDirectory()) {
                    fileCount += listAllFiles(file);
                }
            }
        }
        return fileCount;
    }
}