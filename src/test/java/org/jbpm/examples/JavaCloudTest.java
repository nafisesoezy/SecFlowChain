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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class JavaCloudTest extends JbpmJUnitBaseTestCase {
	
	/*
	//main test
	@Test  
	public void testProcess() {
			KieSession ksession = createRuntimeManager("BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null).getKieSession();
			int i=0;
			//for(i=0;i<5;i++) {
			
			Map<String, Object> params = new HashMap<String, Object>();

			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", new Tenant("nafise"+i).getName());
			ksession.startProcess("ServiceProcess2", params);

			
		
			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", "Elena"+i);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess2", params);
			//assertProcessInstanceCompleted(processInstance.getId(), ksession);

		//}
	}
	*/
	/*
	//main test
		@Test  
		public void testProcess() {
				KieSession ksession = createRuntimeManager("BPMN2-ServiceProcess.bpmn2","BPMN2-ServiceProcess2.bpmn2","InsuranceClaim.bpmn2").getRuntimeEngine(null).getKieSession();
				int i=0;
				//for(i=0;i<5;i++) {
				
					//first process
					ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("s", new Tenant("Majid"+i).getName());
					ksession.startProcess("ServiceProcess1", params);
					
					
					
					//second process
					ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
					params.put("s", new Tenant("nafise"+i).getName());
					ksession.startProcess("ServiceProcess2", params);

					
					
					//Third Process
					ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
					params.put("s", "Elena"+i);
					WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess2", params);
					//assertProcessInstanceCompleted(processInstance.getId(), ksession);

				//}
		}
		*/

	/* falied
	//test single servic task
	@Test  
	public void testProcess() {
			KieSession ksession = createRuntimeManager("BPMN2-ServiceProcess4.bpmn2").getRuntimeEngine(null).getKieSession();
			Map<String, Object> params = new HashMap<String, Object>();

			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", new Tenant("nafise").getName());
			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", new Tenant("majid").getName());
			  WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess4", params);
			//ksession.startProcess("ServiceProcess2", params);
			assertProcessInstanceCompleted(processInstance.getId(), ksession);
				
	}
	*/
	
	/*
	 // test single task service
	@Test
	public void testProcess() {
			KieSession ksession = createRuntimeManager("main.bpmn2").getRuntimeEngine(null).getKieSession();
			
			//Register handlers
			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			Map<String, Object> params = new HashMap<String, Object>();
			//params.put("person", new Tenant("Maryam"));
			//params.put("person", "Maryam");
			   params.put("s", new Tenant("john").getName());
			ksession.startProcess("MainProcess1", params);
			 // WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess", params);
			
	}
	*/
	/*
	 @Test
	    public void testEvaluationProcess() throws Exception {
			KieSession ksession = createRuntimeManager("BPMN2-EvaluationProcess2.bpmn2").getRuntimeEngine(null).getKieSession();
	        //KieBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess.bpmn2");
	        //KieSession ksession = createKnowledgeSession(kbase);
	        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
	        ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("employee", "UserId-53421");
	        ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
	        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
	    }
	*/
	
	
	/*
	// successful (it has two tas services but work like single task service)
	 // test single task service
	@Test
	public void testProcess() {
			RuntimeEngine runtime =createRuntimeManager("BPMN2-ServiceProcess.bpmn2").getRuntimeEngine(null);	
			KieSession ksession = runtime.getKieSession();
			//KieSession ksession = createRuntimeManager("BPMN2-ServiceProcess.bpmn2").getRuntimeEngine(null).getKieSession();
			
			//Register handlers
			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			Map<String, Object> params = new HashMap<String, Object>();
			//params.put("person", new Tenant("Maryam"));
			//params.put("person", "Maryam");
			   params.put("s", new Tenant("john").getName());
			  

			ksession.startProcess("ServiceProcess1", params);

			//TaskService taskService = runtime.getTaskService();
			//System.out.println("service "+taskService);
    		//List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
    		//TaskSummary task = tasks.get(0);
    		//System.out.println("'krisv' completing task " + task.getName() + ": " + task.getDescription());
    		//taskService.start(task.getId(), "krisv");
    		
			
			
			 // WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess", params);
			
	}
	
	*/
	
	/*
	// test receiveTask
	@Test
	public void testProcess() {
		
		KieSession ksession = createRuntimeManager("BPMN2-ReceiveTask.bpmn2").getRuntimeEngine(null).getKieSession();
		ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ReceiveTask");
        receiveTaskHandler.messageReceived("HelloMessage", "Hello john!");
        //System.out.println(" messageReceived ");
	}
	*/
	
	
	/*
	 // test InsuranceClaim2
	@Test
	public void testProcess() {
		
		
			// way 1:
			
			//System.out.println("Result: ");
			KieSession ksession = createRuntimeManager("InsuranceClaim2.bpmn2").getRuntimeEngine(null).getKieSession();
			//Register handlers
			  ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
		        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
		        receiveTaskHandler.messageReceived("HelloMessage", "Hello john!");
		        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("s", "john");	
		        //WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess3");
		        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess3",params);
				
		
		       
		        
		        //assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
		        
		        //ksession = restoreSession(ksession);
		        //assertProcessInstanceCompleted(processInstance.getId(), ksession);
			  	//WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess3", params);
			   
	}
	*/

	/*
	 // test InsuranceClaim (successful)
	@Test
	public void testProcess() {
		
		
			// way 1:
			
			//System.out.println("Result: ");
			KieSession ksession = createRuntimeManager("InsuranceClaim.bpmn2").getRuntimeEngine(null).getKieSession();
			Map<String, Object> params = new HashMap<String, Object>();

			
			//Register handlers
			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", "Elena");
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("insurance1", params);
			   
	}
	*/
	
	/*
	 // test InsuranceClaim3 (successful)
	@Test
	public void testProcess() {
		int i=0;
		System.out.println("Hello Elena");
		KieSession ksession = createRuntimeManager("InsuranceClaim3.bpmn2").getRuntimeEngine(null).getKieSession();

		Map<String, Object> params = new HashMap<String, Object>();

		for(;i<10;i++) {
			//Register handlers
			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", "Elena"+i);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("insurance3", params);
		}
		
	}
	
	*/
	
	
	/*
	 // test InsuranceClaim3  with parallel sessions
	@Test
	public void testProcess() {
		int i=0;
		
		
		 PersistenceUtil.setupPoolingDataSource();
	        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
	            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("InsuranceClaim3.bpmn2"), ResourceType.BPMN2)
	            .get();
	        RuntimeManager manager=RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
		         
         RuntimeEngine runtime1 = manager.getRuntimeEngine(null);
         //RuntimeEngine runtime2 = manager.getRuntimeEngine(null);

         KieSession ksession1 = runtime1.getKieSession();
         //KieSession ksession2 = runtime2.getKieSession();
         KieSession ksession2 = runtime1.getKieSession();

		    
		
		Map<String, Object> params = new HashMap<String, Object>();

		for(;i<5;i++) {
			//Register handlers
			ksession1.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", "Elena"+i);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession1.startProcess("insurance3", params);
		}
		
		for(;i<10;i++) {
			//Register handlers
			ksession2.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", "Elena"+i);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession2.startProcess("insurance3", params);
		}
		
	}
	*/
	
	 // test InsuranceClaim5  with parallel sessions
		@Test
		public void testProcess() {
			int i=0;
			
			
			 PersistenceUtil.setupPoolingDataSource();
		        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
		            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("InsuranceClaim5.bpmn2"), ResourceType.BPMN2)
		            .get();
		        RuntimeManager manager=RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
			         
	         RuntimeEngine runtime1 = manager.getRuntimeEngine(null);
	         //RuntimeEngine runtime2 = manager.getRuntimeEngine(null);

	         KieSession ksession1 = runtime1.getKieSession();
	         //KieSession ksession2 = runtime2.getKieSession();
	         KieSession ksession2 = runtime1.getKieSession();

			    
			
			Map<String, Object> params = new HashMap<String, Object>();

			for(;i<5;i++) {
				//Register handlers
				ksession1.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
				params.put("s", "Elena"+i);
				WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession1.startProcess("insurance5", params);
			}
			
			for(;i<10;i++) {
				//Register handlers
				ksession2.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
				params.put("s", "Elena"+i);
				WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession2.startProcess("insurance5", params);
			}
			
		}
	
	
	
	/*
	 //test single task script
	@Test
	public void testProcess() {
			KieSession ksession = createRuntimeManager("ScriptTask2.bpmn").getRuntimeEngine(null).getKieSession();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("person", new Tenant("Nafise"));
			ksession.startProcess("com.sample.script2", params);
			
	}
	*/
	
	/*
	 //testMultipleProcessInstancesParallel
	@Test 
	public void testMultipleProcessInstancesParallel() {
			RuntimeEngine runtime =createRuntimeManager("ScriptTask2.bpmn","ScriptTask3.bpmn").getRuntimeEngine(null);	
			System.out.println("step2 ");
			KieSession ksession = runtime.getKieSession();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("person", new Tenant("Elena"));
			ksession.startProcess("com.sample.script2", params);
			
			KieSession ksession2 = runtime.getKieSession();
			Map<String, Object> params2 = new HashMap<String, Object>();
			params2.put("person", new Tenant("Majid"));
			ksession2.startProcess("com.sample.script3", params2);
	}
	
	*/
	/*
	 //test evaluation(successful)
		@Test 
		public void tesEvaluation() {
		 try {
	         
			 // load up the knowledge base
			 	System.out.println("Hello Elena");
		        PersistenceUtil.setupPoolingDataSource();
		        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
		            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("Evaluation.bpmn"), ResourceType.BPMN2)
		            .get();
		        RuntimeManager manager=RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
			         
	            RuntimeEngine runtime = manager.getRuntimeEngine(null);
	            KieSession ksession = runtime.getKieSession();

	            // start a new process instance
	            Map<String, Object> params = new HashMap<String, Object>();
	            params.put("employee", "krisv");
	            params.put("reason", "Yearly performance evaluation");
	    		ksession.startProcess("com.sample.evaluation", params);

	    		// complete Self Evaluation
	            TaskService taskService = runtime.getTaskService();
	    		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
	    		TaskSummary task = tasks.get(0);
	    		//System.out.println("count "+tasks);
	    		System.out.println("'krisv' completing task " + task.getName() + ": " + task.getDescription());
	    		taskService.start(task.getId(), "krisv");
	    		Map<String, Object> results = new HashMap<String, Object>();
	    		results.put("performance", "exceeding");
	    		taskService.complete(task.getId(), "krisv", results);
	    		
	    		
	    		// john from HR
	    		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
	    		task = tasks.get(0);
	    		System.out.println("'john' completing task " + task.getName() + ": " + task.getDescription());
	    		taskService.start(task.getId(), "john");
	    		results = new HashMap<String, Object>();
	    		results.put("performance", "acceptable");
	    		taskService.complete(task.getId(), "john", results);
	    		
	    		// mary from PM
	    		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
	    		task = tasks.get(0);
	    		System.out.println("'mary' completing task " + task.getName() + ": " + task.getDescription());
	    		taskService.start(task.getId(), "mary");
	    		results = new HashMap<String, Object>();
	    		results.put("performance", "outstanding");
	    		taskService.complete(task.getId(), "mary", results);
	    		System.out.println("Process instance completed");
	    	
	    		manager.disposeRuntimeEngine(runtime);
	        } catch (Throwable t) {
	            t.printStackTrace();
	        }
	        //System.exit(0);
	        }
	
*/
	
	
	/*
	@Test
	public void testMultipleProcessInstances() {
		System.out.println("multi instances: ");
		try {
			PersistenceUtil.setupPoolingDataSource();
	        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
	            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("multipleinstance.bpmn"), ResourceType.BPMN2)
	            .get();
	        RuntimeManager manager=RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
            RuntimeEngine runtime = manager.getRuntimeEngine(null);
            KieSession ksession = runtime.getKieSession();

			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> list = new ArrayList<String>();
			list.add("krisv");
			//list.add("john doe");
			//list.add("superman");
			params.put("list", list);
			ksession.startProcess("com.sample.multipleinstance", params);

            TaskService taskService = runtime.getTaskService();
    		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK");
    		for (TaskSummary task: tasks) {
	            System.out.println("Sales-rep executing task " + task.getName() + "(" + task.getId() + ": " + task.getDescription() + ")");
	            taskService.start(task.getId(), "sales-rep");
	            taskService.complete(task.getId(), "sales-rep", null);
    		}
            
            manager.disposeRuntimeEngine(runtime);
        } 
		catch (Throwable t) {
            t.printStackTrace();
        }
	}
*/
}

