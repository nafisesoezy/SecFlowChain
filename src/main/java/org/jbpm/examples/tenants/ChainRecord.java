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

public class ChainRecord {

	//private static int id;
	//private ArrayList<Double> securityImpact = new ArrayList<Double>();//securityImpact[0]:confidentiality securityImpact[1]:Integrity securityImpact[2]:availability
	private String taskName;
	private String AttackName;
	private String AttackSeverity;
	private double AttackScore;
	private AdaptationChain selectedChain;
	
	
	// this is the parameters for the entire workflow (EntireTotalCost)
	private double price;
	private double time;
	private double value;
	private double mitigationScore;
	private double totalCost;
	
	//private double Nprice;
	//private double Ntime;
	//private double Nvalue;
	//private double NmitigationScore;
	
	private double Uncertainprice;
	private double Uncertaintime;
	private double Uncertainvalue;
	private double UncertainmitigationScore;
	private double UncertaintotalCost;
	int index;
	double UncertaintyFactor=30;
	
	private double workflowTime,workflowPrice,workflowValue,workflowMitigationScore;
	StateChain state;

	public ChainRecord(String taskN,String AttackN,String AttackS, double score,AdaptationChain selectedA,ArrayList<Double> SI ,double t,double p,double v,double MS) {
		taskName=taskN;
		AttackName=AttackN;
		AttackSeverity=AttackS;
		AttackScore=score;
		selectedChain=selectedA;
		//securityImpact=SI;
		time=t;
		price=p;
		value=v;
		mitigationScore=MS;
	}
	
	public ChainRecord(String taskN,String AttackN,String AttackS, double score) {
		taskName=taskN;
		AttackName=AttackN;
		AttackSeverity=AttackS;
		AttackScore=score;
		
	}

	public void setPrameters(AdaptationChain selectedA,double tc,double t,double p,double v,double MS,StateChain st) {
		selectedChain=selectedA;
		totalCost=tc;
		time=t;
		price=p;
		value=v;
		mitigationScore=MS;
		state=st;
		

	}
	
	public void setAdaptationChain(AdaptationChain selectedA) {
		selectedChain=selectedA;
	}
	
	public void setPrameters(AdaptationChain selectedA,double tc,double t,double p,double v,double MS) {
		selectedChain=selectedA;
		totalCost=tc;
		time=t;
		price=p;
		value=v;
		mitigationScore=MS;

	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public String getAttackName() {
		return AttackName;
	}
	
	public String getAttackSeverity() {
		return AttackSeverity;
	}
	public AdaptationChain getSelectedChain() {
		return selectedChain;
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
	
	public void setTotalCost(double cost) {
		totalCost=cost;
	}
	
	public double getTotalUncertainCost(double priceWeight,double timeWeight,double valueWeight,double securityWeight) {
		return (priceWeight*(price+this.index*price))+(timeWeight*(time+this.index*time))-(valueWeight*value)-(securityWeight*mitigationScore);

	}
	
	
	public StateChain getState() {
		return state;
	}
	
	public void setState(StateChain st) {
		state=st;
	}
	/*
	public ArrayList<Double> getSecurityImpact(){
		return securityImpact;
	}
*/
	
	public double getMitigationScore() {
		return mitigationScore;
	}

	
	public void setUncertainprice(int i) {
		this.index=i;
		System.out.println("index "+index);
		if(this.price!=0)
			Uncertainprice=this.price/index;
		else
			Uncertainprice=UncertaintyFactor/index;
		if(this.time!=0)
			Uncertaintime=this.time/index;
		else
			Uncertaintime=UncertaintyFactor/index;
		//UncertainmitigationScore=this.mitigationScore/index;
		System.out.println("Uncertaintime "+Uncertaintime);
	}
	
	public double Uncertainprice() {
		return Uncertainprice;
	}
	
	public double Uncertaintime() {
		return Uncertaintime;
	}
	
	public void setWorkflowTime(double t) {
		workflowTime=t;
	}
	public void setWorkflowPrice(double t) {
		workflowPrice=t;
	}
	public void setWorkflowValue(double t) {
		workflowValue=t;
	}
	public void setWorkflowMitigationScore(double t) {
		workflowMitigationScore=t;
	}
	
	
	public String ToString() {
		return 	taskName+","+ AttackName+","+AttackSeverity+","+ AttackScore+","+selectedChain.ToString()+"\n";
	}
	
	public String ToString1() {
		return 	taskName+","+ AttackName+","+AttackSeverity+","+ AttackScore+","+selectedChain.ToString()+"\n";
	}

	
	public void print(String type) {
		System.out.println("ChainRecord "+type);
		System.out.print("Adapation Chain: "+ToString()+" time "+time+" price "+price+" value "+value+" totalCost "+totalCost);

	}
	
	public void print() {
		System.out.println("ChainRecord ");
		System.out.print("Adapation Chain: "+ToString()+" time "+time+" price "+price+" value "+value+" totalCost "+totalCost);
		System.out.println("********** ");

	}

}
