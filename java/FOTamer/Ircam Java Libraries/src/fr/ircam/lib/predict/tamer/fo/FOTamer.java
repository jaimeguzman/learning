package fr.ircam.lib.predict.tamer.fo;

import java.util.ArrayList;

import fr.ircam.lib.predict.fo.PredictionReturn;
import fr.ircam.lib.predict.fo.ProbabilizedFO;
import fr.ircam.lib.predict.fo.Transition;



public class FOTamer
{
	private ArrayList<Transition> transitionMemory; // Last-in Last-out list storing previously selected transitions (position 0: most recent transition)
	private int memoryLength;
	private float stepSize;
	private ArrayList<Float> memoryWeights; // Weights ruling the distribution of reward amongst transitions stored in memory
	private ProbabilizedFO factorOracle;
	
	public FOTamer(ProbabilizedFO factorOracle)
	{
		transitionMemory = new ArrayList<Transition>();
		memoryWeights = new ArrayList<Float>();
		this.factorOracle = factorOracle;
	}
	
	public void distributeTransitionReward(float reward) throws Exception
	{ // Distribute transition reward based on weights fixed in transitionMemory
		if (memoryLength > 0)
		{
			if (stepSize > 0)
			{
				if (reward != 0f)
				{ // If reward
					float error = reward;
					for (int i = 0; i < transitionMemory.size(); i++)
					{ // Compute predicted error (gradient descent)
						Transition transition = transitionMemory.get(i);
						int origin = transition.getOrigin();
						int destination = transition.getDestination();
						float previousReinforcement; // The goal is to compute the amount of positive reinforcement already received for each transition (= coordinates of W vector in TAMER paper)
						if (origin < factorOracle.getTransitionProb().size() && destination < factorOracle.getTransitionProb().size())
						{	
							previousReinforcement = (factorOracle.getTransitionProb(origin, destination) - 0.5f); // copyProb.get(i) - 0.5f is the difference between the current probability and the original, neutral probability, ie the received reinforcement
							error -= previousReinforcement * memoryWeights.get(i); // One term of the sum obtained when computing the dot product W.CREDFEATS
						}
					}
					for (int i = 0; i < transitionMemory.size(); i++)
					{ // attribute credits (formula W = W + (stepSize x error . CREDFEATS) in TAMER paper
						Transition transition = transitionMemory.get(i);
						int origin = transition.getOrigin();
						int destination = transition.getDestination();
						if (origin < factorOracle.getTransitionProb().size() && destination < factorOracle.getTransitionProb().size())
						{
							if (!transition.isThroughSuffixLink())
							{
								float newProb = Math.min(Math.max(factorOracle.getTransitionProb(origin, destination) + stepSize * error * memoryWeights.get(i), 0f), 1f); // clamp between 0 and 1
								factorOracle.getTransitionProb().get(origin).put(destination, newProb);
							} else
							{ // If transition was done through a suffix link, a positive reinforcement (due to a positive error) means lowering the copyProb reinforcement
								float newProb = Math.min(Math.max(factorOracle.getTransitionProb(origin, destination) - stepSize * error * memoryWeights.get(i), 0f), 1f);
								factorOracle.getTransitionProb().get(origin).put(destination, newProb);
							}
						}
					}
				}
			} else
			{
				throw new Exception("Step size must be >0");
			}
		} else
		{
			throw new Exception("Bad transition memory settings");
		}
	}
	
	public ArrayList<Float> getMemoryWeights()
	{
		return memoryWeights;
	}

	
	public void clear()
	{
		transitionMemory.clear();

	}
	
	public void setMemory(ArrayList<Float> weights) throws Exception
	{ // Weight is a list of weights to give to stored transitions, from the most recent to the oldest
		if (weights.size() > 0)
		{
			memoryLength = weights.size(); // Set new memory size
			if (memoryLength < transitionMemory.size())
			{ // If new memory size is smaller than actual size, remove last (oldest) transitions
				transitionMemory = new ArrayList(transitionMemory.subList(0, memoryLength - 1));
			}
			// Normalize and set weights
			memoryWeights.clear();
			float minWeight = weights.get(0);
			float sum = 0;
			boolean allPositive = true;
			for (int i = 0; i < weights.size(); i++)
			{
				if (weights.get(i) < 0)
				{
					allPositive = false;
					break;
				}
				sum += weights.get(i);
			}
			if (allPositive && sum != 0)
			{
				for (int i = 0; i < weights.size(); i++)
				{
					float normalizedWeight = weights.get(i) / sum;
					memoryWeights.add(normalizedWeight);
				}
			} else if (sum == 0)
			{
				for (int i = 0; i < weights.size(); i++)
				{
					memoryWeights.add(0f);
				}
			} else
			{
				throw new Exception("Bad transition memory settings");
			}
		} else
		{
			throw new Exception("Bad transition memory settings");
		}
	}

	public void setStepSize(float stepSize) throws Exception
	{
		if (stepSize > 0)
		{
			this.stepSize = stepSize;
		} else
		{
			throw new Exception("Step size must be >0");
		}
	}
	
	public ArrayList<Transition> getTransitionMemory()
	{
		return transitionMemory;
	}

	private void updateTransitionMemory(int origin, int destination, boolean isThroughSuffixLink)
	{
		transitionMemory.add(0, new Transition(origin, destination, isThroughSuffixLink));
		if (transitionMemory.size() > memoryLength)
			transitionMemory.remove(transitionMemory.size() - 1);
	}

	public void updateTransitionMemory(PredictionReturn predictionReturn)
	{
		ArrayList<Integer> stateSequence = predictionReturn.getStateSequence();
		for (int i = 0; i < stateSequence.size() - 1; i++)
		{
			int origin = stateSequence.get(i);
			int destination = stateSequence.get(i+1);
			transitionMemory.add(0, new Transition(origin, destination, false));
			if (transitionMemory.size() > memoryLength)
				transitionMemory.remove(transitionMemory.size() - 1);
			
		}
	}
}
