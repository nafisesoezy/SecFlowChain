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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 
import java.util.Random;
import java.util.*;
import java.lang.Math;

public class Workflow {
	
	private static int numOfWorkflow=0;
	private  int id;
	private String name;
	private TenantKernel tenant;
	KernelManagment KM;
	private String tenantAddress;
	Map<String, ArrayList<User>> LegalUsersforTasks = new HashMap<String, ArrayList<User>>();
	Map<String, ArrayList<Service>> LegalServicesforTasks = new HashMap<String, ArrayList<Service>>();
	Map<String, ArrayList<Integer>> thresholds = new HashMap<String, ArrayList<Integer>>();
	ArrayList<User> AllUsers;
	ArrayList<User> users= new ArrayList<User>();
	ArrayList<Service> services=new ArrayList<Service>();
	private ArrayList<Double> securityRequirementsForServices = new ArrayList<Double>();
	Map<String, ArrayList<String>> serviceTasksLegalAdaptation=new HashMap<String, ArrayList<String>>();//there is a list of legal adaptation for each task(serviceTasksLegalAdaptation.get(ti)=list of adaptation for task ti)
	Map<String, ArrayList<Double>> serviceCost = new HashMap<String, ArrayList<Double>>();
	Map<String, ArrayList<Double>> userCost = new HashMap<String, ArrayList<Double>>();
	ArrayList<ArrayList<String>> parallelTasksList = new ArrayList<ArrayList<String>>();
	Map<String, Map<String, ArrayList<String>>> serviceTasksLegalAdaptationCost=new HashMap<String, Map<String, ArrayList<String>>>();
	Map<String, ArrayList<String>> DataFlowClosureSet = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> ControlFlowClosureSet = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> Successors = new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<String>> Predecessors = new HashMap<String, ArrayList<String>>();
	
	double[][] ConfidentialityMatrix;
	double[][] IntegrityMatrix;
	double[][] AvailabilityMatrix;
	Map<String, Double> DefaultPrice= new HashMap<String, Double>();
	Map<String, Double> DefaultValue = new HashMap<String, Double>();
	Map<String, Double> DefaultTime = new HashMap<String, Double>();
	double priceWeight,timeWeight,valueWeight,securityWeight;

	

	//Map<Service, ServiceTask> scheduledServices = new HashMap<Service, ServiceTask>();



	
	private ArrayList<String> userTasks = new ArrayList<String>();
	Map<String, ArrayList<Double>> serviceTasks = new HashMap<String, ArrayList<Double>>();
	ArrayList<String> serviceTasksOrder=new ArrayList<String>();

	public Workflow(int id) {
		setId(id);
	}
	
	
	public double getConfidentialityMatrix(int i,int j) {
		return ConfidentialityMatrix[i][j];
	}
	
	public double getIntegrityMatrix(int i,int j) {
		return IntegrityMatrix[i][j];
	}
	
	public double getAvailabilityMatrix(int i,int j) {
		return AvailabilityMatrix[i][j];
	}

	
	public double getDefaultPrice(String task) {
		return DefaultPrice.get(task);
	}
	
	public double getDefaultValue(String task) {
		return DefaultValue.get(task);
	}
	
