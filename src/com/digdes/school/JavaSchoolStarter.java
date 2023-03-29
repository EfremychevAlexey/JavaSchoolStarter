package com.digdes.school;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaSchoolStarter {

    private ArrayList<Map<String, Object>> table = new ArrayList<>();

    public JavaSchoolStarter() {

    }
    // Пример добавления строки в коллекцию:
    // INSERT VALUES ‘lastName’ = ‘Федоров’ , ‘id’=3, ‘age’=40, ‘active’=true

    public List<Map<String, Object>> execute(String initialRequest) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        System.out.println("Не оптимизированный запрос:  " + initialRequest);
        String request = stringOptimization(initialRequest);
        System.out.println("Оптимизированный запрос:  " + request);
        String[] requestArray = request.split("\s");

        if (requestArray[0].toLowerCase().equals("insert")) {
            System.out.println("Выполнен запрос insert");
            List<Map<String, Object>> insertElements = insert(requestArray);
            return insertElements;
        }
        if (request.toLowerCase().equals("update")) {
            return update(requestArray);
        }
        if (request.toLowerCase().equals("delete")) {
            return delete(requestArray);
        }
        if (request.toLowerCase().equals("select")) {

            return select(requestArray);
        }

        return table;
    }

    // Метод вставки элемента в таблицу
    public List<Map<String, Object>> insert(String[] requestArray) {
        System.out.println("Выполнение insert");
        ArrayList<Map<String, Object>> listInsert = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        if (requestArray[0].toLowerCase().equals("insert") && requestArray[1].toLowerCase().equals("values")) {
            System.out.println("Выполнение insert values");
            for(int i = 2; i < requestArray.length; i++) {
                String field = requestArray[i].replaceAll(",","");
                Pattern patternColumn = Pattern.compile("^'[a-zA-Z]+'");
                Matcher matcherColumn = patternColumn.matcher(field);
                Pattern patternValue = Pattern.compile("'[a-zA-Z[а-яА-я]]+'");

                if(matcherColumn.find()) {
                    String column = field.substring(matcherColumn.start() + 1, matcherColumn.end() - 1);
                    String operator = field.substring(matcherColumn.end(), matcherColumn.end() + 1);
                    String stringValue = field.substring(matcherColumn.end() + 1);

                    if(!operator.equals("=")){
                        System.out.println("Ошибка ввода");
                        break;
                    }

                    if(column.equals("id")){
                        Long value = Long.valueOf(stringValue);
                        map.put(column, value);
                        System.out.println("Добавлено поле Id");
                        continue;
                    } else if(column.equals("lastName")){
                        if(stringValue.matches("'[a-zA-Z[а-яА-я]]+'")){
                            String value = stringValue.replaceAll("'","");
                            map.put(column, value);
                            System.out.println("Добавлено поле lastName");
                            continue;
                        }
                    } else if(column.equals("age")){
                        Integer value = Integer.valueOf(stringValue);
                        map.put(column, value);
                        System.out.println("Добавлено поле age");
                        continue;
                    } else if(column.equals("cost")){
                        Double value = Double.valueOf(stringValue);
                        map.put(column, value);
                        System.out.println("Добавлено поле cost");
                        continue;
                    } else if(column.equals("active")){
                        Boolean value = Boolean.valueOf(stringValue);
                        map.put(column, value);
                        System.out.println("Добавлено поле activ");
                        continue;
                    }
                }
                else System.out.println("Ошибка ввода");
                return null;
            }
            listInsert.add(map);
        } else{
            System.out.println("Ошибка в синтаксисе");
        }
        return listInsert;
    }



    // Метод обновления элементов таблицы
    public List<Map<String, Object>> update(String[] requestArray) {
        return new ArrayList<Map<String, Object>>();
    }

    // Удаление элементов из таблицы
    public List<Map<String, Object>> delete(String[] requestArray) {
        if (requestArray[0].toLowerCase().equals("delete")) {
            table.clear();
            return table;
        }
        if (requestArray[0].toLowerCase().equals("select") && requestArray[1].toLowerCase().equals("where")) {
            //TODO написать работу метода
        }

        return new ArrayList<Map<String, Object>>();
    }

    // Получение списка элементов
    public List<Map<String, Object>> select(String[] requestArray) {
        if (requestArray.length == 1 && requestArray[0].toLowerCase().equals("select")) {
            return table;
        }

        if (requestArray[0].toLowerCase().equals("select") && requestArray[1].toLowerCase().equals("where")) {

            //TODO написать работу метода
            for (int i = 2; i < requestArray.length; i++) {
            }
            return table;
        }

        return new ArrayList<Map<String, Object>>();
    }


    public String stringOptimization(String request) {
        String str = request.replaceAll("\s+", " ")
                .replaceAll("\s=\s", "=")
                .replaceAll("‘", "'")
                .replaceAll("’", "'")
                .replaceAll("\s?+,\s?", ",\s").trim();


        return str;
    }
}