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
    }
}