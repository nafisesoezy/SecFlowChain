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

public class Adaptation {
	
	private String name;
	private String task;
	private ArrayList<Double> securityImpact = new ArrayList<Double>();//securityImpact[0]:confidentiality securityImpact[1]:Integrity securityImpact[2]:availability
	private double price;
	private double time;
	private double value;
	private double mitigationScore;
	
	private double Nprice;
	private double Ntime;
	private double Nvalue;
	private double NmitigationScore;
	
	private double totalCost;

	public Adaptation(String name, String t) {
		setName(name);
		setTask(t);
		setSecurityImpact();
	}
	
	
	public Adaptation(String name, double t,double p,double v) {
		setName(name);
		time=t;
		price=p;
		value=v;
		setSecurityImpact();
	}
	
	public void setSecurityImpact() {
		//Edoc:
		if(name.equals("Late")) {
			securityImpact.add(0.7);
			securityImpact.add(0.6);	
			securityImpact.add(0.8);	
		}
		else if(name.equals("ReExecute")) {
			securityImpact.add(0.8);
			securityImpact.add(0.9);
			securityImpact.add(0.7);	
		}
		else if(name.equals("Skip")) {
			securityImpact.add(0.5);
			securityImpact.add(0.4);	
			securityImpact.add(0.6);	
		}
		else if(name.equals("Redundancy")) {
			securityImpact.add(0.9);
			securityImpact.add(0.8);	
			securityImpact.add(0.7);	
		}
		else if(name.equals("ReConfiguration")) {
			securityImpact.add(0.6);
			securityImpact.add(0.7);	
			securityImpact.add(0.5);	
		}
		else{
			securityImpact.add(0.2);
			securityImpact.add(0.2);	
			securityImpact.add(0.2);
		}
	}

	
	public String getName() {
		return name;
	}

	
	public String getTask() {
		return task;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	public void setTask(String t) {
		this.task = t;
	}
	
	public void setPriceTimeValue(double t,double p,double v) {
		time=t;
		price=p;
		value=v;
		setSecurityImpact();
	}
	
	public double getPrice() {
		return price;
	}
	public double getTime() {
		return time;
	}
	public double getValue() {
		return value;
	}
	
	
	
	public double getTotalCost() {
		return totalCost;
	}
	
	public void setNormalized(double np,double nt,double nv,double nms) {
		Nprice=np;
		Ntime=nt;
		Nvalue=nv;
		NmitigationScore=nms;
	}
	
	
	public double getNormalizedprice() {
		return Nprice;
	}
	
	public double getNormalizedtime() {
		return Ntime;
	}
	
	public double getNormalizedvalue() {
		return Nvalue;
	}
	
	public double getNormalizedMS() {
		return NmitigationScore;
	}
	
	public void setTotalCost(double cost) {
		totalCost=cost;
	}
	public ArrayList<Double> getSecurityImpact(){
		return securityImpact;
	}
	
	public double calculateMitigationScore(ServiceTask s,Attack attack) {
		mitigationScore=0;
		for(int i=0;i<3;i++) {
			mitigationScore+=(1-s.getSecurityObjectiveRequirements().get(i)*attack.getSecurityImpact().get(i))*this.getSecurityImpact().get(i);
			//System.out.println("		-s.getSecurityObjectiveRequirements().get(i): "+s.getSecurityObjectiveRequirements().get(i)+" attack.getSecurityImpact().get(i): "+attack.getSecurityImpact().get(i)+" option.getSecurityImpact().get(i): "+option.getSecurityImpact().get(i)+" mitigationScore: "+mitigationScore);
		}
		return mitigationScore;
	}
	
	//compute Mitigation Score for the chain(based on MS_{aa_{t_i},vt,a_k} = (1 - obj_{t_i} \cdot obj_{a_k}) \cdot obj_{MI_{aa}}\cdot obj_{SDM[vt][t_i]}
	public double calculateMitigationScoreInChain(ServiceTask s,Attack attack,ServiceTask vt,Workflow w) {
		//System.out.println("calculateMitigationScoreInChain");
		mitigationScore=0;
		for(int i=0;i<3;i++) {
			double dependency=0;
			
			if(i==0)
				dependency=w.getConfidentialityMatrix(w.getTaskIndexInWorkflow(s.getTaskId()),w.getTaskIndexInWorkflow(vt.getTaskId()));
			if(i==1)
				dependency=w.getIntegrityMatrix(w.getTaskIndexInWorkflow(s.getTaskId()),w.getTaskIndexInWorkflow(vt.getTaskId()));
			if(i==2)
				dependency=w.getAvailabilityMatrix(w.getTaskIndexInWorkflow(s.getTaskId()),w.getTaskIndexInWorkflow(vt.getTaskId()));
			
			mitigationScore+=(1-s.getSecurityObjectiveRequirements().get(i)*attack.getSecurityImpact().get(i))*this.getSecurityImpact().get(i)*dependency;
			//System.out.println("		-s.getSecurityObjectiveRequirements().get(i): "+s.getSecurityObjectiveRequirements().get(i)+" attack.getSecurityImpact().get(i): "+attack.getSecurityImpact().get(i)+" option.getSecurityImpact().get(i): "+option.getSecurityImpact().get(i)+" mitigationScore: "+mitigationScore);
		}
		//System.out.println("calculateMitigationScoreInChain "+mitigationScore);
		return mitigationScore;
	}
	
	public double getMitigationScore() {
		return mitigationScore;
	}
	
	public void print() {
		System.out.println("Adapation: "+this.getName()+" time "+time+" price "+price+" value "+value+" mitigationScore "+mitigationScore+" totalCost "+totalCost);

	}
	
	public void smallPrint() {
		System.out.println("	-Adapation: "+this.getName()+" in "+task);

	}

}
