package com.baidu.hugegraph.api.graph;

import java.util.List;

import javax.ws.rs.core.MultivaluedHashMap;

import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.client.RestResult;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.graph.Vertex;
import com.google.common.collect.ImmutableMap;

/**
 * Created by liningrui on 2017/5/23.
 */
public class VertexAPI extends GraphAPI {

    public VertexAPI(RestClient client, String graph) {
        super(client, graph);
    }

    @Override
    public String type() {
        return HugeType.VERTEX.string();
    }

    public Vertex create(Vertex vertex) {
        RestResult result = this.client.post(path(), vertex);
        return result.readObject(Vertex.class);
    }

    public List<String> create(List<Vertex> vertices) {
        MultivaluedHashMap headers = new MultivaluedHashMap();
        headers.putSingle("Content-Encoding", this.BATCH_ENCODING);
        RestResult result = this.client.post(batchPath(), vertices, headers);
        return result.readList(String.class);
    }

    public Vertex get(String name) {
        RestResult result = this.client.get(path(), name);
        return result.readObject(Vertex.class);
    }

    public List<Vertex> list(int limit) {
        RestResult result = this.client.get(path(),
                ImmutableMap.of("limit", limit));
        return result.readList(type(), Vertex.class);
    }

    public void delete(String name) {
        this.client.delete(path(), name);
    }
}
