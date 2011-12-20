/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.hpcloud.object.storage.blobstore.config;

import org.jclouds.hpcloud.object.storage.blobstore.HPCloudObjectStorageAsyncBlobStore;
import org.jclouds.hpcloud.object.storage.blobstore.HPCloudObjectStorageBlobStore;
import org.jclouds.hpcloud.object.storage.blobstore.functions.HPCloudObjectStorageObjectToBlobMetadata;
import org.jclouds.openstack.swift.blobstore.SwiftAsyncBlobStore;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;

/**
 * 
 * @author Adrian Cole
 */
public class HPCloudObjectStorageBlobStoreContextModule extends SwiftBlobStoreContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(SwiftBlobStore.class).to(HPCloudObjectStorageBlobStore.class);
      bind(SwiftAsyncBlobStore.class).to(HPCloudObjectStorageAsyncBlobStore.class);
      bind(ObjectToBlobMetadata.class).to(HPCloudObjectStorageObjectToBlobMetadata.class);
   }
}
