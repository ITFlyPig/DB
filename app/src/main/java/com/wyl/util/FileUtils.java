package com.wyl.util;

import android.os.StrictMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author : yuelinwang
 * @time : 6/18/21
 * @desc : 文件工具类
 */
public class FileUtils {
    /**
     * 将String写到文件
     * @param file
     * @param string
     * @throws IOException
     */
    public static void stringToFile(File file, String string) throws IOException {
        stringToFile(file.getAbsolutePath(), string);
    }

    /**
     * 将String写到filename路径表示的文件
     * @param filePath
     * @param string
     * @throws IOException
     */
    public static void stringToFile(String filePath, String string) throws IOException {
        bytesToFile(filePath, string.getBytes());
    }

    /**
     * 将字节数组写到文件
     * @param filePath 绝对路径
     * @param content
     * @throws IOException
     */
    public static void bytesToFile(String filePath, byte[] content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath, true)) {
            fos.write(content);
        }
    }
}
