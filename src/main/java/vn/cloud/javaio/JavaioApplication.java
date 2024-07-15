package vn.cloud.javaio;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

//@SpringBootApplication
public class JavaioApplication {

	public static void main(String[] args) {
//		SpringApplication.run(JavaioApplication.class, args);

		File file1 = new File("/Users/linhvu/Desktop/javaio/staging/hi1.txt"); //(1)

		File file2 = new File("/Users/linhvu/Desktop/javaio", "staging/hi1.txt"); //(2)

		File folder = new File("/Users/linhvu/Desktop/javaio");
		File file3 = new File(folder, "staging/hi1.txt"); //(3)

		File file4 = new File(URI.create("file:///Users/linhvu/Desktop/javaio/staging/hi1.txt"));

		Path path1 = Paths.get("/Users/linhvu/Desktop/javaio/staging/hi1.txt"); //(1)
		Path path2 = Paths.get("/Users", "linhvu/Desktop", "javaio/staging/hi1.txt"); //(1)

		Path path3 = Paths.get(URI.create("file:///Users/linhvu/Desktop/javaio/staging/hi1.txt")); //(2)

		Path path4 = FileSystems.getDefault().getPath("/Users/linhvu/Desktop/javaio/staging/hi1.txt");
		Path path5 = FileSystems.getDefault().getPath("/Users", "linhvu/Desktop", "javaio/staging/hi1.txt");

		Path path6 = Path.of("/Users/linhvu/Desktop/javaio/staging/hi1.txt"); //(1)
		Path path7 = Path.of("/Users", "linhvu/Desktop", "javaio/staging/hi1.txt"); //(1)

		Path path8 = Path.of(URI.create("file:///Users/linhvu/Desktop/javaio/staging/hi1.txt")); //(2)

		System.out.println(file1.getAbsolutePath());
		System.out.println(file2.getAbsolutePath());
		System.out.println(file3.getAbsolutePath());
		System.out.println(file4.getAbsolutePath());
		System.out.println(path1.toAbsolutePath());
		System.out.println(path2.toAbsolutePath());
		System.out.println(path3.toAbsolutePath());
		System.out.println(path4.toAbsolutePath());
		System.out.println(path5.toAbsolutePath());
		System.out.println(path6.toAbsolutePath());
		System.out.println(path7.toAbsolutePath());
		System.out.println(path8.toAbsolutePath());
	}

}
