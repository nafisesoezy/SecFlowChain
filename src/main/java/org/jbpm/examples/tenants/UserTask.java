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
import java.util.Random;

public class UserTask {
	
	private static int NumeofUserTasks=0;
	private int userTaskId;//unique id for this user task between all of user tasks
	private long instanceId;
	private String processId;
	private String taskId;//task id in the workflow
	private User user;//user assigned to the task
	private String usedIP;
	private String  day;
	private int durartion;
	private int numOfValids;
	private int numOfinvalids;
	private int numOfCalls;
	private int numOfIlegalDataInOutput;
	private int inputTraffic;
	
	private ArrayList<String> legalUsers = new ArrayList<String>();
	
	

	public UserTask(int id) {
		setId(id);
		
	}
	
	public UserTask(int id,long instanceId,String processId, String taskId,User user) {
		setId(id);
		setInstanceId(instanceId);
		setProcessId(processId);
		setTaskId(taskId);
		setUser(user);
		
	}
	
	public UserTask(long instanceId,String processId, String taskId,User user) {
		setId();
		setInstanceId(instanceId);
		setProcessId(processId);
		setTaskId(taskId);
		setUser(user);
		
	}
	
	//onEntruUserTask
	public UserTask(long instanceId,String processId, String taskId) {
		setId();
		setInstanceId(instanceId);
		setProcessId(processId);
		setTaskId(taskId);
	}
	
	//onExitUserTask
		public UserTask(long instanceId,String processId, String taskId, User user ,String usedIP, String day,int durartion,int numOfValids,int numOfinvalids,int inputTraffic,int numOfCalls,int numOfIlegalDataInOutput) {
			setId();
			setInstanceId(instanceId);
			setProcessId(processId);
			setTaskId(taskId);
			setUser(user);
			setUsedIP(usedIP);
			setDay(day);
			setDurartion(durartion);
			setNumOfValids(numOfValids); 
			setNumOfinvalids(numOfinvalids);
			setNumOfcalls(numOfCalls); 
			setNumOfIlegalDataInOutput(numOfIlegalDataInOutput); 
			setInputTraffic(inputTraffic); 
				
			}
	
	public int getId() {
		return userTaskId;
	}

	public void setId(int id) {
		this.userTaskId = id;
	}
	
	public void setId() {
		this.userTaskId = NumeofUserTasks++;
	}
	
	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long id) {
		this.instanceId = id;
	}
	
	public String getProcessId() {
		return processId;
	}
	
	public int getProcessID() {
		Random rand = new Random();
		int ID=rand.nextInt(100);
		return ID;
	}


	public void setProcessId(String id) {
		this.processId = id;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User u) {
		this.user = u;
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String id) {
		this.taskId = id;
	}
	
	
	public String getUsedIP() {
		return usedIP;
	}

	public void setUsedIP(String ip) {
		this.usedIP = ip;
	}
	
	
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public int getDurartion() {
		return durartion;
	}

	public void setDurartion(int d) {
		this.durartion = d;
	}
	

	public int getNumOfValids() {
		return numOfValids;
	}

	public void setNumOfValids(int n) {
		this.numOfValids = n;
	}
	
	public int getNumOfinvalids() {
		return numOfinvalids;
	}

	public void setNumOfinvalids(int n) {
		this.numOfinvalids = n;
	}
	public int getNumOfcalls() {
		return numOfCalls;
	}

	public void setNumOfcalls(int n) {
		this.numOfCalls = n;
	}
	public int getNumOfIlegalDataInOutput() {
		return numOfIlegalDataInOutput;
	}

	public void setNumOfIlegalDataInOutput(int n) {
		this.numOfIlegalDataInOutput = n;
	}
	
	public int getInputTraffic() {
		return inputTraffic;
	}

	public void setInputTraffic(int n) {
		this.inputTraffic = n;
	}
	//******
	

	
	public ArrayList<String> getLegalUsers() {
		return legalUsers;
	}

	public void setLegalUsers(ArrayList<String> lu) {
		this.legalUsers = lu;
	}
	
	public String UserTaskToStringOnEntry() {
		String s1=this.getId()+"   "+this.getInstanceId()+"   "+getProcessId()+"   "+getTaskId()+"   "+getUser().getName();
		return s1;
	}
	
	public String UserTaskToStringOnExit() {
		String s1=this.getId()+"   "+this.getInstanceId()+"   "+getProcessId()+"   "+getTaskId()+"   "+getUser().getName()+"   "+getUsedIP()+"   "+day+"   "+durartion+"   "+numOfValids+"   "+numOfinvalids+"   "+numOfCalls+"   "+numOfIlegalDataInOutput+"   "+inputTraffic;
		return s1;
	}
	
	public void print() {
		System.out.println("userTaskId: "+this.getId()+" instanceID: "+this.getInstanceId()+" processId: "+getProcessId()+" taskId: "+getTaskId()+" userName "+getUser().getName());
		
	}
	
	public void printAll() {
		System.out.println("userTaskId: "+this.getId()+" instanceID: "+this.getInstanceId()+" processId: "+getProcessId()+" taskId: "+getTaskId()+" userName "+getUser().getName()+" usedIP: "+usedIP+" day: "+ day+" durartion: "+durartion+" numOfValids "+numOfValids+" numOfinvalids: "+numOfinvalids+" numOfCalls: "+numOfCalls+" numOfIlegalDataInOutput: "+numOfIlegalDataInOutput+" inputTraffic: "+inputTraffic);
		
	}

}
