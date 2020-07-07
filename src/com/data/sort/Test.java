package com.data.sort;


import com.lowagie.text.pdf.BaseFont;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
    private String name;
    private int age;

    public Test() {
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Test(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * 备份数据库db
     *
     * @param root
     * @param pwd
     * @param dbName
     * @param backPath
     * @param backName
     */
    public static void dbBackUp(String root, String pwd, String dbName, String backPath, String backName) throws Exception {
        String pathSql = backPath + backName;
        File fileSql = new File(pathSql);
        //创建备份sql文件
        if (!fileSql.exists()) {
            fileSql.createNewFile();
        }
        BufferedReader bufferedReader = null;
        //mysqldump -hlocalhost -uroot -p123456 db > /home/back.sql
        StringBuffer sb = new StringBuffer();
        sb.append("mysqldump");
        sb.append(" -h47.98.226.53");
        sb.append(" -u" + root);
        sb.append(" -p" + pwd);
        sb.append(" " + dbName + " >");
        sb.append(pathSql);

        Runtime runtime = Runtime.getRuntime();

        Process process = runtime.exec("cmd /c " + sb.toString());


        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");
        bufferedReader = new BufferedReader(inputStreamReader);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            //printWriter.println(line);  
        }

    }

    /**
     * 恢复数据库
     *
     * @param root
     * @param pwd
     * @param dbName
     * @param filePath mysql -hlocalhost -uroot -p123456 db < /home/back.sql
     */
    public static void dbRestore(String root, String pwd, String dbName, String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("mysql");
        sb.append(" -h47.98.226.53");
        sb.append(" -u" + root);
        sb.append(" -p" + pwd);
        sb.append(" " + dbName + " <");
        sb.append(filePath);

        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec("cmd /c" + sb.toString());
            InputStream is = process.getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "utf8"));
            String line = null;
            while ((line = bf.readLine()) != null) {

            }
            is.close();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param username
     * @param password
     * @param dbname
     * @param path
     * @param
     */
    private static void extracted(String username, String password,
                                  String dbname, String path) {
        StringBuilder cmd;
        // 设置文件名，根据时间来写
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(Calendar.getInstance().getTime()) + dbname;
        // 实例化cmd对象
        cmd = new StringBuilder();
        // 一下操作是做cmd命令的拼写
        cmd.append("cmd.exe /C mysqldump -u");
        cmd.append(username);
        cmd.append(" -p");
        cmd.append(password);
        cmd.append(" ");
        cmd.append(dbname);
        cmd.append(" > ");
        cmd.append(path);
        cmd.append("/" + fileName + ".sql");
        // 获取一个Runtime对象
        Runtime r = Runtime.getRuntime();
        try {
            // 执行cmd备份操作
            Process process = r.exec(cmd.toString());

            InputStream is = process.getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "utf8"));
            String line = null;

            while ((line = bf.readLine()) != null) {

            }
            is.close();
            bf.close();
        } catch (IOException e) {
            // 出错了，记日志文件到e盘的backupmysql.log文件
            //Log.printErr(e, "e:/backupmysql.log");
            System.exit(0);
        }
    }


    public static void main(String[] args) throws Exception {
        new Test().convertHtmlToPdf("D:/ff.html", "D:/ff.pdf");
    }

    public boolean convertHtmlToPdf(String inputFile, String outputFile)
            throws Exception {

//        OutputStream os = new FileOutputStream(outputFile);
//        ITextRenderer renderer = new ITextRenderer();
//        String url = new File(inputFile).toURI().toURL().toString();
//System.out.println("url:"+url);
//        renderer.setDocument(url);
//
//        // 解决中文支持问题
//        ITextFontResolver fontResolver = renderer.getFontResolver();
//        fontResolver.addFont("C:/Windows/Fonts/SIMSUN.TTC", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//        //解决图片的相对路径问题
//        renderer.getSharedContext().setBaseURL("file:/D:/");
//        renderer.layout();
//        renderer.createPDF(os);
//
//        os.flush();
//        os.close();
        return true;
    }

}
