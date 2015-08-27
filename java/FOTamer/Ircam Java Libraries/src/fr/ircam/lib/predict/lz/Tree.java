/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ircam.lib.predict.lz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

/*
 * Adapted from Yohann Coppel's tree structure
 * (ycoppel@google.com)
 * 
 */
public class Tree<T>
{
	private T head;
	private int counter = 1;
	private ArrayList<Tree<T>> leafs = new ArrayList<Tree<T>>();
	private Tree<T> parent = null;
	private HashMap<T, Tree<T>> locate = new HashMap<T, Tree<T>>(); // T: the type of keys maintained by this map; Tree<T>: the type of mapped values

	// portée de locate : head (T), pour repérer l'arbre (Tree<T>) ; toutes les leaf (en tant que leaf, donc que T), pour repérer les sous arbres (tree<T>). Les leaf
	// des sous arbres ne sont pas indexées
	public Tree(T head)
	{
		this.head = head;
		//locate.put(head, this); // don't find head in locate (our tree has duplicate entries)
	}

	public Tree<T> addLeaf(T leaf)
	{ // ajoute une feuille au niveau le plus bas de l'arbre (head)
		Tree<T> t = new Tree<T>(leaf);
		leafs.add(t);
		t.parent = this;
		//t.locate = this.locate; // à enlever ?
		locate.put(leaf, t); // Associates the specified value (leaf) with the specified key (t) in this map
		//incrementCounter();
		return t;
	}

	public void addLeaf(T root, T leaf)
	{
		if (locate.containsKey(root))
		{
			locate.get(root).addLeaf(leaf);
		} else
		{
			addLeaf(root).addLeaf(leaf);
		}
	}

	public void addLeaf(ArrayList<T> roots, T leaf)
	{
		if (roots.size() > 1) // proceed to next level
		{
			roots.remove(0);
			if (locate.containsKey(roots.get(0)))
			{
				locate.get(roots.get(0)).addLeaf(roots, leaf);
			} else
			{
				addLeaf(roots.get(0)).addLeaf(roots, leaf);
			}
		} else
		{
			addLeaf(roots.get(0), leaf); // reached final level: add leaf
		}
	}

	public void addLeaves(ArrayList<T> alphabet)
	{
		for (int i = 0; i < alphabet.size(); i++)
			addLeaf(alphabet.get(i));
	}

	public void incrementLeaves(T[] leaves)
	{
		for (int i = 0; i < leaves.length; i++)
			addLeaf(leaves[i]);
	}

	public void addLeaves(ArrayList<T> roots, ArrayList<T> alphabet)
	{
		if (roots.size() > 1) // proceed to next level
		{
			T currentRoot = roots.get(0);
			roots.remove(0);
			if (locate.containsKey(currentRoot))
			{
				locate.get(currentRoot).addLeaves(roots, alphabet);
			} else
			{
				addLeaf(currentRoot).addLeaves(roots, alphabet);
			}
		} else
		// reached final level: add leaves
		{
			for (int i = 0; i < alphabet.size(); i++)
				addLeaf(roots.get(0), alphabet.get(i));
		}
	}

	public void incrementThisRootCounter(int increment)
	{
		counter = counter + increment;
	}

	public void setThisRootCounter(int value)
	{
		counter = value;
	}

	public void incrementMultipleRootCounters(ArrayList<T> roots, int increment)
	{ // increase counters of roots given in arraylist roots
		if (roots.size() > 0) // proceed to next level
		{
			T currentRoot = roots.get(0);
			roots.remove(0);
			if (locate.containsKey(currentRoot))
			{
				locate.get(currentRoot).incrementThisRootCounter(increment); // NO : increment next root counter
				locate.get(currentRoot).incrementMultipleRootCounters(roots, increment);
			}
		}
	}

	public T getHead()
	{
		return head;
	}

	public int getCounter()
	{
		return counter;
	}

	public Tree<T> getTree(T element)
	{
		return locate.get(element);
	}

	public Tree<T> getParent()
	{
		return parent;
	}

	public Collection<T> getSuccessors(T root)
	{
		Collection<T> successors = new ArrayList<T>();
		Tree<T> tree = getTree(root);
		if (null != tree)
		{
			for (Tree<T> leaf : tree.leafs)
			{
				successors.add(leaf.head);
			}
		}
		return successors;
	}

	public Collection<Tree<T>> getSubTrees()
	{
		return leafs;
	}

