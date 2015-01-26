package com.coderskitchen.thriftTest.runner;

import com.coderskitchen.thriftTest.gen.thrift.CombinedStructure;
import com.coderskitchen.thriftTest.gen.thrift.EventObject;
import com.coderskitchen.thriftTest.gen.thrift.Header;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.util.UUID;

/**
 * Created by Peter on 25.01.2015.
 */
public class CombinedStructureWithConcreteObjectPerformanceTest extends PerformanceTestRun<CombinedStructure, byte[]> {

    public static  final TSerializer SERIALIZER = new TSerializer();
    public static final TDeserializer DESERIALIZER = new TDeserializer();
    public EventObject eventObject;

    public CombinedStructureWithConcreteObjectPerformanceTest(int numberOfElementsToSerialize) {
        super(numberOfElementsToSerialize);
    }

    @Override
    protected CombinedStructure createElement(int index) {
        CombinedStructure simpleStructure = new CombinedStructure(new Header("SomeHeader").setVersion(index));
        eventObject = new EventObject(index, "Some Name" + index, index % 2 == 0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        return simpleStructure;
    }

    @Override
    protected byte[] serializeElement(int index, CombinedStructure toSerializedObject) {
        try {
            toSerializedObject.setBody(SERIALIZER.serialize(eventObject));
            return SERIALIZER.serialize(toSerializedObject);
        } catch (TException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    protected void deserializeElement(int index, byte[] bytes) {
        try {
            CombinedStructure combinedStructure = new CombinedStructure();
            DESERIALIZER.deserialize(combinedStructure, bytes);
            EventObject objectMap = new EventObject();
            DESERIALIZER.deserialize(objectMap, combinedStructure.getBody());
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
