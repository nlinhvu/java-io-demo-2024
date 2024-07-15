package vn.cloud.javaio;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class ActionsFromPath {

    public static void main(String[] args) throws IOException {
        workingWithPath(Path.of("/Users/linhvu/Desktop/javaio/staging/hi1.txt"));
    }

    private static void workingWithPath(Path path) throws IOException {
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            Files.createDirectories(parent); // Throw IOException
            System.out.println("Staging folder is created successfully!");
            for (int i = 0; i < 100000; i++) {
                Path child = parent.resolve("hi%d.txt".formatted(i));
                Files.createFile(child); // Throw IOException
            }
            System.out.println("100000 files created successfully!");
        }

        if (Files.exists(path)) {
            System.out.println("Name: %s".formatted(path.getFileName()));
            System.out.println("Absolute Path: %s".formatted(path.toAbsolutePath()));
            System.out.println("Is File: %s".formatted(Files.isRegularFile(path)));
            System.out.println("Is Directory: %s".formatted(Files.isDirectory(path)));

            if (Files.isRegularFile(path)) {
                System.out.println("Last Modified: %s".formatted(Files.getLastModifiedTime(path))); // Throw IOException
                System.out.println("Length: %s".formatted(Files.size(path))); // Throw IOException
                System.out.println("Is Hidden: %s".formatted(Files.isHidden(path))); // Throw IOException

                Path parent = path.getParent();
                try {
                    Files.delete(parent);
                    System.out.println("Staging folder is deleted successfully!");
                } catch (NoSuchFileException e) {
                    System.out.println("Staging folder is deleted failed because the folder doesn't exist!");
                } catch (DirectoryNotEmptyException e) {
                    System.out.println("Staging folder is deleted failed because the folder is not empty");
                } catch (IOException e) {
                    System.out.println("Staging folder is deleted failed because other reasons");
                }

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) { // Throw IOException // Try-with-resources
                    for (Path child : stream) {
                        Files.delete(child); // Throw IOException
                    }
                }

                try {
                    Files.delete(parent);
                    System.out.println("Attempt to delete the staging folder again successfully!");
                } catch (IOException e) {
                    System.out.println("Staging folder is deleted failed!");
                }
            }
        }
    }
}
