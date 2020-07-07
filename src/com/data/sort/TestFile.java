package com.data.sort;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestFile {
    private String dd = "dd";
    private static Test name  ;

    private TestFile() {

    }
public static void main(String [] args){
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    long time = 1565923584000L;
    date.setTime(time);
    long time1 = 1566143999000L;


        System.out.println("gg:"+sdf.format(date) );
    date.setTime(time1);
    System.out.println("gg:"+sdf.format(date) );
}

}
