package LZ78;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class LZ78Main {

	public static ArrayList<tag> compress(String data, binNum poi) {
		ArrayList<tag> list = new ArrayList<tag>();
		 data += '\1';
		String temp = "";
		char ch;
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("", 0);
		for (int i = 0, j = 0; i < data.length(); i++) {

			ch = data.charAt(i);

			if (temp == "" && ch == '\1')
				break;

			if (!map.containsKey(temp + ch)) {

				tag ta = new tag();
				ta.cha = ch;
				ta.pointer = map.get(temp);

				poi.maxNum(ta.pointer);

				list.add(ta);

				map.put(temp + ch, ++j);

				temp = "";
				ch = '\1';
				continue;
			}  
			
			temp += ch;
		}
		return list;
	}

	public static String deCompress(ArrayList<tag> l) {
		String data = "" , temp ;
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, "");
		for (int i = 0 , j = 0; i < l.size(); i++ ) {
			temp = map.get(l.get(i).pointer) + l.get(i).cha ;
			map.put( ++j , temp) ;
			data += temp;
		}
		return data;
	}

	public static void main(String[] args) {
		// ** // GUI
		 new LZ78APP();
		// ** // number Handling
		// binNum x = new binNum();

		// ** // write to file
		// FileOutputStream outPut = new FileOutputStream("Copress.txt");
		// DataOutputStream out = new DataOutputStream(outPut);
		// out.close();

		// ** // read from file
		// File inpt = new File("Copress.txt");
		// InputStream in = new FileInputStream(inpt);
		// binNum le = new binNum();
		// le.SetMaxBinLen(in.read());
		// binNum poi = new binNum();
		// poi.SetMaxBinLen(in.read());
		// in .close();

		// ** // comprassing
//		binNum poi = new binNum(), le = new binNum();
//		String data = "aablkjfslf;lawkvjnsdkhnsiovjcoisdjviwhiuwhjoiajpdoshbokooooonfbhddlifabab";
//		ArrayList<tag> l = compress(data, poi);
//		// ArrayList<tag> l = compress("abaa");
//		for (int i = 0; i < l.size(); i++) {
//			System.out.println(l.get(i).toString());
//		}

		// ** // decomprassing
//		 String deCompresdData = deCompress(l);
//		 System.out.println(deCompresdData);
//		 System.out.print(data);

		// ** // testing
		// int x= 608571914;
		// while(x > 0 ){
		// System.out.print(x % 2);
		// x /= 2;
		// }
		// System.out.print(Integer.toBinaryString(5));

		// String data = "";
		// //System.out.println(1 %2 + " "+0%2);
		// Scanner reader = new Scanner(new File("mld.txt"));
		// while (reader.hasNext()) {
		// data += (reader.nextLine() + "\n");
		// }
		// //System.out.println(data);
		//
		// binNum le = new binNum(), poi = new binNum();
		// FileOutputStream outPut = new FileOutputStream("1.Lz77");
		// DataOutputStream out = new DataOutputStream(outPut);
		//
		// ArrayList<tag> l = compress(data, poi, le);
		//
		// le.genrate();
		// poi.genrate();
		//
		// for (int i = 0; i < l.size(); i++) {
		// l.get(i).prepareTag(poi, le);
		// //System.out.println(l.get(i).toString());
		// }
		//
		// // System.out.println(poi.binLen + "*" + le.binLen);
		// tag.saveTag(out, poi, le);
		// out.close();

		// binNum le = new binNum(), poi = new binNum();
		// String deCompreseddata = "";
		// InputStream i1n = new FileInputStream(new File("1.Lz77"));
		// DataInputStream in = new DataInputStream(i1n);
		//
		// tag.readTag(in, poi, le);
		// ArrayList<tag> kk = tag.deGenrate(poi, le) ;
		// in.close();
		//
		// //tag.saveTag(out, poi, le);
		//
		// deCompreseddata = deCompress(kk);
		// System.out.println(data);
		// System.out.print(deCompreseddata);

		// for (int i = 0; i < kk.size(); i++) {
		// System.out.println(l.get(i).toString());
		// }
		// Scanner x = new Scanner(new File("1.Lz77"));
		// System.out.println(x.nextBoolean());
		//
		// for (int i1 = 0; i1 < kk.size(); i1++) {
		// System.out.println(kk.get(i1).toString());
		// }

	}

}
/*
 * for (int j = i - temp.length() - 1; j >= 0; j--) { //
 * System.out.println(data.substring(j, j + // temp.length()+ 1) + "_" + temp +
 * (ch == '\1' ? "" : // ch)); if (data.substring(j, j + temp.length() +
 * 1).equals( (temp + (ch == '\1' ? "" : ch)))) { point = j;
 * //System.out.println("Yes" + j); break; } else System.out.println("No" + j);
 * }
 */
