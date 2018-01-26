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

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.domain.LoadBalancer;
import org.jclouds.softlayer.domain.LoadBalancerMember;
import org.jclouds.softlayer.domain.LoadBalancerStatistics;
import org.jclouds.softlayer.domain.LoadBalancerHealthMonitor;
import org.jclouds.softlayer.domain.LoadBalancerListener;
import org.jclouds.softlayer.domain.Location;
import org.jclouds.softlayer.domain.LoadBalancerMemberPoolHealth;
import org.jclouds.softlayer.domain.LoadBalancerProtocolConfiguration;
import org.jclouds.softlayer.domain.LoadBalancerServerInstanceInformation;
import org.jclouds.softlayer.domain.LoadBalancerServicePrice;
import org.jclouds.softlayer.domain.LoadBalancerSubnetId;
import org.jclouds.softlayer.internal.BaseSoftLayerMockTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * Mock tests for the {@link LoadBalancerApi} class.
 */
@Test(groups = "unit", testName = "LoadBalancerApiMockTest")
public class LoadBalancerApiMockTest extends BaseSoftLayerMockTest {

   String NAME_MASK = "listeners%3Blisteners.defaultPool%3Bmembers%3BhealthMonitors%3Bdatacenter";
   String LISTENER_MASK = "defaultPool";

   public void testCreateLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_create.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancer.CreateLoadBalancer createLoadBalancer1 = LoadBalancer.CreateLoadBalancer.builder()
              .complexType("SoftLayer_Container_Product_Order_Network_LoadBalancer_AsAService")
              .packageId(805)
              .prices(new ArrayList<LoadBalancerServicePrice>(Arrays.asList(LoadBalancerServicePrice.create(199445),
                      LoadBalancerServicePrice.create(199465),
                      LoadBalancerServicePrice.create(205837),
                      LoadBalancerServicePrice.create(205975))))
              .name("testNameFromJclouds")
              .description("This is a test description from jclouds api live tests")
              .protocolConfigurations(new ArrayList<LoadBalancerProtocolConfiguration>(Arrays.asList(LoadBalancerProtocolConfiguration.create(
                      "HTTP", 80, "HTTP", 80, "ROUNDROBIN", 1000, null
              ))))
              .serverInstancesInformation(Collections.<LoadBalancerServerInstanceInformation>emptyList())
              .subnets(new ArrayList<LoadBalancerSubnetId>(Arrays.asList(LoadBalancerSubnetId.create(1466061))))
              .useHourlyPricing(true)
              .build();

      api.createLoadBalancer(ImmutableList.of(createLoadBalancer1));

