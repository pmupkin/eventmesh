package org.apache.eventmesh.connector.milvus.source.connector;

import org.apache.eventmesh.openconnect.api.config.Config;
import org.apache.eventmesh.openconnect.api.connector.ConnectorContext;
import org.apache.eventmesh.openconnect.api.connector.SourceConnectorContext;
import org.apache.eventmesh.openconnect.api.source.Source;
import org.apache.eventmesh.openconnect.offsetmgmt.api.data.ConnectRecord;
import org.apache.eventmesh.openconnect.util.CloudEventUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.cloudevents.CloudEvent;

//把event从数据库中拿出来，然后保存到cloudrecord中。
public class MilvusSourceConnector  implements Source {

    private MongodbSourceConfig sourceConfig;

    private static final int DEFAULT_BATCH_SIZE = 10;

    private BlockingQueue<CloudEvent> queue;

    private MongodbSourceClient client;

    final Integer SEARCH_K = 2;                       // TopK
    final String SEARCH_PARAM = "{\"nprobe\":10, \"offset\":5}";    // Params


    @Override
    public Class<? extends Config> configClass() {
        return MongodbSourceConfig.class;
    }

    @Override
    public void init(Config config) throws Exception {
        this.sourceConfig = (MongodbSourceConfig) config;
        doInit();
    }

    @Override
    public void init(ConnectorContext connectorContext) throws Exception {
        SourceConnectorContext sourceConnectorContext = (SourceConnectorContext) connectorContext;
        this.sourceConfig = (MongodbSourceConfig) sourceConnectorContext.getSourceConfig();
        doInit();
    }

    private void doInit() {
        this.queue = new LinkedBlockingQueue<>(1000);
        String connectorType = sourceConfig.getConnectorConfig().getConnectorType();
        if (connectorType.equals(ClusterType.STANDALONE.name())) {
            this.client = new MongodbStandaloneSourceClient(sourceConfig.getConnectorConfig(), queue);
        }
        if (connectorType.equals(ClusterType.REPLICA_SET.name())) {
            this.client = new MongodbReplicaSetSourceClient(sourceConfig.getConnectorConfig(), queue);
        }
        client.init();
    }

    @Override
    public void start() throws Exception {
        this.client.start();
    }

    @Override
    public void commit(ConnectRecord record) {

    }

    @Override
    public String name() {
        return this.sourceConfig.connectorConfig.getConnectorName();
    }

    @Override
    public void stop() throws Exception {
        this.client.stop();
    }

    @Override
    public List<ConnectRecord> poll() {
        List<ConnectRecord> connectRecords = new ArrayList<>(DEFAULT_BATCH_SIZE);
        for (int count = 0; count < DEFAULT_BATCH_SIZE; ++count) {
            try {
                CloudEvent event = queue.poll(3, TimeUnit.SECONDS);
                if (event == null) {
                    break;
                }

                connectRecords.add(CloudEventUtil.convertEventToRecord(event));
            } catch (InterruptedException e) {
                break;
            }
        }
        return connectRecords;
    }
}