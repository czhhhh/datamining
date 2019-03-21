package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.HMM.MyInter;

public class HMM {
	public static void main(String[] args) throws Exception {
		HMM hmm = new HMM();
		MyInter myInter = hmm.new MyInter(0);
		// reading HMM file
		String[] example = hmm.readtextFile("//Users//chenzehao//Desktop//Problem-HMM//example.fa", myInter);
		ArrayList<Character> result = new ArrayList<>();
		ArrayList<Integer> result1 = new ArrayList<>();
		ArrayList<Integer> result2 = new ArrayList<>();
		ArrayList<Integer> result3 = new ArrayList<>();
		List<Character> trueResult = new ArrayList<>();
		int fragmentNum = 0;
		float proA = 0.0f;
		float proB = 0.0f;
		int position = 0;
		// table generation

		int StateNum = 0;
		int genNum = 0;
		// reading HMM file
		ArrayList<String> readHMMFile = readHMMFile("/Users/chenzehao/Desktop/Problem-HMM/example.hmm",
				new HMM().new MyInter(2));

		StateNum = Integer.parseInt(readHMMFile.get(0));
		genNum = Integer.parseInt(readHMMFile.get(1));

		char[] gens = new char[genNum];
		float[] statesPro = new float[StateNum];
		float stateproforAll[] = new float[(StateNum + genNum) * 2];
		for (int i = 0; i < genNum; i++) {
			String string = readHMMFile.get(2);
			gens[i] = string.charAt(i);
		}
		for (int i = 0; i < StateNum; i++) {
			statesPro[i] = Float.parseFloat(readHMMFile.get(3 + i));
		}
		for (int i = 3 + StateNum; i < readHMMFile.size(); i++) {
			stateproforAll[i - 3 - StateNum] = Float.parseFloat(readHMMFile.get(i));
		}

		for (int i = 1; i < example.length; i++) {

			for (int j = 0; j < example[i].length(); j++) {
				int len;
				len = example[i].length();
				if (i == 1 && j == 0) {

					proA = getTotalProBySoFar(null, 'A', null, example[i].charAt(0));

					proB = getTotalProBySoFar(null, 'B', null, example[i].charAt(0));
					position++;
				} else {
					// 1 label下的概率 2上一个概率 3label change的概率
					float proSoFarOneA = 0.0f;
					float proSoFarOneB = 0.0f;
					float proAtem = proA;
					float proBtem = proB;
					proSoFarOneA = getTotalProBySoFar(proA, 'A', 'A', example[i].charAt(j));
					proSoFarOneB = getTotalProBySoFar(proB, 'A', 'B', example[i].charAt(j));
					if (proSoFarOneA > proSoFarOneB) {
						result1.add(1);
						proA = proSoFarOneA;
					} else {
						result1.add(2);
						proA = proSoFarOneB;
					}

					float proSoFarTwoA = 0.0f;
					float proSoFarTwoB = 0.0f;
					proSoFarTwoA = getTotalProBySoFar(proAtem, 'B', 'A', example[i].charAt(j));
					proSoFarTwoB = getTotalProBySoFar(proBtem, 'B', 'B', example[i].charAt(j));
					if (proSoFarTwoA > proSoFarTwoB) {
						result2.add(1);
						proB = proSoFarTwoA;
					} else {
						result2.add(2);
						proB = proSoFarTwoB;
					}
//					System.out.print("a: "+proA+"    b: "+proB);
//					System.out.println();
					position++;
				}

			}

		}

		int positionNext = 0;
		if (proA > proB) {
			result1.add(1);
			result2.add(1);
			trueResult.add('B');
			positionNext = 1;
		} else {
			positionNext = 2;
			result1.add(2);
			result2.add(2);
			trueResult.add('B');
			trueResult.add('B');
		}
		int oneNum = result1.size();
		result3.addAll(result1);
		result3.addAll(result2);
		int threeNum = result3.size();
		for (int i = oneNum - 1, j = threeNum - 1; i >= 0 && j >= oneNum;) {
			if (positionNext == 1) {
				result.add('A');
				Integer integer11 = result1.get(i--);
				Integer integer12 = result1.get(i--);
				Integer integer21 = result3.get(j--);
				Integer integer22 = result3.get(j--);
				if (integer11 == 1) {
					result.add('A');
					// 上一个的位置记录在integer12
					positionNext = integer12;
				} else {
					result.add('B');
					// 上一个的位置记录在integer22
					positionNext = integer22;
				}
			} else {
				result.add('B');
				Integer integer11 = result1.get(i--);
				Integer integer12 = result1.get(i--);
				Integer integer21 = result3.get(j--);
				Integer integer22 = result3.get(j--);
				if (integer21 == 1) {
					result.add('A');
					// 上一个的位置记录在integer12
					positionNext = integer12;
				} else {
					result.add('B');
					// 上一个的位置记录在integer22
					positionNext = integer22;
				}
			}

		}

		for (int i = result.size() - 1; i >= 0; i--) {
			trueResult.add(result.get(i));
		}
		List<Integer> resutInt = new ArrayList<>();
		// the traceback part;
		char c;
		for (int i = 0; i < trueResult.size(); i++) {
			c = trueResult.get(i);

			if (i == 0) {
				resutInt.add(1);
			} else if (i > 0 && trueResult.get(i - 1) == c) {
				resutInt.set(resutInt.size() - 1, resutInt.get(resutInt.size() - 1) + 1);
			} else {
				resutInt.add(i + 1);
			}

		}
		boolean change;
		if (trueResult.get(0) == 'A') {
			change = true;
		} else {
			change = false;
			fragmentNum++;
		}

		for (int i = 0; i < resutInt.size(); i++) {

			if (i == 0 && change) {
				System.out.print("1 " + resutInt.get(i) + " state A");
				change = false;
			} else if (i == 0 && !change) {
				System.out.print("1 " + resutInt.get(i) + " state B");
				change = true;
			} else if (change) {
				System.out.print((resutInt.get(i - 1) + 1) + "  " + resutInt.get(i) + " state A");
				change = false;
			} else {
				System.out.print((resutInt.get(i - 1) + 1) + "  " + resutInt.get(i) + " state B");
				change = true;
				fragmentNum++;
			}
			System.out.println();
		}
		System.out.println("there are " + fragmentNum + " segments as state B in the file");
	}

