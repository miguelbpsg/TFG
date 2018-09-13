package dioco;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOTS{
	private Map<Integer,Map<String, List<Integer>>> transitions;
	private List<String> inputs;
	private List<String> outputs;
	private Integer firstState;
	private List<Integer> finalStates = new ArrayList<Integer>();
	private List<Integer> nonFinalStates = new ArrayList<Integer>();

	public IOTS(String file) {
		transitions = new HashMap<Integer,Map<String,List<Integer>>>();
		inputs = new ArrayList<String>();
		outputs = new ArrayList<String>();
		BufferedReader br = null;

		try{
			br = new BufferedReader(new FileReader(file));
		}
		catch(FileNotFoundException e){
			System.err.println("File: " + file +  " not found");
			e.printStackTrace();
		}
		try {
			String line = br.readLine();
			String[] lines;
			Integer source, destination;
			String transition;
			while (!line.equals("@")) {
				lines = line.split(" ");
				if(lines.length != 3)
					throw new IOException("Error on the file. Not three and only three strings per line");

				source = Integer.valueOf(lines[0]);
				transition = lines[1];
				destination = Integer.valueOf(lines[2]);

				if(firstState == null) firstState = source;
				if(!finalStates.contains(source))
					finalStates.add(source);
				if(!finalStates.contains(destination))
					finalStates.add(destination);
				if(transition.startsWith("?")){
					if(!inputs.contains(transition))
						inputs.add(transition);
				}
				else if(transition.startsWith("!")){
					if(!outputs.contains(transition))
						outputs.add(transition);
				}
				else if (transition.equals("delta")) {
					if(!outputs.contains(transition))
						outputs.add(transition);
				}//delta is inlcuded to the alphabet even though it shouldn't belong
				else throw new IOException("Error on the file. Transitions start by \"?\" if they are inputs or \"!\" if they are outputs");

				Map<String,List<Integer>> map = transitions.containsKey(source) ?
					transitions.get(Integer.valueOf(lines[0])) :
					new HashMap<String,List<Integer>>();
				List<Integer> list = map.containsKey(transition) ?
					map.get(transition) : new ArrayList<Integer>();

				list.add(destination);
				map.put(transition, list);
				transitions.put(source, map);

				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extendMachine(int k){
		int freeState = 0;
		Map<Integer,Integer> copy = new HashMap<Integer,Integer>();
		Map<String,List<Integer>> map1, map2;
		List<Integer> list1, list2;
		for(int i = 0; i < k; i++) {
//copying final states
			int numStates = finalStates.size();
			for(int j = 0; j < numStates; j++){
				Integer state = finalStates.get(j);
				while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
					freeState++;
				copy.put(state, freeState);
				finalStates.add(freeState);
			}
//copying non final states
			numStates = nonFinalStates.size();
			for(int j = 0; j < numStates; j++){
				Integer state = nonFinalStates.get(j);
				while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
					freeState++;
				copy.put(state, freeState);
				nonFinalStates.add(freeState);
			}
			
//copying transitions for final states
			for(int j = 0; j < finalStates.size()/2; j++) {
				/*finalStates has just been duplicated. Dividing its size by 2
				 * gives us the number of initial final states to copy transitions*/
				if(transitions.containsKey(finalStates.get(j))) {
					map1 = transitions.get(finalStates.get(j));
					map2 = new HashMap<String,List<Integer>>();
					for(String input : inputs) {
						list2 = new ArrayList<Integer>();
						if(map1.containsKey(input)) {
							list1 = map1.get(input);
							for(int l = 0; l < list1.size(); l++) {
								list2.add(copy.get(list1.get(l)));
							}
							map2.put(input, list2);
						}
					}
					boolean added = false;
					for(String output : outputs) {
						list2 = new ArrayList<Integer>();
						
						if(map1.containsKey(output)) {
							list1 = map1.get(output);
							for(int l = 0; l < list1.size(); l++) {
								list2.add(copy.get(list1.get(l)));
							}
							map2.put(output, list2);
							added = true;
						}
					}
					if(!added) {
						list2 = new ArrayList<Integer>();
						list2.add(copy.get(finalStates.get(j)));
						map2.put("delta",list2);
					}
						
					transitions.put(copy.get(finalStates.get(j)),map2);
				}
			}

//copying transitions for non final states
			for(int j = 0; j < nonFinalStates.size()/2; j++) {
				/*nonFinalStates has just been duplicated. Dividing its size by 2
				 * gives us the number of initial final states to copy transitions*/
				if(transitions.containsKey(nonFinalStates.get(j))) {
					map1 = transitions.get(nonFinalStates.get(j));
					map2 = new HashMap<String,List<Integer>>();
					for(String input : inputs) {
						list2 = new ArrayList<Integer>();
						if(map1.containsKey(input)) {
							list1 = map1.get(input);
							for(int l = 0; l < list1.size(); l++) {
								list2.add(copy.get(list1.get(l)));
							}
							map2.put(input, list2);
						}
					}
					for(String output : outputs) {
						list2 = new ArrayList<Integer>();
						if(map1.containsKey(output)) {
							list1 = map1.get(output);
							for(int l = 0; l < list1.size(); l++) {
								list2.add(copy.get(list1.get(l)));
							}
							map2.put(output, list2);
						}
					}
					transitions.put(copy.get(nonFinalStates.get(j)),map2);
				}
			}
//swaps starting from final states
			for(int j = 0; j < finalStates.size()/2; j++) {
				/*finalStates has just been duplicated. Dividing its size by 2
				 * gives us the number of initial final states to swap transitions*/
				if(transitions.containsKey(finalStates.get(j))) {
					map1 = transitions.get(finalStates.get(j));
//INPUT
					for(String input : inputs) {
						list1 = map1.get(input);
						int size1 = list1.size();
						for(int l = 0; l < size1; l++) {
							map2 = transitions.get(list1.get(l));
							String port1 = "_" + input.split("_")[1];
	//INPUT INPUT
							for(String input2 : inputs) {
								if(!input2.endsWith(port1) && map2.containsKey(input2)){
									list2 = map2.get(input2);
									int size2 = list2.size();
									for(int m = 0; m < size2; m++){
										if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
											while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
												freeState++;
											nonFinalStates.add(freeState);
											
											List<Integer> laux = new ArrayList<Integer>();
											Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
											laux.add(copy.get(list2.get(m)));
											maux.put(input, laux);
											transitions.put(freeState, maux);
											
											Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(finalStates.get(j)));
											List<Integer> aux = new ArrayList<Integer>(map1prime.get(input2));
											aux.add(freeState);
											map1prime.put(input2, aux);
											transitions.put(finalStates.get(j),map1prime);
										}
									}
								}
							}
	//INPUT OUTPUT
							for(String output : outputs) {
								if(!output.endsWith(port1) && !output.equals("delta") && map2.containsKey(output)){
									list2 = map2.get(output);
									int size2 = list2.size();
									for(int m = 0; m < size2; m++){
										if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
											while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
												freeState++;
											nonFinalStates.add(freeState);
											
											List<Integer> laux = new ArrayList<Integer>();
											Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
											laux.add(copy.get(list2.get(m)));
											maux.put(input, laux);
											transitions.put(freeState, maux);
		
											Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(finalStates.get(j)));
											List<Integer> aux = map1prime.containsKey(output) ?
													new ArrayList<Integer>(map1prime.get(output)) : new ArrayList<Integer>();
											aux.add(freeState);
											map1prime.put(output, aux);
											transitions.put(finalStates.get(j),map1prime);
										}
									}
								}
							}
						}
					}
//OUTPUT
					for(String output : outputs) {
						if (!output.equals("delta") && map1.containsKey(output)) {
							list1 = map1.get(output);
							int size1 = list1.size();
							for(int l = 0; l < size1; l++) {
								map2 = transitions.get(list1.get(l));
								String port1 = "_" + output.split("_")[1];
	//OUTPUT INPUT
								for(String input : inputs) {
									if(!input.endsWith(port1) && map2.containsKey(input)) {
										list2 = map2.get(input);
										int size2 = list2.size();
										for(int m = 0; m < size2; m++){
											if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
												while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
													freeState++;
												nonFinalStates.add(freeState);
												
												List<Integer> laux = new ArrayList<Integer>();
												Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
												laux.add(copy.get(list2.get(m)));
												maux.put(output, laux);
												transitions.put(freeState, maux);
														
												Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(finalStates.get(j)));
												List<Integer> aux = new ArrayList<Integer>(map1prime.get(input));
												aux.add(freeState);
												map1prime.put(input, aux);
												transitions.put(finalStates.get(j),map1prime);
											}											
										}
									}
								}
	//OUTPUT OUTPUT
								for(String output2 : outputs) {
									if(!output2.endsWith(port1) && !output2.equals("delta") && map2.containsKey(output2)) {
										list2 = map2.get(output2);
										int size2 = list2.size();
										for(int m = 0; m < size2; m++){
											if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
												while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
													freeState++;
												nonFinalStates.add(freeState);
												
												List<Integer> laux = new ArrayList<Integer>();
												Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
												laux.add(copy.get(list2.get(m)));
												maux.put(output, laux);
												transitions.put(freeState, maux);

												Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(finalStates.get(j)));
												List<Integer> aux = map1prime.containsKey(output2) ?
													new ArrayList<Integer>(map1prime.get(output2)) : new ArrayList<Integer>();
												aux.add(freeState);
												map1prime.put(output2, aux);
												transitions.put(finalStates.get(j),map1prime);
											}
										}
									}
								}
							}
						}
					}				
				}
			}

