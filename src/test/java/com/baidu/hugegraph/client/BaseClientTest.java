package com.baidu.hugegraph.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.baidu.hugegraph.driver.GraphManager;
import com.baidu.hugegraph.driver.GremlinManager;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.structure.constant.T;
import com.baidu.hugegraph.structure.graph.Edge;
import com.baidu.hugegraph.structure.graph.Vertex;

public class BaseClientTest {

    protected static String BASE_URL = "http://127.0.0.1:8080";
    protected static String GRAPH = "hugegraph";

    private static HugeClient client;

    protected static HugeClient open() {
        return new HugeClient(BASE_URL, GRAPH);
    }

    @BeforeClass
    public static void init() {
        client = open();
    }

    @AfterClass
    public static void clear() throws Exception {
        // client.close();
    }

    public static SchemaManager schema() {
        return client.schema();
    }

    public static GraphManager graph() {
        return client.graph();
    }

    public static GremlinManager gremlin() {
        return client.gremlin();
    }

    @Before
    public void setup() {
        // this.clearData();
    }

    @After
    public void teardown() throws Exception {
        // pass
    }

    protected static void initPropertyKey() {
        SchemaManager schema = schema();
        schema.propertyKey("name").asText().ifNotExist().create();
        schema.propertyKey("age").asInt().ifNotExist().create();
        schema.propertyKey("city").asText().ifNotExist().create();
        schema.propertyKey("lang").asText().ifNotExist().create();
        schema.propertyKey("date").asText().ifNotExist().create();
        schema.propertyKey("price").asInt().ifNotExist().create();
    }

    protected static void initVertexLabel() {
        SchemaManager schema = schema();

        schema.vertexLabel("person")
              .properties("name", "age", "city")
              .primaryKeys("name")
              .nullableKeys("city")
              .ifNotExist()
              .create();

        schema.vertexLabel("software")
              .properties("name", "lang", "price")
              .primaryKeys("name")
              .nullableKeys("price")
              .ifNotExist()
              .create();
    }

    protected static void initEdgeLabel() {
        SchemaManager schema = schema();

        schema.edgeLabel("knows")
              .sourceLabel("person")
              .targetLabel("person")
              .properties("date", "city")
              .nullableKeys("city")
              .ifNotExist()
              .create();

        schema.edgeLabel("created")
              .sourceLabel("person")
              .targetLabel("software")
              .properties("date", "city")
              .nullableKeys("city")
              .ifNotExist()
              .create();
    }

    protected static void initVertex() {
        graph().addVertex(T.label, "person", "name", "marko",
                          "age", 29, "city", "Beijing");
        graph().addVertex(T.label, "person", "name", "vadas",
                          "age", 27, "city", "Hongkong");
        graph().addVertex(T.label, "software", "name", "lop",
                          "lang", "java", "price", 328);
        graph().addVertex(T.label, "person", "name", "josh",
                          "age", 32, "city", "Beijing");
        graph().addVertex(T.label, "software", "name", "ripple",
                          "lang", "java", "price", 199);
        graph().addVertex(T.label, "person", "name", "peter",
                          "age", 29, "city", "Shanghai");
    }

    protected static void initEdge() {
        graph().addEdge("person:marko", "knows", "person:vadas",
                        "date", "20160110");
        graph().addEdge("person:marko", "knows", "person:josh",
                        "date", "20130220");
        graph().addEdge("person:marko", "created", "software:lop",
                        "date", "20171210", "city", "Shanghai");
        graph().addEdge("person:josh", "created", "software:ripple",
                        "date", "20171210", "city", "Beijing");
        graph().addEdge("person:josh", "created", "software:lop",
                        "date", "20091111", "city", "Beijing");
        graph().addEdge("person:peter", "created", "software:lop",
                        "date", "20170324", "city", "Hongkong");
    }

    protected List<Vertex> create100PersonBatch() {
        List<Vertex> vertices = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            Vertex vertex = new Vertex("person");
            vertex.property("name", "Person" + "-" + i);
            vertex.property("city", "Beijing");
            vertex.property("age", 30);
            vertices.add(vertex);
        }
        return vertices;
    }

    protected List<Vertex> create50SoftwareBatch() {
        List<Vertex> vertices = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            Vertex vertex = new Vertex("software");
            vertex.property("name", "Software" + "-" + i);
            vertex.property("lang", "java");
            vertex.property("price", 328);
            vertices.add(vertex);
        }
        return vertices;
    }

    protected List<Edge> create50CreatedBatch() {
        List<Edge> edges = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            Edge edge = new Edge("created");
            edge.sourceLabel("person");
            edge.targetLabel("software");
            edge.source("person:Person-" + i);
            edge.target("software:Software-" + i);
            edge.property("date", "20170324");
            edge.property("city", "Hongkong");
            edges.add(edge);
        }
        return edges;
    }

    protected List<Edge> create50KnowsBatch() {
        List<Edge> edges = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            Edge edge = new Edge("knows");
            edge.sourceLabel("person");
            edge.targetLabel("person");
            edge.source("person:Person-" + i);
            edge.target("person:Person-" + (i + 50));
            edge.property("date", "20170324");
            edges.add(edge);
        }
        return edges;
    }
}
