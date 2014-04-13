package org.meanworks.engine.util;

/**
 * Copyright (C) 2013 Steffen Evensen
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Meanz
 */
public class PerlinNoise {

	public int seed;
	public double persistence;
	public double frequency;
	public double amplitude;
	public int octaves;

	public PerlinNoise() {
		seed = 1327;
		persistence = 0.7d;
		frequency = 0.006d;
		amplitude = 150.0d;
		octaves = 4;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	private double pn(double x, double y, double _amplitude, double freq,
			int octaves, int seed, double persistence) {
		// properties of one octave (changing each loop)
		double t = 0.0f;

		for (int k = 0; k < octaves; k++) {
			t += getValue(x * freq + seed, y * freq + seed) * _amplitude;
			_amplitude *= persistence;
			freq *= 2;
		}

		return t;
	}

	public double getTest(double x, double y) {
		// total = interpolate(total + (0.01f * y), total + (0.01f * y), 0.02);
		double total = total(x, y);
		// total = interpolate(total + (0.01f * y), total + (0.01f * y), 0.02);

		double tot1 = pn(x, y, 128.0d, 0.005d, 10, seed, 0.42d);
		double tot2 = pn(x, y, 128.0d, 0.01d, 10, seed, 0.32d);
		double tot3 = pn(x, y, 64.0d, 0.00006d, 10, seed, 0.11d);
		
		double noise = noise((int)x, (int)y);
		double noise2 = noise((int)x, (int)y);
		
		total = linearInterpolate(tot1, tot2, 0.2d);
		total = linearInterpolate(total, noise, 0.0d);
		total = linearInterpolate(total, noise2, 0.0d);
		total = linearInterpolate(total, tot3, 0.1d);
		
		// System.err.println("total: " + total);
		return total;
	}

	public double getWaterishMountains(double x, double y) {
		double total = total(x, y);
		// total = interpolate(total + (0.01f * y), total + (0.01f * y), 0.02);
		total = linearInterpolate(interpolate(total, total, 0.02f),
				interpolate(total, total, 0.02f), .2d);

		total = linearInterpolate(noise((int) x, (int) y), total, 0.02f);

		// System.err.println("total: " + total);
		return amplitude * total;
	}

	public double getHeight(double x, double y) {
		return amplitude * total(x, y) + amplitude;
	}

	private double total(double i, double j) {
		// properties of one octave (changing each loop)
		double t = 0.0f;
		double _amplitude = 1;
		double freq = frequency;

		for (int k = 0; k < octaves; k++) {
			t += getValue(j * freq + seed, i * freq + seed) * _amplitude;
			_amplitude *= persistence;
			freq *= 2;
		}

		return t;
	}

	private double noise(int x, int y) {
		int n = x + y * 57;
		n = (n << 13) ^ n;
		int t = (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff;
		return 1.0 - (double) t * 0.931322574615478515625e-9;
	}

	private double linearInterpolate(double p1, double p2, double weight) {
		return p1 + (p2 - p1) * weight;
	}

	private double interpolate(double x, double y, double a) {
		double negA = 1.0 - a;
		double negASqr = negA * negA;
		double fac1 = 3.0 * (negASqr) - 2.0 * (negASqr * negA);
		double aSqr = a * a;
		double fac2 = 3.0 * aSqr - 2.0 * (aSqr * a);

		return x * fac1 + y * fac2; // add the weighted factors
	}

	private double getValue(double x, double y) {
		int Xint = (int) x;
		int Yint = (int) y;
		double Xfrac = x - Xint;
		double Yfrac = y - Yint;

		// noise values
		double n01 = noise(Xint - 1, Yint - 1);
		double n02 = noise(Xint + 1, Yint - 1);
		double n03 = noise(Xint - 1, Yint + 1);
		double n04 = noise(Xint + 1, Yint + 1);
		double n05 = noise(Xint - 1, Yint);
		double n06 = noise(Xint + 1, Yint);
		double n07 = noise(Xint, Yint - 1);
		double n08 = noise(Xint, Yint + 1);
		double n09 = noise(Xint, Yint);

		double n12 = noise(Xint + 2, Yint - 1);
		double n14 = noise(Xint + 2, Yint + 1);
		double n16 = noise(Xint + 2, Yint);

		double n23 = noise(Xint - 1, Yint + 2);
		double n24 = noise(Xint + 1, Yint + 2);
		double n28 = noise(Xint, Yint + 2);

		double n34 = noise(Xint + 2, Yint + 2);

		// find the noise values of the four corners
		double x0y0 = 0.0625 * (n01 + n02 + n03 + n04) + 0.125
				* (n05 + n06 + n07 + n08) + 0.25 * (n09);
		double x1y0 = 0.0625 * (n07 + n12 + n08 + n14) + 0.125
				* (n09 + n16 + n02 + n04) + 0.25 * (n06);
		double x0y1 = 0.0625 * (n05 + n06 + n23 + n24) + 0.125
				* (n03 + n04 + n09 + n28) + 0.25 * (n08);
		double x1y1 = 0.0625 * (n09 + n16 + n28 + n34) + 0.125
				* (n08 + n14 + n06 + n24) + 0.25 * (n04);

		// interpolate between those values according to the x and y fractions
		double v1 = interpolate(x0y0, x1y0, Xfrac); // interpolate in x
													// direction (y)
		double v2 = interpolate(x0y1, x1y1, Xfrac); // interpolate in x
													// direction (y+1)
		double fin = interpolate(v1, v2, Yfrac); // interpolate in y direction

		return fin;
	}

	public double clamp(double i, double low, double high) {
		return java.lang.Math.max(java.lang.Math.min(i, high), low);
	}
}