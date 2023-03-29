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

        String initialRequest = "INSERT VALUES   ‘lastName’ =    ‘Федоров’ , ‘id’=3.0, ‘age’=40, ‘active’=true";
        byte[] bytes = initialRequest.getBytes(StandardCharsets.UTF_8);
        initialRequest = new String(bytes);

//        String bool = "false";
//        Boolean value = Boolean.valueOf(bool);
//        System.out.println(value);


        List<Map<String, Object>> table = javaSchoolStarter.execute(initialRequest);
        table.stream().forEach(System.out::println);




//        Pattern pattern = Pattern.compile("‘[a-zA-Z]+’");
//        Matcher matcher = pattern.matcher(request);
//        System.out.println(matcher.find());

    }
}