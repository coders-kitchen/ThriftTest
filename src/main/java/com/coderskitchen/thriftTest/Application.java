package com.coderskitchen.thriftTest;

import com.coderskitchen.thriftTest.runner.CombinedStructurePerformanceTest;
import com.coderskitchen.thriftTest.runner.CombinedStructureWithConcreteObjectPerformanceTest;
import com.coderskitchen.thriftTest.runner.PerformanceTestRun;
import com.coderskitchen.thriftTest.runner.SimpleStructurePerformanceTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    private static boolean verbose = Defaults.VERBOSE;

    private class Defaults {
        public static final int ELEMENTS_PER_ROUND = 200_000;
        public static final int MAP_ELEMENTS = 10;
        public static final int WARM_UP_PERCENTILE = 10;
        public static final int ROUNDS = 5;
        public static final boolean VERBOSE = false;
        public static final String RUNNER_TO_EXECUTE = "all";

    }
    public static final List<PerformanceTestRun> PERFORMANCE_TEST_RUNS = new ArrayList<>();

    public static final StatisticsCollectorImpl STATISTICS_COLLECTOR = new StatisticsCollectorImpl();

    public static void main(String[] args) {

        int elementsPerRound = Defaults.ELEMENTS_PER_ROUND;
        int mapElements = Defaults.MAP_ELEMENTS;
        int rounds = Defaults.ROUNDS;
        int warmUpPercentile = Defaults.WARM_UP_PERCENTILE;
        String runnerToExecute = Defaults.RUNNER_TO_EXECUTE;

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
                case "--warmUpPercentile":
                    i++;
                    if(i < argsLength) {
                        warmUpPercentile = Integer.parseInt(args[i]);
                    }
                    break;
                case "--runnerToExecute":
                    i++;
                    if(i < argsLength) {
                        runnerToExecute = args[i];
                    }
                    break;
                case "--verbose":
                    verbose = true;
                    break;
                case "--listOnly":
                    registerRunner(0, 0);
                    for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
                        System.out.println(performanceTestRun.getClass().getSimpleName());
                    }
                    System.exit(0);
                    break;
            }
        }

        registerRunner(elementsPerRound, mapElements);
        registerStatisticsCollector();
        setWarmUpRoundsForTestRuns(elementsPerRound, warmUpPercentile);

        System.out.println("Starting test run with parameters");
        System.out.println("\t   Elements in Map : " + mapElements);
        System.out.println("\tElements per Round : " + elementsPerRound);
        System.out.println("\t            Rounds : " + rounds);
        System.out.println("\tWarm up percentile : " + warmUpPercentile + "% of " + elementsPerRound);
        System.out.println("\t Runner to execute : " + runnerToExecute);
        System.out.println("\t      Verbose mode : " + verbose);

        Instant start = Instant.now();
        if(Defaults.RUNNER_TO_EXECUTE.equalsIgnoreCase(runnerToExecute)) {
            runTests(rounds);
        } else {
            runTest(rounds, runnerToExecute);
        }
        Instant end = Instant.now();

        printStatistics();
        System.out.println();
        System.out.println("Overall execution time " + Duration.between(start, end));
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

    private static void setWarmUpRoundsForTestRuns(int elementsPerRound, int warmUpPercentile) {
        int warmUpRoundsForTestRun = elementsPerRound / warmUpPercentile;
        for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
            performanceTestRun.setWarmUpRoundsForTestRun(warmUpRoundsForTestRun);
        }
    }

    private static void runTests(int rounds) {
        for (int round = 1; round <= rounds; round++) {
            logIfNotSilent("round %d of %d%n", round, rounds);
            Collections.shuffle(PERFORMANCE_TEST_RUNS);
            for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
                logIfNotSilent("\t" + performanceTestRun.getClass().getSimpleName());
                performanceTestRun.runTest();
            }
        }
    }

    private static void runTest(int rounds, String runnerToExecute) {
        PerformanceTestRun testToRun = null;
        for (PerformanceTestRun performanceTestRun : PERFORMANCE_TEST_RUNS) {
            if(performanceTestRun.getClass().getSimpleName().equals(runnerToExecute)) {
                testToRun = performanceTestRun;
                break;
            }
        }
        assert testToRun == null : "No runner found matching " + runnerToExecute;
        for (int round = 1; round <= rounds; round++) {
            logIfNotSilent("round %d of %d%n", round, rounds);
            testToRun.runTest();
        }
    }

    private static void logIfNotSilent(String log, Object ... args) {
        if(verbose) {
            System.out.printf(log, args);
        }
    }

    private static void printStatistics() {
        STATISTICS_COLLECTOR.printStatistics(TestEvent.SERIALIZE, TestEvent.DESERIALIZE);
    }
}
