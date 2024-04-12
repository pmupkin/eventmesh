package org.apache.eventmesh.runtime.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.eventmesh.common.config.CommonConfiguration;
import org.apache.eventmesh.common.config.Config;
import org.apache.eventmesh.common.config.ConfigFiled;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Config(prefix = "eventMesh.server")
public class EventMeshWEBSOCKETConfiguration extends CommonConfiguration {

    @ConfigFiled(field = "websocket.port", notNull = true, beNumber = true)
    private int httpServerPort = 10405;


    @ConfigFiled(field = "useWss.enabled")
    private boolean eventMeshServerUseWss = false;
}
