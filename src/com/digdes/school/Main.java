package com.digdes.school;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {

        JavaSchoolStarter javaSchoolStarter = new JavaSchoolStarter();

        String initialRequest = "INSERT VALUES   ‘lastName’ =    ‘Федоров’ , ‘id’=3, ‘age’=40, ‘active’=true";
        byte[] bytes = initialRequest.getBytes(StandardCharsets.UTF_8);
        initialRequest = new String(bytes);

        //String[] str1 = new String{0, 1, 2, 3, 4, 5, 6};


        List<Map<String, Object>> list = javaSchoolStarter.execute(initialRequest);
        for (Map m: list){
            System.out.println(m);
        }

    }
}