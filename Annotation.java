import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javafx.util.Pair;

public class Annotation {
	private String[] args;
	private ArrayList<Gene> allGenes;
	private InAndOut reader;
	
	public Annotation (String[] args) throws NumberFormatException, IOException {
	 this.args = args;
	run();
	}

	void run() throws NumberFormatException, IOException {
			reader = new InAndOut();
			reader.getParameters(args);
			this.allGenes = reader.readGtf();
			String line="id\tsymbol\tchr\tstrand\tnprots\tntrans\tSV\tWT\tWT_prots\tSV_prots\tmin_skipped_exon\tmax_skipped_exon\tmin_skipped_bases\tmax_skipped_bases";
			//System.out.println(line);
			reader.openOutFile();
			reader.writeLine(line);
			checkGenes();
			for (int i=0; i<allGenes.size(); i++){
				getExonSkipping(allGenes.get(i));
			}
			reader.closeOutFile();
			}
	
	
	
	private void getExonSkipping(Gene gene) {
		ArrayList<RegionVector> cdsRvList = gene.getCdsList();
		RegionVector inverseAll = new RegionVector();
		for (int i=0; i<cdsRvList.size(); i++){
			RegionVector currCdsRv =cdsRvList.get(i).order(); //sorts region vector by start of regions
			RegionVector inversRv = currCdsRv.inverse();
			inverseAll = inverseAll.merge(inversRv);
		}


		for (int i=0; i<inverseAll.size(); i++){ //iterate through all possible SV-Regions
			String lineWt="";
			String lineSV="";
			String lineWTreg="";
			int maxExons=-1;
			int minExons=(int) Double.POSITIVE_INFINITY;
			int maxBases=-1;
			int minBases=(int) Double.POSITIVE_INFINITY;
			
			int startSV=inverseAll.getRegion(i).getStart();
			int endSV=inverseAll.getRegion(i).getEnd();

			ArrayList <RegionVector> allSkipped = new ArrayList<RegionVector>();
			
			for (int rv=0; rv<cdsRvList.size(); rv++){ // iterate through all RegionVectors
				int end=-1; //to save index matching region
				int start=-1;
				RegionVector skipExList= new RegionVector(); //save all skipped exons
				RegionVector currentRV = cdsRvList.get(rv);
				
				for(int reg=0; reg < currentRV.size(); reg++){ //iterate regions

					if (currentRV.getRegion(reg).getStart()==endSV){ //Exon starts at SV end
						end=reg;
					}
					if (currentRV.getRegion(reg).getEnd()+1==startSV){ //Exon ends at SV start
						start=reg;
					}
					if (currentRV.getRegion(reg).getStart()>startSV && currentRV.getRegion(reg).getEnd()< endSV){ //Exon within SV
						skipExList.addRegion(currentRV.getRegion(reg));
					}
				}
				if (end!=start &&  end!=-1 && start!=-1){
					if (skipExList.size()==0){
						if (lineSV.equals("")){
							lineSV+=currentRV.getId();
						}
						else {
							lineSV+=("|" + currentRV.getId());
						}
					}
					else { // RegionVector contains skipped exons
						if (lineWt.equals("")){
							lineWt+=currentRV.getId();
						}
						else {
							lineWt+="|" + currentRV.getId();
						}
						int skippedExons=skipExList.size();
						int skippedBases=0;
						for (int exon=0; exon<skipExList.size(); exon++){
							skippedBases+=skipExList.getRegion(exon).getEnd()-skipExList.getRegion(exon).getStart()+1;
						}
						
						//nur der Teil im Loop, Rest außerhalb
						skipExList=skipExList.order();
						allSkipped.add(skipExList);
						
						if (skippedExons>maxExons){
							maxExons=skippedExons;
						}
						if (skippedExons<minExons){
							minExons=skippedExons;
						}
						if (skippedBases>maxBases){
							maxBases=skippedBases;
						}
						if (skippedBases<minBases){
							minBases=skippedBases;
						}
					}
					
				}
				
			}
			//WT introns bestimmen über allSkipped
			lineWTreg="";
			Hashtable wtReg =  new Hashtable();
			for (int skip=0; skip<allSkipped.size();skip++){
				ArrayList<Region> tempRV = allSkipped.get(skip).getRv();
				String startKey=startSV +":"+ tempRV.get(0).getStart(); 
				if(!wtReg.containsKey(startKey)){
					wtReg.put(startKey, skip);
					if (lineWTreg.equals("")){
					lineWTreg += startKey;
					}
					else{
					lineWTreg+="|"+startKey;
					}
				}
				for (int reg=0; reg<tempRV.size()-1; reg++){
					String key=(tempRV.get(reg).getEnd()+1)+":"+tempRV.get(reg+1).getStart();
					if (!wtReg.containsKey(key)){
						wtReg.put(key, skip);
						lineWTreg+="|"+key;
					}
				}
				String endKey=(tempRV.get(tempRV.size()-1).getEnd()+1)+":"+endSV; 
				if(!wtReg.containsKey(endKey)){
					wtReg.put(endKey, skip);
					lineWTreg += "|"+endKey;
				}
			}
		

			if (!lineWt.equals("") && !lineSV.equals("")){
			String lineSVreg = inverseAll.getRegion(i).getStart() + ":" + inverseAll.getRegion(i).getEnd();
			String line;	
			line = gene.getId() + "\t" + gene.getName() + "\t" + gene.getChr() + "\t" + gene.getStrand() + "\t" + gene.getCds().size() + "\t" +  gene.getTranscripts().size() + "\t" + 
			lineSVreg + "\t" + lineWTreg + "\t" + lineWt + "\t" + lineSV + "\t" + minExons + "\t" + maxExons + "\t" + minBases + "\t" + maxBases;
			reader.writeLine(line);
			}

		}
		
	}

	private void checkGenes() {
		if (this.allGenes.isEmpty()){
			//System.out.println("No gene defined in gtf file!\nProgram was terminated!");
			reader.closeOutFile();
			System.exit(0);
		}
		
	}

	//get information of a gene
	public Gene getGeneById(String id){
		Iterator<Gene> itr = allGenes.iterator();
		while (itr.hasNext()) {
		    Gene element = itr.next();
		    if (id.equals(element.getId())){
		    	return element;
		    }
		}
		return null;
	}
	
//######Bei Bedarf prüfen###########################
	//get Gene and CDS or transcript RegionVector
	public Pair<Gene, Hashtable> getRegionVectorMap(String id, boolean cds){
		Gene gene = getGeneById(id);
		if (cds){
			return new Pair<Gene, Hashtable>(gene, gene.getCds());
		}
		else return new Pair<Gene, Hashtable>(gene, gene.getTranscripts());
		
	}
	//####################### noch implementieren!!!!!!!!!!!!!######################
	//get all genes within a specified region
	public ArrayList<Gene> getGenes(String chr, int start, int end){
		return null;
		
	}
	//###########################################################################
}
