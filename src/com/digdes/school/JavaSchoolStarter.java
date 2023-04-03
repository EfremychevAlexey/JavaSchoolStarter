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
        Set<Map<String, Object>> deleteSet = new HashSet<>();
        Set<Map<String, Object>> cumulativeSet = new HashSet<>();

        for (int i = 0; i < parameters.length; i++) {
            if(parameters[i].toLowerCase().equals("and")){
                if(i == parameters.length){
                    throw new IllegalAccessException("Wrong format: " + parameters[i]);
                }
                Set<Map<String, Object>> excludedSet = getCumulativeList(parameters[++i]);

                for (Map m: cumulativeSet){
                    if (excludedSet.contains(m)){
                        deleteSet.add(m);
                    }
                }
                cumulativeSet.clear();
            }
            else if (parameters[i].toLowerCase().equals("or")){
                if(i == parameters.length){
                    throw new IllegalAccessException("Wrong format: " + parameters[i]);
                }
                Set<Map<String, Object>> complementarySet = getCumulativeList(parameters[++i]);
                deleteSet.addAll(complementarySet);
                deleteSet.addAll(cumulativeSet);
                cumulativeSet.clear();
            }
            else {
                cumulativeSet.addAll(getCumulativeList(parameters[i]));
            }
        }
        deleteSet.addAll(cumulativeSet);
        System.out.println("Данные на удаление: ");
        for (Map m : deleteSet){
            System.out.println(m);
            table.remove(m);
        }
        return deleteSet.stream().toList();
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
                        if (stringValue.matches("%?[a-zA-Z[а-яА-я]]+%?")) {
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


    //  ***SELECT***  Получение списка элементов из таблицы
    private List<Map<String, Object>> select(String[] parameters) throws Exception {
        Set<Map<String, Object>> selectSet = new HashSet<>();
        Set<Map<String, Object>> cumulativeSet = new HashSet<>();

        for (int i = 0; i < parameters.length; i++) {
            if(parameters[i].toLowerCase().equals("and")){
                Set<Map<String, Object>> excludedSet = getCumulativeList(parameters[++i]);
                for (Map m: cumulativeSet){
                    if (excludedSet.contains(m)){
                        selectSet.add(m);
                    }
                }
                cumulativeSet.clear();
            }
            else if (parameters[i].toLowerCase().equals("or")){
                Set<Map<String, Object>> complementarySet = getCumulativeList(parameters[++i]);
                selectSet.addAll(complementarySet);
                selectSet.addAll(cumulativeSet);
                cumulativeSet.clear();
            }
            else {
                cumulativeSet.addAll(getCumulativeList(parameters[i]));
            }
        }
        selectSet.addAll(cumulativeSet);
        System.out.println("Данные по запросу: ");
        for (Map m : selectSet){
            System.out.println(m);
        }
        return selectSet.stream().toList();
    }

    private Set<Map<String, Object>> getCumulativeList(String field) throws Exception {
        Set<Map<String, Object>> cumulativeList = new HashSet<>();
        String column, operator, stringValue;
        String[] parametersArray = getParametersArray(field);
        column = parametersArray[0];
        operator = parametersArray[1];
        stringValue = parametersArray[2];

        switch (operator) {
            case ("="):
                if (column.equals("id") || column.equals("cost") || column.equals("age")) {
                    System.out.println("Enter");
                }

                cumulativeList.addAll(table.stream().filter(row ->
                        row.get(column).toString().equals(stringValue)).collect(Collectors.toList()));
                break;
            case ("<="):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (long) row.get(column) <= Long.parseLong(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (double) row.get(column) <= Double.parseDouble(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared" + column + operator + stringValue);
                }
            case (">="):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (long) row.get(column) >= Long.parseLong(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (double) row.get(column) >= Double.parseDouble(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared" + column + operator + stringValue);
                }
            case ("!="):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (long) row.get(column) != Long.parseLong(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (double) row.get(column) != Double.parseDouble(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared" + column + operator + stringValue);
                }
            case ("<"):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (long) row.get(column) < Long.parseLong(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (double) row.get(column) <= Double.parseDouble(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared" + column + operator + stringValue);
                }
            case (">"):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (long) row.get(column) > Long.parseLong(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    (double) row.get(column) > Double.parseDouble(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared" + column + operator + stringValue);
                }
            case ("like"):
                if (stringValue.matches("[a-zA-Zа-яА-Я]+")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    row.get(column).toString().contains(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else if (stringValue.matches("[a-zA-Zа-яА-Я]+%")) {
                    String regex = "\\b" + stringValue.replaceAll("%", "");
                    Pattern pattern = Pattern.compile(regex);

                    cumulativeList.addAll(table.stream()
                            .filter(row -> {
                                String value = row.get(column).toString();
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    return true;
                                } else return false;
                            }).collect(Collectors.toList()));
                    break;

                } else if (stringValue.matches("%[a-zA-Zа-яА-Я]+%")) {
                    String regex = "\\B" + stringValue.replaceAll("%", "") + "\\B";
                    Pattern pattern = Pattern.compile(regex);

                    cumulativeList.addAll(table.stream()
                            .filter(row -> {
                                String value = row.get(column).toString();
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    return true;
                                } else return false;
                            }).collect(Collectors.toList()));
                    break;
                } else if (stringValue.matches("%[a-zA-Zа-яА-Я]+")) {
                    String regex = stringValue.replaceAll("%", "") + "\\b";
                    Pattern pattern = Pattern.compile(regex);

                    cumulativeList.addAll(table.stream()
                            .filter(row -> {
                                String value = row.get(column).toString();
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    return true;
                                } else return false;
                            }).collect(Collectors.toList()));
                    break;

                } else {
                    break;
                }
            case ("ilike"):
                if (stringValue.matches("[a-zA-Zа-яА-Я]+")) {
                    cumulativeList.addAll(table.stream().filter(row ->
                                    row.get(column).toString().toLowerCase().contains(stringValue))
                            .collect(Collectors.toList()));
                    break;
                } else if (stringValue.matches("[a-zA-Zа-яА-Я]+%")) {
                    String regex = "\\b" + stringValue.replaceAll("%", "");
                    Pattern pattern = Pattern.compile(regex);

                    cumulativeList.addAll(table.stream()
                            .filter(row -> {
                                String value = row.get(column).toString().toLowerCase();
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    return true;
                                } else return false;
                            }).collect(Collectors.toList()));
                    break;

                } else if (stringValue.matches("%[a-zA-Zа-яА-Я]+%")) {
                    String regex = "\\B" + stringValue.replaceAll("%", "") + "\\B";
                    Pattern pattern = Pattern.compile(regex);

                    cumulativeList.addAll(table.stream()
                            .filter(row -> {
                                String value = row.get(column).toString().toLowerCase();
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    return true;
                                } else return false;
                            }).collect(Collectors.toList()));
                    break;
                } else if (stringValue.matches("%[a-zA-Zа-яА-Я]+")) {
                    String regex = stringValue.replaceAll("%", "") + "\\b";
                    Pattern pattern = Pattern.compile(regex);

                    cumulativeList.addAll(table.stream()
                            .filter(row -> {
                                String value = row.get(column).toString().toLowerCase();
                                Matcher matcher = pattern.matcher(value);
                                if (matcher.find()) {
                                    return true;
                                } else return false;
                            }).collect(Collectors.toList()));
                    break;

                } else {
                    break;
                }
        }
        return cumulativeList;
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
                    .replaceAll("'", "")
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
                .replaceAll("\s?=\s?", "=")
                .replaceAll("\s?>\s?", ">")
                .replaceAll("\s?<\s?", "<")
                .replaceAll("\s?!\s?", "!")
                .replaceAll("'=", "'#=#")
                .replaceAll("[>=]{2}", "#>=#")
                .replaceAll("[<=]{2}", "#<=#")
                .replaceAll("[!=]{2}", "#!=#")
                .replaceAll("'>", "'#>#")
                .replaceAll("'<", "'#<#")
                .replaceAll("\slike\s", "#like#")
                .replaceAll("\silike\s", "#ilike#")
                .trim();

        String[] requestArray = stringRequest.split("\s");

        return requestArray;
    }
}