//swaps starting from non final states
			for(int j = 0; j < numStates; j++) {
				/*numStates is the number of non final states before doing 
				* this iteration of swaps*/
				if(transitions.containsKey(nonFinalStates.get(j))) {
					map1 = transitions.get(nonFinalStates.get(j));
//INPUT
					for(String input : inputs) {
						if(map1.containsKey(input)) {
							list1 = map1.get(input);
							int size1 = list1.size();
							for(int l = 0; l < size1; l++) {
								map2 = transitions.get(list1.get(l));
								String port1 = "_" + input.split("_")[1];
	//INPUT INPUT
								for(String input2 : inputs) {
									if(!input2.endsWith(port1) && map2.containsKey(input2)){
										list2 = map2.get(input2);
										int size2 = list2.size();
										for(int m = 0; m < size2; m++){
											if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
												while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
													freeState++;
												nonFinalStates.add(freeState);
												
												List<Integer> laux = new ArrayList<Integer>();
												Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
												laux.add(copy.get(list2.get(m)));
												maux.put(input, laux);
												transitions.put(freeState, maux);
												
												Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(nonFinalStates.get(j)));
												List<Integer> aux = map1prime.containsKey(input2) ?
													new ArrayList<Integer>(map1prime.get(input2)) : new ArrayList<Integer>();
												aux.add(freeState);
												map1prime.put(input2, aux);
												transitions.put(nonFinalStates.get(j),map1prime);
											}
										}
									}
								}
	//INPUT OUTPUT
								for(String output : outputs) {
									if(!output.endsWith(port1) && !output.equals("delta") && map2.containsKey(output)){
										list2 = map2.get(output);
										int size2 = list2.size();
										for(int m = 0; m < size2; m++){
											if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
												while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
													freeState++;
												nonFinalStates.add(freeState);
												
												List<Integer> laux = new ArrayList<Integer>();
												Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
												laux.add(copy.get(list2.get(m)));
												maux.put(input, laux);
												transitions.put(freeState, maux);
			
												Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(nonFinalStates.get(j)));
												List<Integer> aux = map1prime.containsKey(output) ?
														new ArrayList<Integer>(map1prime.get(output)) : new ArrayList<Integer>();
												aux.add(freeState);
												map1prime.put(output, aux);
												transitions.put(nonFinalStates.get(j),map1prime);
											}
										}
									}
								}
							}
						}
					}
