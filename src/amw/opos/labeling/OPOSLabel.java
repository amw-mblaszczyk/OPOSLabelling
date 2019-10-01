package amw.opos.labeling;

import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OPOSLabel implements Comparable<OPOSLabel>{
	
	private String name;
	private int id;
	
	private Collator collator = Collator.getInstance(Locale.getDefault());
	
	
	public OPOSLabel(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return name + ":" + id;
	}
	
	public String csvStr(String separator) {
		return name+separator+id;
	}
	
	public static List<OPOSLabel> parseCSV(String filename, String separator) throws ParseException{
		List<OPOSLabel> list = new ArrayList<OPOSLabel>();
		List<List<String>> slist = CSVParser.parseCSV(filename, separator);
		for (Iterator<List<String>> iterator = slist.iterator(); iterator.hasNext();) {
			List<String> list1 = (List<String>) iterator.next();
			OPOSLabel label = new OPOSLabel(list1.get(0), Integer.parseInt(list1.get(1)));
			list.add(label);
		}
		return list;
	}
	
	public static Map<Integer,OPOSLabel> parseCVS(String filename, String separator) throws ParseException{
		Map<Integer,OPOSLabel> map = new HashMap<Integer,OPOSLabel>();
		List<List<String>> slist = CSVParser.parseCSV(filename, separator);
		for (Iterator<List<String>> iterator = slist.iterator(); iterator.hasNext();) {
			List<String> list1 = (List<String>) iterator.next();
			OPOSLabel label = new OPOSLabel(list1.get(0), Integer.parseInt(list1.get(1)));
			map.put(label.id, label);
		}
		return map;
	}
	
	public static Map<Integer,OPOSLabel> list2map(List<OPOSLabel> list){
		Map<Integer,OPOSLabel> map = new HashMap<Integer,OPOSLabel>();
		for (Iterator<OPOSLabel> iterator = list.iterator(); iterator.hasNext();) {
			OPOSLabel label = iterator.next();
			map.put(label.id, label);
		}
		return map;
	}
	
	public static String printToStrig(List<OPOSLabel> list, String separator) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<OPOSLabel> iterator = list.iterator(); iterator.hasNext();) {
			OPOSLabel oposLabel = (OPOSLabel) iterator.next();
			sb.append(oposLabel.getName()).append(separator).append(oposLabel.getId()).append('\n');
		}
		return sb.toString();
	}
	
	public static void sortByNames(List<OPOSLabel> list) {
		Collections.sort(list);
	}
	
	public static void renumarate(List<OPOSLabel> list) {
		int l = 1;
		for (Iterator<OPOSLabel> iterator = list.iterator(); iterator.hasNext();) {
			OPOSLabel oposLabel = (OPOSLabel) iterator.next();
			oposLabel.setId(l++);
		}
	}
	
	@Override
	public int compareTo(OPOSLabel o) {
		return collator.compare(getName(), o.getName());
	}

	public static void main(String[] args) {
		try {
			//Locale.setDefault(new Locale("pl","PL"));
			String filename=System.getProperty("user.dir")+"\\"+"test";
			List<OPOSLabel> lista = OPOSLabel.parseCSV(filename+".csv", ";");
			for (Iterator<OPOSLabel> iterator = lista.iterator(); iterator.hasNext();) {
				OPOSLabel oposLabel = (OPOSLabel) iterator.next();
				System.out.println(oposLabel);
			}
			lista.add(new OPOSLabel("test1", lista.size()+1));
			sortByNames(lista);
			renumarate(lista);
			String s = printToStrig(lista, ";");
			System.out.println(s);
			CSVParser.writeCSV(filename+"1.csv", s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
