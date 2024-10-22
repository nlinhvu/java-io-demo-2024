# JAVA I/O API - Locating The Resource

> This repo is used in this Youtube video: https://youtu.be/z2DTrXjyG3E

> **The I/O** in `"Java I/O"` stands for **Input / Output**. The `Java I/O API` gives all the tools your application needs to access information from the outside. 
> For your application, "outside" means two elements: your **disks**, or more generally, your **file systems** (file systems do not always model disks, for instance, they may reside in memory), and your **network**.

> **There are two main concepts in Java I/O:**
> 1. Locating the resource (Accessing a File)
> 2. Opening a stream to this resource (Manipulate and Transform the Data)

Refer to [**dev.java - Understanding the Main Java I/O Concepts**](https://dev.java/learn/java-io/intro/)

> **Noted:** We focus on `1. Locating the resource (Accessing a File)` this time

## 1. History

- ### Mid-90s - Java SE 1.0 - Java I/O API was created

`File` class was introduced, we can create an instance of `File` class using its constructors:
```java
new File(String pathname); //(1)
new File(String parent, String child); //(2)
new File(File parent, String child); //(3)
```
_For example:_
```java
File file1 = new File("/Users/linhvu/Desktop/javaio/staging/hi1.txt"); //(1)

File file2 = new File("/Users/linhvu/Desktop/javaio", "staging/hi1.txt"); //(2)

File folder = new File("/Users/linhvu/Desktop/javaio");
File file3 = new File(folder, "staging/hi1.txt"); //(3)
```

- ### 2002 - Java SE 1.4 - Java NIO was released

Leverage some improvements, like `URI` class was introduced and being used in creating a `File` object:
```java
new File(URI uri);
```
**For example:**
```java
File file4 = new File(URI.create("file:///Users/linhvu/Desktop/javaio/staging/hi1.txt"));
```

- ### 2011 - Java SE 7 - Java NIO2 was released

The `Path` interface was introduced, we can easily create a `Path` object by using: 

1. The `Paths` (note the plural) helper factory class:
```java
Paths.get(String first, String... more); //(1)
Paths.get(URI uri); //(2)
```
_For example:_
```java
Path path1 = Paths.get("/Users/linhvu/Desktop/javaio/staging/hi1.txt"); //(1)
Path path2 = Paths.get("/Users", "linhvu/Desktop", "javaio/staging/hi1.txt"); //(1)

Path path3 = Paths.get(URI.create("file:///Users/linhvu/Desktop/javaio/staging/hi1.txt")); //(2)
```

2. `FileSystems.getDefault()` which is run under `Paths.get`:
```java
FileSystems.getDefault().getPath(String first, String... more);
```
_For example:_
```java
Path path4 = FileSystems.getDefault().getPath("/Users/linhvu/Desktop/javaio/staging/hi1.txt");
Path path5 = FileSystems.getDefault().getPath("/Users", "linhvu/Desktop", "javaio/staging/hi1.txt");
```

3. `Path.of()` Factory methods from `Path` interface in `JavaSE 9`:
```java
Path.of(String first, String... more); //(1)
Path.of(URI uri); //(2)
```
**For example:**
```java
Path path6 = Path.of("/Users/linhvu/Desktop/javaio/staging/hi1.txt"); //(1)
Path path7 = Path.of("/Users", "linhvu/Desktop", "javaio/staging/hi1.txt"); //(1)

Path path8 = Path.of(URI.create("file:///Users/linhvu/Desktop/javaio/staging/hi1.txt")); //(2)
```

## 2. Path (Java NIO2) vs File (Java I/O)

> **Noted:** **Always prefer using `Path` if you can**

Some drawbacks of `File` are listed here, for full list, please take a look at [**dev.java - Understanding the Main Java I/O Concepts**](https://dev.java/learn/java-io/intro/)
> Many methods of the File class do not throw exceptions when they fail, making it impossible to obtain a useful error message. 
> For example, if you call file.delete() and the deletion does not work, your program gets a false value. But you cannot know if it is because the file doesn't exist, the user doesn't have permissions, or there was some other problem.

> Many of the File methods do not scale. Requesting a large directory listing over a server can result in a hang. 
> Large directories can also cause memory resource problems, resulting in a denial of service.

These issues are fixed by the `Path` interface.

If we're still working with legacy libraries that required `File` as its methods' parameters,
we can easily convert `Path` to `File` to pass to these methods, and then convert back `File` to `Path` to use in our application code.

**Technically, both `File`, `Path`, `URI` are wrappers of a `String`, so we can easily convert between them:**
* File -> Path: `file1.toPath();`
* File -> URI: `file1.toURI();`
* Path -> File: `path1.toFile();`
* Path -> URI: `path1.toUri();`
* URI -> File: `new File(uri);`
* URI -> Path: `Path.of(uri);`

## 3. Refactor File to Path:

Refactor from using `File`:
```java
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
```

to `Path` (we're gonna follow the mapping table in [**dev.java - File to Path**](https://dev.java/learn/java-io/file-system/file-path/#file-to-path)):
```java
public class ActionssFromPath {

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
```

> **Confirmed:** Many methods of the `File` class do not throw exceptions when they fail, making it impossible to obtain a useful error message.
> 
> * **In File:** only `File.createNewFile()` throws IOException
> 
> * **In Path:** `Files.createDirectories(folder)`, `Files.createFile(file)`, `Files.getLastModifiedTime(path)`,... throw IOException

> **Confirmed:** If you call `file.delete()` and the deletion does not work, your program gets a false value. But you cannot know if it is because the file doesn't exist, the user doesn't have permissions, or there was some other problem.
> 
> * **In File:**
> ```java
> if (parent.delete()) {
>     System.out.println("Staging folder is deleted successfully!");
> } else {
>     System.out.println("Staging folder is deleted failed!");
> }
> ```
> 
> * **In Path:**
> ```java
> try {
>     Files.delete(parent);
>     System.out.println("Staging folder is deleted successfully!");
> } catch (NoSuchFileException e) {
>     System.out.println("Staging folder is deleted failed because the folder doesn't exist!");
> } catch (DirectoryNotEmptyException e) {
>     System.out.println("Staging folder is deleted failed because the folder is not empty");
> } catch (IOException e) {
>     System.out.println("Staging folder is deleted failed because other reasons");
> }
> ```

> **Confirmed:** Many of the File methods do not scale. Requesting a large directory listing over a server can result in a hang. Large directories can also cause memory resource problems, resulting in a denial of service.
> 
> We're currently using full memory from my Mac.
> 
> Let's limit the memory by setting the maximum heap size for our JVM to 32MB: `-Xmx32m` -> Our application will adjust the way it uses memory.
> 
> Let's shrink it a little bit more: `-Xmx16m`:
> * `parent.listFiles()` in `I/O` starts cracking `java.lang.OutOfMemoryError: Java heap space: failed reallocation of scalar replaced objects`, since it loaded the entire directory at once
> * `Files.newDirectoryStream(parent)` in `NIO2` still works perfectly fine since `Stream` is lazy-load, it loaded each element when needed.

> **Noted:** The returned `DirectoryStream` from `Files.newDirectoryStream(parent)` is a stream, it opens a connection to the filesystem that **must be closed**. 
> If you are not using a `try-with-resources` statement, do not forget to close the stream in the finally block. `Files.newDirectoryStream()` also states it in its documentation. 

## References
- `1.` **Understanding the Main Java I/O Concepts** in [**dev.java**](https://dev.java/learn/java-io/intro/).
- `2.` **Accessing Resources using Paths** in [**dev.java**](https://dev.java/learn/java-io/file-system/file-path/).
- `3.` **Working with Paths** in [**dev.java**](https://dev.java/learn/java-io/file-system/path/).

# JAVA I/O API - I/O Streams: READ and WRITE data

> This repo is used in this Youtube video: https://youtu.be/iXXp1XGlWac

> Basically, it's about **reading data** from a resource (**input**) and **writing data** to a resource (**output**)

> **Noted:** since read/write from/to **files** are the most common use-cases and simple ones, so we'll review **I/O Streams with Files**

Read more [**dev.java - Understanding I/O Streams**](https://dev.java/learn/java-io/intro/)

> There are **2 categories of I/O Streams** to support **2 kinds of content for a resource**:
> 
> * **Byte Streams**: for `byte content` like an `image` or a `video` that process _bytes_ as a unit of data and have class names that end in `InputStream` (reading from files) or `OutputStream` (writing to files)
> * **Character Streams**: for `character content` like a `text file`, a `XML` or `JSON document` that process _characters_ as a unit of data and have class names that end in `Reader` (reading from files) or `Writer` (writing to files)

> Besides **Byte Streams** and **Character Streams**, we also need to distinguish a **Low-Level Streams** and a **High-Level Streams**:
> 
> * **Low-Level Streams**: operate directly on the data source (a file, an array of bytes) and process data primarily as _bytes_, **for example:** `FileInputStream` reads file data one byte at a time
> * **High-Level Streams**: is built on top of another I/O streams using [**Decorator Pattern - dev.java**](https://dev.java/learn/java-io/reading-writing/decorating/), **for example:**
> ```java
> try (FileInputStream fis = new FileInputStream("file.txt"); // low-level
>      BufferedInputStream bis = new BufferedInputStream(fis); // high-level takes low-level as input
>      ObjectInputStream ois = new ObjectInputStream(bis);) { // high-level takes high-level as input
>
>     System.out.println(ois.readObject());
> }
> ```

> **Noted:** We focus on `Byte Streams` this time

## 1. Byte Streams

> **Purpose:** We're going to clone a video "video.mov" to "video_cloned.mov"

> **Direction:** We'll go from the way that _not optimized_ with `low-level streams` and **improve** it by using `batching` and `high-level streams`.
> Then, we'll move on from IO package to NIO package to use modern Java

### 1.1. Low-Level Streams
Since, we're going to read bytes **from** a video **file**, let's start by using a low-level stream `FileInputStream`
```java
public static void simple() {
    try (FileInputStream fis = new FileInputStream("video.mov");
         FileOutputStream fos = new FileOutputStream("video_cloned.mov");) {

        int b;
        while ((b = fis.read()) != -1) {
            fos.write(b);
            // fos.flush(); // you might see flush after write, but it's useless, since it's FileOutputStream
        }

    } catch (FileNotFoundException e) {
        System.out.println("File not found.");
    } catch (IOException e) {
        System.out.println("IO Exception.");
    } finally {
        System.out.println("Done!");
    }

}
```
**Let's run it** and there are somethings we can review in this code:

1. We used the `try-with-resources statement` (`try (resources) {}`), we'll discuss it in details later in `Exception Handling` videos.
At this time, we can simply think that it will help us automatically closing our `resources` (here are `fis` and `fos`) when we escape the try block.
`FileInputStream` and `FileOutputStream` both implement `Closable` that's why we can use `try-with-resources statement` to automatically call their `close()`.
By that, we don't have to worry about manually closing resources when they're no longer needed and reduce the possibility of memory leak. 
At the moment, you can take a look at [**dev.java - Releasing Resources and Catching Exceptions**](https://dev.java/learn/java-io/reading-writing/common-operations/) to have an overview 

2. We're using `fis.read()` that is going to read **each byte at the time**, but why it returns an `int` not a `byte`. Let's look at the doc of the method in the `InputStream` class and `FileInputStream` class respectively
```java
    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an {@code int} in the range {@code 0} to
     * {@code 255}. If no byte is available because the end of the stream
     * has been reached, the value {@code -1} is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * @return     the next byte of data, or {@code -1} if the end of the
     *             stream is reached.
     * @throws     IOException  if an I/O error occurs.
     */
    public abstract int read() throws IOException;
```
```java
    /**
     * Reads a byte of data from this input stream. This method blocks
     * if no input is yet available.
     *
     * @return     the next byte of data, or {@code -1} if the end of the
     *             file is reached.
     * @throws     IOException {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
```
We noticed that the `The value byte is returned as an int in the range 0 to 255` that fits `256 values` = `1 byte` = `8 bits` = `2^8 values`.
So technically, even it's an `int`, but only `the least-significant byte is used`.

Let's try to print out the binary representation, we'll see at most 8 bits are shown:
```java
while ((b = fis.read()) != -1) {
    System.out.printf("%32s%n", Integer.toBinaryString(b));
    fos.write(b);
}
```
And we know that `fos.write(int)` only **writes the last 1 byte**, this should still work (but let's move to lightweight file like `image.png` to see the result quickly)
```java
fos.write(b & 0xFF); // write only the last byte of b
```
```java
try (FileInputStream fis = new FileInputStream("image.png");
     FileOutputStream fos = new FileOutputStream("image_cloned.png");) {
```

Check if 2 files are identical (`Files.mismatch` return -1):
```java
System.out.println(Files.mismatch(Path.of("image.png"), Path.of("image_cloned.png")));
```

> **Considering:** I think the reason of returning an `int` instead of `byte` is: we used `-1` to indicate the end of IO stream, so for 256 values, we had to use a different **range** that not include -1,
> sadly, `byte` **range** is `from -128 to 127`. So `int` is chosen, and the range for 256 value is `from 0 to 255`.

3. `fos.flush()` is redundant, if we take a look at the implementation of `flush()` in `FileOutputStream`, it does nothing. It's reasonable because there's no
internal buffer, minimizing the number of actual I/O operations by writing data in larger blocks
4. **Constructors** of `FileInputStream` and `FileOutputStream` throw `FileNotFoundException` and `fis.read`, `fos.write` throw `IOException`
which are `checked exceptions` and needed to be explicitly processed

### 1.2. Low-Level Streams + Batching

Reading and Writing files `byte by byte` isn't an efficient way, there are overloaded methods for reading and writing `multiple bytes at a time`
```java
public static void batchOnly() {
    try (FileInputStream fis = new FileInputStream("video.mov");
         FileOutputStream fos = new FileOutputStream("video_cloned.mov");) {

        int batchSize = 1024;
        byte[] bytes = new byte[batchSize];
        int length;
        while ((length = fis.read(bytes, 0, batchSize)) != -1) {
            fos.write(bytes, 0, length);
            // fos.flush(); // useless
        }

    } catch (FileNotFoundException e) {
        System.out.println("File not found.");
    } catch (IOException e) {
        System.out.println("IO Exception.");
    } finally {
        System.out.println("Done!");
    }
}
```
**Run it, it's much faster**, here, instead of `fis.read()`, we used `fis.read(bytes, 0, batchSize)` that:
1. Read data from file to `bytes` variable
2. Return the length of bytes written

To understand more, let's debug with `alphabet.txt` that contains 26 Uppercase English alphabet
```java
public static void batchOnly() {
        try (FileInputStream fis = new FileInputStream("alphabet.txt");
             FileOutputStream fos = new FileOutputStream("alphabet_cloned.txt");) {

            int batchSize = 10; // <-- batchSize
            byte[] bytes = new byte[batchSize];
            int length;
            while ((length = fis.read(bytes, 0, batchSize)) != -1) {
                System.out.println(Arrays.toString(bytes)); // <-- print bytes
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
```
`ABCDEFGHIJKLMNOPQRSTUVWXYZ` are all 1-byte characters and will be encoded to 
```java
[A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P   Q   R   S   T   U   V   W   X   Y   Z]
[65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90]
```

```java
// After 1st read: the first 10 bytes 65->74 were written to
bytes = [65, 66, 67, 68, 69, 70, 71, 72, 73, 74]
and the method returns length = 10 (== batchSize)
```
```java
// After 2nd read: the next 10 bytes 75->84 were written to
bytes = [75, 76, 77, 78, 79, 80, 81, 82, 83, 84]
and the method returns length = 10 (== batchSize)
```

```java
// After 3rd read: only 6 bytes left, so 6 bytes 85->90 were written to bytes, and the last 4 bytes remained the same
bytes = [85, 86, 87, 88, 89, 90,     81, 82, 83, 84]
and the method returns length = 6 (!= batchSize)
```

That's also the reason why when writing data from bytes variable to file, we based on `length` instead of `batchSize`.
If we change `fos.write(bytes, 0, length)` to `fos.write(bytes, 0, batchSize)`, after 3rd read, the whole 10 bytes will be written
to file and it will result in ABCDEFGHIJKLMNOPQRSTUVWXYZ**QRST**

### 1.3. Low-Level Streams + High-Level Streams

Here, we introduce `BufferedInputStream` as a `High-Level Stream` that wraps `FileInputStream` and `BufferedOutputStream` as also a `High-Level Stream` that wraps `FileOutputStream`.

Basically, `BufferedInputStream` and `BufferedOutputStream` add an internal buffer (8192 bytes by default) to _reduce the number of I/O operations (disk accesses)_ for **reading** and **writing** respectively

```java
public static void bufferOnly() {
    try (FileInputStream fis = new FileInputStream("video.mov");
         BufferedInputStream bis = new BufferedInputStream(fis); // The default buffer size of 8192 bytes (8 KB) for BufferedInputStream
         FileOutputStream fos = new FileOutputStream("video_cloned.mov");
         BufferedOutputStream bos = new BufferedOutputStream(fos);) {

        int b;
        while ((b = bis.read()) != -1) {
            bos.write(b);
            bos.flush(); // this time, flush has some impact, but using flush after every byte written disable the advantage of buffer
        }

    } catch (FileNotFoundException e) {
        System.out.println("File not found.");
    } catch (IOException e) {
        System.out.println("IO Exception.");
    } finally {
        System.out.println("Done!");
    }
}
```

**For reading side:** we still read one byte at the time, but this time, we use `BufferedInputStream.read()` instead of `FileInputStream.read()`.
The benefit here is _instead of read only one byte for 1 I/O read operation_, **it now reads a large chunk (8 KB by default) in one go and stores it in its internal buffer.**
For every read() 1 byte after, it'll get from the buffer.

**For writing side:** as same as reading, we use `BufferedOutputStream.write(int)` instead of `FileOutputStream.write(int)`, every `write(int)` is actually **a write to the buffer
instead of directly to the file**. When the buffer is full or when `BufferedOutputStream.flush()` is called, the whole buffer data is written to the file.

**For our example:** we called `flush()` on `BufferedOutputStream` after every one byte written into the buffer, _it ended up with one I/O write operation for writing one byte_, in other words, **this disabled the advantage of the buffer**.
Let's delete it, then every 8 KB will be written to the file in 1 I/O write operation.

> **Noted:** When we escape **the try block** of `try-with-resources statement`, `BufferedOutputStream.close()` is automatically called and triggers `BufferedOutputStream.flush()`.
> So _we don't have to worry about closing resources and flushing remaining bytes in the internal buffer to file._

### 1.4. Low-Level Streams + Batching + High-Level Streams

We demonstrated that either batching or High-Level Streams being applied individually can improve the performance as well as reduce the overhead of accessing the file system.
Why don't we apply both of them together?

```java
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
            // it's totally ok to put bos.flush() here
        }

    } catch (FileNotFoundException e) {
        System.out.println("File not found.");
    } catch (IOException e) {
        System.out.println("IO Exception.");
    } finally {
        System.out.println("Done!");
    }
}
```

Here, `batchSize = 1 KB`, `bufferSize = 8 KB`. **For one I/0 read operation,** 8 KB is stored in the buffer of `BufferedInputStream` and can be used for 8 times `bis.read(bytes, 0, batchSize)` (1 KB/a time).
Similar to `BufferedOutputStream`, it needs 8 times `bos.write(bytes, 0, length)` to full up the buffer, once the buffer is full, **one I/O write operatio**n needed to write data in the buffer to the file.

### 1.5. TransferTo

If we just simply copy data without processing/changing data in between reading and writing, let's use `InputStream.transferTo(OutputStream)`. It is optimized for this task

```java
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
```

> **Noted:** all of 5 ways we reviewed so far _process data bytes by bytes_, **it's NOT going to consume lots of memory.**
> So if we limit the memory to 10 MB `-Xmx10m`, we're still able to read/process/write big files (like video.mov in our example is around 690 MB)

### 1.6. Reading small files

```java
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
```

By using `readAllBytes()`, we _at least_ stored 690 MB in **our heap space**, so if we limit the memory to 10 MB `-Xmx10m`, it'll lead to `OutOfMemoryError`.
That's why **this method is for processing small files.**

### 1.7. In modern Java (NIO2)

If you notice, so far, we've been only playing with `java.io` package, but `java.nio` also supports convenient methods for our I/O tasks.
Here is an example for **reading small files** with `java.nio`

```java
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
```

`Files.readAllBytes` and `Files.write` managed opening/closing resources for us, so we don't have to declare `try-with-resources statement`, 
we only need to take care the `IOException` **thrown by these methods.**

`java.nio` also supports create `Low-Level Byte Streams` (it also supports `High-Level Character Streams`, but we will talk about `Character Streams` later)
```java
InputStream is = Files.newInputStream(Path.of("video.mov"));
OutputStream os = Files.newOutputStream(Path.of("video_cloned.mov"));
```

We can apply them to our example in **1.4.** resulting in
```java
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
```

## References
- `1.` **Understanding the Main Java I/O Concepts** in [**dev.java**](https://dev.java/learn/java-io/intro/).
- `2.` **File Operations Basics** in [**dev.java**](https://dev.java/learn/java-io/reading-writing/).

## 2. Character Streams

### 2.1. Charset

> This repo is used in this Youtube video: https://youtu.be/Jdk39lBJ_hY

Follow the [**Java Documentation**](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/charset/Charset.html)**:**
> A **charset** is defined as the combination of
> **one or more coded character sets** and **a character-encoding scheme.**

> A **coded character set** is a mapping between _a set of abstract characters_ and _a set of integers_. 
> `US-ASCII`, `ISO 8859-1`, `JIS X 0201`, and `Unicode` are examples of coded character sets.

👉 In `Unicode` version 16, there are **154,998** _characters(a set of characters)_ ([FAQ from unicode.org](https://www.unicode.org/faq/basic_q.html))
are assigned to _code points(a set of integers)_. For example, [**English characters, Japanese characters and Emoji**](https://en.wikipedia.org/wiki/List_of_Unicode_characters) in `Unicode`:
```text
Code Point    <->   Character   Description
U+0041         |    A           Latin Capital letter A (https://symbl.cc/en/0041/)                
U+0042         |    B           Latin Capital letter B (https://symbl.cc/en/0042/)
...            |
U+005A         |    Z           Latin Capital letter Z (https://symbl.cc/en/005A/)
...            |
U+5301         |    匁          Ideograph Japanese unit of weight (1/1000 of a kan) CJK 匁 (https://symbl.cc/en/5301/)
...            |
U+1F525        |    🔥          Fire Emoji (https://symbl.cc/en/1F525-fire-emoji/)
```

> **A character-encoding scheme** is a mapping between one or more **coded character sets** and a set of octet (eight-bit **byte) sequences**. 
> `UTF-8`, `UTF-16`, `ISO 2022`, and `EUC` are examples of character-encoding schemes. Let's take `UTF-8` as example:
```text
Character   Code Point    <->   Bytes(Hex)
A           U+0041         |    41                  (https://symbl.cc/en/0041/)
B           U+0042         |    42                  (https://symbl.cc/en/0042/)
...                        |
Z           U+005A         |    5A                  (https://symbl.cc/en/005A/)
...                        |
匁          U+5301         |    E5 8C 81            (https://symbl.cc/en/5301/)
...                        |
🔥          U+1F525        |    F0 9F 94 A5         (https://symbl.cc/en/1F525-fire-emoji/)
```
> **Noted:** `UTF-8` is **variable-length** encoding scheme, it means that 1 code point could be encoded to 1,2,3 or 4 bytes depend on the code point

> **Encoding schemes** are often associated with a particular **coded character set**; 
> `UTF-8`(encoding scheme), for example, is used only to encode `Unicode`(character set).
> Look at the table above, we used `UTF-8` to encode `Unicode` **code points** to **byte sequences**.

👉 If we use `UTF-8` as an encoding scheme, the resource MUST be `Unicode` compatible.

> **Noted:** Some schemes, however, are associated with multiple coded character sets; `EUC`, for example, can be used to encode characters in a variety of Asian coded character sets.

> When a **coded character set** is used **_exclusively_** with a single **character-encoding scheme** 
> then the corresponding **charset** is usually named for the **coded character set**; otherwise a **charset** is usually named for the **encoding scheme**.
> 
> 👉 `UTF-8` **character-encoding schemes** is used to encode `Unicode` **coded character set** only
> 👉 The **charset** name for `UTF-8` **character-encoding schemes** and `Unicode` **coded character set** is the **character-encoding schemes**: `UTF-8`.
> 
> 👉 **In Java,** this **charset** is represented as  `StandardCharsets.UTF_8`. So `StandardCharsets.UTF_8` is a combination of `Unicode` **coded character set** and `UTF-8` **character-encoding schemes**.

Similar to `StandardCharsets.UTF_8`, In **Java**, we can look at some others well-known **charset** at  `java.nio.charset.StandardCharsets`
```java
public final class StandardCharsets {

    //...

    /**
     * Seven-bit ASCII, also known as ISO646-US, also known as the
     * Basic Latin block of the Unicode character set.
     */
    public static final Charset US_ASCII = sun.nio.cs.US_ASCII.INSTANCE;

    /**
     * ISO Latin Alphabet {@literal No. 1}, also known as ISO-LATIN-1.
     */
    public static final Charset ISO_8859_1 = sun.nio.cs.ISO_8859_1.INSTANCE;

    /**
     * Eight-bit UCS Transformation Format.
     */
    public static final Charset UTF_8 = sun.nio.cs.UTF_8.INSTANCE;

    /**
     * Sixteen-bit UCS Transformation Format, big-endian byte order.
     */
    public static final Charset UTF_16BE = new sun.nio.cs.UTF_16BE();

    /**
     * Sixteen-bit UCS Transformation Format, little-endian byte order.
     */
    public static final Charset UTF_16LE = new sun.nio.cs.UTF_16LE();

    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark.
     */
    public static final Charset UTF_16 = new sun.nio.cs.UTF_16();
}
```
Or we can create from `Charset#forName`
```java
Charset charset = Charset.forName("UTF-8");
```

### Let's do an experiment to prove the concept

**In Java,** we can use `UTF-8` **charset** to encode a `String`(not `char`) to `byte[]`
```java
String emoji = "🔥";
byte[] emojiBytes = emoji.getBytes(StandardCharsets.UTF_8);
```
and the other way around from `byte[]` to `String`(not `char`)
```java
String emoji = new String(emojiBytes, StandardCharsets.UTF_8);
```

🙋 **You might ask:** we're learning about mapping `Unicode characters` to `byte sequences` so far, why don't we use `char`?

✅ Because `char` primitive type **in Java** is **NOT** equivalent to a `Unicode character`. `Unicode character` is equivalent to a `code point` as its definition.
We can easily prove that by:
```java
String emoji = "🔥";
int numOfChars = emoji.length(); // 2
```
We only have **1** `emoji character`, but it returns **2**, it's actually **the number** of `char`, this is equivalent to
```java
char[] array = emoji.toCharArray();
int numOfArray = array.length; // 2
```
👉 **1** `emoji character` needs 2 `char`s. It's easy to understand because 1 `emoji` is encoded to **4 bytes** (32 bits)
but in **Java**, `char` is only **2 bytes** (16 bits), thus, can only represent set of characters from `U+0000` to `U+FFFF` is sometimes referred to 
as the Basic Multilingual Plane (BMP) - [**Character - Java Documentation**](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Character.html)

💁 **How to get the number of `Unicode Characters` here?** - 🙋 **Counting the code points**
```java
String emoji = "🔥";
long numOfCodePoints = emoji.codePoints().count(); // 1
String s = "Z匁🔥";
long numOfCodePointsInS = s.codePoints().count(); // 3
```

#### Good to Know 👇
> **Noted:** If you dive more into `Unicode`, about `Unicode Normalization`, `character` can be ambiguous, you may want to use `Grapheme` - **a single unit of a human writing system**.
One `Grapheme` may be represented as a single `code point` or a combination of many `code points`.
> 
> One `Grapheme` `Á` could be:
> * `Á` -> `Á`(https://symbl.cc/en/00C1/)
> * `Á` -> `A`(https://symbl.cc/en/0041/) + `́`(https://symbl.cc/en/0301/)
```java
List<String> list = Normalizer.normalize("Á", Normalizer.Form.NFD) // NFD: Canonical decomposition 
        .codePoints()
        .mapToObj(cp -> String.format("%02x", cp))
        .toList(); // [41, 301]
```

🔥 **We just reviewed some knowledge about `Charset`, let's move on to process our file: `unicode_characters.txt` with it**.

The file `unicode_characters.txt` contains `Z匁🔥`, includes 1 english character `Z`, 1 japanese character `匁` and 1 emoji `🔥`.

👉 _We will run through the code below step by step:_
```java
public static void main(String[] args) throws IOException {
    byte[] allBytesInFile = Files.readAllBytes(Path.of("unicode_characters.txt"));
    System.out.println("Byte representation of the file contains: Z匁🔥");
    printByte(allBytesInFile);

    String english = "Z";
    String japanese = "匁";
    String emoji = "🔥";

    System.out.println("Z U+%s (https://symbl.cc/en/005A/)".formatted(Integer.toHexString(english.codePointAt(0))));
    byte[] englishBytes = english.getBytes(StandardCharsets.UTF_8);
    printByte(englishBytes);

    System.out.print("\t\t匁 U+%s (https://symbl.cc/en/5301/)\n\t\t".formatted(Integer.toHexString(japanese.codePointAt(0))));
    byte[] japaneseBytes = japanese.getBytes(StandardCharsets.UTF_8);
    printByte(japaneseBytes);

    System.out.print("\t\t\t\t\t\t\t\t🔥 U+%s (https://symbl.cc/en/1F525-fire-emoji/)\n\t\t\t\t\t\t\t\t".formatted(Integer.toHexString(emoji.codePointAt(0))));
    byte[] emojiBytes = emoji.getBytes(StandardCharsets.UTF_8);
    printByte(emojiBytes);

    System.out.println();
    utf8DecodeByteByByte(allBytesInFile);
    System.out.println();

    byte[] englishB = Arrays.copyOfRange(allBytesInFile, 0, 1);
    printByte(englishB);
    System.out.println(new String(englishB, StandardCharsets.UTF_8));

    byte[] japaneseB = Arrays.copyOfRange(allBytesInFile, 1, 4);
    System.out.print("\t\t");
    printByte(japaneseB);
    System.out.println("\t\t%s".formatted(new String(japaneseB, StandardCharsets.UTF_8)));

    byte[] emojiB = Arrays.copyOfRange(allBytesInFile, 4, 8);
    System.out.print("\t\t\t\t\t\t\t\t");
    printByte(emojiB);
    System.out.println("\t\t\t\t\t\t\t\t%s".formatted(new String(emojiB, StandardCharsets.UTF_8)));

    System.out.println();
    System.out.println(new String(allBytesInFile, StandardCharsets.UTF_8));
}

public static void printByte(byte[] bytes) {
    for (byte aByte : bytes) {
        System.out.print(String.format("%02x", aByte));
        System.out.print("\t|\t");
    }
    System.out.println();
}

public static void utf8DecodeByteByByte(byte[] bytes) {
    for (byte aByte : bytes) {
        String s = new String(new byte[]{aByte});
        System.out.print(s);
        System.out.print("\t|\t");
    }
    System.out.println();
}
```
```java
/* Legal UTF-8 Byte Sequences
 *
 * #    Code Points      Bits   Bit/Byte pattern
 * 1                     7      0xxxxxxx
 *      U+0000..U+007F          00..7F
 *
 * 2                     11     110xxxxx    10xxxxxx
 *      U+0080..U+07FF          C2..DF      80..BF
 *
 * 3                     16     1110xxxx    10xxxxxx    10xxxxxx
 *      U+0800..U+0FFF          E0          A0..BF      80..BF
 *      U+1000..U+FFFF          E1..EF      80..BF      80..BF
 *
 * 4                     21     11110xxx    10xxxxxx    10xxxxxx    10xxxxxx
 *     U+10000..U+3FFFF         F0          90..BF      80..BF      80..BF
 *     U+40000..U+FFFFF         F1..F3      80..BF      80..BF      80..BF
 *    U+100000..U10FFFF         F4          80..8F      80..BF      80..BF
 *
 */
```

> **Noted:** In **Java 17** and before, _the default charset_ was **the platform's default charset**. But in **Java 18**, it was set to **UTF-8**
> as part of [**JEP 400: UTF-8 by Default**](https://openjdk.org/jeps/400). 
> This means that **most Java APIs** will **use UTF-8 by default** for character encoding and decoding, _unless you explicitly specify a different charset_

I'm using Java 21, `s.getBytes(StandardCharsets.UTF_8)` and `s.getBytes()` result in the same, _but it's always a good practice to explicitly specify
the charset to ensure correct encoding and decoding._



