package by.dev.madhead.lzwj;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import by.dev.madhead.lzwj.compress.LZW;

public class Main {
	public static void main(String[] args) throws IOException {
		try {
			if (args.length < 3) {
				System.out.println("You must specify operation and files.");
				System.exit(0);
			}
			if ("c".equals(args[0])) {
				FileInputStream in = new FileInputStream(args[1]);
				FileOutputStream out = new FileOutputStream(args[2]);
				LZW lzw = new LZW();

				lzw.compress(in, out);
			} else if ("d".equals(args[0])) {
				FileInputStream in = new FileInputStream(args[1]);
				FileOutputStream out = new FileOutputStream(args[2]);
				LZW lzw = new LZW();

				lzw.decompress(in, out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
