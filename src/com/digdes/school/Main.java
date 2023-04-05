package com.digdes.school;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {

        JavaSchoolStarter javaSchoolStarter = new JavaSchoolStarter();
        Scanner scanner = new Scanner(System.in);
        while(true){
            String request = scanner.nextLine();
            List<Map<String, Object>> list = javaSchoolStarter.execute(request);
            for (Map m: list){
                System.out.println(m);
            }
        }
        /*

         INSERT VALUES ‘lastName’ = ‘Федоров’, ‘id’ = 1
         INSERT VALUES ‘lastName’ = ‘Максимов’, ‘id’ = 2, ‘age’=20,‘active’=true,‘cost’ = 2.5
         INSERT VALUES ‘lastName’ = ‘Демидов’ , ‘id’ = 3, ‘age’=25,‘active’=true,‘cost’ = 3.5
         INSERT VALUES ‘lastName’ = ‘Киселев’ , ‘id’ = 4, ‘age’=30,‘active’=true,‘cost’ = 4.5
         INSERT VALUES ‘lastName’ = ‘Киселев’ , ‘id’ = 4, ‘age’=30,‘active’=true,‘cost’ = 4.5
         select where ‘cost’ <= 4 or 'age' = 15
         delete where ‘cost’ <= 4 or 'age' = 15
         Select WHERE ‘cost’ = 5.5 or 'age' = 25
         Update VALUES ‘lastName’ = ‘Ефремов’ where 'cost' >= 3

         */

    }
}