      try {
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Product_Order/placeOrder");
      } finally {
         server.shutdown();
      }
   }

   public void testListLoadBalancers() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_list.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      Iterable<LoadBalancer> loadBalancers = api.listLoadBalancers();

      try {
         assertEquals(size(loadBalancers), 1);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getAllObjects?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyListLoadBalancers() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertTrue(api.listLoadBalancers().isEmpty());
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getAllObjects?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancer loadBalancer = api.getLoadBalancer("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertNotNull(loadBalancer);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancer/12ccb5c0-397a-3f56-9527-241cf4c11f69?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertNull(api.getLoadBalancer("12ccb5c0-397a-3f56-9527-241cf4c11f69"));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancer/12ccb5c0-397a-3f56-9527-241cf4c11f69?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancerObject() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancer loadBalancerObject = api.getLoadBalancerObject(170811);

      try {
         assertNotNull(loadBalancerObject);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/170811/getObject?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullLoadBalancerObject() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertNull(api.getLoadBalancerObject(170811));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/170811/getObject?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetDatacenter() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_datacenter_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      Location location = api.getDatacenter("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertNotNull(location);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getDatacenter/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testGetHealthMonitors() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_healthmonitors_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerHealthMonitor healthMonitor = api.getHealthMonitors("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertNotNull(healthMonitor);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getHealthMonitors/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullHealthMonitors() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertNull(api.getHealthMonitors("12ccb5c0-397a-3f56-9527-241cf4c11f69"));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getHealthMonitors/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateHealthMonitors() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_update_healthmonitors_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerHealthMonitor.UpdateLoadBalancerHealthMonitor updateLoadBalancerHealthMonitor1 =
              LoadBalancerHealthMonitor.UpdateLoadBalancerHealthMonitor.builder()
                      .healthMonitorUuid("33f4c489-a976-33i5-a943-e2ad1ace402d")
                      .backendPort(80)
                      .backendProtocol("HTTP")
                      .interval(20)
                      .timeout(10)
                      .maxRetries(10)
                      .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(updateLoadBalancerHealthMonitor1));

      LoadBalancer healthMonitor = api.updateLoadBalancerHealthMonitor(parameters);

      try {
         assertNotNull(healthMonitor);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_HealthMonitor/updateLoadBalancerHealthMonitors?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateHealthMonitorOnNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerHealthMonitor.UpdateLoadBalancerHealthMonitor updateLoadBalancerHealthMonitor1 =
              LoadBalancerHealthMonitor.UpdateLoadBalancerHealthMonitor.builder()
                      .healthMonitorUuid("12ccb5c0-397a-3f56-9527-241cf4c11f69")
                      .backendPort(80)
                      .backendProtocol("HTTP")
                      .interval(20)
                      .timeout(10)
                      .maxRetries(10)
                      .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(updateLoadBalancerHealthMonitor1));

      LoadBalancer healthMonitor = api.updateLoadBalancerHealthMonitor(parameters);

      try {
         assertNull(healthMonitor);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_HealthMonitor/updateLoadBalancerHealthMonitors?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetHealthMonitor() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_healthmonitors_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerHealthMonitor healthMonitor = api.getHealthMonitor("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertNotNull(healthMonitor);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_HealthMonitor/getObject/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNullHealthMonitor() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertNull(api.getHealthMonitor("12ccb5c0-397a-3f56-9527-241cf4c11f69"));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_HealthMonitor/getObject/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancerStatistics() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_healthmonitors_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerStatistics statistics = api.getLoadBalancerStatistics("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertNotNull(statistics);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancerStatistics/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNonExistingLoadBalancerStatistics() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerStatistics statistics = api.getLoadBalancerStatistics("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertNull(statistics);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancerStatistics/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_update_loadbalancer.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      List<String> parameters = new ArrayList<String>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add("This is a new description from jclouds API live test");

      LoadBalancer response = api.updateLoadBalancer(parameters);

      try {
         assertNotNull(response);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_LoadBalancer/updateLoadBalancer?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      List<String> parameters = new ArrayList<String>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add("This is a new description from jclouds API live test");

      LoadBalancer response = api.updateLoadBalancer(parameters);

      try {
         assertNull(response);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_LoadBalancer/updateLoadBalancer?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testAddLoadBalancerMembers() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_add_member.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerMember.AddLoadBalancerMember addLoadBalancerMember1 = LoadBalancerMember.AddLoadBalancerMember.builder()
              .privateIpAddress("10.171.224.204")
              .weight(50)
              .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(addLoadBalancerMember1));

      LoadBalancer response = api.addLoadBalancerMembers(parameters);

      try {
         assertEquals(size(response.members()), 2);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Member/addLoadBalancerMembers?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testAddLoadBalancerMembersToNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerMember.AddLoadBalancerMember addLoadBalancerMember1 = LoadBalancerMember.AddLoadBalancerMember.builder()
              .privateIpAddress("10.171.224.204")
              .weight(50)
              .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(addLoadBalancerMember1));

      LoadBalancer response = api.addLoadBalancerMembers(parameters);

      try {
         assertNull(response);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Member/addLoadBalancerMembers?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancerMembers() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_members_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      List<LoadBalancerMember> loadBalancerMembers = api.getLoadBalancerMembers(170811);

      try {
         assertEquals(size(loadBalancerMembers), 1);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/170811/getMembers");
      } finally {
         server.shutdown();
      }
   }

   public void testGetEmptyLoadBalancerMembers() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertTrue(api.getLoadBalancerMembers(170811).isEmpty());
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/170811/getMembers");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateLoadBalancerMembers() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_update_member.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerMember.UpdateLoadBalancerMember updateLoadBalancerMember1 = LoadBalancerMember.UpdateLoadBalancerMember.builder()
              .uuid("f278aa98-acee-479a-a0d6-05f66a0db8c5")
              .weight(50)
              .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(updateLoadBalancerMember1));

      LoadBalancer response = api.updateLoadBalancerMembers(parameters);

      try {
         assertEquals(size(response.members()), 2);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST",
                 "/SoftLayer_Network_LBaaS_Member/updateLoadBalancerMembers?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateLoadBalancerMembersOnNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerMember.UpdateLoadBalancerMember updateLoadBalancerMember1 = LoadBalancerMember.UpdateLoadBalancerMember.builder()
              .uuid("f278aa98-acee-479a-a0d6-05f66a0db8c5")
              .weight(50)
              .build();

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-358qc7a25r41");
      parameters.add(ImmutableList.of(updateLoadBalancerMember1));

      LoadBalancer response = api.updateLoadBalancerMembers(parameters);

      try {
         assertNull(response);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Member/updateLoadBalancerMembers?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancerMemberHealth() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_member_health_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      List<LoadBalancerMemberPoolHealth> loadBalancerMemberHealth =
              api.getLoadBalancerMemberHealth("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertEquals(size(loadBalancerMemberHealth), 1);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET",
                 "/SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancerMemberHealth/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testGetEmptyLoadBalancerMemberHealth() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertTrue(api.getLoadBalancerMemberHealth("12ccb5c0-397a-3f56-9527-241cf4c11f69").isEmpty());
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET",
                 "/SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancerMemberHealth/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancerMember() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_member_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerMember loadBalancerMember1 = api.getLoadBalancerMember(170811);

      try {
         assertNotNull(loadBalancerMember1);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_Member/170811/getObject");
      } finally {
         server.shutdown();
      }
   }

   public void testGetNonExistingLoadBalancerMember() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertNull(api.getLoadBalancerMember(170811));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_Member/170811/getObject");
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancerListeners() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_listeners_170811.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      List<LoadBalancerListener> loadBalancerListeners = api.getLoadBalancerListeners(170811);

      try {
         assertEquals(size(loadBalancerListeners), 1);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/170811/getListeners?objectMask=" + LISTENER_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetEmptyLoadBalancerListeners() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertTrue(api.getLoadBalancerListeners(170811).isEmpty());
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/170811/getListeners?objectMask=" + LISTENER_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testAddLoadBalancerProtocols() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_add_listener.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerProtocolConfiguration loadBalancerProtocolConfiguration1 = LoadBalancerProtocolConfiguration.create(
              "TCP", 90, "TCP", 9090, "ROUNDROBIN", 1000, null
      );

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(loadBalancerProtocolConfiguration1));

      LoadBalancer response = api.updateLoadBalancerProtocols(parameters);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertEquals(size(response.listeners()), 2);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Listener/updateLoadBalancerProtocols?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testAddLoadBalancerProtocolsOnNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerProtocolConfiguration loadBalancerProtocolConfiguration1 = LoadBalancerProtocolConfiguration.create(
              "TCP", 90, "TCP", 9090, "ROUNDROBIN", 1000, null
      );

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(loadBalancerProtocolConfiguration1));

      try {
         assertNull(api.updateLoadBalancerProtocols(parameters));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Listener/updateLoadBalancerProtocols?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateLoadBalancerProtocols() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_update_listener.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerProtocolConfiguration loadBalancerProtocolConfiguration1 = LoadBalancerProtocolConfiguration.create(
              "HTTP", 81, "HTTP", 8081, "WEIGHTED_RR",
              500, "cae12d29-0293-4a73-bb56-45198c2e4602"
      );

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(loadBalancerProtocolConfiguration1));

      LoadBalancer response = api.updateLoadBalancerProtocols(parameters);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertEquals(size(response.listeners()), 2);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Listener/updateLoadBalancerProtocols?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testUpdateLoadBalancerProtocolsOnNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerProtocolConfiguration loadBalancerProtocolConfiguration1 = LoadBalancerProtocolConfiguration.create(
              "HTTP", 81, "HTTP", 8081, "WEIGHTED_RR",
              500, "cae12d29-0293-4a73-bb56-45198c2e4602"
      );

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(ImmutableList.of(loadBalancerProtocolConfiguration1));

      try {
         assertNull(api.updateLoadBalancerProtocols(parameters));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Listener/updateLoadBalancerProtocols?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetLoadBalancerListener() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_get_listener_214871.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      LoadBalancerListener loadBalancerListener1 = api.getLoadBalancerListener(214871);

      try {
         assertNotNull(loadBalancerListener1);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_Listener/214871/getObject?objectMask=" + LISTENER_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testGetNonExistingLoadBalancerListener() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertNull(api.getLoadBalancerListener(214871));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_Listener/214871/getObject?objectMask=" + LISTENER_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteLoadBalancerListener() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody(payloadFromResource("/loadbalancer_delete_listener.json")));
      LoadBalancerApi api = getLoadBalancerApi(server);

      List<String> deleteLoadBalancerListeners = new ArrayList<String>();
      deleteLoadBalancerListeners.add("cae12d29-0293-4a73-bb56-45198c2e4602");

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(deleteLoadBalancerListeners);

      LoadBalancer response = api.deleteLoadBalancerListeners(parameters);

      try {
         assertEquals(server.getRequestCount(), 1);
         assertEquals(size(response.listeners()), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Listener/deleteLoadBalancerProtocols?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteLoadBalancerListenerOnNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      List<String> deleteLoadBalancerListeners = new ArrayList<String>();
      deleteLoadBalancerListeners.add("cae12d29-0293-4a73-bb56-45198c2e4602");

      List<Object> parameters = new ArrayList<Object>();

      parameters.add("12ccb5c0-397a-3f56-9527-241cf4c11f69");
      parameters.add(deleteLoadBalancerListeners);

      try {
         assertNull(api.deleteLoadBalancerListeners(parameters));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "POST", "/SoftLayer_Network_LBaaS_Listener/deleteLoadBalancerProtocols?objectMask=" + NAME_MASK);
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setBody("true"));
      LoadBalancerApi api = getLoadBalancerApi(server);

      boolean response = api.deleteLoadBalancer("12ccb5c0-397a-3f56-9527-241cf4c11f69");

      try {
         assertTrue(response);
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/cancelLoadBalancer/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   public void testDeleteNonExistingLoadBalancer() throws Exception {
      MockWebServer server = mockWebServer(new MockResponse().setResponseCode(404));
      LoadBalancerApi api = getLoadBalancerApi(server);

      try {
         assertFalse(api.deleteLoadBalancer("12ccb5c0-397a-3f56-9527-241cf4c11f69"));
         assertEquals(server.getRequestCount(), 1);
         assertSent(server, "GET", "/SoftLayer_Network_LBaaS_LoadBalancer/cancelLoadBalancer/12ccb5c0-397a-3f56-9527-241cf4c11f69");
      } finally {
         server.shutdown();
      }
   }

   private LoadBalancerApi getLoadBalancerApi(MockWebServer server) {
      return api(SoftLayerApi.class, server.getUrl("/").toString(), new
              JavaUrlHttpCommandExecutorServiceModule(), new SoftLayerParserModule()).getLoadBalancerApi();
   }
}
