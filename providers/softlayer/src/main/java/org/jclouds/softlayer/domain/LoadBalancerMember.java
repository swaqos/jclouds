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
 * Class LoadBalancerMember
 *
 * @see <a href= "https://sldn.softlayer.com/reference/datatypes/SoftLayer_Network_LBaaS_Member" />
 */

@AutoValue
public abstract class LoadBalancerMember {

   @Nullable
   public abstract String address();
   public abstract Date createDate();
   public abstract int id();
   @Nullable
   public abstract Date modifyDate();
   public abstract String provisiongStatus();
   public abstract String uuid();
   public abstract int weight();

   @SerializedNames({"address", "createDate", "id", "modifyDate", "provisioningStatus", "uuid", "weight"})
   public static LoadBalancerMember create(String address, Date createDate, int id, Date modifyDate,
                                           String provisioningStatus, String uuid, int weight) {
      return new AutoValue_LoadBalancerMember(address, createDate, id, modifyDate, provisioningStatus, uuid, weight);
   }

   LoadBalancerMember() {}

   @AutoValue
   public abstract static class AddLoadBalancerMember {

      public abstract String privateIpAddress();
      public abstract int weight();

      @SerializedNames({"privateIpAddress", "weight"})
      private static AddLoadBalancerMember create(final String privateIpAddress, final int weight) {
         return builder()
                 .privateIpAddress(privateIpAddress)
                 .weight(weight)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_LoadBalancerMember_AddLoadBalancerMember.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder privateIpAddress(String privateIpAddress);
         public abstract Builder weight(int weight);

         abstract AddLoadBalancerMember autoBuild();

         public AddLoadBalancerMember build() {
            return autoBuild();
         }
      }
   }

   @AutoValue
   public abstract static class UpdateLoadBalancerMember {

      public abstract String uuid();
      public abstract int weight();

      @SerializedNames({"uuid", "weight"})
      private static UpdateLoadBalancerMember create(final String uuid, final int weight) {
         return builder()
                 .uuid(uuid)
                 .weight(weight)
                 .build();
      }

      public static Builder builder() {
         return new AutoValue_LoadBalancerMember_UpdateLoadBalancerMember.Builder();
      }

      @AutoValue.Builder
      public abstract static class Builder {

         public abstract Builder uuid(String uuid);
         public abstract Builder weight(int weight);

         abstract UpdateLoadBalancerMember autoBuild();

         public UpdateLoadBalancerMember build() {
            return autoBuild();
         }
      }
   }
}

