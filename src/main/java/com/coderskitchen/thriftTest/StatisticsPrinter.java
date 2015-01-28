package com.coderskitchen.thriftTest;

/**
 * Provides methods to print out statistics.
 *
 *
 * Created by Peter on 28.01.2015.
 */
public interface StatisticsPrinter {
    /**
     * Prints out statistics for all events
     */
    void printStatistics();

    /**
     * Prints out statistics for the given events
     *
     * @param testEvents - the events to be considered
     */
    public void printStatistics(TestEvent... testEvents);
}