	public String[] readtextFile(String file, MyInter myInter) throws Exception {// right
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
		String line;

		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(file)));
		lnr.skip(Long.MAX_VALUE);// 实际上跳过字符的个数

		String[] StringArray = new String[lnr.getLineNumber() + 1];
		Integer lineS = new Integer(0);
		while ((line = bufferedReader.readLine()) != null) {

			String[] split = line.split("\\s+");

			StringArray[lineS++] = split[0];

		}
		myInter.lineNumer = lineS;

		bufferedReader.close();
		lnr.close();
		return StringArray;

	}

	public static float getTotalProBySoFar(Float lastPro, char labelThisOne, Character labelLastOne, char ACTG) {
		if (lastPro == null) {
			return (float) Math.log(0.5f * getProCharAt(labelThisOne, ACTG));
		}
		return lastPro + (float) Math.log(getProCharAt(labelThisOne, ACTG))
				+ (float) Math.log(getProByLabelChange(labelLastOne, labelThisOne));

	}

	public static float getProCharAt(char label, char ACGT) {
		float pro = 0.0f;
		if (label == 'A') {
			switch (ACGT) {
			case 'A':
			case 'a':
				pro = 0.35f;
				break;
			case 'C':
			case 'c':
				pro = 0.15f;
				break;
			case 'G':
			case 'g':
				pro = 0.15f;
				break;
			case 'T':
			case 't':
				pro = 0.35f;
				break;

			default:
				break;
			}
		} else if (label == 'B') {
			switch (ACGT) {
			case 'A':
			case 'a':
				pro = 0.15f;
				break;
			case 'C':
			case 'c':
				pro = 0.35f;
				break;
			case 'G':
			case 'g':
				pro = 0.35f;
				break;
			case 'T':
			case 't':
				pro = 0.15f;
				break;

			default:
				break;
			}
		}
		return pro;
	}

	public static float getProByLabelChange(char fisrtLabel, char secondLabel) {
		if (fisrtLabel == 'A') {
			if (secondLabel == 'A') {
				return 0.999f;
			}
			return 0.001f;
		}
		if (secondLabel == 'A') {
			return 0.01f;
		}
		return 0.99f;

	}

	class MyInter {
		int lineNumer;

		MyInter(int i) {
			lineNumer = i;
		}

		public int getLineNumer() {
			return lineNumer;
		}
	}

	public static ArrayList<String> readHMMFile(String file, MyInter myInter) throws Exception {// right
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
		String line;

		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(file)));
		lnr.skip(Long.MAX_VALUE);// 实际上跳过字符的个数

		ArrayList<String> StringArray = new ArrayList<>();
		Integer lineS = new Integer(0);
		while ((line = bufferedReader.readLine()) != null) {

			String[] split = line.split("\\s+");
			for (int i = 0; i < split.length; i++) {
				StringArray.add(split[i]);
			}

		}
		myInter.lineNumer = lineS;

		bufferedReader.close();
		lnr.close();
		return StringArray;

	}
}
