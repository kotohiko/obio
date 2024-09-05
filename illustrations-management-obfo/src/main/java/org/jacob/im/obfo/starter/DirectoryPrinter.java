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
 * @author Jacob Suen
 * @apiNote Please note that this class runs independently in this project.
 * @since 12:49 Aug 24, 2024
 */
public class DirectoryPrinter {

    private static Boolean EXCEPTION_FLAG = Boolean.TRUE;

    public static void main(String[] args) {
        List<String> subdirectories = new ArrayList<>();
        printSubdirectories(OBFOConstants.MY_GALLERY_PATH, subdirectories);
        // Write the results into a file
        writeToFile(subdirectories);
        if (EXCEPTION_FLAG != Boolean.FALSE) {
            System.out.println("The program has completed, "
                    + "the newest path data has been updated in the path-collection.txt file.");
        }
    }

    public static void printSubdirectories(String directory, List<String> subdirectories) {
        var rootDir = new File(directory);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println("Directory does not exist or is not a directory: " + directory);
            return;
        }
        // 递归遍历目录
        traverseDirectory(rootDir, subdirectories);
    }

    private static void traverseDirectory(File directory, List<String> subdirectories) {
        // Get all files and subdirectories in the directory
        var files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    subdirectories.add(file.getAbsolutePath());
                    // 递归遍历子目录
                    traverseDirectory(file, subdirectories);
                }
            }
        }
    }

    private static void writeToFile(List<String> subdirectories) {
        var outputPath = Paths.get(OBFOConstants.PATH_COLLECTION_TXT);
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath.toFile()))) {
            for (String subdirectory : subdirectories) {
                writer.println(subdirectory);
            }
        } catch (IOException e) {
            System.out.println("An IO exception occurred.");
            EXCEPTION_FLAG = Boolean.TRUE;
        }
    }
}