	public double getDefaultTime(String task) {
		return DefaultTime.get(task);
	}
	
	
	//main
	public Workflow(String name,ArrayList<String> userTasks,ArrayList<String> sTasksOrder,Map<String, ArrayList<Double>> serviceTasks,ArrayList<User> users,TenantKernel tenant,KernelManagment KM,Map<String, ArrayList<String>> LegalAdaptation,Map<String, Map<String, ArrayList<String>>> TasksLegalAdaptationCost,Map<String, ArrayList<Double>> costs,Map<String, ArrayList<Double>> userCost,ArrayList<ArrayList<String>> parallelTasks,Map<String, ArrayList<String>> dataFlowClosureSet,Map<String, ArrayList<String>> controlFlowClosureSet,Map<String, ArrayList<String>> successors,Map<String, ArrayList<String>> predecessors,double pWeight,double tWeight,double vWeight,double sWeight) {
		setId();
		this.name=name;
		this.userTasks=userTasks;
		this.serviceTasks=serviceTasks;
		this.AllUsers=users;
		assignRandomThresholds();
		this.tenant=tenant;
		this.tenantAddress=tenant.getId()+"/";
		
		this.priceWeight=pWeight;
		this.timeWeight=tWeight;
		this.valueWeight=vWeight;
		this.securityWeight=sWeight;
		
		this.KM=KM;
		//assign legalUsers to userTasks
		assignLegalUsersforTasks(AllUsers);
				
		//assign legalServices to serviceTasks
		assignLegalServicesforTasks(KM.getAllServices());
		
		//assign legalApatations to serviceTasks
		this.serviceTasksLegalAdaptation=LegalAdaptation;
		this.serviceTasksLegalAdaptationCost=TasksLegalAdaptationCost;
		
		//assign cost(price,time,value) to serviceTasks
		this.serviceCost = costs;

		//assign cost(price,time,value) to userTasks
		this.userCost = userCost;
		
		//define parallel tasks
		this.parallelTasksList =parallelTasks;
		this.serviceTasksOrder=sTasksOrder;
		//for(int i=0;i<serviceTasksOrder.size();i++) {
		//	System.out.println("serviceTasksOrder "+serviceTasksOrder.get(i));
		//}
		this.DataFlowClosureSet = dataFlowClosureSet;
		this.ControlFlowClosureSet =controlFlowClosureSet;
		
		this.Successors=successors;
		this.Predecessors=predecessors;
		
	
		
		System.out.println("Legal Services for Tasks");

		for(String task:LegalServicesforTasks.keySet()) {
			System.out.println("for "+task);
			for(Service s:LegalServicesforTasks.get(task)) {
				s.print();
			}
		}
		
		
		System.out.println("Legal user for Tasks");

		for(String task:LegalUsersforTasks.keySet()) {
			System.out.println("for "+task);
			for(User s:LegalUsersforTasks.get(task)) {
				s.print();
			}
		}
		System.out.println("legal Apatations for serviceTasks");
		for(String Servicetask:serviceTasksLegalAdaptation.keySet()) {
			System.out.println("for "+Servicetask+" : ");
			for(String s:serviceTasksLegalAdaptation.get(Servicetask)) {
				System.out.print(s+", ");
			}
			System.out.println();
		}
		
		System.out.println("Service cost(price,time,value)");
		for(String Servicetask:serviceCost.keySet()) {
			System.out.println("for "+Servicetask+" : ");
			for(double c:serviceCost.get(Servicetask)) {
				System.out.print(c+", ");
			}
			System.out.println();
		}
		
		System.out.println("User cost(price,time,value)");
		for(String Usertask:userCost.keySet()) {
			System.out.println("for "+Usertask+" : ");
			for(double c:userCost.get(Usertask)) {
				System.out.print(c+", ");
			}
			System.out.println();
		}
		
		ComputeSecurityDependencyMatrix();

		for(String s:serviceTasksOrder) {	
			DefaultPrice.put(s,getDefaultServiceforTasks().get(s).getCost());
			DefaultTime.put(s,getDefaultServiceforTasks().get(s).getART());
			DefaultValue.put(s,serviceCost.get(s).get(2));
		}
		/*
		for(String s:serviceTasksOrder) {
			System.out.println("task "+s);
			System.out.println("	-Time "+DefaultTime.get(s));
			System.out.println("	-Price "+DefaultPrice.get(s));
			System.out.println("	-Value "+DefaultValue.get(s));
		}
		 */
		
	}
	
	
	public int getId() {
		return id;
	}
	

	public String getName() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setId() {
		this.id = ++numOfWorkflow;
	}
	
	public TenantKernel getTenant() {
		return tenant;
	}
	
	public ArrayList<String> getServiceTasks() {
		return serviceTasksOrder;
	}
	
	
	public String getLastTask() {
		return serviceTasksOrder.get(serviceTasksOrder.size() - 1);
	}
	
	public int getTaskIndexInWorkflow(String task) {
		for (int i = 0; i < serviceTasksOrder.size(); i++) {
            if (serviceTasksOrder.get(i).equals(task)) {
                return i; // Return the index if the element is found
            }
        }
		return -1;
	}
	
	public Map<String, ArrayList<String>> getLegalAdaptations() {
		return serviceTasksLegalAdaptation;
	}
	
	public Map<String, ArrayList<String>> getDataFlowClosureSet(){
		return DataFlowClosureSet;
	}
	
