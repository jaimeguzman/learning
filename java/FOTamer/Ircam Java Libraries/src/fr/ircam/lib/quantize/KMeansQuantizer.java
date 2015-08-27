package fr.ircam.lib.quantize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DistanceFunction;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.instance.Normalize;

public class KMeansQuantizer
{
	private Instances instances; // data instances to be quantized into a given number of symbols
	private HashMap<Integer, ArrayList<Instance>> instancesPerClass;
	private HashMap<Instance, Integer> instanceIndices;
	private HashMap<Instance, Float> instanceProb; // (non-normalized) probability of selecting any given instance (relative to other instances in the same class)
	private SimpleKMeans simpleKMeans;
	private int nAttributes;
	private int nClasses; // Number of symbols used for quantization
	private Random random;

	public KMeansQuantizer(int nClasses, int nAttributes) throws Exception
	{
		if (nClasses > 0 && nAttributes > 0)
		{
			random = new Random();
			this.nClasses = nClasses;
			this.nAttributes = nAttributes;
			instancesPerClass = new HashMap<Integer, ArrayList<Instance>>();
			instanceIndices = new HashMap<Instance, Integer>();
			instanceProb = new HashMap<Instance, Float>();
			simpleKMeans = new SimpleKMeans();
			FastVector attributes = new FastVector(nAttributes);
			for (int i = 0; i < nAttributes; i++)
			{
				Attribute attribute = new Attribute(Integer.toString(i));
				attributes.addElement(attribute);
			}
			instances = new Instances("data", attributes, 1);
			simpleKMeans.setNumClusters(nClasses);
		} else
			throw new Exception("Arguments must be >0");
	}

	public void addVector(ArrayList<Float> data) throws Exception
	{
		if (data.size() == nAttributes)
		{
			Instance instance = new Instance(nAttributes); // number of attributes
			for (int i = 0; i < nAttributes; i++)
			{
				instance.setValue(i, data.get(i));
			}
			instances.add(instance);
		} else
		{
			throw new Exception("Data dimension does not match the specified numer of attributes");
		}
	}

	public String[] quantizeData() throws Exception
	{
		Normalize normalize = new Normalize();
		normalize.setInputFormat(instances);
		for (int i = 0; i < instances.numInstances(); i++)
		{ // Read all instances to set normalization
			normalize.input(instances.instance(i));
		}
		instances.delete();
		while (normalize.batchFinished())
		{ // While there are instances pending output
			instances.add(normalize.output()); // Replace with normalized instances
		}
		simpleKMeans = new SimpleKMeans();
		simpleKMeans.setNumClusters(nClasses);
		simpleKMeans.buildClusterer(instances);
		String classIndices[] = new String[instances.numInstances()];
		instanceIndices.clear();
		instanceProb.clear();
		instancesPerClass.clear();
		DistanceFunction distanceFunction = simpleKMeans.getDistanceFunction();
		Instances classCentroids = simpleKMeans.getClusterCentroids();
		for (int i = 0; i < instances.numInstances(); i++)
		{
			Instance instance = instances.instance(i);
			instanceIndices.put(instance, i);
			int thisClass = simpleKMeans.clusterInstance(instance);
			Instance centroid = classCentroids.instance(thisClass);
			float distanceToCentroid = (float) distanceFunction.distance(centroid, instance); // Compute probability according to distance to class' centroid
			instanceProb.put(instance, 1 / distanceToCentroid); // Large init values would be a problem if learning instance probabilities
			classIndices[i] = Integer.toString(thisClass); // Symbols as string for Oracle learning
			if (instancesPerClass.get(thisClass) != null)
			{ // Record the instance's class label
				instancesPerClass.get(thisClass).add(instance);
			} else
			{
				ArrayList<Instance> instancesOfThisClass = new ArrayList<Instance>();
				instancesOfThisClass.add(instance);
				instancesPerClass.put(thisClass, instancesOfThisClass);
			}
		}
		return classIndices;
	}

	public int getInstanceClass(int instanceId)
	{ // Will return -1 if error
		Instance instance = instances.instance(instanceId);
		int theClass = -1;
		try
		{
			theClass = simpleKMeans.clusterInstance(instance);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return theClass;
	}

	public int predictInstance(int predictedClass)
	{
		ArrayList<Instance> instancesOfThisClass = instancesPerClass.get(predictedClass);
		ArrayList<Float> cumProbSum = new ArrayList<Float>();
		float probSum = 0;
		for (int j = 0; j < instancesOfThisClass.size(); j++)
		{ // Cumulative sum of this class' instance probabilities 
			probSum += instanceProb.get(instancesOfThisClass.get(j));
			cumProbSum.add(probSum);
		}
		float randomFloat = random.nextFloat() * probSum;
		int randomIndex = 0;
		while (randomIndex < cumProbSum.size())
		{
			if (cumProbSum.get(randomIndex) < randomFloat)
				randomIndex = randomIndex + 1;
			else
				break;
		}
		if (randomIndex >= cumProbSum.size()) // That shouldn't happen
			randomIndex = random.nextInt(instancesOfThisClass.size());
		Instance predictedInstance = instancesOfThisClass.get(randomIndex);
		return instanceIndices.get(predictedInstance);
	}

	public int predictInstanceGreedy(int predictedClass)
	{
		ArrayList<Instance> instancesOfThisClass = instancesPerClass.get(predictedClass);
		float maxProb = 0;
		int predictedIndex = 0;
		for (int j = 0; j < instancesOfThisClass.size(); j++)
		{
			float prob = instanceProb.get(instancesOfThisClass.get(j));
			if (prob > maxProb)
			{
				maxProb = prob;
				predictedIndex = j;
			}
		}
		Instance predictedInstance = instancesOfThisClass.get(predictedIndex);
		return instanceIndices.get(predictedInstance);
	}

	public void clear()
	{
		instances.delete();
	}

	public int getDim()
	{
		return nAttributes;
	}
}
