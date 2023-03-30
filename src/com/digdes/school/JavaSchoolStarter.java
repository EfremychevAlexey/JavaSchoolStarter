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
        String[] requestArray = stringOptimization(initialRequest);

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
                        System.arraycopy(requestArray, 2, parameters, 0, requestArray.length);
                        return delete(parameters);
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
            ex.getMessage();
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Метод вставки элемента в таблицу
    private List<Map<String, Object>> insert(String[] parameters) throws IllegalAccessException, NumberFormatException {
        System.out.println("Выполнение insert");
        ArrayList<Map<String, Object>> listInsert = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        for (String field : parameters) {
            String column, operator, stringValue;
            Pattern patternColumn = Pattern.compile("^'[a-zA-Z]+'");
            Matcher matcherColumn = patternColumn.matcher(field);
            Pattern patternValue = Pattern.compile("'[a-zA-Z[а-яА-я]]+'");

            if (matcherColumn.find()) {
                column = field.substring(matcherColumn.start() + 1, matcherColumn.end() - 1);
                operator = field.substring(matcherColumn.end(), matcherColumn.end() + 1);
                if (!operator.equals("=")) {
                    throw new IllegalAccessException("Wrong format");
                }
                stringValue = field.substring(matcherColumn.end() + 1).replaceAll(",", "");
            } else {
                throw new IllegalAccessException("Wrong format");
            }
            switch (column) {
                case "id":
                    map.put(column, Long.valueOf(stringValue));
                    System.out.println("Добавлено поле Id");
                    break;
                case "lastName":
                    if (stringValue.matches("'[a-zA-Z[а-яА-я]]+'")) {
                        String value = stringValue.replaceAll("'", "");
                        map.put(column, value);
                        System.out.println("Добавлено поле lastName");
                        break;
                    }
                case "age":
                    map.put(column, Integer.valueOf(stringValue));
                    System.out.println("Добавлено поле age");
                    break;
                case "cost":
                    map.put(column, Double.valueOf(stringValue));
                    System.out.println("Добавлено поле cost");
                    break;
                case "active":
                    map.put(column, Boolean.valueOf(stringValue));
                    System.out.println("Добавлено поле active");
                    break;
                default:
                    throw new IllegalAccessException("Wrong format");
            }
        }
        listInsert.add(map);
        return listInsert;
    }


    // Метод обновления элементов таблицы
    private List<Map<String, Object>> update(String[] parameters, String[] selectedBy) {
        return new ArrayList<Map<String, Object>>();
    }

    // Удаление элементов из таблицы
    private List<Map<String, Object>> delete(String[] requestArray) {
        if (requestArray[0].toLowerCase().equals("delete")) {
            table.clear();
            return table;
        } else if (requestArray[0].toLowerCase().equals("select") && requestArray[1].toLowerCase().equals("where")) {
            //TODO написать работу метода
        }

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


    private String[] stringOptimization(String request) {
        String stringRequest = request.replaceAll("\s+", " ")
                .replaceAll("‘", "'")
                .replaceAll("’", "'")
                .replaceAll("\s?=\s+?", "=")
                .replaceAll("\s?+,\s?", ",\s").trim();

        String[] requestArray = stringRequest.split("\s");

        return requestArray;
    }
}