	public Map<String, ArrayList<String>> getControlFlowClosureSet(){
		return ControlFlowClosureSet;
	}
	
	public  ArrayList<String> getSuccessors(String task){
		return Successors.get(task);
	}
	
	public  ArrayList<String> getPredecessors(String task){
		return Predecessors.get(task);
	}
	
	
	public ArrayList<String> getSubsequentTasks(String violatedTask){
		ArrayList<String> subsequentTasks = new ArrayList<String>();
		
		boolean check=false;
		for (int i = 0; i < getServiceTasks().size(); i++) {
            String task=getServiceTasks().get(i);
            if(check)
            	subsequentTasks.add(task);
            if(task.equals(violatedTask)) 
            	check=true;  
         }
		/*
		System.out.println("SubsequentTasks:");
		for(String s:subsequentTasks) {
			System.out.println(s+", ");
		}
		*/
		return subsequentTasks;
	}
	
	public boolean CheckSecurityDependency(int i,int j) {
		if(ConfidentialityMatrix[i][j]!=0)
			return true;
		if(IntegrityMatrix[i][j]!=0)
			return true;
		if(AvailabilityMatrix[i][j]!=0)
			return true;
		return false;	
	} 
	
	public ArrayList<String> getLegalAdaptationforServiceTask(String task) {
		return serviceTasksLegalAdaptation.get(task);
	}
	
	public  ArrayList<Double> getSecurityRequirementsForService(String task) {
		return serviceTasks.get(task);
	}
	
	public ArrayList<Double> getCostsforServiceTask(String task) {
		return serviceCost.get(task);
	}
	
	public ArrayList<Double> getCostsforUserTask(String task) {
		return userCost.get(task);
	}
	
	public ArrayList<String> getAdaptationCostForService_Action(String task,String action) {
		//System.out.println("Here task  "+task+" "+action);
		return serviceTasksLegalAdaptationCost.get(task).get(action);
	}


	public ArrayList<Double> NormalizedCost(ServiceTask s,double price,double time,double value){
		ArrayList<Double> normalizedCost=new ArrayList<Double>();
		double totalPrice=0,totalTime=0,totalValue=0;
		for(String user:this.userTasks) {
			totalPrice+=userCost.get(user).get(0);
			totalTime+=userCost.get(user).get(1);
			totalValue+=userCost.get(user).get(2);

		}
		for(String service:this.serviceTasks.keySet()) {
			//System.out.println("ElSE  "+s.getTaskId()+" "+service);
			if(!service.equals(s.getTaskId())) {
				
				//option1:based on task definitions
				//totalPrice+=serviceCost.get(service).get(0);	
				//totalTime+=serviceCost.get(service).get(1);
				
				//option2:based on service specification
				totalPrice+=serviceCost.get(service).get(0);	
				totalTime+=serviceCost.get(service).get(1);
				totalValue+=serviceCost.get(service).get(2);
			}
			else {
				//System.out.println("ElSEEE  ");
				totalPrice+=price;
				totalTime+=time;
				totalValue+=value;
			}
		}
		//System.out.println("     totalPrice: "+totalPrice+" totalTime "+totalTime+" totalValue "+totalValue);
		normalizedCost.add((double)price/totalPrice);
		normalizedCost.add((double)time/totalTime);
		normalizedCost.add((double)value/totalValue);
		System.out.println("				-NormalPrice: "+normalizedCost);

		return normalizedCost;
	}
	
	
	
