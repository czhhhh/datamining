

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;

public class MarkovModel {
	public static void main(String[] args) throws Exception {
		MarkovModel markovModel = new MarkovModel();
		//reading inputFile
		String[][] inPutFile = markovModel
				.readInOutFile(args[0]);
		//reading outputFile
		String[][] outPutFile = markovModel
				.readInOutFile(args[1]);
		//store the log ratio
		Float LogFile[][] = new Float[4][4];
		for (int i = 0; i < inPutFile.length; i++) {
			for (int j = 0; j < inPutFile[i].length; j++) {
				float fenzi = Float.parseFloat(inPutFile[i][j]);
				float fenmu = Float.parseFloat(outPutFile[i][j]);
				float result = fenzi / fenmu;
				float LogResult = (float) (Math.log10(fenzi / fenmu) / Math.log10(2));
				LogFile[i][j] = LogResult;
			}
		}
		String[] readTest = markovModel.readTest(args[2]);

		for (int i = 0; i < readTest.length; i++) {
			String string = readTest[i];

			float totalScore = 0;
			if (string.length() > 0) {
				for (int j = 0; j < string.length() - 1; j++) {
					char first = string.charAt(j);
					char second = string.charAt(j + 1);
					int firstPosition = markovModel.getPosition(first);
					int secondPosition = markovModel.getPosition(second);

					Float score = LogFile[firstPosition][secondPosition];
					totalScore += score;
				}
			}
			// the classification results 
			if (totalScore < 0) {
				System.out.println(totalScore + " outside");
			} else if(totalScore>0) {
				System.out.println(totalScore + " inside");
			}

		}
	}

	public String[][] readInOutFile(String file) throws Exception {// right
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
		String line;
		String[][] StringArray = new String[4][4];
		int num = 0;
		while ((line = bufferedReader.readLine()) != null) {
			String[] split = line.split("\\s+");

			for (int k = 0; k < split.length; k++) {
				StringArray[num][k] = split[k];
			}
			num++;
		}

		return StringArray;
	}

	public String[] readTest(String file) throws Exception {// right
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
		String line;

		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(file)));
		lnr.skip(Long.MAX_VALUE);// 实际上跳过字符的个数

		String[] StringArray = new String[lnr.getLineNumber()];
		int lineS = 0;
		while ((line = bufferedReader.readLine()) != null) {

			String[] split = line.split("\\s+");

			StringArray[lineS++] = split[0];

		}

		bufferedReader.close();
		lnr.close();
		return StringArray;

	}

	public int getPosition(char x) {
		switch (x) {
		case 'A':
			return 0;

		case 'C':
			return 1;

		case 'G':
			return 2;

		case 'T':
			return 3;
		default:
			return 0;
		}

	}
}
