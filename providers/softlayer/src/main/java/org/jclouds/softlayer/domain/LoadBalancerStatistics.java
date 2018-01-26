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
import org.jclouds.json.SerializedNames;


/**
 * Class LoadBalancerStatistics
 *
 * @see <a href= "https://sldn.softlayer.com/reference/datatypes/SoftLayer_Network_LBaaS_LoadBalancerStatistics" />
 */


@AutoValue
public abstract class LoadBalancerStatistics {

   public abstract int connectionRate();
   public abstract int dataProcessedByMonth();
   public abstract int numberOfMemberDown();
   public abstract int numberOfMembersUp();
   public abstract double throughput();
   public abstract int totalConnections();

   @SerializedNames({"connectionRate", "dataProcessedByMonth", "numberOfMemberDown", "numberOfMembersUp", "throughput,",
                     "totalConnections"})
   public static LoadBalancerStatistics create(int connectionRate, int dataProcessedByMonth, int numberOfMemberDown,
                                               int numberOfMemberUp, double throughput, int totalConnections) {
      return new AutoValue_LoadBalancerStatistics(connectionRate, dataProcessedByMonth, numberOfMemberDown, numberOfMemberUp,
                                                   throughput, totalConnections);
   }

   LoadBalancerStatistics() {}
}

