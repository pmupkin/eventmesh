package org.apache.eventmesh.connector.milvus.source.config;

import org.apache.eventmesh.openconnect.api.config.SourceConfig;

import lombok.Data;

@Data
public class MilvusSourceConfig  extends SourceConfig {

    public SourceConnectorConfig connectorConfig;
}