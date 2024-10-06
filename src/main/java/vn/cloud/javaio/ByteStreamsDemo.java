package vn.cloud.javaio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ByteStreamsDemo {

    public static void main(String[] args) throws IOException {
        batchAndBuffer();
    }

    public static void simple() {
        try (FileInputStream fis = new FileInputStream("video.mov");
             FileOutputStream fos = new FileOutputStream("video_cloned.mov");) {

            int b;
            while ((b = fis.read()) != -1) {
                fos.write(b);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception.");
        } finally {
            System.out.println("Done!");
        }
    }

    public static void batchOnly() {
        try (FileInputStream fis = new FileInputStream("video.mov");
             FileOutputStream fos = new FileOutputStream("video_cloned.mov");) {

            int batchSize = 1024;
            byte[] bytes = new byte[batchSize];
            int length;
            while ((length = fis.read(bytes, 0, batchSize)) != -1) {
                fos.write(bytes, 0, length);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception.");
        } finally {
            System.out.println("Done!");
        }
    }

    public static void bufferOnly() {
        try (FileInputStream fis = new FileInputStream("video.mov");
             BufferedInputStream bis = new BufferedInputStream(fis); // The default buffer size of 8192 bytes (8 KB) for BufferedInputStream
             FileOutputStream fos = new FileOutputStream("video_cloned.mov");
             BufferedOutputStream bos = new BufferedOutputStream(fos);) {

            int b;
            while ((b = bis.read()) != -1) {
                bos.write(b);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception.");
        } finally {
            System.out.println("Done!");
        }
    }

    public static void batchAndBuffer() {
        try (FileInputStream fis = new FileInputStream("video.mov");
             BufferedInputStream bis = new BufferedInputStream(fis);
             FileOutputStream fos = new FileOutputStream("video_cloned.mov");
             BufferedOutputStream bos = new BufferedOutputStream(fos);) {

            int batchSize = 1024;
            byte[] bytes = new byte[batchSize];
            int length;
            while ((length = bis.read(bytes, 0, batchSize)) != -1) {
                bos.write(bytes, 0, length);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception.");
        } finally {
            System.out.println("Done!");
        }
    }

    public static void transferTo() {
        try (FileInputStream fis = new FileInputStream("video.mov");
             BufferedInputStream bis = new BufferedInputStream(fis);
             FileOutputStream fos = new FileOutputStream("video_cloned.mov");
             BufferedOutputStream bos = new BufferedOutputStream(fos);) {

            bis.transferTo(bos); // <=====

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception.");
        } finally {
            System.out.println("Done!");
        }
    }

    public static void readAllBytes() {
        try (FileInputStream fis = new FileInputStream("video.mov");
             BufferedInputStream bis = new BufferedInputStream(fis);
             FileOutputStream fos = new FileOutputStream("video_cloned.mov");
             BufferedOutputStream bos = new BufferedOutputStream(fos);) {

            byte[] bytes = bis.readAllBytes(); // <=====
            bos.write(bytes);

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception.");
        } finally {
            System.out.println("Done!");
        }
    }

    public static void readAllBytesModern() {
        try {
            byte[] bytes = Files.readAllBytes(Path.of("video.mov"));
            Files.write(Path.of("video_cloned.mov"), bytes);
        } catch (IOException e) {
            System.out.println("IO Exception");
        } finally {
            System.out.println("Done!");
        }
    }

    public static void batchAndBufferModern() {
        try (InputStream is = Files.newInputStream(Path.of("video.mov")); // <=====
             BufferedInputStream bis = new BufferedInputStream(is);
             OutputStream os = Files.newOutputStream(Path.of("video_cloned.mov")); // <=====
             BufferedOutputStream bos = new BufferedOutputStream(os);) {

            int batchSize = 1024;
            byte[] bytes = new byte[batchSize];
            int length;
            while ((length = bis.read(bytes, 0, batchSize)) != -1) {
                bos.write(bytes, 0, length);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception.");
        } finally {
            System.out.println("Done!");
        }
    }

}
