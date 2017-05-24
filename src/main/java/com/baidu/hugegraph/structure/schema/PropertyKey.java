package com.baidu.hugegraph.structure.schema;

import java.util.Set;

import com.baidu.hugegraph.driver.SchemaManager;
import com.baidu.hugegraph.structure.SchemaElement;
import com.baidu.hugegraph.structure.constant.Cardinality;
import com.baidu.hugegraph.structure.constant.DataType;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by liningrui on 2017/5/20.
 */
public class PropertyKey extends SchemaElement {

    @JsonProperty
    private DataType dataType;
    @JsonProperty
    private Cardinality cardinality;

    @JsonCreator
    public PropertyKey(@JsonProperty("name") String name) {
        super(name);
        this.dataType = DataType.TEXT;
        this.cardinality = Cardinality.SINGLE;
    }

    @Override
    public String type() {
        return HugeType.PROPERTY_KEY.string();
    }

    public DataType dataType() {
        return this.dataType;
    }

    public PropertyKey dataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public Cardinality cardinality() {
        return this.cardinality;
    }

    public PropertyKey cardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
        return this;
    }

    protected PropertyKey properties(Set<String> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public String toString() {
        return String.format("{name=%s, cardinality=%s, "
                        + "dataType=%s, properties=%s}",
                this.name,
                this.cardinality,
                this.dataType,
                this.properties);
    }

    public static class Builder {

        private PropertyKey propertyKey;
        private SchemaManager manager;

        public Builder(String name, SchemaManager manager) {
            this.propertyKey = new PropertyKey(name);
            this.manager = manager;
        }

        public PropertyKey create() {
            this.manager.addPropertyKey(this.propertyKey);
            return this.propertyKey;
        }

        public Builder asText() {
            this.propertyKey.dataType = DataType.TEXT;
            return this;
        }

        public Builder asInt() {
            this.propertyKey.dataType = DataType.INT;
            return this;
        }

        public Builder asTimestamp() {
            this.propertyKey.dataType = DataType.TIMESTAMP;
            return this;
        }

        public Builder asUuid() {
            this.propertyKey.dataType = DataType.UUID;
            return this;
        }

        public Builder asBoolean() {
            this.propertyKey.dataType = DataType.BOOLEAN;
            return this;
        }

        public Builder asByte() {
            this.propertyKey.dataType = DataType.BYTE;
            return this;
        }

        public Builder asBlob() {
            this.propertyKey.dataType = DataType.BLOB;
            return this;
        }

        public Builder asDouble() {
            this.propertyKey.dataType = DataType.DOUBLE;
            return this;
        }

        public Builder asFloat() {
            this.propertyKey.dataType = DataType.FLOAT;
            return this;
        }

        public Builder asLong() {
            this.propertyKey.dataType = DataType.LONG;
            return this;
        }

        public Builder valueSingle() {
            this.propertyKey.cardinality = Cardinality.SINGLE;
            return this;
        }

        public Builder valueList() {
            this.propertyKey.cardinality = Cardinality.LIST;
            return this;
        }

        public Builder valueSet() {
            this.propertyKey.cardinality = Cardinality.SET;
            return this;
        }
    }
}
