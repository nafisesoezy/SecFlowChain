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

public class Attack {
	
	private static int numOfAttackTypes=0;
	private int id;
	private String name;
	private ArrayList<Double> securityImpact = new ArrayList<Double>();//securityImpact[0]:confidentiality securityImpact[1]:Integrity securityImpact[2]:availability
	Map<String, ArrayList<String>> attackAdaptationPattern = new HashMap<String, ArrayList<String>>();//for each attack severity(low, medium, high) there is a list of legal adaptations

	

	public Attack(String name, ArrayList<Double> securityImpact,Map<String, ArrayList<String>> attackAdaptation) {
		setId();
		setName(name);
		this.securityImpact=securityImpact;
		this.attackAdaptationPattern= attackAdaptation;
	}
	

	
	public int getId() {
		return id;
	}

	public void setId() {
		this.id=++this.numOfAttackTypes;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}
	
	public ArrayList<Double>  getSecurityImpact(){
		return securityImpact;
	}
	
	public ArrayList<String> getAttackAdaptationPattern(String severity) {
		return attackAdaptationPattern.get(severity);
	}
	
	public void print() {
		System.out.print("Attack ID: "+this.getId()+" Name: "+this.getName()+" securityImpact: ");
		for(double f:securityImpact)
			System.out.print(f+" ");
		System.out.println();
		for(String severity:attackAdaptationPattern.keySet()) {
			System.out.println("            for "+severity+" severity, the available daptations are: ");
			for(String adapt:attackAdaptationPattern.get(severity))
				System.out.println("               - "+adapt+" ");
		}
		System.out.println("**********************");
	}

}
