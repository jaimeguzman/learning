package fr.ircam.lib.quantize;

import java.lang.System;

public class SOM {

	int mapWidth;
	int mapHeight;
	Node[][] nodes;
	float radius;
	float timeConstant;
	float learnRate = (float) 0.05;
	private int inputDimension;
	float learnDecay;
	float radiusDecay;

	public SOM(int h, int w, int n) {

		mapWidth = w;
		mapHeight = h;
		radius = (h + w) / 2;
		inputDimension = n;
		learnDecay = learnRate;
		radiusDecay = (mapWidth + mapHeight) / 2;

		nodes = new Node[h][w];
		// create nodes/initialize map
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				nodes[i][j] = new Node(n, h, w); // n: dimension, h/w:x/y
													// position in 2D SOM (in
													// 'nodes' array),
				nodes[i][j].x = i;
				nodes[i][j].y = j;
				// nodes[i][j].w[] is randomly initiated in Node's constructor
			}
		}

	}

	public void initTraining(int iterations) {

		timeConstant = (float) (iterations / Math.log(radius));
		//post("timeConstant" + Float.toString(timeConstant)); //DEBUG: value OK

	}

	public void train(int i, float w[]) { // W[]: one learning vector (on which we
									// train the weights of each SOM's element)

		radiusDecay = (float) (radius * Math.exp(-1 * i / timeConstant)); // the radius inside which neighbors are also affected decays as iteration count increases.
		learnDecay = (float) (learnRate * Math.exp(-1 * i / timeConstant));
		
		/*if (i == 100) {
			post("radiusDecay" + Float.toString(radiusDecay)); //DEBUG: value OK
			post("learnDecay" + Float.toString(learnDecay)); //DEBUG: value OK		
		}*/

		// get best matching unit
		int ndxComposite = bestMatch(w);
		int x = ndxComposite >> 16;
		int y = ndxComposite & 0x0000FFFF;
		
		// if (bDebug) println("bestMatch: " + x + ", " + y + " ndx: " +
		// ndxComposite);

		// scale best match and neighbors...
		for (int a = 0; a < mapHeight; a++) {
			for (int b = 0; b < mapWidth; b++) {

				// float d = distance(nodes[x][y], nodes[a][b]);
				float d = dist(nodes[x][y].x, nodes[x][y].y, nodes[a][b].x,
						nodes[a][b].y);
				
				float influence = (float) Math.exp((-1 * sq(d))	/ (2 * radiusDecay * i));
				// println("Best Node: ("+x+", "+y+") Current Node ("+a+", "+b+") distance: "+d+" radiusDecay: "+radiusDecay);

				if (d < radiusDecay)
					for (int k = 0; k < getInputDimension(); k++)
						nodes[a][b].w[k] += influence * learnDecay
								* (w[k] - nodes[a][b].w[k]);

			} // for j
		} // for i

	} // train()

	float distance(Node node1, Node node2) {
		return (float) Math.sqrt(sq(node1.x - node2.x) + sq(node1.y - node2.y));
	}

	public int bestMatch(float w[]) {
		float minDist = (float) Math.sqrt(getInputDimension());
		int minIndex = 0;

		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				float tmp = weight_distance(nodes[i][j].w, w);
				if (tmp < minDist) {
					minDist = tmp;
					minIndex = (i << 16) + j;
				} // if
			} // for j
		} // for i

		// note this index is x << 16 + y.
		return minIndex;
	}
	
	public float error(float w[]) {
		float minDist = (float) Math.sqrt(getInputDimension());

		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				float tmp = weight_distance(nodes[i][j].w, w);
				if (tmp < minDist) {
					minDist = tmp;
				} // if
			} // for j
		} // for i
		
		return minDist;
	}

	float weight_distance(float x[], float y[]) {
		if (x.length != y.length) {
			System.out
					.println("Error in SOM::distance(): array lens don't match");
		}
		float tmp = (float) 0.0;
		for (int i = 0; i < x.length; i++)
			tmp += sq((x[i] - y[i]));
		tmp = (float) Math.sqrt(tmp);
		return tmp;
	}

	float dist(int x1, int y1, int x2, int y2) {

		float distance = (float) Math.sqrt(sq(x1 - x2) + sq(y1 - y2));
		return distance;

	}
	
	float sq(float a) {
	    return a*a;
	  }

	public int getInputDimension() {
		return inputDimension;
	}


}
