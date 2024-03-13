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

public class QLearningChain  {
	

	
	ArrayList<StateChain> states = new ArrayList<StateChain>();
	AdaptationChain predictedAdaptationChain; 
	Map<AdaptationChain, Double> minNextValue;
	StateChain currentState;
	//adaptation model trainer
	double explorationRate=0.05,learningRate=0.2,discountFactor=0.9;//alpha:learningRate,  gamma:discountFactor,  epsilon:explorationRate
	
	 Map<StateChain, Map<AdaptationChain, Double>> qTable = new HashMap<>();  // Q-table=state->action,qvalue


	public QLearningChain() {
	
        //this.qTable = new HashMap<>();
 
    }
	
	
	//AdaptationChainSet: set of availbale adaptation chain for this violated task
	//ChainRecord A: includes the  ChainRecord(s.getTaskId(),attack.getName(),getSeverity(attackScore),attackScore)
	public AdaptationChain learnChain(StateChain ct,ArrayList<StateChain> ss,Map<StateChain, Map<AdaptationChain, Double>> qt,ArrayList<AdaptationChain> AdaptationChainSet, ChainRecord A,AdaptationChain randomAdaptationChain,ArrayList<ChainRecord> ChainRs,Workflow workflow,double priceWeight,double timeWeight,double valueWeight,double securityWeight) {
		/*
	    System.out.println("*****************ChainRs*****************");
		for(ChainRecord r: ChainRs) {
			r.print();
		}
	    System.out.println("*****************ChainRs*****************");
		 */
		states=ss;
		currentState=ct;
		qTable=qt;
		
		Map<AdaptationChain, Double> actionReward = new HashMap<AdaptationChain, Double>();
		ArrayList<AdaptationChain> actionsSet=new ArrayList<AdaptationChain>();
		minNextValue = new HashMap<AdaptationChain, Double>();
		predictedAdaptationChain=null;
		
		actionsSet=AdaptationChainSet;
		
		//1-take all possible actions in the current state
		//2-Compute reward for this state for all of actions (state,ac1),(state,ac2),...
		//3-Compute minNextValue for this state for all of actions
		//4-update qtable for the current state for all possible actions
		Map<AdaptationChain, Double> newQValue = new HashMap<AdaptationChain, Double>();
	    //System.out.println("*****************qtable*****************");
		for (AdaptationChain adaptationChain : AdaptationChainSet) {
		    //System.out.println("Compure reward for chain: "+adaptationChain.getID());
			//adaptationChain.printChain();
			double cost=ComputeReward(currentState, adaptationChain,workflow,priceWeight,timeWeight,valueWeight,securityWeight);
			actionReward.put(adaptationChain,cost);
			//way1:
			double newq=cost+discountFactor*minNextValue.get(adaptationChain);
			//newQValue.put(adaptationChain,newq1);
			
			//way2
			double newq2=cost+discountFactor*ComputeMinQValue(adaptationChain);
			newQValue.put(adaptationChain,newq2);
		    //System.out.println(" 		-cost: " + cost+" newQValue(way1): "+newq+" newQValue(way2): "+newq2);
		    //System.out.println("%%%%%%%%%%%%%%%%%%%");

		}
	

		qTable.put(currentState, newQValue);
		
	    //System.out.println("**********************************");
		 
		
		
		 //Map<Integer, Double> stateQValues = qTable.getOrDefault(currentState, new HashMap<>());

	        // Explore or exploit?
	        if (Math.random() < explorationRate) {
	            // Explore: Select a random adaptationChain
	        	predictedAdaptationChain=randomAdaptationChain;
	        } else {
	            // Exploit: Select the adaptationChain with the minimum Q-value
	        	
	        	
	        	if (qTable.containsKey(currentState)) {
	        	    Map<AdaptationChain, Double> stateAdaptationChains = qTable.get(currentState);

	        	    double minReward = Double.MAX_VALUE;
	        	    AdaptationChain minRewardAdaptationChain = null;

	        	    for (Map.Entry<AdaptationChain, Double> entry : stateAdaptationChains.entrySet()) {
	        	    	AdaptationChain adaptationChain = entry.getKey();
	        	        double reward = entry.getValue();
	        	        //System.out.println("reward" + reward+" for chain "+adaptationChain.getID());
	        	        if (reward < minReward) {
	        	            minReward = reward;
	        	            minRewardAdaptationChain = adaptationChain;
	        	        }
	        	    }

	        	    if (minRewardAdaptationChain != null) {
	        	        //System.out.println("minimum reward for " + currentState.getName() + ": " + minReward);
	        	        //System.out.println("Adaptation Chain with minimum reward: ");
	        	        //minRewardAdaptationChain.print();
	        	        predictedAdaptationChain=minRewardAdaptationChain;
	        	    } else {
	        	        //System.out.println("No adaptationChains found for " + currentState.getName());
	        	    }
	        	} else {
	        	    //System.out.println("State not found in the Q-table.");
	        	}

	        	//predictedAdaptationChain=selectedAdaptationChain;
	        }
	
			//System.out.println("Current predicted adaptationChain: ");
			//this.predictedAdaptationChain.print();
			//System.out.println(" randomAdaptationChain ");
			//randomAdaptationChain.print();
			
			
			//updateQtable();

	        //this.print();
	        
	        return predictedAdaptationChain;
	        
	}
	
	
	public double ComputeMinQValue(AdaptationChain adaptationChain){
		ArrayList<StateChain> nextStates = new ArrayList<StateChain>();
		for(StateChain state: states) {
			if(state.IsNextStep(currentState))
				nextStates.add(state);
		}
		double cost=0;
		if(nextStates.size()==0) {
			System.out.println(" the qTable for the next State of the current state is still empty ");
			cost=0;
		}
		else{
			System.out.println(" the qTable for the next States is not empty! size: "+nextStates.size());
			double min= Double.MAX_VALUE;
			for(StateChain s:nextStates) {
				Map<AdaptationChain, Double> stateQValues = qTable.get(s);
			    if (stateQValues == null) {
			        // State not found in the qTable
			        return Double.NaN; // Or throw an exception, depending on your requirements
			    }
			    
			    // Iterate over the q-values for the specified StateChain and find the minimum
			    for (double qValue : stateQValues.values()) {
			        if (qValue < min) {
			            min = qValue;
			        }
			    }
			}
			cost=min;
		}
		
		return cost;
	}
	
	
	public Adaptation isAdaptedtaskBasedOnCurrentChain(String task, AdaptationChain adaptationChain) {
		for(Adaptation aa: adaptationChain.getAdaptations()) {
        	if(task.equals(aa.getTask())) {
        		return aa;
        	}
        }
		//for the tasks that are not invoulved in the adaptation consier a fix default time, price and value(mitigation score for these tasks is 0)
		return null;
	}
	
