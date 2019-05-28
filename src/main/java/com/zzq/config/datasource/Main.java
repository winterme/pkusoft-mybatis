package com.zzq.config.datasource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String str = "mybatis.datasource.config[0].type";
        Matcher matcher = Pattern.compile("mybatis.datasource.config\\D+(\\d+)\\D+").matcher(str);
        if ( matcher.find() ){
            System.out.println( matcher.group(1) );
        }
    }

}
