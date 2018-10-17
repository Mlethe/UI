package com.mlethe.widget.seatTable;

import android.text.TextUtils;

/**
 * 座位
 * Created by Mlethe on 2018/10/17.
 */
public class Seat {

    /**
     * 座位不可用、座位已售、座位已选中、座位可选
     */
    public static final int SEAT_TYPE_NOT_AVAILABLE = 0, SEAT_TYPE_SOLD = 1, SEAT_TYPE_SELECTED = 2, SEAT_TYPE_AVAILABLE = 3;
    /**
     * 行
     */
    private String row;
    /**
     * 列
     */
    private String column;
    /**
     * 状态：0->不可用、3->可选、1->已订、2->选中
     */
    private int state;

    public Seat(int state) {
        this.state = state;
    }

    public Seat(String row, String column, int state) {
        this.row = row;
        this.column = column;
        this.state = state;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isEmpty(){
        return state == SEAT_TYPE_NOT_AVAILABLE && (TextUtils.isEmpty(row) || TextUtils.isEmpty(column));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Seat){
            Seat seat = (Seat) obj;
            if (!TextUtils.isEmpty(row) && !TextUtils.isEmpty(column) && row == seat.row && column == seat.column) {
                return true;
            }
            return false;
        } else {
            return super.equals(obj);
        }
    }
}