	public ArrayList<Double> TotalCost(String s,double price,double time,double value){
		ArrayList<Double> totalCost=new ArrayList<Double>();
		double totalPrice=0,totalTime=0,totalValue=0;
		Map<String, ArrayList<Double>> alltasks=getAllTasksWithCost();
		ArrayList<String> removes=new ArrayList<String>();
		
		for(String task:alltasks.keySet()) {
			if(!removes.contains(task)) {
				//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ ");
				//System.out.println("    -task: "+task);
			
				if(getParallelTasks(task).isEmpty() && s.equals(task)) {//if there is no parallel task with this task and it is adapted task, then use new time for time calculation
					//System.out.println("     -getParallelTasks is Empty for self Task "+task);
					totalPrice+=price;
					totalTime+=time;
					totalValue+=value;
					//System.out.println("     partial totalPrice: "+totalPrice+" totalTime "+totalTime+" totalValue "+totalValue);

					
					}
				else if(getParallelTasks(task).isEmpty() && !s.equals(task)) {//if there is no parallel task with this task and it is not adapted task, then use old time for time calculation
					//System.out.println("     -getParallelTasks is Empty for Task "+task);
					totalPrice+=alltasks.get(task).get(0);
					totalTime+=alltasks.get(task).get(1);
					totalValue+=alltasks.get(task).get(2);
					//System.out.println("     partial totalPrice: "+totalPrice+" totalTime "+totalTime+" totalValue "+totalValue);

					}
				
				else if(!getParallelTasks(task).isEmpty() && s.equals(task)){//if there is any parallel task with this service and it is adapted task, then use new time for parallel time calculation
					//System.out.println("     -getParallelTasks is Not Empty for self Task "+task);
					totalPrice+=price;
					totalValue+=value;
					//*********** for time
					ArrayList<Double> parallelTaskTimes = new ArrayList<Double>();
					parallelTaskTimes.add(time);
					for(String p:getParallelTasks(task)) {
						parallelTaskTimes.add(alltasks.get(task).get(1));
						totalPrice+=alltasks.get(task).get(0);
						totalValue+=alltasks.get(task).get(2);
						//System.out.println("remove "+p);
						removes.add(p);
						
					}
					totalTime+=findMaximumTime(parallelTaskTimes);
					//System.out.println("     partial totalPrice: "+totalPrice+" totalTime "+totalTime+" totalValue "+totalValue);

				}
				else if(!getParallelTasks(task).isEmpty() && !s.equals(task)){//if there is any parallel task with this service and it is not adapted task, then use old time for parallel time calculation
					//System.out.println("     -getParallelTasks is Not Empty for Task "+task);
					totalPrice+=alltasks.get(task).get(0);
					totalValue+=alltasks.get(task).get(2);
					
					//*********** for time
					ArrayList<Double> parallelTaskTimes = new ArrayList<Double>();
					parallelTaskTimes.add(alltasks.get(task).get(1));
					for(String p:getParallelTasks(task)) {
						parallelTaskTimes.add(alltasks.get(task).get(1));
						totalPrice+=alltasks.get(task).get(0);
						totalValue+=alltasks.get(task).get(2);
						//System.out.println("remove "+p);
						removes.add(p);
					}
					totalTime+=findMaximumTime(parallelTaskTimes);
					//System.out.println("parallelTaskTimes "+parallelTaskTimes);
					//System.out.println("     max time: "+findMaximumTime(parallelTaskTimes));
					//System.out.println("     partial totalPrice: "+totalPrice+" totalTime "+totalTime+" totalValue "+totalValue);

				}
			}//end if
			//else 
				//System.out.println("task is removed "+task);

		}//end for
		System.out.println("				-totalPrice: "+totalPrice+" totalTime "+totalTime+" totalValue "+totalValue);
		totalCost.add(totalPrice);
		totalCost.add(totalTime);
		totalCost.add(totalValue);
		return totalCost;
	}
	
	
	public Map<String, ArrayList<Double>>  getAllTasksWithCost() {
		Map<String, ArrayList<Double>> tasks=new HashMap<String, ArrayList<Double>>();
		
		for(String user:this.userTasks) {
			tasks.put(user,userCost.get(user));
		}
		for(String service:this.serviceTasks.keySet()) {
			tasks.put(service,serviceCost.get(service));
		}
		return tasks;
	}
	
	
	

	
	public double findMaximumTime(ArrayList<Double> list) {
		double max = 0;
		for (int i = 0; i < list.size(); i++) {
		    double current = list.get(i);
		    if (current > max) {
		        max = current;
		    }
		}
		return max;
	}
	
	//find the parallel tasks with one given task
	public  ArrayList<String> getParallelTasks(String task) {
		//System.out.println("***main list "+parallelTasksList);

		ArrayList<String> parallelTasks=new ArrayList<String>();
		for(ArrayList<String> list:parallelTasksList) {
			if(list.contains(task)) {
				//System.out.println(" Parallel list for "+task);
				//System.out.println(list);
				parallelTasks.addAll(list);
				parallelTasks.remove(task);
				//System.out.println("list "+list);
				//System.out.println("parallelTasks "+parallelTasks);
			}
		}
		
		return parallelTasks;
	}
	

	
	
