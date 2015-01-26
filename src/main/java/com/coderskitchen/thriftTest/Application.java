package com.coderskitchen.thriftTest;

import com.coderskitchen.thriftTest.runner.CombinedStructurePerformanceTest;
import com.coderskitchen.thriftTest.runner.CombinedStructureWithConcreteObjectPerformanceTest;
import com.coderskitchen.thriftTest.runner.PerformanceTestRun;
import com.coderskitchen.thriftTest.runner.SimpleStructurePerformanceTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    private class Defaults {
        public static final int ELEMENTS_PER_ROUND = 200_000;
        public static final int MAP_ELEMENTS = 10;

        public static final int ROUNDS = 5;

    }
    public static final List<PerformanceTestRun> PERFORMANCE_TEST_RUNS = new ArrayList<>();

    public static final StatisticsCollectorImpl STATISTICS_COLLECTOR = new StatisticsCollectorImpl();

    public static void main(String[] args) {

        int elementsPerRound = Defaults.ELEMENTS_PER_ROUND;
        int mapElements = Defaults.MAP_ELEMENTS;
        int rounds = Defaults.ROUNDS;

        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            String arg = args[i];
            switch (arg) {
                case "--mapElements":
                    i++;
                    if(i < argsLength) {
                        mapElements = Integer.parseInt(args[i]);
                    }
                    break;
                case "--rounds":
                    i++;
                    if(i < argsLength) {
                        rounds = Integer.parseInt(args[i]);
                    }
                    break;
                case "--elementsPerRound":
                    i++;
                    if(i < argsLength) {
                        elementsPerRound = Integer.parseInt(args[i]);
                    }
                    break;
            }
        }

        registerRunner(elementsPerRound, mapElements);
        registerStatisticsCollector();
        setWarmUpRoundsForTestRuns(elementsPerRound);
        runTests(rounds);
        printStatistics();
    }


    private static void registerRunner(int elementsPerRound, int numberOfMapElements) {
        PERFORMANCE_TEST_RUNS.add(new CombinedStructureWithConcreteObjectPerformanceTest(elementsPerRound));
        PERFORMANCE_TEST_RUNS.add(new CombinedStructurePerformanceTest(elementsPerRound, numberOfMapElements));
        PERFORMANCE_TEST_RUNS.add(new SimpleStructurePerformanceTest(elementsPerRound, numberOfMapElements));
    }

    private static void registerStatisticsCollector() {
        for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
            performanceTestRun.setStatisticsCollector(STATISTICS_COLLECTOR);
        }
    }

    private static void setWarmUpRoundsForTestRuns(int elementsPerRound) {
        int warmUpRoundsForTestRun = elementsPerRound /10;
        for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
            performanceTestRun.setWarmUpRoundsForTestRun(warmUpRoundsForTestRun);
        }
    }

    private static void runTests(int rounds) {
        STATISTICS_COLLECTOR.acceptEvents();
        for (int round = 1; round <= rounds; round++) {
            System.out.printf("round %d of %d%s%n", round, rounds, " measurement");
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
