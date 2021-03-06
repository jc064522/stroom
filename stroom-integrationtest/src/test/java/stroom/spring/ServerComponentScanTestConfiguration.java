/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.spring;

import stroom.util.logging.StroomLogger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import stroom.cluster.server.ClusterNodeManagerImpl;
import stroom.node.server.NodeConfigImpl;
import stroom.streamtask.server.StreamProcessorTaskFactory;

/**
 * Configures the context for core integration tests.
 *
 * Reuses production configurations but defines its own component scan.
 *
 * This configuration relies on @ActiveProfile(StroomSpringProfiles.PROD) being
 * applied to the tests.
 */
/**
 * Exclude other configurations that might be found accidentally during a
 * component scan as configurations should be specified explicitly.
 */
@Configuration
@ComponentScan(basePackages = { "stroom" }, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),

        // Exclude these so we get the mocks instead.
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NodeConfigImpl.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ClusterNodeManagerImpl.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = StreamProcessorTaskFactory.class) })
public class ServerComponentScanTestConfiguration {
    private static final StroomLogger LOGGER = StroomLogger.getLogger(ServerComponentScanTestConfiguration.class);

    public ServerComponentScanTestConfiguration() {
        LOGGER.info("CoreConfiguration loading...");
    }
}
