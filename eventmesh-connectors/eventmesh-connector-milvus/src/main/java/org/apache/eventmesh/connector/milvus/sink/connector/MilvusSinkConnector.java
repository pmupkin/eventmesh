/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.eventmesh.connector.milvus.sink.connector;

import org.apache.eventmesh.connector.milvus.sink.config.MilvusSinkConfig;
import org.apache.eventmesh.openconnect.api.config.Config;
import org.apache.eventmesh.openconnect.api.connector.ConnectorContext;
import org.apache.eventmesh.openconnect.api.connector.SinkConnectorContext;
import org.apache.eventmesh.openconnect.api.sink.Sink;
import org.apache.eventmesh.openconnect.offsetmgmt.api.data.ConnectRecord;
import org.apache.eventmesh.openconnect.util.CloudEventUtil;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.cloudevents.CloudEvent;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.dml.InsertParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MilvusSinkConnector implements Sink {

    private MilvusSinkConfig sinkConfig;

    private MilvusServiceClient client;

    @Override
    public Class<? extends Config> configClass() {
        return MilvusSinkConfig.class;
    }

    @Override
    public void init(Config config) throws Exception {
        if (!(config instanceof MilvusSinkConfig)) {
            throw new IllegalArgumentException("Config not be MilvusSinkConfig");
        }
        this.sinkConfig = (MilvusSinkConfig) config;
        doInit();
    }

    @Override
    public void init(ConnectorContext connectorContext) throws Exception {
        SinkConnectorContext sinkConnectorContext = (SinkConnectorContext) connectorContext;
        this.sinkConfig = (MilvusSinkConfig) sinkConnectorContext.getSinkConfig();
        doInit();
    }

    private void doInit() {
        this.client = new MilvusServiceClient(
            ConnectParam.newBuilder()
                .withHost(this.sinkConfig.getSinkConnectorConfig().getHost())
                .withPort(this.sinkConfig.getSinkConnectorConfig().getPort())
                .build()
        );
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void commit(ConnectRecord record) {

    }

    @Override
    public String name() {
        return this.sinkConfig.getSinkConnectorConfig().getConnectorName();
    }

    @Override
    public void stop() throws Exception {
        this.client.close();
    }

    @Override
    public void put(List<ConnectRecord> sinkRecords) {
        try {
            for (ConnectRecord connectRecord : sinkRecords) {
                CloudEvent event = CloudEventUtil.convertRecordToEvent(connectRecord);

                List<InsertParam.Field> fields = new ArrayList<>();
                fields.add(new InsertParam.Field("id", Collections.singletonList(event.getId())));
                fields.add(new InsertParam.Field("data", (List<?>) event.getData()));

                InsertParam insertParam = InsertParam.newBuilder()
                    .withCollectionName(this.sinkConfig.getSinkConnectorConfig().getCollection())
                    .withPartitionName(this.sinkConfig.getSinkConnectorConfig().getPartition())
                    .withFields(fields)
                    .build();
                client.insert(insertParam);


                log.debug("Produced message to event:{}}", event);
            }
        } catch (Exception e) {
            log.error("Failed to produce message:{}", e.getMessage());
        }
    }
}
