package org.jacob.im.obfo.starter;

import org.jacob.im.obfo.constants.OBFOConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Please note that this class runs independently in this project.
 *
 * @author Jacob Suen
 * @since 12:49 Aug 24, 2024
 */
public class DirectoryPrinter {

    /**
     * A boolean type global class variable to detect whether an exception has occurred.
     */
    private static Boolean EXCEPTION_FLAG = Boolean.TRUE;

    public static void main(String[] args) {
        List<String> subdirectories = new ArrayList<>();
        printSubdirectories(subdirectories);
        // Write the results into a file
        writeToFile(subdirectories);
        if (EXCEPTION_FLAG != Boolean.FALSE) {
            System.out.println("The program has completed, "
                    + "the newest path data has been updated in the path-collection.txt file.");
        } else {
            System.out.println("The program did not execute correctly, please check and try again.");
        }
    }

    /**
     * Print the subdirectories.
     *
     * @param subdirectories subdirectories list object
     */
    private static void printSubdirectories(List<String> subdirectories) {
        var rootDir = new File(OBFOConstants.MY_GALLERY_PATH);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println("Directory does not exist or is not a directory: " + OBFOConstants.MY_GALLERY_PATH);
            return;
        }
        traverseDirectory(rootDir, subdirectories);
    }

    /**
     * Traverse the directory recursively.
     *
     * @param directory      The specified directory
     * @param subdirectories subdirectories list object
     */
    private static void traverseDirectory(File directory, List<String> subdirectories) {
        // Get all files and subdirectories in the directory
        var files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    subdirectories.add(file.getAbsolutePath());
                    traverseDirectory(file, subdirectories);
                }
            }
        }
    }

    /**
     * Write the results into a file.
     *
     * @param subdirectories subdirectories collection
     */
    private static void writeToFile(List<String> subdirectories) {
        var outputPath = Paths.get(OBFOConstants.PATH_COLLECTION_TXT);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath.toFile()))) {
            for (String subdirectory : subdirectories) {
                writer.println(subdirectory);
            }
        } catch (IOException e) {
            // This exception description comes from the JavaDoc for the
            // java.io.FileWriter constructor FileWriter(java.io.File)
            System.out.println("An IO exception occurred. This file may exists but is a directory"
                    + " rather than a regular file, does not exist but cannot be created, "
                    + "or cannot be opened for any other reason.");
            EXCEPTION_FLAG = Boolean.TRUE;
        }
    }
}