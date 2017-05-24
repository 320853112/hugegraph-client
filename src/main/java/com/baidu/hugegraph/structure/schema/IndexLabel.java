package com.baidu.hugegraph.structure.schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.exception.ClientException;
import com.baidu.hugegraph.structure.SchemaElement;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.constant.IndexType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by liningrui on 2017/5/16.
 */
@JsonIgnoreProperties({"id", "properties"})
public class IndexLabel extends SchemaElement {

    @JsonProperty
    private HugeType baseType;
    @JsonProperty
    private String baseValue;
    @JsonProperty
    private IndexType indexType;
    @JsonProperty
    private Set<String> fields;

    @JsonCreator
    public IndexLabel(@JsonProperty("name") String name) {
        super(name);
        this.indexType = IndexType.SECONDARY;
        this.fields = new HashSet<String>();
    }

    @Override
    public String type() {
        return HugeType.INDEX_LABEL.string();
    }

    public HugeType baseType() {
        return baseType;
    }

    public IndexLabel baseType(HugeType baseType) {
        this.baseType = baseType;
        return this;
    }

    public String baseValue() {
        return baseValue;
    }

    public IndexLabel baseValue(String baseValue) {
        this.baseValue = baseValue;
        return this;
    }

    public IndexType indexType() {
        return indexType;
    }

    public IndexLabel indexType(IndexType indexType) {
        this.indexType = indexType;
        return this;
    }

    public Set<String> indexFields() {
        return fields;
    }

    public IndexLabel indexFields(Set<String> fields) {
        this.fields = fields;
        return this;
    }

    @Override
    public String toString() {
        return String.format("{name=%s, baseType=%s, baseValue=%s, "
                        + "indexType=%s, fields=%s}",
                this.name,
                this.baseType,
                this.baseValue,
                this.indexType,
                this.fields);
    }

    public static class Builder {

        private IndexLabel indexLabel;
        private SchemaManager manager;

        public Builder(String name, SchemaManager manager) {
            this.indexLabel = new IndexLabel(name);
            this.manager = manager;
        }

        public IndexLabel create() {
            this.updateSchemaIndexName();
            this.manager.addIndexLabel(this.indexLabel);
            return this.indexLabel;
        }

        public void updateSchemaIndexName() {
            HugeType baseType = this.indexLabel.baseType();
            String baseValue = this.indexLabel.baseValue();
            switch (baseType) {
                case VERTEX_LABEL:
                    VertexLabel vertexLabel = this.manager.getVertexLabel
                            (baseValue);
                    vertexLabel.indexNames(this.indexLabel.name());
                    this.manager.addVertexLabel(vertexLabel);
                    break;
                case EDGE_LABEL:
                    EdgeLabel edgeLabel = this.manager.getEdgeLabel(baseValue);
                    edgeLabel.indexNames(this.indexLabel.name());
                    this.manager.addEdgeLabel(edgeLabel);
                    break;
                case PROPERTY_KEY:
                    break;
                default:
                    throw new ClientException(String.format(
                            "Can not update index name of schema type: %s",
                            baseType));
            }
        }

        public Builder on(VertexLabel vertexLabel) {
            this.indexLabel.baseType = HugeType.VERTEX_LABEL;
            this.indexLabel.baseValue = vertexLabel.name();
            return this;
        }

        public Builder on(EdgeLabel edgeLabel) {
            this.indexLabel.baseType = HugeType.EDGE_LABEL;
            this.indexLabel.baseValue = edgeLabel.name();
            return this;
        }

        public Builder by(String... indexFields) {
            this.indexLabel.fields.addAll(Arrays.asList(indexFields));
            return this;
        }

        public Builder secondary() {
            this.indexLabel.indexType = IndexType.SECONDARY;
            return this;
        }

        public Builder search() {
            this.indexLabel.indexType = IndexType.SEARCH;
            return this;
        }
    }
}
