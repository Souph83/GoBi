import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Gene {
private String id;
private String chr;
private int start;
private int end;
private String strand;
private String type;
private String name;
private Hashtable transcripts;
private Hashtable cds;


public Gene (String id, String chr, int start, int end, String strand, String type, String name, Hashtable transcripts, Hashtable cds){
	this.id=id;
	this.chr=chr;
	this.start=start;
	this.end=end;
	this.strand=strand;
	this.type=type;
	this.name=name;
	this.setTranscripts(transcripts);
	this.setCds(cds);
}

public Gene (String id, String chr, String strand, String type, String name){
	this.id=id;
	this.chr=chr;
	this.strand=strand;
	this.type=type;
	this.name=name;
	this.setTranscripts(new Hashtable());
	this.setCds(new Hashtable());
}

public Gene (String id, String chr, int start, int end, String strand, String type, String name){
	this.id=id;
	this.chr=chr;
	this.start=start;
	this.end=end;
	this.strand=strand;
	this.type=type;
	this.name=name;
	this.setTranscripts(new Hashtable());
	this.setCds(new Hashtable());
}

public String getId() {
	return id;
}

public String getName() {
	return name;
}

public String getChr() {
	return chr;
}

public int getStart() {
	return start;
}

public int getEnd() {
	return end;
}

public String getStrand() {
	return strand;
}

public String getType() {
	return type;
}

public Hashtable getTranscripts() {
	return transcripts;
}

public void setTranscripts(Hashtable transcripts) {
	this.transcripts = transcripts;
}

public Hashtable getCds() {
	return cds;
}

public void setCds(Hashtable cds) {
	this.cds = cds;
}


public ArrayList<RegionVector> getCdsList() {
	ArrayList<RegionVector> cdsArray = new ArrayList<RegionVector>();
	 Iterator it =cds.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	       // System.out.println(pair.getKey() + " = " + pair.getValue());
	        cdsArray.add((RegionVector) pair.getValue());
	    }
	return cdsArray;
}

}
