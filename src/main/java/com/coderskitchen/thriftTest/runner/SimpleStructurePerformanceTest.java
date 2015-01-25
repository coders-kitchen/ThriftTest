package com.coderskitchen.thriftTest.runner;

import com.coderskitchen.thriftTest.gen.thrift.Header;
import com.coderskitchen.thriftTest.gen.thrift.SimpleStructure;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Peter on 25.01.2015.
 */
public class SimpleStructurePerformanceTest extends PerformanceTestRun<SimpleStructure, byte[]> {

    public final int MAP_ELEMENTS;
    public static  final TSerializer SERIALIZER = new TSerializer();
    public static final TDeserializer DESERIALIZER = new TDeserializer();

    public SimpleStructurePerformanceTest(int numberOfElementsToSerialize, int numberOfMapElements) {
        super(numberOfElementsToSerialize);
        MAP_ELEMENTS = numberOfMapElements;
    }

    @Override
    protected SimpleStructure createElement(int index) {
        SimpleStructure simpleStructure = new SimpleStructure(new Header("SomeHeader").setVersion(index));
        Map<String, String> body = new HashMap<String, String>();
        for (int mapIndex = 0; mapIndex < MAP_ELEMENTS; mapIndex++) {
            body.put("KEY_"+mapIndex, UUID.randomUUID().toString());
        }
        return simpleStructure.setBody(body);
    }

    @Override
    protected byte[] serializeElement(int index, SimpleStructure toSerializedObject) {
        try {
            return SERIALIZER.serialize(toSerializedObject);
        } catch (TException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    protected void deserializeElement(int index, byte[] bytes) {
        try {
            SimpleStructure base = new SimpleStructure();
            DESERIALIZER.deserialize(base, bytes);
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
