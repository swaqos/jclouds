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

import java.util.List;
import java.util.Date;

/**
 * Class LoadBalancer
 *
 * @see <a href= "https://sldn.softlayer.com/reference/datatypes/SoftLayer_Network_LBaaS_LoadBalancer" />
 */


@AutoValue
public abstract class LoadBalancer {

   public abstract long id();
   public abstract long accountId();
   @Nullable
   public abstract String address();
   public abstract Date createDate();
   public abstract String description();
   public abstract int isPublic();
   public abstract long locationId();
   @Nullable
   public abstract Date modifyDate();
   public abstract String name();
   public abstract String operatingStatus();
   public abstract String provisioningStatus();
   public abstract int useSystemPublicIpPool();
   public abstract String uuid();
   public abstract Location datacenter();
   @Nullable
   public abstract List<LoadBalancerHealthMonitor> healthMonitors();
   @Nullable
   public abstract List<LoadBalancerListener> listeners();
   @Nullable
   public abstract List<LoadBalancerMember> members();

   @SerializedNames({"id", "accountId", "address", "createDate", "description", "isPublic", "locationId", "modifyDate", "name",
                     "operatingStatus", "provisioningStatus", "useSystemPublicIpPool", "uuid", "datacenter", "healthMonitors",
                     "listeners", "members"})
   public static LoadBalancer create(final long id, long accountId, String address, final Date createDate, String description, int isPublic,
                                     long locationId, Date modifyDate, String name, String operatingStatus, String provisioningStatus,
                                     int useSystemPublicIpPool, String uuid, Location datacenter, List<LoadBalancerHealthMonitor> healthMonitors,
                                     List<LoadBalancerListener> listeners, List<LoadBalancerMember> members) {
      return new AutoValue_LoadBalancer(id, accountId, address, createDate, description, isPublic, locationId, modifyDate, name, operatingStatus,
                                        provisioningStatus, useSystemPublicIpPool, uuid, datacenter, healthMonitors, listeners, members);
   }

   LoadBalancer() {
   }

   @AutoValue
   public abstract static class CreateLoadBalancer {

      public abstract String complexType();
      public abstract long packageId();
      public abstract List<LoadBalancerServicePrice> prices();
      public abstract String name();
      public abstract String description();
      public abstract List<LoadBalancerProtocolConfiguration> protocolConfigurations();
      @Nullable
      public abstract List<LoadBalancerServerInstanceInformation> serverInstancesInformation();
      public abstract List<LoadBalancerSubnetId> subnets();
      public abstract boolean useHourlyPricing();

      @SerializedNames({"complexType", "packageId", "prices", "name", "description", "protocolConfigurations",
                        "serverInstancesInformation", "subnets", "useHourlyPricing"})
      private static CreateLoadBalancer create(/*final String complexType, final long packageId, */final List<LoadBalancerServicePrice> prices,
                                               final String name, final String description,
                                               final List<LoadBalancerProtocolConfiguration> protocolConfigurations,
                                               final List<LoadBalancerServerInstanceInformation> serverInstancesInformation,
                                               final List<LoadBalancerSubnetId> subnets/*, final boolean useHourlyPricing*/) {
         return builder()
                 .complexType("SoftLayer_Container_Product_Order_Network_LoadBalancer_AsAService")
                 .packageId(805)
                 .prices(prices)
                 .name(name)
                 .description(description)
                 .protocolConfigurations(protocolConfigurations)
                 .serverInstancesInformation(serverInstancesInformation)
                 .subnets(subnets)
                 .useHourlyPricing(true)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_LoadBalancer_CreateLoadBalancer.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder complexType(String complexType);
         public abstract Builder packageId(long packageId);
         public abstract Builder prices(List<LoadBalancerServicePrice> prices);
         public abstract Builder name(String name);
         public abstract Builder description(String description);
         public abstract Builder protocolConfigurations(List<LoadBalancerProtocolConfiguration> protocolConfigurations);
         public abstract Builder serverInstancesInformation(List<LoadBalancerServerInstanceInformation> serverInstancesInformation);
         public abstract Builder subnets(List<LoadBalancerSubnetId> subnets);
         public abstract Builder useHourlyPricing(boolean useHourlyPricing);

         abstract CreateLoadBalancer autoBuild();

         public CreateLoadBalancer build() {
            return autoBuild();
         }
      }
   }
}

