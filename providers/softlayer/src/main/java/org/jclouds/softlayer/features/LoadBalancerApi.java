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

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.softlayer.domain.LoadBalancer;
import org.jclouds.softlayer.domain.LoadBalancerMember;
import org.jclouds.softlayer.domain.LoadBalancerMemberPoolHealth;
import org.jclouds.softlayer.domain.LoadBalancerListener;
import org.jclouds.softlayer.domain.LoadBalancerHealthMonitor;
import org.jclouds.softlayer.domain.LoadBalancerStatistics;
import org.jclouds.softlayer.domain.Location;

import javax.ws.rs.core.MediaType;
import javax.inject.Named;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.Closeable;
import java.util.List;

/**
 * Provides access to Load Balancer via their REST API.
 * <p/>
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 */
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_JSON)
public interface LoadBalancerApi extends Closeable {

   String NAME_MASK = "listeners;listeners.defaultPool;members;healthMonitors;datacenter";
   String LISTENER_MASK = "defaultPool";

   /**
    * returns a list of load balancers belong to the account
    * @return an account's associated load balancer objects.
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getallobjects" />
    */
   @GET
   @Named("LoadBalancer:getAlllObjects")
   @Path("/SoftLayer_Network_LBaaS_LoadBalancer/getAllObjects")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancer> listLoadBalancers();

