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
//import org.deeplearning4j.rl4j.space.DiscreteSpace;

public class QLearning  {
	

	Map<String, Double> actionReward = new HashMap<String, Double>();//q-table
	ArrayList<String> actions = new ArrayList<String>();
	ArrayList<State> states = new ArrayList<State>();
	String predictedAction; 
	State currentState;
	//adaptation model trainer
	double explorationRate=0.5,learningRate=0.2,discountFactor=0.9;//alpha:learningRate,  gamma:discountFactor,  epsilon:explorationRate
	
	 Map<State, Map<String, Double>> qTable = new HashMap<>();  // Q-table=state->action,reward


	public QLearning() {
	
        //this.qTable = new HashMap<>();
 
    }
	
	
	public String learn(ArrayList<String> acts, ActionRecord A,String randomAction,String selectedAction,ArrayList<ActionRecord> ActionRs) {
		currentState=null;
		predictedAction="";
		System.out.println("Start learning and predicting");
		actions=acts;
		
		//if this state is a new state
		for(State st:states) {
			if(st.isSameState(A,ActionRs))
				currentState=st;
		}
		if(currentState==null) {
			currentState=new State(A,ActionRs);
			this.states.add(currentState);
			}
		 //Map<Integer, Double> stateQValues = qTable.getOrDefault(currentState, new HashMap<>());

	        // Explore or exploit?
	        if (Math.random() < explorationRate) {
	            // Explore: Select a random action
	        	predictedAction=randomAction;
	        } else {
	            // Exploit: Select the action with the highest Q-value
	        	predictedAction=selectedAction;
	        }
			System.out.println("Current predicted action: "+this.predictedAction+" randomAction "+randomAction+" selectedAction "+selectedAction);

	        this.print();
	        
	        return predictedAction;
	        
	}
	
	public String learn(ArrayList<String> acts, ActionRecord A,String randomAction,ArrayList<ActionRecord> ActionRs) {
		currentState=null;
		predictedAction="";
		System.out.println("Start learning and predicting");
		actions=acts;
		
		//if this state is a new state
		for(State st:states) {
			if(st.isSameState(A,ActionRs))
				currentState=st;
		}
		if(currentState==null) {
			currentState=new State(A,ActionRs);
			this.states.add(currentState);
			
			//add value 0 reward for all possible actions for this state:


			Map<String, Double> stateActions = new HashMap<>();
			for (String action : acts) {
			    stateActions.put(action, 0.0);
			}

			qTable.put(currentState, stateActions);
			
		}
		
		
		 //Map<Integer, Double> stateQValues = qTable.getOrDefault(currentState, new HashMap<>());

	        // Explore or exploit?
	        if (Math.random() < explorationRate) {
	            // Explore: Select a random action
	        	predictedAction=randomAction;
	        } else {
	            // Exploit: Select the action with the highest Q-value

	        	if (qTable.containsKey(currentState)) {
	        	    Map<String, Double> stateActions = qTable.get(currentState);

	        	    double maxReward = Double.NEGATIVE_INFINITY;
	        	    String maxRewardAction = null;

	        	    for (Map.Entry<String, Double> entry : stateActions.entrySet()) {
	        	        String action = entry.getKey();
	        	        double reward = entry.getValue();

	        	        if (reward > maxReward) {
	        	            maxReward = reward;
	        	            maxRewardAction = action;
	        	        }
	        	    }

	        	    if (maxRewardAction != null) {
	        	        System.out.println("Maximum reward for " + currentState.getName() + ": " + maxReward);
	        	        System.out.println("Action with maximum reward: " + maxRewardAction);
	        	        predictedAction=maxRewardAction;
	        	    } else {
	        	        System.out.println("No actions found for " + currentState.getName());
	        	    }
	        	} else {
	        	    System.out.println("State not found in the Q-table.");
	        	}

	        	//predictedAction=selectedAction;
	        }
	
			System.out.println("Current predicted action: "+this.predictedAction+" randomAction "+randomAction);

	        this.print();
	        
	        return predictedAction;
	        
	}
	

	
/*	   public void updateQValue(String state, int action, double reward, String nextState) {
	        Map<Integer, Double> stateQValues = qTable.computeIfAbsent(state, k -> new HashMap<>());
	        //double currentQValue = stateQValues.getOrDefault(action, 0.0);
	        currentQValue + learningRate * (reward + discountFactor * maxQValue - currentQValue);
	        }

	        // Update the Q-value for the current state-action pair
	        double newQValue = (1 - learningRate) * currentQValue +
	                learningRate * (reward + discountFactor * maxNextQValue);
	        stateQValues.put(action, newQValue);
	    }
*/
	
	public void setReward(double totalCost,State st) {
		
		if(st!=null) {
			System.out.println("set Reward: "+totalCost);
			double reward=totalCost;
			st.print();
			
			//uddate qTable
			double currentQValue=0;
			if (qTable.containsKey(currentState)) {
			    Map<String, Double> stateActions = qTable.get(currentState);
			    if (stateActions.containsKey(predictedAction)) {
			         currentQValue = stateActions.get(predictedAction);
			        }
			    
			    }
			 Map<String, Double> state1Actions = new HashMap<>();
			 double maxQValue=0;
			 
		     state1Actions.put(predictedAction, currentQValue + learningRate * (reward + discountFactor * maxQValue - currentQValue));
		     qTable.put(currentState, state1Actions);
			printqTable();

			
			//qLearning.updateQValue(st, predictedAction, totalCost, nextState);
			}
		else
			System.out.println("state is null");
		
		

	    }
	

	
	
	   public State getState(ActionRecord Action,ArrayList<ActionRecord> ActionRecords) {
		   State currentState=null;
		   for(State st:states) {
				if(st.isSameState(Action,ActionRecords)) {
					System.out.println("isSameState: true ");
					currentState=st;
					}
			}
		   return currentState;
	   }
	   public void print() {
			System.out.println("Current Qlearning: ");
			currentState.print();
			System.out.println("Current selected action: "+this.predictedAction);

		}
	
	public void printqTable() {
		System.out.println("qTable:");
		for (Map.Entry<State, Map<String, Double>> entry : qTable.entrySet()) {
		    State state = entry.getKey();
		    Map<String, Double> stateActions = entry.getValue();
		    
		    System.out.println("State: " + state.getName());
		    state.print();
		    System.out.println("Actions and Rewards:");

		    for (Map.Entry<String, Double> actionEntry : stateActions.entrySet()) {
		        String action = actionEntry.getKey();
		        double reward = actionEntry.getValue();
		        
		        System.out.println("  Action: " + action + ", Reward: " + reward);
		    }
		    System.out.println();
		}
	}

}
