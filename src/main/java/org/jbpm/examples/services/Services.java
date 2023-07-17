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
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class Services {
	int numOfTenants=2,numOfHosts=4;
	int serviceTaskAnomalyRate=10;// for each "serviceTaskAnomalyRate" service task one is anomaly
	int userTaskAnomalyRate=10;//for each "userTaskAnomalyRate" user task one is anomaly
	static String reportName=null;
	static boolean PoliceResult=false; 
	static boolean HospitalResult=false; 
	static Workflow workflow;
	static boolean createKDDs=false;
	TenantKernel currentTenant;
	static int taskNumber=0;

	
	
	//static Map<UserTask, UserTask> userTasks = new HashMap<UserTask, UserTask>();//map onEntryuserTask' propertises to onExituserTask' propertises
	static ArrayList<UserTask> userTasks = new ArrayList<UserTask>();//onExituserTask' propertises
	static ArrayList<ServiceTask> serviceTasks = new ArrayList<ServiceTask>();//onExitServiceTask' propertises

	//String[] parameters=["userTaskId", "instanceID", "processId", "taskId","userName","userTrust usedIP","day","durartion","numOfValids","numOfinvalids","inputTraffic","numOfCalls","numOfIlegalDataInOutput"];
	
	
	public Services(Workflow w) {
		this.workflow=w;
		//MC=new MultiCloudEnvironment(numOfTenants,numOfHosts);
		System.out.println("workflow in services "+workflow);

	}
	public Services() {
		//System.out.println("workflow "+workflow);
		//MC=new MultiCloudEnvironment(numOfTenants,numOfHosts);
	}
	
	public void setTaskNumber(int t) {
		taskNumber=t;
	}
	
	//########################################################################
	//for all Service and User Tasks
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
		 /*
		    try {
		        FileWriter myWriter = new FileWriter("Userlog.txt");
		        myWriter.write(task);
		        myWriter.close();
		        System.out.println("Successfully wrote to the file.");
		      } catch (IOException e) {
		        System.out.println("An error occurred.");
		        e.printStackTrace();
		      }*/
	}
	
	public void CreateAnomalUserTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String usertask) throws IOException{
		Random rand = new Random();	
		int anomalyType=rand.nextInt(9);
		switch(anomalyType) {
		case 0: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"User");
		case 1: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"IP");
		case 2: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"Day");
		case 3: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"durartion"); 
		case 4: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"numOfValids");
		case 5: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"numOfinvalids");
		case 6: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"numOfCalls");
		case 7: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"numOfIlegalDataInOutput"); 
		case 8: CreateUserTaskWithAnomaly(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask,"inputTraffic"); 
		}
		
	}
	
	
	public void CreateUserTaskWithAnomaly(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String usertask,String type) throws IOException{
		System.out.println("usertask with "+type+" Anomaly!");
		Random rand = new Random();	
		UserTask u1;
		User user;//user assigned to the task
		String usedIP;
		String  day;
		int durartion,numOfValids,numOfinvalids,numOfCalls,numOfIlegalDataInOutput,inputTraffic;
		
		
		currentTenant=workflow.getTenant();
		//public UserTask(long instanceId,String processId, String taskId, User userName ,String usedIP, String day,double durartion,int numOfValids,int numOfinvalids,long inputTraffic,int numOfCalls,int numOfIlegalDataInOutput) {
		ArrayList<User> users=null;
		if(type.equals("User"))
			users = currentTenant.getUsers();
		else
			users = workflow.getLegalUsersforTasks().get(usertask);
		
		user=users.get(rand.nextInt(users.size()));
		//User user=workflow.getLegalUsersforTasks().get("InitialClaimUser").get(rand.nextInt(users.size()));

		if(type.equals("IP"))
			usedIP=currentTenant.getIPAdresses().get(rand.nextInt(currentTenant.getIPAdresses().size()));
		else
			usedIP=user.getIp().get(rand.nextInt(user.getIp().size()));
		
		if(type.equals("Day"))
			day=currentTenant.getWorkingdays().get(rand.nextInt(currentTenant.getWorkingdays().size()));
		else
			day=currentTenant.getWorkingdays().get(rand.nextInt(currentTenant.getWorkingdays().size()));

		ArrayList<Integer> thresholds=workflow.getThresholds().get(usertask);
		if(type.equals("durartion"))
			durartion=rand.nextInt(100)+thresholds.get(0);
		else
			durartion=rand.nextInt(thresholds.get(0)-100+1)+100;
		if(type.equals("numOfValids"))
			numOfValids=rand.nextInt(10)+thresholds.get(1);
		else
			numOfValids=rand.nextInt(thresholds.get(1)-10+1)+10;
		if(type.equals("numOfinvalids"))
			numOfinvalids=rand.nextInt(10)+thresholds.get(2);
		else
			numOfinvalids=rand.nextInt(thresholds.get(2)+1);
		if(type.equals("numOfCalls"))
			numOfCalls=rand.nextInt(10)+thresholds.get(3);
		else
			numOfCalls=rand.nextInt(thresholds.get(3)-10+1)+10;
		if(type.equals("numOfIlegalDataInOutput"))
			numOfIlegalDataInOutput=rand.nextInt(10)+thresholds.get(4);
		else
			numOfIlegalDataInOutput=rand.nextInt(thresholds.get(4)+1);
		if(type.equals("inputTraffic"))
			inputTraffic=rand.nextInt(150000)+thresholds.get(5);
		else
			inputTraffic=rand.nextInt(thresholds.get(5)-150000)+150000;
		
		/*
		for(int t=0;t<thresholds.size();t++) 
			System.out.println("  "+thresholds.get(t));
		System.out.println(durartion+" "+numOfValids+" "+numOfinvalids+" "+numOfCalls+" "+numOfIlegalDataInOutput+" "+inputTraffic);
		*/
		/*
		for(String d:user.getDays()) {
			System.out.println(" Legalday "+d+" "+day);
		}
		
		for(String ip:user.getIp()) {
			System.out.println(" LegalIp "+ip+" "+usedIP);
		}
		for(User u:workflow.getLegalUsersforTasks().get(usertask)) {
			System.out.println(" LegalUsers "+u.getName()+" "+user.getName());
		}
		
		System.out.println(" LegalDurartion "+thresholds.get(0)+" "+durartion);
		System.out.println(" LegalnumOfValids "+thresholds.get(1)+" "+numOfValids);
		*/
		u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, usertask ,user,usedIP,day,durartion,numOfValids,numOfinvalids,numOfCalls,numOfIlegalDataInOutput,inputTraffic);
		//u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "InitialClaimUser",user,user.getIp().get(rand.nextInt(user.getIp().size())),user,user.getDays().get(rand.nextInt(user.getDays().size())));
		u1.printAll();
		
		//System.out.println("****************current tenant "+currentTenant.getId());
		currentTenant.localDetection_userPredictionModel(u1,"Anomaly",this.workflow,this.taskNumber);
		addTaskToFile(u1.UserTaskToStringOnExit()+"   Anomaly",workflow.getTenantAddress()+"Userlog.txt");
		userTasks.add(u1);	
	}
	
	
	public void CreateNormalUserTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String usertask) throws IOException{
		Random rand = new Random();	
		UserTask u1;
		User user;//user assigned to the task
		String usedIP;
		String  day;
		int durartion,numOfValids,numOfinvalids,numOfCalls,numOfIlegalDataInOutput,inputTraffic;
		
		//public UserTask(long instanceId,String processId, String taskId, User userName ,String usedIP, String day,double durartion,int numOfValids,int numOfinvalids,long inputTraffic,int numOfCalls,int numOfIlegalDataInOutput) {
		ArrayList<User> users = workflow.getLegalUsersforTasks().get(usertask);
		user=users.get(rand.nextInt(users.size()));
		//User user=workflow.getLegalUsersforTasks().get("InitialClaimUser").get(rand.nextInt(users.size()));

		usedIP=user.getIp().get(rand.nextInt(user.getIp().size()));
		day=user.getDays().get(rand.nextInt(user.getDays().size()));

		ArrayList<Integer> thresholds=workflow.getThresholds().get(usertask);
		durartion=rand.nextInt(thresholds.get(0)-100+1)+100;
		numOfValids=rand.nextInt(thresholds.get(1)-10+1)+10;
		numOfinvalids=rand.nextInt(thresholds.get(2)+1);
		numOfCalls=rand.nextInt(thresholds.get(3)-10+1)+10;
		numOfIlegalDataInOutput=rand.nextInt(thresholds.get(4)+1);
		inputTraffic=rand.nextInt(thresholds.get(5)-150000)+150000;

		/*
		for(int t=0;t<thresholds.size();t++) 
			System.out.println("  "+thresholds.get(t));
		System.out.println(ProcessInstance_Id+" "+usedIP+" "+day+" "+user+" "+durartion+" "+numOfValids+" "+numOfinvalids+" "+numOfCalls+" "+numOfIlegalDataInOutput+" "+inputTraffic);
		*/
		
		u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, usertask ,user,usedIP,day,durartion,numOfValids,numOfinvalids,numOfCalls,numOfIlegalDataInOutput,inputTraffic);
		//u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "InitialClaimUser",user,user.getIp().get(rand.nextInt(user.getIp().size())),user,user.getDays().get(rand.nextInt(user.getDays().size())));
		u1.printAll();
		currentTenant=workflow.getTenant();
		//System.out.println("****************current tenant "+currentTenant.getId());
		currentTenant.localDetection_userPredictionModel(u1,"Normal",this.workflow,this.taskNumber);
		addTaskToFile(u1.UserTaskToStringOnExit()+"   Normal",workflow.getTenantAddress()+"Userlog.txt");
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
		addTaskToFile(timestamp+"\t"+currentTenant.getId()+"\t"+workflow.getId()+"\t"+ u1.getInstanceId()+"\t "+taskNumber+"\t UserTask \t Normal ","adaptationLogEdoc.txt");//add this adaptation to file 

		userTasks.add(u1);
	}
	
	public void CreateUserTask (long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String usertask) throws IOException{
		Random rand = new Random();	
		//int anomaly=rand.nextInt(userTaskAnomalyRate);
		int anomaly=rand.nextInt(2);//for test
		//System.out.println("anomaly random"+anomaly);
		if(anomaly<7) {//usertask withOut Anomaly
			System.out.println("usertask withOut Anomaly!");
			CreateNormalUserTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask);
			//u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "InitialClaimUser",users.get(rand.nextInt(users.size())));
		}
		else {//usertask with Anomaly
			
			System.out.println("usertask with Anomaly!");
			CreateAnomalUserTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,usertask);

			
			
		}
	}
	
	public void CreateAnomalServiceTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String serviceTask) throws IOException{
		System.out.println("ServiceTask with  Anomaly!");
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
		
		Service anomalService=services.get(rand.nextInt(services.size()));
		Service backupService = services.get(rand.nextInt(services.size()));
		

		System.out.println("AnomalService: ");
		anomalService.print();
		System.out.println("backupService: ");
		backupService.print();
	
		ServiceTask s1=new ServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName, serviceTask,anomalService,backupService);
		serviceTasks.add(s1);
		s1.print();
		currentTenant=workflow.getTenant();
		//System.out.println("****************current tenant "+currentTenant.getId());
		currentTenant.AdaptationDecisionEngine(s1,task,workflow,10);
		addTaskToFile(workflow.getTenantAddress()+"   "+s1.ServiceTaskToStringOnExit()+" "+task,"Servicelog.txt");
		addTaskToFile(s1.ServiceTaskToStringOnExit()+" "+task,workflow.getTenantAddress()+"Servicelog.txt");
	}
	
	
	public void CreateNormalServiceTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String serviceTask) throws IOException{
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
		
		
		Service normalService=services.get(rand.nextInt(services.size()));
		Service backupService = services.get(rand.nextInt(services.size()));
		

		System.out.println("normalService: ");
		normalService.print();
		System.out.println("backupService: ");
		backupService.print();
	
		ServiceTask s1=new ServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName, serviceTask,normalService,backupService);
		serviceTasks.add(s1);
		s1.print();
		currentTenant=workflow.getTenant();
		//System.out.println("****************current tenant "+currentTenant.getId());
		currentTenant.AdaptationDecisionEngine(s1,task,workflow,10);
		addTaskToFile(workflow.getTenantAddress()+"   "+s1.ServiceTaskToStringOnExit()+" "+task,"Servicelog.txt");
		addTaskToFile(s1.ServiceTaskToStringOnExit()+" "+task,workflow.getTenantAddress()+"Servicelog.txt");
	}
	
	public void CreateServiceTask (long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String serviceTask) throws IOException{
		
		//**********Create KDDnormal.txt and KDDanomal.txt for the first time
		/*if(createKDDs==false) {
			CreateKDDFiles();
			createKDDs=true;
			}
		*/
		Random rand = new Random();	
		int anomaly=rand.nextInt(serviceTaskAnomalyRate);
		//int anomaly=rand.nextInt(2);//for test

		if(anomaly<3) {
			System.out.println("serviceTask withOut Anomaly!");
			CreateNormalServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,serviceTask);
			
		}
		else {
			
			System.out.println("serviceTask with Anomaly!");
			CreateAnomalServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,serviceTask);		
		}
	}
	//########################################################################

	public void SetOnEntryParametersInitialClaimUser(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId)throws IOException  {
		System.out.println("**************** UserTask: InitialClaimUser ****************");
		System.out.println("OnEntryParametersInitialClaimUser ");
		//ArrayList<User> users = workflow.getLegalUsersforTasks().get("InitialClaimUser");
		//Random rand = new Random();	
		//UserTask u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "InitialClaimUser",users.get(rand.nextInt(users.size())));
		//addUserTaskToFile(u1);
		//u1.print();
		this.taskNumber++;
	}
	
	public void SetOnEntryParametersObtainClaimService(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("**************** ServiceTask: ObtainClaimService ****************");
		System.out.println("OnEntryParametersObtainClaimService ");
		//ArrayList<Service> services = workflow.getLegalServiceforTasks().get("ObtainClaimService");
		//Random rand = new Random();	
		//ServiceTask s1=new ServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName, "ObtainClaimService",services.get(rand.nextInt(services.size())));
		//s1.print();
		this.taskNumber++;
	}
	
	public void SetOnEntryParametersObtainHospitalReport(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("**************** ServiceTask: ObtainHospitalReport ****************");
		System.out.println("OnEntryParametersObtainHospitalReport ");
		//ArrayList<User> users = workflow.getLegalUsersforTasks().get("ObtainHospitalReport");
		//Random rand = new Random();	
		//UserTask u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "ObtainHospitalReport",users.get(rand.nextInt(users.size())));
		//u1.print();
		this.taskNumber++;
	}
	
	
	public void SetOnEntryParametersObtainPoliceReport(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("**************** ServiceTask: ObtainPoliceReport ****************");
		System.out.println("OnEntryParametersObtainPoliceReport ");
		//ArrayList<User> users = workflow.getLegalUsersforTasks().get("ObtainPoliceReport");
		//Random rand = new Random();	
		//UserTask u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "ObtainPoliceReport",users.get(rand.nextInt(users.size())));
		//u1.print();
		this.taskNumber++;
	}
	
	public void SetOnEntryParametersSelectBestExpert(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("**************** ServiceTask: SelectBestExpert ****************");
		System.out.println("OnEntrySetParametersSelectBestExpert ");
		//ArrayList<Service> services = workflow.getLegalServiceforTasks().get("SelectBestExpert");
		//Random rand = new Random();	
		//ServiceTask s1=new ServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName, "SelectBestExpert",services.get(rand.nextInt(services.size())));
		//s1.print();
		this.taskNumber++;
	}
	
	public void SetOnEntryParametersReimbursementDecision(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("**************** ServiceTask: ReimbursementDecision ****************");
		System.out.println("OnEntrySetParametersReimbursementDecision ");
		//ArrayList<User> users = workflow.getLegalUsersforTasks().get("ReimbursementDecision");
		//Random rand = new Random();	
		//UserTask u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "ReimbursementDecision",users.get(rand.nextInt(users.size())));
		//u1.print();
		this.taskNumber++;
	}
	
	public void SetOnEntryParametersCustomerFeedbackCollection(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("****************ServiceTask: CustomerFeedbackCollection**************** ");
		System.out.println("OnEntrySetParametersCustomerFeedbackCollection ");
		//ArrayList<User> users = workflow.getLegalUsersforTasks().get("ReimbursementDecision");
		//Random rand = new Random();	
		//UserTask u1=new UserTask(ProcessInstance_Id,ProcessInstance_ProcessName, "ReimbursementDecision",users.get(rand.nextInt(users.size())));
		//u1.print();
		this.taskNumber++;
	}

	
	public void SetOnExitParametersInitialClaimUser(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitParametersInitialClaimUser ");
		CreateUserTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"InitialClaimUser");
		System.out.println("****************end InitialClaimUser ****************");

	}
	
	
	public void SetOnExitParametersObtainClaimService(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitParametersObtainClaimService ");
		//MC.ExecuteServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"ObtainClaimService");
		currentTenant=workflow.getTenant();
		this.currentTenant.getKernelManagment().getMultiCloudEnvironment().AssigningServiceToTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,this.workflow,"ObtainClaimService",this.taskNumber);
		//CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"ObtainClaimService");
		System.out.println("****************end ObtainClaimService****************");

	}
	
	
	public void SetOnExitParametersObtainHospitalReport(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitParametersObtainHospitalReport ");
		//CreateUserTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"ObtainHospitalReport");
		currentTenant=workflow.getTenant();
		this.currentTenant.getKernelManagment().getMultiCloudEnvironment().AssigningServiceToTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,this.workflow,"HospitalReport",this.taskNumber);
		//CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"HospitalReport");
		System.out.println("****************end HospitalReport****************");
	}
	
	public void SetOnExitParametersObtainPoliceReport(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitParametersObtainPoliceReport ");
		//CreateUserTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"ObtainPoliceReport");
		currentTenant=workflow.getTenant();
		this.currentTenant.getKernelManagment().getMultiCloudEnvironment().AssigningServiceToTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,this.workflow,"PoliceReport",this.taskNumber);
		//CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"PoliceReport");
		System.out.println("****************end PoliceReport ****************");
	}
	
	public void SetOnExitParametersSelectBestExpert(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitSetParametersSelectBestExpert ");
		//MC.ExecuteServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"SelectBestExpert");
		currentTenant=workflow.getTenant();
		this.currentTenant.getKernelManagment().getMultiCloudEnvironment().AssigningServiceToTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,this.workflow,"SelectBestExpert",this.taskNumber);
		//CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"SelectBestExpert");
		System.out.println("****************end SelectBestExpert****************");
	}
	
	public void SetOnExitParametersReimbursementDecision(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitSetParametersReimbursementDecision ");
		//CreateUserTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"ReimbursementDecision");
		currentTenant=workflow.getTenant();
		this.currentTenant.getKernelManagment().getMultiCloudEnvironment().AssigningServiceToTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,this.workflow,"BankReimbursment",this.taskNumber);
		//CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"BankReimbursment");
		System.out.println("****************end BankReimbursment****************");
	}
	
	public void SetOnExitParametersCustomerFeedbackCollection(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitSetParametersCustomerFeedbackCollection ");
		currentTenant=workflow.getTenant();
		this.currentTenant.getKernelManagment().getMultiCloudEnvironment().AssigningServiceToTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,this.workflow,"CustomerFeedbackCollection",this.taskNumber);
		//CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"CustomerFeedbackCollection");
		System.out.println("****************end CustomerFeedbackCollection****************");
	}
	
	
	
	public static void SetOnEntryParameters(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("OnEntryParameters "+ProcessInstance_Id+" "+ProcessInstance_ProcessName+" "+ProcessInstance_ProcessId);
		//System.out.println(" my name: "+name);
				//System.out.println("processInstanceIdfromusertask = "+kcontext.getProcessInstance().getId());
				//ProcessContext kcontext = new ProcessContext(this.kSession);
				try {createAndWrite( "ali");
				} catch(Exception e) {
				      e.getStackTrace();
				    }

	}
	
	public static void SetOnExitParameters(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("OnExitParameters "+ProcessInstance_Id+" "+ProcessInstance_ProcessName+" "+ProcessInstance_ProcessId);
	}
	
	public static void createAndWrite(String context) throws IOException {
		 File file = new File("log.txt");

		    try {

		      // create a new file with name specified
		      // by the file object
		      boolean value = file.createNewFile();
		      if (value) {
		        System.out.println("New Java File is created.");
		      }
		      else {
		        System.out.println("The file already exists.");
		      }
		    }
		    catch(Exception e) {
		      e.getStackTrace();
		    }
	}
	public void  InitialClaimUser(String name) throws IOException{
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
	            e.printStackTrace();
			}*/
		System.out.println("InitialClaimUser is done!");
	}
	
	public void ObtainClaimService(String name)throws IOException {
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		System.out.println(" ObtainClaimService is done!");
		//System.out.println(" my name: "+name);
		
	}
	
	public void ObtainHospitalReport(String name)throws IOException {
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		Random rand = new Random();
		int p=rand.nextInt(10);
		if(p!=0)
			HospitalResult=true;
		System.out.println("ObtainHospitalReport is done!");
	}
	
	public void ObtainPoliceReport(String name)throws IOException {
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		Random rand = new Random();
		int p=rand.nextInt(10);
		if(p!=0)
			PoliceResult=true;
		System.out.println("ObtainPoliceReport is done!");
	}
	
	
	public void Initial(String name)throws IOException {
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		System.out.println("InitialClaim of " + name);
		System.out.println(" my name: "+name);
		if(this.reportName==null)
			this.reportName=name;
	}
	
	
	public void SelectBestExpert(String name)throws IOException {
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		Random rand = new Random();
		int expert=rand.nextInt(10);
		System.out.println("Expert: " + expert+" is selected for this process");
		System.out.println("SelectBestExpert is done!");
	}
	
	public void GetResult(String name)throws IOException {
		/*try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		boolean r=false;
		String result="Rejected";
		if(this.PoliceResult==true && this.HospitalResult==true) 
			r=true;
		if(r==true)
			result="Accepted";
		System.out.println("Result of the report is: " + result);
	}
	
	public void ReimbursementDecision(String name) throws IOException{
	/*
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		boolean r=false;
		String result="Rejected";
		if(this.PoliceResult==true && this.HospitalResult==true) 
			r=true;
		if(r==true)
			result="Accepted";
		System.out.println("Reimbursement is: " + result);
		System.out.println("ReimbursementDecision is done! ");
	}
	
	public void CustomerFeedbackCollection(String name)throws IOException {
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}*/
		System.out.println("CustomerFeedbackCollection is done! ");
	}
	
	
	
	public void setWorkflow(Workflow w) {
		this.workflow=w;
	}
	
	public Workflow getWorkflow() {
		return workflow;
	}
	/*
	public ArrayList<UserTask> CreateServiceTasks(String bpmn) {
		if(bpmn=="InsuranceClaim6.bpmn2") {
			//ArrayList<UserTask> userTasks = new ArrayList<UserTask>();
			//UserTask u1=new UserTask(0);
			
		}
		if(bpmn=="BPMN2-ServiceProcess2.bpmn2") {
			
		}
		
	}
	
	public ArrayList<ServiceTask> CreateUserTasks(String bpmn) {
		if(bpmn=="InsuranceClaim6.bpmn2") {
			
			
		}
		if(bpmn=="BPMN2-ServiceProcess2.bpmn2") {
			
		}
	}
*/
	
	public void CreateKDDFiles()throws IOException {
		// File path is passed as parameter
		File file = new File("KDD.txt");

		// Note: Double backquote is to avoid compiler
		// interpret words
		// like \test as \t (ie. as a escape sequence)

		// Creating an object of BufferedReader class
		BufferedReader br
			= new BufferedReader(new FileReader(file));

		// Declaring a string variable
		String st;
		// Condition holds true till
		// there is character in a string
		while ((st = br.readLine()) != null) {
		//for(int i=0;i<10;i++) {
			//System.out.println(br.readLine());
			if(st.contains("normal"))
				addTaskToFile(st,"KDDnormal.txt");
			else
				addTaskToFile(st,"KDDanomal.txt");
			}
	}

}
