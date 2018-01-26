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
package org.jclouds.softlayer.features;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.LoadBalancer;
import org.jclouds.softlayer.domain.LoadBalancerMember;
import org.jclouds.softlayer.domain.LoadBalancerStatistics;
import org.jclouds.softlayer.domain.LoadBalancerHealthMonitor;
import org.jclouds.softlayer.domain.LoadBalancerListener;
import org.jclouds.softlayer.domain.LoadBalancerMemberPoolHealth;
import org.jclouds.softlayer.domain.LoadBalancerServicePrice;
import org.jclouds.softlayer.domain.LoadBalancerProtocolConfiguration;
import org.jclouds.softlayer.domain.LoadBalancerServerInstanceInformation;
import org.jclouds.softlayer.domain.LoadBalancerSubnetId;
import org.jclouds.softlayer.domain.Location;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code LoadBalancerApi}
 */
@Test(groups = "live")
public class LoadBalancerApiLiveTest extends BaseSoftLayerApiLiveTest {

   private LoadBalancerApi loadBalancerApi;
   private LoadBalancer loadBalancer = null;
   private Datacenter datacenter = null;
   private LoadBalancerMember loadBalancerMember = null;
   private LoadBalancerHealthMonitor loadBalancerHealthMonitor = null;
   private LoadBalancerListener loadBalancerListener = null;

   //load balancer creation config
   private int subnetId = -1;
   private int servicePrices[] = {199445, 199465, 205837, 205975};
   private String testInstancePrivateIp = "";

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();

      loadBalancerApi = api.getLoadBalancerApi();