	public void assignRandomThresholds() {
		for(String s:this.userTasks) {
			ArrayList<Integer> threshes = new ArrayList<Integer>();
			Random rand = new Random();
			int durationThresh=rand.nextInt(1000)+100;
			//System.out.print(" durationThresh: "+durationThresh+" "+durationThresh.toString());
			threshes.add(durationThresh);
			int numOfValids=rand.nextInt(100)+10;
			threshes.add(numOfValids);
			int numOfinvalids=rand.nextInt(3);
			threshes.add(numOfinvalids);	
			int numOfCalls=rand.nextInt(100)+10;
			threshes.add(numOfCalls);			
			int numOfIlegalDataInOutput=0;
			threshes.add(numOfIlegalDataInOutput);
			int inputTraffic=rand.nextInt(10000)+150000;
			threshes.add(inputTraffic);
			thresholds.put(s,threshes);
		}
		
		for(String s:this.serviceTasks.keySet()) {
			ArrayList<Integer> threshes = new ArrayList<Integer>();
			Random rand = new Random();
			int durationThresh=rand.nextInt(1000)+1000;
			threshes.add(durationThresh);
			int numOfValids=rand.nextInt(100)+100;
			threshes.add(numOfValids);
			int numOfinvalids=rand.nextInt(2);
			threshes.add(numOfinvalids);		
			int numOfCalls=rand.nextInt(100)+50;
			threshes.add(numOfCalls);	
			int numOfIlegalDataInOutput=0;
			threshes.add(numOfIlegalDataInOutput);	
			int inputTraffic=rand.nextInt(10000)+150000;
			threshes.add(inputTraffic);
			thresholds.put(s,threshes);
		}
		
		for (Map.Entry<String, ArrayList<Integer>> entry : thresholds.entrySet()) {
			System.out.print("task: "+entry.getKey());
			for(int t:entry.getValue() ) {
				System.out.print("  "+t);
			}
			System.out.println(" ");
		}
	
	}
	
	
	
	
	public Map<String, ArrayList<User>> getLegalUsersforTasks(){
		return this.LegalUsersforTasks;
	}
	
	public Map<String, ArrayList<Service>> getLegalServiceforTasks(){
		return this.LegalServicesforTasks;
	}
	
	public Map<String, Service> getDefaultServiceforTasks(){
		Map<String, Service> DefaultServiceforTasks=new HashMap<String, Service>();
		for(String task:serviceTasksOrder) {
			double mintotalCost=Double.MAX_VALUE;
			Service ServiceDefault=null;
			for(Service s: LegalServicesforTasks.get(task)) {			
				double cost=this.priceWeight*s. getCost()+this.timeWeight*s.getART();// time and cost have positive and value has negetive relationship with final cost 
				if(cost<mintotalCost) {
					mintotalCost=cost;
					ServiceDefault=s;
				}
			}
			//System.out.println("DefaultServiceforTasks  "+task);
			//ServiceDefault.print();
			DefaultServiceforTasks.put(task,ServiceDefault);
		}
		return DefaultServiceforTasks;
	}
	
	public String getTenantAddress() {
		return tenantAddress;
	}
	
	public ArrayList<String> getUserTasks() {
		return userTasks;
	}

	public void setUserTasks(ArrayList<String> ut) {
		this.userTasks = ut;
	}
	
	public Map<String, ArrayList<Integer>> getThresholds() {
		return thresholds;
	}

	
	public ArrayList<User> getUsers(){
		return this.users;
	}
	public ArrayList<Service>  getServices(){
		return this.services;
	}
	
	public void assignLegalUsersforTasks(ArrayList<User> users) {
		for(String s:this.userTasks) {
			assignLegalUsersforTask(s,users);
		}
	}
	
	public void assignLegalAdaptationforTasks() {
		
	}
	
