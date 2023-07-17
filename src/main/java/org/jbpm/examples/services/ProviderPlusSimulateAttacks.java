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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.*;
import java.io.FileWriter; 
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.PoissonDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.faultinjection.VmClonerSimple;



import java.util.*;
import java.util.ArrayList;
import java.util.List;


public class ProviderPlusSimulateAttacks {
	int providerId;

	 private static final int SCHEDULING_INTERVAL = 1;

	//provider specifications 
    private static final int HOSTS = 3;
    private static final int HOST_PES = 8;
    private static final int VMS = 1;
    private static final int VM_PES = 4;
    
    //cloudlet specifications
    private static final int CLOUDLETS = 1;
    int CLOUDLET_PES;
    int CLOUDLET_LENGTH;
    double ram_UtilizationModel;
    double bw_UtilizationModel;
    long inputSize,outputsize;
    double confidentiality,integrity,availability;
    private  ArrayList<ServiceTypeSpecification> serviceSpecifications;
    
    private  CloudSim simulation;
    private  DatacenterBroker broker0;
    private  List<Vm> vmList;
    private  List<Cloudlet> cloudletList;
    private  Datacenter datacenter0;
    
    
    private  Map<Vm, Map<Double, Double>> allVmsRamUtilizationHistory;

    /** @see #allVmsRamUtilizationHistory */
    private  Map<Vm, Map<Double, Double>> allVmsBwUtilizationHistory;

    
    /*The average number of failures expected to happen each hour
    in a Poisson Process, which is also called event rate or rate parameter.*/
    private static final double MEAN_FAILURE_NUMBER_PER_HOUR = 0.001;
    private HostFaultInjection fault;

    /**
     * The Poisson Random Number Generator used to generate failure times (in hours).
     */
    private PoissonDistr poisson;
	
	
	public ProviderPlusSimulateAttacks(int Id) {
		InitializeServiceSpecification();
	}
	
	
	
	//public void executeService(int tenantId,long ProcessInstance_Id,String ProcessInstance_ProcessName,String ProcessInstance_ProcessId,String serviceTask) {
	public void executeService(String serviceTask) {

		//System.out.println("Starting execution of process Instance "+ProcessInstance_Id+" "+ProcessInstance_ProcessName+" for Service: "+serviceTask);
		/*Enables just some level of log messages.
        Make sure to import org.cloudsimplus.util.Log;*/
      //Log.setLevel(ch.qos.logback.classic.Level.WARN);
		
		
      simulation = new CloudSim();
      
      datacenter0 = createDatacenter();

      broker0 = new DatacenterBrokerSimple(simulation);

      vmList = createVms();
      cloudletList = createCloudlets(InitiatizeParametersForSerives(serviceTask));
      broker0.submitVmList(vmList);
      broker0.submitCloudletList(cloudletList);
      
      //inject fault
      createFaultInjectionForHosts(datacenter0);

      allVmsRamUtilizationHistory = initializeUtilizationHistory();
      allVmsBwUtilizationHistory = initializeUtilizationHistory();
      simulation.addOnClockTickListener(this::onClockTickListener);

      simulation.start();

      final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
      new CloudletsTableBuilder(finishedCloudlets).build();
      
      System.out.printf(
              "%n# Mean Number of Failures per Hour: %.3f (1 failure expected at each %.2f hours).%n",
              MEAN_FAILURE_NUMBER_PER_HOUR, poisson.getInterArrivalMeanTime());
          System.out.printf("# Number of Host faults: %d%n", fault.getNumberOfHostFaults());
          System.out.printf("# Number of VM faults (VMs destroyed): %d%n", fault.getNumberOfFaults());
          System.out.printf("# Time the simulations finished: %.4f hours%n", simulation.clockInHours());
          System.out.printf("# Mean Time To Repair Failures of VMs in minutes (MTTR): %.2f minute%n", fault.meanTimeToRepairVmFaultsInMinutes());
          System.out.printf("# Mean Time Between Failures (MTBF) affecting all VMs in minutes: %.2f minutes%n", fault.meanTimeBetweenVmFaultsInMinutes());
          System.out.printf("# Hosts MTBF: %.2f minutes%n", fault.meanTimeBetweenHostFaultsInMinutes());
          System.out.printf("# Availability: %.2f%%%n%n", fault.availability()*100);
      printVmListResourceUtilizationHistory();
      
  }
	
