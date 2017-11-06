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

package com.baidu.hugegraph.functional;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.client.BaseClientTest;
import com.baidu.hugegraph.exception.InvalidOperationException;
import com.baidu.hugegraph.structure.constant.Direction;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.testutil.Assert;
import com.baidu.hugegraph.testutil.Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class EdgeTest extends BaseFuncTest {

    @Before
    public void setup() {
        BaseClientTest.initPropertyKey();
        BaseClientTest.initVertexLabel();
        BaseClientTest.initEdgeLabel();
        BaseClientTest.initVertex();
    }

    @After
    public void teardown() throws Exception {
        BaseFuncTest.clearData();
    }

    @Test
    public void testAddEdgeProperty() {
        Edge created = graph().addEdge("person:peter", "created",
                                       "software:lop",
                                       "date", "20170324");
        Map<String, Object> props = ImmutableMap.of("date", "20170324");
        Assert.assertEquals(props, created.properties());

        created.property("city", "HongKong");
        props = ImmutableMap.of("date", "20170324", "city", "HongKong");
        Assert.assertEquals(props, created.properties());
    }

    @Test
    public void testUpdateEdgeProperty() {
        Edge created = graph().addEdge("person:peter", "created",
                                       "software:lop",
                                       "date", "20170324");
        Map<String, Object> props = ImmutableMap.of("date", "20170324");
        Assert.assertEquals(props, created.properties());

        created.property("date", "20170808");
        props = ImmutableMap.of("date", "20170808");
        Assert.assertEquals(props, created.properties());
    }

    @Test
    public void testAddEdgePropertyValueList() {
        schema().propertyKey("time")
                .asText()
                .valueList()
                .ifNotExist()
                .create();
        schema().edgeLabel("created")
                .properties("time")
                .nullableKeys("time")
                .append();

        Edge created = graph().addEdge("person:peter", "created",
                                        "software:lop",
                                        "date", "20170324",
                                        "time", "20121010");

        Map<String, Object> props = ImmutableMap.of("date", "20170324",
                                                    "time", ImmutableList.of(
                                                    "20121010"));
        Assert.assertEquals(props, created.properties());

        created.property("time", "20140214");
        props = ImmutableMap.of("date", "20170324", "time",
                                ImmutableList.of("20121010", "20140214"));
        Assert.assertEquals(props, created.properties());
    }

    @Test
    public void testAddEdgePropertyValueSet() {
        schema().propertyKey("time")
                .asText()
                .valueSet()
                .ifNotExist()
                .create();
        schema().edgeLabel("created")
                .properties("time")
                .nullableKeys("time")
                .append();

        Edge created = graph().addEdge("person:peter", "created",
                                       "software:lop",
                                       "date", "20170324",
                                       "time", "20121010");

        Map<String, Object> props = ImmutableMap.of("date", "20170324",
                                                    "time", ImmutableList.of(
                                                    "20121010"));
        Assert.assertEquals(props, created.properties());

        created.property("time", "20140214");
        props = ImmutableMap.of("date", "20170324", "time",
                                ImmutableList.of("20140214", "20121010"));
        Assert.assertEquals(props, created.properties());
    }

    @Test
    public void testAddEdgePropertyValueListWithSameValue() {
        schema().propertyKey("time")
                .asText()
                .valueList()
                .ifNotExist()
                .create();
        schema().edgeLabel("created")
                .properties("time")
                .nullableKeys("time")
                .append();

        Edge created = graph().addEdge("person:peter", "created",
                                       "software:lop",
                                       "date", "20170324",
                                       "time", "20121010");

        Map<String, Object> props = ImmutableMap.of("date", "20170324",
                                                    "time", ImmutableList.of(
                                                    "20121010"));
        Assert.assertEquals(props, created.properties());

        created.property("time", "20121010");
        props = ImmutableMap.of("date", "20170324", "time",
                                ImmutableList.of("20121010", "20121010"));
        Assert.assertEquals(props, created.properties());
    }

    @Test
    public void testAddEdgePropertyValueSetWithSameValue() {
        schema().propertyKey("time")
                .asText()
                .valueSet()
                .ifNotExist()
                .create();
        schema().edgeLabel("created")
                .properties("time")
                .nullableKeys("time")
                .append();

        Edge created = graph().addEdge("person:peter", "created",
                                       "software:lop",
                                       "date", "20170324",
                                       "time", "20121010");

        Map<String, Object> props = ImmutableMap.of("date", "20170324",
                                                    "time", ImmutableList.of(
                                                    "20121010"));
        Assert.assertEquals(props, created.properties());

        created.property("time", "20121010");
        props = ImmutableMap.of("date", "20170324", "time",
                                ImmutableList.of("20121010"));
        Assert.assertEquals(props, created.properties());
    }

    @Test
    public void testRemoveEdgeProperty() {
        schema().propertyKey("time")
                .asText()
                .valueSet()
                .ifNotExist()
                .create();
        schema().edgeLabel("created")
                .properties("time")
                .nullableKeys("time")
                .append();

        Edge created = graph().addEdge("person:peter", "created",
                                       "software:lop",
                                       "date", "20170324",
                                       "time", "20121010");

        Map<String, Object> props = ImmutableMap.of("date", "20170324",
                                                    "time", ImmutableList.of(
                                                    "20121010"));
        Assert.assertEquals(props, created.properties());

        created.removeProperty("time");
        props = ImmutableMap.of("date", "20170324");
        Assert.assertEquals(props, created.properties());
    }

    @Test
    public void testRemoveEdgePropertyNotExist() {
        Edge created = graph().addEdge("person:peter", "created",
                                       "software:lop",
                                       "date", "20170324");
        Map<String, Object> props = ImmutableMap.of("date", "20170324");
        Assert.assertEquals(props, created.properties());

        Assert.assertThrows(InvalidOperationException.class, () -> {
            created.removeProperty("not-exist");
        });
    }

    @Test
    public void testGetAllEdges() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().listEdges();
        Assert.assertEquals(6, edges.size());
        assertContains(edges, "person:marko", "knows", "person:vadas",
                              "date", "20160110");
        assertContains(edges, "person:marko", "knows", "person:josh",
                              "date", "20130220");
        assertContains(edges, "person:marko", "created", "software:lop",
                              "date", "20171210", "city", "Shanghai");
        assertContains(edges, "person:josh", "created", "software:ripple",
                              "date", "20171210", "city", "Beijing");
        assertContains(edges, "person:josh", "created", "software:lop",
                              "date", "20091111", "city", "Beijing");
        assertContains(edges, "person:peter", "created", "software:lop",
                              "date", "20170324", "city", "Hongkong");
    }

    @Test
    public void testGetAllEdgesWithNoLimit() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().listEdges(-1);
        Assert.assertEquals(6, edges.size());
        assertContains(edges, "person:marko", "knows", "person:vadas",
                              "date", "20160110");
        assertContains(edges, "person:marko", "knows", "person:josh",
                              "date", "20130220");
        assertContains(edges, "person:marko", "created", "software:lop",
                              "date", "20171210", "city", "Shanghai");
        assertContains(edges, "person:josh", "created", "software:ripple",
                              "date", "20171210", "city", "Beijing");
        assertContains(edges, "person:josh", "created", "software:lop",
                              "date", "20091111", "city", "Beijing");
        assertContains(edges, "person:peter", "created", "software:lop",
                              "date", "20170324", "city", "Hongkong");
    }

    @Test
    public void testGetEdgesByVertexId() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().getEdges("person:marko");
        Assert.assertEquals(3, edges.size());
        assertContains(edges, "person:marko", "knows", "person:vadas",
                              "date", "20160110");
        assertContains(edges, "person:marko", "knows", "person:josh",
                              "date", "20130220");
        assertContains(edges, "person:marko", "created", "software:lop",
                              "date", "20171210", "city", "Shanghai");
    }

    @Test
    public void testGetEdgesByVertexIdWithLimit2() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().getEdges("person:marko", 2);
        Assert.assertEquals(2, edges.size());
        for (Edge edge : edges) {
            Assert.assertEquals("person:marko", edge.source());
        }
    }

    @Test
    public void testGetEdgesByVertexIdDirection() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().getEdges("person:josh", Direction.OUT);
        Assert.assertEquals(2, edges.size());
        assertContains(edges, "person:josh", "created", "software:ripple",
                              "date", "20171210", "city", "Beijing");
        assertContains(edges, "person:josh", "created", "software:lop",
                              "date", "20091111", "city", "Beijing");

        edges = graph().getEdges("person:josh", Direction.IN);
        Assert.assertEquals(1, edges.size());
        assertContains(edges, "person:marko", "knows", "person:josh",
                              "date", "20130220");
    }

    @Test
    public void testGetEdgesByVertexIdDirectionWithLimit1() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().getEdges("person:josh", Direction.OUT, 1);
        Assert.assertEquals(1, edges.size());
        for (Edge edge : edges) {
            // TODO: Whether need to add direction property in Edge?
            Assert.assertEquals("person:josh", edge.source());
        }

        edges = graph().getEdges("person:josh", Direction.IN, 1);
        Assert.assertEquals(1, edges.size());
        for (Edge edge : edges) {
            Assert.assertEquals("person:josh", edge.target());
        }
    }

    @Test
    public void testGetEdgesByVertexIdDirectionLabel() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().getEdges("person:josh", Direction.OUT,
                                            "created");
        Assert.assertEquals(2, edges.size());
        assertContains(edges, "person:josh", "created", "software:ripple",
                              "date", "20171210", "city", "Beijing");
        assertContains(edges, "person:josh", "created", "software:lop",
                              "date", "20091111", "city", "Beijing");

        edges = graph().getEdges("person:josh", Direction.IN, "knows");
        Assert.assertEquals(1, edges.size());
        assertContains(edges, "person:marko", "knows", "person:josh",
                              "date", "20130220");
    }

    @Test
    public void testGetEdgesByVertexIdDirectionLabelWithLimit1() {
        BaseClientTest.initEdge();

        List<Edge> edges = graph().getEdges("person:josh", Direction.OUT,
                                            "created", 1);
        Assert.assertEquals(1, edges.size());
        for (Edge edge : edges) {
            Assert.assertEquals("person:josh", edge.source());
            Assert.assertEquals("created", edge.label());
        }

        edges = graph().getEdges("person:josh", Direction.IN, "knows", 1);
        Assert.assertEquals(1, edges.size());
        for (Edge edge : edges) {
            Assert.assertEquals("person:josh", edge.target());
            Assert.assertEquals("knows", edge.label());
        }
    }

    @Test
    public void testGetEdgesByVertexIdDirectionLabelProperties() {
        BaseClientTest.initEdge();

        Map<String, Object> properties = ImmutableMap.of("date", "20171210");
        List<Edge> edges = graph().getEdges("person:josh", Direction.OUT,
                                            "created", properties);
        Assert.assertEquals(1, edges.size());
        assertContains(edges, "person:josh", "created", "software:ripple",
                              "date", "20171210", "city", "Beijing");

        properties = ImmutableMap.of("date", "20130220");
        edges = graph().getEdges("person:josh", Direction.IN, "knows",
                                 properties);
        Assert.assertEquals(1, edges.size());
        assertContains(edges, "person:marko", "knows", "person:josh",
                              "date", "20130220");
    }

    @Test
    public void testGetEdgesByVertexIdDirectionLabelPropertiesWithLimit1() {
        BaseClientTest.initEdge();

        Map<String, Object> properties = ImmutableMap.of("date", "20171210");
        List<Edge> edges = graph().getEdges("person:josh", Direction.OUT,
                                            "created", properties);
        Assert.assertEquals(1, edges.size());
        for (Edge edge : edges) {
            Assert.assertEquals("person:josh", edge.source());
            Assert.assertEquals("created", edge.label());
        }

        properties = ImmutableMap.of("date", "20130220");
        edges = graph().getEdges("person:josh", Direction.IN, "knows",
                                 properties);
        Assert.assertEquals(1, edges.size());
        for (Edge edge : edges) {
            Assert.assertEquals("person:josh", edge.target());
            Assert.assertEquals("knows", edge.label());
        }
    }
    
    private static void assertContains(List<Edge> edges, String source,
                                       String label, String target,
                                       Object... keyValues) {
        Map<String, Object> properties = Utils.asMap(keyValues);

        Edge edge = new Edge(label);
        edge.source(source);
        edge.target(target);
        for (String key : properties.keySet()) {
            edge.property(key, properties.get(key));
        }

        Assert.assertTrue(Utils.contains(edges, edge));
    }
}
