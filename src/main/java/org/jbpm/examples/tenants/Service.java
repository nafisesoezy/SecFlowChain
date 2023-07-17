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
import java.util.Map; 
import java.util.HashMap;
import java.util.Random;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Service {
	
	private int id;
	private String name;
	private int hostId;//define the provider ID which this service are located on.
	private double trust;
	private double confidentiality,integrity,availability;
	private double cost;
	private double ART;//Average Response Time

	private String repositoryAddress;	
	Map<String, Integer> AttackNum = new HashMap<String, Integer>();
	Map<String, Double> AFR = new HashMap<String, Double>();
	int interval =0;
	Map<String, ArrayList<Double>> attackSecurityRisk = new HashMap<String, ArrayList<Double>>();
	private ArrayList<Double> securityRiskAssesment= new ArrayList<Double>();//securityRiskAssesment[0]:confidentiality securityRiskAssesment[1]:Integrity securityRiskAssesment[2]:availability



	

	public Service(int id) {
		setId(id);
		System.out.println(id+ "is created");
	}
	
	public Service(int id,String name,int hostId,double trust) {
		setId(id);
		setName(name);
		setHostId(hostId);
		setTrust(trust);
		serParameters();
	}
	
	
	public Service(int id,String name,int hostId,double trust,String repositoryAddress) {
		setId(id);
		setName(name);
		setHostId(hostId);
		setTrust(trust);
		setRepositoryAddress(repositoryAddress);
		serParameters();
	}
	
	public void serParameters() {
		Random rand = new Random();
		cost=0.1 + (10 - 0.1) * rand.nextDouble();
		
		if(this.name.equals("ObtainClaimService")) {
			if (cost>7) {
				confidentiality=1;
				integrity=1;
				availability=1;
				ART=rand.nextInt(20);
			}
			if (3<cost && cost<7) {
				confidentiality=1;
				integrity=1;
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+20;
			}
			else {
				confidentiality=0.5 + (rand.nextDouble() * 0.5);
				integrity=0.5 + (rand.nextDouble() * 0.5);
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+30;
			}
		}
		if(this.name.equals("HospitalReport")) {
			if (cost>7) {
				confidentiality=1;
				integrity=1;
				availability=1;
				ART=rand.nextInt(20);
			}
			if (3<cost && cost<7) {
				confidentiality=0.7 + (rand.nextDouble() * 0.3);
				integrity=1;
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+20;
			}
			else {
				confidentiality=0.5 + (rand.nextDouble() * 0.5);
				integrity=0.5 + (rand.nextDouble() * 0.5);
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+30;
			}
		}
		if(this.name.equals("PoliceReport")) {
			if (cost>7) {
				confidentiality=1;
				integrity=1;
				availability=1;
				ART=rand.nextInt(20);
			}
			if (3<cost && cost<7) {
				confidentiality=0.7 + (rand.nextDouble() * 0.3);
				integrity=1;
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+20;
			}
			else {
				confidentiality=0.5 + (rand.nextDouble() * 0.5);
				integrity=0.5 + (rand.nextDouble() * 0.5);
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+30;
			}
		}
		if(this.name.equals("SelectBestExpert")) {
			if (cost>7) {
				confidentiality=1;
				integrity=1;
				availability=1;
				ART=rand.nextInt(20);
			}
			if (3<cost && cost<7) {
				confidentiality=0.5 + (rand.nextDouble() * 0.5);
				integrity=1;
				availability=1;
				ART=rand.nextInt(20)+20;
			}
			else {
				confidentiality=0.5 + (rand.nextDouble() * 0.5);
				integrity=0.5 + (rand.nextDouble() * 0.5);
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+30;
			}
		}
		if(this.name.equals("BankReimbursment")) {
			if (cost>7) {
				confidentiality=1;
				integrity=1;
				availability=1;
				ART=rand.nextInt(20);
			}
			if (3<cost && cost<7) {
				confidentiality=1;
				integrity=1;
				availability=1;
				ART=rand.nextInt(20)+20;
			}
			else {
				confidentiality=0.5 + (rand.nextDouble() * 0.5);
				integrity=0.5 + (rand.nextDouble() * 0.5);
				availability=0.5 + (rand.nextDouble() * 0.5);
				ART=rand.nextInt(20)+30;
			}
		}
		if(this.name.equals("CustomerFeedbackCollection")) {
			if (cost>7) {
				confidentiality=0.5 + (rand.nextDouble() * 0.3);
				integrity=0.5 + (rand.nextDouble() * 0.3);
				availability=0.5 + (rand.nextDouble() * 0.3);
				ART=rand.nextInt(20);
			}
			if (3<cost && cost<7) {
				confidentiality=0.1 + (rand.nextDouble() * 0.3);
				integrity=0.1 + (rand.nextDouble() * 0.3);
				availability=0.1 + (rand.nextDouble() * 0.3);
				ART=rand.nextInt(20)+20;
			}
			else {
				confidentiality=rand.nextDouble() * 0.5;
				integrity=rand.nextDouble() * 0.5;
				availability=rand.nextDouble() * 0.5;
				ART=rand.nextInt(20)+30;
			}
		}
		else{
			if (cost>7) {
				//confidentiality=0.7 + (rand.nextDouble() * 0.3);
				confidentiality=1;
				//integrity=0.7 + (rand.nextDouble() * 0.3);
				integrity=1;
				//availability=0.7 + (rand.nextDouble() * 0.3);
				availability=1;
				ART=rand.nextInt(20);
			}
			if (3<cost && cost<7) {
				confidentiality=1;
				//confidentiality=0.7 + (rand.nextDouble() * 0.3);
				//integrity=0.5 + (rand.nextDouble() * 0.2);
				integrity=1;
				//availability=0.7 + (rand.nextDouble() * 0.3);
				availability=1;
				ART=rand.nextInt(20)+20;
			}
			else {
				confidentiality=rand.nextDouble() * 0.5;
				integrity=rand.nextDouble() * 0.5;
				availability=rand.nextDouble() * 0.5;
				ART=rand.nextInt(20)+30;
			}
		}
		System.out.println("cost "+cost+" confidentiality "+confidentiality+" integrity "+integrity+" availability "+availability+" ART "+ART);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}
	
	public double getAFR(String attackType) {
		return AFR.get(attackType);
	}
	

	public String getRepositoryAddress() {
		return repositoryAddress;
	}

	public void setRepositoryAddress(String n) {
		this.repositoryAddress = n;
	}
	
	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	
	public double getTrust() {
		return trust;
	}
	
	public double getCost() {
		return cost;
	}
	
	public double getART() {
		return ART;
	}

	public double getConfidentiality() {
		return confidentiality;
	}

	public double getIntegrity() {
		return integrity;
	}

	public double getAvailability() {
		return availability;
	}


	public void setTrust(double t) {
		this.trust = t;
	}
	
	public Map<String, ArrayList<Double>> getAttackSecurityRisk(){
		return attackSecurityRisk;
	}
	
	public void SecurityRiskAssesment(ArrayList<Attack> attackTypes) {
		calculateAFR();
		
		for(Attack a:attackTypes) {
			ArrayList<Double> securityImpact=a.getSecurityImpact();
			ArrayList<Double> result=new ArrayList<Double>();
			for(int i=0;i<securityImpact.size();i++) {
				result.add(securityImpact.get(i)*this.AFR.get(a.getName()));
				System.out.println("here: "+a.getName()+" "+securityImpact.get(i)+" "+AttackNum.get(a.getName())+" "+this.interval);
				}
			attackSecurityRisk.put(a.getName(),result);
		}
		
		
		for(String s:this.attackSecurityRisk.keySet()) {
			System.out.println("Attack "+s);
			for(double obj:attackSecurityRisk.get(s)) {
				System.out.print(" "+obj);
			}
			System.out.println();
		}
		
		
		
	}
	
	public void calculateAFR() {//Attack Frequency Rate
		String File=this.repositoryAddress;
		BufferedReader reader;
		int dosNum=0,probeNum=0,u2rNum=0,r2lNum=0;
		String delimsComma = "[,]+";
		try {
			reader = new BufferedReader(new FileReader(File));
			String line = reader.readLine();

			while (line != null) {
				if(line.contains("dos")) {
					dosNum++;
					}
				if(line.contains("probe")) {
					probeNum++;
					}
				if(line.contains("u2r")) {
					u2rNum++;
					}
				if(line.contains("r2l")) {
					r2lNum++;
					}
				
				String[] arr  = line.split(delimsComma);
				//System.out.println("arr[0] : "+arr[0]);
				//if(Integer.parseInt(arr[0])==0)
				//	interval++;
				//else
					interval+=Integer.parseInt(arr[0]);
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		this.AttackNum.put("dos",dosNum);
		this.AttackNum.put("probe",probeNum);
		this.AttackNum.put("u2r",u2rNum);
		this.AttackNum.put("r2l",r2lNum);
		
		this.AFR.put("dos",((double)(dosNum/(double)this.interval*100))*(1-0.01)+0.01);
		this.AFR.put("probe",((double)(probeNum/(double)this.interval*100))*(1-0.01)+0.01);
		this.AFR.put("u2r",((double)(u2rNum/(double)this.interval*100))*(1-0.01)+0.01);
		this.AFR.put("r2l",((double)(r2lNum/(double)this.interval*100))*(1-0.01)+0.01);
		
		for(String s:this.AttackNum.keySet())
		System.out.println("Service "+this.repositoryAddress+" Attack: "+s+" number "+this.AttackNum.get(s)+"  AFR: "+this.AFR.get(s)+" Duration: "+this.interval);
	}
	
	public String ServiceToString() {
		String s1=this.getId()+","+this.getName()+","+getHostId();
		return s1;
	}
	
	public void print() {
		System.out.println("Service ID: "+this.getId()+" Name: "+this.getName()+" hostId: "+hostId+" "+ getTrust()+" repositoryAddress:"+repositoryAddress+" ResponseTime: "+ART+" cost:"+cost+" confidentiality,integrity,availability: "+confidentiality+", "+integrity+", "+availability);
	
	}

}
