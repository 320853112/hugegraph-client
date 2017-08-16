/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baidu.hugegraph.driver;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.hugegraph.exception.ClientException;
import com.baidu.hugegraph.structure.constant.DataType;
import com.baidu.hugegraph.structure.constant.Frequency;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.constant.IndexType;
import com.baidu.hugegraph.structure.constant.T;
import com.baidu.hugegraph.structure.schema.EdgeLabel;
import com.baidu.hugegraph.structure.schema.IndexLabel;
import com.baidu.hugegraph.structure.schema.PropertyKey;
import com.baidu.hugegraph.structure.schema.VertexLabel;

public class SchemaTest extends BaseTest{

    // propertykey tests
    @Test
    public void testAddPropertyKeyWithoutDataType() {
        SchemaManager schema = client().schema();
        PropertyKey id = schema.propertyKey("id").create();
        Assert.assertNotNull(id);
        Assert.assertEquals(DataType.TEXT, id.dataType());
    }

    @Test
    public void testAddPropertyKey() {
        SchemaManager schema = client().schema();
        schema.propertyKey("name").asText().create();

        Assert.assertNotNull(schema.getPropertyKey("name"));
        Assert.assertEquals("name", schema.getPropertyKey("name").name());
        Assert.assertEquals(DataType.TEXT,
                schema.getPropertyKey("name").dataType());
    }

    // vertexlabel tests
    @Test
    public void testAddVertexLabel() {
        initProperties();
        SchemaManager schema = client().schema();

        VertexLabel person = schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();

        Assert.assertNotNull(person);
        Assert.assertEquals("person", person.name());
        Assert.assertEquals(3, person.properties().size());
        Assert.assertTrue(person.properties().contains("name"));
        Assert.assertTrue(person.properties().contains("age"));
        Assert.assertTrue(person.properties().contains("city"));
        Assert.assertEquals(1, person.primaryKeys().size());
        Assert.assertTrue(person.primaryKeys().contains("name"));
    }

    @Test
    public void testAddVertexLabelWith2PrimaryKey() {
        initProperties();
        SchemaManager schema = client().schema();

        VertexLabel person = schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name", "age")
                .create();

        Assert.assertNotNull(person);
        Assert.assertEquals("person", person.name());
        Assert.assertEquals(3, person.properties().size());
        Assert.assertTrue(person.properties().contains("name"));
        Assert.assertTrue(person.properties().contains("age"));
        Assert.assertTrue(person.properties().contains("city"));
        Assert.assertEquals(2, person.primaryKeys().size());
        Assert.assertTrue(person.primaryKeys().contains("name"));
        Assert.assertTrue(person.primaryKeys().contains("age"));
    }

    @Test
    public void testAddVertexLabelWithoutPrimaryKey() {
        initProperties();
        SchemaManager schema = client().schema();

        Utils.assertThrows(ClientException.class, () -> {
            schema.vertexLabel("person")
                    .properties("name", "age", "city")
                    .create();
        });
    }

    @Test
    public void testAddVertexLabelWithoutPropertyKey() {
        initProperties();
        SchemaManager schema = client().schema();

        Utils.assertThrows(ClientException.class, () -> {
            schema.vertexLabel("person").create();
        });
    }

    @Test
    public void testAddVertexLabelWithNotExistProperty() {
        initProperties();
        SchemaManager schema = client().schema();

        Utils.assertThrows(ClientException.class, () -> {
            schema.vertexLabel("person").properties("sex").create();
        });
    }

    @Test
    public void testAddVertexLabelNewVertexWithUndefinedProperty() {
        initProperties();
        SchemaManager schema = client().schema();

        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();

        Utils.assertThrows(ClientException.class, () -> {
            client().graph().addVertex(T.label, "person", "name", "Baby",
                    "city", "Hongkong", "age", 3, "sex", "male");
        });
    }

    @Test
    public void testAddVertexLabelNewVertexWithPropertyAbsent() {
        initProperties();
        SchemaManager schema = client().schema();
        VertexLabel person = schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();

        client().graph().addVertex(T.label, "person", "name", "Baby",
                "city", "Hongkong");

        Assert.assertNotNull(person);
        Assert.assertEquals("person", person.name());
        Assert.assertEquals(3, person.properties().size());
        Assert.assertTrue(person.properties().contains("name"));
        Assert.assertTrue(person.properties().contains("age"));
        Assert.assertTrue(person.properties().contains("city"));
        Assert.assertEquals(1, person.primaryKeys().size());
        Assert.assertTrue(person.primaryKeys().contains("name"));
    }

    @Test
    public void testAddVertexLabelNewVertexWithUnmatchPropertyType() {
        initProperties();
        SchemaManager schema = client().schema();

        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();

        Utils.assertThrows(ClientException.class, () -> {
            client().graph().addVertex(T.label, "person", "name", "Baby",
                    "city", 2, "age", 3);
        });

    }

    // edgelabel tests
    @Test
    public void testAddEdgeLabel() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();
        schema.vertexLabel("book").properties("id", "name")
                .primaryKeys("id").create();
        EdgeLabel look = schema.edgeLabel("look").multiTimes()
                .properties("time")
                .link("person", "book")
                .sortKeys("time")
                .create();

