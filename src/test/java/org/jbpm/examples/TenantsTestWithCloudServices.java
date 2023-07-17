/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.examples;
import org.jbpm.examples.tenants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.test.util.db.PersistenceUtil;
import org.jbpm.bpmn2.handler.*;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import static org.assertj.core.api.Assertions.assertThat;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;

/**
 * This is a sample file to test a process.
 */
public class TenantsTestWithCloudServices extends JbpmJUnitBaseTestCase {
	int numOfTenants=2;
	int numOfHosts=5;
	int numOfServices=3;//num of different services provided by clouds
	int MaxNumOfUsers=50;//MaxNumOfUsers for each tenants
	int maxNumOfHosts=5;//maxNumOfHosts provide the service
	int minNumOfHosts=5;//minNumOfHosts provide the service
	
	boolean cloudService=true;
	
	
	 // test InsuranceClaim5  with parallel sessions
		@Test
		public void testProcess() throws IOException{
			
			KernelManagment km=new KernelManagment(numOfTenants,numOfHosts,numOfServices,MaxNumOfUsers,maxNumOfHosts,minNumOfHosts,cloudService);
			
			//start 1
			//RuntimeEngine runtime =createRuntimeManager("InsuranceClaim6.bpmn2","BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null);	
			
			//start2
			/*
			 PersistenceUtil.setupPoolingDataSource();
			RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
		            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("InsuranceClaim6.bpmn2"), ResourceType.BPMN2)
		            .get();
		        RuntimeManager manager=RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
			         
	        RuntimeEngine runtime1 = manager.getRuntimeEngine(null);
	        //RuntimeEngine runtime2 = manager.getRuntimeEngine(null);
			*/
			/*
			for(int t=0;t<numOfTenants;t++) {//for each tenant
				//int t=0;
				String tenantName="tenant"+t;
				TenantKernel tenant=new TenantKernel(tenantName,runtime);
				//TenantKernel tenant=new TenantKernel(tenantName);

				//tenant.submitWorkflow(instanceNumber,runtime);
				//tenant.submitWorkflow(instanceNumber,runtime1);
				
			}//end of tenant
			*/
		}
	
	
	
}

