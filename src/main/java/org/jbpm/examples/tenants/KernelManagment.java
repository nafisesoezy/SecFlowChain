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

package org.jbpm.examples.tenants;
import org.jbpm.examples.services.*;
import java.util.ArrayList;
import java.util.Map; 
import java.util.HashMap;
import java.util.Random;

import org.jbpm.test.JbpmJUnitBaseTestCase;
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
import org.jbpm.test.JbpmJUnitBaseTestCase;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.*;
import java.io.FileWriter; 
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 
import java.util.Random;
import java.util.*;

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


public class KernelManagment extends JbpmJUnitBaseTestCase{
	
	MultiCloudEnvironment MC;
	int numOfTenants;
	int numOfHosts;
	int numOfServices;
	int MaxNumOfUsers;
	int maxNumOfHosts;
	int minNumOfHosts;
	int numOfRecordsPerService=100;
	
	private String id;
	ArrayList<TenantKernel> users = new ArrayList<TenantKernel>();
	ArrayList<Service> services = new ArrayList<Service>();
	ArrayList<Attack> attackTypes;


	//use local services
	public KernelManagment(int numOfT,int numOfH,int numOfS,int MaxNumOfU,int maxNumOfH,int minNumOfH)throws IOException {
		numOfTenants=numOfT;
		numOfHosts=numOfH;
		numOfServices=numOfS;
		MaxNumOfUsers=MaxNumOfU;
		maxNumOfHosts=maxNumOfH;
		minNumOfHosts=minNumOfH;
		System.out.println("******** KernelManagment ********");
		RuntimeEngine runtime =createRuntimeManager("InsuranceClaim6.bpmn2","BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null);	
		

		attackTypes=createAttacksFeathers();
		
		//option1:Create ServiceRepository for the first time(based on ServiceNames.txt) and create logfile for them(based on numOfRecordsPerService)
		//createServices("ServiceNames.txt");
		//createServicesModels();
		
		//option2:Create services from ServiceRepository
		createServicesFormServiceRepository();
		
		SecurityRiskAssesment();//Calculate AFR(Attack Frequency Rate)*AttackImpact for each service
		
		numOfTenants=10;
		for(int t=0;t<numOfTenants;t++) {//for each tenant
			
			String tenantName="tenant"+t;
			TenantKernel tenant=new TenantKernel(tenantName,runtime,maxNumOfHosts,minNumOfHosts,MaxNumOfUsers,attackTypes,this);
			
		}//end of tenant
		
	}
	
	//Use CloudServices***************
	public KernelManagment(int numOfT,int numOfH,int numOfS,int MaxNumOfU,int maxNumOfH,int minNumOfH,boolean cloudService)throws IOException {
		numOfTenants=numOfT;
		numOfHosts=numOfH;
		numOfServices=numOfS;
		MaxNumOfUsers=MaxNumOfU;
		maxNumOfHosts=maxNumOfH;
		minNumOfHosts=minNumOfH;
		
		System.out.println("******** KernelManagmentWithCloudServices ********");
		/*
		ProviderPlusSimulateAttacks ppi=new ProviderPlusSimulateAttacks(1);
		ppi.executeService("SelectBestExpert");
		*/
		//1-RuntimeEngine runtime =createRuntimeManager("InsuranceClaim6.bpmn2","BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null);	
		//Coopis
		//RuntimeEngine runtime =createRuntimeManager("InsuranceClaim9.bpmn2","BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null);	
		//EDOC
		RuntimeEngine runtime =createRuntimeManager("InsuranceClaim9.bpmn2","InsuranceClaim9-2.bpmn2","InsuranceClaim9-3.bpmn2","InsuranceClaim10.bpmn2","InsuranceClaim11.bpmn2","InsuranceClaim12.bpmn2","InsuranceClaim13.bpmn2","InsuranceClaim14.bpmn2","InsuranceClaim15.bpmn2","InsuranceClaim16.bpmn2","InsuranceClaim17.bpmn2","InsuranceClaim18.bpmn2","InsuranceClaim19.bpmn2","InsuranceClaim20.bpmn2","InsuranceClaim21.bpmn2","InsuranceClaim21-2.bpmn2","InsuranceClaim21-3.bpmn2","InsuranceClaim22.bpmn2","InsuranceClaim22-2.bpmn2","InsuranceClaim22-3.bpmn2","BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null);	
		//RuntimeEngine runtime =createRuntimeManager("InsuranceClaim9.bpmn2","InsuranceClaim10.bpmn2","InsuranceClaim11.bpmn2","InsuranceClaim12.bpmn2","InsuranceClaim13.bpmn2","InsuranceClaim14.bpmn2","InsuranceClaim15.bpmn2","InsuranceClaim16.bpmn2","InsuranceClaim17.bpmn2","InsuranceClaim18.bpmn2","InsuranceClaim19.bpmn2","InsuranceClaim20.bpmn2","InsuranceClaim21.bpmn2","BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null);	

		//the impact of different attacks on confidentiality,intergrity, availabilty(based on attackImpacts.txt)+ sutable adaptation action for eeach of them in diffrent levels(low, medium, high) based on attackAdaptationPattern.txt
		attackTypes=createAttacksFeathers();//coopis
		
		
		//create Multi-Cloud environments with different providers and services.
		CreateMultiCloudEnvironment();
		
		numOfTenants=1;
		for(int t=0;t<numOfTenants;t++) {//for each tenant
			
			String tenantName="tenant"+t;
			TenantKernel tenant=new TenantKernel(tenantName,runtime,maxNumOfHosts,minNumOfHosts,MaxNumOfUsers,attackTypes,this);
			
		}//end of tenant
		
	}
	
	public MultiCloudEnvironment getMultiCloudEnvironment() {
		return MC;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public ArrayList<Service> getServices(String serviceName) {
		ArrayList<Service> ss = new ArrayList<Service>();
		for(Service service:this.services) {
			if(service.getName().equals(serviceName)) {
				ss.add(service);
			}
		}
		return ss;
	}
	
	
	public ArrayList<Service> getAllServices() {
		return services;
	}
	
	
	public void CreateMultiCloudEnvironment() throws IOException{
		MC=new MultiCloudEnvironment(numOfTenants,numOfHosts);
		
		//option1:Create ServiceRepository for the first time(based on ServiceNames.txt) and create logfile for them(based on numOfRecordsPerService)
		//createServices("ServiceNames.txt");
		//createServicesModels();
		
		//option2: Create services from ServiceRepository
		createServicesFormServiceRepository();
		
		//Calculate AFR(Attack Frequency Rate)*AttackImpact for each service
		SecurityRiskAssesment();
		
	}
	
	public ArrayList<Attack> createAttacksFeathers() throws IOException {
		ArrayList<Attack> AttackTypes=new ArrayList<Attack>();
		String attacktype="";
		ArrayList<String>  attacks=ReadLinebyLine("attackImpacts.txt");	
		ArrayList<String>  attackAdaptationPatterns=ReadLinebyLine("attackAdaptationPattern.txt");	
		String delimsComma = "[,]+";
		for(int line=0;line<attacks.size();line++) {
			//attack security impact: 
			ArrayList<Double> securityImpact = new ArrayList<Double>();
			String[] arr2  = attacks.get(line).split(delimsComma);
			int i=0;
			for (String obj : arr2) {
				if(i==0) {
					i=1;
					attacktype=obj;
				}
				else
					securityImpact.add(Double.parseDouble(obj));
			}
			//adaptation pattern:
			Map<String, ArrayList<String>> attackAdaptationPattern = new HashMap<String, ArrayList<String>>();
			String[] arr3  = attackAdaptationPatterns.get(line).split(";");
			ArrayList<String> adaptationsLow=new ArrayList<String>();
			ArrayList<String> adaptationsMedium=new ArrayList<String>();
			ArrayList<String> adaptationsHigh=new ArrayList<String>();
			int j=0;
			for (String obj : arr3) {
				if(j==0) {
					j=1;
					attacktype=obj;
				}
				else {
					String severity="";
					String[] arr4= obj.split(delimsComma);


					for (String adapt : arr4) {
						
						if(adapt.equals("low")) {
							severity="low";
							//System.out.println("severityLow0 "+severity+" adaptations "+adapt);
						}
						else if(severity.equals("low") && !adapt.equals("medium")) {
							adaptationsLow.add(adapt);
							//System.out.println("severityLow1 "+severity+" adaptations "+adapt);
							//System.out.println("print adaptations size "+adaptationsLow.size());

						}
						else if(adapt.equals("medium")){
							//System.out.println("print adaptations size "+adaptationsLow.size());
							severity="medium";
							//attackAdaptationPattern.put("low",adaptationsLow);
							//System.out.println("print100 "+attackAdaptationPattern.get("low")+" "+adaptationsLow.size());
							//System.out.println("severitymedium1 "+severity+" adaptations "+adapt);

						}
						else if(severity.equals("medium") && !adapt.equals("high")) {
							adaptationsMedium.add(adapt);
							//System.out.println("severitymedium2 "+severity+" adaptations "+adapt);

						}
						else if(adapt.equals("high")) {
							severity="high";
							//attackAdaptationPattern.put("medium",adaptationsMedium);
							//System.out.println("print "+attackAdaptationPattern.get("medium"));
							//System.out.println("severityhigh1 "+severity+" adaptations "+adapt);

						}
						else if(severity.equals("high")) {
							adaptationsHigh.add(adapt);
							//System.out.println("severityhigh2 "+severity+" adaptations "+adapt);

						}
					}
					//System.out.println("print "+attackAdaptationPattern.get("low")+" "+adaptationsLow.size());
					attackAdaptationPattern.put("low",adaptationsLow);
					attackAdaptationPattern.put("medium",adaptationsMedium);
					attackAdaptationPattern.put("high",adaptationsHigh);

				}
			}
	
			Attack a=new Attack(attacktype,securityImpact,attackAdaptationPattern);
			a.print();
			AttackTypes.add(a);
		}
		/* for the first time
		//create attackfiles for each attack type	
		String[] attackTypes= {"dos","probe","r2l","u2r"};
		for(String attack:attackTypes) 
			CreateAttackFile(attack);
			*/
		return AttackTypes;	
	}
	

	public ArrayList<String> ReadLinebyLine(String File) {
		BufferedReader reader;
		ArrayList<String> lines=new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(File));
			String line = reader.readLine();

			while (line != null) {
				//System.out.println(line);
				// read next line
				lines.add(line);
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		//printList(lines);
		return lines;
	}
	
	public void createServices(String file) {
		Random rand = new Random();
		ArrayList<String>  ServiceNames=ReadLinebyLine(file);
		for(String serviceName:ServiceNames) {
			
			//option1:variabe number of services exist 
			//int numOfServices=rand.nextInt(maxNumOfHosts-minNumOfHosts)+minNumOfHosts;
			
			//option2:fix number of services available
			int numOfServices=numOfHosts;
					
					
			ArrayList<Integer>  hosts=new ArrayList<Integer>();
			for(int i=0;i<maxNumOfHosts;i++) {
				hosts.add(i);
			}
			//int numOfServices=rand.nextInt(MaxServiceVariety-MinServiceVariety)+MinServiceVariety;
			for(int i=0;i<numOfServices;i++) {
				double trust=1;
				//Service service=new  Service(i,serviceName,rand.nextInt(numOfHosts),trust);
				int hostId=createUniqueHost(hosts);
				hosts.remove(Integer.valueOf(hostId));
				Service service=new  Service(i,serviceName,hostId,trust);
				service.print();
				this.services.add(service);
			}
		}
		
	}
	
	public int createUniqueHost(ArrayList<Integer> nums) {
		Random rand = new Random();
		/*
		for(int i:nums) {
			System.out.println(i);

		}
		*/
		return nums.get(rand.nextInt(nums.size()));
	}
	
	public void createServicesModels()throws IOException{
		for(Service s:this.services)
			createServiceLogfiles(s);
	}
	
	public void createServiceLogfiles(Service s) throws IOException{
		for(int k=0;k<3;k++){
		s.setRepositoryAddress("ServiceRepository/"+s.getName()+"_"+s.getId()+"_"+k+".txt");
		addTaskToFile(s.ServiceToString(),"ServiceRepository/"+s.getName()+"_"+s.getId()+"_"+k+".txt");
		for(int R=0;R<numOfRecordsPerService;R++) {		
				Random rand = new Random();
				int line=rand.nextInt(145587);
				FileInputStream fs= new FileInputStream("KDD.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fs));
				for(int i = 0; i < line; ++i)
				  br.readLine();
				String record = br.readLine();
				addTaskToFile(record,"ServiceRepository/"+s.getName()+"_"+s.getId()+"_"+k+".txt");
			}
		}
	}
	
	public void createServicesFormServiceRepository() throws IOException{
		File folder = new File("ServiceRepository/");
		File[] listOfFiles = folder.listFiles();
		double trust=1;
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		        FileInputStream fs= new FileInputStream("ServiceRepository/"+file.getName());
				BufferedReader br = new BufferedReader(new InputStreamReader(fs));
				String record = br.readLine();
				String delimsComma = "[,]+";
				String[] arr1  = record.split(delimsComma);
				Service service=new  Service(Integer.parseInt(arr1[0]),arr1[1],Integer.parseInt(arr1[2]),trust,"ServiceRepository/"+file.getName());
				service.print();
				this.services.add(service);
		    }
		}
		
	}
	
	public void SecurityRiskAssesment() {
		for(Service s:this.services) {
			s.SecurityRiskAssesment(this.attackTypes);
		}
	}
	
	public void ProviderService_Repository() {
		
	}
	
	public void globalDetection_providerTrainingModel() {
		
	}
	
	public void GlobalAdaptation_ServiceAdaptation(String adaptation,ServiceTask s,Workflow workflow) throws IOException{
		System.out.println("************ GlobalAdaptation_ServiceAdaptation **************");
		if(adaptation.equals("ReExecute"))
			this.MC.AssigningBackupServiceToTask(s.getBackupService(),workflow,s.getTaskId());
		else
			Reconfigration(s,workflow);
	}
	
	public void Reconfigration(ServiceTask s,Workflow workflow) {
		System.out.println("	Reconfigration...");

	}
	
	public void addTaskToFile(String task,String file) throws IOException {
		 try {
			 //file=workflow.getTenantAddress()+file;
			 //System.out.println(file);
		      File myObj = new File(file);
		      if (myObj.createNewFile()) {
		        System.out.println("File created: " + myObj.getName());
		      } else {
		        //System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		    
		 try (FileWriter f = new FileWriter(file, true);
				 BufferedWriter b = new BufferedWriter(f);
				 PrintWriter p = new PrintWriter(b);){
			 p.println(task);			
			 //System.out.println("Task Successfully wrote to the file.");
			 } catch (IOException i){
				 i.printStackTrace(); 
				}
		 
	}
	
	public void CreateAttackFile(String attack)throws IOException {
		   File file = new File("KDDanomal.txt");

		    // Creating an object of BufferedReader class
		    BufferedReader br = new BufferedReader(new FileReader(file));

		    // Declaring a string variable
		    String st;

		    // Condition holds true till there is a line in the CSV file
		    while ((st = br.readLine()) != null) {
		        if(st.contains(attack))
		        	 addTaskToFile(st, "KDD"+attack+".txt");
		    }
		}

	
}
