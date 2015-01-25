package com.coderskitchen.thriftTest.runner;

import com.coderskitchen.thriftTest.StatisticsCollector;
import com.coderskitchen.thriftTest.TestEvent;

import java.time.Instant;

/**
 * Created by Peter on 25.01.2015.
 */
public abstract class PerformanceTestRun<TO_SERIALIZE, SERIALIZER_OUTPUT> {

    private final int numberOfElementsToSerialize;
    private int setWarmUpRoundsForTestRun = 0;

    // Initialized with default no-op runner
    private StatisticsCollector statisticsCollector = (runner, testEvent, start, end) -> {
        // DO NOTHING
    };

    public PerformanceTestRun(int numberOfElementsToSerialize) {
        this.numberOfElementsToSerialize = numberOfElementsToSerialize;
    }


    /**
     * Execute three test stages
     * - setup
     * - serialize
     * - deserialize
     */
    public void runTest() {
        for (int index = 0; index < numberOfElementsToSerialize; index++) {
            TO_SERIALIZE element = createElementAndMeasureDuration(index);
            SERIALIZER_OUTPUT serializerOutput = serializeElementAndMeasureDuration(index, element);
            deserializeElementAndMeasureDuration(index, serializerOutput);
        }
    }

    private void deserializeElementAndMeasureDuration(int index, SERIALIZER_OUTPUT serializerOutput) {
        Instant start = Instant.now();
        deserializeElement(index, serializerOutput);
        Instant end = Instant.now();
        storeDurationAfterWarmUpPeriod(index, TestEvent.DESERIALIZE, start, end);
    }

    private SERIALIZER_OUTPUT serializeElementAndMeasureDuration(int index, TO_SERIALIZE element) {
        Instant start= Instant.now();
        SERIALIZER_OUTPUT serializerOutput = serializeElement(index, element);
        Instant end = Instant.now();
        storeDurationAfterWarmUpPeriod(index, TestEvent.SERIALIZE, start, end);
        return serializerOutput;
    }

    private TO_SERIALIZE createElementAndMeasureDuration(int index) {
        Instant start = Instant.now();
        TO_SERIALIZE element = createElement(index);
        Instant end = Instant.now();
        storeDurationAfterWarmUpPeriod(index, TestEvent.CREATE, start, end);
        return element;
    }

    private void storeDurationAfterWarmUpPeriod(int index, TestEvent event, Instant start, Instant end) {
        if (setWarmUpRoundsForTestRun < index) {
            storeDuration(event, start, end);
        }
    }


    /**
     * Creates a single element that should be serialized.
     *
     * @param index current index in storage array
     * @return new element
     */
    protected abstract TO_SERIALIZE createElement(int index);


    /**
     * Calls the specific serializer to serialize one element
     *
     * @param index              current index in array of to be serialized objects
     * @param toSerializedObject current serialized object
     * @return serialized object
     */
    protected abstract SERIALIZER_OUTPUT serializeElement(int index, TO_SERIALIZE toSerializedObject);

    /**
     * Deserialize specific element
     *
     * @param index             index in array of serialized objects
     * @param serializer_output
     */
    protected abstract void deserializeElement(int index, SERIALIZER_OUTPUT serializer_output);


    private void storeDuration(TestEvent event, Instant start, Instant end) {
        statisticsCollector.addDuration(this.getClass(), event, start, end);
    }

    public void setWarmUpRoundsForTestRun(int warmUpRoundsForTestRun) {
        this.setWarmUpRoundsForTestRun = warmUpRoundsForTestRun;
    }

    public void setStatisticsCollector(StatisticsCollector statisticsCollector) {
        this.statisticsCollector = statisticsCollector;
    }
}
