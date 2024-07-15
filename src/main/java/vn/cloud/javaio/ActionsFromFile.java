package vn.cloud.javaio;

import java.io.File;
import java.io.IOException;

public class ActionsFromFile {

    public static void main(String[] args) throws IOException {
        workingWithFile(new File("/Users/linhvu/Desktop/javaio/staging/hi1.txt"));
    }

    private static void workingWithFile(File file) throws IOException {
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent.mkdirs()) {
                System.out.println("Staging folder is created successfully!");
                for (int i = 0; i < 100000; i++) {
                    File child = new File(parent, "hi%d.txt".formatted(i));
                    if (!child.createNewFile()) { // Throw IOException
                        System.out.printf("Failed in creating hi%d.txt%n", i);
                    }
                }
                System.out.println("100000 files created successfully!");
            }
        }

        if (file.exists()) {
            System.out.println("Name: %s".formatted(file.getName()));
            System.out.println("Absolute Path: %s".formatted(file.getAbsolutePath()));
            System.out.println("Is File: %s".formatted(file.isFile()));
            System.out.println("Is Directory: %s".formatted(file.isDirectory()));

            if (file.isFile()) {
                System.out.println("Last Modified: %s".formatted(file.lastModified()));
                System.out.println("Length: %s".formatted(file.length()));
                System.out.println("Is Hidden: %s".formatted(file.isHidden()));

                File parent = file.getParentFile();
                if (parent.delete()) {
                    System.out.println("Staging folder is deleted successfully!");
                } else {
                    System.out.println("Staging folder is deleted failed!");
                }

                for (File child : parent.listFiles()) {
                    child.delete();
                }

                if (parent.delete()) {
                    System.out.println("Attempt to delete the staging folder again successfully!");
                }
            }
        }
    }
}
