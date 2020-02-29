import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.nitrr.OSEAFactory;
import com.nitrr.detection.QRSDetector2;



public class TestQRS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File file = new File("/Users/introtuce/eclipse-workspace/QRS Detection/src/text_data.csv");
		
		if (!file.exists()) {
			System.out.println("File Not found");
			return;
		}
		System.out.println("File exists");
		try {
			FileInputStream fis = new FileInputStream(file);
			
			Scanner scanner = new Scanner(fis);
			scanner.nextLine();// Scipping the column name
			
			List<Integer> list = new ArrayList<Integer>();
			while(scanner.hasNext()) {
				list.add(scanner.nextInt());
			}
			
			scanner.close();
			
			int[] ecg = new int[list.size()];
			for(int i=0;i<list.size();i++) {
				ecg[i] = list.get(i);
			}
			System.out.println(ecg.length);
			QRSDetection(ecg,100);
			
			
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	
	public static void QRSDetection(int[] ecgSamples, int sampleRate) {
		QRSDetector2 qrsDetector = OSEAFactory.createQRSDetector2(sampleRate);
		System.out.println(ecgSamples[0]);
		for (int i = 0; i < ecgSamples.length; i++) {
			  int result = qrsDetector.QRSDet(ecgSamples[i]);
			  if (result != 0) {
			    System.out.println("A QRS-Complex was detected at sample: " + (i-result));
			  }
			}
	}
	

}
