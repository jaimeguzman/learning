package fr.ircam.lib.predict.fo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ProbabilizedFO extends FO
{
	private ArrayList<HashMap<Integer, Float>> transitionProb; // For each state, access transition probability by providing the transition's destination to a HashMap

	public ProbabilizedFO() throws Exception
	{ // TODO check if should throw exception
		super();
		transitionProb = new ArrayList<HashMap<Integer, Float>>(); // Probability of copying the original next symbol (by a forward transition) for each state
	}

	public void learn(String[] s) throws Exception
	{
		initDataStructures(s.length);
		for (int i = 1; i < s.length + 1; i++)
		{
			String symbol = s[i - 1]; // Add new symbol
			transitionMatrixReverse.get(i - 1).put(symbol, i); // Add transition from state i-1 to i by "symbol"
			transitionMatrix.get(i - 1).put(i, symbol);
			if (i - 1 == 0)
			{ // If dealing with transition from state 0 to state 1
				transitionProb.get(i - 1).put(i, 1f); // First state has 100% forward copy probability (no suffix link)
			} else
			{
				transitionProb.get(i - 1).put(i, 0.5f); // Init all probabilities at 0.5
			}
			int k = suffixLinks.get(i - 1); // Follow previous state's suffix links
			while (k != -1 && !transitionMatrixReverse.get(k).containsKey(symbol)) // Follow back suffix links while no transitions by "symbol" is found
			{
				transitionMatrixReverse.get(k).put(symbol, i); // If no transition, add one
				transitionMatrix.get(k).put(i, symbol);
				transitionProb.get(k).put(i, 0.5f);
				k = suffixLinks.get(k); // Since no transition has been found, look further back
			}
			if (k == -1) // if no transition existed previously, add a suffix link to first state
				suffixLinks.set(i, 0);
			else
				// otherwise (previously existing transition detected) add a suffix link to the transition's target
				suffixLinks.set(i, transitionMatrixReverse.get(k).get(symbol));
		}
	}

	public int predictNextState(int currentState)
	{ // Predict next state based on transition probabilities
		ArrayList<Float> cumProbSum = new ArrayList<Float>();
		float probSum = 0;
		for (int state : transitionProb.get(currentState).keySet())
		{ // Cumulative sum of this state's possible transition probabilities 
			probSum += transitionProb.get(currentState).get(state);
			cumProbSum.add(probSum);
		}
		float randomFloat = random.nextFloat() * probSum;
		int randomIndex = 0;
		while (randomIndex < cumProbSum.size())
		{
			if (cumProbSum.get(randomIndex) < randomFloat)
				randomIndex = randomIndex + 1;
			else
				break;
		}
		return (Integer) transitionProb.get(currentState).keySet().toArray()[randomIndex];
	}

	public void clear()
	{ // Erase all data structures (not memory settings)
		super.clear();
		transitionProb.clear();
	}

	protected void initDataStructures(int sequenceLength)
	{
		super.initDataStructures(sequenceLength);
		for (int i = 0; i < sequenceLength; i++) // init transition probability matrix. Number of states having transitions = total number of states - 1 = sequenceLength
		{
			transitionProb.add(new HashMap<Integer, Float>());
		}
	}

	public ArrayList<HashMap<Integer, Float>> getTransitionProb()
	{
		return transitionProb;
	}

	public float getTransitionProb(int origin, int destination)
	{ // Get probability of transition from state origin to state destination //TODO add exception
		float prob = transitionProb.get(origin).get(destination);
		return prob;
	}
}
