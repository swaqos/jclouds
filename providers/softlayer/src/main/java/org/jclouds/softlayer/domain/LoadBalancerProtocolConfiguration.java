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
package org.jclouds.softlayer.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;


/**
 * Class LoadBalancerProtocolConfiguration
 *
 * @see <a href= "https://sldn.softlayer.com/reference/datatypes/softlayer_network_lbaas_loadbalancerprotocolconfiguration" />
 */


@AutoValue
public abstract class LoadBalancerProtocolConfiguration {

   public abstract String frontendProtocol();
   public abstract int frontendPort();
   public abstract String backendProtocol();
   public abstract int backendPort();
   public abstract String loadBalancingMethod();
   public abstract int maxConn();
   @Nullable
   public abstract String listenerUuid();

   @SerializedNames({"frontendProtocol", "frontendPort", "backendProtocol", "backendPort", "loadBalancingMethod", "maxConn", "listenerUuid"})
   public static LoadBalancerProtocolConfiguration create(String frontendProtocol, int frontendPort, String backendProtocol,
                                                          int backendPort, String loadBalancingMethod, int maxConn, String listenerUuid) {
      return new AutoValue_LoadBalancerProtocolConfiguration(frontendProtocol, frontendPort, backendProtocol, backendPort, loadBalancingMethod,
              maxConn, listenerUuid);
   }

   LoadBalancerProtocolConfiguration() {}
}

