package fr.ircam.lib.tool;

public class TimedSymbol implements java.lang.Comparable
{
	private String symbol;
	private float startTime;
	private float duration;
	
	public TimedSymbol(String symbol, float startTime)
	{
		this.symbol = symbol;
		this.startTime = startTime;
	}
	
	public TimedSymbol(String symbol, float startTime, float duration)
	{
		this.symbol = symbol;
		this.startTime = startTime;
		this.duration = duration;
	}

	@Override
	public int compareTo(Object other)
	{
	      float otherStartTime = ((TimedSymbol) other).getStartTime(); 
	      float thisStartTime = this.getStartTime(); 
	      if (otherStartTime > thisStartTime)  return -1; 
	      else if(otherStartTime == thisStartTime) return 0; 
	      else return 1; 
	}

	public float getStartTime()
	{
		return startTime;
	}

	public String getSymbol()
	{
		return symbol;
	}
	
	public float getDuration()
	{
		return duration;
	}
	
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}

	public void setStartTime(float startTime)
	{
		this.startTime = startTime;
	}

	public void setDuration(float duration)
	{
		this.duration = duration;
	}

	public int getPolyphony()
	{
		String[] split = symbol.split(" ");
		return split.length;
	}
	
	
}
