/*
 * Copyright 2011 VZ Netzwerke Ltd
 * Copyright 2014 devbliss GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mongojack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import org.bson.BsonNull;
import org.bson.BsonUndefined;
import org.bson.Document;
import org.bson.types.Binary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mongojack.mock.MockEmbeddedObject;
import org.mongojack.mock.MockObject;
import org.mongojack.mock.MockObjectIntId;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for parser and generator
 */
public class TestParsingAndGenerating extends MongoDBTestBase {

    private JacksonMongoCollection<MockObject> coll;

    @BeforeEach
    public void setup() throws Exception {
        coll = getCollection(MockObject.class);
    }

    @Test
    public void testInsertNoId() {
        MockObject object = new MockObject();
        coll.insert(object);
        Assertions.assertNotNull(coll.findOne()._id);
    }

    @Test
    public void testInsertRetrieveAllEmpty() {
        MockObject object = new MockObject();
        object._id = "1";
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object, result);
    }

    @Test
    public void testInsertRetrieveString() {
        MockObject object = new MockObject();
        object.string = "a string";
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.string, result.string);
    }

    @Test
    public void testInsertRetrieveInteger() {
        MockObject object = new MockObject();
        object.integer = 10;
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.integer, result.integer);
    }

    @Test
    public void testInsertRetrieveLong() {
        MockObject object = new MockObject();
        object.longs = 10L;
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.longs, result.longs);
    }

    @Test
    //@Ignore("BSON doesn't yet know how to handle BigInteger")
    public void testInsertRetrieveBigInteger() {
        MockObject object = new MockObject();
        object.bigInteger = BigInteger.valueOf(100);
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.bigInteger, result.bigInteger);
    }

    @Test
    //@Ignore("BSON doesn't yet know how to handle BigInteger")
    public void testInsertRetrieveBigInteger2() {
        MockObject object = new MockObject();
        object.bigInteger = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.TEN);
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.bigInteger, result.bigInteger);
    }

    @Test
    public void testInsertRetrieveFloat() {
        MockObject object = new MockObject();
        object.floats = 3.0f;
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.floats, result.floats);
    }

    @Test
    public void testInsertRetrieveDouble() {
        MockObject object = new MockObject();
        object.doubles = 4.65;
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.doubles, result.doubles);
    }

    @Test
    public void testInsertRetrieveBigDecimal() {
        MockObject object = new MockObject();
        object.bigDecimal = BigDecimal.valueOf(4, 6);
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.bigDecimal, result.bigDecimal);
    }

    @Test
    public void testInsertRetrieveBoolean() {
        MockObject object = new MockObject();
        object.booleans = true;
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.booleans, result.booleans);
    }

    @Test
    public void testInsertRetrieveDate() {
        MockObject object = new MockObject();
        object.date = new Date(10000);
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.date, result.date);
    }

    @Test
    public void testDateIsStoredAsBsonDate() {
        MockObject object = new MockObject();
        object.date = new Date(10000);
        coll.insert(object);
        Document result = getMongoCollection(coll.getName(), Document.class).find().first();
        Assertions.assertEquals(object.date, result.get("date"));
    }

    @Test
    public void testInsertRetrieveEmptyList() {
        MockObject object = new MockObject();
        object.simpleList = Collections.emptyList();
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.simpleList, result.simpleList);
    }

    @Test
    public void testInsertRetrievePopulatedSimpleList() {
        MockObject object = new MockObject();
        object.simpleList = Arrays.asList("1", "2");
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.simpleList, result.simpleList);
    }

    @Test
    public void testInsertRetrievePopulatedComplexList() {
        MockObject object = new MockObject();
        MockEmbeddedObject o1 = new MockEmbeddedObject();
        o1.value = "o1";
        MockEmbeddedObject o2 = new MockEmbeddedObject();
        o2.value = "o2";
        object.complexList = Arrays.asList(o1, o2);
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.complexList, result.complexList);
    }

    @Test
    public void testInsertRetrieveEmbeddedObject() {
        MockObject object = new MockObject();
        object.object = new MockEmbeddedObject();
        object.object.value = "blah";
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.object, result.object);
    }

    @Test
    public void testInsertRetrieveEmebeddedObjectList() {
        MockObject object = new MockObject();
        object.object = new MockEmbeddedObject();
        object.object.list = Arrays.asList("1", "2");
        coll.insert(object);
        MockObject result = coll.findOne();
        Assertions.assertEquals(object.object, result.object);
    }

    @Test
    public void testEverything() {
        MockObject object = new MockObject();
        object._id = "theid";
        object.integer = 123;
        object.longs = 1234L;
        object.floats = 12.34f;
        object.doubles = 123.456;
        object.booleans = true;
        object.simpleList = Arrays.asList("simple1", "simple2");
        MockEmbeddedObject o1 = new MockEmbeddedObject();
        o1.value = "embedded 1";
        o1.list = Arrays.asList("e1 list1", "e1 list2");
        MockEmbeddedObject o2 = new MockEmbeddedObject();
        o2.value = "embedded 2";
        o2.list = Arrays.asList("e2 list1", "e2 list2");
        MockEmbeddedObject o3 = new MockEmbeddedObject();
        o3.value = "embedded 3";
        o3.list = Arrays.asList("e3 list1", "e3 list2");

        object.complexList = Arrays.asList(o1, o2);
        object.object = o3;

        coll.insert(object);
        Assertions.assertEquals(object, coll.findOne());
    }

    @Test
    public void testIntId() {
        MockObjectIntId object = new MockObjectIntId();
        object._id = 123456;

        JacksonMongoCollection<MockObjectIntId> coll = getCollection(MockObjectIntId.class);

        coll.insert(object);
        MockObjectIntId result = coll.findOne();
        Assertions.assertEquals(object._id, result._id);
    }

    @Test
    public void testParseErrors() {
        assertThrows(
            MongoException.class,
            () -> {
                coll.find(new Document("integer", new Document("$thisisinvalid", "true"))).cursor().hasNext();
            }
        );
    }

    @Test
    public void testByteArray() {
        ObjectWithByteArray object = new ObjectWithByteArray();
        object._id = "id";
        object.bytes = new byte[]{1, 2, 3, 4, 5};

        JacksonMongoCollection<ObjectWithByteArray> coll = getCollection(ObjectWithByteArray.class);
        coll.insert(object);

        ObjectWithByteArray result = coll.findOne();
        assertThat(result.bytes).isEqualTo(object.bytes);

        // Ensure that it is actually stored as binary
        Document dbObject = getMongoCollection(coll.getName(), Document.class).find().first();
        assertThat(dbObject.get("bytes")).isInstanceOf(Binary.class);
    }

    public static class ObjectWithByteArray {
        public String _id;
        public byte[] bytes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IgnoreUnknownObject {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String name) {
            this.value = name;
        }
    }

    @Test
    public void deserializingNull() {
        final JacksonMongoCollection<IgnoreUnknownObject> col = getCollection(IgnoreUnknownObject.class);
        final com.mongodb.client.MongoCollection<Document> collection = getMongoCollection(col.getName(), Document.class);
        collection.drop();
        collection.insertOne(new Document("value", new BsonNull()));
        final IgnoreUnknownObject foo = col.find().first();
        assertThat(foo).isNotNull();
        assertThat(foo.getValue()).isNull();
    }

    @Test
    public void deserializingUndefined() {
        final JacksonMongoCollection<IgnoreUnknownObject> col = getCollection(IgnoreUnknownObject.class);
        final com.mongodb.client.MongoCollection<Document> collection = getMongoCollection(col.getName(), Document.class);
        collection.drop();
        collection.insertOne(new Document("value", new BsonUndefined()));
        final IgnoreUnknownObject foo = col.find().first();
        assertThat(foo).isNotNull();
        assertThat(foo.getValue()).isNull();
    }

}