	public void InitializeServiceSpecification() {
		System.out.println("InitializeServiceSpecification");
		serviceSpecifications=new ArrayList<ServiceTypeSpecification>();
		ArrayList<String>  services=ReadLinebyLine("ServiceSpecification.txt");	
		String delimsComma = "[,]+";
		for(int line=1;line<services.size();line++) {
			ArrayList<Double> cloudletSpecification = new ArrayList<Double>();
			String[] arr  = services.get(line).split(delimsComma);
			CLOUDLET_PES=Integer.parseInt(arr[1]);
		    CLOUDLET_LENGTH=Integer.parseInt(arr[2]);;
			ram_UtilizationModel=Double.parseDouble(arr[3]);
			bw_UtilizationModel=Double.parseDouble(arr[4]);
			inputSize=Long.parseLong(arr[5]);
			outputsize=Long.parseLong(arr[6]);
			confidentiality=Double.parseDouble(arr[7]);
			integrity=Double.parseDouble(arr[8]);
			availability=Double.parseDouble(arr[9]);

			//System.out.println("ServiceSpecification: ram_UtilizationModel: "+ram_UtilizationModel+" bw_UtilizationModel: "+bw_UtilizationModel+" inputSize: "+inputSize+" outputsize:"+outputsize);
			serviceSpecifications.add(new ServiceTypeSpecification(arr[0],CLOUDLET_PES,CLOUDLET_LENGTH,ram_UtilizationModel,bw_UtilizationModel,inputSize,outputsize, confidentiality,integrity,availability));
		}
	}
	
	public ServiceTypeSpecification InitiatizeParametersForSerives(String serviceTask) {
		ServiceTypeSpecification serviceType=null;
		for(ServiceTypeSpecification s:serviceSpecifications){
			if(s.getName().equals(serviceTask)) {
				 ram_UtilizationModel=s.getRam_UtilizationModel();
			     bw_UtilizationModel=s.getBw_UtilizationModel();
			     inputSize=s.getInputSize();
			     outputsize=s.geOutputsize();
			     s.print();
			     serviceType=s;
			}
		}
		
		return serviceType;
	}
	
	public ArrayList<String> ReadLinebyLine(String File) {
		BufferedReader reader;
		ArrayList<String> lines=new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(File));
			String line = reader.readLine();

			while (line != null) {
				//System.out.println(line);
				// read next line
				lines.add(line);
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		//printList(lines);
		return lines;
	}
  /**
   * Prints the RAM and BW utilization history of every Vm.
   */
  private void printVmListResourceUtilizationHistory() {
      System.out.println();
      for (Vm vm : vmList) {
          printVmUtilizationHistory(vm);
      }
  }

  /**
   * Prints the RAM and BW utilization history of a given Vm.
   */
  private void printVmUtilizationHistory(Vm vm) {
      System.out.println(vm + " RAM and BW utilization history");
      System.out.println("----------------------------------------------------------------------------------");

      //A set containing all resource utilization collected times
      final Set<Double> timeSet = allVmsRamUtilizationHistory.get(vm).keySet();

      final Map<Double, Double> vmRamUtilization = allVmsRamUtilizationHistory.get(vm);
      final Map<Double, Double> vmBwUtilization = allVmsBwUtilizationHistory.get(vm);

      
      for (final double time : timeSet) {
          System.out.printf(
              "Time: %10.1f secs | RAM Utilization: %10.2f%% | BW Utilization: %10.2f%%%n",
              time, vmRamUtilization.get(time) * 100, vmBwUtilization.get(time) * 100);
      }
      

      System.out.printf("----------------------------------------------------------------------------------%n%n");
  }


  /**
   * Initializes a map that will store utilization history for
   * some resource (such as RAM or BW) of every VM.
   * It also creates an empty internal map to store
   * the resource utilization for every VM along the simulation execution.
   * The internal map for every VM will be empty.
   * They are filled inside the {@link #onClockTickListener(EventInfo)}.
   */
  private Map<Vm, Map<Double, Double>> initializeUtilizationHistory() {
      //TreeMap sorts entries based on the key
      final Map<Vm, Map<Double, Double>> map = new HashMap<>(VMS);

      for (Vm vm : vmList) {
          map.put(vm, new TreeMap<>());
      }

      return map;
  }

