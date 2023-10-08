package org.apache.eventmesh.connector.milvus.config;

import lombok.Data;

@Data
public class MilvusServerConfig {
    private boolean sourceEnable;
    private boolean sinkEnable;
}
