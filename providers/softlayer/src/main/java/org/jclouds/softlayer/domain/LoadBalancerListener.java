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

import java.util.Date;

/**
 * Class LoadBalancerListener
 *
 * @see <a href= "https://sldn.softlayer.com/reference/datatypes/softlayer_network_lbaas_listener" />
 */

@AutoValue
public abstract class LoadBalancerListener {

   @Nullable
   public abstract Integer connectionLimit();
   public abstract Date createDate();
   @Nullable
   public abstract Date modifyDate();
   public abstract String protocol();
   public abstract int protocolPort();
   public abstract String provisioningStatus();
   @Nullable
   public abstract Integer tlsCertificateId();
   public abstract String uuid();
   public abstract long id();
   public abstract LoadBalancerListenerDefaultPool defaultPool();

   @SerializedNames({"connectionLimit", "createDate", "modifyDate", "protocol", "protocolPort,", "provisioningStatus",
                     "tlsCertificateId", "uuid", "id", "defaultPool"})
   public static LoadBalancerListener create(Integer connectionLimit, Date createDate, Date modifyDate, String protocol,
                                              int protocolPort, String provisioningStatus, Integer tlsCertificateId,
                                              String uuid, long id, LoadBalancerListenerDefaultPool defaultPool) {
      return new AutoValue_LoadBalancerListener(connectionLimit, createDate, modifyDate, protocol, protocolPort,
                                                 provisioningStatus, tlsCertificateId, uuid, id, defaultPool);
   }

   LoadBalancerListener() {}

   @AutoValue
   public abstract static class UpdateLoadBalancerListener {
      public abstract int backendPort();
      public abstract String backendProtocol();
      public abstract int frontendPort();
      public abstract String frontendProtocol();
      public abstract String loadBalancingMethod();
      public abstract int maxConn();
      public abstract String listenerUuid();

      @SerializedNames({"backendPort", "backendProtocol", "frontendPort", "frontendProtocol", "loadBalancingMethod", "maxConn", "listenerUuid"})
      private static UpdateLoadBalancerListener create(final int backendPort, final String backendProtocol, final int frontendPort,
                                                       final String frontendProtocol, final String loadBalancingMethod, final int maxConn,
                                                       final String listenerUuid) {
         return builder()
                 .backendPort(backendPort)
                 .backendProtocol(backendProtocol)
                 .frontendPort(frontendPort)
                 .frontendProtocol(frontendProtocol)
                 .loadBalancingMethod(loadBalancingMethod)
                 .maxConn(maxConn)
                 .listenerUuid(listenerUuid)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_LoadBalancerListener_UpdateLoadBalancerListener.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder backendPort(int backendPort);
         public abstract Builder backendProtocol(String backendProtocol);
         public abstract Builder frontendPort(int frontendPort);
         public abstract Builder frontendProtocol(String frontendProtocol);
         public abstract Builder loadBalancingMethod(String loadBalancingMethod);
         public abstract Builder maxConn(int maxConn);
         public abstract Builder listenerUuid(String listenerUuid);

         abstract UpdateLoadBalancerListener autoBuild();

         public UpdateLoadBalancerListener build() {
            return autoBuild();
         }
      }
   }
}

