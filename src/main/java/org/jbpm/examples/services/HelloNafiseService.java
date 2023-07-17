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
import org.jbpm.examples.tenants.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.*;
import java.io.FileWriter; 
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 
import java.util.Random;
import java.util.*;

public class HelloNafiseService {
	
	public static String VALIDATE_STRING = null;
	static Workflow workflow;    
    
	public HelloNafiseService(Workflow w) {
		this.workflow=w;
	}
	
	public HelloNafiseService() {
		//System.out.println("workflow "+workflow);

	}
	
	public void hello(String name) {
		System.out.println("Service task " + name);
	}
	
	
	
	
	public void SetOnEntryParametersHelloTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) {
		System.out.println("*** ServiceTask: HelloTask ");
		System.out.println("SetOnEntryParametersHelloTask ");
	}
	

	public void SetOnExitParametersHelloTask(long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId) throws IOException{
		System.out.println("OnExitSetParametersHelloTask ");
		//CreateServiceTask(ProcessInstance_Id,ProcessInstance_ProcessName,ProcessInstance_ProcessId,"hello");
	}
	
	/*
	public String hello(String name) {
        return "Hello Nafise Service " + name + "!";
    }
  */  
    public String helloEcho(String name) {
        return name;
    }
    
    public String validate(String value) {
    	if (VALIDATE_STRING != null) {
    		if (!VALIDATE_STRING.equals(value)) {
    			throw new RuntimeException("Value does not match expected string: " + value);
    		}
    	}
    	return value;
    }

    public String helloException(String name) {
        throw new RuntimeException("Hello Nafise Exception " + name + "!");
    }

}
