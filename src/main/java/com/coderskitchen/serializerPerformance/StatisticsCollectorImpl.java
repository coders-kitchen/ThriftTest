package com.coderskitchen.serializerPerformance;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Created by Peter on 25.01.2015.
 */
public class StatisticsCollectorImpl implements StatisticsCollector {
    public static final String STATISTIC_LINE = "%50s,%25s, %7s,%15s,%15s,%15s%n";
    public static final int PRECISION = 12;
    private final Map<String, Map<TestEvent, List<Double>>> statistics = new HashMap<>();

    private final List<String> runners = new ArrayList<>();

    private boolean acceptEvents = false;


    public void addDuration(Class runner, TestEvent testEvent, Instant start, Instant end) {
        if (!acceptEvents) {
            return;
        }
        String name = runner.getSimpleName();
        addRunnerToListIfMissing(name);
        statistics.computeIfAbsent(name, k -> new HashMap<>());
        statistics.get(name).computeIfAbsent(testEvent, k -> new ArrayList<>());
        statistics.get(name).get(testEvent).add(durationAsDouble(start, end));
    }

    private void addRunnerToListIfMissing(String name) {
        if (!this.runners.contains(name)) {
            this.runners.add(name);
        }
    }

    private Double durationAsDouble(Instant start, Instant end) {
        Duration duration = Duration.between(start, end);
        return duration.getSeconds() + (double) duration.getNano() / 1000000000;
    }

    public void acceptEvents() {
        acceptEvents = true;
    }

    public void printStatistics(TestEvent... testEvents) {

        System.out.printf(STATISTIC_LINE, "Class", "Event", "#Events", "Min", "Average", "Max");
        for (TestEvent testEvent : testEvents) {
            for (String runner : runners) {
                Map<TestEvent, List<Double>> testEventListMap = statistics.get(runner);
                printStatisticForEvent(runner, testEventListMap.get(testEvent), testEvent);
            }
        }
    }

    public void printStatistics() {
        printStatistics(TestEvent.values());
    }

    private void printStatisticForEvent(String runner, List<Double> eventDurations, TestEvent event) {
        if (eventDurations == null || eventDurations.isEmpty()) {
            System.out.printf(STATISTIC_LINE, runner, event, 0, "NA", "NA", "NA");
            return;
        }
        DoubleSummaryStatistics summaryStatistics = eventDurations.stream().mapToDouble(duration -> duration).summaryStatistics();

        System.out.printf(STATISTIC_LINE, runner, event, summaryStatistics.getCount(), round(summaryStatistics.getMin(), PRECISION), round(summaryStatistics.getAverage(), PRECISION), round(summaryStatistics.getMax(), PRECISION));

    }

    public String round(double value, int places) {
        return new BigDecimal(value, new MathContext(places, RoundingMode.HALF_UP)).setScale(places, RoundingMode.HALF_UP).toString();
    }
}
