package fr.ircam.lib.tool;

import java.util.ArrayList;

public class Descriptor
{
	private String name; // Name of descriptor
	String type; // Type of descriptor
	private ArrayList<Float> values; // Values of descriptor (float)

	public Descriptor(ArrayList<Float> values, String name, String type)
	{
		this.values = values;
		this.name = name;
		this.type = type;
	}

	public Descriptor(String name)
	{
		this.values = new ArrayList<Float>(); // Have to do new ?
		this.name = name;
		this.type = "Undef";
	}

	public float getSummedValue()
	{ // Get the sum of all stored descriptor values
		float sum = 0;
		for (int i = 0; i < getValues().size(); i++)
		{
			sum += (Float) getValues().get(i);
		}
		return sum;
	}

	public float getMeanValue()
	{ // Get the sum of all stored descriptor values
		float mean = 0;
		for (int i = 0; i < getValues().size(); i++)
		{
			mean += (Float) getValues().get(i);
		}
		return mean / getValues().size();
	}

	public float getValue(int i)
	{ // Get value n¡i
		float value = getValues().get(i);
		return value;
	}

	public void addValue(float value)
	{
		values.add(value);
	}

	public float[] toArray()
	{
		float[] valuesArray = new float[getValues().size()];
		for (int i = 0; i < getValues().size(); i++)
		{
			valuesArray[i] = (Float) getValues().get(i);
		}
		return valuesArray;
	}

	public ArrayList<Float> getValues()
	{
		return values;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
