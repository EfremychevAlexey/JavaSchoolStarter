package com.digdes.school;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {

        JavaSchoolStarter javaSchoolStarter = new JavaSchoolStarter();
//        Scanner scanner = new Scanner(System.in);
//        while(true){
//            String request = scanner.nextLine();
//            List<Map<String, Object>> list = javaSchoolStarter.execute(request);
//            for (Map m: list){
//                System.out.println(m);
//            }
//        }


        ArrayList<String> requestList = new ArrayList<>();
        requestList.add("INSERT VALUES ‘lastName’ = ‘Федоров’, ‘id’ = 1, ‘age’=15,‘active’=true,‘cost’ = 1.5");
        requestList.add("INSERT VALUES ‘lastName’ = ‘Максимов’ , ‘id’ = 2, ‘age’=20,‘active’=true,‘cost’ = 2.5");
        requestList.add("INSERT VALUES ‘lastName’ = ‘Демидов’ , ‘id’ = 3, ‘age’=25,‘active’=true,‘cost’ = 3.5");
        requestList.add("INSERT VALUES ‘lastName’ = ‘Киселев’ , ‘id’ = 4, ‘age’=30,‘active’=true,‘cost’ = 4.5");
        requestList.add("INSERT VALUES ‘lastName’ = ‘Абрамов’ , ‘id’ = 5, ‘age’=35,‘active’=true,‘cost’ = 5.5");
        requestList.add("DELETE WHERE ‘lastName’ like 'Киселев'");

        for(String request : requestList){
            javaSchoolStarter.execute(request);
        }
//        for (Map m: javaSchoolStarter.table){
//            System.out.println(m);
//        }

    }
}