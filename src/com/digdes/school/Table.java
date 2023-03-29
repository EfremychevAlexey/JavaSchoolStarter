package com.digdes.school;

import java.util.*;
import java.util.regex.Pattern;

public class Table {
    private ArrayList<Map<String, Object>> collection = new ArrayList<>();

    public ArrayList<Map<String, Object>> getCollection(){
        return collection;
    }

    public void clear() {
        collection.clear();
    }

    public List<Map<String, Object>> select(ArrayList<String> parameters) {

        for (String parameter: parameters){
            String[] parameterArray = parameter.split("=");
            Arrays.stream(parameterArray).forEach(System.out::println);
        }

        //parameters.forEach(System.out::println);

        return new ArrayList<Map<String, Object>>();
    }
}
