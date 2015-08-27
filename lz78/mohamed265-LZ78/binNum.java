package LZ78;

import java.util.ArrayList;

public class binNum {
	public int maxNum, binLen;

	binNum() {
		maxNum = 0;
	}

	public void SetMaxBinLen(int len) {
		binLen = len;
	}

	public void maxNum(int len) {
		maxNum = Math.max(len, maxNum);
	}

	public void genrate() {
		binLen = (int) (Math.log(maxNum) / Math.log(2)) + 1;
	}

	public ArrayList<Boolean> toBinary(int num) {
		boolean[] arr = new boolean[binLen];
		for (int i = 0; num > 0; i++, num /= 2)
			arr[i] = (num % 2 == 1 ? true : false);

		ArrayList<Boolean> lis = new ArrayList<Boolean>(binLen);
		for (int i = 0; i < arr.length; i++) {
			lis.add(arr[i]);
		}
		return lis;

	}

	public int toDecimal(ArrayList<Boolean> arr) {
		// System.out.println("*"+arr.size() + " " + binLen);
		int num = 0;
		for (int i = 0; i < binLen; i++)
			num += Math.pow(2, i) * (arr.get(i) ? 1 : 0);
		return num;
	}

	public static ArrayList<Boolean> fromChar(char ch) {

		ArrayList<Boolean> lis = new ArrayList<Boolean>(8);
		for (int i = 0; i < 8; i++ , ch /= 2) {
			lis.add((ch % 2 == 1 ? true : false));
		}
		return lis;

	}

	public static char toChar(ArrayList<Boolean> arr) {
		int num = 0;
		for (int i = 0; i < arr.size(); i++)
			num += Math.pow(2, i) * (arr.get(i) ? 1 : 0);

		return (char) num;
	}

}
