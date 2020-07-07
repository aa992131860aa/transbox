package com.life.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommUtils {
    public static void insertTransboxErrorFile(String content) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String recordTime = sdf.format(new Date()) + "=";
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            //File f = new File("/home/transbox_error.txt");
            File f = new File("E:\\transbox_error.txt");
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(recordTime + content);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delTransboxErrorFile() {
        File file = new File("E:\\transbox_error.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void redTransboxErrorFile() {
        File file = new File("E:\\transbox_error.txt");
        if (!file.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }


    public static void main(String args[]) {
        insertTransboxErrorFile("dddss");
        delTransboxErrorFile();
        redTransboxErrorFile();
    }
}
