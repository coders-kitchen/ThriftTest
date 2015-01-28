package com.coderskitchen.thriftTest;

import java.util.*;

/**
 * Default statistics collector.
 *
 * Collects all the transferred start and end dates per event and runner and calculates statistics.
 *
 * The statistics are pre runner and event and provide information about
 * - number of measurement points
 * - minimal duration
 * - maximal duration
 * - average duration
 * - standard deviation
 *
 * Created by Peter on 25.01.2015.
 */
public class StatisticsCollectorImpl implements StatisticsCollector, StatisticsPrinter {
    public static final String STATISTIC_LINE = "%50s,%25s, %7s,%15s,%15s,%15s,%15s%n";
    private final Map<String, Map<TestEvent, List<Long>>> statistics = new HashMap<>();

    private final List<String> runners = new ArrayList<>();



    public void addDuration(Class runner, TestEvent testEvent, long start, long end) {
        String name = runner.getSimpleName();
        addRunnerToListIfMissing(name);
        statistics.computeIfAbsent(name, k -> new HashMap<>());
        statistics.get(name).computeIfAbsent(testEvent, k -> new ArrayList<>());
        statistics.get(name).get(testEvent).add(end-start);
    }

    private void addRunnerToListIfMissing(String name) {
        if (!this.runners.contains(name)) {
            this.runners.add(name);
        }
    }

    public void printStatistics(TestEvent... testEvents) {

        System.out.printf(STATISTIC_LINE, "Class", "Event", "#Events", "Min (Nanos)", "Average (Nanos)", "Max (Nanos)", "Std.Dev");
        for (TestEvent testEvent : testEvents) {
            for (String runner : runners) {
                Map<TestEvent, List<Long>> testEventListMap = statistics.get(runner);
                printStatisticForEvent(runner, testEventListMap.get(testEvent), testEvent);
            }
        }
    }

    public void printStatistics() {
        printStatistics(TestEvent.values());
    }

    private void printStatisticForEvent(String runner, List<Long> eventDurations, TestEvent event) {
        if (eventDurations == null || eventDurations.isEmpty()) {
            System.out.printf(STATISTIC_LINE, runner, event, 0, "NA", "NA", "NA", "NA");
            return;
        }
        final LongSummaryStatistics summaryStatistics = eventDurations.stream().mapToLong(duration -> duration).summaryStatistics();


        Double standardDeviation = calculateStandardDeviation(eventDurations, summaryStatistics);

        System.out.printf(STATISTIC_LINE, runner, event, summaryStatistics.getCount(), summaryStatistics.getMin(), Math.round(summaryStatistics.getAverage()), summaryStatistics.getMax(),standardDeviation);

    }

    private Double calculateStandardDeviation(List<Long> eventDurations, LongSummaryStatistics summaryStatistics) {
        final long divisor = summaryStatistics.getCount() - 1;
        Double stdDeviation = 0.0;
        if(divisor > 0) {
            Optional<Double> stdDevSquared = eventDurations.stream().map((measurementPoint) -> Math.pow(measurementPoint - summaryStatistics.getAverage(), 2)).reduce(Double::sum).map(sum -> sum / divisor);
            stdDeviation = Math.sqrt(stdDevSquared.get());
        }
        return stdDeviation;
    }
}
