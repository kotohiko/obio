package org.jacob.im.obfo.starter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Kotohiko
 * @since 13:00 10月 02, 2024
 */
public class DirectoryStructureCreator {

        public static void main(String[] args) {
            // 源文件路径
            Path sourceFilePath = Paths.get("C:\\Users\\tachi\\Desktop\\path-collection.txt"); // 替换为你的文件路径
            // 目标根目录
            Path targetRootDir = Paths.get("S:"); // 替换为你希望创建目录结构的目标根目录

            try {
                createDirectoryStructureFromFilePaths(sourceFilePath, targetRootDir);
                System.out.println("Directory structure created successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从文件中读取路径并在目标位置创建相应的目录结构。
         *
         * @param sourceFilePath 包含路径的源文件
         * @param targetRootDir  目标根目录
         * @throws IOException 如果在读取文件或创建目录过程中发生I/O错误
         */
        private static void createDirectoryStructureFromFilePaths(Path sourceFilePath, Path targetRootDir) throws IOException {
            try (BufferedReader reader = Files.newBufferedReader(sourceFilePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Path relativePath = Paths.get(line);
                        Path targetPath = targetRootDir.resolve(relativePath);
                        Files.createDirectories(targetPath);
                        System.out.println("Created: " + targetPath);
                    }
                }
            }
    }
}