  /**
   * Keeps track of simulation clock.
   * Every time the clock changes, this method is called.
   * To enable this method to be called at a defined
   * interval, you need to set the {@link Datacenter#setSchedulingInterval(double) scheduling interval}.
   *
   * @param evt information about the clock tick event
   * @see #SCHEDULING_INTERVAL
   */
  private void onClockTickListener(final EventInfo evt) {
      collectVmResourceUtilization(this.allVmsRamUtilizationHistory, Ram.class);
      collectVmResourceUtilization(this.allVmsBwUtilizationHistory, Bandwidth.class);
  }

  /**
   * Collects the utilization percentage of a given VM resource for every VM.
   * CloudSim Plus already has built-in features to obtain VM's CPU utilization.
   * Check {@link org.cloudsimplus.examples.power.PowerExample}.
   *
   * @param allVmsUtilizationHistory the map where the collected utilization for every VM will be stored
   * @param resourceClass the kind of resource to collect its utilization (usually {@link Ram} or {@link Bandwidth}).
   */
  private void collectVmResourceUtilization(final Map<Vm, Map<Double, Double>> allVmsUtilizationHistory, Class<? extends ResourceManageable> resourceClass) {
      for (Vm vm : vmList) {
          /*Gets the internal resource utilization map for the current VM.
          * The key of this map is the time the usage was collected (in seconds)
          * and the value the percentage of utilization (from 0 to 1). */
          final Map<Double, Double> vmUtilizationHistory = allVmsUtilizationHistory.get(vm);
          vmUtilizationHistory.put(simulation.clock(), vm.getResource(resourceClass).getPercentUtilization());
      }
  }

  /**
   * Creates a Datacenter and its Hosts.
   */
  private Datacenter createDatacenter() {
      final List<Host> hostList = new ArrayList<>(HOSTS);
      for(int i = 0; i < HOSTS; i++) {
          Host host = createHost();
          hostList.add(host);
      }
      final Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
      dc.setSchedulingInterval(SCHEDULING_INTERVAL);
      return dc;
  }

  private Host createHost() {
      List<Pe> peList = new ArrayList<>(HOST_PES);
      //List of Host's CPUs (Processing Elements, PEs)
      for (int i = 0; i < HOST_PES; i++) {
          peList.add(new PeSimple(1000, new PeProvisionerSimple()));
      }

      final long ram = 2048; //in Megabytes
      final long bw = 10000; //in Megabits/s
      final long storage = 1000000; //in Megabytes
      Host host = new HostSimple(ram, bw, storage, peList);
      host
          .setRamProvisioner(new ResourceProvisionerSimple())
          .setBwProvisioner(new ResourceProvisionerSimple())
          .setVmScheduler(new VmSchedulerTimeShared());
      return host;
  }

  /**
   * Creates a list of VMs.
   */
  private List<Vm> createVms() {
      final List<Vm> list = new ArrayList<>(VMS);
      for (int i = 0; i < VMS; i++) {
          list.add(createVm(VM_PES));
      }
      return list;
  }

  private Vm createVm(final int pes) {
      return new VmSimple(1000, pes)
          .setRam(1000).setBw(1000).setSize(10000)
          .setCloudletScheduler(new CloudletSchedulerTimeShared());
  }

  /**
   * Creates a list of Cloudlets.
   */
  private List<Cloudlet> createCloudlets(ServiceTypeSpecification s) {
      final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
      for (int i = 0; i < CLOUDLETS; i++) {
          list.add(createCloudlet(s));
      }

      return list;
  }

  /**
   * Creates a Cloudlet with specific {@link UtilizationModel} for RAM, BW and CPU.
   * You can change the method to use any {@link UtilizationModel} you want.
   * @return
   */
  private Cloudlet createCloudlet(ServiceTypeSpecification s) {
      //UtilizationModelDynamic ramUtilizationModel = new UtilizationModelDynamic(0.2);
      //UtilizationModelDynamic bwUtilizationModel = new UtilizationModelDynamic(0.1);
	  UtilizationModelDynamic ramUtilizationModel = new UtilizationModelDynamic(s.getRam_UtilizationModel());
      UtilizationModelDynamic bwUtilizationModel = new UtilizationModelDynamic(s.getBw_UtilizationModel());

      ramUtilizationModel.setUtilizationUpdateFunction(this::utilizationUpdate);
      bwUtilizationModel.setUtilizationUpdateFunction(this::utilizationUpdate);

      //return new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES)
      return new CloudletSimple(s.getCLOUDLET_LENGTH(), s.getCLOUDLET_PES())
          //.setFileSize(1024)
          //.setOutputSize(1024)
    	  .setFileSize(s.getInputSize())
          .setOutputSize(s.geOutputsize())
          .setUtilizationModelCpu(new UtilizationModelFull())
          .setUtilizationModelRam(ramUtilizationModel)
          .setUtilizationModelBw(bwUtilizationModel);
  }

