package com.coderskitchen.thriftTest.runner;

import com.coderskitchen.thriftTest.StatisticsCollector;
import com.coderskitchen.thriftTest.TestEvent;

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

    /**
     * Constructor, takes the number of elements to be serialized and deserialized
     * @param numberOfElementsToSerialize
     */
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
        long start = System.nanoTime();
        deserializeElement(index, serializerOutput);
        long end = System.nanoTime();
        storeDurationAfterWarmUpPeriod(index, TestEvent.DESERIALIZE, start, end);
    }

    private SERIALIZER_OUTPUT serializeElementAndMeasureDuration(int index, TO_SERIALIZE element) {
        long start = System.nanoTime();
        SERIALIZER_OUTPUT serializerOutput = serializeElement(index, element);
        long end = System.nanoTime();
        storeDurationAfterWarmUpPeriod(index, TestEvent.SERIALIZE, start, end);
        return serializerOutput;
    }

    private TO_SERIALIZE createElementAndMeasureDuration(int index) {
        long start = System.nanoTime();
        TO_SERIALIZE element = createElement(index);
        long end = System.nanoTime();
        storeDurationAfterWarmUpPeriod(index, TestEvent.CREATE, start, end);
        return element;
    }

    private void storeDurationAfterWarmUpPeriod(int index, TestEvent event, long start, long end) {
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


    private void storeDuration(TestEvent event, long start, long end) {
        statisticsCollector.addDuration(this.getClass(), event, start, end);
    }

    public void setWarmUpRoundsForTestRun(int warmUpRoundsForTestRun) {
        this.setWarmUpRoundsForTestRun = warmUpRoundsForTestRun;
    }

    /**
     * Set the StatisticsCollector to be used.
     * Overrides the default no-op implementation
     *
     * @param statisticsCollector
     */
    public void setStatisticsCollector(StatisticsCollector statisticsCollector) {
        this.statisticsCollector = statisticsCollector;
    }
}
