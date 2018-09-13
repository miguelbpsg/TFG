package dioco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FDA {
	private Map<Integer,Map<String, Integer>> transitions;
	private List<String> actions;
	private Integer firstState;
	private List<Integer> finalStates;
	private List<Integer> nonFinalStates;
	
	public FDA(Integer firstState, List<Integer> finalStates, List<Integer> nonFinalStates,
			Map<Integer, Map<String, Integer>> transitions, List<String> inputs,
			List<String> outputs) {
		this.firstState = firstState;
		this.finalStates = finalStates;
		this.nonFinalStates = nonFinalStates;
		this.transitions = transitions;
		actions = new ArrayList<String>();
		actions.addAll(inputs);
		actions.addAll(outputs);
	}
	
	public Map<Integer,Map<String,Integer>> getTransitions() {
		return transitions;
	}
	
	public List<String> getActions() {
		return actions;
	}
	
	public Integer getFirstState() {
		return firstState;
	}
	
	public List<Integer> getFinalStates() {
		return finalStates;
	}

	public List<Integer> getNonFinalStates() {
		return nonFinalStates;
	}

	public String toString() {
		return transitions.toString() + "\n" + finalStates + "\n" + nonFinalStates;
	}
	
	public boolean productMachine(FDA M) {
		List<String> Mact = M.getActions();
		if (!Mact.containsAll(actions) || !actions.containsAll(Mact)) 
			return false; //Alphabet problems.
		Map<Integer,Map<String,Integer>> Mtrans = M.getTransitions();
		List<Integer> Mfails = M.getNonFinalStates();
		List<Pair> visitedStates = new ArrayList<Pair>();
		List<Pair> statesLeft = new ArrayList<Pair>();
		Map<Pair,Map<String, Pair>> Ptrans = new HashMap<Pair,Map<String,Pair>>();
		
		Pair Pini = new Pair(M.getFirstState(),firstState);

		statesLeft.add(Pini);

		while(!statesLeft.isEmpty())
		{
			Pair p = statesLeft.get(0);
			if(!visitedStates.contains(p)) {
				int m = p.getM();
				int n = p.getN();
				for(String action : actions) {
					if(Mtrans.get(m).containsKey(action) && transitions.get(n).containsKey(action)) {
						Map<String,Pair> map = Ptrans.containsKey(p) ?
							Ptrans.get(p) : new HashMap<String,Pair>();
						Pair p2 = new Pair(Mtrans.get(m).get(action),transitions.get(n).get(action));
						if(!statesLeft.contains(p2))
							statesLeft.add(p2);
						map.put(action, p2);
						Ptrans.put(p, map);
					}
					else if(!Mtrans.get(m).containsKey(action) && transitions.get(n).containsKey(action)
							&& !Mfails.contains(Mtrans.get(m).containsKey(action))) {
						return false;
					}
					//else; //Nothing to check, as we want N subset M
				}
				visitedStates.add(p);
			}
			statesLeft.remove(p);
		}
			
		return true;
	}
}
