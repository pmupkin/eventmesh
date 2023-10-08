package org.apache.eventmesh.connector.milvus.sink.config;

import org.apache.eventmesh.openconnect.api.config.SinkConfig;

import lombok.Data;

@Data
public class MilvusSinkConfig extends SinkConfig {
    private SinkConnectorConfig sinkConnectorConfig;
}
