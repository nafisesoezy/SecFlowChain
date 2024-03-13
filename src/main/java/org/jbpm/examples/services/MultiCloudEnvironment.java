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

package org.jbpm.examples.services;
import org.jbpm.examples.tenants.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;






public class MultiCloudEnvironment {
	
	int numOfProviders;//number of provisers in multi cloud environment
	static List<ProviderPlus> providers = new ArrayList<ProviderPlus>();
	
	//anomaly parameters
	int serviceTaskAnomalyRate=10;// for each "serviceTaskAnomalyRate" service task one is anomaly
	static ArrayList<Map<String, Boolean>> anomalyPattern = new ArrayList<>();
	int numOfExecutions=1000;
	static int currentExecutionNumber=0;
	//Workflow workflow;
	
	TenantKernel currentTenant;
	static ArrayList<UserTask> userTasks = new ArrayList<UserTask>();//onExituserTask' propertises
	static ArrayList<ServiceTask> serviceTasks = new ArrayList<ServiceTask>();//onExitServiceTask' propertises
	
	
	public MultiCloudEnvironment(int numOfTenants,int ps) {
		System.out.println("cloudSim: Starting MultiCloudEnvironment...");
		int numOfProviders=ps;

		
		try {
			
			for(int i=0;i<numOfProviders;i++) {
				ProviderPlus p=new ProviderPlus(i);
				providers.add(p);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("The simulation has been terminated due to an unexpected error");
		}
		
		
		
	}
	

	public void setWorkflow(Workflow w) {
		//workflow=w;
		//CreateAnomalyPattern();
	}
	public void CreateAnomalyPattern(Workflow workflow) {
		//System.out.println("**************CreateAnomalyPattern***********");
		anomalyPattern = new ArrayList<>();
		int size=workflow.getServiceTasks().size();	
		//int size=10;		

		boolean[] currentOption = new boolean[size];
        generateOptionsRecursive(currentOption, 0,workflow);
        //printOptions();
		//System.out.println("size of anomalyPattern"+anomalyPattern.size());
	}
	
	public  void generateOptionsRecursive(boolean[] currentOption, int currentIndex,Workflow workflow) {
        if (currentIndex == currentOption.length) {
            // Add current option to the list
            anomalyPattern.add(convertToMap(currentOption,workflow));
            return;
        }

        // Generate option with current node participating (true)
        currentOption[currentIndex] = true;
        generateOptionsRecursive(currentOption, currentIndex + 1, workflow);

        // Generate option with current node not participating (false)
        currentOption[currentIndex] = false;
        generateOptionsRecursive(currentOption, currentIndex + 1, workflow);
    }

    public  Map<String, Boolean> convertToMap(boolean[] option,Workflow workflow) {
        Map<String, Boolean> map = new HashMap<>();
        for (int i = 0; i < option.length; i++) {
            map.put(workflow.getServiceTasks().get(i), option[i]);
        }
        return map;
    }

    public  void printOptions() {
        for (Map<String, Boolean> option : anomalyPattern) {
            System.out.println(option);
        }
    }
	
	
	public void ExecuteServiceTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String serviceTask) {
		//getProviders().get(0).executeService(serviceTask);
		//getProviders().get(0).executeService2(1,ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,serviceTask);

	}
	
	
	public  List<ProviderPlus> getProviders(){
		return providers;
	}
	
	
	
