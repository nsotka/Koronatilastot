package com.example.koronatilastot;

import java.net.URL;

public class RequestUrls {

    static String baseUrl = "https://sampo.thl.fi/pivot/prod/fi/epirapo/covid19case/fact_epirapo_covid19case.json";

    public static URL allByDate() {
        URL byDate = null;

        try {
            byDate = new URL(baseUrl + "?column=dateweek2020010120201231-443702L");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return byDate;
    }

    public static URL allByWeek() {
        URL byWeek = null;
        try {
            byWeek = new URL(baseUrl + "?column=dateweek2020010120201231-443686");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return byWeek;
    }

    public static URL allByTown() {
        URL byTown = null;

        try {
            byTown = new URL(baseUrl + "?column=hcdmunicipality2020-445268L");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return byTown;
    }

}
