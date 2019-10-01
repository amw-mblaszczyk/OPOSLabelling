package amw.opos.labeling;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LabelRegion {

	private int x0,y0,x1,y1;
	private OPOSLabel olabel;
	
	public LabelRegion(int x0, int y0, int x1, int y1, OPOSLabel olabel) {
		super();
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.olabel = olabel;
	}

	public LabelRegion(int x0, int y0, int x1, int y1, int label_id) {
		super();
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.olabel = new OPOSLabel(null, label_id);
	}

	public OPOSLabel getOlabel() {
		return olabel;
	}

	public void setOlabel(OPOSLabel olabel) {
		this.olabel = olabel;
	}

	public int getX0() {
		return x0;
	}

	public int getY0() {
		return y0;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	@Override
	public String toString() {
		return "Region (" + x0 + "," + y0 + ")(" + x1 + "," + y1 + ") - " + olabel;
	}
	
	public String toCsv(String separator) {
		return new String(x0+separator+y0+separator+x1+separator+y1+separator+olabel.getId());
	}
	
	public static List<LabelRegion> parseCSV(String filename, String separator, Map<Integer, OPOSLabel> labels) throws ParseException{
		List<LabelRegion> list = new ArrayList<LabelRegion>();
		List<List<String>> slist = CSVParser.parseCSV(filename, separator);
		for (Iterator<List<String>> iterator = slist.iterator(); iterator.hasNext();) {
			List<String> list1 = (List<String>) iterator.next();
			int x0 = Integer.parseInt(list1.get(0));
			int y0 = Integer.parseInt(list1.get(1));
			int x1 = Integer.parseInt(list1.get(2));
			int y1 = Integer.parseInt(list1.get(3));
			int label_id = Integer.parseInt(list1.get(4));
			LabelRegion label;
			if ((labels!=null) &&(labels.get(label_id) != null))
				label = new LabelRegion(x0, y0, x1, y1, labels.get(label_id));
			else
				label = new LabelRegion(x0, y0, x1, y1, label_id);
			list.add(label);
		}
		return list;
	}
	
	public static String printToStrig(List<LabelRegion> list, String separator) {
		StringBuffer sb = new StringBuffer();
		for (Iterator<LabelRegion> iterator = list.iterator(); iterator.hasNext();) {
			LabelRegion region = (LabelRegion) iterator.next();
			sb.append(region.getX0()).append(separator).append(region.getY0()).append(separator)
			.append(region.getX1()).append(separator).append(region.getY1()).append(separator)
			.append(region.getOlabel().getId()).append(separator).append('\n');
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		try {
			String filename=System.getProperty("user.dir")+"\\"+"regions";
			List<LabelRegion> lista = parseCSV(filename+".csv", ";",null);
			for (Iterator<LabelRegion> iterator = lista.iterator(); iterator.hasNext();) {
				LabelRegion region = (LabelRegion) iterator.next();
				System.out.println(region);
			}
			lista.add(new LabelRegion(0,0,0,0,1));
			String s = printToStrig(lista, ";");
			System.out.println(s);
			CSVParser.writeCSV(filename+"r1.csv", s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