	public static <T> Collection<T> getSuccessors(T of, Collection<Tree<T>> in)
	{
		for (Tree<T> tree : in)
		{
			if (tree.locate.containsKey(of))
			{
				return tree.getSuccessors(of);
			}
		}
		return new ArrayList<T>();
	}

	@Override
	public String toString()
	{
		return printTree(0);
	}

	private static final int indent = 2;

	private String printTree(int increment)
	{
		String s = "";
		String inc = "";
		for (int i = 0; i < increment; ++i)
		{
			inc = inc + " ";
		}
		s = inc + head + "(" + counter + ")";
		for (Tree<T> child : leafs)
		{
			s += "\n" + child.printTree(increment + indent);
		}
		return s;
	}

	public Tree<T> getRandomLeaf()
	{
		int sum = 0;
		Random generator = new Random();
		ArrayList<Integer> cumSum = new ArrayList<Integer>();
		for (int i = 0; i < leafs.size(); i++)
		{
			sum += leafs.get(i).getCounter();
			cumSum.add(sum);
		}
		float randomProb = generator.nextFloat(); // from 0 to cumSum (exclusive)
		int randomLeafIndex = 0;
		while ((float) cumSum.get(randomLeafIndex) / sum < randomProb)
			randomLeafIndex++;
		return leafs.get(randomLeafIndex);
	}

	public Tree<T> getRandomLeaf(Tree<T> tree, int minCount)
	{ // Don't take into account leaves with counter < minCount. Argument tree is a reference root level used in case of error.
		int sum = 0;
		Random generator = new Random();
		ArrayList<Integer> cumSum = new ArrayList<Integer>();
		ArrayList<Integer> leafIndices = new ArrayList<Integer>();
		for (int i = 0; i < leafs.size(); i++)
		{
			if (leafs.get(i).getCounter() >= minCount)
			{ // select matching leaves
				sum += leafs.get(i).getCounter();
				cumSum.add(sum);
				leafIndices.add(i);
			}
		}
		if (leafIndices.size() > 0)
		{
			float randomProb = generator.nextFloat();
			int prunedLeafIndex = 0;
			while ((float) cumSum.get(prunedLeafIndex) / sum < randomProb)
				// get index amongst selected leaves
				prunedLeafIndex++;
			int randomLeafIndex = leafIndices.get(prunedLeafIndex); // retrieve actual leaf index
			return leafs.get(randomLeafIndex);
		} else
		{
			return tree.getRandomLeaf(tree, minCount); // if no leaves were selected (insufficient counter values), return to root level defined by tree
		}
	}
	
	public Tree<T> getRandomLeaf(Tree<T> tree, int minCount, int exponent)
	{ // Don't take into account leaves with counter < minCount. Argument tree is a reference root level used in case of error. Exponentiate (normalized) probabilities
		int maxCounter = this.getMaxLeafCounter();
		float sum = 0;
		Random generator = new Random();
		ArrayList<Float> cumSum = new ArrayList<Float>();
		ArrayList<Integer> leafIndices = new ArrayList<Integer>();
		for (int i = 0; i < leafs.size(); i++)
		{
			if (leafs.get(i).getCounter() >= minCount)
			{ // select matching leaves
				sum += Math.pow( ( (double)leafs.get(i).getCounter() ) / maxCounter, (double) exponent); // Exponentiate normalized probabilities
				cumSum.add(sum);
				leafIndices.add(i);
			}
		}
		if (leafIndices.size() > 0)
		{
			float randomProb = generator.nextFloat();
			int prunedLeafIndex = 0;
			while ((float) cumSum.get(prunedLeafIndex) / sum < randomProb) // Still need to normalize here...
				// get index amongst selected leaves
				prunedLeafIndex++;
			int randomLeafIndex = leafIndices.get(prunedLeafIndex); // retrieve actual leaf index
			return leafs.get(randomLeafIndex);
		} else
		{
			return tree.getRandomLeaf(tree, minCount); // if no leaves were selected (insufficient counter values), return to root level defined by tree
		}
	}


	public int getMaxLeafCounter()
	{
		int max = 0;
		for (int i = 0; i < leafs.size(); i++)
		{
			if (leafs.get(i).getCounter() > max)
				max = leafs.get(i).getCounter();
		}
		return max;
	}

	public boolean isTerminal()
	{
		if (leafs.size() > 0)
			return false;
		else
			return true;
	}

	public boolean hasLeaf(T leaf)
	{
		if (locate.containsKey(leaf))
			return true;
		else
			return false;
	}

	public Tree<T> getLeaf(T leaf)
	{
		if (locate.containsKey(leaf))
			return locate.get(leaf);
		else
			return null;
	}
}