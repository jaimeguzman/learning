package fr.ircam.lib.predict.fo;

public class Transition
{
	private int origin; // The state from which the transition was performed
	private int destination; // The state towards which the transition was performed
	private boolean isThroughSuffixLink; // True if the transition was performed through a suffix link

	public Transition(int origin, boolean isThroughSuffixLink)
	{
		this.origin = origin;
		this.isThroughSuffixLink = isThroughSuffixLink;
	}

	public Transition(int origin, int destination, boolean isThroughSuffixLink)
	{
		this.origin = origin;
		this.destination = destination;
		this.isThroughSuffixLink = isThroughSuffixLink;
	}

	public int getOrigin()
	{
		return origin;
	}

	public int getDestination()
	{
		return destination;
	}

	public boolean isThroughSuffixLink()
	{
		return isThroughSuffixLink;
	}
}
