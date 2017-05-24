package com.baidu.hugegraph.structure.graph;

import java.util.HashMap;
import java.util.Map;

import com.baidu.hugegraph.driver.GraphManager;
import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.structure.GraphElement;
import com.baidu.hugegraph.structure.constant.T;
import com.baidu.hugegraph.structure.schema.VertexLabel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 * Created by liningrui on 2017/5/11.
 */
public class Vertex extends GraphElement {

    // Hold a graphManager object for addEdge().
    private GraphManager manager;

    @JsonCreator
    public Vertex(@JsonProperty("label") String label) {
        this.label = label;
        this.properties = new HashMap<>();
    }

    public Edge addEdge(String label, Vertex vertex, Object... properties) {
        Preconditions.checkNotNull(label,
                "The edge label can not be null.");
        Preconditions.checkNotNull(vertex,
                "The target vertex can not be null.");
        return this.manager.addEdge(this, label, vertex, properties);
    }

    public void manager(GraphManager manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return String.format("{label=%s, properties=%s}",
                this.label,
                this.properties);
    }
}
