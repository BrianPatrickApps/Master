package com.example.windows10.work_weather;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnPxWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class WeatherRoom extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Database db = new Database(this);

        ArrayList<String[]> theArray = db.collectFormattedUsers();
        String[][] array = new String[theArray.size()][];
        for (int i = 0; i < theArray.size(); i++) {
            array[i] = theArray.get(i);
        }
        String[] headerData = {"Input","Mean","Date","Shift"};
        //noinspection unchecked
        TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.tableView);
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, array));
        tableView.setHeaderBackground(R.drawable.side_nav_bar);
        tableView.setBackgroundResource(R.drawable.side_nav_bar_reverse);
        TableColumnPxWidthModel columnModel = new TableColumnPxWidthModel(4, 200);
        columnModel.setColumnWidth(3,450);
        tableView.setColumnModel(columnModel);
        tableView.setSwipeToRefreshEnabled(false);
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this,headerData);
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

    }

}
