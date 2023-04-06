package com.digdes.school;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        JavaSchoolStarter javaSchoolStarter = new JavaSchoolStarter();
        Scanner scanner = new Scanner(System.in);
        while(true){
            String request = scanner.nextLine();
            if(request.isEmpty()){
                continue;
            }
            List<Map<String, Object>> list = javaSchoolStarter.execute(request);
            for (Map m: list){
                System.out.println(m);
            }
        }
        /*
         INSERT VALUES ‘lastName’ = ‘Максимов’, ‘id’ = 1, ‘age’=20,‘active’=true,‘cost’ = 1.5
         INSERT VALUES ‘lastName’ = ‘Соколов’, ‘id’ = 2, ‘age’=231,‘active’=false,‘cost’ = 2.5
         INSERT VALUES ‘lastName’ = ‘Кашин’, ‘id’ = 3, ‘age’=32,‘active’=true,‘cost’ = 2.5
         INSERT VALUES ‘lastName’ = ‘Ерин’, ‘id’ = 4, ‘age’=27,‘active’=true,‘cost’ = 5.0
         INSERT VALUES ‘lastName’ = ‘Подосенов’, ‘id’ = 5, ‘age’=30,‘active’=false,‘cost’ = 5.0
         INSERT VALUES ‘lastName’ = ‘Фоминский’, ‘id’ = 6, ‘age’=40,‘cost’ = 6.2
         select where ‘cost’ <= 4 and 'age' = 15
         select where ‘cost’ <= 4 or 'age' = 15
         select where ‘cost’ <= 4 or 'age' = 15 and 'id' = 2
         select where ‘cost’ <= 4 or 'age' = 15 and 'id' = 2 and 'age' = 231
         update values 'age' = 31 where 'lastName' like 'Со%
         update values 'age' = 31 where 'lastName' ilike '%дос% and 'id' = 5
         delete where ‘cost’ <= 4 or 'age' = 15
         update values 'age' = 35
         select where 'active' > true
         select where 'active' = false
         */

    }
}