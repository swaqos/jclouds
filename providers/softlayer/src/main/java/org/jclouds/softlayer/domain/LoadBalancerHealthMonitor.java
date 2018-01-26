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
 * Class LoadBaancerHealthMonitor
 *
 * @see <a href= "https://sldn.softlayer.com/reference/datatypes/softlayer_network_lbaas_healthmonitor" />
 */


@AutoValue
public abstract class LoadBalancerHealthMonitor {

   public abstract Date createDate();
   public abstract int interval();
   public abstract int maxRetries();
   @Nullable
   public abstract Date modifyDate();
   public abstract String monitorType();
   public abstract String provisioningStatus();
   public abstract int timeout();
   @Nullable
   public abstract String urlPath();
   public abstract String uuid();
   public abstract long id();

   @SerializedNames({"createDate", "interval", "maxRetries", "modifyDate", "monitorType", "provisioningStatus",
                     "timeout", "urlPath", "uuid", "id"})
   public static LoadBalancerHealthMonitor create(final Date createDate, int interval, int maxRetries, Date modifyDate,
                                                  String monitorType, String provisioningStatus, int timeout, String urlPath,
                                                  String uuid, long id) {
      return new AutoValue_LoadBalancerHealthMonitor(createDate, interval, maxRetries, modifyDate, monitorType, provisioningStatus,
                                                      timeout, urlPath, uuid, id);
   }

   LoadBalancerHealthMonitor() {}

   @AutoValue
   public abstract static class UpdateLoadBalancerHealthMonitor {
      public abstract String healthMonitorUuid();
      public abstract int interval();
      public abstract int timeout();
      public abstract String backendProtocol();
      public abstract int backendPort();
      public abstract int maxRetries();

      @SerializedNames({"healthMonitorUuid", "interval", "timeout", "backendProtocol", "backendPort", "maxRetries"})
      private static UpdateLoadBalancerHealthMonitor create(final String healthMonitorUuid, final int interval, final int timeout,
                                                            final String backendProtocol, final int backendPort, final int maxRetries) {
         return builder()
                 .healthMonitorUuid(healthMonitorUuid)
                 .interval(interval)
                 .timeout(timeout)
                 .backendProtocol(backendProtocol)
                 .backendPort(backendPort)
                 .maxRetries(maxRetries)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_LoadBalancerHealthMonitor_UpdateLoadBalancerHealthMonitor.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {
         public abstract Builder healthMonitorUuid(String healthMonitorUuid);
         public abstract Builder interval(int interval);
         public abstract Builder timeout(int timeout);
         public abstract Builder backendProtocol(String backendProcotol);
         public abstract Builder backendPort(int backendPort);
         public abstract Builder maxRetries(int maxRetries);

         abstract UpdateLoadBalancerHealthMonitor autoBuild();

         public UpdateLoadBalancerHealthMonitor build() {
            return autoBuild();
         }
      }
   }
}
