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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


public class ServiceTask {
	
	private static int NumeofServiceTasks=0;
	private int serviceTaskId;//unique id for this service task between all of service tasks
	private long instanceId;// id of instance
	private String processId;//id of process
	private String taskId;// task id in the workflow
	private Service service;//service assigned to the task
	private Service backupService;//backupService assigned to the task
	private ArrayList<Double> securityObjectiveRequirements = new ArrayList<Double>();//confidentiality,integrity,availability
	String duration,protocol_type,serviceName,flag,src_bytes,dst_bytes,land,wrong_fragment,urgent,hot,num_failed_logins,logged_in,num_compromised,root_shell,su_attempted,num_root,num_file_creations,num_shells,num_access_files,num_outbound_cmds,is_hot_login,is_guest_login,count,srv_count,serror_rate,srv_serror_rate,rerror_rate,srv_rerror_rate,same_srv_rate,diff_srv_rate,srv_diff_host_rate,dst_host_count,dst_host_srv_count,dst_host_same_srv_rate,dst_host_diff_srv_rate,dst_host_same_src_port_rate,dst_host_srv_diff_host_rate,dst_host_serror_rate,dst_host_srv_serror_rate,dst_host_rerror_rate,dst_host_srv_rerror_rate,result;
	
	ServiceTypeSpecification specification;
	
	private ArrayList<String> legalServices = new ArrayList<String>();
	

	public ServiceTask(int id) {
		setId(id);
	}
	

	
	public ServiceTask(long instanceId,String processId, String taskId,Service service,Service backup) {
		
		//System.out.println("ServiceTask creation");
		setId();
		setInstanceId(instanceId);
		setProcessId(processId);
		setTaskId(taskId);
		setService(service);
		backupService=backup;
		setSecurityObjectives();
		
	}
	
	
	public int getId() {
		return serviceTaskId;
	}

	public void setId(int id) {
		this.serviceTaskId = id;
	}
	public void setId() {
		this.serviceTaskId = NumeofServiceTasks++;
	}
	public long getInstanceId() {
		return instanceId;
	}
	
	public int getProcessID() {
		Random rand = new Random();
		int ID=rand.nextInt(100);
		return ID;
	}

	public void setInstanceId(long id) {
		this.instanceId = id;
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String id) {
		this.processId = id;
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String id) {
		this.taskId = id;
	}
	
	
	public Service getService() {
		return service;
	}
	
	public Service getBackupService() {
		return backupService;
	}


	public void setService(Service id) {
		this.service = id;
	}
	
	public ArrayList<String> getLegalServices() {
		return legalServices;
	}

	public void setLegalServices(ArrayList<String> lu) {
		this.legalServices = lu;
	}
	
	public ArrayList<Double> getSecurityObjectiveRequirements() {
		return this.securityObjectiveRequirements;
	}
	
	public void setSecurityObjectives() {
		
		String delimsComma = "[,]+";
		for(String serviceName:ReadLinebyLine("ServiceTaskRequirements.txt")) {
			if(serviceName.contains(getTaskId())) {
				String[] arr2  = serviceName.split(delimsComma);
				int i=0;
				for (String obj : arr2) {
					if(i==0) 
						i=1;
					else {
						//System.out.println(obj);
						this.securityObjectiveRequirements.add(Double.parseDouble(obj));
					}
				}
			}
		}
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
	
	public String ServiceTaskToStringOnExit() {
		String s1=this.getId()+"   "+this.getInstanceId()+"   "+getProcessId()+"   "+getTaskId()+"   "+getService().getHostId();
		return s1;
	}
	
	public void print() {
		System.out.println("serviceTaskId: "+this.getId()+" instanceID: "+this.getInstanceId()+" processId: "+getProcessId()+" taskId: "+getTaskId()+" serviceName "+getService().getName()+" "+getService().getId()+" From host "+getService().getHostId());
		
	}

}
