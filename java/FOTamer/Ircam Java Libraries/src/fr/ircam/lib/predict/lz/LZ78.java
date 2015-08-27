package fr.ircam.lib.predict.lz;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class LZ78
{
	/**
	 * LZ78 compression algorithm, prediction version (Langdon 1983, Rissanen
	 * 1983)
	 */
	private Tree<String> tree;
	private ArrayList<String> alphabet;
	private String firstSymbol;
	private String rootHeadName; // Name of root level's head

	public LZ78(String rootHeadName)
	{ // LZ78
		this.rootHeadName = rootHeadName;
		tree = new Tree<String>(this.rootHeadName);
		tree.setThisRootCounter(0); // this.rootHeadName counter should be 0
		alphabet = new ArrayList<String>();
	}

	public LZ78(String rootHeadName, ArrayList<String> alphabet, String[] s) throws Exception
	{ // LZ78
		Set<String> unique = new HashSet<String>(alphabet);
		if (unique.size() < alphabet.size())
		{
			throw new Exception("alphabet contains duplicate symbols");
		} else if (alphabet.contains(rootHeadName))
		{
			throw new Exception(rootHeadName + " is a reserved symbol");
		} else
		{
			this.rootHeadName = rootHeadName;
			tree = new Tree<String>(this.rootHeadName);
			tree.setThisRootCounter(0); // this.rootHeadName counter should be 0
			this.alphabet = alphabet;
			Set<ArrayList<String>> dictionary = new HashSet<ArrayList<String>>();
			ArrayList<String> w = new ArrayList<String>();
			firstSymbol = s[0];
			tree.addLeaves(this.alphabet); // add one node for each symbol of the alphabet //tree.addlead(this.rootHeadName, this.alphabet)
			for (int i = 0; i < s.length; i++)
			{
				String c = s[i];
				w.add(c);
				if (!dictionary.contains(w))
				{
					dictionary.add(w);
					ArrayList<String> roots = new ArrayList<String>(w);
					ArrayList<String> roots2 = new ArrayList<String>(w); // need a second copy for incrementMultipleRootCounters
					tree.addLeaves(roots, this.alphabet); // add new symbol
					tree.incrementMultipleRootCounters(roots2, this.alphabet.size() - 1); // -1 : don't count the breeding root twice
					w = new ArrayList<String>(); // reset
				}
			}
			if (w.size() != 0)
			{ // s'il reste un symbole non ajouté au dictionnaire à la fin
			}
		}
	}

	public void setAlphabet(ArrayList<String> newAlphabet) throws Exception
	{
		Set<String> unique = new HashSet<String>(newAlphabet);
		if (unique.size() < newAlphabet.size())
		{
			throw new Exception("alphabet contains duplicate symbols");
		} else if (alphabet.contains(this.rootHeadName))
		{
			throw new Exception(rootHeadName + " is a reserved symbol");
		} else
		{
			alphabet = newAlphabet;
		}
	}

	public String getFirstSymbol()
	{
		return firstSymbol;
	}

	public void learn(String[] s) throws Exception
	{
		Set<ArrayList<String>> dictionary = new HashSet<ArrayList<String>>();
		ArrayList<String> w = new ArrayList<String>();
		firstSymbol = s[0];
		tree.addLeaves(this.alphabet); // add one node for each symbol of the alphabet //tree.addlead(this.rootHeadName, this.alphabet)
		for (int i = 0; i < s.length; i++)
		{
			String c = s[i];
			if (alphabet.contains(c))
			{
				w.add(c);
				if (!dictionary.contains(w))
				{
					dictionary.add(w);
					ArrayList<String> roots = new ArrayList<String>(w);
					ArrayList<String> roots2 = new ArrayList<String>(w); // need a second copy for incrementMultipleRootCounters
					tree.addLeaves(roots, this.alphabet); // add new symbol
					tree.incrementMultipleRootCounters(roots2, this.alphabet.size() - 1); // -1 : don't count the breeding root twice
					w = new ArrayList<String>(); // reset
				}
			} else
			{
				throw new Exception("unknown symbol at position " + i);
			}
		}
		if (w.size() != 0)
		{ // s'il reste un symbole non ajouté au dictionnaire à la fin
		}
	}

	public String printTree()
	{
		return tree.toString();
	}

	public String predict(int predictionLength) // generate from this.rootHeadName
	{
		String prediction = new String();
		Tree<String> lastPosition = tree;
		for (int i = 0; i < predictionLength; i++)
		{
			lastPosition = lastPosition.getRandomLeaf();
			prediction += lastPosition.getHead();
			if (lastPosition.isTerminal()) // if reached terminal position in tree (no leaves), start at root level
				lastPosition = tree;
		}
		return prediction;
	}

	public String predict(int predictionLength, int minCount)
	{ // generate from this.rootHeadName
		if (minCount <= tree.getMaxLeafCounter())
		{ // maximum counter is found at root level (defined by "tree")
			String prediction = new String();
			Tree<String> lastPosition = tree;
			for (int i = 0; i < predictionLength; i++)
			{
				lastPosition = lastPosition.getRandomLeaf(tree, minCount);
				if (i > 0)
					prediction += " " + lastPosition.getHead();
				else
					prediction += lastPosition.getHead();
			}
			return prediction;
		} else
		{
			return "";
		}
	}
	
	public String predict(int predictionLength, int minCount, int exponent)
	{ // generate from this.rootHeadName
		if (minCount <= tree.getMaxLeafCounter())
		{ // maximum counter is found at root level (defined by "tree")
			String prediction = new String();
			Tree<String> lastPosition = tree;
			for (int i = 0; i < predictionLength; i++)
			{
				lastPosition = lastPosition.getRandomLeaf(tree, minCount, exponent);
				if (i > 0)
					prediction += " " + lastPosition.getHead();
				else
					prediction += lastPosition.getHead();
			}
			return prediction;
		} else
		{
			return "";
		}
	}
	
	public String[] predict(int predictionLength, int minCount, String start) throws Exception
	{ // generate from symbol given by start (must be immediately below this.rootHeadName)
		String prediction[] = new String[predictionLength];
		if (minCount <= tree.getMaxLeafCounter()) // maximum counter is found at root level (defined by "tree")
		{
			Tree<String> lastPosition = tree;
			for (int i = 0; i < predictionLength; i++)
			{
				if (i == 0)
				{
					if (tree.hasLeaf(start))
					{
						lastPosition = tree.getLeaf(start);
						prediction[i] = start;
					} else
					{
						lastPosition = lastPosition.getRandomLeaf(tree, minCount);
						prediction[i] = lastPosition.getHead();
					}
				} else
				{
					lastPosition = lastPosition.getRandomLeaf(tree, minCount);
					prediction[i] = lastPosition.getHead();
				}
			}
			return prediction;
		} else
			throw new Exception("tree doesn't contain enough symbols");
	}

	public String[] predict(int predictionLength, int minCount, int exponent, String start) throws Exception
	{ // generate from symbol given by start (must be immediately below this.rootHeadName)
		String prediction[] = new String[predictionLength];
		if (minCount <= tree.getMaxLeafCounter()) // maximum counter is found at root level (defined by "tree")
		{
			Tree<String> lastPosition = tree;
			for (int i = 0; i < predictionLength; i++)
			{
				if (i == 0)
				{
					if (tree.hasLeaf(start))
					{
						lastPosition = tree.getLeaf(start);
						prediction[i] = start;
					} else
					{
						lastPosition = lastPosition.getRandomLeaf(tree, minCount, exponent);
						prediction[i] = lastPosition.getHead();
					}
				} else
				{
					lastPosition = lastPosition.getRandomLeaf(tree, minCount, exponent);
					prediction[i] = lastPosition.getHead();
				}
			}
			return prediction;
		} else
			throw new Exception("tree doesn't contain enough symbols");
	}


	
	public void clearTree()
	{
		tree = new Tree<String>(this.rootHeadName);
		tree.setThisRootCounter(0); // this.rootHeadName counter should be 0
	}


	public static void createSequence(String filename, String phrase, ArrayList<String> alphabet, ArrayList<String> notes, int velocity)
	{
		File outputFile = new File(filename);
		Sequence sequence = null;
		try
		{
			sequence = new Sequence(Sequence.PPQ, 2); // 2: # ticks per beat
		} catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		Track track = sequence.createTrack();
		String[] symbols = phrase.split(" ");
		for (int i = 0; i < symbols.length; i++)
		{
			int symbolIndex = alphabet.indexOf(symbols[i]);
			String[] symbolNotes = notes.get(symbolIndex).split(" ");
			for (int j = 0; j < symbolNotes.length; j++)
			{
				try
				{
					int midiNote = Integer.parseInt(symbolNotes[j]);
					track.add(createNoteOnEvent(midiNote, i, velocity));
					track.add(createNoteOffEvent(midiNote, i + 1, velocity));
				} catch (NumberFormatException e)
				{ // Insert silence
				}
			}
		}
		try
		{
			MidiSystem.write(sequence, 0, outputFile);
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static MidiEvent createNoteOnEvent(int nKey, long lTick, int velocity)
	{
		return createNoteEvent(ShortMessage.NOTE_ON, nKey, velocity, lTick);
	}

	private static MidiEvent createNoteOffEvent(int nKey, long lTick, int velocity)
	{
		return createNoteEvent(ShortMessage.NOTE_OFF, nKey, 0, lTick);
	}

	private static MidiEvent createNoteEvent(int nCommand, int nKey, int nVelocity, long lTick)
	{
		ShortMessage message = new ShortMessage();
		try
		{
			message.setMessage(nCommand, 0, nKey, nVelocity); // always on channel 1
		} catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		MidiEvent event = new MidiEvent(message, lTick);
		return event;
	}

	public static String duplicateSequence(String sequence, int times)
	{
		String newSequence = new String();
		for (int i = 0; i < times; i++)
		{
			if (i == 0)
				newSequence += sequence;
			else
				newSequence += " " + sequence;
		}
		return newSequence;
	}
}