package com.mlethe.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mlethe.ui.R;
import com.mlethe.widget.seatTable.Seat;
import com.mlethe.widget.seatTable.SeatTableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeatTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_table);
        SeatTableView seatTableView = findViewById(R.id.seat_table);
        Random random = new Random();
        List<List<Seat>> lists = new ArrayList<>();
        int count = 0;
        for (int j = 0; j < 10; j++) {
            List<Seat> list = new ArrayList<>();
            if (j % 3 != 1) {
                count ++;
            }
            int index = 0;
            for (int i = 0; i < 16; i++) {
                int nextInt = random.nextInt(4);
                if (j % 3 == 1) {
                    list.add(new Seat(0));
                } else if (nextInt == 0) {
                    list.add(new Seat(0));
                } else {
                    index ++;
                    list.add(new Seat(count + "", index + "", nextInt));
                }
            }
            lists.add(list);
        }
        seatTableView.setScreenText("3号厅 荧幕").setSeat(lists);
    }
}
