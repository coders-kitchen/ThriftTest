package com.coderskitchen.thriftTest;

/**
 * Created by Peter on 25.01.2015.
 */
public interface StatisticsCollector {
    void addDuration(Class runner, TestEvent testEvent, long start, long end);
}
