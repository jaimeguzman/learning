package fr.ircam.lib.predict.fo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FO
{
	protected ArrayList<HashMap<String, Integer>> transitionMatrixReverse; // For each state, access transition destinations by providing the transition's symbol to a HashMap
	protected ArrayList<HashMap<Integer, String>> transitionMatrix; // For each state, access transition symbols by providing the transition's destination to a HashMap
	protected ArrayList<Integer> suffixLinks; // A single state is referred to with the same index in suffixLinks and copyProb
	protected Random random;
	protected float copyProb = 0.5f;
	protected int maxPredictionIteration = 100; // Maximum number of iterations of a single prediction step

	public FO() throws Exception
	{ // TODO check if should throw exception
		random = new Random();
		suffixLinks = new ArrayList<Integer>();
		transitionMatrixReverse = new ArrayList<HashMap<String, Integer>>();
		transitionMatrix = new ArrayList<HashMap<Integer, String>>();
	}

	public void learn(String[] s) throws Exception
	{
		initDataStructures(s.length);
		for (int i = 1; i < s.length + 1; i++)
		{
			String symbol = s[i - 1]; // Add new symbol
			transitionMatrixReverse.get(i - 1).put(symbol, i); // Add transition from state i-1 to i by "symbol"
			transitionMatrix.get(i - 1).put(i, symbol);
			int k = suffixLinks.get(i - 1); // Follow previous state's suffix links
			while (k != -1 && !transitionMatrixReverse.get(k).containsKey(symbol)) // Follow back suffix links while no transitions by "symbol" is found
			{
				transitionMatrixReverse.get(k).put(symbol, i); // If no transition, add one
				transitionMatrix.get(k).put(i, symbol);
				k = suffixLinks.get(k); // Since no transition has been found, look further back
			}
			if (k == -1) // if no transition existed previously, add a suffix link to first state
				suffixLinks.set(i, 0);
			else
				// otherwise (previously existing transition detected) add a suffix link to the transition's target
				suffixLinks.set(i, transitionMatrixReverse.get(k).get(symbol));
		}
	}

	public PredictionReturn predict(int predictionLength, int start) throws Exception
	{ // Predict state sequence
		if (predictionLength > 0)
		{
			if (suffixLinks.size() > 0)
			{
				if (start >= 0 && start < suffixLinks.size())
				{
					String prediction[] = new String[predictionLength];
					int k = start; // k: current state. Will be the new state at the end of the prediction
					ArrayList<Integer> stateSequence = new ArrayList<Integer>(); // Sequence of states visited during prediction
					for (int i = 0; i < predictionLength; i++)
					{
						// Predict next symbol (class)
						String predictedClass = "";
						if (suffixLinks.get(k) != -1)
						{ // For states having suffix links, 2 choices : do a forward transition, or provide an alternative path
							int previousState = k;
							double randomProb = Math.random();
							if (randomProb < copyProb && k < transitionMatrix.size())
							{ // If random copy has been selected and current state is not the last
								predictedClass = transitionMatrix.get(k).get(k + 1); // Copy symbols forward
								stateSequence.add(previousState); // There is transition from previousState (= k) to k+1 ; add previousState, k+1 will be added later
								k = k + 1;
							} else
							{ // If failed to do a forward transition, follow back suffix links
								int stateAfterSuffixTransition = suffixLinks.get(previousState); // Follow back suffix link to an intermediate state
								int iteration = 0;
								do
								{
									iteration++;
									if (iteration > maxPredictionIteration) // Only try this 100 times
										break;
									k = predictNextState(stateAfterSuffixTransition); // Follow transition link
								} while (k == previousState + 1); // Force selection of transitions actually providing an alternate path through the oracle
								predictedClass = transitionMatrix.get(stateAfterSuffixTransition).get(k); // select next symbol
								stateSequence.add(stateAfterSuffixTransition); // There is transition from stateAfterSuffixTransition to k ; add stateAfterSuffixTransition, k will be added later
							}
						} else
						{ // If no suffix link: only possible to copy forward next symbol
							predictedClass = transitionMatrix.get(k).get(k + 1);
							stateSequence.add(k); // There is transition from k to k+1 ; add k, k+1 will be added later
							k = k + 1;
						}
						prediction[i] = predictedClass; // Record predicted class
					}
					stateSequence.add(k); // Add last reached state
					PredictionReturn predictionReturn = new PredictionReturn(prediction, stateSequence);
					return predictionReturn;
				} else
				{
					throw new Exception("Wrong start state");
				}
			} else
			{
				throw new Exception("Oracle structure is empty");
			}
		} else
		{
			throw new Exception("Prediction length must be >0");
		}
	}

	public int predictNextState(int currentState)
	{ // Uniform prediction of next state amongst all available
		int randomIndex = random.nextInt(transitionMatrix.get(currentState).keySet().size());
		return (Integer) transitionMatrix.get(currentState).keySet().toArray()[randomIndex];
	}

	public void clear()
	{ // Erase all data structures (not memory settings)
		suffixLinks.clear();
		transitionMatrixReverse.clear();
		transitionMatrix.clear();
	}

	public int getOracleSize()
	{ // Return oracle size (number of states)
		return suffixLinks.size();
	}

	protected void initDataStructures(int sequenceLength)
	{
		clear();
		suffixLinks.add(-1); // -1 used for null suffix link by convention
		for (int i = 0; i < sequenceLength; i++)
		{
			suffixLinks.add(0); // init suffix links. Number of non-null links = number of states - 1 = sequenceLength
		} // Total length of suffixLinks = sequenceLength + 1 = number of states
		for (int i = 0; i < sequenceLength; i++) // init transition matrix. Number of states having transitions = total number of states - 1 = sequenceLength
		{
			transitionMatrixReverse.add(new HashMap<String, Integer>());
			transitionMatrix.add(new HashMap<Integer, String>());
		}
	}

	public ArrayList<HashMap<Integer, String>> getTransitionMatrix()
	{
		return transitionMatrix;
	}

	public void setCopyProb(float copyProb)
	{
		this.copyProb = copyProb;
	}



	public static void main (String args[]){

		System.out.println(  "Hello World " );
	}






}
