package com.example.berkcirisci.alphafitness;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import javax.xml.datatype.Duration;

public class UserProfile extends AppCompatActivity implements OnItemSelectedListener{

    private EditText height;
    private EditText weight;
    private EditText name;
    private Object databaseHelper;
    private RuntimeExceptionDao<User, Integer> userDao;
    private User user;
    private TextView weeklyDistance;
    private TextView allTimeDistance;
    private TextView weeklyDuration;
    private TextView allTimeDuration;
    private TextView weeklyCalories;
    private TextView allTimeCalories;
    private TextView weeklyCounts;
    private TextView allTimeCounts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        createUser();
        initializeWidgets();
        fillWidgets();
        createListeners();
    }

    private void createListeners() {
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                user.setName(charSequence.toString());
                userDao.update(user);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                user.setWeight(Integer.parseInt(charSequence.toString()));
                userDao.update(user);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                user.setHeight(Integer.parseInt(charSequence.toString()));
                userDao.update(user);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void fillWidgets() {
        name.setText(user.getName());
        weight.setText(Integer.toString(user.getWeight()));
        height.setText(Integer.toString(user.getHeight()));
        long oneWeekAgo = System.currentTimeMillis() - 7*24*60*60*1000;
        Workout weeklySummary = Workout.getSummary(getHelper(),oneWeekAgo);
        Workout allTimeSummary = Workout.getSummary(getHelper(),-1);
        weeklyDistance.setText(String.format("%.3f",weeklySummary.distance/1000)+ " km");
        allTimeDistance.setText(String.format("%.3f",allTimeSummary.distance/1000)+ " km");
        weeklyDuration.setText(String.format("%d",weeklySummary.duration/1000)+ " sec");
        allTimeDuration.setText(String.format("%d",allTimeSummary.duration/1000)+ " sec");
        weeklyCounts.setText(String.format("%d",weeklySummary.id)+ " time(s)");
        allTimeCounts.setText(String.format("%d",allTimeSummary.id)+ " time(s)");
        weeklyCalories.setText(String.format("%.2f",user.distanceToCalorie(weeklySummary.distance))+ " Cal");
        allTimeCalories.setText(String.format("%.2f",user.distanceToCalorie(allTimeSummary.distance))+ " Cal");
    }

    private void createUser() {
        userDao = getHelper().getUserDao();
        try {
            user = userDao.queryBuilder().queryForFirst();
            if(user == null){
                user = new User(72,180,"Berk Cirisci");
                userDao.create(user);
            }
        } catch (SQLException e) {
            user = null;
            e.printStackTrace();
        }
    }

    private DatabaseHelper getHelper() {
        return OpenHelperManager.getHelper(this,DatabaseHelper.class);
    }

    private void initializeWidgets() {
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        name = (EditText) findViewById(R.id.userName);
        weeklyDistance = (TextView) findViewById(R.id.weeklyDistance);
        allTimeDistance = (TextView) findViewById(R.id.allTimeDistance);
        weeklyDuration = (TextView) findViewById(R.id.weeklyDuration);
        allTimeDuration = (TextView) findViewById(R.id.allTimeDuration);
        weeklyCounts = (TextView) findViewById(R.id.weeklyCounts);
        allTimeCounts = (TextView) findViewById(R.id.allTimeCounts);
        weeklyCalories = (TextView) findViewById(R.id.weeklyCalories);
        allTimeCalories = (TextView) findViewById(R.id.allTimeCalories);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}