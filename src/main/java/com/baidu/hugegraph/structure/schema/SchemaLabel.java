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

package com.baidu.hugegraph.structure.schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.baidu.hugegraph.structure.SchemaElement;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SchemaLabel extends SchemaElement {

    @JsonProperty
    protected Set<String> nullableKeys;
    @JsonProperty
    protected Set<String> indexNames;

    public SchemaLabel(String name) {
        super(name);
        this.nullableKeys = new HashSet<>();
        this.indexNames = new HashSet<>();
    }

    public Set<String> nullableKeys() {
        return this.nullableKeys;
    }

    public SchemaLabel nullableKeys(String... keys) {
        this.nullableKeys.addAll(Arrays.asList(keys));
        return this;
    }

    public Set<String> indexNames() {
        return this.indexNames;
    }

    public SchemaLabel indexNames(String... indexNames) {
        this.indexNames.addAll(Arrays.asList(indexNames));
        return this;
    }

}
