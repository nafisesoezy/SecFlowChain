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

package org.jbpm.examples.services;
import java.util.ArrayList;
import java.util.Map; 
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ServiceTypeSpecification {
	
	private String name;
	int CLOUDLET_PES;
    int CLOUDLET_LENGTH;
    double ram_UtilizationModel;
    double bw_UtilizationModel;
    long inputSize,outputsize;
    double confidentiality,integrity,availability;


	
	public ServiceTypeSpecification(String n,int pes,int length,double ram,double bw,long input,long output,double conf,double inte,double avai) {
		name=n;
		ram_UtilizationModel=ram;
	    bw_UtilizationModel=bw;
	    inputSize=input;
	    outputsize=output;
	    confidentiality=conf;
	    integrity=inte;
	    availability=avai;
	    CLOUDLET_PES =pes;
	    CLOUDLET_LENGTH = length;
	}
	
	
	
	public String getName() {
		return name;
	}


	public int getCLOUDLET_PES() {
		return CLOUDLET_PES;
	}

	
	public int getCLOUDLET_LENGTH() {
		return CLOUDLET_LENGTH;
	}
	
	public double getRam_UtilizationModel() {
		return ram_UtilizationModel;
	}

	
	public double getBw_UtilizationModel() {
		return bw_UtilizationModel;
	}
	

	public long getInputSize() {
		return inputSize;
	}

	
	public long geOutputsize() {
		return outputsize;
	}
	
	
	public void print() {
		System.out.println("Service Name: "+this.name+" CLOUDLET_LENGTH: "+CLOUDLET_LENGTH+" CLOUDLET_PES: "+CLOUDLET_PES+" ram_UtilizationModel: "+this.ram_UtilizationModel+" bw_UtilizationModel: "+bw_UtilizationModel+" inputSize "+ inputSize+" outputsize:"+outputsize+" confidentiality "+confidentiality+" integrity "+integrity+" availability "+availability);
		
	}

}
