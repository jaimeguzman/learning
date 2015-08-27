package fr.ircam.lib.predict.lz;

public class MidiEvent implements java.lang.Comparable
{
	private String notes;
	private float startTime;
	
	public MidiEvent(String notes, float startTime)
	{
		this.notes = notes;
		this.startTime = startTime;
	}

	@Override
	public int compareTo(Object other)
	{
	      float otherStartTime = ((MidiEvent) other).getStartTime(); 
	      float thisStartTime = this.getStartTime(); 
	      if (otherStartTime > thisStartTime)  return -1; 
	      else if(otherStartTime == thisStartTime) return 0; 
	      else return 1; 
	}

	public float getStartTime()
	{
		return startTime;
	}

	public String getNotes()
	{
		return notes;
	}
	
	public int getPolyphony()
	{
		String[] split = notes.split(" ");
		return split.length;
	}
	
	
}
