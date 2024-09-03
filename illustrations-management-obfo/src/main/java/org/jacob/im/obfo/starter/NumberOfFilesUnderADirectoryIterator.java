package org.jacob.im.obfo.starter;

import java.io.File;

/**
 * @author Jacob Suen
 * @since 22:08 Sep 01, 2024
 */
public class NumberOfFilesUnderADirectoryIterator {

    public static void main(String[] args) {
        // 替换为你想要列出文件的路径
        var path = "S:\\Gallery\\【二次元】Anime World／二次元世界／アニメの世界\\#Genshin Impact／#原神／#原神（げんしん）／#원신";
        var directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            int fileCount = listAllFiles(directory);
            System.out.println("文件总数: " + fileCount);
        } else {
            System.out.println("指定的路径不存在或不是一个目录");
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