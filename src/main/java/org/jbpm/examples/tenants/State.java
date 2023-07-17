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

public class State {

	private static int id;
	private String stateName;
	ActionRecord currentAction;
	ArrayList<ActionRecord> ActionRecords = new ArrayList<ActionRecord>();




	public State(ActionRecord ct,ArrayList<ActionRecord> history) {
		System.out.print("Create new State");
		stateName="st"+id;
		id++;
		currentAction=ct;
		ActionRecords=history;
	}
	
	
	public String getName() {
		return stateName;
	}
	//State with history of number of prior violation 
	public boolean isSameState(ActionRecord a,ArrayList<ActionRecord> ActionRs) {
		/*
		System.out.println("		State1: "+" taskName "+a.getTaskName()+" AttackName "+a.getAttackName()+" AttackSeverity "+a.getAttackSeverity()+" numOfpriorViolations "+ActionRs.size());
		System.out.println(" 				-historyactions:");
		for(ActionRecord AR:ActionRs) {
			System.out.println("					"+AR.ToString());
		}
		print();
		*/
		if(this.currentAction.getTaskName().equals(a.getTaskName()) && this.currentAction.getAttackName().equals(a.getAttackName()) && this.ActionRecords.size()==ActionRs.size()) {
			System.out.println("isSameState: true");
			return true;
			}
		else {
			System.out.println("isSameState: false");
			return false;
			}
	}
	
	
	
	/*
	//State with history of exact actions
	public boolean isSameState(ActionRecord a,ArrayList<ActionRecord> ActionRs) {
		if(this.currentAction.getTaskName().equals(a.getTaskName()) && this.currentAction.getAttackName().equals(a.getAttackName()) && this.currentAction.getAttackSeverity().equals(a.getAttackSeverity()) && this.ActionRecords.size()==ActionRs.size() && this.ActionRecords.equals(ActionRs)) {
			System.out.println("isSameState: true");
			return true;
			}
		else {
			System.out.println("isSameState: false");
			return false;
			}
	}
	*/
	
	/*
	public boolean isSameState(String taskN,String AttackN,String AttackS,int numOfV,ArrayList<ActionRecord> ActionRs) {
		System.out.print("isSameState");
		System.out.print("		State1: "+" taskName "+taskN+" AttackName "+AttackN+" AttackSeverity "+AttackS+" numOfpriorViolations "+ActionRs.size());
		System.out.print(" 				-historyactions:");
		for(ActionRecord AR:ActionRs) {
			System.out.print(AR.ToString());
		}
		print();
		
		if(this.currentAction.getTaskName().equals(taskN) && this.currentAction.getAttackName().equals(AttackN) && this.currentAction.getAttackSeverity().equals(AttackS) && this.ActionRecords.size()==numOfV && this.ActionRecords.equals(ActionRs))
			return true;
		else
			return false;
	}
	*/
	/*
	public boolean CheckHistorty(ArrayList<ActionRecord> ActionRs) {
		for(ActionRecord AR:ActionRecords) {
			if(ActionRs.ToString(AR).equals
		}
	}
	*/
	public ActionRecord getCurrentAction() {
		return currentAction; 
	}
	
	public void CheckForUncertainty() {
		System.out.println("CheckForUncertainty");
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
	
	public void print() {
		System.out.println("		-Current State: "+stateName+" taskName "+currentAction.getTaskName()+" AttackName "+this.currentAction.getAttackName()+" AttackSeverity "+this.currentAction.getAttackSeverity()+" numOfpriorViolations "+this.ActionRecords.size());
		System.out.println(" 				-historyactions:");
		for(ActionRecord AR:ActionRecords) {
			System.out.println("					"+AR.ToString());
		}
	}

}
