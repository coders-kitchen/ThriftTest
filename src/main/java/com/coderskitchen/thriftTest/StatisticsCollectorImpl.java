package com.coderskitchen.thriftTest;

import java.util.*;

/**
 * Created by Peter on 25.01.2015.
 */
public class StatisticsCollectorImpl implements StatisticsCollector {
    public static final String STATISTIC_LINE = "%50s,%25s, %7s,%15s,%15s,%15s%n";
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

        System.out.printf(STATISTIC_LINE, "Class", "Event", "#Events", "Min (Nanos)", "Average (Nanos)", "Max (Nanos)");
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
            System.out.printf(STATISTIC_LINE, runner, event, 0, "NA", "NA", "NA");
            return;
        }
        DoubleSummaryStatistics summaryStatistics = eventDurations.stream().mapToDouble(duration -> duration).summaryStatistics();

        System.out.printf(STATISTIC_LINE, runner, event, summaryStatistics.getCount(), summaryStatistics.getMin(), Math.round(summaryStatistics.getAverage()), summaryStatistics.getMax());

    }
}
