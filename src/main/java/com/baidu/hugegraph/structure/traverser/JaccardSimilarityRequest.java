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

package com.baidu.hugegraph.structure.traverser;

import com.baidu.hugegraph.api.traverser.TraversersAPI;
import com.baidu.hugegraph.structure.constant.Traverser;
import com.baidu.hugegraph.util.E;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JaccardSimilarityRequest {

    @JsonProperty("vertex")
    private Object vertex;
    @JsonProperty("step")
    public EdgeStep step;
    @JsonProperty("top")
    public int top = 10;
    @JsonProperty("capacity")
    public long capacity = Traverser.DEFAULT_CAPACITY;

    private JaccardSimilarityRequest() {
        this.vertex = null;
        this.step = null;
        this.top = 10;
        this.capacity = Traverser.DEFAULT_CAPACITY;
    }

    @Override
    public String toString() {
        return String.format("JaccardSimilarityRequest{vertex=%s,step=%s," +
                             "top=%s,capacity=%s}",
                             this.vertex, this.step, this.top, this.capacity);
    }

    public static class Builder {

        private JaccardSimilarityRequest request;
        private EdgeStep.Builder stepBuilder;

        public Builder() {
            this.request = new JaccardSimilarityRequest();
            this.stepBuilder = new EdgeStep.Builder();
        }

        public Builder verteex(Object vertex) {
            E.checkNotNull(vertex, "vertex");
            this.request.vertex = vertex;
            return this;
        }

        public EdgeStep.Builder step() {
            EdgeStep.Builder builder = new EdgeStep.Builder();
            this.stepBuilder = builder;
            return builder;
        }

        public Builder top(int top) {
            TraversersAPI.checkPositive(top, "top");
            this.request.top = top;
            return this;
        }

        public Builder capacity(long capacity) {
            TraversersAPI.checkCapacity(capacity);
            this.request.capacity = capacity;
            return this;
        }

        public JaccardSimilarityRequest build() {
            E.checkArgument(this.request.vertex != null,
                            "The vertex can't be null");
            this.request.step = this.stepBuilder.build();
            E.checkNotNull(this.request.step, "step");
            TraversersAPI.checkCapacity(this.request.capacity);
            E.checkArgument(this.request.top >= 0,
                            "The top must be >= 0, but got: %s",
                            this.request.top);
            return this.request;
        }
    }
}