	public void assignLegalUsersforTask(String task,ArrayList<User> users) {
		if(userTasks.size()!=0) {
			ArrayList<User> legalUsers=new ArrayList<User>();
			Random rand = new Random();
			for(int i=0;i<10;i++) 
				legalUsers.add(users.get(rand.nextInt(users.size())));
			LegalUsersforTasks.put(task,legalUsers);
		}
	}
	
	
	public void assignLegalServicesforTasks(ArrayList<Service> services) {
		for(String s:this.serviceTasks.keySet()) {
			System.out.println(" assignLegalServicesfor task "+s);
			assignLegalServiceforTask(s,services,serviceTasks.get(s));
		}
		
		
		
	}
	

	
	public void assignLegalServiceforTask(String task,ArrayList<Service> services,ArrayList<Double> securityRequirements) {
		//System.out.println("I'm hereee");
		if(services.size()!=0) {
		//if(serviceTasks.size()!=0) {
			ArrayList<Service> legalServices=new ArrayList<Service>();
			for(Service s:services) {
				//System.out.println("s.getName()"+s.getName()+" task "+task);
				if(s.getName().equals(task) && s.getIntegrity()>=securityRequirements.get(0) && s.getConfidentiality()>=securityRequirements.get(1)  && s.getAvailability()>=securityRequirements.get(2)) {
					legalServices.add(s);
					//System.out.println("HEEERE: Integrity: "+s.getIntegrity()+" "+securityRequirements.get(0)+" Confidentiality: "+s.getConfidentiality()+" "+securityRequirements.get(1)+" Availability: "+s.getAvailability()+" "+securityRequirements.get(2) );
				}
			}
	
			LegalServicesforTasks.put(task,legalServices);
			/*
			Random rand = new Random();
			legalServices.add(services.get(rand.nextInt(services.size())));
			legalServices.add(services.get(rand.nextInt(services.size())));
			LegalServicesforTasks.put(task,legalServices);
			*/
		}
	}
	
	
	public void Scheduling(ArrayList<User> users,KernelManagment KM) {
		System.out.println("trust-Aware Scheduling Planner for "+this.getName());
		for(String u:this.userTasks) {
			UserScheduling(u);
		}
		for(String s:this.serviceTasks.keySet()) {
			ServiceScheduling(s);
		}
	}
	
	
	public User UserScheduling(String userTask) {
		System.out.println("UserScheduling "+userTask);
		Random rand = new Random();	
		ArrayList<User> users = getLegalUsersforTasks().get(userTask);
		User user=users.get(rand.nextInt(users.size()));
		return user;
	}
	
	public Service ServiceScheduling(String serviceTask) {
		System.out.println("ServiceScheduling "+serviceTask);
		Random rand = new Random();	
		ArrayList<Service> services = getLegalServiceforTasks().get(serviceTask);
		int s=rand.nextInt(services.size());
		System.out.println("this service "+serviceTask+" is scheduling on "+services.get(s).getHostId());
		return services.get(s);
	}
	
