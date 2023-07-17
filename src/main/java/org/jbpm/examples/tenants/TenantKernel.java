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
import java.util.*;
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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.FileWriter; 
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TenantKernel {
	double attackThresh=2;

	//Coopis
	//double priceWeight=0.1,timeWeight=0.1,valueWeight=0.1,securityWeight=0.7;
	double priceWeight=0.3,timeWeight=0.3,valueWeight=0.3,securityWeight=0.1;
	//edoc
	//double priceWeight=0.3,timeWeight=0.3,valueWeight=0.2,securityWeight=0.4;

	double adaptationTimeOverhead=5;
	double valueOfReconfigration=5;
	double timeOfReconfigration=10,timeOfIncreaseMonitoring=100,timeOfEnhancingSecurityMeasures=100,timeOfRestrictingAccess=100;
	double costOfReconfigration=5,costOfIncreaseMonitoring=100,costOfEnhancingSecurityMeasures=100,costOfRestrictingAccess=100;

	
	int maxNumOfHosts;
	int minNumOfHosts;
	int numOfUsers;
	int numberOfDetectedattacks=0;
	//lowest cost parameters in coopis
	static double coopisTime=0, coopisPrice=0,coopisValue=0, coopisScore=0,coopisTimeMax=0, coopisPriceMax=0,coopisValueMax=0, coopisScoreMax=0;	
	Map<Integer, ArrayList<Double>> CoopisResults = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore
	static int completeVersion=0;
	ArrayList<ActionRecord> ActionRecords = new ArrayList<ActionRecord>();
	//adaptive parameters in coopis
	static double coopisTimeAdaptive=0, coopisPriceAdaptive=0,coopisValueAdaptive=0, coopisScoreAdaptive=0,coopisTimeMaxAdaptive=0, coopisPriceMaxAdaptive=0,coopisValueMaxAdaptive=0, coopisScoreMaxAdaptive=0;	
	Map<Integer, ArrayList<Double>> CoopisResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore
	Map<Integer, ArrayList<Double>> CoopisResultsAdaptive = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore
	static int completeVersionAdaptive=0;
	ArrayList<ActionRecord> ActionRecordsAdaptive = new ArrayList<ActionRecord>();
	QLearning qlearning;
	
	static double edocTime=0, edoPrice=0, edocScore=0,edocTimeMax=0, edoPriceMax=0, edocScoreMax=0;
	Map<Integer, ArrayList<Double>> EdocResults = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,MitigationScore


	private String id;
	ArrayList<User> users = new ArrayList<User>();
	ArrayList<Attack> AttackTypes = new ArrayList<Attack>();
	//ArrayList<User> userTasks = new ArrayList<User>();
	//ArrayList<User> serviceTasks = new ArrayList<User>();
	ArrayList<Workflow> workflows = new ArrayList<Workflow>();
	Map<String, String> instances = new HashMap<String, String>();
	private static RuntimeEngine runtime;
	ArrayList<String> UserNames;
	ArrayList<String> IPAdresses;
	ArrayList<String>  workingdays;
	static KernelManagment KM;
	

	public TenantKernel(String id,RuntimeEngine runtime1,int maxNumOfH,int minNumOfH,int numOfU,ArrayList<Attack> attacks,KernelManagment km) throws IOException{
		setId(id);
		maxNumOfHosts=maxNumOfH;
		minNumOfHosts=minNumOfH;
		numOfUsers=numOfU;
		this.AttackTypes=attacks;
		this.KM=km;
		System.out.println("********"+ id+ " is created ********");
		this.runtime=runtime1;
		createUsers();
		//Services s=new Services(createWorkflowModel("InsuranceClaim6.txt"));
		//Services s=new Services(createWorkflowModel("InsuranceClaim6-withAdaptation.txt"));
		//2-Services s=new Services(createWorkflowModel("InsuranceClaim6-withAdaptation-withPriceTimeValue.txt"));
		//Services s=new Services(createWorkflowModel("InsuranceClaim9-withAdaptation-withPriceTimeValue.txt"));
		Services s=new Services(createWorkflowModel("InsuranceClaim9-withAdaptation-withPriceTimeValue-moreOptions.txt"));


		HelloNafiseService h=new HelloNafiseService(createWorkflowModel("BPMN2-ServiceProcess2.txt"));
		//createWorkflowModel("BPMN2-ServiceProcess2.txt");
		
		trustAwareSchedulingPlanner();
		
		int k=0;

	/*
		//Coopis experiments
		
		for(int i=0;i<10;i++) {
			qlearning= new QLearning();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("small "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("small "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//ExecuteWorkflow("insurance6",1,"Elena");
			//ExecuteWorkflow("insurance6-2",1,"Elena");
			ExecuteWorkflow("insurance6-3",1,"Elena");

			System.out.println("Number of detected attack for small bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End small "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("End small "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//if(completeVersion<4 || completeVersion>7) 
			//if(completeVersion<5)
					//addTaskToFile("InComplete Version: numofTasks"+completeVersion,"LearningAdaptationLogCoopis.txt");
			//else {
				values.add(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead));
				values.add(coopisPrice);
				values.add(coopisValue);
				values.add(coopisScore);
				CoopisResults.put(i,values);
				System.out.println("COOPIS Time:"+coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead)+" "+coopisPrice+" "+coopisValue);
				System.out.println(" ActionRecords "+ActionRecords.size());
				for(ActionRecord Action: ActionRecords) {
					Action.setWorkflowTime(values.get(0));
					Action.setWorkflowPrice(values.get(1));
					Action.setWorkflowValue(values.get(2));
					Action.setWorkflowMitigationScore(values.get(3));
			    	addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopis.txt");
				}
		    	addTaskToFile("for this workflow execution: coopisTime "+(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead))+" coopisPrice "+coopisPrice+" coopisValue "+coopisValue+"coopisScore"+coopisScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopis.txt");
		    	
		    	valuesAdaptive.add(coopisTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead));
				valuesAdaptive.add(coopisPriceAdaptive);
				valuesAdaptive.add(coopisValueAdaptive);
				valuesAdaptive.add(coopisScoreAdaptive);
				CoopisResultsAdaptive.put(i,valuesAdaptive);
				CoopisResultsAdaptive100.put(i,valuesAdaptive);
				
				for(ActionRecord Action: ActionRecordsAdaptive) {
					Action.setWorkflowTime(values.get(0));
					Action.setWorkflowPrice(values.get(1));
					Action.setWorkflowValue(values.get(2));
					Action.setWorkflowMitigationScore(values.get(3));
			    	addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopisAdaptive.txt");
				}
		    	addTaskToFile("for this workflow execution: coopisTime "+(coopisTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead))+" coopisPrice "+coopisPriceAdaptive+" coopisValue "+coopisValueAdaptive+"coopisScore"+coopisScoreAdaptive+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
			//}
		    	k++;
			numberOfDetectedattacks=0;
			resetTimePriceScore();
			if(k==100) {
				k=0;
				    double totalTime = 0.0,minTime=Double.MAX_VALUE;
				    double totalCost = 0.0,minCost=Double.MAX_VALUE;
				    double totalValue=0.0,minValue=Double.MAX_VALUE;
				    double totalMitigationScore = 0.0,minMS=Double.MAX_VALUE;
				    
				
				    // Calculate the sum of Time, Cost, and MitigationScore
				    for (ArrayList<Double> result : CoopisResultsAdaptive100.values()) {
				        totalTime += result.get(0);
				        totalCost += result.get(1);
				        totalValue+=result.get(2);
				        totalMitigationScore += result.get(3);
				        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
				        if(result.get(0)<minTime && result.get(0)!=0)
				        	minTime=result.get(0);
				        if(result.get(1)<minCost && result.get(1)!=0)
				        	minCost=result.get(1);
				        if(result.get(2)<minValue && result.get(2)!=0)
				        	minValue=result.get(2);
				        if(result.get(3)<minMS && result.get(3)!=0)
				        	minMS=result.get(3);
				        
				    }
					//addTaskToFile("Final: totalTime "+totalTime+" totalCost "+totalCost+" totalValue "+totalValue+"totalMitigationScore"+totalMitigationScore+" ***************************************************************","LearningAdaptationLogCoopis.txt");

				    // Calculate the average
				    
				    if(minTime==Double.MAX_VALUE)
				    	minTime=1;
				    if(minCost==Double.MAX_VALUE)
				    	minCost=1;
				    if(minValue==Double.MAX_VALUE)
				    	minValue=1;
				    if(minMS==Double.MAX_VALUE)
				    	minMS=1;
				    
				    
				    double averageTime = totalTime / 100;
				    double averageCost = totalCost / 100;
				    double averageValue=totalValue/100;
				    double averageMitigationScore = totalMitigationScore / 100;
				
		    	addTaskToFile("for 100s workflow execution: coopisTime "+averageTime+" coopisPrice "+averageCost+" coopisValue "+coopisValueAdaptive+"coopisScore"+averageValue+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
				CoopisResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore

				}
			}
		averageOfCoopisResults("small");
		averageOfCoopisResultsAdaptive("small");
		resetCoopisResults(); 
*/	
/*
		k=0;
		for(int i=0;i<1000;i++) {
			qlearning= new QLearning();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("meduim "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("meduim "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//ExecuteWorkflow("insurance21",1,"E");
			ExecuteWorkflow("insurance21-2",1,"E");
			//ExecuteWorkflow("insurance21-3",1,"E");

			System.out.println("Number of detected attack for meduim bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End meduim "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("End meduim "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//if(completeVersion<30 || completeVersion>50) 
			//if(completeVersion<40)
				//addTaskToFile("InComplete Version: numofTasks"+completeVersion,"LearningAdaptationLogCoopis.txt");
			//else {
			values.add(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead));
			values.add(coopisPrice);
			values.add(coopisValue);
			values.add(coopisScore);
			CoopisResults.put(i,values);
			System.out.println("COOPIS Time:"+coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead)+" "+coopisPrice+" "+coopisValue);
			System.out.println(" ActionRecords "+ActionRecords.size());
			for(ActionRecord Action: ActionRecords) {
				Action.setWorkflowTime(values.get(0));
				Action.setWorkflowPrice(values.get(1));
				Action.setWorkflowValue(values.get(2));
				Action.setWorkflowMitigationScore(values.get(3));
		    	addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopis.txt");
			}
	    	addTaskToFile("for this workflow execution: coopisTime "+(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead))+" coopisPrice "+coopisPrice+" coopisValue "+coopisValue+"coopisScore"+coopisScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopis.txt");
	    	
	    	valuesAdaptive.add(coopisTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead));
			valuesAdaptive.add(coopisPriceAdaptive);
			valuesAdaptive.add(coopisValueAdaptive);
			valuesAdaptive.add(coopisScoreAdaptive);
			CoopisResultsAdaptive.put(i,valuesAdaptive);
			CoopisResultsAdaptive100.put(i,valuesAdaptive);
			
			for(ActionRecord Action: ActionRecordsAdaptive) {
				Action.setWorkflowTime(values.get(0));
				Action.setWorkflowPrice(values.get(1));
				Action.setWorkflowValue(values.get(2));
				Action.setWorkflowMitigationScore(values.get(3));
		    	addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopisAdaptive.txt");
			}
	    	addTaskToFile("for this workflow execution: coopisTime "+(coopisTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead))+" coopisPrice "+coopisPriceAdaptive+" coopisValue "+coopisValueAdaptive+"coopisScore"+coopisScoreAdaptive+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
		//}
	    	k++;
		numberOfDetectedattacks=0;
		resetTimePriceScore();
		if(k==100) {
			k=0;
			    double totalTime = 0.0,minTime=Double.MAX_VALUE;
			    double totalCost = 0.0,minCost=Double.MAX_VALUE;
			    double totalValue=0.0,minValue=Double.MAX_VALUE;
			    double totalMitigationScore = 0.0,minMS=Double.MAX_VALUE;
			    
			
			    // Calculate the sum of Time, Cost, and MitigationScore
			    for (ArrayList<Double> result : CoopisResultsAdaptive100.values()) {
			        totalTime += result.get(0);
			        totalCost += result.get(1);
			        totalValue+=result.get(2);
			        totalMitigationScore += result.get(3);
			        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
			        if(result.get(0)<minTime && result.get(0)!=0)
			        	minTime=result.get(0);
			        if(result.get(1)<minCost && result.get(1)!=0)
			        	minCost=result.get(1);
			        if(result.get(2)<minValue && result.get(2)!=0)
			        	minValue=result.get(2);
			        if(result.get(3)<minMS && result.get(3)!=0)
			        	minMS=result.get(3);
			        
			    }
				//addTaskToFile("Final: totalTime "+totalTime+" totalCost "+totalCost+" totalValue "+totalValue+"totalMitigationScore"+totalMitigationScore+" ***************************************************************","LearningAdaptationLogCoopis.txt");

			    // Calculate the average
			    
			    if(minTime==Double.MAX_VALUE)
			    	minTime=1;
			    if(minCost==Double.MAX_VALUE)
			    	minCost=1;
			    if(minValue==Double.MAX_VALUE)
			    	minValue=1;
			    if(minMS==Double.MAX_VALUE)
			    	minMS=1;
			    
			    
			    double averageTime = totalTime / 100;
			    double averageCost = totalCost / 100;
			    double averageValue=totalValue/100;
			    double averageMitigationScore = totalMitigationScore / 100;
			
	    	addTaskToFile("for 100s workflow execution: coopisTime "+averageTime+" coopisPrice "+averageCost+" coopisValue "+coopisValueAdaptive+"coopisScore"+averageValue+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
			CoopisResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore

			}
		}
	averageOfCoopisResults("meduim");
	averageOfCoopisResultsAdaptive("meduim");
	resetCoopisResults(); 
*/	

		
		k=0;
		for(int i=0;i<1000;i++) {
			qlearning= new QLearning();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("large "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("large "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			ExecuteWorkflow("insurance22",1,"E");
			//ExecuteWorkflow("insurance22-2",1,"E");
			//ExecuteWorkflow("insurance22-3",1,"E");

			System.out.println("Number of detected attack for large bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End large "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("End large "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//if(completeVersion<60 || completeVersion>100)
			//if(completeVersion<85)
				//addTaskToFile("InComplete Version: numofTasks"+completeVersion,"LearningAdaptationLogCoopis.txt");
			//else {
				values.add(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead));
				values.add(coopisPrice);
				values.add(coopisValue);
				values.add(coopisScore);
				CoopisResults.put(i,values);
				System.out.println(" ActionRecords "+ActionRecords.size());
				for(ActionRecord Action: ActionRecords) {
					Action.setWorkflowTime(values.get(0));
					Action.setWorkflowPrice(values.get(1));
					Action.setWorkflowValue(values.get(2));
					Action.setWorkflowMitigationScore(values.get(3));
			    	addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopis.txt");
				}
		    	addTaskToFile("for this workflow execution: coopisTime "+(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead))+" coopisPrice "+coopisPrice+" coopisValue "+coopisValue+"coopisScore"+coopisScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopis.txt");
		    	
		    	values.add(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead));
				values.add(coopisPrice);
				values.add(coopisValue);
				values.add(coopisScore);
				CoopisResults.put(i,values);
				System.out.println("COOPIS Time:"+coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead)+" "+coopisPrice+" "+coopisValue);
				System.out.println(" ActionRecords "+ActionRecords.size());
				for(ActionRecord Action: ActionRecords) {
					Action.setWorkflowTime(values.get(0));
					Action.setWorkflowPrice(values.get(1));
					Action.setWorkflowValue(values.get(2));
					Action.setWorkflowMitigationScore(values.get(3));
			    	addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopis.txt");
				}
		    	addTaskToFile("for this workflow execution: coopisTime "+(coopisTime+(numberOfDetectedattacks*adaptationTimeOverhead))+" coopisPrice "+coopisPrice+" coopisValue "+coopisValue+"coopisScore"+coopisScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopis.txt");
		    	
		    	valuesAdaptive.add(coopisTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead));
				valuesAdaptive.add(coopisPriceAdaptive);
				valuesAdaptive.add(coopisValueAdaptive);
				valuesAdaptive.add(coopisScoreAdaptive);
				CoopisResultsAdaptive.put(i,valuesAdaptive);
				CoopisResultsAdaptive100.put(i,valuesAdaptive);
				
				for(ActionRecord Action: ActionRecordsAdaptive) {
					Action.setWorkflowTime(values.get(0));
					Action.setWorkflowPrice(values.get(1));
					Action.setWorkflowValue(values.get(2));
					Action.setWorkflowMitigationScore(values.get(3));
			    	addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopisAdaptive.txt");
				}
		    	addTaskToFile("for this workflow execution: coopisTime "+(coopisTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead))+" coopisPrice "+coopisPriceAdaptive+" coopisValue "+coopisValueAdaptive+"coopisScore"+coopisScoreAdaptive+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
			//}
		    	k++;
			numberOfDetectedattacks=0;
			resetTimePriceScore();
			if(k==100) {
				k=0;
				    double totalTime = 0.0,minTime=Double.MAX_VALUE;
				    double totalCost = 0.0,minCost=Double.MAX_VALUE;
				    double totalValue=0.0,minValue=Double.MAX_VALUE;
				    double totalMitigationScore = 0.0,minMS=Double.MAX_VALUE;
				    
				
				    // Calculate the sum of Time, Cost, and MitigationScore
				    for (ArrayList<Double> result : CoopisResultsAdaptive100.values()) {
				        totalTime += result.get(0);
				        totalCost += result.get(1);
				        totalValue+=result.get(2);
				        totalMitigationScore += result.get(3);
				        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
				        if(result.get(0)<minTime && result.get(0)!=0)
				        	minTime=result.get(0);
				        if(result.get(1)<minCost && result.get(1)!=0)
				        	minCost=result.get(1);
				        if(result.get(2)<minValue && result.get(2)!=0)
				        	minValue=result.get(2);
				        if(result.get(3)<minMS && result.get(3)!=0)
				        	minMS=result.get(3);
				        
				    }
					//addTaskToFile("Final: totalTime "+totalTime+" totalCost "+totalCost+" totalValue "+totalValue+"totalMitigationScore"+totalMitigationScore+" ***************************************************************","LearningAdaptationLogCoopis.txt");

				    // Calculate the average
				    
				    if(minTime==Double.MAX_VALUE)
				    	minTime=1;
				    if(minCost==Double.MAX_VALUE)
				    	minCost=1;
				    if(minValue==Double.MAX_VALUE)
				    	minValue=1;
				    if(minMS==Double.MAX_VALUE)
				    	minMS=1;
				    
				    
				    double averageTime = totalTime / 100;
				    double averageCost = totalCost / 100;
				    double averageValue=totalValue/100;
				    double averageMitigationScore = totalMitigationScore / 100;
				
		    	addTaskToFile("for 100s workflow execution: coopisTime "+averageTime+" coopisPrice "+averageCost+" coopisValue "+averageValue+"coopisScore"+averageMitigationScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
				CoopisResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore

				}
			}
		averageOfCoopisResults("large");
		averageOfCoopisResultsAdaptive("large");
		resetCoopisResults(); 
		
		

		
		
		//Edoc experiments
		/*
		for(int i=0;i<1;i++) {
			s.setTaskNumber(0);
			ArrayList<Double> values=new ArrayList<Double>();
			addTaskToFile("Small "+i+" ***************************************************************","ResultLogEdoc.txt");
			ExecuteWorkflow("insurance6",3,"Elena");
			System.out.println("Number of detected attack for small bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End Small "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","ResultLogEdoc.txt");
			values.add(edocTime+(numberOfDetectedattacks*adaptationTimeOverhead));
			values.add(edoPrice);
			values.add(edocScore);
			EdocResults.put(i,values);
			
			numberOfDetectedattacks=0;
			resetTimePriceScore();
			}
		averageOfEdocResults();
		resetEdocResults();
		/*
		for(int i=0;i<10;i++) {
			ArrayList<Double> values=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("meduim "+i+" ***************************************************************","ResultLogEdoc.txt");
			ExecuteWorkflow("insurance21",3,"E");
			System.out.println("Number of detected attack for meduim bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End meduim "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","ResultLogEdoc.txt");
			values.add(edocTime+(numberOfDetectedattacks*adaptationTimeOverhead));
			values.add(edoPrice);
			values.add(edocScore);
			EdocResults.put(i,values);
			
			numberOfDetectedattacks=0;
			resetTimePriceScore();
			}
		averageOfEdocResults();
		resetEdocResults();
		
	/*
		for(int i=0;i<10;i++) {
			ArrayList<Double> values=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("large "+i+" ***************************************************************","ResultLogEdoc.txt");
			ExecuteWorkflow("insurance22",3,"E");
			System.out.println("Number of detected attack for large bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End large "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","ResultLogEdoc.txt");
			System.out.println("Edoc time"+edocTime);
			values.add(edocTime+(numberOfDetectedattacks*adaptationTimeOverhead));
			values.add(edoPrice);
			values.add(edocScore);
			EdocResults.put(i,values);
			
			numberOfDetectedattacks=0;
			resetTimePriceScore();
			}
		averageOfEdocResults();
		resetEdocResults();
		/*for(int i=19;i<23;i++) {
			s.setTaskNumber(0);
			ExecuteWorkflow("insurance"+i,3,"E"+i);
			}*/
		//ExecuteWorkflow("ServiceProcess2",3,"Nafiseh");

	}
	
	

	public void averageOfEdocResults()  throws IOException{
	    int totalEntries = EdocResults.size();
	    double totalTime = 0.0;
	    double totalCost = 0.0;
	    double totalMitigationScore = 0.0;

	    // Calculate the sum of Time, Cost, and MitigationScore
	    for (ArrayList<Double> result : EdocResults.values()) {
	        totalTime += result.get(0);
	        totalCost += result.get(1);
	        totalMitigationScore += result.get(2);
	        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
	    }

	    // Calculate the average
	    
	    double averageTime = totalTime / totalEntries;
	    double averageCost = totalCost / totalEntries;
	    double averageMitigationScore = totalMitigationScore / totalEntries;
		addTaskToFile("Final: averageTime "+averageTime+" averageCost "+averageCost+" averageMitigationScore "+averageMitigationScore+" ***************************************************************","ResultLogEdoc.txt");

	}
	
	public void averageOfCoopisResults(String size)  throws IOException{
	    int totalEntries = CoopisResults.size();
	    double totalTime = 0.0,minTime=Double.MAX_VALUE;;
	    double totalCost = 0.0,minCost=Double.MAX_VALUE;;
	    double totalValue=0.0,minValue=Double.MAX_VALUE;;
	    double totalMitigationScore = 0.0,minMS=Double.MAX_VALUE;
	    
	
	    // Calculate the sum of Time, Cost, and MitigationScore
	    for (ArrayList<Double> result : CoopisResults.values()) {
	        totalTime += result.get(0);
	        totalCost += result.get(1);
	        totalValue+=result.get(2);
	        totalMitigationScore += result.get(3);
	        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
	        if(result.get(0)<minTime && result.get(0)!=0)
	        	minTime=result.get(0);
	        if(result.get(1)<minCost && result.get(1)!=0)
	        	minCost=result.get(1);
	        if(result.get(2)<minValue && result.get(2)!=0)
	        	minValue=result.get(2);
	        if(result.get(3)<minMS && result.get(3)!=0)
	        	minMS=result.get(3);
	        
	    }
		//addTaskToFile("Final: totalTime "+totalTime+" totalCost "+totalCost+" totalValue "+totalValue+"totalMitigationScore"+totalMitigationScore+" ***************************************************************","LearningAdaptationLogCoopis.txt");

	    // Calculate the average
	    
	    if(minTime==Double.MAX_VALUE)
	    	minTime=1;
	    if(minCost==Double.MAX_VALUE)
	    	minCost=1;
	    if(minValue==Double.MAX_VALUE)
	    	minValue=1;
	    if(minMS==Double.MAX_VALUE)
	    	minMS=1;
	    
	    
	    double averageTime = totalTime / totalEntries;
	    double averageCost = totalCost / totalEntries;
	    double averageValue=totalValue/totalEntries;
	    double averageMitigationScore = totalMitigationScore / totalEntries;
		addTaskToFile("Final: averageNormalizedTime "+(averageTime/minTime)+" averageNormalizedCost "+(averageCost/minCost)+" averageNormalizedValue "+(averageValue/minValue)+" averageNormalizedMitigationScore "+(averageMitigationScore/minMS)+" ***************************************************************","LearningAdaptationLogCoopis.txt");
		addTaskToFile("Final: averageNormalizedTime "+averageTime+" averageNormalizedCost "+averageCost+" averageNormalizedValue "+averageValue+" averageNormalizedMitigationScore "+averageMitigationScore+" numOFTasks"+completeVersion+" ***************************************************************","LearningAdaptationLogCoopis.txt");

	}
	
	
	public void averageOfCoopisResultsAdaptive(String size)  throws IOException{
	    int totalEntries = CoopisResultsAdaptive.size();
	    double totalTime = 0.0,minTime=Double.MAX_VALUE;;
	    double totalCost = 0.0,minCost=Double.MAX_VALUE;;
	    double totalValue=0.0,minValue=Double.MAX_VALUE;;
	    double totalMitigationScore = 0.0,minMS=Double.MAX_VALUE;
	    
	
	    // Calculate the sum of Time, Cost, and MitigationScore
	    for (ArrayList<Double> result : CoopisResultsAdaptive.values()) {
	        totalTime += result.get(0);
	        totalCost += result.get(1);
	        totalValue+=result.get(2);
	        totalMitigationScore += result.get(3);
	        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
	        if(result.get(0)<minTime && result.get(0)!=0)
	        	minTime=result.get(0);
	        if(result.get(1)<minCost && result.get(1)!=0)
	        	minCost=result.get(1);
	        if(result.get(2)<minValue && result.get(2)!=0)
	        	minValue=result.get(2);
	        if(result.get(3)<minMS && result.get(3)!=0)
	        	minMS=result.get(3);
	        
	    }
		//addTaskToFile("Final: totalTime "+totalTime+" totalCost "+totalCost+" totalValue "+totalValue+"totalMitigationScore"+totalMitigationScore+" ***************************************************************","LearningAdaptationLogCoopis.txt");

	    // Calculate the average
	    
	    if(minTime==Double.MAX_VALUE)
	    	minTime=1;
	    if(minCost==Double.MAX_VALUE)
	    	minCost=1;
	    if(minValue==Double.MAX_VALUE)
	    	minValue=1;
	    if(minMS==Double.MAX_VALUE)
	    	minMS=1;
	    
	    
	    double averageTime = totalTime / totalEntries;
	    double averageCost = totalCost / totalEntries;
	    double averageValue=totalValue/totalEntries;
	    double averageMitigationScore = totalMitigationScore / totalEntries;
		addTaskToFile("Final: averageNormalizedTime "+(averageTime/minTime)+" averageNormalizedCost "+(averageCost/minCost)+" averageNormalizedValue "+(averageValue/minValue)+" averageNormalizedMitigationScore "+(averageMitigationScore/minMS)+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
		addTaskToFile("Final: averageNormalizedTime "+averageTime+" averageNormalizedCost "+averageCost+" averageNormalizedValue "+averageValue+" averageNormalizedMitigationScore "+averageMitigationScore+" numOFTasks"+completeVersion+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

	}
	
	
	
	
	public TenantKernel(String id) {
		setId(id);
		System.out.println("********"+ id+ " is created ********");
		if(this.runtime==null) {
			PersistenceUtil.setupPoolingDataSource();
			RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
		            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("InsuranceClaim6.bpmn2"), ResourceType.BPMN2)
		            .get();
		        RuntimeManager manager=RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
			         
	         runtime = manager.getRuntimeEngine(null);
			//runtime =createRuntimeManager("InsuranceClaim6.bpmn2","BPMN2-ServiceProcess2.bpmn2").getRuntimeEngine(null);	
		}
		ExecuteWorkflow("insurance6",3,"Elena");
		ExecuteWorkflow("ServiceProcess2",3,"Nafiseh");
		
	}
	
	public TenantKernel(String id,int numOfInstances) {
		setId(id);
		
		System.out.println("*************"+id+ " is created");
	
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public ArrayList<String> getUserNames() {
		return UserNames;
	}

	public ArrayList<String> getIPAdresses() {
		return IPAdresses;
	}

	public ArrayList<String> getWorkingdays() {
		return workingdays;
	}
	
	public ArrayList<User>  getUsers() {
		return users;
	}

	public KernelManagment getKernelManagment() {
		return KM;
	}
	public void resetTimePriceScore() {
		if(edocTime>edocTimeMax)
			edocTimeMax=edocTime;
		if(edoPrice>edoPriceMax)
			edoPriceMax=edoPrice;
		if(edocScore>edocScoreMax)
			edocScoreMax=edocScore;
		edocTime=0;
		edoPrice=0;
		edocScore=0;
		
		
		
		if(coopisTime>coopisTimeMax)
			coopisTimeMax=coopisTime;
		if(coopisPrice>coopisPriceMax)
			coopisPriceMax=coopisPrice;
		if(coopisValue>coopisValueMax)
			coopisValueMax=coopisValue;
		if(coopisScore>coopisScoreMax)
			coopisScoreMax=coopisScore;
		coopisTime=0;
		coopisPrice=0;
		coopisValue=0;
		coopisScore=0;
		completeVersion=0;
		ActionRecords = new ArrayList<ActionRecord>();
		
		
		if(coopisTimeAdaptive>coopisTimeMaxAdaptive)
			coopisTimeMaxAdaptive=coopisTimeAdaptive;
		if(coopisPriceAdaptive>coopisPriceMaxAdaptive)
			coopisPriceMaxAdaptive=coopisPriceAdaptive;
		if(coopisValueAdaptive>coopisValueMaxAdaptive)
			coopisValueMaxAdaptive=coopisValueAdaptive;
		if(coopisScoreAdaptive>coopisScoreMaxAdaptive)
			coopisScoreMaxAdaptive=coopisScoreAdaptive;
		coopisTimeAdaptive=0;
		coopisPriceAdaptive=0;
		coopisValueAdaptive=0;
		coopisScoreAdaptive=0;
		completeVersionAdaptive=0;
		ActionRecordsAdaptive = new ArrayList<ActionRecord>();

	}
	
	public void resetEdocResults() {
		 EdocResults = new HashMap<Integer, ArrayList<Double>>();
		edocTimeMax=0;
		edoPriceMax=0;
		edocScoreMax=0;
	}
	
	public void resetCoopisResults() {
		 CoopisResults = new HashMap<Integer, ArrayList<Double>>();
		coopisTimeMax=0;
		coopisPriceMax=0;
		coopisScoreMax=0;
		completeVersion=0;
		
		 CoopisResultsAdaptive = new HashMap<Integer, ArrayList<Double>>();
			coopisTimeMaxAdaptive=0;
			coopisPriceMaxAdaptive=0;
			coopisScoreMaxAdaptive=0;
			completeVersionAdaptive=0;
	}

	public void ExecuteWorkflow(String ProcessName,int instanceNum, String input) {
		System.out.println("ExecuteWorkflow");
		//createWorkflowModel("InsuranceClaim6.bpmn2");
		//createUsers();   

        KieSession ksession1 = runtime.getKieSession();
        KieSession ksession2 = runtime.getKieSession();
		Map<String, Object> params = new HashMap<String, Object>();
		
		for(int i=0;i<(instanceNum);i++) {
			//Register handlers
			System.out.println("# "+ProcessName+" instanceNum "+"#"+i);
			ksession1.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", input+i);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession1.startProcess(ProcessName, params);
		}
		
		
		/*
		for(;i<instanceNum;i++) {
			//Register handlers
			ksession2.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", "nafise"+i);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession2.startProcess("ServiceProcess2", params);
			//assertProcessInstanceCompleted(processInstance.getId(), ksession2);
			
			
			ksession.getWorkItemManager().registerWorkItemHandler("Service Task",new ServiceTaskHandler());
			params.put("s", new Tenant("nafise"+i).getName());
			ksession.startProcess("ServiceProcess2", params);
			
		}*/
		
	}
	
	
	public void createUsers() {
		//String[] UserName = {"Roy", "Earl", "James", "Charles", "Ryan", "Marilyn", "Emily", "Craig", "Howard", "Amanda", "Johnny", "Brian", "Jack", "Paul", "Joe", "Ronald", "Donald", "Anna", "Steve", "Lisa", "Gema", "Doretta", "Hannah", "Maryellen", "Pam", "Sherell", "Micheline", "Shandi", "Hugo", "Jamika", "Brant", "Rossana", "Della", "Velda", "Hoyt", "Tiffiny", "Frances", "Alpha", "Jimmy", "Junior", "Issac", "Evelin", "Deloras", "Hassie", "Josef", "Clayton", "Sandra", "Rossie", "Vickie", "Lourdes", "Jin", "Sigrid", "Elisha", "Sherlene", "Lucy", "Chan", "Lannie", "Alyce", "Melany", "Wilton", "Seth", "Sonia", "Iluminada", "Michaele", "Ling", "Keven","Roseanne", "Sharee", "Carmella", "Grayce"};
		//String[] IPS= {}
		initialize();
		Random rand = new Random();
		for(int i=0;i<numOfUsers;i++) {
			String name=UserNames.get(rand.nextInt(UserNames.size()));
			ArrayList<String> ips = new ArrayList<String>();
			int max=rand.nextInt(2);
			//System.out.println("Maaaax "+max);
			for(int j=0;j<=max;j++){
				//System.out.println("for "+max);
				ips.add(IPAdresses.get(rand.nextInt(IPAdresses.size())));
			}
			//Sting name=UserNames.get(rand.nextInt(100));
			//users.add();
			ArrayList<String> days= new ArrayList<String>();
			max=rand.nextInt(5);
			for(int j=0;j<=max;j++){
				//System.out.println("for "+max);
				days.add(workingdays.get(j));
			}
			double trust=1;
			User user=new  User(i,name,ips,trust,days);
			//user.print();
			this.users.add(user);
		}
	}
	
	
	

		
		public void initialize() {
			UserNames=ReadLinebyLine("UserName.txt");
			IPAdresses=ReadLinebyLine("IPAdresses.txt");
			workingdays=new ArrayList<String>();
			workingdays.add("Mon");
			workingdays.add("Tue");
			workingdays.add("Wed");
			workingdays.add("Thu");
			workingdays.add("Fri");
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

	
	public void printList(ArrayList<String> list){
		for(String l : list)
			System.out.print(l+", ");
		System.out.println();
	}
	
	public Workflow createWorkflowModel(String bpmn) {
		System.out.println("***********Create workflow "+bpmn);
		ArrayList<String> userTasks=new ArrayList<String>();
		Map<String, ArrayList<Double>> serviceTasks = new HashMap<String, ArrayList<Double>>();
		Map<String, ArrayList<String>> serviceTasksLegalAdaptation = new HashMap<String, ArrayList<String>>();
		Map<String, Map<String, ArrayList<String>>> serviceTasksLegalAdaptationCost = new HashMap<String, Map<String, ArrayList<String>>>();
		Map<String, ArrayList<Double>> serviceCost = new HashMap<String, ArrayList<Double>>();
		Map<String, ArrayList<Double>> userCost = new HashMap<String, ArrayList<Double>>();
		ArrayList<ArrayList<String>> parallelTasksList = new ArrayList<ArrayList<String>>();


		String delimsComma = "[,]+";
		for(String line:ReadLinebyLine(bpmn)) {
			if(line.contains("serviceTask")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						String serviceName=arr2[0];
						ArrayList<String> legalAdaptation = new ArrayList<String>();
						Map<String, ArrayList<String>> adaptationCosts = new HashMap<String, ArrayList<String>>();//time,price,value
						ArrayList<Double> securityRequirementsForServices = new ArrayList<Double>();
						ArrayList<Double> costs = new ArrayList<Double>();

						for (int j=1;j<4; j++) {
							securityRequirementsForServices.add(Double.parseDouble(arr2[j]));

								//System.out.println("Else "+arr2[j]);
								
							}
						for (int j=4;j<7; j++) {
							costs.add(Double.parseDouble(arr2[j]));
							//System.out.println("cost "+arr2[j]+" "+serviceName);
						}
						for (int j=7;j<arr2.length; j++) {
							ArrayList<String> costLocal = new ArrayList<String>();
							String[] arr3  = arr2[j].split(":");
							System.out.println("legalAdaptation "+arr2[j]+" "+serviceName);
							System.out.println("legalAdaptation "+arr3[0]+" "+arr3[1]+" "+arr3[2]+" "+arr3[3]);
							legalAdaptation.add(arr3[0]);
							costLocal.add(arr3[1]);
							costLocal.add(arr3[2]);
							costLocal.add(arr3[3]);
							adaptationCosts.put(arr3[0],costLocal);
						}
					
						serviceTasks.put(serviceName,securityRequirementsForServices);
						serviceCost.put(serviceName,costs);
						serviceTasksLegalAdaptation.put(serviceName,legalAdaptation);
						serviceTasksLegalAdaptationCost.put(serviceName,adaptationCosts);
					}
				}
			}//end if serviceTask
			if(line.contains("userTask")) {
				String userTask="";
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						ArrayList<Double> costs = new ArrayList<Double>();
						userTasks.add(arr2[0]);
						userTask=arr2[0];
						for(int j=1;j<arr2.length;j++) {
							costs.add(Double.parseDouble(arr2[j]));
							}
						//System.out.println(" userCost "+costs);
						userCost.put(userTask,costs);
						}
					}
				}//end if userTask
			
			if(line.contains("parallelTasks")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						ArrayList<String> parallelTasks=new ArrayList<String>();
						for(int j=0;j<arr2.length;j++) {
							parallelTasks.add(arr2[j]);
							}
						System.out.println(" parallelTasks are "+parallelTasks);
						parallelTasksList.add(parallelTasks);
						}
					}
				
			}//end if parallelTasks
			
		}//for
		
		Workflow w=new Workflow(bpmn,userTasks,serviceTasks,this.users,this,this.KM,serviceTasksLegalAdaptation,serviceTasksLegalAdaptationCost,serviceCost,userCost,parallelTasksList);
		workflows.add(w);
		w.print();
		System.out.println("***********end of Create workflow "+bpmn);
		return w;
	}
	/*
	public Workflow createWorkflowModel2(String bpmn) {
		System.out.println("***********Create workflow "+bpmn);
		ArrayList<String> userTasks=new ArrayList<String>();
		Map<String, ArrayList<Double>> serviceTasks = new HashMap<String, ArrayList<Double>>();
		String delimsComma = "[,]+";
		for(String line:ReadLinebyLine(bpmn)) {
			if(line.contains("serviceTask")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						String serviceName=null;
						int j=0;
						ArrayList<Double> securityRequirementsForServices = new ArrayList<Double>();
						for (String obj1 : arr2) {
							if(j==0) {
								//System.out.println("if "+obj1);
								serviceName=obj1;
								j=1;
								}
							else {
								//System.out.println("Else "+obj1);
								securityRequirementsForServices.add(Double.parseDouble(obj1));
							}
						}
						serviceTasks.put(serviceName,securityRequirementsForServices);
					}
				}
			}//end if serviceTask
			if(line.contains("userTask")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						//System.out.println(obj);
						userTasks.add(obj);
					}
				}
			}//end if userTask
		}

		Workflow w=new Workflow(bpmn,userTasks,serviceTasks,this.users,this,this.KM);
		workflows.add(w);
		w.print();
		System.out.println("***********end of Create workflow "+bpmn);
		return w;
	}
	*/
	/*
	public void createWorkflowModel(String bpmn) {
		ArrayList<Service> services;
		if(bpmn=="InsuranceClaim6.bpmn2") {
			services=null;
			ArrayList<String> userTasks=new ArrayList<String>();
			userTasks.add("InitialClaimUser");
			userTasks.add("ObtainHospitalReport");
			userTasks.add("ObtainPoliceReport");
			userTasks.add("ReimbursementDecision");
			ArrayList<String>  serviceNames=ReadLinebyLine("ServiceName1.txt");	
			//serviceTasks.add("ObtainClaimService");
			//serviceTasks.add("SelectBestExpert");
			services.add(KM.getServices("ObtainClaimService"));
			services.add(KM.getServices("SelectBestExpert"));
			//Workflow w=new Workflow(numOfWorkflows,"InsuranceClaim6.bpmn2",6,userTasks,serviceTasks,this.users,services);
			//String tenantAddress=this.id+"/";
			Workflow w=new Workflow(numOfWorkflows,"InsuranceClaim6.bpmn2",6,userTasks,serviceNames,this.users,services,this.UserNames,this.IPAdresses,this.workingdays,this);

			Services s=new Services(w);
			numOfWorkflows++;
			
		}
		if(bpmn=="BPMN2-ServiceProcess2.bpmn2") {
			services=null;
			ArrayList<String> userTasks=new ArrayList<String>();
			ArrayList<String>  serviceNames=ReadLinebyLine("ServiceName2.txt");	
			//serviceTasks.add("hello1");
			services.add(KM.getServices("hello1"));
			//services=createServices("ServiceName2.txt");
			//String tenantAddress=this.id+"/";
			Workflow w=new Workflow(numOfWorkflows,"BPMN2-ServiceProcess2.bpmn2",1,userTasks,serviceNames,this.users,services,this.UserNames,this.IPAdresses,this.workingdays,this);
			numOfWorkflows++;
		}
		
	}
	*/
	
	
	public void securityEvaluator() {
		
	}
	
	public void trustAwareSchedulingPlanner() {
		System.out.println("trust-Aware Scheduling Planner");
		for(Workflow w:workflows)
			w.Scheduling(this.users,this.KM);
	}
	
	
	
	

	
	public void  taskAnonymizerAllocator() {
		
	}
	
	public void localDetection() {
		
	}
	
	public void AdaptationDecisionEngine(ServiceTask s,String payload,Workflow workflow,int taskNumber)throws IOException  {
		System.out.println("***** Adaptation Decision Engine *****");
		//System.out.println("Check the sniffiing  network traffic with global model");
		if(IsAnomaly(payload)) {
			System.out.println("Anomaly is detected!!");
			double attackScore=computeAttackScore2(s,payload);
			//double attackScore=computeAttackScore(s,payload);
			if(attackScore>this.attackThresh) {
				System.out.println("attackScore(detected attack)>Tenant.attackThresh!!");
				//localAdaptation(s,payload,attackScore,workflow,taskNumber);
				//localAdaptationEdoc(s,payload,attackScore,workflow,taskNumber);
				//localAdaptationCoopis(s,payload,attackScore,workflow,taskNumber);
				localAdaptationCoopisPredictive(s,payload,attackScore,workflow,taskNumber);

			}
			else {
				Attack attack=null;
				for(Attack a:AttackTypes) {
					//System.out.println(attack.getName()+" "+payload);
					if(payload.contains(a.getName()))
						attack=a;
				}
				try {
				if(attack.getName().equals("dos") || attack.getName().equals("u2r"))
					Thread.sleep(1000);
				} catch (InterruptedException e) {
                e.printStackTrace();
				}
				System.out.println("attackScore(detected attack)<Tenant.attackThresh!!");
				System.out.println("Ignore the attack!!");
				String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
				addTaskToFile(timestamp+"\t"+this.getId()+"\t"+workflow.getId()+"\t"+ s.getInstanceId()+"\t"+taskNumber+"\t ServiceTask"+" \t Abnormal  \t"+ s.getService().getId()+"\t"+attack.getName()+"\t"+" Ignore the attack!","adaptationLogEdoc.txt");//add this adaptation to file 
				numberOfDetectedattacks++;
				}			
		}
		else {
			System.out.println("No malicious behaviour detected !!");
			//String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
			//addTaskToFile(timestamp+"\t"+this.getId()+"\t"+workflow.getId()+"\t"+ s.getInstanceId()+"\t"+taskNumber+"\t ServiceTask "+"\t Normal","adaptationLogEdoc.txt");//add this adaptation to file 
			ArrayList<Double> costs=workflow.getCostsforServiceTask(s.getTaskId());
			double cost=0,price=s.getService(). getCost(),time=s.getService(). getART(),value=costs.get(2);
			coopisTime+=time;
			coopisPrice+=price;
			coopisValue+=value;
			
			coopisTimeAdaptive+=time;
			coopisPriceAdaptive+=price;
			coopisValueAdaptive+=value;
			//addTaskToFile(s.getTaskId()+"\t"+"normal"+"\t"+0+"\t"+"nothing"+"\t"+0+"\t"+time+"\t"+price+"\t"+value+"\t"+0,"LearningAdaptationLogCoopis.txt");//add this adaptation to file 
			//addTaskToFile(s.getTaskId()+"\t"+"normal"+"\t"+0+"\t"+"nothing"+"\t"+0+"\t"+time+"\t"+price+"\t"+value+"\t"+0,"LearningAdaptationLogCoopisAdaptive.txt");//add this adaptation to file 

			completeVersion++;

			}

	}
	
	public boolean IsAnomaly(String payload) {
		boolean result=false;
		for(Attack attack:AttackTypes) {
			//System.out.println(attack.getName()+" "+payload);
			if(payload.contains(attack.getName()))
				result=true;
		}
		return result;
	}
	

	public double computeAttackScore2(ServiceTask s,String payload)throws IOException {
		Attack attack=null;
		for(Attack a:AttackTypes) {
			//System.out.println(attack.getName()+" "+payload);
			if(payload.contains(a.getName()))
				attack=a;
		}
		System.out.println("compute Attack Score for detected attack  "+attack.getName());
		ArrayList<Double> securityObjectiveRequirements=s.getSecurityObjectiveRequirements();
		System.out.println("	-securityObjectiveRequirements for task: "+securityObjectiveRequirements.get(0)+","+securityObjectiveRequirements.get(1)+","+securityObjectiveRequirements.get(2));
		ArrayList<Double> securityImpacts=attack.getSecurityImpact();
		System.out.println("	-securityImpact of attack: "+securityImpacts.get(0)+","+securityImpacts.get(1)+","+securityImpacts.get(2));
		System.out.println("	-AFR detected attack in service: "+s.getService().getAFR(attack.getName()));
		double attackSophistication=(double)(getAttackSophistication(s,payload,attack)+1)/100;//compute attack severiry
		System.out.println("	-attack sophistication: "+attackSophistication);
		
		ArrayList<Double> attackScore=new ArrayList<Double>();
		attackScore.add(securityObjectiveRequirements.get(0)*securityImpacts.get(0));//for confidentiality
		attackScore.add(securityObjectiveRequirements.get(1)*securityImpacts.get(1));//for integrity
		attackScore.add(securityObjectiveRequirements.get(2)*securityImpacts.get(2));//for availability
		for(double a:attackScore) {
			System.out.println("	securityObjectiveRequirements*securityImpacts : "+a);
		}
		System.out.println("	1-securityObjectiveRequirements*securityImpacts: "+(1-attackScore.get(0))*(1-attackScore.get(1))*(1-attackScore.get(2)));
		double finalAttackScore=(1-((1-attackScore.get(0))*(1-attackScore.get(1))*(1-attackScore.get(2))))*s.getService().getAFR(attack.getName())*attackSophistication*1000;
		System.out.println("	-finalAttackScore: "+finalAttackScore);//between 0 to 1000
		return finalAttackScore;
	}
	
	
	public double computeAttackScore(ServiceTask s,String payload)throws IOException {
		
		Attack attack=null;
		for(Attack a:AttackTypes) {
			//System.out.println(attack.getName()+" "+payload);
			if(payload.contains(a.getName()))
				attack=a;
		}
		System.out.println("compute Attack Score for detected attack  "+attack.getName());
		ArrayList<Double> securityObjectiveRequirements=s.getSecurityObjectiveRequirements();
		System.out.println("	-securityObjectiveRequirements for task: "+securityObjectiveRequirements.get(0)+","+securityObjectiveRequirements.get(1)+","+securityObjectiveRequirements.get(2));
		ArrayList<Double> securityImpacts=attack.getSecurityImpact();
		System.out.println("	-securityImpact of attack: "+securityImpacts.get(0)+","+securityImpacts.get(1)+","+securityImpacts.get(2));
		System.out.println("	-securityImpact*AFR: "+s.getService().getAttackSecurityRisk().get(attack.getName()).get(0)+","+s.getService().getAttackSecurityRisk().get(attack.getName()).get(1)+","+s.getService().getAttackSecurityRisk().get(attack.getName()).get(2));
		double attackSophistication=(double)(getAttackSophistication(s,payload,attack)+1)/100;//compute attack severiry
		System.out.println("	-attack sophistication: "+attackSophistication);
		
		ArrayList<Double> attackScore=new ArrayList<Double>();
		attackScore.add(securityObjectiveRequirements.get(0)*s.getService().getAttackSecurityRisk().get(attack.getName()).get(0)*attackSophistication);//for confidentiality
		attackScore.add(securityObjectiveRequirements.get(1)*s.getService().getAttackSecurityRisk().get(attack.getName()).get(1)*attackSophistication);//for integrity
		attackScore.add(securityObjectiveRequirements.get(2)*s.getService().getAttackSecurityRisk().get(attack.getName()).get(2)*attackSophistication);//for availability
		for(double a:attackScore) {
			System.out.println("	-attackScore: "+a);
		}
		double finalAttackScore=(1-((1-attackScore.get(0))*(1-attackScore.get(1))*(1-attackScore.get(2))))*1000;
		System.out.println("	-finalAttackScore: "+finalAttackScore);//between 0 to 1000
		return finalAttackScore;
	}
	
	public double getAttackSophistication(ServiceTask s,String payload,Attack attack) throws IOException {
		File file = new File("KDD_Final_predictions_withNormal_stringFormat.csv");
		BufferedReader br= new BufferedReader(new FileReader(file));
		double prediction=0;
		String st;
		  while ((st = br.readLine()) != null) {
		    	String[] fields = st.split(",");
		    	String[] task = payload.split(",");
				//System.out.println("attack sophistication: "+fields[0]+" : "+task[41]);
		    	if(fields[0].equals(attack.getName())) {
		    		boolean find=true;
		    		for(int i=1;i<41;i++) {
		    			if(!fields[i].equals(task[i-1].replace("\"", ""))) {
		    				//System.out.println("fields[i] "+fields[i]+" : "+task[i-1]);
		    				find=false;
		    				break;
		    			}
		    		}
		    		if(find) {
		    			//System.out.println("fiiiind "+fields[43]+ " "+attack.getName());
		    			prediction=Double.parseDouble(fields[43]);
		    		}
		    	}
		  }//while
		  return prediction;

	}
	
	public void localDetection_userPredictionModel(UserTask u,String payload,Workflow workflow,int taskNumber) throws IOException{
		System.out.println("localDetection_userPredictionModel");
		if(payload.equals("Anomaly")) {
			String selectedAdaptation="";
			Random rand = new Random();
			int a=rand.nextInt(2);
			if(a== 0)
				selectedAdaptation="Restricting User Access";
			else
				selectedAdaptation="Update User Trust";
			String detectedAttack= "Elevation of Privilege";
			String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
			addTaskToFile(timestamp+"\t"+this.getId()+"\t"+workflow.getId()+"\t"+ u.getInstanceId()+"\t "+taskNumber+"\t UserTask"+" \t Abnormal "+"\t"+ u.getUser().getId()+"\t"+detectedAttack+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
			numberOfDetectedattacks++;
			//addTaskToFile("Tenant Kernel: "+this.getId()+" User malicious behaviour is detected in User:"+ u.getId()+", Workflow: "+workflow.getId()+", Instance: "+ u.getInstanceId()+" detected attack: "+detectedAttack+" selected adatation: "+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
			//addTaskToFile( u.getProcessID()+"\t"+ u.getInstanceId()+"\t"+ u.getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		}
	}
	
	
	public void localAdaptation(ServiceTask s,String payload,double attackScore,Workflow workflow,int taskNumber) throws IOException{
		System.out.println("localAdaptation");
		Attack attack=null;
		for(Attack a:AttackTypes) {
			//System.out.println(attack.getName()+" "+payload);
			if(payload.contains(a.getName()))
				attack=a;
		}
		
		System.out.println("$For serviceTask "+s.getTaskId()+" in instance "+s.getInstanceId()+" from ProcessId "+s.getProcessId()+" from workflow "+workflow.getName()+"  attack:"+attack.getName()+" with attackScore "+attackScore+"("+getSeverity(attackScore)+") will be adapted");
		
		ArrayList<String> attackAdaptations=attack.getAttackAdaptationPattern(getSeverity(attackScore));
		ArrayList<String> taskAdaptations=workflow.getLegalAdaptationforServiceTask(s.getTaskId());
		
		System.out.println("	Available Adaptation: ");
		System.out.println("		-for attack: "+attackAdaptations);
		System.out.println("		-for task: "+taskAdaptations);
		
		ArrayList<String> commonAdaptations = new ArrayList<>(attackAdaptations);
		commonAdaptations.retainAll(taskAdaptations);
		System.out.println("		-common: "+commonAdaptations);
		Map<String, Double> adaptationCost = new HashMap<String, Double>();
		ArrayList<Adaptation> adaptations = new ArrayList<Adaptation>();
		Adaptation currentAdaptation;
		for(String adaptation:commonAdaptations) {
			System.out.println("			-Calculate cost of "+adaptation);
			currentAdaptation=predictCostOfAdaptation(s,adaptation,workflow);
			adaptations.add(currentAdaptation);
			//adaptationCost.put(adaptation,currentAdaptation.getTotalCost());
		}
		
		double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
		
		if(adaptations.size()>1) {
			//normalized the time,price and value
	        for(Adaptation option:adaptations){
	            minPrice = Math.min(minPrice, option.getPrice());
	            maxPrice = Math.max(maxPrice, option.getPrice());
	            minTime = Math.min(minTime, option.getTime());
	            maxTime = Math.max(maxTime, option.getTime());
	            minValue = Math.min(minValue, option.getValue());
	            maxValue = Math.max(maxValue, option.getValue());
	        }
		}
		else {//if there is only one adaptation, we don't need to normalize
		    minPrice = 0;
            maxPrice = 1;
            minTime = 0;
            maxTime = 1;
            minValue = 0;
            maxValue = 1;
		}
		System.out.println("		-minPrice "+ minPrice+" maxPrice "+maxPrice+" minTime "+minTime+" maxTime "+maxTime+" minValue "+minValue+" maxValue "+maxValue);
		for(Adaptation option:adaptations) {
			double normalizedPrice = (minPrice == maxPrice) ? 0 : (option.getPrice() - minPrice) / (maxPrice - minPrice);
			System.out.println(option.getPrice()+"- "+minPrice+ "/" +maxPrice +"-"+ minPrice+": "+(option.getPrice() - minPrice) / (maxPrice - minPrice));
            double normalizedTime = (minTime == maxTime) ? 0 : (option.getTime() - minTime) / (maxTime - minTime);
            System.out.println((option.getTime() - minTime) / (maxTime - minTime));
            double normalizedValue = (minValue == maxValue) ? 0 : (option.getValue() - minValue) / (maxValue - minValue);
			System.out.println("		-Normalize ("+option.getName()+"): "+" time "+normalizedTime +" price "+normalizedPrice+" value "+normalizedValue);
	        option.setTotalCost((this.priceWeight*normalizedPrice)+(this.timeWeight*normalizedTime)-(this.valueWeight*normalizedValue));
			adaptationCost.put(option.getName(),option.getTotalCost());
			System.out.println("		-NewCost For adaptation ("+option.getName()+"): "+option.getTotalCost()+" time "+option.getTime() +" price "+option.getPrice()+" value "+option.getValue());

		}

		System.out.println("		-adaptationCost: "+adaptationCost);
		
		//selectBestAdaptationsStrategy(adaptationCost,s);
		String selectedAdaptation=selectBestAdaptationsStrategy2(adaptationCost,s,workflow);
		
		addTaskToFile(s.getTaskId()+"\t"+attack.getName()+"\t"+attackScore+"\t"+adaptationCost+"\t"+selectedAdaptation,"adaptationLog.txt");//add this adaptation to file 
		//addTaskToFile(s.getProcessID()+"\t"+s.getInstanceId()+"\t"+s.getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		//addTaskToFile("Tenant: "+this.getId()+" Service malicious behaviour is detected in Service Task:"+ s.getId()+", Workflow: "+workflow.getId()+", Instance: "+ s.getInstanceId()+" detected attack: "+attack.getName()+" selected adatation: "+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		 String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
		addTaskToFile(timestamp+"\t"+this.getId()+"\t"+workflow.getId()+"\t"+ s.getInstanceId()+"\t"+taskNumber+"\t ServiceTask \t Abnormal "+"\t"+ s.getService().getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		
		numberOfDetectedattacks++;

	}
	
	public void localAdaptationCoopis(ServiceTask s,String payload,double attackScore,Workflow workflow,int taskNumber) throws IOException{
		System.out.println("localAdaptation Coopis");
		Attack attack=null;
		for(Attack a:AttackTypes) {
			//System.out.println(attack.getName()+" "+payload);
			if(payload.contains(a.getName()))
				attack=a;
		}
		
		System.out.println("$For serviceTask "+s.getTaskId()+" in instance "+s.getInstanceId()+" from ProcessId "+s.getProcessId()+" from workflow "+workflow.getName()+"  attack:"+attack.getName()+" with attackScore "+attackScore+"("+getSeverity(attackScore)+") will be adapted");
		
		ArrayList<String> attackAdaptations=attack.getAttackAdaptationPattern(getSeverity(attackScore));
		ArrayList<String> taskAdaptations=workflow.getLegalAdaptationforServiceTask(s.getTaskId());
		
		System.out.println("	Available Adaptation: ");
		System.out.println("		-for attack: "+attackAdaptations);
		System.out.println("		-for task: "+taskAdaptations);
		
		ArrayList<String> commonAdaptations = new ArrayList<>(attackAdaptations);
		commonAdaptations.retainAll(taskAdaptations);
		System.out.println("		-common: "+commonAdaptations);
		Map<String, Double> adaptationCost = new HashMap<String, Double>();
		ArrayList<Adaptation> adaptations = new ArrayList<Adaptation>();
		Adaptation currentAdaptation;
		for(String adaptation:commonAdaptations) {
			System.out.println("			-Calculate cost of "+adaptation);
			currentAdaptation=predictCostOfAdaptation(s,adaptation,workflow);
			currentAdaptation.calculateMitigationScore(s,attack);
			adaptations.add(currentAdaptation);
			//adaptationCost.put(adaptation,currentAdaptation.getTotalCost());
		}
		
		double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        double minMS = Double.MAX_VALUE;
        double maxMS = Double.MIN_VALUE;
		
		if(adaptations.size()>1) {
			//normalized the time,price and value
	        for(Adaptation option:adaptations){
	            minPrice = Math.min(minPrice, option.getPrice());
	            maxPrice = Math.max(maxPrice, option.getPrice());
	            minTime = Math.min(minTime, option.getTime());
	            maxTime = Math.max(maxTime, option.getTime());
	            minValue = Math.min(minValue, option.getValue());
	            maxValue = Math.max(maxValue, option.getValue());
	            minMS = Math.min(minMS, option.getMitigationScore());
	            maxMS = Math.max(maxMS, option.getMitigationScore());
	        }
		}
		else {//if there is only one adaptation, we don't need to normalize
		    minPrice = 0;
            maxPrice = 1;
            minTime = 0;
            maxTime = 1;
            minValue = 0;
            maxValue = 1;
            minMS = 0;
            maxMS = 1;
		}
		System.out.println("		-minPrice "+ minPrice+" maxPrice "+maxPrice+" minTime "+minTime+" maxTime "+maxTime+" minValue "+minValue+" maxValue "+maxValue+" maxMS "+maxMS);
		for(Adaptation option:adaptations) {
			double normalizedPrice = (minPrice == maxPrice) ? 0 : (option.getPrice() - minPrice) / (maxPrice - minPrice);
			System.out.println(option.getPrice()+"- "+minPrice+ "/" +maxPrice +"-"+ minPrice+": "+(option.getPrice() - minPrice) / (maxPrice - minPrice));
            double normalizedTime = (minTime == maxTime) ? 0 : (option.getTime() - minTime) / (maxTime - minTime);
            System.out.println((option.getTime() - minTime) / (maxTime - minTime));
            double normalizedValue = (minValue == maxValue) ? 0 : (option.getValue() - minValue) / (maxValue - minValue);
            double normalizedMS = (minMS == maxMS) ? 0 : (option.getMitigationScore() - minMS) / (maxMS - minMS);

            System.out.println("		-Normalize ("+option.getName()+"): "+" time "+normalizedTime +" price "+normalizedPrice+" value "+normalizedValue+" MS "+normalizedMS);
	        option.setTotalCost((this.priceWeight*normalizedPrice)+(this.timeWeight*normalizedTime)-(this.valueWeight*normalizedValue)-(this.securityWeight*normalizedMS));
			adaptationCost.put(option.getName(),option.getTotalCost());
			System.out.println("		-NewCost For adaptation ("+option.getName()+"): "+option.getTotalCost()+" time "+option.getTime() +" price "+option.getPrice()+" value "+option.getValue()+" MS"+option.getMitigationScore());

		}
	
		String selectedAdaptation=selectBestAdaptationsStrategy2(adaptationCost,s,workflow);
		
		//coopis calculation
		for(Adaptation option:adaptations) {
			if(option.getName().equals(selectedAdaptation)) {
				/*
				double mitigationScore=0;
				for(int i=0;i<3;i++) {
					mitigationScore+=(1-s.getSecurityObjectiveRequirements().get(i)*attack.getSecurityImpact().get(i))*option.getSecurityImpact().get(i);
					System.out.println("		-s.getSecurityObjectiveRequirements().get(i): "+s.getSecurityObjectiveRequirements().get(i)+" attack.getSecurityImpact().get(i): "+attack.getSecurityImpact().get(i)+" option.getSecurityImpact().get(i): "+option.getSecurityImpact().get(i)+" mitigationScore: "+mitigationScore);

				}
				double coopisCost=this.timeWeight*option.getTime()+  this.priceWeight*option.getPrice()-this.valueWeight*option.getValue()-this.securityWeight*mitigationScore;
				System.out.println("		-Coopis: "+adaptationCost+" time: "+option.getTime()+" price: "+option.getPrice()+" Value: "+option.getValue()+" mitigationScore: "+mitigationScore);
				*/
				coopisTime+=option.getTime();
				coopisPrice+=option.getPrice();
				coopisValue+=option.getValue();
				coopisScore+=option.getMitigationScore();
				addTaskToFile(s.getTaskId()+"\t"+attack.getName()+"\t"+getSeverity(attackScore)+"\t"+attackScore+"\t"+selectedAdaptation+"\t"+option.getTotalCost()+"\t"+option.getTime()+"\t"+option.getPrice()+"\t"+option.getValue()+"\t"+option.getMitigationScore(),"LearningAdaptationLogCoopis.txt");//add this adaptation to file 
				completeVersion++;
				System.out.println("Coopis time/price/vale/score: "+coopisTime+" "+coopisPrice+" "+coopisValue+" "+coopisScore);
			}
				
		}
		
		//addTaskToFile(s.getTaskId()+"\t"+attack.getName()+"\t"+attackScore+"\t"+adaptationCost+"\t"+selectedAdaptation,"adaptationLog.txt");//add this adaptation to file 
		//addTaskToFile(s.getProcessID()+"\t"+s.getInstanceId()+"\t"+s.getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		//addTaskToFile("Tenant: "+this.getId()+" Service malicious behaviour is detected in Service Task:"+ s.getId()+", Workflow: "+workflow.getId()+", Instance: "+ s.getInstanceId()+" detected attack: "+attack.getName()+" selected adatation: "+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		 //String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
		//addTaskToFile(timestamp+"\t"+this.getId()+"\t"+workflow.getId()+"\t"+ s.getInstanceId()+"\t"+taskNumber+"\t ServiceTask \t Abnormal "+"\t"+ s.getService().getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		
		numberOfDetectedattacks++;

	}
	
	
	public void localAdaptationCoopisPredictive(ServiceTask s,String payload,double attackScore,Workflow workflow,int taskNumber) throws IOException{
		System.out.println("localAdaptation Coopis");
		Attack attack=null;
		for(Attack a:AttackTypes) {
			//System.out.println(attack.getName()+" "+payload);
			if(payload.contains(a.getName()))
				attack=a;
		}
		
		System.out.println("$For serviceTask "+s.getTaskId()+" in instance "+s.getInstanceId()+" from ProcessId "+s.getProcessId()+" from workflow "+workflow.getName()+"  attack:"+attack.getName()+" with attackScore "+attackScore+"("+getSeverity(attackScore)+") will be adapted");
		
		ArrayList<String> attackAdaptations=attack.getAttackAdaptationPattern(getSeverity(attackScore));
		ArrayList<String> taskAdaptations=workflow.getLegalAdaptationforServiceTask(s.getTaskId());
		
		System.out.println("	Available Adaptation: ");
		System.out.println("		-for attack: "+attackAdaptations);
		System.out.println("		-for task: "+taskAdaptations);
		
		ArrayList<String> commonAdaptations = new ArrayList<>(attackAdaptations);
		commonAdaptations.retainAll(taskAdaptations);
		System.out.println("		-common: "+commonAdaptations);
		Map<String, Double> adaptationCost = new HashMap<String, Double>();
		ArrayList<Adaptation> adaptations = new ArrayList<Adaptation>();
		Adaptation currentAdaptation;
		for(String adaptation:commonAdaptations) {
			System.out.println("			-Calculate cost of "+adaptation);
			currentAdaptation=predictCostOfAdaptation(s,adaptation,workflow);
			currentAdaptation.calculateMitigationScore(s,attack);
			adaptations.add(currentAdaptation);
			//adaptationCost.put(adaptation,currentAdaptation.getTotalCost());
		}
		
		double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        double minMS = Double.MAX_VALUE;
        double maxMS = Double.MIN_VALUE;
		
		if(adaptations.size()>1) {
			//normalized the time,price and value
	        for(Adaptation option:adaptations){
	            minPrice = Math.min(minPrice, option.getPrice());
	            maxPrice = Math.max(maxPrice, option.getPrice());
	            minTime = Math.min(minTime, option.getTime());
	            maxTime = Math.max(maxTime, option.getTime());
	            minValue = Math.min(minValue, option.getValue());
	            maxValue = Math.max(maxValue, option.getValue());
	            minMS = Math.min(minMS, option.getMitigationScore());
	            maxMS = Math.max(maxMS, option.getMitigationScore());
	        }
		}
		else {//if there is only one adaptation, we don't need to normalize
		    minPrice = 0;
            maxPrice = 1;
            minTime = 0;
            maxTime = 1;
            minValue = 0;
            maxValue = 1;
            minMS = 0;
            maxMS = 1;
		}
		System.out.println("		-minPrice "+ minPrice+" maxPrice "+maxPrice+" minTime "+minTime+" maxTime "+maxTime+" minValue "+minValue+" maxValue "+maxValue+" maxMS "+maxMS);
		for(Adaptation option:adaptations) {
			double normalizedPrice = (minPrice == maxPrice) ? 0 : (option.getPrice() - minPrice) / (maxPrice - minPrice);
			System.out.println(option.getPrice()+"- "+minPrice+ "/" +maxPrice +"-"+ minPrice+": "+(option.getPrice() - minPrice) / (maxPrice - minPrice));
            double normalizedTime = (minTime == maxTime) ? 0 : (option.getTime() - minTime) / (maxTime - minTime);
            System.out.println((option.getTime() - minTime) / (maxTime - minTime));
            double normalizedValue = (minValue == maxValue) ? 0 : (option.getValue() - minValue) / (maxValue - minValue);
            double normalizedMS = (minMS == maxMS) ? 0 : (option.getMitigationScore() - minMS) / (maxMS - minMS);

            System.out.println("		-Normalize ("+option.getName()+"): "+" time "+normalizedTime +" price "+normalizedPrice+" value "+normalizedValue+" MS "+normalizedMS);
            option.setNormalized(normalizedPrice,normalizedTime,normalizedValue,normalizedMS);
            option.setTotalCost((this.priceWeight*normalizedPrice)+(this.timeWeight*normalizedTime)-(this.valueWeight*normalizedValue)-(this.securityWeight*normalizedMS));
			adaptationCost.put(option.getName(),option.getTotalCost());
			System.out.println("		-NewCost For adaptation ("+option.getName()+"): "+option.getTotalCost()+" time "+option.getTime() +" price "+option.getPrice()+" value "+option.getValue()+" MS"+option.getMitigationScore());

		}
	

		//coopis calculation
		ActionRecord ActionPredictive=new ActionRecord(s.getTaskId(),attack.getName(),getSeverity(attackScore),attackScore);
		ActionRecord Action=new ActionRecord(s.getTaskId(),attack.getName(),getSeverity(attackScore),attackScore);

		//select random action for creating logfile for ML
		String randomAction=selectRandomAdaptationsStrategy2(adaptationCost,s,workflow);
		String selectedAction=selectBestAdaptationsStrategy2(adaptationCost,s,workflow);
		String selectedAdaptation=qlearning.learn(commonAdaptations,ActionPredictive,randomAction,ActionRecordsAdaptive);
		
		for(Adaptation option:adaptations) 
			if(option.getName().equals(selectedAdaptation) && selectedAdaptation!=null ) 
				System.out.println("Adaptation selected by qlearning strategy: "+selectedAdaptation+" time "+option.getTime()+"Price"+option.getPrice());
			
		for(Adaptation option:adaptations) 
			if(option.getName().equals(selectedAction) && selectedAction!=null ) 
				System.out.println("Adaptation selected lowes cost strategy: "+selectedAction+" time "+option.getTime()+"Price"+option.getPrice());
		
		//for adatation selected by adaptive  strategy:
		for(Adaptation option:adaptations) {
			if(option.getName().equals(selectedAdaptation) && selectedAdaptation!=null ) {
			
				/*
				double mitigationScore=0;
				for(int i=0;i<3;i++) {
					mitigationScore+=(1-s.getSecurityObjectiveRequirements().get(i)*attack.getSecurityImpact().get(i))*option.getSecurityImpact().get(i);
					System.out.println("		-s.getSecurityObjectiveRequirements().get(i): "+s.getSecurityObjectiveRequirements().get(i)+" attack.getSecurityImpact().get(i): "+attack.getSecurityImpact().get(i)+" option.getSecurityImpact().get(i): "+option.getSecurityImpact().get(i)+" mitigationScore: "+mitigationScore);

				}
				double coopisCost=this.timeWeight*option.getTime()+  this.priceWeight*option.getPrice()-this.valueWeight*option.getValue()-this.securityWeight*mitigationScore;
				System.out.println("		-Coopis: "+adaptationCost+" time: "+option.getTime()+" price: "+option.getPrice()+" Value: "+option.getValue()+" mitigationScore: "+mitigationScore);
				*/
				coopisTimeAdaptive+=option.getTime();
				coopisPriceAdaptive+=option.getPrice();
				coopisValueAdaptive+=option.getValue();
				coopisScoreAdaptive+=option.getMitigationScore();
				State currentSt=qlearning.getState(ActionPredictive,ActionRecordsAdaptive);
				ActionPredictive.setState(currentSt);
				
				ActionPredictive.setPrameters(selectedAdaptation,option.getSecurityImpact() ,option.getTotalCost(),option.getTime(),option.getPrice(),option.getValue(),option.getMitigationScore(),currentSt,option.getNormalizedprice(),option.getNormalizedtime(),option.getNormalizedvalue(),option.getNormalizedMS());
				System.out.println("currentSt"+currentSt);
				
				System.out.println("before Uncertainty CoopisAdaptive time/price/vale/score: "+coopisTimeAdaptive+" "+coopisPriceAdaptive+" "+coopisValueAdaptive+" "+coopisScoreAdaptive);
				System.out.println("before Uncertainty CoopisAdaptive totalcostAction: "+ActionPredictive.getTotalCost());

				currentSt.CheckForUncertainty();
				
				coopisTimeAdaptive+=ActionPredictive.Uncertaintime();
				coopisPriceAdaptive+=ActionPredictive.Uncertainprice();
				System.out.println("after Uncertainty CoopisAdaptive time/price/vale/score: "+coopisTimeAdaptive+" "+coopisPriceAdaptive+" "+coopisValueAdaptive+" "+coopisScoreAdaptive);
				System.out.println("After Uncertainty CoopisAdaptive totalcostAction: "+ActionPredictive.getTotalUncertainCost(priceWeight,timeWeight,valueWeight,securityWeight));
				qlearning.setReward(ActionPredictive.getTotalUncertainCost(priceWeight,timeWeight,valueWeight,securityWeight),ActionPredictive.getState());
				
				//System.out.println("hereee");
				ActionRecordsAdaptive.add(ActionPredictive);
				//addTaskToFile(ActionPredictive.ToString(),"LearningAdaptationLogCoopisAdaptive.txt");
				//addTaskToFile(s.getTaskId()+","+attack.getName()+","+getSeverity(attackScore)+","+attackScore+","+selectedAdaptation+","+option.getTotalCost()+","+option.getTime()+","+option.getPrice()+","+option.getValue()+","+option.getMitigationScore(),"TraningLearningAdaptationLogCoopis.txt");//add this adaptation to file 
				completeVersion++;
			}
		}
			
			//for adatation selected by lowest-cost strategy:
			for(Adaptation option:adaptations) {
				if(option.getName().equals(selectedAction) && selectedAction!=null ) {
					/*
					double mitigationScore=0;
					for(int i=0;i<3;i++) {
						mitigationScore+=(1-s.getSecurityObjectiveRequirements().get(i)*attack.getSecurityImpact().get(i))*option.getSecurityImpact().get(i);
						System.out.println("		-s.getSecurityObjectiveRequirements().get(i): "+s.getSecurityObjectiveRequirements().get(i)+" attack.getSecurityImpact().get(i): "+attack.getSecurityImpact().get(i)+" option.getSecurityImpact().get(i): "+option.getSecurityImpact().get(i)+" mitigationScore: "+mitigationScore);

					}
					double coopisCost=this.timeWeight*option.getTime()+  this.priceWeight*option.getPrice()-this.valueWeight*option.getValue()-this.securityWeight*mitigationScore;
					System.out.println("		-Coopis: "+adaptationCost+" time: "+option.getTime()+" price: "+option.getPrice()+" Value: "+option.getValue()+" mitigationScore: "+mitigationScore);
					*/
					coopisTime+=option.getTime();
					coopisPrice+=option.getPrice();
					coopisValue+=option.getValue();
					coopisScore+=option.getMitigationScore();
					//State currentSt=qlearning.getState(Action,ActionRecords);
					//Action.setState(currentSt);
					
					//Action.setPrameters(option.getSecurityImpact() ,option.getTotalCost(),option.getTime(),option.getPrice(),option.getValue(),option.getMitigationScore(),currentSt,option.getNormalizedprice(),option.getNormalizedtime(),option.getNormalizedvalue(),option.getNormalizedMS());
					Action.setPrameters(selectedAction,option.getSecurityImpact() ,option.getTotalCost(),option.getTime(),option.getPrice(),option.getValue(),option.getMitigationScore(),option.getNormalizedprice(),option.getNormalizedtime(),option.getNormalizedvalue(),option.getNormalizedMS());

					//System.out.println("currentSt"+currentSt);
					
					System.out.println("before Uncertainty Coopis time/price/vale/score: "+coopisTime+" "+coopisPrice+" "+coopisValue+" "+coopisScore);
					System.out.println("before Uncertainty Coopis totalcostAction: "+Action.getTotalCost());

					CheckForUncertainty(Action);
					//currentSt.CheckForUncertainty();
					
					coopisTime+=Action.Uncertaintime();
					coopisPrice+=Action.Uncertainprice();
					System.out.println("after Uncertainty Coopis time/price/vale/score: "+coopisTime+" "+coopisPrice+" "+coopisValue+" "+coopisScore);
					System.out.println("After Uncertainty Coopis totalcostAction: "+Action.getTotalUncertainCost(priceWeight,timeWeight,valueWeight,securityWeight));
					qlearning.setReward(Action.getTotalUncertainCost(priceWeight,timeWeight,valueWeight,securityWeight),Action.getState());
					
					//System.out.println("hereee");
					ActionRecords.add(Action);
					//addTaskToFile(Action.ToString(),"LearningAdaptationLogCoopis.txt");

					//addTaskToFile(s.getTaskId()+","+attack.getName()+","+getSeverity(attackScore)+","+attackScore+","+selectedAdaptation+","+option.getTotalCost()+","+option.getTime()+","+option.getPrice()+","+option.getValue()+","+option.getMitigationScore(),"TraningLearningAdaptationLogCoopis.txt");//add this adaptation to file 
					completeVersion++;
				}
			
				
		}
		
		//addTaskToFile(s.getTaskId()+"\t"+attack.getName()+"\t"+attackScore+"\t"+adaptationCost+"\t"+selectedAdaptation,"adaptationLog.txt");//add this adaptation to file 
		//addTaskToFile(s.getProcessID()+"\t"+s.getInstanceId()+"\t"+s.getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		//addTaskToFile("Tenant: "+this.getId()+" Service malicious behaviour is detected in Service Task:"+ s.getId()+", Workflow: "+workflow.getId()+", Instance: "+ s.getInstanceId()+" detected attack: "+attack.getName()+" selected adatation: "+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		 //String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
		//addTaskToFile(timestamp+"\t"+this.getId()+"\t"+workflow.getId()+"\t"+ s.getInstanceId()+"\t"+taskNumber+"\t ServiceTask \t Abnormal "+"\t"+ s.getService().getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		
		numberOfDetectedattacks++;

	}
	
	
	public void CheckForUncertainty(ActionRecord currentAction) {
			System.out.println("CheckForUncertainty");
			System.out.println("ActionRecords.size()"+ActionRecords.size());

			if(ActionRecords.size()==1 && !ActionRecordsContains("Insert")) {
				currentAction.setUncertainprice(2);
			}
			else if(ActionRecords.size()==1 && ActionRecordsContains("Insert")) {
				System.out.print("CheckForUncertainty: Insert");
				currentAction.setUncertainprice(1);
			}
			else if(ActionRecords.size()==2) {
				currentAction.setUncertainprice(1);
			}
			else if(ActionRecords.size()>2 && ActionRecords.size()<=5) {
				currentAction.setUncertainprice(1);
			}
			else if(ActionRecords.size()>5) {
				currentAction.setUncertainprice(1);
			}
			/*
			for(ActionRecord AR:ActionRecords) {
				System.out.print(AR.ToString());
			}*/
		}
	
	public boolean ActionRecordsContains(String s) {
		if(ActionRecords.size()!=0)
			for(ActionRecord A:ActionRecords) {
				if(A.getSelectedAdaptation()!=null)
					if(A.getSelectedAdaptation().equals(s))
						return true;
				}
		return false;
	}
	
	public void localAdaptationEdoc(ServiceTask s,String payload,double attackScore,Workflow workflow,int taskNumber) throws IOException{
		System.out.println("localAdaptation Edoc");
		Attack attack=null;
		for(Attack a:AttackTypes) {
			//System.out.println(attack.getName()+" "+payload);
			if(payload.contains(a.getName()))
				attack=a;
		}
		
		System.out.println("$For serviceTask "+s.getTaskId()+" in instance "+s.getInstanceId()+" from ProcessId "+s.getProcessId()+" from workflow "+workflow.getName()+"  attack:"+attack.getName()+" with attackScore "+attackScore+"("+getSeverity(attackScore)+") will be adapted");
		
		ArrayList<String> attackAdaptations=attack.getAttackAdaptationPattern(getSeverity(attackScore));
		ArrayList<String> taskAdaptations=workflow.getLegalAdaptationforServiceTask(s.getTaskId());
		
		System.out.println("	Available Adaptation: ");
		System.out.println("		-for attack: "+attackAdaptations);
		System.out.println("		-for task: "+taskAdaptations);
		
		ArrayList<String> commonAdaptations = new ArrayList<>(attackAdaptations);
		commonAdaptations.retainAll(taskAdaptations);
		System.out.println("		-common: "+commonAdaptations);
		Map<String, Double> adaptationCost = new HashMap<String, Double>();
		ArrayList<Adaptation> adaptations = new ArrayList<Adaptation>();
		Adaptation currentAdaptation;
		for(String adaptation:commonAdaptations) {
			System.out.println("			-Calculate cost of "+adaptation);
			currentAdaptation=predictCostOfAdaptation(s,adaptation,workflow);
			adaptations.add(currentAdaptation);
			//adaptationCost.put(adaptation,currentAdaptation.getTotalCost());
		}
		
		double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
		
		if(adaptations.size()>1) {
			//normalized the time,price and value
	        for(Adaptation option:adaptations){
	            minPrice = Math.min(minPrice, option.getPrice());
	            maxPrice = Math.max(maxPrice, option.getPrice());
	            minTime = Math.min(minTime, option.getTime());
	            maxTime = Math.max(maxTime, option.getTime());
	            minValue = Math.min(minValue, option.getValue());
	            maxValue = Math.max(maxValue, option.getValue());
	        }
		}
		else {//if there is only one adaptation, we don't need to normalize
		    minPrice = 0;
            maxPrice = 1;
            minTime = 0;
            maxTime = 1;
            minValue = 0;
            maxValue = 1;
		}
		System.out.println("		-minPrice "+ minPrice+" maxPrice "+maxPrice+" minTime "+minTime+" maxTime "+maxTime+" minValue "+minValue+" maxValue "+maxValue);
		for(Adaptation option:adaptations) {
			double normalizedPrice = (minPrice == maxPrice) ? 0 : (option.getPrice() - minPrice) / (maxPrice - minPrice);
			System.out.println(option.getPrice()+"- "+minPrice+ "/" +maxPrice +"-"+ minPrice+": "+(option.getPrice() - minPrice) / (maxPrice - minPrice));
            double normalizedTime = (minTime == maxTime) ? 0 : (option.getTime() - minTime) / (maxTime - minTime);
            System.out.println((option.getTime() - minTime) / (maxTime - minTime));
            double normalizedValue = (minValue == maxValue) ? 0 : (option.getValue() - minValue) / (maxValue - minValue);
			System.out.println("		-Normalize ("+option.getName()+"): "+" time "+normalizedTime +" price "+normalizedPrice+" value "+normalizedValue);
	        option.setTotalCost((this.priceWeight*normalizedPrice)+(this.timeWeight*normalizedTime)-(this.valueWeight*normalizedValue));
			adaptationCost.put(option.getName(),option.getTotalCost());
			System.out.println("		-NewCost For adaptation ("+option.getName()+"): "+option.getTotalCost()+" time "+option.getTime() +" price "+option.getPrice()+" value "+option.getValue());

		}
	
		String selectedAdaptation=selectBestAdaptationsStrategy2(adaptationCost,s,workflow);
		
		//Edoc calculation
		for(Adaptation option:adaptations) {
			if(option.getName().equals(selectedAdaptation)) {
				double mitigationScore=0;
				for(int i=0;i<3;i++) {
					mitigationScore+=(1-s.getSecurityObjectiveRequirements().get(i)*attack.getSecurityImpact().get(i))*option.getSecurityImpact().get(i);
					System.out.println("		-s.getSecurityObjectiveRequirements().get(i): "+s.getSecurityObjectiveRequirements().get(i)+" attack.getSecurityImpact().get(i): "+attack.getSecurityImpact().get(i)+" option.getSecurityImpact().get(i): "+option.getSecurityImpact().get(i)+" mitigationScore: "+mitigationScore);

				}
				double edocCost=this.timeWeight*option.getTime()+  this.priceWeight*option.getPrice()-this.securityWeight*mitigationScore;
				System.out.println("		-edocCost: "+adaptationCost+" time: "+option.getTime()+" price: "+option.getPrice()+" mitigationScore: "+mitigationScore);
				edocTime+=option.getTime();
				edoPrice+=option.getPrice();
				edocScore+=mitigationScore;
				addTaskToFile(s.getTaskId()+"\t"+attack.getName()+"\t"+selectedAdaptation+"\t"+edocCost+"\t"+option.getTime()+"\t"+option.getPrice()+"\t"+mitigationScore,"ResultLogEdoc.txt");//add this adaptation to file 
				System.out.println("Edoc time: "+edocTime+" "+edoPrice+" "+edocScore);
			}
				
		}
		
		addTaskToFile(s.getTaskId()+"\t"+attack.getName()+"\t"+attackScore+"\t"+adaptationCost+"\t"+selectedAdaptation,"adaptationLog.txt");//add this adaptation to file 
		//addTaskToFile(s.getProcessID()+"\t"+s.getInstanceId()+"\t"+s.getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		//addTaskToFile("Tenant: "+this.getId()+" Service malicious behaviour is detected in Service Task:"+ s.getId()+", Workflow: "+workflow.getId()+", Instance: "+ s.getInstanceId()+" detected attack: "+attack.getName()+" selected adatation: "+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		 String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
		addTaskToFile(timestamp+"\t"+this.getId()+"\t"+workflow.getId()+"\t"+ s.getInstanceId()+"\t"+taskNumber+"\t ServiceTask \t Abnormal "+"\t"+ s.getService().getId()+"\t"+attack.getName()+"\t"+selectedAdaptation,"adaptationLogEdoc.txt");//add this adaptation to file 
		
		numberOfDetectedattacks++;

	}
	
	public String selectBestAdaptationsStrategy2(Map<String, Double> adaptationCost,ServiceTask s,Workflow workflow)throws IOException{
		double minCost = Double.MAX_VALUE;
		
		for (Double d : adaptationCost.values()) {
		    if (d < minCost) {
		    	minCost = d;
		    }
		}
		//System.out.println("minCost "+minCost);
		//System.out.println("minCost "+ adaptationCost.values());
		String selectedAdaptation="";
		for (Map.Entry<String, Double> entry : adaptationCost.entrySet()) {
			//System.out.println("minCost "+ entry);
		    if (entry.getValue() == minCost) {
		    	selectedAdaptation = entry.getKey();
		        break;
		    }
		}
		System.out.println("			-selectedAdaptation : "+selectedAdaptation+" with Cost "+minCost+" which is the minimum cost between all possible adaptation actions");
		if(selectedAdaptation.equals("ReExecute") || selectedAdaptation.equals("ReConfiguration"))
			KM.GlobalAdaptation_ServiceAdaptation(selectedAdaptation,s,workflow);
		else
			 performTaskAdaptation(selectedAdaptation,s);
		return selectedAdaptation;
	}
	
	public String selectRandomAdaptationsStrategy2(Map<String, Double> adaptationCost,ServiceTask s,Workflow workflow)throws IOException{
		Random rand = new Random();
		String selectedAdaptation="";
		if(adaptationCost.size()>0) {
		int randadapt=rand.nextInt(adaptationCost.size());
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(adaptationCost.entrySet());
		selectedAdaptation=entryList.get(randadapt).getKey();
	
		//System.out.println("			-selectedAdaptation : "+selectedAdaptation+" with Cost "+minCost+" which is the minimum cost between all possible adaptation actions");
		if(selectedAdaptation.equals("ReExecute") || selectedAdaptation.equals("ReConfiguration"))
			KM.GlobalAdaptation_ServiceAdaptation(selectedAdaptation,s,workflow);
		else
			 performTaskAdaptation(selectedAdaptation,s);
		}
		return selectedAdaptation;
	}
	
	public void selectBestAdaptationsStrategy(Map<String, Double> adaptationCost,ServiceTask s){
		
		Map<String, Double>  firstCategory=new HashMap<String, Double>();
		Map<String, Double>  secondCategory=new HashMap<String,Double>();
		
		for(String adaptation:adaptationCost.keySet()) {
			//from first category: ReExecute,Switch,Skip
			if(adaptation.equals("ReExecute") ||  adaptation.equals("Late") || adaptation.equals("Skip")) {
				firstCategory.put(adaptation,adaptationCost.get(adaptation));
			}
			
			//from second category: ReConfiguration(IncreaseMonitoring,EnhancingSecurityMeasures,RestrictingAccess)
			if(adaptation.equals("ReConfiguration") || adaptation.equals("IncreaseMonitoring") || adaptation.equals("EnhancingSecurityMeasures") || adaptation.equals("RestrictingAccess")) {
				secondCategory.put(adaptation,adaptationCost.get(adaptation));
			}	
		}
		double finalAdaptationCost=0; 
		if(!firstCategory.isEmpty())
			finalAdaptationCost+=firstAdaptationCategory(firstCategory,s);
		if(!secondCategory.isEmpty())
			finalAdaptationCost+=secondAdaptationCategory(secondCategory,s);
		
		System.out.println("	******Final Adaptation cost= "+finalAdaptationCost);

	}
	
	
	public double firstAdaptationCategory(Map<String, Double> adaptationCost,ServiceTask s){
		double minCost = Double.MAX_VALUE;
		
		for (Double d : adaptationCost.values()) {
		    if (d < minCost) {
		    	minCost = d;
		    }
		}
		//System.out.println("minCost "+minCost);
		//System.out.println("minCost "+ adaptationCost.values());
		String selectedAdaptation="";
		for (Map.Entry<String, Double> entry : adaptationCost.entrySet()) {
			//System.out.println("minCost "+ entry);
		    if (entry.getValue() == minCost) {
		    	selectedAdaptation = entry.getKey();
		        break;
		    }
		}
		
		performTaskAdaptation(selectedAdaptation,s);
		return minCost;
	}
	
	public double secondAdaptationCategory(Map<String, Double> adaptationCost,ServiceTask s) {
		double cost=0;
		for(String adaptation:adaptationCost.keySet()) {
			performConfigurationAdaptation(adaptation,s);
			cost+=adaptationCost.get(adaptation);
		}
		return cost;
	}
	
	public void performTaskAdaptation(String adaptation,ServiceTask s) {
		System.out.println("	*Selected Task Adaptation: "+adaptation+" for task "+s.getTaskId());

	}
	
	public void performConfigurationAdaptation(String adaptation,ServiceTask s) {
		System.out.println("	*Selected Configuration Adaptation: "+adaptation+" for task "+s.getTaskId());

	}
	
	public Adaptation predictCostOfAdaptation(ServiceTask s,String adaptation,Workflow workflow) {

		//System.out.println(" cost vector "+workflow.getCostsforServiceTask(s.getTaskId()));
		ArrayList<Double> costs=workflow.getCostsforServiceTask(s.getTaskId());
		double cost=0,price=s.getService(). getCost(),time=s.getService(). getART(),value=costs.get(2);
		//System.out.println(" cost vector "+price+" "+time+" "+value);
		
		double oldtotalCost=this.priceWeight*price+this.timeWeight*time-this.valueWeight*value;// time and cost have positive and value has negetive relationship with final cost 
		System.out.println("					-OldCost  "+oldtotalCost+" time "+time +" price "+price+" value "+value);
		
		
		ArrayList<String> costVector=workflow.getAdaptationCostForService_Action(s.getTaskId(),adaptation);
		//System.out.println(" adaptation cost vector "+costVector.get(0)+" "+costVector.get(1)+" "+costVector.get(2));

		//Tenant-level adaptation
		//a:Task-level adaptations: 
			
		if(adaptation.equals("ReExecute")) {
			//System.out.println("s.getBackupService(). getCost()  "+s.getBackupService(). getCost()+" s.getBackupService(). getART() "+s.getBackupService(). getART());
			time=s.getBackupService(). getART();
			price=s.getBackupService(). getCost();
		}
		
		else if(adaptation.equals("Redundancy")) {
			//System.out.println("s.getBackupService(). getCost()  "+s.getBackupService(). getCost()+" s.getBackupService(). getART() "+s.getBackupService(). getART());
			time=s.getBackupService(). getART();
			price=price+s.getBackupService(). getCost();
			value=value*2;
		}
		
		else if(adaptation.equals("Late") || adaptation.equals("Insert")) {
			if (costVector.get(0).length() > 1) {
				if(costVector.get(0).charAt(1)== '/') 
					time=time/Character.getNumericValue(costVector.get(0).charAt(2));
				if(costVector.get(0).charAt(1)== '*') 
					time=time*Character.getNumericValue(costVector.get(0).charAt(2));
			}
			//else time=time
			if (costVector.get(1).length() > 1) {
				if(costVector.get(1).charAt(1)== '/') 
					price=price/Character.getNumericValue(costVector.get(1).charAt(2));
				if(costVector.get(1).charAt(1)== '*') 
					price=price*Character.getNumericValue(costVector.get(1).charAt(2));
			}
			//else price=price
			if (costVector.get(2).length() > 1) {
				if(costVector.get(2).charAt(1)== '/') 
					value=value/Character.getNumericValue(costVector.get(2).charAt(2));
				if(costVector.get(2).charAt(1)== '*') 
					value=value*Character.getNumericValue(costVector.get(2).charAt(2));
			}
			//else value=value
			//System.out.println("here cost  "+time+" "+price+" "+value);
		}
		
		else if(adaptation.equals("Switch")) {
			value=value/2;
		}
		else if(adaptation.equals("Skip")) {
			value=0;
			time=0;
			price=0;
		}
		else if(adaptation.equals("Recovery")) {
			time=2*time-time/2;
			price=2*price-price/2;
		}
		else if(adaptation.equals("Containment")) {
			time=3*time;
			price=3*price;
		}
		
		//b:Process-level adaptations:
		else if(adaptation.equals("Rerouting")) {
			time=2*time;
			price=2*price;
		}
		
		else if(adaptation.equals("ReSequencing")) {
			time=2*time;
			price=2*price;
		}
		
		else if(adaptation.equals("ProcessSuspension")) {
			value=0;
		}
		
		//Middleware-level adaptations: 
		else if(adaptation.equals("ReConfiguration")) {
			time+=this.timeOfReconfigration;
			cost+=this.costOfReconfigration;
			value+=this.valueOfReconfigration;
			
		}
		
		else if(adaptation.equals("IncreaseMonitoring")) {
			time+=this.timeOfIncreaseMonitoring;
			cost+=this.costOfIncreaseMonitoring;
			value=value+1;

		}
		
		else if(adaptation.equals("EnhancingSecurityMeasures")) {
			time+=this.timeOfEnhancingSecurityMeasures;
			cost+=this.costOfEnhancingSecurityMeasures;
			value=value+1;
		}
		
		else if(adaptation.equals("RestrictingAccess")) {
			time+=this.timeOfRestrictingAccess;
			cost+=this.costOfRestrictingAccess;
			value=value+1;
		}
		
	
		//double newTotalCost=this.priceWeight*price+this.timeWeight*time-this.valueWeight*value;// time and cost have positive and value has negetive relationship with final cost 
		//System.out.println("					-NewCost: "+newTotalCost+" time "+time +" price "+price+" value "+value);

		
		
		/*  for normalizing the final cost of the workflow based on number of tasks that were under attack.
		//after applying new costs
		ArrayList<Double> newcosts=workflow.NormalizedCost(s,price,time,value);
		//System.out.println("new price,time,value  "+price+" "+time+" "+value);
		ArrayList<Double> totalcosts=workflow.TotalCost(s.getTaskId(),price,time,value);
		//cost=this.priceWeight*newcosts.get(0)+this.timeWeight*newcosts.get(1)-this.valueWeight*newcosts.get(2);// time and cost have positive and value has negetive relationship with final cost 
		double newTotalCost=this.priceWeight*totalcosts.get(0)+this.timeWeight*totalcosts.get(1)-this.valueWeight*totalcosts.get(2);// time and cost have positive and value has negetive relationship with final cost 
		System.out.println("				-newTotalCost  "+newTotalCost);
		double costRate=(double)newTotalCost/oldTotalCost;
		return costRate;
		*/

		Adaptation adaptationAction=new Adaptation(adaptation,time,price,value);
		return adaptationAction;
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
	
	public String getSeverity(double attackScore){
		String result="";
		if(attackScore<=10)
			result="low";
		else if(attackScore<=100)
			result="medium";
		else if(attackScore>100)
			result="high";
		return result;
	}
	
}
