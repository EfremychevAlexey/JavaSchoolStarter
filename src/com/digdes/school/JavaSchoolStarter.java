package com.digdes.school;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class JavaSchoolStarter {

    ArrayList<Map<String, Object>> table = new ArrayList<>();

    public JavaSchoolStarter() {

    }

    public List<Map<String, Object>> execute(String initialRequest) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String[] requestArray = stringOptimization(initialRequest);

        try {
            switch (requestArray[0].toLowerCase()) {
                case "select":
                    if (requestArray.length == 1) {
                        return table;
                    } else if (requestArray[1].toLowerCase().equals("where") && requestArray.length > 2) {
                        String[] parameters = new String[requestArray.length - 2];
                        System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                        resultList = select(parameters);
                        break;
                    }
                case "delete":
                    if (requestArray.length == 1) {
                        List<Map<String, Object>> finalResultList = resultList;
                        table.stream().forEach(row -> {
                            Map<String, Object> map = new HashMap<>();
                            map.putAll(row);
                            finalResultList.add(map);
                        });
                        table.clear();
                        return finalResultList;

                    } else if (requestArray[1].toLowerCase().equals("where") && requestArray.length > 2) {
                        String[] parameters = new String[requestArray.length - 2];
                        System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                        resultList = delete(parameters);
                        break;
                    } else {
                        throw new IllegalAccessException("Wrong format");
                    }
                case "insert":
                    if (requestArray.length <= 2) {
                        throw new IllegalAccessException("Wrong format");
                    } else if (requestArray[1].toLowerCase().equals("values")) {
                        String[] parameters = new String[requestArray.length - 2];
                        System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                        resultList = insert(parameters);
                        break;
                    } else {
                        throw new IllegalAccessException("Wrong format");
                    }
                case "update":
                    if (requestArray.length <= 2) {
                        throw new IllegalAccessException("Wrong format command");
                    } else if (requestArray[1].toLowerCase().equals("values")) {
                        String[] parameters = new String[requestArray.length - 2];
                        System.arraycopy(requestArray, 2, parameters, 0, parameters.length);
                        resultList = update(parameters);
                        break;
                    } else {
                        throw new IllegalAccessException("Wrong format");
                    }
                default:
                    throw new IllegalAccessException("Wrong format");
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultList;
    }

    //  ***DELETE***   Удаление элементов из таблицы
    private List<Map<String, Object>> delete(String[] parameters) throws Exception {
        List<Map<String, Object>> resultDeleteList = new ArrayList<>();
        List<Map<String, Object>> deleteSet = new ArrayList<>();
        List<Map<String, Object>> cumulativeSet = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {

            if (parameters[i].toLowerCase().equals("and")) {
                if (i >= parameters.length) {
                    throw new IllegalAccessException("Wrong format: " + parameters[i]);
                }
                List<Map<String, Object>> excludedSet = getCumulativeList(parameters[++i]);

                if (cumulativeSet.size() == 0) {
                    cumulativeSet.addAll(deleteSet);
                    deleteSet.clear();
                }
                for (Map m : cumulativeSet) {
                    if (excludedSet.contains(m)) {
                        deleteSet.add(m);
                    }
                }
                cumulativeSet.clear();
            } else if (parameters[i].toLowerCase().equals("or")) {
                if (i == parameters.length) {
                    throw new IllegalAccessException("Wrong format: " + parameters[i]);
                }
                List<Map<String, Object>> complementarySet = getCumulativeList(parameters[++i]);
                deleteSet.addAll(complementarySet);
                deleteSet.addAll(cumulativeSet);
                cumulativeSet.clear();
            } else {
                cumulativeSet.addAll(getCumulativeList(parameters[i]));
            }
        }
        deleteSet.addAll(cumulativeSet);

        deleteSet.stream().forEach(m -> {
            Map<String, Object> map = new HashMap<>();
            map.putAll(m);
            resultDeleteList.add(map);
            table.remove(m);
        });
        return resultDeleteList;
    }

    //  ***INSERT***       Метод вставки элемента в таблицу
    private List<Map<String, Object>> insert(String[] parameters) throws IllegalAccessException {

        List<Map<String, Object>> listInsert = new ArrayList<>();
        Map<String, Object> map = initialMap();

        for (String field : parameters) {
            String column, operator, stringValue;
            String[] parametersArray = getParametersArray(field);
            column = parametersArray[0];
            operator = parametersArray[1];
            stringValue = parametersArray[2];

            if (!operator.equals("=")) {
                throw new IllegalAccessException("Wrong format: " + column + operator + stringValue);
            } else {
                switch (column) {
                    case "id", "age":
                        try {
                            map.put(column, Long.valueOf(stringValue));
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "lastName":
                        if (stringValue.matches("[a-zA-Z[а-яА-я]]+")) {
                            map.put(column, stringValue);
                            break;
                        } else {
                            throw new IllegalAccessException("Wrong format: " + stringValue);
                        }
                    case "cost":
                        try {
                            map.put(column, Double.valueOf(stringValue));
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "active":
                        map.put(column, Boolean.valueOf(stringValue));
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

    //  ***SELECT***  Получение списка элементов из таблицы
    private List<Map<String, Object>> select(String[] parameters) throws Exception {
        List<Map<String, Object>> selectSet = new ArrayList<>();
        List<Map<String, Object>> cumulativeSet = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].toLowerCase().equals("and")) {
                List<Map<String, Object>> excludedSet = getCumulativeList(parameters[++i]);
                if (cumulativeSet.size() == 0) {
                    cumulativeSet.addAll(selectSet);
                    selectSet.clear();
                }
                for (Map m : cumulativeSet) {
                    if (excludedSet.contains(m)) {
                        selectSet.add(m);
                    }
                }
                cumulativeSet.clear();
            } else if (parameters[i].toLowerCase().equals("or")) {
                List<Map<String, Object>> complementarySet = getCumulativeList(parameters[++i]);
                selectSet.addAll(complementarySet);
                selectSet.addAll(cumulativeSet);
                cumulativeSet.clear();
            } else {
                cumulativeSet.addAll(getCumulativeList(parameters[i]));
            }
        }
        selectSet.addAll(cumulativeSet);
        return selectSet.stream().toList();
    }

    // ***UPDATE***   Метод обновления элементов таблицы
    private List<Map<String, Object>> update(String[] parameters) throws Exception {
        List<Map<String, Object>> updateList = table;
        String[] whereValues;
        String[] newValues = parameters;

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].toLowerCase().equals("where")) {
                whereValues = new String[parameters.length - i - 1];
                newValues = new String[i];
                System.arraycopy(parameters, i + 1, whereValues, 0, whereValues.length);
                updateList = select(whereValues);
                System.arraycopy(parameters, 0, newValues, 0, newValues.length);
            }
        }
        for (String field : newValues) {
            String column, operator, stringValue;
            String[] parametersArray = getParametersArray(field);
            column = parametersArray[0];
            operator = parametersArray[1];
            stringValue = parametersArray[2];

            if (!operator.equals("=")) {
                throw new IllegalAccessException("Wrong format: " + column + operator + stringValue);
            } else {
                switch (column) {
                    case "id", "age":
                        try {
                            updateList.stream().forEach(row -> {
                                row.put(column, Long.valueOf(stringValue));
                            });
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "lastName":
                        if (stringValue.matches("[a-zA-Z[а-яА-я]]+")) {
                            updateList.stream().forEach(row -> {
                                row.put(column, stringValue);
                            });
                            break;
                        } else {
                            throw new IllegalAccessException("Wrong format: " + stringValue);
                        }
                    case "cost":
                        try {
                            updateList.stream().forEach(row -> {
                                row.put(column, Double.valueOf(stringValue));
                            });
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "active":
                        updateList.stream().forEach(row -> {
                            row.put(column, Boolean.valueOf(stringValue));
                        });
                        break;
                    default:
                        throw new IllegalAccessException("Wrong the column name");
                }
            }
        }
        return updateList;
    }

    public List<Map<String, Object>> getCumulativeList(String field) throws Exception {
        List<Map<String, Object>> cumulativeList = new ArrayList<>();
        String column, operator, stringValue;
        String[] parametersArray = getParametersArray(field);
        column = parametersArray[0];
        operator = parametersArray[1];
        stringValue = parametersArray[2];

        switch (operator) {
            case ("="):
                cumulativeList.addAll(table.stream().filter(row -> {
                    if (row.get(column) == null) {
                        return false;
                    }
                    if (row.get(column).toString().equals(stringValue)) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList()));
                break;
            case ("<="):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        } else if ((long) row.get(column) <= Long.parseLong(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        }
                        if ((double) row.get(column) <= Double.parseDouble(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared: " + column + operator + stringValue);
                }
            case (">="):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        } else if ((long) row.get(column) >= Long.parseLong(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        }
                        if ((double) row.get(column) >= Double.parseDouble(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared: " + column + operator + stringValue);
                }
            case ("!="):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        } else if ((long) row.get(column) != Long.parseLong(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        }
                        if ((double) row.get(column) != Double.parseDouble(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                }
                else if (column.equals("active")) {
                    if (stringValue.toLowerCase().equals("true")
                            || stringValue.toLowerCase().equals("false")) {
                        cumulativeList.addAll(table.stream().filter(row -> {
                            if (row.get(column) == null) {
                                return false;
                            }
                            String value = row.get(column).toString().toLowerCase();
                            if(!stringValue.equals(value)){
                                return true;
                            }
                            return false;
                        }).collect(Collectors.toList()));
                        break;
                    }
                }
                else{
                    throw new Exception("The data cannot be compared: " + column + operator + stringValue);
                }
            case ("<"):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        } else if ((long) row.get(column) < Long.parseLong(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        }
                        if ((double) row.get(column) < Double.parseDouble(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared: " + column + operator + stringValue);
                }
            case (">"):
                if (column.equals("id") || column.equals("age")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        } else if ((long) row.get(column) > Long.parseLong(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else if (column.equals("cost")) {
                    cumulativeList.addAll(table.stream().filter(row -> {
                        if (row.get(column) == null) {
                            return false;
                        }
                        if ((double) row.get(column) > Double.parseDouble(stringValue)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                    break;
                } else {
                    throw new Exception("The data cannot be compared: " + column + operator + stringValue);
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
            throw new IllegalAccessException("Wrong format: " + field);
        }
        if (matcherOperator.find()) {
            operator = field.substring(matcherOperator.start() + 1, matcherOperator.end() - 1);
            stringValue = field.substring(matcherOperator.end(), field.length())
                    .replaceAll("'", "")
                    .replaceAll(",", "");
        } else {
            throw new IllegalAccessException("Wrong format: " + field);
        }
        if (column.toLowerCase().equals("active") && !stringValue.toLowerCase().equals("true")
                && !stringValue.toLowerCase().equals("false")) {
            throw new IllegalAccessException("Wrong format: " + field);
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