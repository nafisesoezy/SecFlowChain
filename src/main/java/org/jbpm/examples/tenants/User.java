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

public class User {
	
	private int id;
	private String name;
	ArrayList<String> ip = new ArrayList<String>();
	private double trust;
	ArrayList<String> days = new ArrayList<String>();
	

	public User(int id) {
		setId(id);
		System.out.println(id+ "is created");
	}
	
	public User(int id,String name,ArrayList<String> ip,double trust,ArrayList<String> days) {
		setId(id);
		setName(name);
		setIp(ip);
		setTrust(trust);
		setDays(days);
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
	
	public ArrayList<String> getIp() {
		return ip;
	}

	public void setIp(ArrayList<String> ip) {
		this.ip = ip;
	}
	
	public double getTrust() {
		return trust;
	}

	public void setTrust(double t) {
		this.trust = t;
	}
	
	public void setDays(ArrayList<String> d) {
		this.days = d;
	}
	
	public ArrayList<String>  getDays() {
		return this.days;
	}
	
	public void print() {
		System.out.print("User ID: "+this.getId()+" Name: "+this.getName()+" Legal IPs: ");
		for(int i=0;i<this.ip.size();i++)
			System.out.print(this.ip.get(i)+" ");
		System.out.print("Legal days: ");
		for(int i=0;i<this.days.size();i++)
			System.out.print(this.days.get(i));
		System.out.println();
	}

}