   /**
    * returns the details of the load balancer of the given uuid
    * @param uuid uuid of the load balancer
    * @return load balancer information or null
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getloadbalancer" />
    */
   @GET
   @Named("LoadBalancer:getLoadBalancer")
   @Path("/SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancer/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer getLoadBalancer(@PathParam("uuid") String uuid);

   /**
    * returns the details of the load balancer of the given uuid
    * @param id id of the load balancer
    * @return load balancer information or null
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getobject" />
    */
   @GET
   @Named("LoadBalancer:getObject")
   @Path("/SoftLayer_Network_LBaaS_LoadBalancer/{id}/getObject")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer getLoadBalancerObject(@PathParam("id") long id);

   /**
    * creates a LBaaS object
    * complexType shuold always be "SoftLayer_Container_Product_Order_Network_LoadBalancer_AsAService"
    * packageId should always be 805
    * LoadBalancerServicePrices are suggested to be 199445, 199465, 205837, 205975 (dal10)
    * Please find the subnetId through SoftLayer Network API, or create a new subnet, note that the subnet should be
    *    on the same datacenter as the services used above
    * @return load balancer information or null
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getobject" />
    */
   @POST
   @Named("LoadBalancer:createObject")
   @Path("/SoftLayer_Product_Order/placeOrder")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   void createLoadBalancer(@WrapWith("parameters") List<LoadBalancer.CreateLoadBalancer> LoadBalancersToBeCreated);

   /**
    * Update description of a Load Balancer
    * @param LoadBalancersToBeUpdated List of Strings containing new load balancer information of uuid and the new description
    *                                in order
    * @return new load balancer information or null
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/updateLoadBalancer" />
    */
   @GET
   @Named("LoadBalancer:updateLoadBalancer")
   @Path("/SoftLayer_Network_LBaaS_LoadBalancer/updateLoadBalancer")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer updateLoadBalancer(@WrapWith("parameters") List<String> LoadBalancersToBeUpdated);

   /**
    * Cancels the specified load balancer
    * @param uuid uuid of the targeted load balancer
    * @reutrn boolean value true for successful deletion and false for failed deletion
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/cancelloadbalancer" />
    */
   @GET
   @Named("LoadBalancer:cancelLoadBalancer")
   @Path("SoftLayer_Network_LBaaS_LoadBalancer/cancelLoadBalancer/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   boolean deleteLoadBalancer(@PathParam("uuid") String uuid);

   /**
    * returns the Datacenter information of the given load balancer uuid
    * @param uuid uuid of the targeted load balancer
    * @reutrn datacenter location information of the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getdatacenter" />
    */
   @GET
   @Named("LoadBalancer:getDatacenter")
   @Path("SoftLayer_Network_LBaaS_LoadBalancer/getDatacenter/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   Location getDatacenter(@PathParam("uuid") String uuid);

   /**
    * Retrieve health monitors for the backend members
    * @param uuid uuid of the targeted load balancer
    * @reutrn health monitors information of the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/gethealthmonitors" />
    */
   @GET
   @Named("LoadBalancer:getHealthMonitors")
   @Path("SoftLayer_Network_LBaaS_LoadBalancer/getHealthMonitors/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancerHealthMonitor getHealthMonitors(@PathParam("uuid") String uuid);

   /**
    * Retrieve a health monitor of the given uuid
    * @param uuid uuid of the targeted load balancer health monitor
    * @reutrn health monitor information of the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_healthmonitor/getobject" />
    */
   @GET
   @Named("LoadBalancerHealthMonitor:getHealthMonitor")
   @Path("SoftLayer_Network_LBaaS_HealthMonitor/getObject/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancerHealthMonitor getHealthMonitor(@PathParam("uuid") String uuid);

   /**
    * Updates targeted load balancer health monitor
    * @param LoadBalancerHealthMonitorToBeUpdated the load balancer health monitor update definition of
    *                                             LoadBalancerHealthMonitor.UpdateLoadBalancerHealthMonitor
    *                                             along with the uuid of the targeted load balancer
    * @reutrn new load balancer obejct
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_healthmonitor/updateloadbalancerhealthmonitor" />
    */
   @POST
   @Named("LoadBalancerHealthMonitor:updateLoadBalancerHealthMonitor")
   @Path("SoftLayer_Network_LBaaS_HealthMonitor/updateLoadBalancerHealthMonitors")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer updateLoadBalancerHealthMonitor(@WrapWith("parameters") List<Object> LoadBalancerHealthMonitorToBeUpdated);

   /**
    * Returns load balancers statistics
    * @param uuid uuid of the targeted load balancer
    * @reutrn statistics of the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getloadbalancerstatistics" />
    */
   @GET
   @Named("LoadBalancer:getStatistics")
   @Path("SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancerStatistics/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancerStatistics getLoadBalancerStatistics(@PathParam("uuid") String uuid);

   /**
    * Returns load balancers members
    * @param id id of the targeted load balancer
    * @reutrn members of the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getMembers" />
    */
   @GET
   @Named("LoadBalancer:getMembers")
   @Path("SoftLayer_Network_LBaaS_LoadBalancer/{id}/getMembers")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancerMember> getLoadBalancerMembers(@PathParam("id") long id);

   /**
    * Returns load balancers members healths
    * @param uuid uuid of the targeted load balancer
    * @reutrn members healths of the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getLoadBalancerMemberHealth" />
    */
   @GET
   @Named("LoadBalancer:getLoadBalancerMemberHealth")
   @Path("SoftLayer_Network_LBaaS_LoadBalancer/getLoadBalancerMemberHealth/{uuid}")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancerMemberPoolHealth> getLoadBalancerMemberHealth(@PathParam("uuid") String uuid);

   /**
    * Returns load balancers member with the given id
    * @param id id of the targeted load balancer member
    * @reutrn member the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_member/getobject" />
    */
   @GET
   @Named("LoadBalancerMember:getLoadBalancerMember")
   @Path("SoftLayer_Network_LBaaS_Member/{id}/getObject")
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancerMember getLoadBalancerMember(@PathParam("id") int id);

   /**
    * Add load balancer members
    * @param LoadBalancerMembersToBeAdded the load balancer member creation definition of LoadBalancerMember.AddLoadBalancerMember
    *                                     along with the uuid of the targeted load balancer
    * @reutrn new load balancer object
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_member/addloadbalancermembers" />
    */
   @POST
   @Named("LoadBalanceMember:addLoadBalancerMembers")
   @Path("SoftLayer_Network_LBaaS_Member/addLoadBalancerMembers")
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer addLoadBalancerMembers(@WrapWith("parameters") List<Object> LoadBalancerMembersToBeAdded);

   /**
    * Update member's weight
    * @param LoadBalancerMembersToBeUpdated the load balancer member edition definition of LoadBalancerMember.UpdateLoadBalancerMember
    *                                     along with the uuid of the targeted load balancer
    * @reutrn new load balancer object
    * @see <a href="https://sldn.softlayer.com/reference/services/SoftLayer_Network_LBaaS_Member/updateLoadBalancerMembers" />
    */
   @POST
   @Named("LoadBalanceMember:updateLoadBalancerMembers")
   @Path("SoftLayer_Network_LBaaS_Member/updateLoadBalancerMembers")
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer updateLoadBalancerMembers(@WrapWith("parameters") List<Object> LoadBalancerMembersToBeUpdated);

   /**
    * Delete load balancer members
    * @param LoadBalancerMembersToBeDeleted the array of the load balancer member uuid along with the uuid of the
    *                                       targeted load balancer
    * @reutrn new load balancer object
    * @see <a href="https://sldn.softlayer.com/reference/services/SoftLayer_Network_LBaaS_Member/deleteLoadBalancerMembers" />
    */
   @POST
   @Named("LoadBalanceMember:deleteLoadBalancerMembers")
   @Path("SoftLayer_Network_LBaaS_Member/deleteLoadBalancerMembers")
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer deleteLoadBalancerMembers(@WrapWith("parameters") List<Object> LoadBalancerMembersToBeDeleted);

   /**
    * Returns load balancers listeners
    * @param id id of the targeted load balancer
    * @reutrn listeners of the load balancer
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_loadbalancer/getListeners" />
    */
   @GET
   @Named("LoadBalancer:getListeners")
   @Path("SoftLayer_Network_LBaaS_LoadBalancer/{id}/getListeners")
   @QueryParams(keys = "objectMask", values = LISTENER_MASK)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<LoadBalancerListener> getLoadBalancerListeners(@PathParam("id") long id);

   /**
    * Returns load balancers listener with the given id
    * @param id id of the targeted load balancer listener
    * @reutrn listener with the targeted id
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_listener/getObject" />
    */
   @GET
   @Named("LoadBalancerListener:getObject")
   @Path("SoftLayer_Network_LBaaS_Listener/{id}/getObject")
   @QueryParams(keys = "objectMask", values = LISTENER_MASK)
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancerListener getLoadBalancerListener(@PathParam("id") long id);

   /**
    * Updates targeted load balancer listeners
    * @param LoadBalancerProtocolsToBeUpdated the load balancer protocol update definition of LoadBalancerProtocolConfiguration
    *                                             along with the uuid of the targeted load balancer
    * @reutrn new load balancer object
    * @see <a href="https://sldn.softlayer.com/reference/services/softlayer_network_lbaas_listener/updateloadbalancerprotocols" />
    */
   @POST
   @Named("LoadBalancerListener:LoadBalancerProtocolsToBeUpdated")
   @Path("SoftLayer_Network_LBaaS_Listener/updateLoadBalancerProtocols")
   @Produces(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "objectMask", values = NAME_MASK)
   @Fallback(NullOnNotFoundOr404.class)
   LoadBalancer updateLoadBalancerProtocols(@WrapWith("parameters") List<Object> LoadBalancerProtocolsToBeUpdated);

    /**
     * Delete load balancer listeners
     * @param LoadBalancerListenersToBeDeleted the array of the load balancer listener uuid along with the uuid of the
     *                                       targeted load balancer
     * @reutrn new load balancer object
     * @see <a href="https://sldn.softlayer.com/reference/services/SoftLayer_Network_LBaaS_Listener/deleteLoadBalancerProtocols" />
     */
    @POST
    @Named("LoadBalanceListener:deleteLoadBalancerProtocols")
    @Path("SoftLayer_Network_LBaaS_Listener/deleteLoadBalancerProtocols")
    @QueryParams(keys = "objectMask", values = NAME_MASK)
    @Produces(MediaType.APPLICATION_JSON)
    @Fallback(NullOnNotFoundOr404.class)
    LoadBalancer deleteLoadBalancerListeners(@WrapWith("parameters") List<Object> LoadBalancerListenersToBeDeleted);
}

