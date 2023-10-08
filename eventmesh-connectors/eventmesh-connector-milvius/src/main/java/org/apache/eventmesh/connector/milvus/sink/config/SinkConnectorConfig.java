package org.apache.eventmesh.connector.milvus.sink.config;

import lombok.Data;

@Data
public class SinkConnectorConfig {
    private Integer port;
    private String host;
    private String connectorName;
    private String collection;
    private String partition;
}
