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

        ArrayList<String[]> collectFormattedUsers = db.collectFormattedUsers();
        String[][] usersArray = new String[collectFormattedUsers.size()][];
        for (int i = 0; i < collectFormattedUsers.size(); i++) {
            usersArray[i] = collectFormattedUsers.get(i);
        }
        String[] headerData = { "Id","Input","Median","Date","Shift"};
        //noinspection unchecked
        TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.tableView);
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, usersArray));
        tableView.setHeaderBackground(R.drawable.side_nav_bar);
        tableView.setBackgroundResource(R.drawable.side_nav_bar_reverse);
        TableColumnPxWidthModel columnModel = new TableColumnPxWidthModel(5, 200);
        columnModel.setColumnWidth(3,450);
        tableView.setColumnModel(columnModel);
        tableView.setSwipeToRefreshEnabled( true );
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this,headerData);
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
    }
}
