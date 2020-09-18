package com.example.koronatilastot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextInputLayout hometownLayout;
    AutoCompleteTextView homeTownEditText;
    Button hometownActionButton;
    MaterialButtonToggleGroup toggleButton;
    Button[] dateButtons = new Button[3];
    TextView casesOfSelectedDay, casesOfTheWeek, casesOfTheMonth, casesOfTheYear, casesOfHometown,
            metricPercent, metricTitle;
    LinearLayout metricGreen, metricGreenWhite, metricRed, metricRedWhite;

    SimpleDateFormat formatter;

    JSONObject casesByDateData, casesByTownData, casesByWeekData;
    List<String> passedCases = new ArrayList<>();
    Calendar cal;

    Boolean hometownEdit = false;
    String hometownName;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locale locale = new Locale("fi", "FI");
        formatter = new SimpleDateFormat("dd.M.yyyy");
        cal = Calendar.getInstance(locale);

        getAllByDate();
        getAllByTown();
        getAllByWeek();
        passedDays();

        hometownLayout = findViewById(R.id.hometown_layout);
        homeTownEditText = findViewById(R.id.hometown_editView);
        hometownActionButton = findViewById(R.id.hometown_action);
        casesOfSelectedDay = findViewById(R.id.cases_selected_day);
        casesOfTheWeek = findViewById(R.id.card1_week_cases);
        casesOfTheMonth = findViewById(R.id.card2_month_cases);
        casesOfTheYear = findViewById(R.id.card3_year_cases);
        casesOfHometown = findViewById(R.id.card4_home_case);
        toggleButton = findViewById(R.id.toggle_button);
        metricGreen = findViewById(R.id.metric_green);
        metricGreenWhite = findViewById(R.id.metric_green_white);
        metricRed = findViewById(R.id.metric_red);
        metricRedWhite = findViewById(R.id.metric_red_white);
        metricPercent = findViewById(R.id.metric_percent);
        metricTitle = findViewById(R.id.metric_title);

        initDateButtons(dateButtons.length);
        initCaseCards();
        initCityAdapter();
        initHometown();
        updateWeekChange();

        hometownActionButton.setOnClickListener(townActionOnClickListener);


    }

    private View.OnClickListener townActionOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (hometownEdit) {
                if (saveHometown()) {
                    Drawable testi = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_edit_24_white,null);
                    hometownActionButton.setCompoundDrawablesWithIntrinsicBounds(testi,null,null,null);

                    hometownLayout.setEnabled(false);
                    hometownEdit = false;
                }

            }
            else {
                Drawable testi2 = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_save_24_white,null);
                hometownActionButton.setCompoundDrawablesWithIntrinsicBounds(testi2,null,null,null);

                hometownLayout.setEnabled(true);
                hometownLayout.requestFocus();
                homeTownEditText.setText("");

                hometownEdit = true;
            }
        }
    };

    private void getAllByDate() {

        ApiRequest apiRequest = new ApiRequest();

        try {
            casesByDateData = new JSONObject(apiRequest.execute(RequestUrls.allByDate()).get());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void passedDays() {
        passedCases = ResponseDataHandler.passCases(casesByDateData, 4);
    }

    private void initDateButtons(int buttonCount) {
        Calendar buttonDate = (Calendar) cal.clone();

        for (int i = 0; i < buttonCount;  i++) {
            String button = "day" + (i+1);
            int resID = getResources().getIdentifier(button, "id", getPackageName());

            dateButtons[i] = findViewById(resID);
        }

        for (int i = 0; i < buttonCount; i++) {
            buttonDate.add(Calendar.DATE, -1);
            dateButtons[i].setText(formatter.format(buttonDate.getTime()));
        }

        casesOfSelectedDay.setText(passedCases.get(1));

        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.day1:
                            casesOfSelectedDay.setText(passedCases.get(1));
                            break;
                        case R.id.day2:
                            casesOfSelectedDay.setText(passedCases.get(2));
                            break;
                        case R.id.day3:
                            casesOfSelectedDay.setText(passedCases.get(3));
                            break;
                    }

                }
            }
        });

    }

    private void initCaseCards() {
        int daysThisMonth = cal.get(Calendar.DAY_OF_MONTH);
        int[] days = {7, 1, 2, 3, 4, 5, 6};
        int daysThisWeek = days[cal.get(Calendar.DAY_OF_WEEK) - 1];
        int daysThisYear = cal.get(Calendar.DAY_OF_YEAR);

        String thisWeek = String.valueOf(ResponseDataHandler.casesThisWeekMonth(casesByDateData, daysThisWeek));
        String thisMonth = String.valueOf(ResponseDataHandler.casesThisWeekMonth(casesByDateData, daysThisMonth));
        String thisYear = String.valueOf(ResponseDataHandler.casesThisWeekMonth(casesByDateData, daysThisYear));

        casesOfTheWeek.setText(thisWeek);
        casesOfTheMonth.setText(thisMonth);
        casesOfTheYear.setText(thisYear);
    }

    private void initCityAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getTowns());
        homeTownEditText.setAdapter(adapter);

    }

    private void getAllByTown() {

        ApiRequest apiRequest = new ApiRequest();

        try {
            casesByTownData = new JSONObject(apiRequest.execute(RequestUrls.allByTown()).get());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getTowns() {
        return ResponseDataHandler.allByTowns(casesByTownData);
    }

    private void initHometown() {
        sharedPreferences = getSharedPreferences("CovSettings", MODE_PRIVATE);

        sharedPreferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        hometownName =  sharedPreferences.getString("hometown",  "");

        homeTownEditText.setText(hometownName);

        casesOfHometown.setText(ResponseDataHandler.casesByTown(casesByTownData, hometownName));

    }

    private boolean saveHometown()  {
        String userInput = homeTownEditText.getText().toString();

        if (!userInput.isEmpty()) {
            userInput = userInput.substring(0, 1).toUpperCase() + userInput.substring(1);
        }


        SharedPreferences.Editor editor =  sharedPreferences.edit();

        if (getTowns().contains(userInput)) {
            homeTownEditText.setText(userInput);
            editor.putString("hometown", userInput);

            editor.apply();

            return true;
        }
        else if (userInput.isEmpty()) {
            editor.putString("hometown", userInput);

            editor.apply();

            return true;
        }
        else {
            homeTownEditText.setError("Tarkista antamasi kaupunki");
            return false;
        }

    }

    private void getAllByWeek() {

        ApiRequest apiRequest = new ApiRequest();

        try {
            casesByWeekData = new JSONObject(apiRequest.execute(RequestUrls.allByWeek()).get());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWeekChange() {
        int casesChange = ResponseDataHandler.casesWeekChange(casesByWeekData);
        float changeToFloat = (float) casesChange / 100;

        if (casesChange != -101) {
            if (casesChange > 0) {
                metricTitle.setText("Tartuntamäärien kasvu viime viikolla");
                metricPercent.setText(String.valueOf("+" + casesChange) + "%");
                metricGreen.setLayoutParams(setWeight(metricGreen,changeToFloat));
                metricGreenWhite.setLayoutParams(setWeight(metricGreenWhite,1 - changeToFloat));
                metricRed.setLayoutParams(setWeight(metricRed,0));
                metricRedWhite.setLayoutParams(setWeight(metricRedWhite,1));
            }
            else if (casesChange < 0) {
                metricTitle.setText("Tartuntamäärien lasku viime viikolla");
                metricPercent.setText(String.valueOf(casesChange) + "%");
                metricGreen.setLayoutParams(setWeight(metricGreen,0));
                metricGreenWhite.setLayoutParams(setWeight(metricGreenWhite,1));
                metricRed.setLayoutParams(setWeight(metricRed,changeToFloat * -1));
                metricRedWhite.setLayoutParams(setWeight(metricRedWhite,1 - (changeToFloat * -1)));
            }
            else {
                metricTitle.setText("Tartuntamäärien kasvu viime viikolla");
                metricPercent.setText(String.valueOf("+" + casesChange) + "%");
            }
        }
    }

    private LinearLayout.LayoutParams setWeight(View v, float change) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
        params.weight = change;

        return params;
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String selectedTown = sharedPreferences.getString(key, "");

            casesOfHometown.setText(ResponseDataHandler.casesByTown(casesByTownData, selectedTown));
        }
    };
}