  /**
   * Defines how the Cloudlet's utilization of RAM and BW will increase along the simulation time.
   * @return the updated utilization for RAM or BW
   * @see #createCloudlet()
   */
  private double utilizationUpdate(UtilizationModelDynamic utilizationModel) {
      return utilizationModel.getUtilization() + utilizationModel.getTimeSpan() * 0.01;
  }
  
  //for fault injection
  private void createFaultInjectionForHosts(Datacenter datacenter) {
      //Use the system time to get random results every time you run the simulation
      //final long seed = System.currentTimeMillis();
      final long seed = 112717613L;
      this.poisson = new PoissonDistr(MEAN_FAILURE_NUMBER_PER_HOUR, seed);

      fault = new HostFaultInjection(datacenter, poisson);
      fault.setMaxTimeToFailInHours(800);

      fault.addVmCloner(broker0, new VmClonerSimple(this::cloneVm, this::cloneCloudlets));
  }

  /**
   * Clones a VM by creating another one with the same configurations of a
   * given VM.
   *
   * @param vm the VM to be cloned
   * @return the cloned (new) VM.
   *
   * @see #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
   */
  private Vm cloneVm(Vm vm) {
      Vm clone = new VmSimple(vm.getMips(), (int) vm.getNumberOfPes());
      /*It' not required to set an ID for the clone.
      It is being set here just to make it easy to
      relate the ID of the vm to its clone,
      since the clone ID will be 10 times the id of its
      source VM.*/
      clone.setId(vm.getId() * 10);
      clone.setDescription("Clone of VM " + vm.getId());
      clone
          .setSize(vm.getStorage().getCapacity())
          .setBw(vm.getBw().getCapacity())
          .setRam(vm.getBw().getCapacity())
          .setCloudletScheduler(new CloudletSchedulerTimeShared());
      System.out.printf("%n%n# Cloning %s - MIPS %.2f Number of Pes: %d%n", vm, clone.getMips(), clone.getNumberOfPes());

      return clone;
  }

  /**
   * Clones each Cloudlet associated to a given VM. The method is called when
   * a VM is destroyed due to a Host failure and a snapshot from that VM (a
   * clone) is started into another Host. In this case, all the Cloudlets
   * which were running inside the destroyed VM will be recreated from scratch
   * into the VM clone, re-starting their execution from the beginning.
   *
   * @param sourceVm the VM to clone its Cloudlets
   * @return the List of cloned Cloudlets.
   * @see
   * #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
   */
  private List<Cloudlet> cloneCloudlets(Vm sourceVm) {
      final List<Cloudlet> sourceVmCloudlets = sourceVm.getCloudletScheduler().getCloudletList();
      final List<Cloudlet> clonedCloudlets = new ArrayList<>(sourceVmCloudlets.size());
      for (Cloudlet cl : sourceVmCloudlets) {
          Cloudlet clone = cloneCloudlet(cl);
          clonedCloudlets.add(clone);
          System.out.printf("# Created Cloudlet Clone for %s (Cloned Cloudlet Id: %d)%n", sourceVm, clone.getId());
      }

      return clonedCloudlets;
  }

  /**
   * Creates a clone from a given Cloudlet.
   *
   * @param source the Cloudlet to be cloned.
   * @return the cloned (new) cloudlet
   */
  private Cloudlet cloneCloudlet(Cloudlet source) {
      Cloudlet clone = new CloudletSimple(source.getLength(), source.getNumberOfPes());
      /*It' not required to set an ID for the clone.
      It is being set here just to make it easy to
      relate the ID of the cloudlet to its clone,
      since the clone ID will be 10 times the id of its
      source cloudlet.*/
      clone.setId(source.getId() * 10);
      clone
          .setUtilizationModelBw(source.getUtilizationModelBw())
          .setUtilizationModelCpu(source.getUtilizationModelCpu())
          .setUtilizationModelRam(source.getUtilizationModelRam());
      return clone;
  }
  
}