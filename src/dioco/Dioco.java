package dioco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dioco {
	public static boolean productMachine(IOTS M, IOTS N) {
		List<String> Min = M.getInputs();
		List<String> Mout = M.getOutputs();
		List<String> Nin = N.getInputs();
		List<String> Nout = N.getOutputs();
		if (!Nin.containsAll(Min) || !Mout.containsAll(Nout)) 
			return false; //Alphabet problems.
		Map<Integer,Map<String,Integer>> Mtrans = M.determinize();
		Map<Integer,Map<String,Integer>> Ntrans = N.determinize();
		
		List<Pair> visitedStates = new ArrayList<Pair>();
		List<Pair> statesLeft = new ArrayList<Pair>();
		Map<Pair,Map<String, Pair>> Ptrans = new HashMap<Pair,Map<String,Pair>>();
		
		Integer Mini = M.getFirstState();
		Integer Nini = N.getFirstState();
		Pair Pini = new Pair(Mini,Nini);

		statesLeft.add(Pini);

		while(!statesLeft.isEmpty())
		{
			Pair p = statesLeft.get(0);
			if(!visitedStates.contains(p)) {
				int m = p.getM();
				int n = p.getN();
				for(String input : Min) {
					if(Mtrans.get(m).containsKey(input) && Ntrans.get(n).containsKey(input)) {
						Map<String,Pair> map = Ptrans.containsKey(p) ?
							Ptrans.get(p) : new HashMap<String,Pair>();
						Pair p2 = new Pair(Mtrans.get(m).get(input),Ntrans.get(n).get(input));
						if(!statesLeft.contains(p2))
							statesLeft.add(p2);
						map.put(input, p2);
						Ptrans.put(p, map);
					}
					//else;
				}
				for(String output : Nout) {
					if(Mtrans.get(m).containsKey(output) && Ntrans.get(n).containsKey(output)) {
						Map<String,Pair> map = Ptrans.containsKey(p) ?
							Ptrans.get(p) : new HashMap<String,Pair>();
						Pair p2 = new Pair(Mtrans.get(m).get(output),Ntrans.get(n).get(output));
						if(!statesLeft.contains(p2))
							statesLeft.add(p2);
						map.put(output, p2);
						Ptrans.put(p, map);
					}
					else if(!Mtrans.get(m).containsKey(output) && Ntrans.get(n).containsKey(output)) {
						return false;
					}
					//else;
				}
				visitedStates.add(p);
			}
			statesLeft.remove(p);
		}
			
		return true;
	}
}
