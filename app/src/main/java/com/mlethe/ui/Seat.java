package com.mlethe.ui;

public class Seat {
    /**
     * 行
     */
    private int row;
    /**
     * 列
     */
    private int column;
    /**
     * 状态：0->无、空、已订、选中
     */
    private int state;

    public Seat(int state) {
        this.state = state;
    }

    public Seat(int row, int column, int state) {
        this.row = row;
        this.column = column;
        this.state = state;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Seat){
            Seat seat = (Seat) obj;
            if (row > 0 && column > 0 && row == seat.row && column == seat.column) {
                return true;
            }
            return false;
        } else {
            return super.equals(obj);
        }
    }
}
