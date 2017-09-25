package com.example.windows10.work_weather;


class Counter {
    private int count = 0;

    Counter(){
    }
    void setCount(){
        count++;
    }

    void resetCount(){
        count = 0;
    }

    int getCount(){
        return count;
    }

    void removeCount(){
        count--;
    }
}
