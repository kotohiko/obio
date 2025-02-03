package org.jacob.im.obfo.service;

import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.common.utils.factory.CustomThreadFactory;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.enums.FilesMoveOperStatusEnums;
import org.jacob.im.obfo.enums.ThreadPoolSituationStatusEnums;
import org.jacob.im.obfo.logger.OBFOLogFilesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides functionality for reading and moving files between specified paths.
 * This class handles loading configuration files, moving files, and logging operations.
 *
 * @author Kotohiko
 * @since 07:26 Aug 02, 2024
 */
public class ReadAndMoveService {

    /**
     * The logger instance used for logging messages related to the {@link ReadAndMoveService} class.
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(ReadAndMoveService.class);

    /**
     * A fixed-size thread pool executor service used for processing file read and move operations.
     *
     * <p>This thread pool is created with a core and maximum pool size of 3, ensuring that up to 3
     * threads can be active at any given time. The threads in this pool are created using a custom
     * {@link CustomThreadFactory} with the name prefix "ReadAndMovePool" to provide meaningful names
     * for easier identification and debugging.
     *
     * <p>The thread pool is designed to handle tasks related to reading from and moving files, providing
     * a controlled and efficient way to manage these I/O-bound operations.
     */
    private final ExecutorService executorService
            = Executors.newFixedThreadPool(3, new CustomThreadFactory("ReadAndMovePool"));

