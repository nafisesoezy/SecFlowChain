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

public class StateChain {

	private static int id;
	private String stateName;
	ChainRecord currentChain;
	ArrayList<ChainRecord> ChainRecords = new ArrayList<ChainRecord>();




	public StateChain(ChainRecord ct,ArrayList<ChainRecord> history) {
		System.out.println("Create new State");
		stateName="st"+id;
		id++;
		currentChain=ct;
		ChainRecords=history;
	}
	
	
	public String getName() {
		return stateName;
	}
	
	public ArrayList<ChainRecord> getHistory() {
		return ChainRecords;
	}
	
	
	public boolean isAdaptedTaskInHistory(String task) {
		for(ChainRecord r:ChainRecords) {
			AdaptationChain ac=r.getSelectedChain();
			if(ac.isAdaptedtask(task))
				return true;
		}
		return false;
	}
	
	public String getPreviousViolatedTask() {
		String previousViolatedTask=null;
		if (!ChainRecords.isEmpty()) {
		    ChainRecord lastElement = ChainRecords.get(ChainRecords.size() - 1);
		    previousViolatedTask=lastElement.getTaskName();
		   }
		return previousViolatedTask;
	}
	
	public Adaptation getLastHistoryAdaptation(String task) {
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
	
	
	//State with history of number of prior violation 
	public boolean isSameState2(ChainRecord a,ArrayList<ChainRecord> ActionRs) {

		/*
		System.out.println("		State1: "+" taskName "+a.getTaskName()+" AttackName "+a.getAttackName()+" AttackSeverity "+a.getAttackSeverity()+" numOfpriorViolations "+ActionRs.size());
		System.out.println(" 				-historyactions:");
		for(ChainRecord AR:ActionRs) {
			System.out.println("					"+AR.ToString());
		}
		print();
		*/
		if(this.currentChain.getTaskName().equals(a.getTaskName()) && this.currentChain.getAttackName().equals(a.getAttackName()) && this.ChainRecords.size()==ActionRs.size()) {
			System.out.println("isSameState: true");
			return true;
			}
		else {
			System.out.println("isSameState: false");
			return false;
			}
	}
	
	public boolean isSameState(ChainRecord a,ArrayList<ChainRecord> ActionRs) {

		/*
		System.out.println("		State1: "+" taskName "+a.getTaskName()+" AttackName "+a.getAttackName()+" AttackSeverity "+a.getAttackSeverity()+" numOfpriorViolations "+ActionRs.size());
		System.out.println(" 				-historyactions:");
		for(ChainRecord AR:ActionRs) {
			System.out.println("					"+AR.ToString());
		}
		print();
		*/
		if(checkTaskAttack(currentChain,a) && checkHistoryEqual(ChainRecords,ActionRs)) {
			System.out.println("isSameState: true");
			return true;
			}
		else {
			System.out.println("isSameState: false");
			return false;
			}
	}
	
	
	public boolean checkHistoryEqual(ArrayList<ChainRecord> list1, ArrayList<ChainRecord> list2) {
        // If sizes are different, tasks cannot be equal
        if (list1.size() != list2.size()) {
            return false;
        }

        // Iterate over both lists simultaneously and compare tasks
        for (int i = 0; i < list1.size(); i++) {
            ChainRecord record1 = list1.get(i);
            ChainRecord record2 = list2.get(i);

            // If tasks are not equal, return false
            if (!checkTaskAttack(record1,record2)) {
                return false;
            }
        }

        // All tasks are equal
        return true;
    }
	
	
	public boolean checkTaskAttack(ChainRecord cr,ChainRecord a) {
		if(cr.getTaskName().equals(a.getTaskName()) && cr.getAttackName().equals(a.getAttackName()))
			return true;
		return false;
	}
	
	
	public boolean IsNextStep(StateChain state) {
		if(ChainRecords.size()!=0) {
			ChainRecord lastElement = ChainRecords.get(ChainRecords.size() - 1);
			if(checkTaskAttack(lastElement,state.getCurrentChain()))
				return true; 
		}
		
		return false;
	}
	
	/*
	public boolean CheckStateChain(ChainRecord a) {
		if(a.getTaskName().equals(a.getTaskName()) && this.currentChain.getAttackName().equals(a.getAttackName()) )
			returm true;
		return false;
	}
	*/
	
	/*
	//State with history of exact actions
	public boolean isSameState(ChainRecord a,ArrayList<ChainRecord> ActionRs) {
		if(this.currentChain.getTaskName().equals(a.getTaskName()) && this.currentChain.getAttackName().equals(a.getAttackName()) && this.currentChain.getAttackSeverity().equals(a.getAttackSeverity()) && this.ChainRecords.size()==ActionRs.size() && this.ChainRecords.equals(ActionRs)) {
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
	public boolean isSameState(String taskN,String AttackN,String AttackS,int numOfV,ArrayList<ChainRecord> ActionRs) {
		System.out.print("isSameState");
		System.out.print("		State1: "+" taskName "+taskN+" AttackName "+AttackN+" AttackSeverity "+AttackS+" numOfpriorViolations "+ActionRs.size());
		System.out.print(" 				-historyactions:");
		for(ChainRecord AR:ActionRs) {
			System.out.print(AR.ToString());
		}
		print();
		
		if(this.currentChain.getTaskName().equals(taskN) && this.currentChain.getAttackName().equals(AttackN) && this.currentChain.getAttackSeverity().equals(AttackS) && this.ChainRecords.size()==numOfV && this.ChainRecords.equals(ActionRs))
			return true;
		else
			return false;
	}
	*/
	/*
	public boolean CheckHistorty(ArrayList<ChainRecord> ActionRs) {
		for(ChainRecord AR:ChainRecords) {
			if(ActionRs.ToString(AR).equals
		}
	}
	*/
	public ChainRecord getCurrentChain() {
		return currentChain; 
	}
	
	public void CheckForUncertainty() {
		System.out.println("CheckForUncertainty");
		//if(ChainRecords.size()==1 && !ChainRecordsContains("Insert")) {
		if(ChainRecords.size()==1) {
			currentChain.setUncertainprice(2);
		}
		/*
		else if(ChainRecords.size()==1 && ChainRecordsContains("Insert")) {
			System.out.print("CheckForUncertainty: Insert");
			currentChain.setUncertainprice(1);
		}
		*/
		else if(ChainRecords.size()==2) {
			currentChain.setUncertainprice(2);
		}
		else if(ChainRecords.size()>2 && ChainRecords.size()<=5) {
			currentChain.setUncertainprice(1);
		}
		else if(ChainRecords.size()>5) {
			currentChain.setUncertainprice(1);
		}
		/*
		for(ChainRecord AR:ChainRecords) {
			System.out.print(AR.ToString());
		}*/
	}
	
	public boolean ChainRecordsContains(AdaptationChain s) {
		if(ChainRecords.size()!=0)
			for(ChainRecord A:ChainRecords) {
				if(A.getSelectedChain()!=null)
					if(A.getSelectedChain().equals(s))
						return true;
			}
		return false;
	}
	
	public void print() {
		System.out.println("		-Current State: "+stateName+" taskName "+currentChain.getTaskName()+" AttackName "+this.currentChain.getAttackName()+" AttackSeverity "+this.currentChain.getAttackSeverity()+" numOfpriorViolations "+this.ChainRecords.size());
		System.out.println(" 				-historyactions:");
		for(ChainRecord AR:ChainRecords) {
			System.out.println("					"+AR.ToString());
		}
	}

}
