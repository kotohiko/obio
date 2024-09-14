package org.jacob.im.obfo.starter;

import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/**
 * Although Windows allows user to view the number of files in a folder directly through its properties,
 * this class clearly lists all filenames, so it cannot be replaced.
 *
 * @author Kotohiko
 * @apiNote Please note that this class runs independently in this project.
 * @since 22:08 Sep 01, 2024
 */
public class DirectoryFilePrinter {

    public static void main(String[] args) {
        System.out.print(ResManager.loadResString("DirectoryFilePrinter_1"));
        // Input your path here
        try (BufferedReader in = IMCommonHelper.consoleReader()) {
            var directory = new File(in.readLine());
            if (directory.exists() && directory.isDirectory()) {
                var fileCount = listAllFiles(directory);
                System.out.println("Total files: " + fileCount);
            } else {
                System.out.println(ResManager.loadResString("DirectoryFilePrinter_0"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int listAllFiles(File directory) {
        var fileCount = 0;
        var files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println(file.getAbsolutePath());
                    ++fileCount;
                } else if (file.isDirectory()) {
                    fileCount += listAllFiles(file);
                }
            }
        }
        return fileCount;
    }
}