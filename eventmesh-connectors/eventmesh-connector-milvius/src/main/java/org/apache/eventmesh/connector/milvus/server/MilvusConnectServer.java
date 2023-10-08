package org.apache.eventmesh.connector.milvus.server;

import org.apache.eventmesh.connector.milvus.config.MilvusServerConfig;
import org.apache.eventmesh.connector.milvus.sink.connector.MilvusSinkConnector;
import org.apache.eventmesh.connector.milvus.source.connector.MilvusSourceConnector;
import org.apache.eventmesh.openconnect.Application;
import org.apache.eventmesh.openconnect.util.ConfigUtil;

public class MilvusConnectServer {
    public static void main(String[] args) throws Exception {

        MilvusServerConfig serverConfig = ConfigUtil.parse(MilvusServerConfig.class, "server-config.yml");

        if (serverConfig.isSourceEnable()) {
            Application milvusSourceApp = new Application();
            milvusSourceApp.run(MilvusSourceConnector.class);
        }

        if (serverConfig.isSinkEnable()) {
            Application milvusSinkApp = new Application();
            milvusSinkApp.run(MilvusSinkConnector.class);
        }
    }
}
