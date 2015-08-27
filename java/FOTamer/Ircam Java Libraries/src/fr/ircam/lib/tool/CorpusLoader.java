package fr.ircam.lib.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import net.ianislallemand.lib.tool.StringLoader;

public class CorpusLoader
{
	public CorpusLoader()
	{
	}

	static public Description loadDescription(String path, String fileName)
	{
		// Give filename without extension (.ds.txt)
		File[] files = StringLoader.listFiles(path);
		String descriptionName = "";
		ArrayList<String> descriptorNames = new ArrayList<String>(); // Descriptor names for current description
		ArrayList<String> descriptorTypes = new ArrayList<String>(); // Descriptor types for current description storing descriptor names and values
		ArrayList<Descriptor> descriptors = new ArrayList<Descriptor>(); // Descriptors objects storing descriptor names and values
		int i = 0;
		while (i < files.length)
		{
			File f = files[i];
			if (f.getName().equals(fileName + ".ds.txt"))
			{ // Load current description, identified by .ds.txt file
				// println(f.getName()+"...");
				String[] current_file = StringLoader.loadStrings(f);
				// Get description name
				descriptionName = f.getName().substring(0, f.getName().length() - 7);
				// Get descriptor names for current description
				for (int j = 0; j < current_file.length; j++)
				{
					String[] names = split(current_file[j], ' ');
					if (names.length == 8)
					{
						String name = names[0]; // .ds.txt files store descriptor names at the beginning of each line
						String type = names[2]; // Type is line's third element
						descriptorNames.add(name);
						descriptorTypes.add(type);
					}
				}
				// Get descriptor values: find associated .ud.txt file
				for (int j = 0; j < files.length; j++)
				{
					File f_ud = files[j];
					if (f_ud.getName().equals(descriptionName + ".ud.txt"))
					{
						String[] current_file_ud = StringLoader.loadStrings(f_ud);
						// Get descriptor values for descriptor n¡ k
						for (int k = 0; k < descriptorNames.size(); k++)
						{
							ArrayList<Float> descriptorValues = new ArrayList<Float>();
							String descriptorName = (String) descriptorNames.get(k);
							String descriptorType = (String) descriptorTypes.get(k);
							// Get k-th descriptor value on each line of .ud.txt file (.ud.txt files give each descriptor a column in which its values are stored)
							for (int l = 0; l < current_file_ud.length; l++)
							{
								String[] values = split(current_file_ud[l], ' ');
								if (values.length == descriptorNames.size())
								{
									String value = values[k];
									descriptorValues.add(Float.valueOf(value.trim()).floatValue());
									//post(Float.valueOf(value.trim()).floatValue());
									//post(value);
								}
							}
							descriptors.add(new Descriptor(descriptorValues, descriptorName, descriptorType));
							// println("loading "+descriptorName+" ("+descriptorType+") values...");
						}
					}
				}
				break;
			}
			i++; // Look if next file matches given filename
		}
		// Store in description
		Description description = new Description(descriptors, 1, descriptionName);
		// println("loaded "+descriptors.size()+" descriptors.\n");
		return description;
	}
	
	static public String[] split(String what, char delim)
	{ // TODO use standard java split and not this stuff from processing
		// do this so that the exception occurs inside the user's
		// program, rather than appearing to be a bug inside split()
		if (what == null)
			return null;
		// return split(what, String.valueOf(delim)); // huh
		char chars[] = what.toCharArray();
		int splitCount = 0; // 1;
		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] == delim)
				splitCount++;
		}
		// make sure that there is something in the input string
		// if (chars.length > 0) {
		// if the last char is a delimeter, get rid of it..
		// if (chars[chars.length-1] == delim) splitCount--;
		// on second thought, i don't agree with this, will disable
		// }
		if (splitCount == 0)
		{
			String splits[] = new String[1];
			splits[0] = new String(what);
			return splits;
		}
		// int pieceCount = splitCount + 1;
		String splits[] = new String[splitCount + 1];
		int splitIndex = 0;
		int startIndex = 0;
		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] == delim)
			{
				splits[splitIndex++] = new String(chars, startIndex, i - startIndex);
				startIndex = i + 1;
			}
		}
		// if (startIndex != chars.length) {
		splits[splitIndex] = new String(chars, startIndex, chars.length - startIndex);
		// }
		return splits;
	}


}
