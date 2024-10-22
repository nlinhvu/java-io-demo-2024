package vn.cloud.javaio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class CharacterStreamsDemo {
    public static void main(String[] args) throws IOException {
        byte[] allBytesInFile = Files.readAllBytes(Path.of("unicode_characters.txt"));
        System.out.println("Byte representation of the file contains: Z匁🔥");
        printByte(allBytesInFile);

        String english = "Z";
        String japanese = "匁";
        String emoji = "🔥";

        System.out.println("Z U+%s (https://symbl.cc/en/005A/)".formatted(Integer.toHexString(english.codePointAt(0))));
        byte[] englishBytes = english.getBytes();
        printByte(englishBytes);

        System.out.print("\t\t匁 U+%s (https://symbl.cc/en/5301/)\n\t\t".formatted(Integer.toHexString(japanese.codePointAt(0))));
        byte[] japaneseBytes = japanese.getBytes();
        printByte(japaneseBytes);

        System.out.print("\t\t\t\t\t\t\t\t🔥 U+%s (https://symbl.cc/en/1F525-fire-emoji/)\n\t\t\t\t\t\t\t\t".formatted(Integer.toHexString(emoji.codePointAt(0))));
        byte[] emojiBytes = emoji.getBytes();
        printByte(emojiBytes);

        System.out.println();
        utf8DecodeByteByByte(allBytesInFile);
        System.out.println();

        byte[] englishB = Arrays.copyOfRange(allBytesInFile, 0, 1);
        printByte(englishB);
        System.out.println(new String(englishB));

        byte[] japaneseB = Arrays.copyOfRange(allBytesInFile, 1, 4);
        System.out.print("\t\t");
        printByte(japaneseB);
        System.out.println("\t\t%s".formatted(new String(japaneseB)));

        byte[] emojiB = Arrays.copyOfRange(allBytesInFile, 4, 8);
        System.out.print("\t\t\t\t\t\t\t\t");
        printByte(emojiB);
        System.out.println("\t\t\t\t\t\t\t\t%s".formatted(new String(emojiB)));

        System.out.println();
        System.out.println(new String(allBytesInFile));
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

}