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
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.api;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.baidu.hugegraph.structure.constant.Frequency;
import com.baidu.hugegraph.structure.schema.EdgeLabel;
import com.baidu.hugegraph.testutil.Assert;
import com.baidu.hugegraph.testutil.Utils;
import com.google.common.collect.ImmutableSet;

public class EdgeLabelApiTest extends BaseApiTest {

    private static Function<String, EdgeLabel> fillEdgeLabel =
            (name) -> schema().edgeLabel(name)
                              .sourceLabel("person")
                              .targetLabel("software")
                              .singleTime()
                              .properties("date", "city")
                              .build();

    @BeforeClass
    public static void prepareSchema() {
        BaseApiTest.initPropertyKey();
        BaseApiTest.initVertexLabel();
    }

    @After
    public void teardown() throws Exception {
        edgeLabelAPI.list().forEach(el -> {
            waitUntilTaskCompleted(edgeLabelAPI.delete(el.name()));
        });
    }

    @Test
    public void testCreate() {
        EdgeLabel edgeLabel = fillEdgeLabel.apply("created");
        edgeLabel = edgeLabelAPI.create(edgeLabel);

        Assert.assertEquals("created", edgeLabel.name());
        Assert.assertEquals("person", edgeLabel.sourceLabel());
        Assert.assertEquals("software", edgeLabel.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel.frequency());
        Assert.assertEquals(true, edgeLabel.enableLabelIndex());
        Set<String> props = ImmutableSet.of("date", "city");
        Assert.assertTrue(props.size() == edgeLabel.properties().size());
        Assert.assertTrue(props.containsAll(edgeLabel.properties()));
    }

    @Test
    public void testCreateWithEnableLabelIndexFalse() {
        EdgeLabel edgeLabel = schema().edgeLabel("created")
                                      .sourceLabel("person")
                                      .targetLabel("software")
                                      .singleTime()
                                      .properties("date", "city")
                                      .enableLabelIndex(false)
                                      .create();

        Assert.assertEquals("created", edgeLabel.name());
        Assert.assertEquals("person", edgeLabel.sourceLabel());
        Assert.assertEquals("software", edgeLabel.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel.frequency());
        Assert.assertEquals(false, edgeLabel.enableLabelIndex());
        Set<String> props = ImmutableSet.of("date", "city");
        Assert.assertTrue(props.size() == edgeLabel.properties().size());
        Assert.assertTrue(props.containsAll(edgeLabel.properties()));
    }

