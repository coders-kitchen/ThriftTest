package com.coderskitchen.thriftTest;

import com.coderskitchen.thriftTest.runner.CombinedStructurePerformanceTest;
import com.coderskitchen.thriftTest.runner.CombinedStructureWithConcreteObjectPerformanceTest;
import com.coderskitchen.thriftTest.runner.PerformanceTestRun;
import com.coderskitchen.thriftTest.runner.SimpleStructurePerformanceTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    public static final int NUMBER_OF_ELEMENTS_TO_SERIALIZE = 1_000_000;
    public static final int NUMBER_OF_MAP_ELEMENTS = 10;

    public static final int ROUNDS = 100;
    public static final List<PerformanceTestRun> PERFORMANCE_TEST_RUNS = new ArrayList<>();

    public static final StatisticsCollectorImpl STATISTICS_COLLECTOR = new StatisticsCollectorImpl();

    public static void main(String[] args) {
        registerRunner();
        registerStatisticsCollector();
        setWarmUpRoundsForTestRuns();
        runTests();
        printStatistics();
    }


    private static void registerRunner() {
        PERFORMANCE_TEST_RUNS.add(new CombinedStructureWithConcreteObjectPerformanceTest(NUMBER_OF_ELEMENTS_TO_SERIALIZE));
        PERFORMANCE_TEST_RUNS.add(new CombinedStructurePerformanceTest(NUMBER_OF_ELEMENTS_TO_SERIALIZE, NUMBER_OF_MAP_ELEMENTS));
        PERFORMANCE_TEST_RUNS.add(new SimpleStructurePerformanceTest(NUMBER_OF_ELEMENTS_TO_SERIALIZE, NUMBER_OF_MAP_ELEMENTS));
    }

    private static void registerStatisticsCollector() {
        for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
            performanceTestRun.setStatisticsCollector(STATISTICS_COLLECTOR);
        }
    }

    private static void setWarmUpRoundsForTestRuns() {
        int warmUpRoundsForTestRun = NUMBER_OF_ELEMENTS_TO_SERIALIZE/10;
        for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
            performanceTestRun.setWarmUpRoundsForTestRun(warmUpRoundsForTestRun);
        }
    }

    private static void runTests() {
        STATISTICS_COLLECTOR.acceptEvents();
        for (int round = 1; round <= ROUNDS; round++) {
            System.out.printf("round %d of %d%s%n", round, ROUNDS, " measurement");
            Collections.shuffle(PERFORMANCE_TEST_RUNS);
            for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
                System.out.println("\t" + performanceTestRun.getClass().getSimpleName());
                performanceTestRun.runTest();
            }
        }
    }

    private static void printStatistics() {
        STATISTICS_COLLECTOR.printStatistics(TestEvent.SERIALIZE, TestEvent.DESERIALIZE);
    }
}