	public void ComputeSecurityDependencyMatrix() {
		System.out.print("SecurityDependencyMatrix: ");
		int size = serviceTasksOrder.size();

		// Initialize the ConfidentialityMatrix, IntegrityMatrix, and AvailabilityMatrix
		ConfidentialityMatrix = new double[size][size];
		IntegrityMatrix = new double[size][size];
		AvailabilityMatrix = new double[size][size];

		/*
		for (int k = 0; k < size; k++) {
		    for (int t = 0; t < size; t++) {
		        ConfidentialityMatrix[k][t] = 1.0;
		        IntegrityMatrix[k][t] = 1.0;
		        AvailabilityMatrix[k][t] = 1.0;
		    }
		}
		 */
		for(int index1=0;index1<size;index1++) {
			String i=serviceTasksOrder.get(index1);
			//System.out.print("ServiceTask [i]: "+i+"  ");
			for(int index2=0;index2<size;index2++) {
				String j=serviceTasksOrder.get(index2);
				//System.out.print("ServiceTask [j]: "+j+"  ");
				if (!i.equals(j)) {
                    double C_ij = 0, I_ij = 0, A_ij = 0;

                    // Check conditions for DFCS and compute C_ij, I_ij, A_ij accordingly
                    // Compute C_ij
                    if (DataFlowClosureSet.get(i).contains(j)) {  
                    	ArrayList<String> dataFlow=DataFlowClosureSet.get(i);
                    	C_ij =  serviceTasks.get(i).get(0);
                        for (int index3=0;index3<dataFlow.size();index3++) {
                        	String t_k=dataFlow.get(index3);
                            C_ij *= serviceTasks.get(t_k).get(0);
                        	//System.out.println("t_k :"+ t_k+" c_j :"+ serviceTasks.get(j).get(0)+" C_ij:"+C_ij);
                            if(t_k.equals(j)) {
                            	//System.out.println("break");
                            	break;
                            }
                        }            
                    }
                    //System.out.println("step1:"+C_ij);
                    
                    if (DataFlowClosureSet.get(j).contains(i)) {  
                    	ArrayList<String> dataFlow=DataFlowClosureSet.get(j);
                    	C_ij =serviceTasks.get(i).get(0);
                        for (int index3=0;index3<dataFlow.size();index3++) {
                        	String t_k=dataFlow.get(index3);
                        	C_ij *= serviceTasks.get(t_k).get(0);
                        	//System.out.println("t_k :"+ t_k+" c_t_k :"+ serviceTasks.get(t_k).get(0)+" C_ij:"+C_ij);
                            if(t_k.equals(j)) {
                            	//System.out.println("break");
                            	break;
                            }
                        }            
                    }
                    //System.out.println("step2:"+C_ij);
                    ConfidentialityMatrix[index1][index2]=C_ij;
                    
                    // Compute I_ij
                    if (DataFlowClosureSet.get(i).contains(j)) {  
                    	ArrayList<String> dataFlow=DataFlowClosureSet.get(i);
                    	I_ij = serviceTasks.get(i).get(1);
                        for (int index3=0;index3<dataFlow.size();index3++) {
                        	String t_k=dataFlow.get(index3);
                        	I_ij *= serviceTasks.get(t_k).get(1);
                        	//System.out.println("t_k :"+ t_k+" I_j :"+ serviceTasks.get(j).get(1)+" I_ij:"+I_ij);
                            if(t_k.equals(j)) {
                            	//System.out.println("break");
                            	break;
                            }
                        }            
                    }
                    //System.out.println("step3:"+I_ij);

                    if (ControlFlowClosureSet.get(i).contains(j)) {  
                    	ArrayList<String> ControlFlow=ControlFlowClosureSet.get(i);
                    	I_ij =serviceTasks.get(i).get(1);
                        for (int index3=0;index3<ControlFlow.size();index3++) {
                        	String t_k=ControlFlow.get(index3);
                        	I_ij *= serviceTasks.get(t_k).get(1);
                        	//System.out.println("t_k :"+ t_k+" I_j :"+ serviceTasks.get(j).get(1)+" I_ij:"+I_ij);
                            if(t_k.equals(j)) {
                            	//System.out.println("break");
                            	break;
                            }
                        }            
                    }
                    //System.out.println("step4:"+I_ij);               
                    IntegrityMatrix[index1][index2]=I_ij;
                    
                    // Compute A_ij
                    if (DataFlowClosureSet.get(i).contains(j)) {  
                    	ArrayList<String> dataFlow=DataFlowClosureSet.get(i);
                    	A_ij = serviceTasks.get(i).get(2);
                        for (int index3=0;index3<dataFlow.size();index3++) {
                        	String t_k=dataFlow.get(index3);
                        	A_ij *= serviceTasks.get(t_k).get(2);
                        	//System.out.println("t_k :"+ t_k+" I_j :"+ serviceTasks.get(j).get(2)+" A_ij:"+A_ij);
                            if(t_k.equals(j)) {
                            	//System.out.println("break");
                            	break;
                            }
                        }            
                    }
                    //System.out.println("step5:"+A_ij);
                    AvailabilityMatrix[index1][index2]=A_ij;

                }
				else {
					ConfidentialityMatrix[index1][index2]=1; 
					IntegrityMatrix[index1][index2]=1;
					AvailabilityMatrix[index1][index2]=1;
				}
			}
		}
		
		System.out.println("Matrix:");
		for (int i = 0; i < size; i++) {
		    for (int j = 0; j < size; j++) {
		        System.out.print(ConfidentialityMatrix[i][j] + ",");
		        System.out.print(IntegrityMatrix[i][j] + ",");
		        System.out.print(AvailabilityMatrix[i][j] + " ");
		    }
		    System.out.println();
		}
	}

	
	public void print(){
		System.out.println("Workflow: "+this.id+"  ");
		for(String u:this.userTasks)
			System.out.print("userTask: "+u+"  ");
		for(String s:this.serviceTasks.keySet())
			System.out.print("ServiceTask: "+s+"  ");
		System.out.println();
	}
	

}
