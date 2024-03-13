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

public class AdaptationChain {
	
	private int ID;
	private String violatedTask;
	private ArrayList<Adaptation> adapts=new ArrayList<Adaptation>();

	private double totalCost;
	private double entireTotalCost;
	private double sofarTotalCost;
	
	// Calculate the parameters, taking into account only the tasks involved in the adaptation chain.
	private double price;
	private double time;
	private double value;
	private double mitigationScore;
	
	// Calculate the parameters by considering all tasks in the entire workflow, encompassing both those involved in the adaptation chain and those not included in it.
	private double entirePrice;
	private double entireTime;
	private double entireValue;
	private double entireMitigationScore;
	
	// Compute the parameters, considering only tasks that meet two conditions simultaneously: 
	// 1) have already been visited up to the violated task, and 
	// 2) are part of the adaptation chain.	
	private double sofarPrice;
	private double sofarTime;
	private double sofarValue;
	private double sofarMitigationScore;
	
	// based on the computed parameters in the RL(q learning) 
	private double finalPriceRL;
	private double finalTimeRL;
	private double finalValueRL;
	private double finalMitigationScoreRL;

	public AdaptationChain(int id,String vt,ArrayList<Adaptation> a) {
		ID=id;
		violatedTask=vt;
		adapts=a;
		//print();
	}
	
	public int getID() {
		return ID;
	}
	
	
	public String getViolatedTask() {
		return violatedTask;
	}
	
	public ArrayList<Adaptation> getAdaptations() {
		return adapts;
	}
	