      datacenter = Iterables.get(api.getDatacenterApi().listDatacenters(), 0);
      assertNotNull(datacenter, "Datacenter must not be null");
   }

   @Test
   public void testCreateLoadBalancer() throws Exception {
      assertTrue(subnetId != -1, "Please set the subnetId");
      assertTrue(!testInstancePrivateIp.equals(""), "Please set the testInstancePrivateIp for Member functions testing");
      loadBalancer = LoadBalancer.create(0, 0, "", new Date(), "", 0, 0,
              new Date(), "", "", "", 0,
              "", Location.create(0, "", "", 0), null, null, null);

      LoadBalancer.CreateLoadBalancer createLoadBalancer1 = LoadBalancer.CreateLoadBalancer.builder()
              .complexType("SoftLayer_Container_Product_Order_Network_LoadBalancer_AsAService")
              .packageId(805)
              .prices(new ArrayList<LoadBalancerServicePrice>(Arrays.asList(LoadBalancerServicePrice.create(servicePrices[0]),
                                                                            LoadBalancerServicePrice.create(servicePrices[1]),
                                                                            LoadBalancerServicePrice.create(servicePrices[2]),
                                                                            LoadBalancerServicePrice.create(servicePrices[3]))))
              .name("testNameFromJclouds1")
              .description("This is a test description from jclouds api live tests")
              .protocolConfigurations(new ArrayList<LoadBalancerProtocolConfiguration>(Arrays.asList(LoadBalancerProtocolConfiguration.create(
                      "HTTP", 80, "HTTP", 80, "ROUNDROBIN", 2000, null
              ))))
              .serverInstancesInformation(Collections.<LoadBalancerServerInstanceInformation>emptyList())
              .subnets(new ArrayList<LoadBalancerSubnetId>(Arrays.asList(LoadBalancerSubnetId.create(subnetId))))
              .useHourlyPricing(true)
              .build();

      List<LoadBalancer.CreateLoadBalancer> parameters = new ArrayList<LoadBalancer.CreateLoadBalancer>();
      parameters.add(createLoadBalancer1);

      loadBalancerApi.createLoadBalancer(parameters);

      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            boolean isProvisioned = false;
            for (LoadBalancer tempLb : loadBalancerApi.listLoadBalancers()) {
               if (tempLb.name().equals("testNameFromJclouds1")) {
                  loadBalancer = tempLb;
                  isProvisioned = true;
                  break;
               }
            }
            return isProvisioned;
         }
      }, 3 * 60, 10, TimeUnit.SECONDS).apply(loadBalancer), "LoadBalancer service is still being created!");

      checkLoadBalancer(loadBalancer);

      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            lb = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());
            return checkProvisioningStatus(lb.provisioningStatus(), "ACTIVE");
         }
      }, 30, 2, TimeUnit.MINUTES).apply(loadBalancer), "LoadBalancer service is still being created!");
      System.out.println("LoadBalancer service is fully created and ready to be tested.");
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testListLoadBalancers() throws Exception {
      List<LoadBalancer> response = loadBalancerApi.listLoadBalancers();

      assertNotEquals(response, Collections.emptyList(), "There is no load balancers associated with the account");

      for (LoadBalancer lb : response) {
         checkLoadBalancer(lb);
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetLoadBalancer() throws Exception {
      LoadBalancer response = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());
      checkLoadBalancer(response);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetLoadBalancerObject() throws Exception {
      LoadBalancer response = loadBalancerApi.getLoadBalancerObject(loadBalancer.id());
      checkLoadBalancer(response);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetDatacenter() throws Exception {
      Location response = loadBalancerApi.getDatacenter(loadBalancer.uuid());
      checkDataCenter(response);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testUpdateLoadBalancer() throws Exception {
      List<String> parameters = new ArrayList<String>();

      parameters.add(loadBalancer.uuid());
      parameters.add("This is a new description from jclouds api live tests");

      LoadBalancer response = loadBalancerApi.updateLoadBalancer(parameters);
      checkLoadBalancer(response);
      assertEquals(response.description(), "This is a new description from jclouds api live tests");

      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            lb = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());
            return checkProvisioningStatus(lb.provisioningStatus(), "ACTIVE");
         }
      }, 30, 2, TimeUnit.MINUTES).apply(loadBalancer), "LoadBalancer service is still being updated!");

      loadBalancer = response;
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetHealthMonitors() throws Exception {
      LoadBalancerHealthMonitor response = loadBalancerApi.getHealthMonitors(loadBalancer.uuid());
      checkLoadBalancerHealthMonitor(response);
      loadBalancerHealthMonitor = response;
   }

   @Test(dependsOnMethods = "testGetHealthMonitors")
   public void testUpdateHealthMonitor() throws Exception {
      LoadBalancerHealthMonitor.UpdateLoadBalancerHealthMonitor updateLoadBalancerHealthMonitor1 =
              LoadBalancerHealthMonitor.UpdateLoadBalancerHealthMonitor.builder()
                      .healthMonitorUuid(loadBalancer.healthMonitors().get(0).uuid())
                      .backendPort(80)
                      .backendProtocol(loadBalancerHealthMonitor.monitorType())
                      .interval(10)
                      .timeout(5)
                      .maxRetries(5)
                      .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add(loadBalancer.uuid());
      parameters.add(ImmutableList.of(updateLoadBalancerHealthMonitor1));

      LoadBalancer response = loadBalancerApi.updateLoadBalancerHealthMonitor(parameters);

      loadBalancerHealthMonitor = response.healthMonitors().get(0);
      checkLoadBalancerHealthMonitor(loadBalancerHealthMonitor);
      assertEquals(loadBalancerHealthMonitor.monitorType(), "HTTP", "The monitorType was not updated correctly");
      assertEquals(loadBalancerHealthMonitor.interval(), 10, "The interval was not updated correctly");
      assertEquals(loadBalancerHealthMonitor.timeout(), 5, "The timeout was not updated correctly");
      assertEquals(loadBalancerHealthMonitor.maxRetries(), 5, "The maxRetries was not updated correctly");

      loadBalancer = response;
      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            lb = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());
            return checkProvisioningStatus(lb.provisioningStatus(), "ACTIVE") &&
                    checkProvisioningStatus(lb.healthMonitors().get(0).provisioningStatus(), "ACTIVE");
         }
      }, 300, 30, TimeUnit.SECONDS).apply(loadBalancer), "LoadBalancer service is still updating!");

   }

   @Test(dependsOnMethods = "testUpdateHealthMonitor")
   public void testGetHealthMonitor() throws Exception {
      LoadBalancerHealthMonitor response = loadBalancerApi.getHealthMonitor(loadBalancer.uuid());
      checkLoadBalancerHealthMonitor(response);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetLoadBalancerStatistics() throws Exception {
      LoadBalancerStatistics response = loadBalancerApi.getLoadBalancerStatistics(loadBalancer.uuid());
      checkLoadBalancerStatistics(response);
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testAddLoadBalancerMembers() throws Exception {
      LoadBalancerMember.AddLoadBalancerMember addLoadBalancerMember1 = LoadBalancerMember.AddLoadBalancerMember.builder()
              .privateIpAddress(testInstancePrivateIp)
              .weight(50)
              .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add(loadBalancer.uuid());
      parameters.add(ImmutableList.of(addLoadBalancerMember1));

      LoadBalancer response = loadBalancerApi.addLoadBalancerMembers(parameters);

      assertTrue(response.members().size() == 1);
      loadBalancerMember = response.members().get(0);

      assertEquals(loadBalancerMember.address(), testInstancePrivateIp);
      assertEquals(loadBalancerMember.weight(), 50);

      loadBalancer = response;
      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            lb = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());
            return checkProvisioningStatus(lb.provisioningStatus(), "ACTIVE");
         }
      }, 300, 30, TimeUnit.SECONDS).apply(loadBalancer), "LoadBalancer service is still updating!");
   }

   @Test(dependsOnMethods = "testAddLoadBalancerMembers")
   public void testGetLoadBalancerMembers() throws Exception {
      List<LoadBalancerMember> response = loadBalancerApi.getLoadBalancerMembers(loadBalancer.id());
      for (LoadBalancerMember lbm : response) {
         checkLoadBalancerMember(lbm);
      }
   }

   @Test(dependsOnMethods = "testAddLoadBalancerMembers")
   public void testGetLoadBalancerMemberHealth() throws Exception {
      List<LoadBalancerMemberPoolHealth> response = loadBalancerApi.getLoadBalancerMemberHealth(loadBalancer.uuid());

      for (LoadBalancerMemberPoolHealth lbmph : response) {
         assertEquals(lbmph.membersHealth().get(0).uuid(), loadBalancerMember.uuid());
      }
   }

   @Test(dependsOnMethods = "testAddLoadBalancerMembers")
   public void testGetLoadBalancerMember() throws Exception {
      LoadBalancerMember response = loadBalancerApi.getLoadBalancerMember(loadBalancerMember.id());
      checkLoadBalancerMember(response);
   }

   @Test(dependsOnMethods = "testAddLoadBalancerMembers")
   public void testUpdateLoadBalancerMembers() throws Exception {
      LoadBalancerMember.UpdateLoadBalancerMember updateLoadBalancerMember1 = LoadBalancerMember.UpdateLoadBalancerMember.builder()
              .uuid(loadBalancerMember.uuid())
              .weight(100)
              .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add(loadBalancer.uuid());
      parameters.add(ImmutableList.of(updateLoadBalancerMember1));

      LoadBalancer response = loadBalancerApi.updateLoadBalancerMembers(parameters);

      assertEquals(loadBalancerMember.address(), testInstancePrivateIp);
      assertEquals(loadBalancerMember.weight(), 50);

      loadBalancerMember = response.members().get(0);

      loadBalancer = response;
   }

   @Test(dependsOnMethods = "testUpdateLoadBalancerMembers")
   public void testDeleteLoadBalancerMembers() throws Exception {
      List<String> deleteLoadBalancerMembers = new ArrayList<String>();
      deleteLoadBalancerMembers.add(loadBalancerMember.uuid());

      List<Object> parameters = new ArrayList<Object>();

      parameters.add(loadBalancer.uuid());
      parameters.add(deleteLoadBalancerMembers);

      LoadBalancer response = loadBalancerApi.deleteLoadBalancerMembers(parameters);

      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            lb = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());
            return lb.members().size() == 0;
         }
      }, 180, 30, TimeUnit.SECONDS).apply(loadBalancer), "LoadBalancer member is still being deleted!");

      loadBalancer = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());

      assertTrue(loadBalancer.members().size() == 0);
      loadBalancerMember = null;
      loadBalancer = response;
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testGetLoadBalancerListeners() throws Exception {
      List<LoadBalancerListener> response = loadBalancerApi.getLoadBalancerListeners(loadBalancer.id());
      for (LoadBalancerListener lbl : response) {
         checkLoadBalancerListener(lbl);
      }
   }

   @Test(dependsOnMethods = "testGetLoadBalancerListeners")
   public void testAddLoadBalancerProtocols() throws Exception {
      List<LoadBalancerListener> oldListener = loadBalancerApi.getLoadBalancerListeners(loadBalancer.id());
      loadBalancerListener = oldListener.get(0);

      LoadBalancerProtocolConfiguration loadBalancerProtocolConfiguration1 = LoadBalancerProtocolConfiguration.create(
              "TCP", 90, "TCP", 9090, "ROUNDROBIN", 1000, null
              );

      List<Object> parameters = new ArrayList<Object>();

      parameters.add(loadBalancer.uuid());
      parameters.add(ImmutableList.of(loadBalancerProtocolConfiguration1));

      LoadBalancer response = loadBalancerApi.updateLoadBalancerProtocols(parameters);

      loadBalancer = response;

      for (LoadBalancerListener lbl : response.listeners()) {
         if (!lbl.uuid().equals(loadBalancerListener.uuid())) {
            loadBalancerListener = lbl;
            break;
         }
      }

      checkState(retry(new Predicate<LoadBalancerListener>() {
         public boolean apply(LoadBalancerListener lbl) {
            lbl = loadBalancerApi.getLoadBalancerListener(loadBalancerListener.id());
            return checkProvisioningStatus(lbl.provisioningStatus(), "ACTIVE");
         }
      }, 300, 30, TimeUnit.SECONDS).apply(loadBalancerListener), "LoadBalancer listener is still being created!");

      loadBalancerListener = loadBalancerApi.getLoadBalancerListener(loadBalancerListener.id());

      assertEquals(loadBalancerListener.protocol(), "TCP");
      checkLoadBalancerListener(loadBalancerListener);
   }

   @Test(dependsOnMethods = "testAddLoadBalancerProtocols")
   public void testUpdateLoadBalancerProtocols() throws Exception {
      LoadBalancerProtocolConfiguration loadBalancerProtocolConfiguration1 = LoadBalancerProtocolConfiguration.create(
              "HTTP", 81, "HTTP", 8081, "WEIGHTED_RR",
              500, loadBalancerListener.uuid()
      );

      List<Object> parameters = new ArrayList<Object>();

      parameters.add(loadBalancer.uuid());
      parameters.add(ImmutableList.of(loadBalancerProtocolConfiguration1));

      loadBalancer = loadBalancerApi.updateLoadBalancerProtocols(parameters);

      checkState(retry(new Predicate<LoadBalancerListener>() {
         public boolean apply(LoadBalancerListener lbl) {
            lbl = loadBalancerApi.getLoadBalancerListener(loadBalancerListener.id());
            return checkProvisioningStatus(lbl.provisioningStatus(), "ACTIVE");
         }
      }, 300, 30, TimeUnit.SECONDS).apply(loadBalancerListener), "LoadBalancer listener is still being updated!");

      loadBalancerListener = loadBalancerApi.getLoadBalancerListener(loadBalancerListener.id());

      assertEquals(loadBalancerListener.protocol(), "HTTP");
      assertEquals(loadBalancerListener.defaultPool().loadBalancingAlgorithm(), "WEIGHTED_RR");
      checkLoadBalancerListener(loadBalancerListener);

      checkState(retry(new Predicate<LoadBalancerListener>() {
         public boolean apply(LoadBalancerListener lbl) {
            lbl = loadBalancerApi.getLoadBalancerListener(loadBalancerListener.id());
            return checkProvisioningStatus(lbl.provisioningStatus(), "ACTIVE");
         }
      }, 300, 30, TimeUnit.SECONDS).apply(loadBalancerListener), "LoadBalancer listener is still being updated!");
   }

   @Test(dependsOnMethods = "testUpdateLoadBalancerProtocols")
   public void testGetLoadBalancerListener() throws Exception {
      LoadBalancerListener response = loadBalancerApi.getLoadBalancerListener(loadBalancerListener.id());
      checkLoadBalancerListener(response);
   }

   @Test(dependsOnMethods = "testGetLoadBalancerListener")
   public void testDeleteLoadBalancerListener() throws Exception {
      List<String> deleteLoadBalancerListeners = new ArrayList<String>();
      deleteLoadBalancerListeners.add(loadBalancerListener.uuid());

      List<Object> parameters = new ArrayList<Object>();

      parameters.add(loadBalancer.uuid());
      parameters.add(deleteLoadBalancerListeners);

      LoadBalancer response = loadBalancerApi.deleteLoadBalancerListeners(parameters);

      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            lb = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());
            return lb.listeners().size() == 1;
         }
      }, 180, 30, TimeUnit.SECONDS).apply(loadBalancer), "LoadBalancer listener is still being deleted!");

      loadBalancer = loadBalancerApi.getLoadBalancer(loadBalancer.uuid());

      assertTrue(loadBalancer.listeners().size() == 1);
      loadBalancerListener = null;
      loadBalancer = response;
   }

   @Test(dependsOnMethods = {"testDeleteLoadBalancerMembers", "testUpdateLoadBalancer", "testGetHealthMonitor", "testDeleteLoadBalancerListener"})
   public void testDeleteLoadBalancer() throws Exception {
      boolean response = loadBalancerApi.deleteLoadBalancer(loadBalancer.uuid());

      assertTrue(response, "deletion failed");

      checkState(retry(new Predicate<LoadBalancer>() {
         public boolean apply(LoadBalancer lb) {
            boolean isDeleted = true;
            for (LoadBalancer tempLb : loadBalancerApi.listLoadBalancers()) {
               if (tempLb.uuid().equals(loadBalancer.uuid())) {
                  isDeleted = false;
                  break;
               }
            }
            return isDeleted;
         }
      }, 20, 1, TimeUnit.MINUTES).apply(loadBalancer), "LoadBalancer service is still being deleted!");

      loadBalancer = null;
   }

   private boolean checkProvisioningStatus(String actualProvisioningStatus, String expectedProvisioningStatus) {
      return actualProvisioningStatus.equals(expectedProvisioningStatus);
   }

   private void checkLoadBalancer(LoadBalancer lb) {
      assertNotNull(lb.accountId(), "accountId must be not null");
      assertNotNull(lb.uuid(), "uuid must be not null");
      assertNotNull(lb.name(), "name must be not null");
      assertNotNull(lb.operatingStatus(), "operatingStatus must be not null");
      assertNotNull(lb.provisioningStatus(), "provisioningStatus must be not null");
      assertTrue(lb.locationId() > 0, "location id must be greater than 0");
      assertTrue(lb.id() > 0, "id must be greater than 0");

   }

   private void checkDataCenter(Location location) {
      assertNotNull(location.statudId(), "statusId must be not null");
      assertNotNull(location.name(), "name must be not null");
      assertTrue(location.id() > 0, "locationId must be greater than 0");
   }

   private void checkLoadBalancerHealthMonitor(LoadBalancerHealthMonitor lbhm) {
      assertNotNull(lbhm.uuid(), "uuid must be not null");
      assertNotNull(lbhm.monitorType(), "monitorType must be not null");
      assertNotNull(lbhm.provisioningStatus(), "provisioningStatus must be not null");
      assertTrue(lbhm.id() > 0, "id must be greater than 0");
      assertTrue(lbhm.interval() > 0, "interval must be greater than 0");
      assertTrue(lbhm.maxRetries() >= 0, "maxRetries must be greater than or equal to 0");
      assertEquals(lbhm.monitorType(), "HTTP", "monitorType was not set properly");
   }

   private void checkLoadBalancerStatistics(LoadBalancerStatistics lbs) {
      assertTrue(lbs.connectionRate() >= 0, "connectionRate must be greater than or equal to 0");
      assertTrue(lbs.dataProcessedByMonth() >= 0, "dataProcessedByMonth must be greater than or equal to 0");
      assertTrue(lbs.numberOfMemberDown() >= 0, "numberOfMemberDown must be greater than or equal to 0");
      assertTrue(lbs.numberOfMembersUp() >= 0, "numberOfMembersUp must be greater than or equal to 0");
      assertTrue(lbs.throughput() >= 0, "throughput must be greater than or equal to 0");
      assertTrue(lbs.totalConnections() >= 0, "totalConnections must be greater than or equal to 0");
   }

   private void checkLoadBalancerMember(LoadBalancerMember lbm) {
      assertNotNull(lbm.uuid(), "uuid must be not null");
      assertNotNull(lbm.address(), "address must be not null");
      assertNotNull(lbm.provisiongStatus(), "provisiongStatus must be not null");
      assertTrue(lbm.id() > 0, "id must be greater than 0");
      assertTrue(lbm.weight() >= 0, "weight must be greater than or equal to 0");
   }

   private void checkLoadBalancerListener(LoadBalancerListener lbl) {
      assertNotNull(lbl.uuid(), "uuid must be not null");
      assertNotNull(lbl.provisioningStatus(), "provisioningStatus must be not null");
      assertTrue(lbl.id() > 0, "id must be greater than 0");
   }
}
