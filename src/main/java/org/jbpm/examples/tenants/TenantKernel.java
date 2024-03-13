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
	double attackThresh=0;
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
	static int currentExecutionNumber=0;
	
	
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
	static int completeVersion=0;
	static ArrayList<Adaptation> upcomingAdaptations = new ArrayList<Adaptation>();//the adaptations that are scheduled to implement because of the prior violations
	static double coopisTime=0, coopisPrice=0,coopisValue=0, coopisScore=0,coopisTimeMax=0, coopisPriceMax=0,coopisValueMax=0, coopisScoreMax=0;	
	Map<Integer, ArrayList<Double>> CoopisResults = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore
	ArrayList<ActionRecord> ActionRecords = new ArrayList<ActionRecord>();
	
	//adaptive parameters in coopis
	static int completeVersionAdaptive=0;
	static ArrayList<Adaptation> upcomingAdaptationsAdaptive = new ArrayList<Adaptation>();//the adaptations that are scheduled to implement because of the prior violations
	static double coopisTimeAdaptive=0, coopisPriceAdaptive=0,coopisValueAdaptive=0, coopisScoreAdaptive=0,coopisTimeMaxAdaptive=0, coopisPriceMaxAdaptive=0,coopisValueMaxAdaptive=0, coopisScoreMaxAdaptive=0;	
	Map<Integer, ArrayList<Double>> CoopisResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore
	Map<Integer, ArrayList<Double>> CoopisResultsAdaptive = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore
	ArrayList<ActionRecord> ActionRecordsAdaptive = new ArrayList<ActionRecord>();
	QLearning qlearning;

	
	
	//Random cost parameters in ChainAdaptation
	static double chainTimeRandom=0, chainPriceRandom=0,chainValueRandom=0, chainScoreRandom=0,chainFinalCostRandom=0,chainTimeMaxRandom=0, chainPriceMaxRandom=0,chainValueMaxRandom=0, chainScoreMaxRandom=0,chainFinalCostMaxRandom=0;	
	ArrayList<ChainRecord> randomChainRecords = new ArrayList<ChainRecord>();
	Map<Integer, ArrayList<Double>> chainResultsrandom = new HashMap<Integer, ArrayList<Double>>();//Time,Price,value,MitigationScore,totalcost


	
	//lowest cost parameters in ChainAdaptation
	static double chainTime=0, chainPrice=0,chainValue=0, chainScore=0,chainFinalCost=0,chainTimeMax=0, chainPriceMax=0,chainValueMax=0, chainScoreMax=0,chainFinalCostMax=0;	
	Map<Integer, ArrayList<Double>> chainResultslowestcost = new HashMap<Integer, ArrayList<Double>>();//Time,Price,value,MitigationScore,totalcost
	ArrayList<ChainRecord> lowestCostChainRecords = new ArrayList<ChainRecord>();

	
	//adaptive parameters in ChainAdaptation
	static double chainTimeAdaptive=0, chainPriceAdaptive=0,chainValueAdaptive=0, chainScoreAdaptive=0,chainFinalCostAdaptive=0,chainTimeMaxAdaptive=0, chainPriceMaxAdaptive=0,chainValueMaxAdaptive=0, chainScoreMaxAdaptive=0,chainFinalCostMaxAdaptive=0;	
	Map<Integer, ArrayList<Double>> chainResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Price,value,MitigationScore,totalcost
	Map<Integer, ArrayList<Double>> chainResultsAdaptive = new HashMap<Integer, ArrayList<Double>>();//Time,Price,value,MitigationScore,totalcost
	static ArrayList<ChainRecord> currentWorkflowState = new ArrayList<ChainRecord>();
	static Map<StateChain, Map<AdaptationChain, Double>> qTable  = new HashMap<>();  // Q-table=state->action,qvalue
	static ArrayList<StateChain> states = new ArrayList<StateChain>();
	//ArrayList<ChainRecord> RLChainRecords = new ArrayList<ChainRecord>();
	QLearningChain qlearningchain;

	
	// parameters in Edoc
	static double edocTime=0, edoPrice=0, edocScore=0,edocTimeMax=0, edoPriceMax=0, edocScoreMax=0;
	Map<Integer, ArrayList<Double>> EdocResults = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,MitigationScore



	

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
		//Services s=new Services(createWorkflowModel("InsuranceClaim9-withAdaptation-withPriceTimeValue-moreOptions.txt"));
		Services s=new Services(createWorkflowModel("InsuranceClaim9-withAdaptation-withPriceTimeValue-moreOptions-controlDataDependency.txt"));


		HelloNafiseService h=new HelloNafiseService(createWorkflowModel("BPMN2-ServiceProcess2.txt"));
		//createWorkflowModel("BPMN2-ServiceProcess2.txt");
		
		trustAwareSchedulingPlanner();
		
		int k=0;

	
		//Chain experiments
		
		for(int i=0;i<200;i++) {
			qlearningchain= new QLearningChain();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			//addTaskToFile("small "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			//addTaskToFile("small "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
			addTaskToFile("small "+i+" ***************************************************************","LearningAdaptationLogchain.txt");
			addTaskToFile("small "+i+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
			currentExecutionNumber=i;

			ExecuteWorkflow("insurance6",1,"S");
			//ExecuteWorkflow("insurance6-2",1,"S");
			//ExecuteWorkflow("insurance6-3",1,"S");//InsuranceClaim9-3.bpmn2

			System.out.println("Number of detected attack for small bpmn: "+numberOfDetectedattacks);
			//addTaskToFile("End small "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			//addTaskToFile("End small "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");
			addTaskToFile("End small "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogchain.txt");
			addTaskToFile("End small "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");

			//values.add(chainTime+(numberOfDetectedattacks*adaptationTimeOverhead));
			values.add(chainTime);
			values.add(chainPrice);
			values.add(chainValue);
			values.add(chainScore);
			double cost1=(priceWeight*chainPrice)+(timeWeight*chainTime)-(valueWeight*chainValue)-(securityWeight*chainScore);
			values.add(cost1);
			chainResultslowestcost.put(i,values);
			System.out.println("Lowest cost Time:"+chainTime+(numberOfDetectedattacks*adaptationTimeOverhead)+" "+chainPrice+" "+chainValue);
			System.out.println(" lowestCostChainRecords  "+lowestCostChainRecords.size());
			String state1="";
			for(ChainRecord Action: lowestCostChainRecords) {
				 state1+=Action.ToString();
			}
			addTaskToFile(state1,"LearningAdaptationLogchain.txt");
		    addTaskToFile("for this workflow execution: LowestcostTime "+(chainTime+(numberOfDetectedattacks*adaptationTimeOverhead))+" LowestcostPrice "+chainPrice+" LowestcostValue "+chainValue+"LowestcostScore"+chainScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchain.txt");
		    	
		    	//valuesAdaptive.add(chainTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead));
		    	valuesAdaptive.add(chainTimeAdaptive);
				valuesAdaptive.add(chainPriceAdaptive);
				valuesAdaptive.add(chainValueAdaptive);
				valuesAdaptive.add(chainScoreAdaptive);
				double cost2=(priceWeight*chainPriceAdaptive)+(timeWeight*chainTimeAdaptive)-(valueWeight*chainValueAdaptive)-(securityWeight*chainScoreAdaptive);
				valuesAdaptive.add(cost2);
				chainResultsAdaptive.put(i,valuesAdaptive);
				chainResultsAdaptive100.put(i,valuesAdaptive);
				String state2="";
				for(ChainRecord Action: currentWorkflowState) {
					 state2+=Action.ToString();
				}
				addTaskToFile(state2,"LearningAdaptationLogchainAdaptive.txt");
		    	addTaskToFile("for this workflow execution: RLTime "+(chainTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead))+" RLPrice "+chainPriceAdaptive+" RLValue "+chainValueAdaptive+"chainScore"+chainScoreAdaptive+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
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
				    double totalFinalCost = 0.0,minFS=Double.MAX_VALUE;

				    
				
				    // Calculate the sum of Time, Cost, and MitigationScore
				    for (ArrayList<Double> result : chainResultsAdaptive100.values()) {
				        totalTime += result.get(0);
				        totalCost += result.get(1);
				        totalValue+=result.get(2);
				        totalMitigationScore += result.get(3);
				        totalFinalCost += result.get(4);
				        
				        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
				        if(result.get(0)<minTime && result.get(0)!=0)
				        	minTime=result.get(0);
				        if(result.get(1)<minCost && result.get(1)!=0)
				        	minCost=result.get(1);
				        if(result.get(2)<minValue && result.get(2)!=0)
				        	minValue=result.get(2);
				        if(result.get(3)<minMS && result.get(3)!=0)
				        	minMS=result.get(3);
				        if(result.get(4)<minFS && result.get(4)!=0)
				        	minFS=result.get(4);
				        
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
				    if(minFS==Double.MAX_VALUE)
				    	minFS=1;
				    
				    
				    double averageTime = totalTime / 100;
				    double averageCost = totalCost / 100;
				    double averageValue=totalValue/100;
				    double averageMitigationScore = totalMitigationScore / 100;
				    double averageFinalCost = totalFinalCost / 100;
				    
				    double NaverageTime = totalTime / 100/minTime;
				    double NaverageCost = totalCost / 100/minCost;
				    double NaverageValue=totalValue/100/minValue;
				    double NaverageMitigationScore = totalMitigationScore / 100/minMS;
				    double NaverageFinalCost = totalFinalCost / 100/minFS;

			    addTaskToFile("for 100s workflow execution average: RLTime "+averageTime+" RLPrice "+averageCost+" RLValue "+averageValue+"RLScore"+averageMitigationScore+"RLFinalCost"+averageFinalCost+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
		    	addTaskToFile("for 100s workflow execution Normalized: RLTime "+averageTime+" RLPrice "+averageCost+" RLValue "+averageValue+"RLScore"+averageMitigationScore+"RLFinalCost"+averageFinalCost+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
		    	chainResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore

				}
			}
		averageOfChainResults("small");
		averageOfChainResultsAdaptive("small");
		resetChainResults(); 
		
		
	/*	
		
		for(int i=0;i<100;i++) {
			qlearningchain= new QLearningChain();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("meduim "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("meduim "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//ExecuteWorkflow("insurance21",1,"M");
			ExecuteWorkflow("insurance21-2",1,"M");
			//ExecuteWorkflow("insurance21-3",1,"M");

			System.out.println("Number of detected attack for meduim bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End meduim "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("End meduim "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//values.add(chainTime+(numberOfDetectedattacks*adaptationTimeOverhead));
			values.add(chainTime);
			values.add(chainPrice);
			values.add(chainValue);
			values.add(chainScore);
			double cost1=(priceWeight*chainPrice)+(timeWeight*chainTime)-(valueWeight*chainValue)-(securityWeight*chainScore);
			values.add(cost1);
			chainResultslowestcost.put(i,values);
			System.out.println("Lowest cost Time:"+chainTime+(numberOfDetectedattacks*adaptationTimeOverhead)+" "+chainPrice+" "+chainValue);
			System.out.println(" lowestCostChainRecords  "+lowestCostChainRecords.size());
			String state1="";
			for(ChainRecord Action: lowestCostChainRecords) {
				 state1+=Action.ToString();
			}
			addTaskToFile(state1,"LearningAdaptationLogchain.txt");
		    addTaskToFile("for this workflow execution: LowestcostTime "+(chainTime+(numberOfDetectedattacks*adaptationTimeOverhead))+" LowestcostPrice "+chainPrice+" LowestcostValue "+chainValue+"LowestcostScore"+chainScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchain.txt");
		    	
		    	//valuesAdaptive.add(chainTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead));
		    	valuesAdaptive.add(chainTimeAdaptive);
				valuesAdaptive.add(chainPriceAdaptive);
				valuesAdaptive.add(chainValueAdaptive);
				valuesAdaptive.add(chainScoreAdaptive);
				double cost2=(priceWeight*chainPriceAdaptive)+(timeWeight*chainTimeAdaptive)-(valueWeight*chainValueAdaptive)-(securityWeight*chainScoreAdaptive);
				valuesAdaptive.add(cost2);
				chainResultsAdaptive.put(i,valuesAdaptive);
				chainResultsAdaptive100.put(i,valuesAdaptive);
				String state2="";
				for(ChainRecord Action: currentWorkflowState) {
					 state2+=Action.ToString();
				}
				addTaskToFile(state2,"LearningAdaptationLogchainAdaptive.txt");
		    	addTaskToFile("for this workflow execution: RLTime "+(chainTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead))+" RLPrice "+chainPriceAdaptive+" RLValue "+chainValueAdaptive+"chainScore"+chainScoreAdaptive+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
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
				    double totalFinalCost = 0.0,minFS=Double.MAX_VALUE;

				    
				
				    // Calculate the sum of Time, Cost, and MitigationScore
				    for (ArrayList<Double> result : chainResultsAdaptive100.values()) {
				        totalTime += result.get(0);
				        totalCost += result.get(1);
				        totalValue+=result.get(2);
				        totalMitigationScore += result.get(3);
				        totalFinalCost += result.get(4);
				        
				        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
				        if(result.get(0)<minTime && result.get(0)!=0)
				        	minTime=result.get(0);
				        if(result.get(1)<minCost && result.get(1)!=0)
				        	minCost=result.get(1);
				        if(result.get(2)<minValue && result.get(2)!=0)
				        	minValue=result.get(2);
				        if(result.get(3)<minMS && result.get(3)!=0)
				        	minMS=result.get(3);
				        if(result.get(4)<minFS && result.get(4)!=0)
				        	minFS=result.get(4);
				        
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
				    if(minFS==Double.MAX_VALUE)
				    	minFS=1;
				    
				    
				    double averageTime = totalTime / 100;
				    double averageCost = totalCost / 100;
				    double averageValue=totalValue/100;
				    double averageMitigationScore = totalMitigationScore / 100;
				    double averageFinalCost = totalFinalCost / 100;

				
		    	addTaskToFile("for 100s workflow execution: RLTime "+averageTime+" RLPrice "+averageCost+" RLValue "+averageValue+"RLScore"+averageMitigationScore+"RLFinalCost"+averageFinalCost+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
		    	chainResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore

				}
			}
		averageOfChainResults("meduim");
		averageOfChainResultsAdaptive("meduim");
		resetChainResults(); 
*/
		
		
/*		
		for(int i=0;i<100;i++) {
			qlearningchain= new QLearningChain();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("large "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("large "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			ExecuteWorkflow("insurance22",1,"L");
			//ExecuteWorkflow("insurance22-2",1,"L");
			//ExecuteWorkflow("insurance22-3",1,"L");

			System.out.println("Number of detected attack for large bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End large "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("End large "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//values.add(chainTime+(numberOfDetectedattacks*adaptationTimeOverhead));
			values.add(chainTime);
			values.add(chainPrice);
			values.add(chainValue);
			values.add(chainScore);
			double cost1=(priceWeight*chainPrice)+(timeWeight*chainTime)-(valueWeight*chainValue)-(securityWeight*chainScore);
			values.add(cost1);
			chainResultslowestcost.put(i,values);
			System.out.println("Lowest cost Time:"+chainTime+(numberOfDetectedattacks*adaptationTimeOverhead)+" "+chainPrice+" "+chainValue);
			System.out.println(" lowestCostChainRecords  "+lowestCostChainRecords.size());
			String state1="";
			for(ChainRecord Action: lowestCostChainRecords) {
				 state1+=Action.ToString();
			}
			addTaskToFile(state1,"LearningAdaptationLogchain.txt");
		    addTaskToFile("for this workflow execution: LowestcostTime "+(chainTime+(numberOfDetectedattacks*adaptationTimeOverhead))+" LowestcostPrice "+chainPrice+" LowestcostValue "+chainValue+"LowestcostScore"+chainScore+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchain.txt");
		    	
		    	//valuesAdaptive.add(chainTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead));
		    	valuesAdaptive.add(chainTimeAdaptive);
				valuesAdaptive.add(chainPriceAdaptive);
				valuesAdaptive.add(chainValueAdaptive);
				valuesAdaptive.add(chainScoreAdaptive);
				double cost2=(priceWeight*chainPriceAdaptive)+(timeWeight*chainTimeAdaptive)-(valueWeight*chainValueAdaptive)-(securityWeight*chainScoreAdaptive);
				valuesAdaptive.add(cost2);
				chainResultsAdaptive.put(i,valuesAdaptive);
				chainResultsAdaptive100.put(i,valuesAdaptive);
				String state2="";
				for(ChainRecord Action: currentWorkflowState) {
					 state2+=Action.ToString();
				}
				addTaskToFile(state2,"LearningAdaptationLogchainAdaptive.txt");
		    	addTaskToFile("for this workflow execution: RLTime "+(chainTimeAdaptive+(numberOfDetectedattacks*adaptationTimeOverhead))+" RLPrice "+chainPriceAdaptive+" RLValue "+chainValueAdaptive+"chainScore"+chainScoreAdaptive+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
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
				    double totalFinalCost = 0.0,minFS=Double.MAX_VALUE;

				    
				
				    // Calculate the sum of Time, Cost, and MitigationScore
				    for (ArrayList<Double> result : chainResultsAdaptive100.values()) {
				        totalTime += result.get(0);
				        totalCost += result.get(1);
				        totalValue+=result.get(2);
				        totalMitigationScore += result.get(3);
				        totalFinalCost += result.get(4);
				        
				        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
				        if(result.get(0)<minTime && result.get(0)!=0)
				        	minTime=result.get(0);
				        if(result.get(1)<minCost && result.get(1)!=0)
				        	minCost=result.get(1);
				        if(result.get(2)<minValue && result.get(2)!=0)
				        	minValue=result.get(2);
				        if(result.get(3)<minMS && result.get(3)!=0)
				        	minMS=result.get(3);
				        if(result.get(4)<minFS && result.get(4)!=0)
				        	minFS=result.get(4);
				        
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
				    if(minFS==Double.MAX_VALUE)
				    	minFS=1;
				    
				    
				    double averageTime = totalTime / 100;
				    double averageCost = totalCost / 100;
				    double averageValue=totalValue/100;
				    double averageMitigationScore = totalMitigationScore / 100;
				    double averageFinalCost = totalFinalCost / 100;

				
		    	addTaskToFile("for 100s workflow execution: RLTime "+averageTime+" RLPrice "+averageCost+" RLValue "+averageValue+"RLScore"+averageMitigationScore+"RLFinalCost"+averageFinalCost+ "numOFtask "+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
		    	chainResultsAdaptive100 = new HashMap<Integer, ArrayList<Double>>();//Time,Cost,value,MitigationScore

				}
			}
		averageOfChainResults("large");
		averageOfChainResultsAdaptive("large");
		resetChainResults(); 

*/		
		

/*
		k=0;
		for(int i=0;i<1000;i++) {
			qlearning= new QLearning();
			qlearningchain= new QLearningChain();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("meduim "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("meduim "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			//ExecuteWorkflow("insurance21",1,"M");
			ExecuteWorkflow("insurance21-2",1,"M");
			//ExecuteWorkflow("insurance21-3",1,"M");

			System.out.println("Number of detected attack for meduim bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End meduim "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("End meduim "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");


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
	averageOfChainResults("meduim");
	averageOfChainResultsAdaptive("meduim");
	resetChainResults(); 


		
		k=0;
		for(int i=0;i<1000;i++) {
			qlearning= new QLearning();
			qlearningchain= new QLearningChain();
			ArrayList<Double> values=new ArrayList<Double>();
			ArrayList<Double> valuesAdaptive=new ArrayList<Double>();
			s.setTaskNumber(0);
			addTaskToFile("large "+i+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("large "+i+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

			ExecuteWorkflow("insurance22",1,"L");
			//ExecuteWorkflow("insurance22-2",1,"L");
			//ExecuteWorkflow("insurance22-3",1,"L");

			System.out.println("Number of detected attack for large bpmn: "+numberOfDetectedattacks);
			addTaskToFile("End large "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopis.txt");
			addTaskToFile("End large "+i+" numberOfDetectedattacks:"+ numberOfDetectedattacks+" ***************************************************************","LearningAdaptationLogCoopisAdaptive.txt");

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
		averageOfChainResults("large");
		averageOfChainResultsAdaptive("large");
		resetChainResults(); 
		
		

	*/	
		
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
	
	public int getCurrentExecutionNumber() {
		return currentExecutionNumber;
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
	
	public void averageOfChainResults(String size)  throws IOException{
	    int totalEntries = chainResultslowestcost.size();
	    double totalTime = 0.0,minTime=Double.MAX_VALUE;;
	    double totalCost = 0.0,minCost=Double.MAX_VALUE;;
	    double totalValue=0.0,minValue=Double.MAX_VALUE;;
	    double totalMitigationScore = 0.0,minMS=Double.MAX_VALUE;
	    double totalFinalCost = 0.0,minFS=Double.MAX_VALUE;

	    
	
	    // Calculate the sum of Time, Cost, and MitigationScore
	    for (ArrayList<Double> result : chainResultslowestcost.values()) {
	        totalTime += result.get(0);
	        totalCost += result.get(1);
	        totalValue+=result.get(2);
	        totalMitigationScore += result.get(3);
	        totalFinalCost += result.get(4);

	        
	        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
	        if(result.get(0)<minTime && result.get(0)!=0)
	        	minTime=result.get(0);
	        if(result.get(1)<minCost && result.get(1)!=0)
	        	minCost=result.get(1);
	        if(result.get(2)<minValue && result.get(2)!=0)
	        	minValue=result.get(2);
	        if(result.get(3)<minMS && result.get(3)!=0)
	        	minMS=result.get(3);
	        if(result.get(4)<minFS && result.get(4)!=0)
	        	minFS=result.get(4);
	        
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
	    if(minFS==Double.MAX_VALUE)
	    	minFS=1;
	    
	    
	    double averageTime = totalTime / totalEntries;
	    double averageCost = totalCost / totalEntries;
	    double averageValue=totalValue/totalEntries;
	    double averageMitigationScore = totalMitigationScore / totalEntries;
	    double averageFinalCost = totalFinalCost / totalEntries;

		addTaskToFile("Final: averageNormalizedTime "+(averageTime/minTime)+" averageNormalizedCost "+(averageCost/minCost)+" averageNormalizedValue "+(averageValue/minValue)+" averageNormalizedMitigationScore "+(averageMitigationScore/minMS)+" averageNormalizedFinalCost "+(averageFinalCost/minFS)+" ***************************************************************","LearningAdaptationLogchain.txt");
		addTaskToFile("Final: averageNormalizedTime "+averageTime+" averageNormalizedCost "+averageCost+" averageNormalizedValue "+averageValue+" averageNormalizedMitigationScore "+averageMitigationScore+" averageNormalizedFinalCost "+averageFinalCost+" numOFTasks"+completeVersion+" ***************************************************************","LearningAdaptationLogchain.txt");

	}
	
	
	public void averageOfChainResultsAdaptive(String size)  throws IOException{
	    int totalEntries = chainResultsAdaptive .size();
	    double totalTime = 0.0,minTime=Double.MAX_VALUE;;
	    double totalCost = 0.0,minCost=Double.MAX_VALUE;;
	    double totalValue=0.0,minValue=Double.MAX_VALUE;;
	    double totalMitigationScore = 0.0,minMS=Double.MAX_VALUE;
	    double totalFinalCost = 0.0,minFS=Double.MAX_VALUE;

	
	    // Calculate the sum of Time, Cost, and MitigationScore
	    for (ArrayList<Double> result : chainResultsAdaptive.values()) {
	        totalTime += result.get(0);
	        totalCost += result.get(1);
	        totalValue+=result.get(2);
	        totalMitigationScore += result.get(3);
	        totalFinalCost += result.get(4);

	        
	        System.out.println("total "+totalTime+" "+totalCost+" "+totalMitigationScore);
	        if(result.get(0)<minTime && result.get(0)!=0)
	        	minTime=result.get(0);
	        if(result.get(1)<minCost && result.get(1)!=0)
	        	minCost=result.get(1);
	        if(result.get(2)<minValue && result.get(2)!=0)
	        	minValue=result.get(2);
	        if(result.get(3)<minMS && result.get(3)!=0)
	        	minMS=result.get(3);
	        if(result.get(4)<minFS && result.get(4)!=0)
	        	minFS=result.get(4);
	        
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
	    if(minFS==Double.MAX_VALUE)
	    	minFS=1;
	    
	    
	    double averageTime = totalTime / totalEntries;
	    double averageCost = totalCost / totalEntries;
	    double averageValue=totalValue/totalEntries;
	    double averageMitigationScore = totalMitigationScore / totalEntries;
	    double averageFinalCost = totalFinalCost / totalEntries;

		addTaskToFile("Final: averageNormalizedTime "+(averageTime/minTime)+" averageNormalizedCost "+(averageCost/minCost)+" averageNormalizedValue "+(averageValue/minValue)+" averageNormalizedMitigationScore "+(averageMitigationScore/minMS)+" averageNormalizedFinalCost "+(averageFinalCost/minFS)+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
		addTaskToFile("Final: averageNormalizedTime "+averageTime+" averageNormalizedCost "+averageCost+" averageNormalizedValue "+averageValue+" averageNormalizedMitigationScore "+averageMitigationScore+" averageNormalizedFinalCost "+averageFinalCost+" numOFTasks"+completeVersion+" ***************************************************************","LearningAdaptationLogchainAdaptive.txt");
		
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
		upcomingAdaptations=new ArrayList<Adaptation>();
		ActionRecords = new ArrayList<ActionRecord>();
		
		
		if(coopisTimeAdaptive>coopisTimeMaxAdaptive)
			coopisTimeMaxAdaptive=coopisTimeAdaptive;
		if(coopisPriceAdaptive>coopisPriceMaxAdaptive)
			coopisPriceMaxAdaptive=coopisPriceAdaptive;
		if(coopisValueAdaptive>coopisValueMaxAdaptive)
			coopisValueMaxAdaptive=coopisValueAdaptive;
		if(coopisScoreAdaptive>coopisScoreMaxAdaptive)
			coopisScoreMaxAdaptive=coopisScoreAdaptive;
		chainTimeAdaptive=0;
		coopisPriceAdaptive=0;
		coopisValueAdaptive=0;
		coopisScoreAdaptive=0;
		completeVersionAdaptive=0;
		upcomingAdaptationsAdaptive=new ArrayList<Adaptation>();
		ActionRecordsAdaptive = new ArrayList<ActionRecord>();
		//******
		if(chainTime>chainTimeMax)
			chainTimeMax=chainTime;
		if(chainPrice>chainPriceMax)
			chainPriceMax=chainPrice;
		if(chainValue>chainValueMax)
			chainValueMax=chainValue;
		if(chainScore>chainScoreMax)
			chainScoreMax=chainScore;
		if(chainFinalCost>chainFinalCostMax)
			chainFinalCostMax=chainFinalCost;
		
		chainTime=0;
		chainPrice=0;
		chainValue=0;
		chainScore=0;
		chainFinalCost=0;
		completeVersion=0;
		//upcomingAdaptations=new ArrayList<Adaptation>();
		lowestCostChainRecords = new ArrayList<ChainRecord>();
		
		
		if(chainTimeAdaptive>chainTimeMaxAdaptive)
			chainTimeMaxAdaptive=chainTimeAdaptive;
		if(chainPriceAdaptive>chainPriceMaxAdaptive)
			chainPriceMaxAdaptive=chainPriceAdaptive;
		if(chainValueAdaptive>chainValueMaxAdaptive)
			chainValueMaxAdaptive=chainValueAdaptive;
		if(chainScoreAdaptive>chainScoreMaxAdaptive)
			chainScoreMaxAdaptive=chainScoreAdaptive;
		if(chainFinalCostAdaptive>chainFinalCostMaxAdaptive)
			chainFinalCostMaxAdaptive=chainFinalCostAdaptive;
		chainTimeAdaptive=0;
		chainPriceAdaptive=0;
		chainValueAdaptive=0;
		chainScoreAdaptive=0;
		chainFinalCostAdaptive=0;
		completeVersionAdaptive=0;
		//upcomingAdaptationsAdaptive=new ArrayList<Adaptation>();
		currentWorkflowState = new ArrayList<ChainRecord>();
		
		

	}
	
	public void resetEdocResults() {
		 EdocResults = new HashMap<Integer, ArrayList<Double>>();
		edocTimeMax=0;
		edoPriceMax=0;
		edocScoreMax=0;
	}
	
	public void resetChainResults() {
		 chainResultslowestcost = new HashMap<Integer, ArrayList<Double>>();
		chainTimeMax=0;
		chainPriceMax=0;
		chainScoreMax=0;
		chainFinalCostMax=0;
		completeVersion=0;
		upcomingAdaptations=new ArrayList<Adaptation>();
		
		 chainResultsAdaptive  = new HashMap<Integer, ArrayList<Double>>();
			chainTimeMaxAdaptive=0;
			chainPriceMaxAdaptive=0;
			chainScoreMaxAdaptive=0;
			chainFinalCostMaxAdaptive=0;
			completeVersionAdaptive=0;
			upcomingAdaptationsAdaptive=new ArrayList<Adaptation>();
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
		Map<String, ArrayList<String>> DataFlowClosureSet = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> ControlFlowClosureSet = new HashMap<String, ArrayList<String>>();
		ArrayList<String> serviceTasksOrder=new ArrayList<String>();
		Map<String, ArrayList<String>> Successors = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> Predecessors = new HashMap<String, ArrayList<String>>();

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
						serviceTasksOrder.add(serviceName);
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
			if(line.contains("DataFlowClosureSet")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						String serviceName=arr2[0];
						//System.out.println("ElenaN "+serviceName);	
						ArrayList<String> DataFlow = new ArrayList<String>();
						for(int j=1;j<arr2.length;j++) {
							DataFlow.add(arr2[j]);
							//System.out.println("ElenaH "+arr2[j]);	
						}
						DataFlowClosureSet.put(serviceName,DataFlow);
					}
				}
			}//end if DataFlowClosureSet
			if(line.contains("ControlFlowClosureSet")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						String serviceName=arr2[0];
						//System.out.println("ElenaN "+serviceName);	
						ArrayList<String> ControlFlow = new ArrayList<String>();
						for(int j=1;j<arr2.length;j++) {
							ControlFlow.add(arr2[j]);
							//System.out.println("ElenaH "+arr2[j]);	
						}
						ControlFlowClosureSet.put(serviceName,ControlFlow);
					}
				}
			}//end if ControlFlowClosureSet
			
			if(line.contains("Successors")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						String serviceName=arr2[0];
						//System.out.println("ElenaN "+serviceName);	
						ArrayList<String> successor = new ArrayList<String>();
						for(int j=1;j<arr2.length;j++) {
							successor.add(arr2[j]);
							//System.out.println("ElenaH "+arr2[j]);	
						}
						Successors.put(serviceName,successor);
					}
				}
			}//end if Successors
			
			if(line.contains("Predecessors")) {
				String[] arr1  = line.split(";");
				int i=0;
				for (String obj : arr1) {
					//System.out.println(obj);
					if(i==0) 
						i=1;
					else {
						String[] arr2  = obj.split(delimsComma);
						String serviceName=arr2[0];
						//System.out.println("ElenaN "+serviceName);	
						ArrayList<String> predecessor = new ArrayList<String>();
						for(int j=1;j<arr2.length;j++) {
							predecessor.add(arr2[j]);
							//System.out.println("ElenaH "+arr2[j]);	
						}
						Predecessors.put(serviceName,predecessor);
					}
				}
			}//end if Predecessors
			
			
			
		}//for

		
		
		
		
		Workflow w=new Workflow(bpmn,userTasks,serviceTasksOrder,serviceTasks,this.users,this,this.KM,serviceTasksLegalAdaptation,serviceTasksLegalAdaptationCost,serviceCost,userCost,parallelTasksList,DataFlowClosureSet,ControlFlowClosureSet,Successors,Predecessors,this.priceWeight,this.timeWeight,this.valueWeight,this.securityWeight);
		this.KM.getMultiCloudEnvironment().setWorkflow(w);
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
				//localAdaptationCoopisPredictive(s,payload,attackScore,workflow,taskNumber);
				//localChainAdaptationPredictive(s,payload,attackScore,workflow,taskNumber);
				localChainAdaptationPredictive2(s,payload,attackScore,workflow,taskNumber);


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
			
			//if it is part of upcomingAdaptationsAdaptive(RL strategy) in previous tasks
			boolean plannedAdaptationAdaptive=false;
			Adaptation currentAdaptationAdaptive=null;
			for(Adaptation aa:upcomingAdaptationsAdaptive) {
				if(aa.getTask().equals(s.getTaskId())) {
					plannedAdaptationAdaptive=true;
					currentAdaptationAdaptive=aa;
				}
			}
			//if it's not part of the previous adaptation chains
			if(plannedAdaptationAdaptive) {
				System.out.println("it's a planned adaptation (part of the previous adaptation chains (RL strategy))");
				
				chainTimeAdaptive+=currentAdaptationAdaptive.getTime();
				chainPriceAdaptive+=currentAdaptationAdaptive.getPrice();
				chainValueAdaptive+=currentAdaptationAdaptive.getValue();
				chainScoreAdaptive+=currentAdaptationAdaptive.getMitigationScore();
				currentAdaptationAdaptive.print();
				
			}
			else {
				System.out.println("it's not a planned adaptation");
				ArrayList<Double> costs=workflow.getCostsforServiceTask(s.getTaskId());
				double cost=0,price=s.getService(). getCost(),time=s.getService(). getART(),value=costs.get(2);			
				chainTimeAdaptive+=time;
				chainPriceAdaptive+=price;
				chainScoreAdaptive+=value;
				//addTaskToFile(s.getTaskId()+"\t"+"normal"+"\t"+0+"\t"+"nothing"+"\t"+0+"\t"+time+"\t"+price+"\t"+value+"\t"+0,"LearningAdaptationLochains.txt");//add this adaptation to file 
				//addTaskToFile(s.getTaskId()+"\t"+"normal"+"\t"+0+"\t"+"nothing"+"\t"+0+"\t"+time+"\t"+price+"\t"+value+"\t"+0,"LearningAdaptationLogCoopisAdaptive.txt");//add this adaptation to file 
			}
			
			
			//if it is part of upcomingAdaptationsAdaptive(Lowest cost strategy) in previous tasks
			boolean plannedAdaptation=false;
			Adaptation currentAdaptation=null;
			for(Adaptation aa:upcomingAdaptations) {
				if(aa.getTask().equals(s.getTaskId())) {
					plannedAdaptation=true;
					currentAdaptation=aa;
				}
				
			}
			//if it's not part of the previous adaptation chains
			if(plannedAdaptation) {
				System.out.println("it's a planned adaptation (part of the previous adaptation chains (Lowest cost strategy))");
				chainTime+=currentAdaptation.getTime();
				chainPrice+=currentAdaptation.getPrice();
				chainValue+=currentAdaptation.getValue();
				chainScore+=currentAdaptation.getMitigationScore();
				currentAdaptation.print();

				
			}
			else {
				System.out.println("it's not a planned adaptation");
				ArrayList<Double> costs=workflow.getCostsforServiceTask(s.getTaskId());
				double cost=0,price=s.getService(). getCost(),time=s.getService(). getART(),value=costs.get(2);
				chainTime+=time;
				chainPrice+=price;
				chainValue+=value;
				
				//addTaskToFile(s.getTaskId()+"\t"+"normal"+"\t"+0+"\t"+"nothing"+"\t"+0+"\t"+time+"\t"+price+"\t"+value+"\t"+0,"LearningAdaptationLogCoopis.txt");//add this adaptation to file 
				//addTaskToFile(s.getTaskId()+"\t"+"normal"+"\t"+0+"\t"+"nothing"+"\t"+0+"\t"+time+"\t"+price+"\t"+value+"\t"+0,"LearningAdaptationLogCoopisAdaptive.txt");//add this adaptation to file 
			}
			
			
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
	
	
	
	public ArrayList<ArrayList<Adaptation>> ComputeAdaptationSecurityChainSet(ServiceTask s,Workflow workflow,ArrayList<String> attackAdaptations){
		System.out.println("ComputeAdaptationSecurityChainSet");
		 ArrayList<ArrayList<Adaptation>>  AdaptationSecurityChainSet=new ArrayList<>();
		Map<String, ArrayList<String>> AdaptationChains=new HashMap<String, ArrayList<String>>();
		
		int indexOfS=0;
		//System.out.println("here taskID "+s.getTaskId());
		 System.out.println("suitable adaptations for the detected attack:");
		for (String adaptation : attackAdaptations) {
            System.out.println("	-"+adaptation);
        }
		
		
		for(int i=0;i<workflow.getServiceTasks().size();i++) {
			if(s.getTaskId().equals(workflow.getServiceTasks().get(i)))
				indexOfS=i;
		}
		//System.out.println("indexOfS: "+indexOfS);
		//add the current task's adaptations
		//AdaptationChains.put(s.getTaskId(),workflow.getLegalAdaptationforServiceTask(s.getTaskId()));
		ArrayList<String> LegalAdaptationforTask_Attack0 = new ArrayList<String>();
		for(String a:workflow.getLegalAdaptationforServiceTask(s.getTaskId())) {
			if(attackAdaptations.contains(a))
				LegalAdaptationforTask_Attack0.add(a);
		}
		AdaptationChains.put(s.getTaskId(),LegalAdaptationforTask_Attack0);
		
		
		
		//add the security dependent tasks' adaptations
		for(int j=0;j<workflow.getServiceTasks().size();j++) {
			String task=workflow.getServiceTasks().get(j);
			//System.out.println("ServiceTask: "+task+"  "+j);
			if(workflow.CheckSecurityDependency(indexOfS,j)) {
				ArrayList<String> LegalAdaptationforTask_Attack = new ArrayList<String>();
				for(String a:workflow.getLegalAdaptationforServiceTask(task)) {
					if(attackAdaptations.contains(a))
						LegalAdaptationforTask_Attack.add(a);
				}
				AdaptationChains.put(task,LegalAdaptationforTask_Attack);
				System.out.println("SecurityDependency["+indexOfS+"]["+j+"]=true");
			}
			else
				System.out.println("SecurityDependency["+indexOfS+"]["+j+"]=false");
		}
		
		// Remove the entry if the list of actions is empty
		 Iterator<Map.Entry<String, ArrayList<String>>> iterator = AdaptationChains.entrySet().iterator();

	        while (iterator.hasNext()) {
	            Map.Entry<String, ArrayList<String>> entry = iterator.next();
	            String task = entry.getKey();
	            ArrayList<String> actions = entry.getValue();

	            System.out.print(task + "-> ");

	            if (actions.isEmpty()) {
	                // Remove the entry if the list of actions is empty
	                iterator.remove();
	                System.out.println("Removed");
	            } else {
	                // Print actions
	                for (String action : actions) {
	                    System.out.print(action + ", ");
	                }
	                System.out.println();
	            }
	        }
		
            System.out.println("remove tasks with empty actions*************************************");

			 for (Map.Entry<String, ArrayList<String>> entry : AdaptationChains.entrySet()) {
		            String task = entry.getKey();
		            ArrayList<String> actions = entry.getValue();

		            System.out.print(task + "-> ");

		            for (String action : actions) {
		                System.out.print(action + ", ");
		            }
		            System.out.println();
		        }
			 
		
		
		
		 //AdaptationSecurityChainSet=GenerateCombinations(AdaptationChains);
		 AdaptationSecurityChainSet=FinalGenerateCombinations(AdaptationChains);

		return AdaptationSecurityChainSet;
	}
	
	public  boolean hasCommonElements(ArrayList<String> list1,ArrayList<String> list2) {
		boolean hasCommon = false;
	    for (String element : list1) {
	        if (list2.contains(element)) {
	            return true;
	        }
	    }
	    return false;
    }
	
	public ArrayList<ArrayList<Adaptation>> ComputeAdaptationSecurityChainLoops(ServiceTask s,Workflow workflow,ArrayList<ArrayList<Adaptation>> AdaptationChains){
		System.out.println("ComputeAdaptationSecurityChainLoops");
		ArrayList<ArrayList<Adaptation>> AdaptationChainsLoops=new ArrayList<>();
		for(ArrayList<Adaptation> ac: AdaptationChains) {
			//System.out.println("ac: ");
			ArrayList<Adaptation> newac=ac;
			ArrayList<String> adaptTasks=new ArrayList<String>();
			ArrayList<String> SPs=new ArrayList<String>();
			for(Adaptation adapt:ac) {
				adaptTasks.add(adapt.getTask());
				//System.out.println("	task: "+adapt.getTask());		
			}
			for(String task: adaptTasks) {
				if(workflow.getPredecessors(task)!=null && !hasCommonElements(workflow.getPredecessors(task),adaptTasks)) {
					SPs.add(task);
					//System.out.println("	starting point: "+task);		
				}
			}
			for(String sp:SPs) {
				for(int j=0;j<workflow.getServiceTasks().size();j++) {
					String task=workflow.getServiceTasks().get(j);
					//if(workflow.getPredecessors(s.getTaskId()).contains(task))
						//System.out.println("	add unintentional adaptation1 ");
					//if(workflow.getSuccessors(sp).contains(task))
						//System.out.println("	add unintentional adaptation2 ");
					//if(!adaptTasks.contains(task)) 
						//System.out.println("	add unintentional adaptation3 ");

					if(workflow.getPredecessors(s.getTaskId()).contains(task) && workflow.getSuccessors(sp).contains(task) && !adaptTasks.contains(task)) {
						System.out.println("In Adaptation Chain: ");
						for(Adaptation adapt:ac) {
							System.out.println("	task: "+adapt.getTask());		
						}
						System.out.println("add unintentional adaptation: ");
						Adaptation newadapt=new Adaptation("ReExecute",task);
						newadapt.smallPrint();
						newac.add(newadapt);
					}
				}
			}
			AdaptationChainsLoops.add(newac);
			
		}
		return AdaptationChainsLoops;
	}
	
	
	
	public  ArrayList<ArrayList<Adaptation>> FinalGenerateCombinations(Map<String, ArrayList<String>> AdaptationChains){
		System.out.println("GenerateCombinations");		
		ArrayList<ArrayList<Adaptation>> AdaptationSecurityChainSet = new ArrayList<>();
        ArrayList<String> traces = new ArrayList<>();
        
		int sizeOfCombination=AdaptationChains.size();
		
		System.out.println("sizeOfCombination: "+sizeOfCombination);
		
		 for (Map.Entry<String, ArrayList<String>> entry : AdaptationChains.entrySet()) {
	            String task = entry.getKey();
	            ArrayList<String> actions = entry.getValue();

	            if (!traces.contains(task)) {
	                traces.add(task);
	                //System.out.println("task: " + task);
	                for (String action : actions) {
	                    //System.out.println("    action: " + action);
	                    ArrayList<Adaptation> currentChain = new ArrayList<>();
	                    Adaptation a=new Adaptation(action, task);
	                    generateCombinationsRecursive(a,AdaptationSecurityChainSet);
	                 }
	            }
        }
		/*
		 int i=0;
		 for (ArrayList<Adaptation> innerList : AdaptationSecurityChainSet) {
			 System.out.println("chain "+i);
			 i++;
	            for (Adaptation adaptation : innerList) {
	            	adaptation.smallPrint();
	            }
	        }
	     */
		 return AdaptationSecurityChainSet;
	}
	
	
	private void generateCombinationsRecursive(Adaptation a,ArrayList<ArrayList<Adaptation>> AdaptationSecurityChainSet) {
		//System.out.println("step");
		//System.out.println("AdaptationSecurityChainSet "+AdaptationSecurityChainSet.size());
        ArrayList<ArrayList<Adaptation>> newChains = new ArrayList<>();
        
        //we need to add three sets {(all previous +new),(new),(all previous)}
      //all previous +new
		for(ArrayList<Adaptation> chain: AdaptationSecurityChainSet) {
			boolean check=false;
			ArrayList<Adaptation> previousChain=new ArrayList<Adaptation>();
			for(Adaptation adapt:chain) {
				previousChain.add(adapt);
				if(a.getTask().equals(adapt.getTask()))
					check=true;
			}
			if(check==false) {
				newChains.add(previousChain);
				chain.add(a);
			}
		}
		
		//new
		ArrayList<Adaptation> currentChain=new ArrayList<Adaptation>();
		currentChain.add(a);
		AdaptationSecurityChainSet.add(currentChain);
		
		//all previous 
		for(ArrayList<Adaptation> chain: newChains) {
			AdaptationSecurityChainSet.add(chain);
		}
 
    }
	
	
	
	public  ArrayList<ArrayList<Adaptation>> generateCombinationsFixedSize2(Map<String, ArrayList<String>> AdaptationChains){
		System.out.println("generateCombinationsFixedSize2");
		ArrayList<ArrayList<Adaptation>>  AdaptationSecurityChainSet=new ArrayList<>();		
    	ArrayList<String> traces = new ArrayList<String>();

		 for (Map.Entry<String, ArrayList<String>> entry1 : AdaptationChains.entrySet()) {
	            String task1 = entry1.getKey();
	            traces.add(task1);
	            ArrayList<String> actions1 = entry1.getValue();

	            System.out.println("task1: "+task1);

	            for (String action1 : actions1) {
	                System.out.println("	action1: "+action1);
	                ArrayList<Adaptation> ac1 = new ArrayList<Adaptation>();
	            	Adaptation a1=new Adaptation(action1,task1);
	            	ac1.add(a1);
	            	AdaptationSecurityChainSet.add(ac1);
	                for (Map.Entry<String, ArrayList<String>> entry2 : AdaptationChains.entrySet()) {
	                	String task2 = entry2.getKey();
	    	            ArrayList<String> actions2 = entry2.getValue();
	    	            //if(!task1.equals(task2)) {
	    	            if(!traces.contains(task2)) {
	    	            System.out.println("		task2: "+task2);
	    	            for (String action2 : actions2) {
	    	            	ArrayList<Adaptation> ac2 = new ArrayList<Adaptation>();
	    	            	ArrayList<Adaptation> ac3 = new ArrayList<Adaptation>();
	    	                System.out.println("			action2: "+action2);    	                	
    	                	Adaptation a2=new Adaptation(action2,task2);
    	                	ac2.add(a1);
    	                	ac2.add(a2);
    	                	ac3.add(a2);
	    	            	AdaptationSecurityChainSet.add(ac2);
	    	            	AdaptationSecurityChainSet.add(ac3);
    	                }
    	             }
	             }
	                
	        }
	        System.out.println();
	     }
		 
		return AdaptationSecurityChainSet;
	}

	
	
	
	
	
	//Chain Adaptation with precice qlearning
		public void localChainAdaptationPredictive2(ServiceTask s,String payload,double attackScore,Workflow workflow,int taskNumber) throws IOException{
			System.out.println("localAdaptation Chain Adaptation2");
			Attack attack=null;
			for(Attack a:AttackTypes) {
				//System.out.println(attack.getName()+" "+payload);
				if(payload.contains(a.getName()))
					attack=a;
			}
			
			System.out.println("$For serviceTask "+s.getTaskId()+" in instance "+s.getInstanceId()+" from ProcessId "+s.getProcessId()+" from workflow "+workflow.getName()+"  attack:"+attack.getName()+" with attackScore "+attackScore+"("+getSeverity(attackScore)+") will be adapted");
			
			ArrayList<String> attackAdaptations=attack.getAttackAdaptationPattern(getSeverity(attackScore));
			//compute Adaptation Security Chain Set
			ArrayList<ArrayList<Adaptation>> AdaptationChains=ComputeAdaptationSecurityChainSet(s,workflow,attackAdaptations);
			ArrayList<ArrayList<Adaptation>> AdaptationChainsLoops=ComputeAdaptationSecurityChainLoops(s,workflow,AdaptationChains);

			
			ArrayList<AdaptationChain> AdaptationChainSet=new ArrayList<AdaptationChain>();
			int i=0;
			for(ArrayList<Adaptation> ac: AdaptationChainsLoops) {
				AdaptationChainSet.add(new AdaptationChain(i,s.getTaskId(),ac));
				i++;
			}
			
			//for(AdaptationChain chain: AdaptationChainSet) 
			//	chain.print();
			
			//compute cost of each chain in Adaptation Security Chain Set
			System.out.println("******** Chains after compute all parameters");
			for(AdaptationChain chain: AdaptationChainSet) {
				//System.out.println("			-Calculate cost of "+chain.getID());
				for(Adaptation currentAdaptation: chain.getAdaptations()) {
					predictCostOfAdaptation2(s,currentAdaptation.getTask(),currentAdaptation,workflow,attack);
				}
				chain.ComputeCost(workflow,priceWeight,timeWeight,valueWeight,securityWeight);
				chain.ComputeCostSofar(workflow,priceWeight,timeWeight,valueWeight,securityWeight);
				chain.ComputeCostForEntireWorkflow(workflow,priceWeight,timeWeight,valueWeight,securityWeight);
				chain.print();
			}
			
			

			//adaptation chain calculation
			ChainRecord random=new ChainRecord(s.getTaskId(),attack.getName(),getSeverity(attackScore),attackScore);
			ChainRecord lowestCost=new ChainRecord(s.getTaskId(),attack.getName(),getSeverity(attackScore),attackScore);
			ChainRecord currentTaskState=new ChainRecord(s.getTaskId(),attack.getName(),getSeverity(attackScore),attackScore);
			
			
			//select random adaptation chain for creating logfile for ML
			AdaptationChain randomAdaptationChain=selectRandomAdaptationChainStrategy(AdaptationChainSet,s,workflow);
			System.out.println("******Random Adaptation Chain******");
			//randomAdaptationChain.print();
			random.setAdaptationChain(randomAdaptationChain);
			StateChain currentStateRandom=new StateChain(random,randomChainRecords);
			ArrayList<Double> randomParameters=ComputeFinalParameters(currentStateRandom,randomAdaptationChain,workflow);
			chainTimeRandom+=randomParameters.get(0);
			chainPriceRandom+=randomParameters.get(1);
			chainValueRandom+=randomParameters.get(2);
			chainScoreRandom+=randomParameters.get(3);
			chainFinalCostRandom+=randomParameters.get(4);

			//add for the nex rand
			randomChainRecords.add(random);
			random.print(" Random Adaptation Chain ");
			


			
			
			//select best adaptation chain based on lowest cost strategy
			AdaptationChain selectedAdaptationChain=selectBestAdaptationChainStrategy(AdaptationChainSet,s,workflow);
			System.out.println("******Lowest cost Adaptation Chain******");
			//selectedAdaptationChain.print();
			lowestCost.setAdaptationChain(selectedAdaptationChain);
			StateChain currentStateLowestCost=new StateChain(lowestCost,lowestCostChainRecords);
			ArrayList<Double> lowestCostParameters=ComputeFinalParameters(currentStateLowestCost,selectedAdaptationChain,workflow);
			chainTime+=lowestCostParameters.get(0);
			chainPrice+=lowestCostParameters.get(1);
			chainValue+=lowestCostParameters.get(2);
			chainScore+=lowestCostParameters.get(3);
			chainFinalCost+=lowestCostParameters.get(4);
			//add for the nex rand
			lowestCostChainRecords.add(lowestCost);
			lowestCost.print(" Lowest cost Adaptation Chain ");
			
			//select best adaptation chain based on RL strategy
			System.out.println("******Start Selecting Best Adaptation Chain basd on RL strategy******");	
			//1-check the state 
			StateChain currentState=null;
			//currentTaskState: current state of the task(s.getTaskId(),attack.getName(),getSeverity(attackScore),attackScore)
			//currentWorkflowState: current state of the workflow		
			for(StateChain st:states) {
				if(st.isSameState(currentTaskState,currentWorkflowState))
					currentState=st;
			}
			
			if(currentState==null) {
				currentState=new StateChain(currentTaskState,currentWorkflowState);
				this.states.add(currentState);
			}
			//2-upgate the qtable
			AdaptationChain finalSelectedAdaptationChain=qlearningchain.learnChain(currentState,states,qTable,AdaptationChainSet,currentTaskState,randomAdaptationChain,currentWorkflowState,workflow,priceWeight,timeWeight,valueWeight,securityWeight);
			
			System.out.println("******Best Adaptation Chain(RL)******");
			//finalSelectedAdaptationChain.print();
			currentTaskState.setAdaptationChain(finalSelectedAdaptationChain);
			ArrayList<Double> RLParameters=ComputeFinalParameters(currentState,finalSelectedAdaptationChain,workflow);
			chainTimeAdaptive+=RLParameters.get(0);
			chainPriceAdaptive+=RLParameters.get(1);
			chainValueAdaptive+=RLParameters.get(2);
			chainScoreAdaptive+=RLParameters.get(3);
			chainFinalCostAdaptive+=RLParameters.get(4);

			//add for the nex rand
			currentWorkflowState.add(currentTaskState);
			currentTaskState.print(" Best Adaptation Chain(RL) ");
			numberOfDetectedattacks++;
		}
		
		public  List<String> findCommonElements(ArrayList<String> list1, ArrayList<String> list2) {
			//System.out.println("findCommonElements is started!");
			
			/*
			System.out.println("List 1:");
			 for (String element : list1) {
		            System.out.println(element);
		        }
		        System.out.println("\nList 2:");
		        for (String element : list2) {
		            System.out.println(element);
		        }
		      */
			
	        // Use a HashSet to efficiently store unique elements of list1
	        Set<String> set = new HashSet<>(list1);

	        // Create a list to store common elements
	        List<String> commonElements = new ArrayList<>();

	        // Iterate through list2 and check if each element exists in the set
	        for (String element : list2) {
	            if (set.contains(element)) {
	                commonElements.add(element);
	        		//System.out.println("	-commonElements "+element);

	            }
	        }
			 //System.out.println("findCommonElements is completed!");


	        return commonElements;
	    }
		
		public Adaptation getLastHistoryAdaptation(ArrayList<ChainRecord> ChainRecords,String task) {
			Adaptation aa=null;
			for (int i = ChainRecords.size() - 1; i >= 0; i--) {
	            ChainRecord record = ChainRecords.get(i);
	            for(Adaptation aah:record.getSelectedChain().getAdaptations()) {
	            	if(aah.getTask().equals(task)) {
	            		aa= aah;
	            		break;
	            	}
	            }
	        }
			return aa;
		}
		
		
		public Adaptation isAdaptedtaskBasedOnCurrentChain(String task, AdaptationChain adaptationChain) {
			for(Adaptation aa: adaptationChain.getAdaptations()) {
	        	if(task.equals(aa.getTask())) {
	        		return aa;
	        	}
	        }
			//for the tasks that are not invoulved in the adaptation consier a fix default time, price and value(mitigation score for these tasks is 0)
			return null;
		}
		
		public ArrayList<Double> ComputeFinalParameters(StateChain currentState, AdaptationChain adaptationChain,Workflow w) {
			//System.out.println("@@@@@Compute Reward Sofar@@@@@");
		    ArrayList<String> coveredParallelTasks=new ArrayList<String>();
		    
		    String violatedTask=currentState.getCurrentChain().getTaskName();
		    String previousViolatedTask=currentState.getPreviousViolatedTask();
			//System.out.println("	-violatedTask "+violatedTask);
			//System.out.println("	-previousViolatedTask "+previousViolatedTask); 
		    
		    
			//System.out.println("@Compute the cost of completing workflow until the violated task (part one algorithm5)");   
		    //compute the cost of completing workflow until the violated task (part one algorithm5)
			List<String> commonElements;
		    if(previousViolatedTask==null)
		    	commonElements = findCommonElements(w.getServiceTasks(), w.getPredecessors(violatedTask));
		    else
		    	commonElements = findCommonElements(w.getSuccessors(previousViolatedTask), w.getPredecessors(violatedTask));
		    
		    double p=0,t=0,v=0,ms=0;
		    if(commonElements.size()==0)
	    		//System.out.println("	-There is no pending adaptations until the current violated task ");   
		    for(String commonTask: commonElements) {
	    		//System.out.println("commonTask: "+commonTask);   
		    	Adaptation aaHistory=currentState.getLastHistoryAdaptation(commonTask);
		    	if(aaHistory!=null) {
		    		//System.out.println("	-aaHistory is not null for the task "+commonTask+" so the cost of this adaptation will be add");   
		    		//aaHistory.smallPrint(); 
		    		
		    		p+=aaHistory.getPrice();
					v+=aaHistory.getValue();
					ms+=aaHistory.getMitigationScore();
					if(w.getParallelTasks(aaHistory.getTask()).size()!=0) {
						if(!coveredParallelTasks.contains(aaHistory.getTask())) {
							//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
							t=+adaptationChain.MAXDefaulttimeForParallelTasks(aaHistory.getTask(),w.getParallelTasks(aaHistory.getTask()),w);
							coveredParallelTasks.add(commonTask);
							coveredParallelTasks.addAll(w.getParallelTasks(aaHistory.getTask()));
						}
					}
					else {
						//System.out.println("	-this task is not parallel with any task!!:"+aa.getTime());
						t+=aaHistory.getTime();
						}  		
		    	}
		    	else {
		    		//System.out.println("	-aaHistory is null for the task "+commonTask+" so the default cost will be add");   
					//System.out.println("	-this task is not involved in adaptation chain!!:");
					p+=w.getDefaultPrice(commonTask);
					v+=w.getDefaultValue(commonTask);
					if(w.getParallelTasks(commonTask).size()!=0) {
						if(!coveredParallelTasks.contains(commonTask)) {
							//System.out.println("	-this task is parallel with some tasks!!  max: "+MAXDefaulttimeForParallelTasks(task,w.getParallelTasks(task),w)+" task: "+w.getDefaultTime(task));
							t+=adaptationChain.MAXDefaulttimeForParallelTasks(commonTask,w.getParallelTasks(commonTask),w);
							coveredParallelTasks.add(commonTask);
							coveredParallelTasks.addAll(w.getParallelTasks(commonTask));
						}
					}
					else {
						//System.out.println("	-this task is not parallel with any task!!:"+w.getDefaultTime(task));
						t+=w.getDefaultTime(commonTask);
					}
				}
		    	//System.out.println("t: "+t+" p: "+p+" v: "+v 	);
		    }
		    
			
		    double pNextMin=0,tNextMin=0,vNextMin=0,msNextMin=0;

			//System.out.println("@Compute the cost of adaptation chain until the current violated task");   
			boolean sofar=false;
			for (int i = 0; i < w.getServiceTasks().size(); i++) {
	            String task=w.getServiceTasks().get(i);
	            if(!sofar) {
	    			//System.out.println("task:"+task);
					if(isAdaptedtaskBasedOnCurrentChain(task,adaptationChain)!=null) {
						//System.out.println("	-this task is adapted Based On Current Chain!!");
						Adaptation aa=isAdaptedtaskBasedOnCurrentChain(task,adaptationChain);
						p+=aa.getPrice();
						v+=aa.getValue();
						ms+=aa.getMitigationScore();
						if(w.getParallelTasks(aa.getTask()).size()!=0) {
							if(!coveredParallelTasks.contains(aa.getTask())) {
								//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
								t=+adaptationChain.MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w);
								coveredParallelTasks.add(task);
								coveredParallelTasks.addAll(w.getParallelTasks(aa.getTask()));
							}
						}
						else {
							//System.out.println("	-this task is not parallel with any task!!:"+aa.getTime());
							t+=aa.getTime();
							}
		            }
					
				   if(task.equals(violatedTask)) {
		            	sofar=true;
		            }
				   
				   if(w.getLastTask().equals(violatedTask)) {
					   adaptationChain.setFinalRLParameters(p, t, v, ms);
					   System.out.println("	Final parameters :"+p+ t+ v+ ms);

				   }
					
		          }
	            else {//compute the rest as minNextValue(The minimum cost for the state occurs when the workflow is completed without any violations, thereby requiring no further adaptations.)
	            	if(isAdaptedtaskBasedOnCurrentChain(task,adaptationChain)!=null) {
						//System.out.println("	-this task is adapted Based On Current Chain!!");
						Adaptation aa=isAdaptedtaskBasedOnCurrentChain(task,adaptationChain);
						pNextMin+=aa.getPrice();
						vNextMin+=aa.getValue();
						msNextMin+=aa.getMitigationScore();
						if(w.getParallelTasks(aa.getTask()).size()!=0) {
							if(!coveredParallelTasks.contains(aa.getTask())) {
								//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
								tNextMin=+adaptationChain.MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w);
								coveredParallelTasks.add(task);
								coveredParallelTasks.addAll(w.getParallelTasks(aa.getTask()));
							}
						}
						else {
							//System.out.println("	-this task is not parallel with any task!!:"+aa.getTime());
							tNextMin+=aa.getTime();
							}
		            }
					
				   if(task.equals(violatedTask)) {
		            	sofar=true;
		            }
	            	
	            	}
	          //System.out.println("t: "+t+" p: "+p+" v: "+v 	);
			}
			
			ArrayList<Double> parameters = new ArrayList<>();
			double sofarPrice=p;
			double sofarTime=t;
			double sofarValue=v;
			double sofarMitigationScore=ms;
			
			parameters.add(sofarTime);
			parameters.add(sofarPrice);
			parameters.add(sofarValue);
			parameters.add(sofarMitigationScore);

			double sofarTotalCost=(priceWeight*sofarPrice)+(timeWeight*sofarTime)-(valueWeight*sofarValue)-(securityWeight*sofarMitigationScore);
			parameters.add(sofarTotalCost);

			//System.out.println("@@@@@Compute Reward Completed@@@@@");
			return parameters;

	}

		
		
		
		

	  public StateChain getState(ChainRecord Chain,ArrayList<ChainRecord> ChainRecords) {
		   StateChain currentState=null;
		   for(StateChain st:states) {
				if(st.isSameState(Chain,ChainRecords)) {
					System.out.println("isSameState: true ");
					currentState=st;
					}
			}
		   return currentState;
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
				currentAction.setUncertainprice(2);
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
	
	
	
	public AdaptationChain selectBestAdaptationChainStrategy(ArrayList<AdaptationChain> AdaptationChainSet,ServiceTask s,Workflow workflow)throws IOException{
		double minCost = Double.MAX_VALUE;
		AdaptationChain bestSelectedAdaptationChain=null;
		for(AdaptationChain chain: AdaptationChainSet) {
			if(chain.getEntireTotalCost()<minCost) {
				minCost=chain.getEntireTotalCost();
				bestSelectedAdaptationChain=chain;
			}
		}
		
		
		/*
		System.out.println("			-selectedAdaptation : "+selectedAdaptation+" with Cost "+minCost+" which is the minimum cost between all possible adaptation actions");
		if(selectedAdaptation.equals("ReExecute") || selectedAdaptation.equals("ReConfiguration"))
			KM.GlobalAdaptation_ServiceAdaptation(selectedAdaptation,s,workflow);
		else
			 performTaskAdaptation(selectedAdaptation,s);
		*/
		return bestSelectedAdaptationChain;
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
	
	public AdaptationChain selectRandomAdaptationChainStrategy(ArrayList<AdaptationChain> AdaptationChainSet,ServiceTask s,Workflow workflow)throws IOException{
		Random rand = new Random();
		int randadapt=rand.nextInt(AdaptationChainSet.size());
		AdaptationChain selectedAdaptationChain=AdaptationChainSet.get(randadapt);
		
		
		/*
		if(selectedAdaptation.equals("ReExecute") || selectedAdaptation.equals("ReConfiguration"))
			KM.GlobalAdaptation_ServiceAdaptation(selectedAdaptation,s,workflow);
		else
			 performTaskAdaptation(selectedAdaptation,s);
		*/
	
		return selectedAdaptationChain;
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
	
	
	//chain version
	public void predictCostOfAdaptation2(ServiceTask task,String serviceTask,Adaptation adapt,Workflow workflow,Attack attack) {

		//Scheduling the Service
		ArrayList<Service> services = workflow.getLegalServiceforTasks().get(serviceTask);
		Random rand = new Random();
		Service service=services.get(rand.nextInt(services.size()));
		//service.print();
		Service backupService = services.get(rand.nextInt(services.size()));
		//backupService.print();
 
		ServiceTask s=new ServiceTask(task.getInstanceId(),task.getProcessId(), serviceTask,service,backupService);
		//s.print();
		
		adapt.calculateMitigationScoreInChain(s,attack,task,workflow);

		
		String adaptation=adapt.getName();
		//System.out.println(" cost vector "+workflow.getCostsforServiceTask(s.getTaskId()));
		ArrayList<Double> costs=workflow.getCostsforServiceTask(s.getTaskId());
		double cost=0,price=s.getService(). getCost(),time=s.getService(). getART(),value=costs.get(2);
		//System.out.println(" cost vector "+price+" "+time+" "+value);
		
		double oldtotalCost=this.priceWeight*price+this.timeWeight*time-this.valueWeight*value;// time and cost have positive and value has negetive relationship with final cost 
		//System.out.println("					-OldCost  "+oldtotalCost+" time "+time +" price "+price+" value "+value);
		
		
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

		adapt.setPriceTimeValue(time,price,value);
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
