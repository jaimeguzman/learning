package fr.ircam.lib.predict.fo;

import java.util.ArrayList;

public class PredictionReturn
{
	private String[] prediction;
	private ArrayList<Integer> stateSequence;

	public PredictionReturn(String[] prediction, ArrayList<Integer> stateSequence)
	{
		this.prediction = prediction;
		this.stateSequence = stateSequence;
	}
	
	public void setPrediction(String[] predictedSequence)
	{
		this.prediction = predictedSequence;
	}

	public void setStateSequence(ArrayList<Integer> stateSequence)
	{
		this.stateSequence = stateSequence;
	}

	public String[] getPrediction()
	{
		return prediction;
	}

	public ArrayList<Integer> getStateSequence()
	{
		return stateSequence;
	}
	
}
