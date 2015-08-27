package fr.ircam.lib.tool;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

import net.ianislallemand.lib.tool.StringSaver;

public class Description
{
	int id;
	String name;
	private ArrayList<Descriptor> descriptors; // List of Descriptor objects, storing instantaneous
	// descriptor values
	private int nFrames; // Number of analysis frames used for each descriptor

	public Description()
	{
		id = 1;
		name = "Undefined";
		setDescriptors(new ArrayList<Descriptor>());
		nFrames = 0;
	}

	Description(ArrayList<Descriptor> descriptors)
	{
		id = 1;
		name = "Undefined";
		setDescriptors(descriptors);
		Descriptor first = descriptors.get(0);
		nFrames = first.getValues().size();
	}

	Description(ArrayList<Descriptor> descriptors, int id, String name)
	{
		this.id = id;
		this.name = name;
		setDescriptors(descriptors);
		Descriptor first = descriptors.get(0);
		nFrames = first.getValues().size();
	}

	void setName(String name)
	{
		this.name = name;
	}

	void setId(int id)
	{
		this.id = id;
	}

	public void addFrame(float[] descriptorValues)
	{
		if (descriptorValues.length == getDescriptors().size())
		{
			for (int i = 0; i < getDescriptors().size(); i++)
			{
				Descriptor newDescriptor = getDescriptors().get(i);
				newDescriptor.addValue(descriptorValues[i]);
				getDescriptors().set(i, newDescriptor);
			}
			nFrames++;
		}
	}

	public void setDescriptors(ArrayList<Descriptor> descriptors)
	{
		this.descriptors = descriptors;
	}

	public void addDescriptor(Descriptor descriptor)
	{
		getDescriptors().add(descriptor);
	}

	public void clear()
	{
		name = "Undefined";
		id = 1;
		getDescriptors().clear();
		nFrames = 0;
	}

	public int getNFrames()
	{
		return nFrames;
	}

	public ArrayList<Descriptor> getDescriptors()
	{
		return descriptors;
	}
}
