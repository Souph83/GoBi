import java.util.ArrayList;
import java.util.Hashtable;

public class RegionVector {
private ArrayList<Region> rv;
private String id;


public RegionVector(String id){
	this.id=id;
	this.setRv(new ArrayList<Region>());
}
public RegionVector(){
	this.setRv(new ArrayList<Region>());
}

public ArrayList<Region> getRv() {
	return rv;
}

public void addRegion(Region region) {
	rv.add(region);
}

public void setRv(ArrayList<Region> rv) {
	this.rv = rv;
}

public int size(){
	return rv.size();
}

public Region getRegion(int i) {
	return rv.get(i);
}
public RegionVector order() {
	
	//insertionSort(int[] sortieren) {
	int temp;
	for (int i=1; i<this.rv.size(); i++){
		temp=rv.get(i).getStart();
		Region tempReg=rv.get(i);
		int j = i;
		while (j>0 && rv.get(j-1).getStart()>temp){
			rv.set(j, rv.get(j-1));
			j--;
		}
		rv.set(j, tempReg);
	}
//		int temp;
//		for (int i = 1; i < sortieren.length; i++) {
//			temp = sortieren[i];
//			int j = i;
//			while (j > 0 && sortieren[j - 1] > temp) {
//				sortieren[j] = sortieren[j - 1];
//				j--;
//			}
//			sortieren[j] = temp;
//		}
//		return sortieren;
	
	return this;
}
public RegionVector inverse() {
 RegionVector inverse = new RegionVector();
 for (int i=0; i<rv.size()-1; i++){
	 inverse.addRegion(new Region(rv.get(i).getEnd()+1, rv.get(i+1).getStart()));
 }
	return inverse;
}
public RegionVector merge(RegionVector newRv) {
	RegionVector merged = new RegionVector();
	Hashtable<String, Integer> mergedTab = new Hashtable<String, Integer>();
	for (int i = 0; i<rv.size();i++){
		if (!mergedTab.containsKey(rv.get(i).getStart()+":"+rv.get(i).getEnd())){
		mergedTab.put(rv.get(i).getStart()+":"+rv.get(i).getEnd(), i);
		merged.addRegion(rv.get(i));
		}
	}
	for (int i = 0; i<newRv.size();i++){
		if (!mergedTab.containsKey(newRv.getRegion(i).getStart()+":"+newRv.getRegion(i).getEnd())){
		mergedTab.put(newRv.getRegion(i).getStart()+":"+newRv.getRegion(i).getEnd(), i);
		merged.addRegion(newRv.getRegion(i));
		}
	}
	return merged;
}
public String getId() {
	return id;
}

}
