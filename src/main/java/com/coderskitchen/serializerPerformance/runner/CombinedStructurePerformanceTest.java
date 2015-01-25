package com.coderskitchen.serializerPerformance.runner;

import com.coderskitchen.serializerPerformance.gen.thrift.CombinedStructure;
import com.coderskitchen.serializerPerformance.gen.thrift.Header;
import com.coderskitchen.serializerPerformance.gen.thrift.ObjectMap;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Peter on 25.01.2015.
 */
public class CombinedStructurePerformanceTest extends PerformanceTestRun<CombinedStructure, byte[]> {

    public final int MAP_ELEMENTS;
    public static  final TSerializer SERIALIZER = new TSerializer();
    public static final TDeserializer DESERIALIZER = new TDeserializer();
    public ObjectMap[] objectMaps;

    public CombinedStructurePerformanceTest(int numberOfElementsToSerialize, int numberOfMapElements) {
        super(numberOfElementsToSerialize);
        MAP_ELEMENTS = numberOfMapElements;
        objectMaps = new ObjectMap[numberOfElementsToSerialize];
    }

    @Override
    protected CombinedStructure createElement(int index) {
        CombinedStructure simpleStructure = new CombinedStructure(new Header("SomeHeader").setVersion(index));
        Map<String, String> body = new HashMap<String, String>();
        for (int mapIndex = 0; mapIndex < MAP_ELEMENTS; mapIndex++) {
            body.put("KEY_"+mapIndex, UUID.randomUUID().toString());
        }
        objectMaps[index] = new ObjectMap(body);
        return simpleStructure;
    }

    @Override
    protected byte[] serializeElement(int index, CombinedStructure toSerializedObject) {
        try {
            toSerializedObject.setBody(SERIALIZER.serialize(objectMaps[index]));
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
            ObjectMap objectMap = new ObjectMap();
            DESERIALIZER.deserialize(objectMap, combinedStructure.getBody());
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