	public ArrayList<Adaptation> getSubsequentAdaptations(Workflow w){
		ArrayList<Adaptation> subsequentAdaptationsinChain = new ArrayList<Adaptation>();
		ArrayList<String> subsequentTasks=w.getSubsequentTasks(this.violatedTask);
		boolean check=false;	
		for (int i = 0; i < subsequentTasks.size(); i++) {
            String task=subsequentTasks.get(i);
            	for(Adaptation aa: this.getAdaptations()) 
                	if(task.equals(aa.getTask())) 
                		subsequentAdaptationsinChain.add(aa);
          
         }
		
		return subsequentAdaptationsinChain;
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
	public double getMitigationScore() {
		return mitigationScore;
	}
	
	
	
	public void setFinalRLParameters(double p,double t,double v,double ms) {
		finalPriceRL=p;
		finalTimeRL=t;
		finalValueRL=v;
		finalMitigationScoreRL=ms;
	}
	
	public double getfinalPriceRL() {
		return finalPriceRL;
	}
	public double getFinalTimeRL() {
		return finalTimeRL;
	}
	public double getFinalValueRL() {
		return finalValueRL;
	}
	public double getFinalMitigationScoreRL() {
		return finalMitigationScoreRL;
	}
	
	
	
	public double getTotalCost() {
		return totalCost;
	}
	
	
	public double getEntirePrice() {
		return entirePrice;
	}
	public double getEntireTime() {
		return entireTime;
	}
	public double getEntireValue() {
		return entireValue;
	}
	public double getEntireMitigationScore() {
		return entireMitigationScore;
	}
	public double getEntireTotalCost() {
		return entireTotalCost;
	}
	
	
	public double getsofarPrice() {
		return sofarPrice;
	}
	public double getsofarTime() {
		return sofarTime;
	}
	public double getsofarValue() {
		return sofarValue;
	}
	public double getsofarMitigationScore() {
		return sofarMitigationScore;
	}
	public double getsofarTotalCost() {
		return sofarTotalCost;
	}
	
	
	public boolean Equals(AdaptationChain ac) {
		for(Adaptation aa:ac.getAdaptations()) {
			Adaptation currentAdaptation = getAdaptationFortask(aa.getTask());
			if(currentAdaptation!=null) {
				if(!aa.getName().equals(currentAdaptation.getName())) {
					return false;
				}
			}
			else
				return false;
		}
		return true;
	}
	
	public boolean isAdaptedtask(String task) {
		for(Adaptation aa: this.getAdaptations()) {
        	if(task.equals(aa.getTask())) {
        		return true;
        	}
        }
		//for the tasks that are not invoulved in the adaptation consier a fix default time, price and value(mitigation score for these tasks is 0)
		return false;
	}
	

	
	public Adaptation getAdaptationFortask(String task) {
		for(Adaptation aa: this.getAdaptations()) {
        	if(task.equals(aa.getTask())) {
        		return aa;
        	}
        }
		//for the tasks that are not invoulved in the adaptation consier a fix default time, price and value(mitigation score for these tasks is 0)
		return null;
	}
	
	//compute parameters for the entire workflow after implementing the adaptation chain(for the task that are not involved in the chain consider default paremeters)
	public void ComputeCostForEntireWorkflow(Workflow w,double priceWeight,double timeWeight,double valueWeight,double securityWeight) {
		//System.out.println("ComputeCostForEntireWorkflow:");
	    ArrayList<String> coveredParallelTasks=new ArrayList<String>();
		double p=0,t=0,v=0,ms=0;
		for (int i = 0; i < w.getServiceTasks().size(); i++) {
            String task=w.getServiceTasks().get(i);
			//System.out.println("task:"+task);
			if(isAdaptedtask(task)) {
				//System.out.println("	-this task is adapted!!:");
				Adaptation aa=getAdaptationFortask(task);
				p+=aa.getPrice();
				v+=aa.getValue();
				ms+=aa.getMitigationScore();
				if(w.getParallelTasks(aa.getTask()).size()!=0) {
					if(!coveredParallelTasks.contains(aa.getTask())) {
						//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
						t=+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w);
						coveredParallelTasks.add(task);
						coveredParallelTasks.addAll(w.getParallelTasks(aa.getTask()));
					}
				}
				else {
					//System.out.println("	-this task is not parallel with any task!!:"+aa.getTime());
					t+=aa.getTime();
					}
            }
			//for the tasks that are not invoulved in the adaptation, consier a fix default time, price and value(mitigation score for these tasks is 0)
			else {
				//System.out.println("	-this task is not involved in adaptation chain!!:");
				p+=w.getDefaultPrice(task);
				v+=w.getDefaultValue(task);
				if(w.getParallelTasks(task).size()!=0) {
					if(!coveredParallelTasks.contains(task)) {
						//System.out.println("	-this task is parallel with some tasks!!  max: "+MAXDefaulttimeForParallelTasks(task,w.getParallelTasks(task),w)+" task: "+w.getDefaultTime(task));
						t+=MAXDefaulttimeForParallelTasks(task,w.getParallelTasks(task),w);
						coveredParallelTasks.add(task);
						coveredParallelTasks.addAll(w.getParallelTasks(task));
					}
				}
				else {
					//System.out.println("	-this task is not parallel with any task!!:"+w.getDefaultTime(task));
					t+=w.getDefaultTime(task);
				}
			}
          }
		
		
		this.entirePrice=p;
		this.entireTime=t;
		this.entireValue=v;
		this.entireMitigationScore=ms;
		
		entireTotalCost=(priceWeight*entirePrice)+(timeWeight*entireTime)-(valueWeight*entireValue)-(securityWeight*entireMitigationScore);
	}
	
	
	// it return the parameters(time,proce,value, mitigation score, and totalcost) until the violated task and don't include the upcoming adaptations in the next tasks in the workflow
	public void ComputeCostSofar(Workflow w,double priceWeight,double timeWeight,double valueWeight,double securityWeight) {
		System.out.println("Compute Cost So far");
	    ArrayList<String> coveredParallelTasks=new ArrayList<String>();
		double p=0,t=0,v=0,ms=0;
		boolean sofar=false;
		for (int i = 0; i < w.getServiceTasks().size(); i++) {
            String task=w.getServiceTasks().get(i);
			System.out.println("task:"+task);
            if(!sofar) {
				if(isAdaptedtask(task)) {
					System.out.println("	-this task is adapted!!:");
					Adaptation aa=getAdaptationFortask(task);
					p+=aa.getPrice();
					v+=aa.getValue();
					ms+=aa.getMitigationScore();
					if(w.getParallelTasks(aa.getTask()).size()!=0) {
						if(!coveredParallelTasks.contains(aa.getTask())) {
							//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
							t=+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w);
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
				
	          }
            else {
            	System.out.println("cut the rest");
            	break;
            	}
		}
		
		this.sofarPrice=p;
		this.sofarTime=t;
		this.sofarValue=v;
		this.sofarMitigationScore=ms;

		
		sofarTotalCost=(priceWeight*sofarPrice)+(timeWeight*sofarTime)-(valueWeight*sofarValue)-(securityWeight*sofarMitigationScore);
	}
	
	
	public double MAXDefaulttimeForParallelTasks(String s, ArrayList<String> parallelTasks,Workflow w) {
		double max=getTimeForTask(s,w);
		
		for(String t: parallelTasks) {
			if(max<getTimeForTask(t,w))
					max=getTimeForTask(t,w);
		}
		return max;
	}
	
	
	public double getTimeForTask(String s,Workflow w) {
		double time=0;
		if(isAdaptedtask(s)) {
			Adaptation aa=getAdaptationFortask(s);
			time=aa.getTime();
			}
		else
			time=w.getDefaultTime(s);
		return time;
	}
	
	//compute parameters for only the adaptation chain
	public void ComputeCost(Workflow w,double priceWeight,double timeWeight,double valueWeight,double securityWeight) {
		ArrayList<String> coveredParallelTasks=new ArrayList<String>();
		double p=0,t=0,v=0,ms=0;
		for(Adaptation aa: this.getAdaptations()) {
			//aa.smallPrint();
			p+=aa.getPrice();
			v+=aa.getValue();
			ms+=aa.getMitigationScore();
			if(w.getParallelTasks(aa.getTask()).size()!=0) {
				if(!coveredParallelTasks.contains(aa.getTask())) {
					t+=MAXtimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w);
					coveredParallelTasks.add(aa.getTask());
					coveredParallelTasks.addAll(w.getParallelTasks(aa.getTask()));
					//System.out.println("max: "+MAXtimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask())));
				}
			}
			else
				t+=aa.getTime();
		}
		price=p;
		time=t;
		value=v;
		mitigationScore=ms;
		
		totalCost=(priceWeight*price)+(timeWeight*time)-(valueWeight*value)-(securityWeight*mitigationScore);

	}
	
	public double MAXtimeForParallelTasks(String s, ArrayList<String> parallelTasks,Workflow w) {
		double max=getTimeForTask(s,w);
		for(Adaptation aa: this.getAdaptations()) {
			if(aa.getTask().equals(s) || parallelTasks.contains(aa.getTask()))
				if(max<aa.getTime())
					max=aa.getTime();
		}
		return max;
	}
	

	

	
	/*
	public AdaptationChain(String name, double t,double p,double v) {
		setName(name);
		time=t;
		price=p;
		value=v;
		setSecurityImpact();
	}
	*/
	public String ToString() {
		 StringBuilder aas = new StringBuilder();
		for(Adaptation aa: this.adapts)
			 aas.append("("+aa.getName()+","+aa.getTask()+")");
		return aas.toString();
	}

	public void print() {
		System.out.println("Chain: "+this.ID+" For violatedTask: "+violatedTask);
		System.out.println("           -ChainCost:     time "+time+" price "+price+" value "+value+" mitigationScore "+mitigationScore+" totalCost "+totalCost);
		System.out.println("           -EntireCost(w): time "+entireTime+" price "+entirePrice+" value "+entireValue+" mitigationScore "+entireMitigationScore+" totalCost "+entireTotalCost);
		System.out.println("           -SofarCost(w): time "+sofarTime+" price "+sofarPrice+" value "+sofarValue+" mitigationScore "+sofarMitigationScore+" totalCost "+sofarTotalCost);
		printChain();
	}
	
	public void printChain() {
		for(Adaptation aa:adapts) {
			aa.smallPrint();
		}
	}

}
