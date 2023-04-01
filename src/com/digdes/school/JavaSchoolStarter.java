package com.digdes.school;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class JavaSchoolStarter {

    ArrayList<Map<String, Object>> table = new ArrayList<>();

    public JavaSchoolStarter() {

    }
    // Пример добавления строки в коллекцию:
    // INSERT VALUES ‘lastName’ = ‘Федоров’ , ‘id’=3, ‘age’=40, ‘active’=true

    public List<Map<String, Object>> execute(String initialRequest) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        System.out.println("Не оптимизированный запрос:  " + initialRequest);
        String[] requestArray = stringOptimization(initialRequest);
        System.out.println("Оптимизированный запрос:  ");
        Arrays.stream(requestArray).forEach(System.out::println);
        System.out.println();

        try {
            switch (requestArray[0].toLowerCase()) {
                case "select":
                    if (requestArray.length == 1) {
                        System.out.println("получен весь список");
                        return table;
                    } else if (requestArray[1].toLowerCase().equals("where") && requestArray.length > 2) {
                        String[] parameters = new String[requestArray.length - 2];
                        System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                        return select(parameters);
                    }
                case "delete":
                    if (requestArray.length == 1) {
                        table.clear();
                        System.out.println("таблица очищена");
                        return table;
                    } else if (requestArray[1].toLowerCase().equals("where") && requestArray.length > 2) {
                        String[] parameters = new String[requestArray.length - 2];
                        System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                        return delete(parameters);
                    } else {
                        throw new IllegalAccessException("Wrong format");
                    }
                case "insert":
                    if (requestArray.length <= 2) {
                        throw new IllegalAccessException("Wrong format");
                    } else if (requestArray[1].toLowerCase().equals("values")) {
                        String[] parameters = new String[requestArray.length - 2];
                        System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                        return insert(parameters);
                    } else {
                        throw new IllegalAccessException("Wrong format");
                    }
                case "update":
                    if (requestArray.length <= 2) {
                        throw new IllegalAccessException("Wrong format");
                    } else if (requestArray[0].toLowerCase().equals("update") && requestArray[1].toLowerCase().equals("values")) {
                        for (int i = 2; i < requestArray.length; i++) {
                            if (requestArray[i].toLowerCase().equals("where")) {
                                String[] parameters = new String[i - 2];
                                System.out.println("Размер массива с параметрами " + parameters.length);
                                String[] selectionBy = new String[requestArray.length - i];
                                System.out.println("Размер массива с параметрами отбора " + selectionBy.length);

                                System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                                System.arraycopy(requestArray, i + 1, selectionBy, 0, selectionBy.length);
                                return update(parameters, selectionBy);
                            } else {
                                String[] parameters = new String[requestArray.length - 1];
                                System.arraycopy(requestArray, 1, parameters, 0, parameters.length);
                                return update(parameters, null);
                            }
                        }
                    } else {
                        throw new IllegalAccessException("Wrong format");
                    }
                default:
                    throw new IllegalAccessException("Wrong format");
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    //  ***DELETE***   Удаление элементов из таблицы
    private List<Map<String, Object>> delete(String[] parameters) throws Exception {
        ArrayList<Map<String, Object>> deletedLines = new ArrayList<>();
        for (String field : parameters) {
            String column, operator, stringValue;
            String[] parametersArray = getParametersArray(field);
            column = parametersArray[0];
            operator = parametersArray[1];
            stringValue = parametersArray[2];

            switch (operator){
                case ("="):
                    deletedLines.addAll(table.stream().filter(row ->
                            row.get(column).toString().equals(stringValue)).collect(Collectors.toList()));
                    for (Map row : deletedLines){
                        System.out.println(row);
                    }
                    break;
                case ("<="):
                    if(column.equals("id") || column.equals("age")){
                        deletedLines.addAll(table.stream().filter(row ->
                                        (long)row.get(column) <= Long.parseLong(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    } else if (column.equals("cost")) {
                        deletedLines.addAll(table.stream().filter(row ->
                                        (double)row.get(column) <= Double.parseDouble(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    }
                    else {
                        throw new Exception("The data cannot be compared" + column + operator + stringValue);
                    }
                case (">="):
                    if(column.equals("id") || column.equals("age")){
                        deletedLines.addAll(table.stream().filter(row ->
                                        (long)row.get(column) >= Long.parseLong(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    }
                    else if (column.equals("cost")) {
                        deletedLines.addAll(table.stream().filter(row ->
                                        (double)row.get(column) >= Double.parseDouble(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    }
                    else {
                        throw new Exception("The data cannot be compared" + column + operator + stringValue);
                    }
                case ("!="):
                    if(column.equals("id") || column.equals("age")){
                        deletedLines.addAll(table.stream().filter(row ->
                                        (long)row.get(column) != Long.parseLong(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    }
                    else if (column.equals("cost")) {
                        deletedLines.addAll(table.stream().filter(row ->
                                        (double)row.get(column) != Double.parseDouble(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    }
                    else {
                        throw new Exception("The data cannot be compared" + column + operator + stringValue);
                    }
                case ("<"):
                    if(column.equals("id") || column.equals("age")){
                        deletedLines.addAll(table.stream().filter(row ->
                                        (long)row.get(column) < Long.parseLong(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    } else if (column.equals("cost")) {
                        deletedLines.addAll(table.stream().filter(row ->
                                        (double)row.get(column) <= Double.parseDouble(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    }
                    else {
                        throw new Exception("The data cannot be compared" + column + operator + stringValue);
                    }
                case (">"):
                    if(column.equals("id") || column.equals("age")){
                        deletedLines.addAll(table.stream().filter(row ->
                                        (long)row.get(column) <= Long.parseLong(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    } else if (column.equals("cost")) {
                        deletedLines.addAll(table.stream().filter(row ->
                                        (double)row.get(column) > Double.parseDouble(stringValue))
                                .collect(Collectors.toList()));
                        for (Map row : deletedLines){
                            System.out.println(row);
                        }
                        break;
                    }
                    else {
                        throw new Exception("The data cannot be compared" + column + operator + stringValue);
                    }
                case ("like"):
//                    if (column.equals("lastName")){
//                        if(stringValue.matches("\b[a-zA-Z[а-яА-Я]]+\b")){
//                            deletedLines.addAll(table.stream().filter(row ->
//                                            row.get("lastName").toString().contains(stringValue))
//                                    .collect(Collectors.toList()));
//                            for (Map row : deletedLines){
//                                System.out.println(row);
//                            }
//                            break;
//                        }
//                        if (stringValue.matches("\b[a-zA-Z[а-яА-Я]]+")){
//
//                        }
//                    }
//                    else {
//                        throw new Exception("The data cannot be compared" + column + operator + stringValue);
//                    }

                case ("ilike"):

            }
        }

        return new ArrayList<Map<String, Object>>();
    }

    //  ***INSERT***       Метод вставки элемента в таблицу
    private List<Map<String, Object>> insert(String[] parameters) throws IllegalAccessException {
        System.out.println("Выполнение insert");
        boolean mapIsEmpty = true;
        ArrayList<Map<String, Object>> listInsert = new ArrayList<>();
        Map<String, Object> map = initialMap();

        for (String field : parameters) {
            String column, operator, stringValue;
            String[] parametersArray = getParametersArray(field);
            column = parametersArray[0];
            System.out.println("Колонка " + column);
            operator = parametersArray[1];
            System.out.println("Оператор " + operator);
            stringValue = parametersArray[2];
            System.out.println("Значение " + stringValue);

            if (!operator.equals("=")) {
                throw new IllegalAccessException("Wrong format: " + column + operator + stringValue);
            } else {
                switch (column) {
                    case "id":
                        try {
                            map.put(column, Long.valueOf(stringValue));
                            System.out.println("Добавлено поле Id");
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "lastName":
                        if (stringValue.matches("[a-zA-Z[а-яА-я]]+")) {
                            String value = stringValue.replaceAll("'", "");
                            map.put(column, value);
                            System.out.println("Добавлено поле lastName");
                            break;
                        }
                    case "age":
                        try {
                            map.put(column, Long.valueOf(stringValue));
                            System.out.println("Добавлено поле age");
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "cost":
                        try {
                            map.put(column, Double.valueOf(stringValue));
                            System.out.println("Добавлено поле cost");
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "active":
                        map.put(column, Boolean.valueOf(stringValue));
                        System.out.println("Добавлено поле active");
                        break;
                    default:
                        throw new IllegalAccessException("Wrong the column name");
                }
            }
        }
        listInsert.add(map);
        table.addAll(listInsert);
        return table;
    }


    // Метод обновления элементов таблицы
    private List<Map<String, Object>> update(String[] parameters, String[] selectedBy) {
//        ArrayList<String[]> parametersList = new ArrayList<>();
//        for (String field : parameters) {
//            if(field.toLowerCase().equals("and") || field.toLowerCase().equals("or")){
//                parametersList.add(new String[]{field.toLowerCase()});
//                continue;
//            }
//            String column, operator, stringValue;
//            String[] parametersArray = getParametersArray(field);
//            column = parametersArray[0];
//            operator = parametersArray[1];
//            stringValue = parametersArray[2];
//            parametersList.add(new String[]{column, operator, stringValue});
//        }
//        for (String[] parameter : parametersList){
//            for(String str : parameter){
//                System.out.print(str);
//            }
//            System.out.println();
//        }
        return new ArrayList<Map<String, Object>>();
    }



    // Получение списка элементов
    private List<Map<String, Object>> select(String[] requestArray) {
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


    private Map<String, Object> initialMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", null);
        map.put("lastName", null);
        map.put("age", null);
        map.put("cost", null);
        map.put("active", null);
        return map;
    }

    public String[] getParametersArray(String field) throws IllegalAccessException {
        String column, operator, stringValue;
        Pattern patternColumn = Pattern.compile("^'[a-zA-Z]+'");
        Matcher matcherColumn = patternColumn.matcher(field);
        Pattern patternOperator = Pattern.compile("#.+#");
        Matcher matcherOperator = patternOperator.matcher(field);

        if (matcherColumn.find()) {
            column = field.substring(matcherColumn.start() + 1, matcherColumn.end() - 1);
        } else {
            throw new IllegalAccessException("Wrong format");
        }
        if (matcherOperator.find()) {
            operator = field.substring(matcherOperator.start() + 1, matcherOperator.end() - 1);
            stringValue = field.substring(matcherOperator.end(), field.length())
                    .replaceAll("'","")
                    .replaceAll(",", "");
        } else {
            throw new IllegalAccessException("Wrong format");
        }
        return new String[]{column, operator, stringValue};
    }

    private String[] stringOptimization(String request) {
        String stringRequest = request.replaceAll("\s+", " ")
                .replaceAll("\s?,\s?", ",\s")
                .replaceAll("‘", "'")
                .replaceAll("’", "'")
                .replaceAll("\s?=\s?","=")
                .replaceAll("\s?>\s?",">")
                .replaceAll("\s?<\s?","<")
                .replaceAll("\s?!\s?","!")
                .replaceAll("'=","'#=#")
                .replaceAll("[>=]{2}","#>=#")
                .replaceAll("[<=]{2}","#<=#")
                .replaceAll("[!=]{2}","#!=#")
                .replaceAll("'>","'#>#")
                .replaceAll("'<","'#<#")
                .replaceAll("\slike\s","#like#")
                .replaceAll("\silike\s","#ilike#")
                .trim();

        String[] requestArray = stringRequest.split("\s");

        return requestArray;
    }
}