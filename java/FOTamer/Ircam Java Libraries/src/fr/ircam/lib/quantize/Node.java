package fr.ircam.lib.quantize;

import java.util.Random;

public class Node {

	int x, y;
	int weightCount;
	float[] w;

	Node(int n, int x, int y) {
		this.x = x;
		this.y = y;
		weightCount = n;
		w = new float[weightCount];

		// initialize weights to random values
		Random generator = new Random();

		for (int i = 0; i < weightCount; i++) {
			//w[i] = (float) (0.2 + generator.nextFloat() * 0.8);
			w[i] = (float) (generator.nextFloat()*2 - 1);
		}

	}

}
