package com.example.koronatilastot;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ResponseDataHandler {

    public static List<String> passCases(JSONObject data, int days) {
        JSONObject allDates = null, allIndex = null, allValue = null;
        List<String> casesToReturn = new ArrayList<>();

        String todayKey = "";
        String todayIndex = "";

        try {
            allDates = data.getJSONObject("dataset").getJSONObject("dimension").getJSONObject("dateweek2020010120201231").getJSONObject("category").getJSONObject("label");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            allIndex = data.getJSONObject("dataset").getJSONObject("dimension").getJSONObject("dateweek2020010120201231").getJSONObject("category").getJSONObject("index");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            allValue = data.getJSONObject("dataset").getJSONObject("value");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (allDates != null) {
            todayKey = getTodayKey(allDates);
        }

        if (allIndex != null) {
            todayIndex = getTodayIndex(allIndex, todayKey);
        }

        if (allValue != null) {
            casesToReturn = getCases(allValue, days, todayIndex);
        }

        return casesToReturn;
    }

    private static String getTodayKey(JSONObject allDates) {

        Iterator<String> keys = allDates.keys();
        String todayKey  = "";

        while (keys.hasNext()) {
            String key = keys.next();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String todayDate =  formatter.format(new Date());

            try {
                if (allDates.getString(key).equals(todayDate)) {
                    todayKey = key;
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return todayKey;
    }

    private static String  getTodayIndex(JSONObject allIndex, String todayKey) {
        Iterator<String> keys = allIndex.keys();
        String todayIndex = "";

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                if (key.equals(todayKey) && !todayKey.equals("")) {
                    todayIndex = allIndex.getString(key);
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return todayIndex;
    }

    private static List<String> getCases(JSONObject allValue, int days, String todayIndex) {
        List<String> casesToReturn = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            String index = String.valueOf(Integer.parseInt(todayIndex) - i);
            try {
                String cases = allValue.get(index).toString();
                casesToReturn.add(cases);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return casesToReturn;
    }

    public static int casesThisWeekMonth(JSONObject data, int days) {
        List<String> cases = passCases(data, days);
        int thisMonth = 0;

        for (int i = 0; i < cases.size();  i++) {
            thisMonth += Integer.parseInt(cases.get(i));
        }

        return thisMonth;
    }

    public static List<String> allByTowns(JSONObject data) {
        JSONObject allTowns = null;
        List<String> townsList = new ArrayList<>();

        try {
            allTowns = data.getJSONObject("dataset").getJSONObject("dimension").getJSONObject("hcdmunicipality2020").getJSONObject("category").getJSONObject("label");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (allTowns != null) {
            Iterator<String> keys = allTowns.keys();

            while  (keys.hasNext()) {
                String key = keys.next();
                try {
                    townsList.add(allTowns.getString(key));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return townsList;
    }

    public static String casesByTown(JSONObject data, String hometown) {
        String cases = "", townKey = "", townIndex = "";
        JSONObject allTowns = null, allIndex = null, allValue = null;

        try {
            allTowns = data.getJSONObject("dataset").getJSONObject("dimension").getJSONObject("hcdmunicipality2020").getJSONObject("category").getJSONObject("label");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            allIndex = data.getJSONObject("dataset").getJSONObject("dimension").getJSONObject("hcdmunicipality2020").getJSONObject("category").getJSONObject("index");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            allValue = data.getJSONObject("dataset").getJSONObject("value");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (allTowns != null) {
            townKey = getTownKey(allTowns, hometown);
        }

        if (allIndex != null) {
            townIndex = getTownIndex(allIndex, townKey);
        }

        if (allValue != null) {
            cases = getCasesByTown(allValue, townIndex);
        }

        return cases;
    }

    private static String getTownKey(JSONObject allTowns, String hometown) {
        Iterator<String> keys = allTowns.keys();
        String selectedTownKey  = "";

        while (keys.hasNext()) {
            String key = keys.next();

            try {
                if (allTowns.getString(key).equals(hometown)) {
                    selectedTownKey = key;
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return selectedTownKey;
    }

    private static String getTownIndex(JSONObject allIndex, String townKey) {
        Iterator<String> keys = allIndex.keys();
        String townIndex = "";

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                if (key.equals(townKey) && !townKey.equals("")) {
                    townIndex = allIndex.getString(key);
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return townIndex;
    }

    private static String getCasesByTown(JSONObject allValues, String townIndex) {
        Iterator<String> keys = allValues.keys();
        String townValue = "";

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                if (key.equals(townIndex) && !townIndex.equals("")) {
                    townValue = allValues.getString(key);
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return townValue;
    }

    public static int casesWeekChange(JSONObject data) {

        int thisYear, thisWeek, casesLastWeek, casesWeekBeforeLast, change = -101;


        Calendar cal = Calendar.getInstance();

        thisYear = cal.get(Calendar.YEAR);
        thisWeek = cal.get(Calendar.WEEK_OF_YEAR);

        casesLastWeek = getCasesByYearWeek(data, thisYear, thisWeek - 1);
        casesWeekBeforeLast = getCasesByYearWeek(data, thisYear, thisWeek - 2);

        if (casesLastWeek >= 0 && casesWeekBeforeLast >= 0) {
            change = (int) ((double) casesLastWeek / (double) casesWeekBeforeLast * 100) - 100;
        }

        return change;
    }

    private static int getCasesByYearWeek(JSONObject data, int year, int weekNbr) {
        JSONObject allWeeks = null, allIndex = null, allValue = null;

        String weekKey = "", weekIndex = "";
        int cases = -1;

        try {
            allWeeks = data.getJSONObject("dataset").getJSONObject("dimension").getJSONObject("dateweek2020010120201231").getJSONObject("category").getJSONObject("label");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            allIndex = data.getJSONObject("dataset").getJSONObject("dimension").getJSONObject("dateweek2020010120201231").getJSONObject("category").getJSONObject("index");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            allValue = data.getJSONObject("dataset").getJSONObject("value");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (allWeeks != null) {
            weekKey = getPassedWeekKey(allWeeks,year,weekNbr);
        }

        if (allIndex != null) {
            weekIndex = getPassedWeekIndex(allIndex, weekKey);
        }

        if (allValue != null) {
            cases = Integer.parseInt(getPassedWeekValue(allValue, weekIndex));
        }

        return cases;
    }

    private static String getPassedWeekKey(JSONObject allWeeks, int year, int weekNbr) {

        String weekSearch = "Vuosi " + year + " Viikko " +  weekNbr;

        Iterator<String> keys = allWeeks.keys();
        String weekKey  = "";

        while (keys.hasNext()) {
            String key = keys.next();

            try {
                if (allWeeks.getString(key).equals(weekSearch)) {
                    weekKey = key;
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return weekKey;

    }

    private static String getPassedWeekIndex(JSONObject allIndex, String weekKey) {
        Iterator<String> keys = allIndex.keys();
        String weekIndex = "";

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                if (key.equals(weekKey) && !weekKey.equals("")) {
                    weekIndex = allIndex.getString(key);
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return weekIndex;

    }

    private static String getPassedWeekValue(JSONObject allValue, String weekIndex) {
        Iterator<String> keys = allValue.keys();
        String weekValue = "";

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                if (key.equals(weekIndex) && !weekIndex.equals("")) {
                    weekValue = allValue.getString(key);
                    break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return weekValue;
    }
}