    /**
     * Moves files from a default source path to a target path specified by a target path code.
     *
     * <p>This method first defines the source path using the provided default source path. It then
     * retrieves the target path string from the provided map of paths data using the target path code.
     * If the target path string is null, an error message is logged. Otherwise, it calls the
     * {@link #checkBeforeMove(Path, String)} method to perform any necessary checks before moving
     * the files.
     *
     * @param defaultSourcePath The default source path from which files will be moved.
     * @param pathsData         A map containing path data where the key is the target path code and the value
     *                          is the corresponding target path string.
     * @param targetPathCode    The target path code used to retrieve the target path string from the
     *                          paths data map.
     */
    public void defineSourcePathAndTargetPath(String defaultSourcePath,
                                              Map<String, String> pathsData, String targetPathCode) {
        var sourcePath = Paths.get(defaultSourcePath);
        try (var stream = Files.list(sourcePath)) {
            if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath) || stream.findAny().isEmpty()) {
                System.out.println("No files in source directory " + sourcePath + ", returning directly");
                return;
            }
        } catch (IOException e) {
            logger.error("Failed to check source directory: {}", e.getMessage());
            return;
        }

        var targetPathStr = pathsData.get(targetPathCode);
        if (targetPathStr == null) {
            System.out.println(ResManager.loadResString("ReadAndMoveService_0"));
            System.out.println(IMCommonConstants.EXCEPTIONAL_SEPARATOR_LINE);
        } else {
            checkBeforeMove(sourcePath, targetPathStr);
            System.out.println(IMCommonConstants.SUCCESS_SEPARATOR_LINE);
        }
    }

    /**
     * Checks the source directory for files and moves them to the target directory if found.
     *
     * <p>This method iterates through all the files in the specified source directory,
     * and moves each file to the specified target directory. It ignores any subdirectories
     * and processes only regular files. If no files are found in the source directory,
     * it logs an error message indicating that no files were found. If an IOException
     * occurs during file traversal, it logs an error message for the exception.</p>
     *
     * @param sourcePath    the path of the source directory to check for files.
     * @param targetPathStr the string representation of the target directory path
     *                      where the files will be moved.
     */
    private void checkBeforeMove(Path sourcePath, String targetPathStr) {
        var targetPath = Paths.get(targetPathStr);
        AtomicBoolean foundFiles = new AtomicBoolean(false);
        List<Path> filePaths = new ArrayList<>();

        try {
            processDirectory(sourcePath, targetPath, filePaths);
            submitToExecutorPool(filePaths, targetPath, foundFiles, sourcePath);
        } catch (IOException | InterruptedException e) {
            logger.error(ResManager.loadResString("ReadAndMoveService_2"), e);
        }
    }

    /**
     * Moves the contents of a source directory to a target directory, including both files and subdirectories.
     * <p>
     * If the source path contains regular files, each file is moved to the corresponding location
     * in the target directory.
     * If the source path contains subdirectories, the entire subdirectory is moved to the target location
     * without further recursive processing of its contents.
     * <p>
     * If the source path itself is a regular file, the file is moved directly to the target directory.
     *
     * @param sourcePath the source directory or file to process
     * @param targetPath the target directory where files and subdirectories should be moved
     * @param filePaths  the list to store the paths of regular files that are moved
     * @throws IOException if an I/O error occurs while accessing the file system
     */
    private void processDirectory(Path sourcePath, Path targetPath, List<Path> filePaths) throws IOException {
        if (Files.isDirectory(sourcePath)) {
            Files.createDirectories(targetPath);

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
                for (Path filePath : directoryStream) {
                    Path newTargetPath = targetPath.resolve(filePath.getFileName());

                    if (Files.isDirectory(filePath)) {
                        Files.move(filePath, newTargetPath, StandardCopyOption.REPLACE_EXISTING);
                    } else if (Files.isRegularFile(filePath)) {
                        Files.move(filePath, newTargetPath, StandardCopyOption.REPLACE_EXISTING);
                        filePaths.add(newTargetPath);
                    }

                }
            }
        } else if (Files.isRegularFile(sourcePath)) {
            Files.move(sourcePath, targetPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            filePaths.add(targetPath.resolve(sourcePath.getFileName()));
        }
    }


    /**
     * Submits tasks to the executor pool for moving files to the target path.
     * <p>
     * This method takes a list of file paths and submits each file moving task to an executor service.
     * It uses a {@link CountDownLatch} to ensure that all tasks complete before proceeding.
     * If no files are successfully moved, it logs an error message indicating that no files were found.
     *
     * @param filePaths  the list of file paths to be moved
     * @param targetPath the target path where the files will be moved
     * @param foundFiles an {@link AtomicBoolean} flag indicating if any files were successfully moved
     * @param sourcePath the source path from which the files are being moved (for logging purposes)
     * @throws InterruptedException if the current thread is interrupted while waiting for tasks to complete
     */
    private void submitToExecutorPool(List<Path> filePaths, Path targetPath,
                                      AtomicBoolean foundFiles, Path sourcePath) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(filePaths.size());
        printThreadPoolInfo(ThreadPoolSituationStatusEnums.BEFORE_SUBMITTED);

        for (Path filePath : filePaths) {
            executorService.submit(() -> {

                try {
                    FilesMoveOperStatusEnums status = moveTheFiles(targetPath, filePath);
                    if (status != FilesMoveOperStatusEnums.NO_FILES) {
                        foundFiles.set(true);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all submitted tasks to complete before proceeding.
        latch.await();

        printThreadPoolInfo(ThreadPoolSituationStatusEnums.TASK_FINISHED);

        // If no files were successfully moved, log an error with the source path.
        if (!foundFiles.get()) {
            logger.error(ResManager.loadResString("ReadAndMoveService_3", sourcePath.toString()));
        }
    }

    /**
     * Prints the current status and configuration information of the thread pool.
     *
     * <p>This method logs the active thread count, current pool size, core pool size,
     * and maximum pool size of the {@link ThreadPoolExecutor} instance. The information
     * is logged with a specific status enum and resource strings for better readability
     * and context.
     *
     * @param enums The status enum to be included in the log messages, providing
     *              context about the situation when the information is being printed.
     */
    private void printThreadPoolInfo(ThreadPoolSituationStatusEnums enums) {
        if (executorService instanceof ThreadPoolExecutor threadPoolExecutor) {
            var activeCount = threadPoolExecutor.getActiveCount();
            var poolSize = threadPoolExecutor.getPoolSize();
            var corePoolSize = threadPoolExecutor.getCorePoolSize();
            var maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();

            logger.info("[{}] {}{}", enums, ResManager.loadResString("ReadAndMoveService_6"), activeCount);
            logger.info("[{}] {}{}", enums, ResManager.loadResString("ReadAndMoveService_7"), poolSize);
            logger.info("[{}] {}{}", enums, ResManager.loadResString("ReadAndMoveService_8"), corePoolSize);
            logger.info("[{}] {}{}", enums, ResManager.loadResString("ReadAndMoveService_9"), maximumPoolSize);
        }
    }

    /**
     * Moves a file from its current location to a specified target directory.
     * <p>
     * This method constructs the target file path by resolving the given file name with the target directory path.
     * It then attempts to move the file to the new location, replacing any existing file with the same name
     * in the target directory.
     * <p>
     * If the move operation is successful, the method logs an info message and increments the count of
     * files moved (assuming {@link ReadAndMoveService#countTheNumberOfFiles()} updates a count).
     * In case of an error, such as if the target path is invalid or the file cannot be moved for any reason,
     * it logs an error message and returns an error status.
     *
     * @param targetPath The target directory where the file should be moved to.
     * @param filePath   The path of the file to be moved.
     * @return A {@link FilesMoveOperStatusEnums} enum indicating the status of the operation.
     * - {@link FilesMoveOperStatusEnums#HAS_FILES} if the file was successfully moved.
     * - {@link FilesMoveOperStatusEnums#TARGET_PATH_INVALID} if the target path is invalid or the move failed
     * for any other reason.
     * @see Files#move(Path, Path, CopyOption...)
     * @see StandardCopyOption#REPLACE_EXISTING
     */
    private FilesMoveOperStatusEnums moveTheFiles(Path targetPath, Path filePath) {
        // Construct the target path
        var targetFilePath = targetPath.resolve(filePath.getFileName());

        try {
            Files.move(filePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info(ResManager.loadResString("ReadAndMoveService_4", targetFilePath.toString()));
            countTheNumberOfFiles();

            // Signal that the files have been found
            return FilesMoveOperStatusEnums.HAS_FILES;
        } catch (IOException e) {
            logger.error(ResManager.loadResString("ReadAndMoveService_5"));
            return FilesMoveOperStatusEnums.TARGET_PATH_INVALID;
        }
    }

    /**
     * Counts the number of files in the specified directory and writes the count to a log file.
     * This method traverses all the files under the directory specified by
     * {@code OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH}, counts the number of files,
     * and then calls {@code OBFOLogFilesWriter.filesMoveLogWriter} to write the count to a log file.
     */
    private void countTheNumberOfFiles() {
        var folder = new File(OBFOConstants.PATH_OF_UNCLASSIFIED_REMAINING_IMAGES);
        int fileCount = 0;

        // Traverse all the files under the specified directory.
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                ++fileCount;
            }
        }
        OBFOLogFilesWriter.filesMoveLogWriter(fileCount);
    }

}