package amw.opos.labeling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class CSVParser {
	private String separator = ";";
	
	public static List<OPOSLabel> parseCSVlabels(String filename, String separator){
		ArrayList<OPOSLabel> list = new ArrayList<OPOSLabel>();
		try {
			BufferedReader br = new BufferedReader( new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, separator);
				OPOSLabel olabel = new OPOSLabel(st.nextToken(), Integer.parseInt(st.nextToken()));
				list.add(olabel);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list.clear();
			return null;
		}
		
		return list;
	}

	public static List<List<String>> parseCSV(String filename, String separator) throws ParseException {
		ArrayList<List<String>> list = new ArrayList<List<String>>();
		int line_no = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, separator);
				List<String> elems = new ArrayList<String>();
				while (st.hasMoreTokens())
					elems.add(st.nextToken());
				list.add(elems);
				line_no++;
			}
			br.close();
		} catch (Exception e) {
			list.clear();
			throw new ParseException("Parse CSV file ("+filename+") line:"+line_no+". Original message:"+e.getMessage(), line_no);
		}

		return list;
	}
	
	public static void writeCSV(String filename, String csv) throws IOException {
		FileWriter writer = new FileWriter(filename);
		writer.write(csv);
		writer.close();
	}
	
	public static void main(String[] args) {
		try {
			String filename=System.getProperty("user.dir")+"\\"+"test.csv";
			List<List<String>> lista = CSVParser.parseCSV(filename, ";");
			for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
				List<String> list = (List<String>) iterator.next();
				for (Iterator iterator2 = list.iterator(); iterator2.hasNext();) {
					String string = (String) iterator2.next();
					System.out.print('['+string+']');
				}
				System.out.print('\n');
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