	/*
	public boolean isAdaptedtask(String task,StateChain currentState, AdaptationChain adaptationChain) {
		for(Adaptation aa: currentState.) {
        	if(task.equals(aa.getTask())) {
        		return true;
        	}
        }
		//for the tasks that are not invoulved in the adaptation consier a fix default time, price and value(mitigation score for these tasks is 0)
		return false;
	}
	*/
	
	public static List<String> findCommonElements(ArrayList<String> list1, ArrayList<String> list2) {
		//System.out.println("findCommonElements is started!");
		
		/*
		System.out.println("List 1:");
		 for (String element : list1) {
	            System.out.println(element);
	        }
	        System.out.println("\nList 2:");
	        for (String element : list2) {
	            System.out.println(element);
	        }
	      */
		
        // Use a HashSet to efficiently store unique elements of list1
        Set<String> set = new HashSet<>(list1);

        // Create a list to store common elements
        List<String> commonElements = new ArrayList<>();

        // Iterate through list2 and check if each element exists in the set
        for (String element : list2) {
            if (set.contains(element)) {
                commonElements.add(element);
        		//System.out.println("	-commonElements "+element);

            }
        }
		 //System.out.println("findCommonElements is completed!");


        return commonElements;
    }
	
	
	
	
	public double ComputeReward(StateChain currentState, AdaptationChain adaptationChain,Workflow w,double priceWeight,double timeWeight,double valueWeight,double securityWeight) {
		//System.out.println("@@@@@Compute Reward Sofar@@@@@");
	    ArrayList<String> coveredParallelTasks=new ArrayList<String>();
	    
	    String violatedTask=currentState.getCurrentChain().getTaskName();
	    String previousViolatedTask=currentState.getPreviousViolatedTask();
		//System.out.println("	-violatedTask "+violatedTask);
		//System.out.println("	-previousViolatedTask "+previousViolatedTask); 
	    
	    
		//System.out.println("@Compute the cost of completing workflow until the violated task (part one algorithm5)");   
	    //compute the cost of completing workflow until the violated task (part one algorithm5)
		List<String> commonElements;
	    if(previousViolatedTask==null)
	    	commonElements = findCommonElements(w.getServiceTasks(), w.getPredecessors(violatedTask));
	    else
	    	commonElements = findCommonElements(w.getSuccessors(previousViolatedTask), w.getPredecessors(violatedTask));
	    
	    double p=0,t=0,v=0,ms=0;
	    if(commonElements.size()==0)
    		//System.out.println("	-There is no pending adaptations until the current violated task ");   
	    for(String commonTask: commonElements) {
    		//System.out.println("commonTask: "+commonTask);   
	    	Adaptation aaHistory=currentState.getLastHistoryAdaptation(commonTask);
	    	if(aaHistory!=null) {
	    		//System.out.println("	-aaHistory is not null for the task "+commonTask+" so the cost of this adaptation will be add");   
	    		//aaHistory.smallPrint(); 
	    		
	    		p+=aaHistory.getPrice();
				v+=aaHistory.getValue();
				ms+=aaHistory.getMitigationScore();
				if(w.getParallelTasks(aaHistory.getTask()).size()!=0) {
					if(!coveredParallelTasks.contains(aaHistory.getTask())) {
						//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
						t=+adaptationChain.MAXDefaulttimeForParallelTasks(aaHistory.getTask(),w.getParallelTasks(aaHistory.getTask()),w);
						coveredParallelTasks.add(commonTask);
						coveredParallelTasks.addAll(w.getParallelTasks(aaHistory.getTask()));
					}
				}
				else {
					//System.out.println("	-this task is not parallel with any task!!:"+aa.getTime());
					t+=aaHistory.getTime();
					}  		
	    	}
	    	else {
	    		//System.out.println("	-aaHistory is null for the task "+commonTask+" so the default cost will be add");   
				//System.out.println("	-this task is not involved in adaptation chain!!:");
				p+=w.getDefaultPrice(commonTask);
				v+=w.getDefaultValue(commonTask);
				if(w.getParallelTasks(commonTask).size()!=0) {
					if(!coveredParallelTasks.contains(commonTask)) {
						//System.out.println("	-this task is parallel with some tasks!!  max: "+MAXDefaulttimeForParallelTasks(task,w.getParallelTasks(task),w)+" task: "+w.getDefaultTime(task));
						t+=adaptationChain.MAXDefaulttimeForParallelTasks(commonTask,w.getParallelTasks(commonTask),w);
						coveredParallelTasks.add(commonTask);
						coveredParallelTasks.addAll(w.getParallelTasks(commonTask));
					}
				}
				else {
					//System.out.println("	-this task is not parallel with any task!!:"+w.getDefaultTime(task));
					t+=w.getDefaultTime(commonTask);
				}
			}
	    	//System.out.println("t: "+t+" p: "+p+" v: "+v 	);
	    }
	    
		
	    double pNextMin=0,tNextMin=0,vNextMin=0,msNextMin=0;

		//System.out.println("@Compute the cost of adaptation chain until the current violated task");   
		boolean sofar=false;
		for (int i = 0; i < w.getServiceTasks().size(); i++) {
            String task=w.getServiceTasks().get(i);
            if(!sofar) {
    			//System.out.println("task:"+task);
				if(isAdaptedtaskBasedOnCurrentChain(task,adaptationChain)!=null) {
					//System.out.println("	-this task is adapted Based On Current Chain!!");
					Adaptation aa=isAdaptedtaskBasedOnCurrentChain(task,adaptationChain);
					p+=aa.getPrice();
					v+=aa.getValue();
					ms+=aa.getMitigationScore();
					if(w.getParallelTasks(aa.getTask()).size()!=0) {
						if(!coveredParallelTasks.contains(aa.getTask())) {
							//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
							t=+adaptationChain.MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w);
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
			   
			   if(w.getLastTask().equals(violatedTask)) {
				   adaptationChain.setFinalRLParameters(p, t, v, ms);
				   System.out.println("	Final parameters :"+p+ t+ v+ ms);

			   }
				
	          }
            else {//compute the rest as minNextValue(The minimum cost for the state occurs when the workflow is completed without any violations, thereby requiring no further adaptations.)
            	if(isAdaptedtaskBasedOnCurrentChain(task,adaptationChain)!=null) {
					//System.out.println("	-this task is adapted Based On Current Chain!!");
					Adaptation aa=isAdaptedtaskBasedOnCurrentChain(task,adaptationChain);
					pNextMin+=aa.getPrice();
					vNextMin+=aa.getValue();
					msNextMin+=aa.getMitigationScore();
					if(w.getParallelTasks(aa.getTask()).size()!=0) {
						if(!coveredParallelTasks.contains(aa.getTask())) {
							//System.out.println("	-this task is parallel with some tasks!!   max: "+MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w)+"  task: "+aa.getTime());
							tNextMin=+adaptationChain.MAXDefaulttimeForParallelTasks(aa.getTask(),w.getParallelTasks(aa.getTask()),w);
							coveredParallelTasks.add(task);
							coveredParallelTasks.addAll(w.getParallelTasks(aa.getTask()));
						}
					}
					else {
						//System.out.println("	-this task is not parallel with any task!!:"+aa.getTime());
						tNextMin+=aa.getTime();
						}
	            }
				
			   if(task.equals(violatedTask)) {
	            	sofar=true;
	            }
            	
            	}
          //System.out.println("t: "+t+" p: "+p+" v: "+v 	);
		}
		
		
		double sofarPrice=p;
		double sofarTime=t;
		double sofarValue=v;
		double sofarMitigationScore=ms;

		double TotalCostNextMin=(priceWeight*(sofarPrice+pNextMin))+(timeWeight*(sofarTime+tNextMin))-(valueWeight*(sofarValue+vNextMin))-(securityWeight*(sofarMitigationScore+msNextMin));
		minNextValue.put(adaptationChain,TotalCostNextMin);
		double sofarTotalCost=(priceWeight*sofarPrice)+(timeWeight*sofarTime)-(valueWeight*sofarValue)-(securityWeight*sofarMitigationScore);
		//System.out.println("@@@@@Compute Reward Completed@@@@@");
		return sofarTotalCost;
	}

	
/*	   public void updateQValue(String state, int adaptationChain, double reward, String nextState) {
	        Map<Integer, Double> stateQValues = qTable.computeIfAbsent(state, k -> new HashMap<>());
	        //double currentQValue = stateQValues.getOrDefault(adaptationChain, 0.0);
	        currentQValue + learningRate * (reward + discountFactor * minQValue - currentQValue);
	        }

	        // Update the Q-value for the current state-adaptationChain pair
	        double newQValue = (1 - learningRate) * currentQValue +
	                learningRate * (reward + discountFactor * minNextQValue);
	        stateQValues.put(adaptationChain, newQValue);
	    }
*/
	
	public void setReward(double totalCost,StateChain st) {
		
		if(st!=null) {
			System.out.println("set Reward: "+totalCost);
			double reward=totalCost;
			//st.print();
			
			//uddate qTable
			double currentQValue=0;
			if (qTable.containsKey(currentState)) {
			    Map<AdaptationChain, Double> stateAdaptationChains = qTable.get(currentState);
			    if (stateAdaptationChains.containsKey(predictedAdaptationChain)) {
			         currentQValue = stateAdaptationChains.get(predictedAdaptationChain);
			        }
			    
			    }
			 Map<AdaptationChain, Double> state1AdaptationChains = new HashMap<>();
			 double minQValue=0;
			 
		     state1AdaptationChains.put(predictedAdaptationChain, currentQValue + learningRate * (reward + discountFactor * minQValue - currentQValue));
		     qTable.put(currentState, state1AdaptationChains);
			printqTable();

			
			//qLearning.updateQValue(st, predictedAdaptationChain, totalCost, nextState);
			}
		else
			System.out.println("state is null");
		
		

	    }
	

	
	
	   public StateChain getState(ChainRecord Chain,ArrayList<ChainRecord> ChainRecords) {
		   StateChain currentState=null;
		   for(StateChain st:states) {
				if(st.isSameState(Chain,ChainRecords)) {
					System.out.println("isSameState: true ");
					currentState=st;
					}
			}
		   return currentState;
	   }
	   
	   public void print() {
			System.out.println("Current Qlearning: ");
			currentState.print();
			System.out.println("Current selected adaptationChain: ");
			this.predictedAdaptationChain.print();

		}
	
	public void printqTable() {
		System.out.println("qTable:");
		for (Map.Entry<StateChain, Map<AdaptationChain, Double>> entry : qTable.entrySet()) {
		    StateChain state = entry.getKey();
		    Map<AdaptationChain, Double> stateAdaptationChains = entry.getValue();
		    
		    System.out.println("State: " + state.getName());
		    state.print();
		    System.out.println("AdaptationChains and Rewards:");

		    for (Map.Entry<AdaptationChain, Double> adaptationChainEntry : stateAdaptationChains.entrySet()) {
		    	AdaptationChain adaptationChain = adaptationChainEntry.getKey();
		        double reward = adaptationChainEntry.getValue();
		        
		        System.out.println("  AdaptationChain: " + adaptationChain.getID() + ", Reward: " + reward);
		        adaptationChain.print();
		    }
		    System.out.println();
		}
	}
}