        Assert.assertNotNull(look);
        Assert.assertEquals("look", look.name());
        Assert.assertTrue(look.sourceLabel().equals("person"));
        Assert.assertTrue(look.targetLabel().equals("book"));
        Assert.assertEquals(1, look.properties().size());
        Assert.assertTrue(look.properties().contains("time"));
        Assert.assertEquals(1, look.sortKeys().size());
        Assert.assertTrue(look.sortKeys().contains("time"));
        Assert.assertEquals(Frequency.MULTIPLE, look.frequency());
    }

    @Test
    public void testAddEdgeLabelWithoutFrequency() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();
        schema.vertexLabel("book").properties("id", "name")
                .primaryKeys("id").create();
        EdgeLabel look = schema.edgeLabel("look").properties("time")
                .link("person", "book")
                .create();

        Assert.assertNotNull(look);
        Assert.assertEquals("look", look.name());
        Assert.assertTrue(look.sourceLabel().equals("person"));
        Assert.assertTrue(look.targetLabel().equals("book"));
        Assert.assertEquals(1, look.properties().size());
        Assert.assertTrue(look.properties().contains("time"));
        Assert.assertEquals(0, look.sortKeys().size());
        Assert.assertEquals(Frequency.SINGLE, look.frequency());
    }

    @Test
    public void testAddEdgeLabelWithoutProperty() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();
        schema.vertexLabel("book").properties("id", "name")
                .primaryKeys("id").create();
        EdgeLabel look = schema.edgeLabel("look").singleTime()
                .link("person", "book")
                .create();

        Assert.assertNotNull(look);
        Assert.assertEquals("look", look.name());
        Assert.assertTrue(look.sourceLabel().equals("person"));
        Assert.assertTrue(look.targetLabel().equals("book"));
        Assert.assertEquals(0, look.properties().size());
        Assert.assertEquals(0, look.sortKeys().size());
        Assert.assertEquals(Frequency.SINGLE, look.frequency());
    }

    @Test
    public void testAddEdgeLabelWithoutLink() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();

        Utils.assertThrows(ClientException.class, () -> {
            EdgeLabel look = schema.edgeLabel("look").multiTimes()
                    .properties("time")
                    .sortKeys("time")
                    .create();
        });
    }

    @Test
    public void testAddEdgeLabelWithNotExistProperty() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();

        Utils.assertThrows(ClientException.class, () -> {
            schema.edgeLabel("look").properties("date-time")
                    .link("author", "book")
                    .link("person", "book")
                    .create();
        });
    }

    @Test
    public void testAddEdgeLabelWithNotExistVertexLabel() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();

        Utils.assertThrows(ClientException.class, () -> {
            schema.edgeLabel("look").multiTimes().properties("time")
                    .link("reviewer", "book")
                    .link("person", "book")
                    .sortKeys("time")
                    .create();
        });
    }

    @Test
    public void testAddEdgeLabelMultipleWithoutSortKey() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();

        Utils.assertThrows(ClientException.class, () -> {
            schema.edgeLabel("look").multiTimes().properties("date")
                    .link("author", "book")
                    .link("person", "book")
                    .create();
        });
    }

    @Test
    public void testAddEdgeLabelSortKeyNotInProperty() {
        initProperties();
        SchemaManager schema = client().schema();
        schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();

        Utils.assertThrows(ClientException.class, () -> {
            schema.edgeLabel("look").multiTimes().properties("date")
                    .link("author", "book")
                    .link("person", "book")
                    .sortKeys("time")
                    .create();
        });
    }

    // indexlabel tests
    @Test
    public void testAddIndexLabelOfVertex() {
        initProperties();
        SchemaManager schema = client().schema();
        VertexLabel person = schema.vertexLabel("person")
                .properties("name", "age", "city")
                .primaryKeys("name")
                .create();
        IndexLabel personByCity = schema.indexLabel("personByCity")
                .onV("person").secondary().by("city").create();
        IndexLabel personByAge = schema.indexLabel("personByAge")
                .onV("person").search().by("age").create();

        Assert.assertNotNull(personByCity);
        Assert.assertNotNull(personByAge);
        Assert.assertEquals(2, person.indexNames().size());
        Assert.assertTrue(person.indexNames().contains("personByCity"));
        Assert.assertTrue(person.indexNames().contains("personByAge"));
        Assert.assertEquals(HugeType.VERTEX_LABEL, personByCity.baseType());
        Assert.assertEquals(HugeType.VERTEX_LABEL, personByAge.baseType());
        Assert.assertEquals("person", personByCity.baseValue());
        Assert.assertEquals("person", personByAge.baseValue());
        Assert.assertEquals(IndexType.SECONDARY, personByCity.indexType());
        Assert.assertEquals(IndexType.SEARCH, personByAge.indexType());
    }

    @Test
    public void testAddIndexLabelOfEdge() {
        initProperties();
        SchemaManager schema = client().schema();

        schema.vertexLabel("author").properties("id", "name")
                .primaryKeys("id").create();
        schema.vertexLabel("book").properties("name")
                .primaryKeys("name").create();
        EdgeLabel authored = schema.edgeLabel("authored").singleTime()
                .link("author", "book")
                .properties("contribution", "time")
                .create();

        IndexLabel authoredByContri = schema.indexLabel("authoredByContri")
                .onE("authored")
                .secondary()
                .by("time")
                .create();

        Assert.assertNotNull(authoredByContri);
        Assert.assertEquals(1, authored.indexNames().size());
        Assert.assertTrue(authored.indexNames().contains("authoredByContri"));
        Assert.assertEquals(HugeType.EDGE_LABEL, authoredByContri.baseType());
        Assert.assertEquals("authored", authoredByContri.baseValue());
        Assert.assertEquals(IndexType.SECONDARY, authoredByContri.indexType());
    }

    // utils
    public void initProperties() {
        SchemaManager schema = client().schema();
        schema.propertyKey("id").asInt().create();
        schema.propertyKey("name").asText().create();
        schema.propertyKey("age").asInt().valueSingle().create();
        schema.propertyKey("city").asText().create();
        schema.propertyKey("time").asText().create();
        schema.propertyKey("contribution").asText().valueSet().create();
    }
}
