import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class InAndOut {
	public String gtf;
	public String outPath;
	private Gene currentGene; // assuming inFile is sorted by Genes
	private ArrayList<Gene> allGenes;
	private BufferedWriter out;
	
// read input parameters	
	public void getParameters(String[] args) {
		this.gtf="";
		this.outPath="";
	
		//System.out.println(args[0]);
		for (int i =0 ; i<args.length; i++){
			switch(args[i]){
			case "-gtf":
			File inFile = new File(args[i+1]);
			if (inFile.exists()){
			this.gtf=args[i+1];
			}
			else {
				System.out.println("gtf file does not exist!\n");
				printHelpText();
				System.exit(0);
			}
			break;
			case "-o":
			File path = new File(args[i+1]);
			if (!path.getParent().isEmpty()){
			//if (path.isDirectory()){
				this.outPath=args[i+1];
			//System.out.println(outPath);
			}
			else{
				System.out.println("output directory does not exist!\n");
				printHelpText();
				System.exit(0);
			}
			}
	}
		if (this.gtf.equals("") || this.outPath.equals("")){
			System.out.println("Parameter missing!\n");
			printHelpText();
		}
}

// print help text if input parameters are missing/wrong
	private void printHelpText() {
		Scanner scanner = new Scanner(getClass().getResourceAsStream("helptext.txt"));
		while(scanner.hasNextLine()){
	        System.out.println(scanner.nextLine());
		}
	System.exit(0);
		
	}

	//read in gtf file
	public ArrayList<Gene> readGtf() throws NumberFormatException, IOException {
		try{
		this.currentGene=null;
		BufferedReader br = new BufferedReader (new FileReader (this.gtf) ) ;
		String line;
		allGenes= new ArrayList<Gene>();
		while((line = br.readLine( ))  != null)  {
			//if line starts with # its a comment
				if(!line.startsWith("#")){
					String[] columns = new String[9];
					columns = line.split("\t");
					//columns[0]=chr, [1]=source, [2]=feature (gene/transcript/exon ...)
					//[3]=start, [4]=end, [5]=score, [6]=strand, [7]=frame (0/1/2), [8]attribute (;-separated incl. gene_id/gene_name
					
					if(checkProteinCoding(columns[1])){
					readPC(columns);
					}

		 	}
		}

		br.close(); 

		
		return allGenes;
		}
		catch (IOException e) {
			System.out.println("Error reading gtf file. /n" + e);
			return null;
		}
	}

private boolean checkProteinCoding(String string) {
		return (string.equals("protein_coding") || string.equals("nonsense_mediated_decay") ||
				string.equalsIgnoreCase("ensembl_havana") || string.equalsIgnoreCase("havana") || string.equalsIgnoreCase("ensembl") ||
				string.equals("IG_C_gene") || string.equals("IG_D_gene") || string.equals("IG_J_gene") ||
				string.equals("IG_LV_gene") || string.equals("IG_M_gene") || string.equals("IG_V_gene") ||
				string.equals("IG_Z_gene") 
				|| string.equals("nontranslating_CDS") || string.equals("non_stop_decay") || 
				string.equals("polymorphic_pseudogene") || string.equals("TR_C_gene") || string.equals("TR_D_gene") ||
				string.equals("TR_gene") || string.equals("TR_J_gene") || string.equals("TR_V_gene") 
				//|| string.equals("processed_transcript") 
				);
		// http://www.ensembl.org/Help/Glossary?id=275		
	}

private void readPC(String[] columns) {
		//get gene id
		String id= getAttribute("gene_id", columns[8]);
		//get gene name
		String name= getAttribute("gene_name", columns[8]);
		//get gene biotype
		String type= getAttribute("gene_biotype", columns[8]);

			if(columns[2].equals("gene")){	
			//String id, int start, int end, String strand, String type, String name	
			currentGene = new Gene(id, columns[0], Integer.parseInt(columns[3]), Integer.parseInt(columns[4]), columns[6], type, name);
			allGenes.add(currentGene);
			
			}
			else{
				
				// Was wenn Gen=null 
				if (allGenes.isEmpty()){
					currentGene = new Gene(id, columns[0], 0, 0, columns[6], type, name);
					allGenes.add(currentGene);
				}
				if (!currentGene.getId().equals(id)){
					currentGene = new Gene(id, columns[0], 0, 0, columns[6], type, name);
					allGenes.add(currentGene);
				}
				
				addFeature(columns);
			}
//		}


}

// add cds/transcript to current gene
	private void addFeature(String[] columns) {
		Region tempReg = new Region(Integer.parseInt(columns[3]), Integer.parseInt(columns[4]));
		String transcriptID= getAttribute("transcript_id", columns[8]);
		String proteinID= getAttribute("protein_id", columns[8]);
		
		if(columns[2].equals("CDS")){
			
			if(currentGene.getCds().containsKey(proteinID)){
				RegionVector currRV=(RegionVector) currentGene.getCds().get(proteinID);
				currRV.addRegion(tempReg);
			}
			else {
				RegionVector currRV=new RegionVector(proteinID);
				currRV.addRegion(tempReg);
				currentGene.getCds().put(proteinID, currRV);
			}
	
		}
		if(columns[2].equals("transcript")){
			//System.out.println("Name aktuelles Gen: " + currentGene.getName());
			currentGene.getTranscripts().put(transcriptID, tempReg);
		}
		// implement more features when needed
		// e.g. exon, start_codon, stop_codon, UTR
	
}

	private String getAttribute(String attributeName, String text) {
		String attribute= text.substring(text.indexOf(attributeName+" \"") + attributeName.length()+2);
		String[] attributes = attribute.split("\"");
		attribute= attributes[0];	
		return attribute;
	}

	
	
	public void openOutFile() {
	try  
		{
		//FileWriter fstream = new FileWriter(outPath+"\\output.exonskippings", true); 
		FileWriter fstream = new FileWriter(outPath, true); //true tells to append data.
		    out = new BufferedWriter(fstream);
		}
		catch (IOException e)
		{
		    System.err.println("Error in openOutFile: " + e.getMessage());
		}
	}
	
	public void closeOutFile(){
	    if(out != null) {
	        try {
				out.close();
			} catch (IOException e) {
				System.out.println("Error in closeOutFile: " + e);
			}
	    }
	}

	public void writeLine(String line) {
		try {
			out.write(line+"\n");
		} catch (IOException e) {
			System.out.println("Error in writeLine: " + e);
		}
		
	}
}