	public void AssigningServiceToTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,Workflow workflow,String serviceTask,int taskNumber,int count) throws IOException{//Scheduling and assigning the service task with this serviceType to a provider
		//we can add service need(ram,bw,input,output here) but now we read it in provicerPlus based on ServiceSpecification.txt
		CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,taskNumber,count);
		
	}
	
	public void AssigningBackupServiceToTask(Service backup,Workflow workflow,String serviceTask) throws IOException{//execute task in the backup service
		//we can add service need(ram,bw,input,output here) but now we read it in provicerPlus based on ServiceSpecification.txt
		System.out.println("			starting execution of serviceTask backup service in multicloud!");//without anomaly
		providers.get(0).executeService(serviceTask,backup);		
	}
	
	public void CreateServiceTask (long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,Workflow workflow,String serviceTask,int taskNumber,int count) throws IOException{
		System.out.println("count..."+count);

		//Scheduling the Service
		ArrayList<Service> services = workflow.getLegalServiceforTasks().get(serviceTask);
		Random rand = new Random();
		Service s=services.get(rand.nextInt(services.size()));
		Service backupService = services.get(rand.nextInt(services.size()));
		
		//2-RL approach to train the model
		CreateAnomalyPattern(workflow);
		//System.out.println("anomalyPattern creating..."+anomalyPattern.size());
		if(count<anomalyPattern.size()) {
			//System.out.println("anomalyPattern creating...");
			boolean checkAnomaly=true;
			Map<String, Boolean> taskanomaly=anomalyPattern.get(count);
			for (Map.Entry<String, Boolean> entry : taskanomaly.entrySet()) {
				String key = entry.getKey();
				Boolean value = entry.getValue();
				//System.out.println("Key: " + key + ", Value: " + value+" "+serviceTask);
				if(key.equals(serviceTask))
					checkAnomaly=value;
	        }
			System.out.println("checkAnomaly..."+checkAnomaly);
			
			//boolean valueForTask=taskanomaly.get("key");
			
			if(!checkAnomaly) {
				System.out.println("starting execution of serviceTask in multicloud!");//with anomaly
				//add1:providers.get(0).executeService(serviceTask,s);
				CreateAnomalServiceTaskFromSpecificAttackPossibility(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,taskNumber);				
				//CreateAnomalServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService);				
			}
			else {
				System.out.println("starting execution of serviceTask in multicloud!");//without anomaly
				//add2:providers.get(0).executeService(serviceTask,s);
				CreateNormalServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,taskNumber);
			}
		}
		
		else {
			//2-random approach
			//execute the Service
			int anomaly=rand.nextInt(serviceTaskAnomalyRate);
			//int anomaly=rand.nextInt(2);//for test
			
			if(anomaly<3) {
				System.out.println("starting execution of serviceTask in multicloud!");//with anomaly
				//add1:providers.get(0).executeService(serviceTask,s);
				CreateAnomalServiceTaskFromSpecificAttackPossibility(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,taskNumber);				
				//CreateAnomalServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService);				
			}
			else {
				System.out.println("starting execution of serviceTask in multicloud!");//without anomaly
				//add2:providers.get(0).executeService(serviceTask,s);
				CreateNormalServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,taskNumber);
			}
		}
	}

	public void CreateAnomalServiceTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,Workflow workflow,String serviceTask,Service s, Service backupService,int taskNumber) throws IOException{
		//System.out.println("ServiceTask with  Anomaly!");
		String task;
		Random rand = new Random();
		int line=rand.nextInt(57755);
		FileInputStream fs= new FileInputStream("KDDanomal.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for(int i = 0; i < line; ++i)
		  br.readLine();
		task = br.readLine();
		
		//System.out.println("****************Normal serviceTask "+serviceTask);
		//System.out.println(workflow.getLegalServiceforTasks().size());
	
		ArrayList<Service> services = workflow.getLegalServiceforTasks().get(serviceTask);
		
		//Service anomalService=services.get(rand.nextInt(services.size()));
		//Service backupService = services.get(rand.nextInt(services.size()));
		
		System.out.println("selected service and backup: ");
		//System.out.println("AnomalService: ");
		s.print();
		//System.out.println("backupService: ");
		backupService.print();
	
		ServiceTask s1=new ServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName, serviceTask,s,backupService);
		serviceTasks.add(s1);
		s1.print();
		currentTenant=workflow.getTenant();
		//System.out.println("****************current tenant "+currentTenant.getId());
		currentTenant.AdaptationDecisionEngine(s1,task,workflow,taskNumber);
		//addTaskToFile(workflow.getTenantAddress()+"   "+s1.ServiceTaskToStringOnExit()+" "+task,"Servicelog.txt");
		//addTaskToFile(s1.ServiceTaskToStringOnExit()+" "+task,workflow.getTenantAddress()+"Servicelog.txt");
	}

	public void CreateAnomalServiceTaskFromSpecificAttackPossibility(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,Workflow workflow,String serviceTask,Service s, Service backupService,int taskNumber) throws IOException{
		Random rand = new Random();
		int att=rand.nextInt(4);	
		if(att==0)
			CreateAnomalServiceTaskFromSpecificAttackType(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,"KDDdos.txt",54572,taskNumber);
		if(att==1)
			CreateAnomalServiceTaskFromSpecificAttackType(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,"KDDprobe.txt",2130,taskNumber);
		if(att==2)
			CreateAnomalServiceTaskFromSpecificAttackType(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,"KDDr2l.txt",999,taskNumber);
		if(att==3)
			CreateAnomalServiceTaskFromSpecificAttackType(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,workflow,serviceTask,s,backupService,"KDDu2r.txt",52,taskNumber);
	}
	public void CreateAnomalServiceTaskFromSpecificAttackType(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,Workflow workflow,String serviceTask,Service s, Service backupService,String file,int lineNumber,int taskNumber) throws IOException{
		//System.out.println("ServiceTask with  Anomaly!");
		String task;
		Random rand = new Random();
		int line=rand.nextInt(lineNumber);
		FileInputStream fs= new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for(int i = 0; i < line; ++i)
		  br.readLine();
		task = br.readLine();
		
		//System.out.println("****************Normal serviceTask "+serviceTask);
		//System.out.println(workflow.getLegalServiceforTasks().size());
	
		ArrayList<Service> services = workflow.getLegalServiceforTasks().get(serviceTask);
		
		//Service anomalService=services.get(rand.nextInt(services.size()));
		//Service backupService = services.get(rand.nextInt(services.size()));
		
		System.out.println("selected service and backup: ");
		//System.out.println("AnomalService: ");
		s.print();
		//System.out.println("backupService: ");
		backupService.print();
	
		ServiceTask s1=new ServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName, serviceTask,s,backupService);
		serviceTasks.add(s1);
		s1.print();
		currentTenant=workflow.getTenant();
		//System.out.println("****************current tenant "+currentTenant.getId());
		currentTenant.AdaptationDecisionEngine(s1,task,workflow,taskNumber);
		//addTaskToFile(workflow.getTenantAddress()+"   "+s1.ServiceTaskToStringOnExit()+" "+task,"Servicelog.txt");
		//addTaskToFile(s1.ServiceTaskToStringOnExit()+" "+task,workflow.getTenantAddress()+"Servicelog.txt");
	}


	public void CreateNormalServiceTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,Workflow workflow,String serviceTask,Service s, Service backupService,int taskNumber) throws IOException{
		String task;
		Random rand = new Random();
		int line=rand.nextInt(87833);
		FileInputStream fs= new FileInputStream("KDDnormal.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		for(int i = 0; i < line; ++i)
		  br.readLine();
		task = br.readLine();
		
		//System.out.println("****************Normal serviceTask "+serviceTask);
		//System.out.println(workflow.getLegalServiceforTasks().size());
	
		ArrayList<Service> services = workflow.getLegalServiceforTasks().get(serviceTask);
		//System.out.println("Service Siiiize: "+services.size());
		
		
		//Service normalService=services.get(rand.nextInt(services.size()));
		//Service backupService = services.get(rand.nextInt(services.size()));
		
		System.out.println("selected service and backup: ");
		//System.out.println("normalService: ");
		s.print();
		//System.out.println("backupService: ");
		backupService.print();
	
		ServiceTask s1=new ServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName, serviceTask,s,backupService);
		serviceTasks.add(s1);
		s1.print();
		currentTenant=workflow.getTenant();
		//System.out.println("****************current tenant "+currentTenant.getId());
		currentTenant.AdaptationDecisionEngine(s1,task,workflow,taskNumber);
		//addTaskToFile(workflow.getTenantAddress()+"   "+s1.ServiceTaskToStringOnExit()+" "+task,"Servicelog.txt");
		//addTaskToFile(s1.ServiceTaskToStringOnExit()+" "+task,workflow.getTenantAddress()+"Servicelog.txt");
	}
	
	
}