//OUTPUT
					for(String output : outputs) {
						if (!output.equals("delta") && map1.containsKey(output)) {
							list1 = map1.get(output);
							int size1 = list1.size();
							for(int l = 0; l < size1; l++) {
								map2 = transitions.get(list1.get(l));
								String port1 = "_" + output.split("_")[1];
	//OUTPUT INPUT
								for(String input : inputs) {
									if(!input.endsWith(port1) && map2.containsKey(input)) {
										list2 = map2.get(input);
										int size2 = list2.size();
										for(int m = 0; m < size2; m++){
											if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
												while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
													freeState++;
												nonFinalStates.add(freeState);
												
												List<Integer> laux = new ArrayList<Integer>();
												Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
												laux.add(copy.get(list2.get(m)));
												maux.put(output, laux);
												transitions.put(freeState, maux);
														
												Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(nonFinalStates.get(j)));
												List<Integer> aux = map1prime.containsKey(input) ?
													new ArrayList<Integer>(map1prime.get(input)) : new ArrayList<Integer>();
												aux.add(freeState);
												map1prime.put(input, aux);
												transitions.put(nonFinalStates.get(j),map1prime);
											}
										}
									}
								}
	//OUTPUT OUTPUT
								for(String output2 : outputs) {
									if(!output2.endsWith(port1) && !output2.equals("delta") && map2.containsKey(output2)){
										list2 = map2.get(output2);
										int size2 = list2.size();
										for(int m = 0; m < size2; m++){
											if(copy.containsKey(list2.get(m))) {	//Avoiding making swaps from a swapped item
												while(finalStates.contains(freeState) || nonFinalStates.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
													freeState++;
												nonFinalStates.add(freeState);
												
												List<Integer> laux = new ArrayList<Integer>();
												Map<String,List<Integer>> maux = new HashMap<String,List<Integer>>();
												laux.add(copy.get(list2.get(m)));
												maux.put(output, laux);
												transitions.put(freeState, maux);

												Map<String,List<Integer>> map1prime = new HashMap<String,List<Integer>>(transitions.get(nonFinalStates.get(j)));
												List<Integer> aux = map1prime.containsKey(output2) ?
														new ArrayList<Integer>(map1prime.get(output2)) : new ArrayList<Integer>();
												aux.add(freeState);
												map1prime.put(output2, aux);
												transitions.put(nonFinalStates.get(j),map1prime);
											}
										}
									}
								}
							}
						}
					}				
				}
			}
		}
	}
	
	public FDA determinize() {
		Map<Integer,Map<String, Integer>> transitionsDet = new HashMap<Integer,Map<String,Integer>>();
		List<Integer> finalStatesDet = new ArrayList<Integer>();
		List<Integer> nonFinalStatesDet = new ArrayList<Integer>();
		finalStatesDet.add(firstState);
		int freeState = 0;
		List<Integer> states2Explore = new ArrayList<Integer>();
		states2Explore.add(firstState);
		Map<Integer,List<Integer>> DFA2NFA = new HashMap<Integer,List<Integer>>();
		Map<List<Integer>,Integer> NFA2DFA = new HashMap<List<Integer>,Integer>();
		List<Integer> group = new ArrayList<Integer>();
		group.add(firstState);
		DFA2NFA.put(firstState,group);
		NFA2DFA.put(group,firstState);

		List<Integer> group2;
		boolean isFinal, transitionExists;
		
		while(!states2Explore.isEmpty()) {
			group = DFA2NFA.get(states2Explore.get(0));
			for(String input : inputs) {
				group2 = new ArrayList<Integer>();
				isFinal = false;
				transitionExists = false;
				for(Integer state : group){
					if(transitions.get(state).containsKey(input)) {
						List<Integer> states = transitions.get(state).get(input);
						transitionExists = true;
						for(Integer state2 : states){
							if(!group2.contains(state2))
								group2.add(state2);
							if(finalStates.contains(state2))
								isFinal = true;
						}
					}
				}
				if(transitionExists) {
					Collections.sort(group2);
					
					if(NFA2DFA.containsKey(group2)) {//There exists a state with this group of states. Note that group2 is sorted
						Map<String,Integer> map = transitionsDet.containsKey(states2Explore.get(0)) ?
							transitionsDet.get(states2Explore.get(0)) : new HashMap<String,Integer>();
						map.put(input,NFA2DFA.get(group2));
						transitionsDet.put(states2Explore.get(0),map);
					}
					else {
						while(finalStatesDet.contains(freeState) || nonFinalStatesDet.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
							freeState++;
						DFA2NFA.put(freeState,group2);
						NFA2DFA.put(group2,freeState);
						states2Explore.add(freeState);
						Map<String,Integer> map = transitionsDet.containsKey(states2Explore.get(0)) ?
							transitionsDet.get(states2Explore.get(0)) : new HashMap<String,Integer>();
						map.put(input,freeState);
						transitionsDet.put(states2Explore.get(0), map);
						if(isFinal)
							finalStatesDet.add(freeState);
						else
							nonFinalStatesDet.add(freeState);
					}
				}
			}
			for(String output : outputs) {
				group2 = new ArrayList<Integer>();
				isFinal = false;
				transitionExists = false;
				for(Integer state : group){
					if(transitions.get(state).containsKey(output)) {
						List<Integer> states = transitions.get(state).get(output);
						transitionExists = true;
						for(Integer state2 : states){
							if(!group2.contains(state2))
								group2.add(state2);
							if(finalStates.contains(state2))
								isFinal = true;
						}
					}
				}
				if(transitionExists) {
					Collections.sort(group2);
	
					if(NFA2DFA.containsKey(group2)) {//There exists a state with this group of states. Note that group2 is sorted
						Map<String,Integer> map = transitionsDet.containsKey(states2Explore.get(0)) ?
							transitionsDet.get(states2Explore.get(0)) : new HashMap<String,Integer>();
						map.put(output,NFA2DFA.get(group2));
						transitionsDet.put(states2Explore.get(0),map);
					}
					else {
						while(finalStatesDet.contains(freeState) || nonFinalStatesDet.contains(freeState))	//CAUTION, MIGHT BE AN ENDLESS LOOP
							freeState++;
						DFA2NFA.put(freeState,group2);
						NFA2DFA.put(group2,freeState);
						states2Explore.add(freeState);
						Map<String,Integer> map = transitionsDet.containsKey(states2Explore.get(0)) ?
							transitionsDet.get(states2Explore.get(0)) : new HashMap<String,Integer>();
						map.put(output,freeState);
						transitionsDet.put(states2Explore.get(0), map);
						if(isFinal)
							finalStatesDet.add(freeState);
						else
							nonFinalStatesDet.add(freeState);
					}
				}
			}
			states2Explore.remove(0);
		}
		return new FDA(firstState,finalStatesDet,nonFinalStatesDet,transitionsDet, inputs, outputs);
	}
	
	public String toString() {
		return transitions.toString() + "\n" + finalStates + "\n" + nonFinalStates;
	}
}