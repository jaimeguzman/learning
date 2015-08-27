package by.dev.madhead.lzwj.util;

public class Euclid {
	public static long GCD(long a, long b) {
		long tmp;

		while (b != 0) {
			tmp = b;
			b = a % b;
			a = tmp;
		}
		return Math.abs(a);
	}

	public static long LCM(long a, long b) {
		return Math.abs((a * b) / GCD(a, b));
	}
}
