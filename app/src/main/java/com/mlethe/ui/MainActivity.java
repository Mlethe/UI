package com.mlethe.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeatTable seatTable = findViewById(R.id.seat_table);
        List<List<Seat>> lists = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            List<Seat> list = new ArrayList<>();
            int index = 0;
            for (int i = 0; i < 16; i++) {
                if (i % 5 == 2) {
                    list.add(new Seat(0, 0, 0));
                } else {
                    index ++;
                    list.add(new Seat((j + 1), index, 1));
                }
            }
            lists.add(list);
        }
        seatTable.setSeat(lists).redraw();
    }
}