    @Test
    public void testCreateWithInvalidName() {
        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(fillEdgeLabel.apply(""));
        });
        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(fillEdgeLabel.apply(" "));
        });
        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(fillEdgeLabel.apply("    "));
        });
    }

    @Test
    public void testCreateExistedEdgeLabel() {
        edgeLabelAPI.create(fillEdgeLabel.apply("created"));

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(fillEdgeLabel.apply("created"));
        });
    }

    @Test
    public void testCreateWithUndefinedPropertyKey() {
        EdgeLabel edgeLabel = schema().edgeLabel("created")
                                      .sourceLabel("person")
                                      .targetLabel("software")
                                      .singleTime()
                                      .properties("undefined", "city")
                                      .build();

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(edgeLabel);
        });
    }

    @Test
    public void testCreateWithUndefinedSortKey() {
        EdgeLabel edgeLabel = schema().edgeLabel("created")
                                      .sourceLabel("person")
                                      .targetLabel("software")
                                      .multiTimes()
                                      .properties("date", "city")
                                      .sortKeys("undefined")
                                      .build();

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(edgeLabel);
        });
    }

    @Test
    public void testCreateWithUndefinedNullableKeys() {
        EdgeLabel edgeLabel = schema().edgeLabel("created")
                                      .sourceLabel("person")
                                      .targetLabel("software")
                                      .singleTime()
                                      .properties("date", "city")
                                      .nullableKeys("undefined")
                                      .build();

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(edgeLabel);
        });
    }

    @Test
    public void testCreateWithNonNullKeysIntersectSortKeys() {
        EdgeLabel edgeLabel = schema().edgeLabel("created")
                                      .sourceLabel("person")
                                      .targetLabel("software")
                                      .multiTimes()
                                      .properties("date", "city")
                                      .sortKeys("date")
                                      .nullableKeys("date")
                                      .build();

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(edgeLabel);
        });

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(edgeLabel);
        });
    }

    @Test
    public void testCreateWithUndefinedVertexLabel() {
        EdgeLabel edgeLabel = schema().edgeLabel("created")
                                      .sourceLabel("programmer")
                                      .targetLabel("software")
                                      .singleTime()
                                      .properties("date", "city")
                                      .build();

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.create(edgeLabel);
        });
    }

    @Test
    public void testAppend() {
        EdgeLabel edgeLabel1 = schema().edgeLabel("created")
                                       .sourceLabel("person")
                                       .targetLabel("software")
                                       .singleTime()
                                       .properties("date")
                                       .build();

        edgeLabel1 = edgeLabelAPI.create(edgeLabel1);

        Assert.assertEquals("created", edgeLabel1.name());
        Assert.assertEquals("person", edgeLabel1.sourceLabel());
        Assert.assertEquals("software", edgeLabel1.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel1.frequency());
        Set<String> props = ImmutableSet.of("date");
        Assert.assertEquals(props, edgeLabel1.properties());

        EdgeLabel edgeLabel2 = schema().edgeLabel("created")
                                       .properties("city")
                                       .nullableKeys("city")
                                       .build();
        edgeLabel2 = edgeLabelAPI.append(edgeLabel2);

        Assert.assertEquals("created", edgeLabel2.name());
        Assert.assertEquals("person", edgeLabel2.sourceLabel());
        Assert.assertEquals("software", edgeLabel2.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel2.frequency());
        props = ImmutableSet.of("date", "city");
        Set<String> nullableKeys = ImmutableSet.of("city");
        Assert.assertEquals(props, edgeLabel2.properties());
        Assert.assertEquals(nullableKeys, edgeLabel2.nullableKeys());
    }

    @Test
    public void testAppendWithUndefinedPropertyKey() {
        EdgeLabel edgeLabel1 = schema().edgeLabel("created")
                                       .sourceLabel("person")
                                       .targetLabel("software")
                                       .singleTime()
                                       .properties("date")
                                       .build();
        edgeLabel1 = edgeLabelAPI.create(edgeLabel1);

        Assert.assertEquals("created", edgeLabel1.name());
        Assert.assertEquals("person", edgeLabel1.sourceLabel());
        Assert.assertEquals("software", edgeLabel1.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel1.frequency());
        Set<String> props = ImmutableSet.of("date");
        Assert.assertEquals(props, edgeLabel1.properties());

        EdgeLabel edgeLabel2 = schema().edgeLabel("created")
                                       .properties("undefined")
                                       .build();
        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.append(edgeLabel2);
        });
    }

    @Test
    public void testAppendWithUndefinedNullableKeys() {
        EdgeLabel edgeLabel1 = schema().edgeLabel("created")
                                       .sourceLabel("person")
                                       .targetLabel("software")
                                       .singleTime()
                                       .properties("date")
                                       .build();
        
        edgeLabel1 = edgeLabelAPI.create(edgeLabel1);

        Assert.assertEquals("created", edgeLabel1.name());
        Assert.assertEquals("person", edgeLabel1.sourceLabel());
        Assert.assertEquals("software", edgeLabel1.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel1.frequency());
        Set<String> props = ImmutableSet.of("date");
        Assert.assertEquals(props, edgeLabel1.properties());

        EdgeLabel edgeLabel2 = schema().edgeLabel("created")
                                .nullableKeys("undefined").build();
        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.append(edgeLabel2);
        });
    }

    @Test
    public void testAppendWithSourceOrTaregtLabel() {
        EdgeLabel edgeLabel1 = schema().edgeLabel("created")
                                       .sourceLabel("person")
                                       .targetLabel("software")
                                       .singleTime()
                                       .properties("date")
                                       .build();
        edgeLabel1 = edgeLabelAPI.create(edgeLabel1);

        Assert.assertEquals("created", edgeLabel1.name());
        Assert.assertEquals("person", edgeLabel1.sourceLabel());
        Assert.assertEquals("software", edgeLabel1.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel1.frequency());
        Set<String> props = ImmutableSet.of("date");
        Assert.assertEquals(props, edgeLabel1.properties());

        EdgeLabel edgeLabel2 = schema().edgeLabel("created")
                                       .sourceLabel("person")
                                       .targetLabel("person")
                                       .properties("city")
                                       .build();

        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.append(edgeLabel2);
        });
    }

    @Test
    public void testEliminate() {
        EdgeLabel edgeLabel1 = schema().edgeLabel("created")
                                       .sourceLabel("person")
                                       .targetLabel("software")
                                       .singleTime()
                                       .properties("date")
                                       .build();
        edgeLabel1 = edgeLabelAPI.create(edgeLabel1);
        Assert.assertEquals("created", edgeLabel1.name());
        Assert.assertEquals("person", edgeLabel1.sourceLabel());
        Assert.assertEquals("software", edgeLabel1.targetLabel());
        Assert.assertEquals(Frequency.SINGLE, edgeLabel1.frequency());
        Set<String> props = ImmutableSet.of("date");
        Assert.assertEquals(props, edgeLabel1.properties());

        EdgeLabel edgeLabel2 = schema().edgeLabel("created")
                                .properties("city").build();
        Utils.assertResponseError(400, () -> {
            edgeLabelAPI.eliminate(edgeLabel2);
        });
    }

    @Test
    public void testGet() {
        EdgeLabel edgeLabel1 = edgeLabelAPI.create(fillEdgeLabel.apply
                ("created"));

        EdgeLabel edgeLabel2 = edgeLabelAPI.get("created");

        Assert.assertEquals(edgeLabel1.name(), edgeLabel2.name());
        Assert.assertEquals(edgeLabel1.sourceLabel(), edgeLabel2.sourceLabel());
        Assert.assertEquals(edgeLabel1.targetLabel(), edgeLabel2.targetLabel());
        Assert.assertEquals(edgeLabel1.frequency(), edgeLabel2.frequency());
        Assert.assertEquals(edgeLabel1.properties(), edgeLabel2.properties());
    }

    @Test
    public void testGetNotExist() {
        Utils.assertResponseError(404, () -> {
            edgeLabelAPI.get("not-exist-el");
        });
    }

    @Test
    public void testList() {
        EdgeLabel edgeLabel1 = schema().edgeLabel("created")
                                       .sourceLabel("person")
                                       .targetLabel("software")
                                       .singleTime()
                                       .properties("date", "city")
                                       .build();
        edgeLabel1 = edgeLabelAPI.create(edgeLabel1);

        EdgeLabel edgeLabel2 = schema().edgeLabel("knows")
                                       .sourceLabel("person")
                                       .targetLabel("person")
                                       .singleTime()
                                       .properties("date")
                                       .build();
        edgeLabel2 = edgeLabelAPI.create(edgeLabel2);

        List<EdgeLabel> edgeLabels = edgeLabelAPI.list();
        Assert.assertEquals(2, edgeLabels.size());
        assertContains(edgeLabels, edgeLabel1);
        assertContains(edgeLabels, edgeLabel2);
    }

    @Test
    public void testDelete() {
        EdgeLabel edgeLabel = schema().edgeLabel("created")
                                      .sourceLabel("person")
                                      .targetLabel("software")
                                      .singleTime()
                                      .properties("date", "city")
                                      .build();
        edgeLabelAPI.create(edgeLabel);

        long taskId = edgeLabelAPI.delete("created");
        waitUntilTaskCompleted(taskId);

        Utils.assertResponseError(404, () -> {
            edgeLabelAPI.get("created");
        });
    }

    @Test
    public void testDeleteNotExist() {
        Utils.assertResponseError(404, () -> {
            edgeLabelAPI.delete("not-exist-el");
        });
    }

    @Test
    public void testAddEdgeLabelWithUserData() {
        schema().vertexLabel("book")
                .properties("name")
                .primaryKeys("name")
                .ifNotExist()
                .create();

        EdgeLabel father = schema().edgeLabel("father")
                                   .link("person", "person")
                                   .properties("weight")
                                   .userdata("multiplicity", "one-to-many")
                                   .build();
        edgeLabelAPI.create(father);
        Assert.assertEquals(1, father.userdata().size());
        Assert.assertEquals("one-to-many",
                            father.userdata().get("multiplicity"));

        EdgeLabel write = schema().edgeLabel("write")
                                  .link("person", "book")
                                  .properties("date", "weight")
                                  .userdata("multiplicity", "one-to-many")
                                  .userdata("multiplicity", "many-to-many")
                                  .build();
        edgeLabelAPI.create(write);
        // The same key user data will be overwritten
        Assert.assertEquals(1, write.userdata().size());
        Assert.assertEquals("many-to-many",
                            write.userdata().get("multiplicity"));
    }

    private static void assertContains(List<EdgeLabel> edgeLabels,
                                       EdgeLabel edgeLabel) {
        Assert.assertTrue(Utils.contains(edgeLabels, edgeLabel));
    }
}
