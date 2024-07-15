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