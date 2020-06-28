/*************************************************************************************
@Author Michael T.-L. Lee @Tainan, Taiwan
@email: michaelee0407@gmail.com
@version 2018-03-25
 *************************************************************************************/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;
 

public class PURPOSE_meta2pubtator {


	private ArrayList<Integer> matched_Pair_metaIDList_TOP100 = null;
	private ArrayList<Integer> matched_Pair_PMIDList_TOP100 = null;
	private int count_Big_F = 0;
	private int count_Big_T = 0;
	private int count_Big_P = 0;
	private int count_T_intersect_P = 0;
	
	
	private File total_citation_Count_per_query_topic = null;
	private BufferedWriter out_total_citation_Count_per_query_topic = null;

	

	public  PURPOSE_meta2pubtator(String QueryTerm , String species){
        
		try{
			
			
			species = species.toLowerCase();
			String[] species_array = {"human","mouse","rat","fly","worm","yeast"};
			int num_of_speices = 6;
			
			if(species.toLowerCase().equals("all")){
				num_of_speices = 6;
			}
			else{
				num_of_speices = 1;
				species_array[0] = species;
			}

			for(int sp = 0; sp < num_of_speices; sp++){
			
			
				boolean computePURPOSE = true;
				
				QueryTerm = QueryTerm.toLowerCase();
				String root_directory = "./PURPOSE_metabolite_Results/" + species_array[sp] + "/";
				
				File statistic_directory =  new File("./PURPOSE_metabolite_Results/" + species_array[sp] + "/statistic/");
				if(!statistic_directory.exists()){
					statistic_directory.mkdirs();
				}
				
				total_citation_Count_per_query_topic = new File("./PURPOSE_metabolite_Results/" + species_array[sp] + "/statistic/"+"Total_Citation_Count_"+QueryTerm);
				out_total_citation_Count_per_query_topic = new BufferedWriter(
						new FileWriter(total_citation_Count_per_query_topic));
				


				String fileDirAndName_total_unique_PMIDs_CitationYear = "";
				String fileDirAndName_meta2pubtator_meta2Count = "";
				String fileDirAndName_meta2pubtator_PMID_Sort = "";
				String fileDirAndName_meta2pubtator_with_metaID = "";
					
				String entrez_esearch_db = "pubmed";
		    	String entrez_esearch_retmax = "5000000";
		    	int  entrez_esearch_retstart = 0;
				
		    	URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?"
						+"db="+entrez_esearch_db
						+"&term="+species_array[sp]
						+"&retmax="+entrez_esearch_retmax
						+"&retstart="+ entrez_esearch_retstart
						);
				
				URLConnection connect = url.openConnection();
				BufferedReader in_F = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
				
				String temp = null;
				boolean done_parse_PMID_Count = false;
				int PMID_count = 0;
				
				while ((temp = in_F.readLine()) != null && !done_parse_PMID_Count) {
					if(!done_parse_PMID_Count){
						if(temp.indexOf("<Count>") != -1){
							PMID_count = Integer.parseInt(temp.substring(temp.indexOf(">")+1, temp.indexOf("</Count>")));
							
							done_parse_PMID_Count = true;
						}
					}
				}
				
				if(in_F!= null){
					in_F.close();
				}
				
				count_Big_F = PMID_count;
				System.out.println("species="+species_array[sp] + "-> count_Big_F=" + count_Big_F);
				out_total_citation_Count_per_query_topic.write("F=" + count_Big_F + "\n");
				
				
				
				
				fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_9606_CitationYear";
				fileDirAndName_meta2pubtator_meta2Count = "./data/myMeta2Pubtator/meta2pubtator_meta2count";
				fileDirAndName_meta2pubtator_PMID_Sort = "./data/myMeta2Pubtator/meta2pubtator_PMID_sort";
				
				//fileDirAndName_meta2pubtator_with_metaID = "./data/myMeta2Pubtator/meta2pubtator_with_metaID";
				fileDirAndName_meta2pubtator_with_metaID = "./data/myMeta2Pubtator/meta2pubtator_with_metaID_HMDB_name";//2018-03-01
				
				
				

				if(species_array[sp].equals("human")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_meta2Pubtator_9606_CitationYear";
				}
				else if(species_array[sp].equals("mouse")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_meta2Pubtator_10090_CitationYear";
				}
				else if(species_array[sp].equals("fly")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_meta2Pubtator_7227_CitationYear";		
				}
				else if(species_array[sp].equals("worm")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_meta2Pubtator_6239_CitationYear";
				}
				else if(species_array[sp].equals("yeast")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_meta2Pubtator_4932_CitationYear";
				}
				else if(species_array[sp].equals("rat")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_meta2Pubtator_10116_CitationYear";
				}
				else{
					computePURPOSE = false;
					System.out.println("Sorry, current platform is not handling selected species");
				}
				
				

				if(computePURPOSE) {
		
					if(QueryTerm.toLowerCase().equals("hpp22")){
				
						String[] hpp22_topics = {
								"brain",
								"cancer", 
								"cardiovascular", 
								"diabetes",
								"hot+OR+cold+OR+alkaline+condition+OR+acidic+condition+OR+hypersaline+OR+radiation",
								"eye+OR+ocular",
								"food+OR+nutrition+OR+nutrients",
								"glycoproteins", 
								"immune+OR+immune+system",
								"infectious+OR+infection",
								"kidney+OR+urine",
								"liver+OR+hepatic",
								"mitochondria",
								"rat+OR+mouse",
								"muscle+OR+bone+OR+musculoskeletal",
								"pathology",
								"pediatric+OR+newborn+OR+infant+OR+toddler+OR+child+OR+adolescent",
								"plasma+OR+serum",
								"protein+aggregation",
								"rheumatic",
								"respiratory",
								"stem+cells",
								"toxicology+OR+toxic+OR+toxin"
						};	


						for(int i = 0; i < hpp22_topics.length; i++){
							
							out_total_citation_Count_per_query_topic.write(hpp22_topics[i] + "\t");
							
							String query_term_and_species = null;
							if(hpp22_topics[i].equals("rat+OR+mouse")) {
								query_term_and_species = hpp22_topics[i];
							}
							else {
								query_term_and_species = hpp22_topics[i]+"+AND+"+species_array[sp];
							}
							
							ComputePURPOSE_from_extractAbstractUsingEntrez_Esearch(
									query_term_and_species, 
				        			root_directory, 
				        			fileDirAndName_total_unique_PMIDs_CitationYear,
				        			fileDirAndName_meta2pubtator_meta2Count,
				        			fileDirAndName_meta2pubtator_PMID_Sort,	
				        			fileDirAndName_meta2pubtator_with_metaID,
				        			count_Big_F);

						}//for(int i = 0; i < hpp22_topics.length; i++){
						
					}//if(QueryTerm.equals("hpp22")){
					else{
						
						String query_term_and_species = null;
						if(QueryTerm.equals("rat+OR+mouse")) {
							query_term_and_species = QueryTerm;
						}
						else {
							query_term_and_species = QueryTerm+"+AND+"+species_array[sp];
						}

			        	ComputePURPOSE_from_extractAbstractUsingEntrez_Esearch(
			        			query_term_and_species,  //
			        			root_directory, 
			        			fileDirAndName_total_unique_PMIDs_CitationYear,
			        			fileDirAndName_meta2pubtator_meta2Count,
			        			fileDirAndName_meta2pubtator_PMID_Sort,	
			        			fileDirAndName_meta2pubtator_with_metaID,
			        			count_Big_F);
			        	
					}//else
				

				if(out_total_citation_Count_per_query_topic!=null){
					out_total_citation_Count_per_query_topic.close();
				}
			
        		
	
			}//for(int sp = 0; sp < num_of_speices; sp++){
			
			
				
			}//if(computePURPOSE) {
			
			
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
	

	public void ComputePURPOSE_from_extractAbstractUsingEntrez_Esearch(
			String QueryTerm, 
			String root_directory, 
			String fileDirAndName_total_unique_PMIDs_CitationYear,
			String fileDirAndName_meta2pubtator_meta2Count,
			String fileDirAndName_meta2pubtator_PMID_Sort,
			String fileDirAndName_metaical2pubtator_with_metaID,
			int count_Big_F
	){
	
		
		long startTime = System.currentTimeMillis();
		
		matched_Pair_metaIDList_TOP100 = new ArrayList<Integer>();
		matched_Pair_PMIDList_TOP100 = new ArrayList<Integer>();
		
		
		File input_directory = new File(root_directory);
		if(!input_directory.exists()){       
			 input_directory.mkdirs();
	    }

		System.out.print("Calculating PURPOSE[meta2pubtator] Score [query="+QueryTerm+ "]");

		String temp = null;
		
		ArrayList<Integer> unique_queried_topic_pmidList = new ArrayList<Integer>();

		ArrayList<Integer> matched_PMIDList = new ArrayList<Integer>();
		ArrayList<Integer> matched_PMID_CitationList = new ArrayList<Integer>();
		ArrayList<Integer> matched_PMID_PubDateList = new ArrayList<Integer>();
		ArrayList<meta_data> meta_result_List = new ArrayList<meta_data>();
	
	
		
		//String query_url 
		//= "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=cancer+AND+human&retmax=1000000&retstart=2486433";
		
		String entrez_esearch_db = "pubmed";
    	String entrez_esearch_retmax = "5000000";
    	int  entrez_esearch_retstart = 0;

		//System.out.println("URL = " + "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?"
		//		+"\ndb="+entrez_esearch_db
		//		+"\n&term="+QueryTerm
		//		+"\n&retmax="+entrez_esearch_retmax
		//		+"\n&retstart="+ entrez_esearch_retstart);

		try{
			
			System.out.print(".");
			
			URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?"
					+"db="+entrez_esearch_db
					+"&term="+QueryTerm
					+"&retmax="+entrez_esearch_retmax
					+"&retstart="+ entrez_esearch_retstart
					);
			
			//System.out.println(url.toString());
			
			URLConnection connect = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
			

			String pmid = null;
			boolean IdList_parsing_begin = false;
			boolean done_parse_PMID_Count = false;
			int IdList_index = -1;
			int PMID_count = 0;
			int current_PMID = 0;
			
			
			while ((temp = in.readLine()) != null) {

				if(!done_parse_PMID_Count){
					if(temp.indexOf("<Count>") != -1){
						PMID_count = Integer.parseInt(temp.substring(temp.indexOf(">")+1, temp.indexOf("</Count>")));
						//System.out.println("PMID_count =" + PMID_count);
						done_parse_PMID_Count = true;
					}
				}
				else if(!IdList_parsing_begin){
					IdList_index =  temp.indexOf("<IdList>");
					if(IdList_index != -1){
						IdList_parsing_begin = true;
					}
				}
				//To save total unique PMID list results into ArrayList
				else if(current_PMID < PMID_count){
					pmid = temp.substring(temp.indexOf(">")+1, temp.indexOf("</Id>"));	
					unique_queried_topic_pmidList.add(Integer.parseInt(pmid));
					current_PMID++;
				}
			}//while
			
	
			if(in!=null){	
				in.close();
			}
				
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	

		
		//=====================================================================================================================
		
		// This section is to compute the intersected PMID list between meta2pubtator PMID list and queried topic returned PMID lists
		// The trick to increase the computation time is take advantage of BIG array which was set to the size of the LARGEST PMID number.
		// The size need to be adjusted if LARGEST PMID is greater than the current setting 28753741(Date: 2017-08-19). 
				
		//=====================================================================================================================
		
		temp= null;
		String[] token_string = null;
		matched_PMIDList = new ArrayList<Integer>();
		matched_PMID_CitationList = new ArrayList<Integer>();
		matched_PMID_PubDateList = new ArrayList<Integer>();
		
		ArrayList<String> meta2pubtator_metaIDList = new ArrayList<String>();
		ArrayList<Integer> meta2pubtator_CountList = new ArrayList<Integer>();
		
		
final int TOTAL_PMID_ARRAY_SIZE = 40000001;
		
		int[] total_unique_PMID_Array = new int[TOTAL_PMID_ARRAY_SIZE]; //need to change accordingly
		int[] total_unique_PMID_Citation_Array = new int[TOTAL_PMID_ARRAY_SIZE]; //need to change accordingly
		int[] total_unique_PMID_PubDate_Array = new int[TOTAL_PMID_ARRAY_SIZE]; //need to change accordingly
		
		
		File total_unique_PMIDs_CitationYear_inputFile 
		= new File(fileDirAndName_total_unique_PMIDs_CitationYear);
		

		if (total_unique_PMIDs_CitationYear_inputFile.exists()){
			try {
				BufferedReader in_total_unique_PMIDs_CitationYear = new BufferedReader(
						new FileReader(total_unique_PMIDs_CitationYear_inputFile));
				
				String[] token = null;
				while ((temp = in_total_unique_PMIDs_CitationYear.readLine()) != null) {
					token = temp.split("\t");
					total_unique_PMID_Array[Integer.parseInt(token[0])] = 1; //save a lot of Execution time this way
					total_unique_PMID_Citation_Array[Integer.parseInt(token[0])] = Integer.parseInt(token[1]); //save a lot of Execution time this way
					total_unique_PMID_PubDate_Array[Integer.parseInt(token[0])] = Integer.parseInt(token[2]); //save a lot of Execution time this way
				}
				
				if(in_total_unique_PMIDs_CitationYear!=null){
					in_total_unique_PMIDs_CitationYear.close();
				}

			
				//System.out.println("--- ("+QueryTerm+") ===> "
				//+"================== start matching process ==============================");

				int current_count_T = 0;
				for(int t = 0; t < unique_queried_topic_pmidList.size(); t++) {	
					if(total_unique_PMID_Array[unique_queried_topic_pmidList.get(t)] == 1) {
						current_count_T++;
					}
				}
				count_Big_T = current_count_T;
				System.out.print(".count_Big_T="+count_Big_T);
				out_total_citation_Count_per_query_topic.write("T=" + count_Big_T + "\t");

				
				int count_pmid_oversize = 0;
				for(int m = 0; m < unique_queried_topic_pmidList.size(); m++){
					
					if(unique_queried_topic_pmidList.get(m) <= TOTAL_PMID_ARRAY_SIZE){
						if(total_unique_PMID_Array[unique_queried_topic_pmidList.get(m)] == 1){
							matched_PMIDList.add( unique_queried_topic_pmidList.get(m));
							matched_PMID_CitationList.add(total_unique_PMID_Citation_Array[unique_queried_topic_pmidList.get(m)]);
							matched_PMID_PubDateList.add(total_unique_PMID_PubDate_Array[unique_queried_topic_pmidList.get(m)]);
							//num_of_matched++;
							
							//if(num_of_matched%1000==0){ //print out the progress of matching process
							//	System.out.println(" ("+QueryTerm+")===> "
							//			+" num_of_matched = "+ num_of_matched);
							//}
						}
					}
					else{
						count_pmid_oversize++;
					}
				}
				
				
				//System.out.println("count_pmid_over array size = " + count_pmid_oversize);
				
				Collections.sort(matched_PMIDList, new Comparator<Integer>() {
			        @Override
			        public int compare(Integer data1, Integer data2)
			        {
			        	if( data1.intValue() < data2.intValue()){
			        		return  -1;
			        	}
			        	if( data1.intValue() > data2.intValue()){
			        		return 1;
			        	}
			        	return 0;
			        }
			    });
				
				//System.out.println("matched_PMIDList.size() = "+ matched_PMIDList.size());
				System.out.print(".");
				
				if(matched_PMIDList.size() == 0) {
					System.out.println("--> No matched PMIDs for calculating PURPOSE Score!!!");
				}
				
			} catch ( IOException e) {
				e.printStackTrace();
			}
						
		}//if (meta2pubtator_inputFile.exists()){
		
		
	
		//***********************************************************************************************************
		// Updated - 2018-01-11 by Michael Lee @Tainan
		// Evaluation of with or without citation count
		//***********************************************************************************************************
		boolean remove_citation_count = false;
		
		ArrayList<metaID_to_PMID_data> matched_Pair_meta_to_PMID_dataList = new ArrayList<metaID_to_PMID_data>();
		ArrayList<Integer> matched_Pair_meta_IDList = new ArrayList<Integer>();
		ArrayList<Integer> matched_Pair_PMIDList = new ArrayList<Integer>();
		
		ArrayList<String> meta2pubtator_matched_Pair_Citation_metaIDList = new ArrayList<String>();
		ArrayList<Integer>meta2pubtator_matched_Pair_Citation_PMIDCountList  = new ArrayList<Integer>();
		ArrayList<Integer> meta2pubtator_matched_Pair_Citation_CitationCountList  = new ArrayList<Integer>();
		
		
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//=================================================================================
		//2018-03-09 updated by Michael Lee -------> TO SKIP the rest of the programs for those query terms without ANY matched PMID
		
			
				
		if(matched_PMIDList.size() > 0) {
		
		
		File meta2pubtator_topic_meta_Count_inputFile 
		= new File(fileDirAndName_meta2pubtator_meta2Count);
		
		if(meta2pubtator_topic_meta_Count_inputFile.exists()){
			
			try{
			
				BufferedReader in_meta2Count = new BufferedReader(
						new FileReader(meta2pubtator_topic_meta_Count_inputFile));
				
				while ((temp = in_meta2Count.readLine()) != null) {
					token_string = temp.split("\t");
					if(token_string.length == 2){	
						meta2pubtator_metaIDList.add(token_string[0]);
						meta2pubtator_CountList.add(Integer.parseInt(token_string[1]));
					}
				}
				
				if(in_meta2Count!=null){
					in_meta2Count.close();
				}
				
				System.out.print(".");
				
			} catch ( IOException e) {
				e.printStackTrace();
			}
		}
		
		
		//=====================================================================================================================
		
		// This section is to compute metabolite ID to PMID pair of T+P, we need this data to further compute citation count for each metabolite ID
		// Use the pre-computed meta2pubtator_tax_id_PMID_Sort which is sorted version of gene2pubtator(downloaded from pubtator)
		// The format of the gene2pubtator_tax_id_PMID_Sort file is a tab-delimited text file while 
		// first term is PMID and second term is metabolite ID, since the PMID is sorted so that matching process does not need to search back
		// save a lot of execution time.
				
		//=====================================================================================================================
				
	
		File meta2pubtator_PMID_Sort_inputFile = 
  				new File(fileDirAndName_meta2pubtator_PMID_Sort);
  		
		//if(!file_gene2pubtator_matched_Pair.exists()){
  		
		if (meta2pubtator_PMID_Sort_inputFile.exists()){
		
			try{
				
				BufferedReader in = new BufferedReader(
						new FileReader(meta2pubtator_PMID_Sort_inputFile));

				int current_index = 0;
				int current_PMID = matched_PMIDList.get(current_index);
				
				while ((temp = in.readLine()) != null) {
					
					token_string = temp.split("\t");
					
					if(token_string.length == 2){
						
						//System.out.println(Integer.parseInt(token_string[0]) + "\t" + current_PMID);
						
						if(current_PMID  < Integer.parseInt(token_string[0])){
							if(current_index < matched_PMIDList.size()-1){
								current_index++;
								current_PMID = matched_PMIDList.get(current_index);
							}
						}
						
						if(Integer.parseInt(token_string[0]) == current_PMID){

							matched_Pair_meta_to_PMID_dataList.add(
									new metaID_to_PMID_data(
									Integer.parseInt(token_string[1]), 
									Integer.parseInt(token_string[0])));
							
							if(current_index < matched_PMIDList.size()-1){
								current_index++;
								current_PMID = matched_PMIDList.get(current_index);
							}
						}	
					}
		
				}//while
				
				if(in!=null) {
					in.close();
				}
				
				
				//System.out.println("matched_Pair_gene_to_PMID_dataList.size() = " + matched_Pair_gene_to_PMID_dataList.size());

				Collections.sort(matched_Pair_meta_to_PMID_dataList, new Comparator<metaID_to_PMID_data>() {
			        @Override
			        public int compare(metaID_to_PMID_data data1, metaID_to_PMID_data data2)
			        {
			        	if( data1.getMetaID() < data2.getMetaID()){
			        		return  -1;
			        	}
			        	if( data1.getMetaID() > data2.getMetaID()){
			        		return 1;
			        	}
			        	return 0;
			        }
			    });

				for(int a = 0 ; a < matched_Pair_meta_to_PMID_dataList.size(); a++){
					matched_Pair_meta_IDList.add(matched_Pair_meta_to_PMID_dataList.get(a).getMetaID());
					matched_Pair_PMIDList.add(matched_Pair_meta_to_PMID_dataList.get(a).getPMID());
				}
				System.out.print(".");

  			} catch ( IOException e) {
  				e.printStackTrace();
  			}
			
  		}//if (gene2pubtator_inputFile.exists()){
			
		
  		
		//=====================================================================================================================
		
		//For each matched GeneID to PubMedID pair ----> calculate the sum of citation/yr of each GeneID(protein)
		
		//=====================================================================================================================
		

		// ArrayList for metaical2pubtator metaical names and HMDB ID
		ArrayList<String> meta2pubtator_meta_ID_dataList = new ArrayList<String>();
		ArrayList<String> meta2pubtator_meta_name_dataList = new ArrayList<String>();
		ArrayList<String> meta2pubtator_HMDB_ID_dataList = new ArrayList<String>();

		int total_citation_count_per_query_topic = 0;
		
		try{
		

			String current_metaID = null;
			int PMID_count = 0;
			int total_citation_count_per_year = 0;
			int pub_year = 0;
			
			current_metaID = matched_Pair_meta_IDList.get(0)+"";
			PMID_count = 1;
			
			pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(0)];
			if(pub_year >= 2018){
				pub_year = 2017;
			}
			total_citation_count_per_year = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(0)]/(2018-pub_year);
			total_citation_count_per_query_topic = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(0)];
		
			
			for(int a = 1; a < matched_Pair_meta_IDList.size(); a++){
				
				if(current_metaID.equals(matched_Pair_meta_IDList.get(a)+"")){
					PMID_count++;
					pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(a)];
					if(pub_year >= 2018){
						pub_year = 2017;
					}
					total_citation_count_per_year = total_citation_count_per_year + total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)]/(2018-pub_year);
					total_citation_count_per_query_topic = total_citation_count_per_query_topic + total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)];
				
					//System.out.println(total_citation_count_per_year);
				}
				else{
					//save current data
					meta2pubtator_matched_Pair_Citation_metaIDList.add(current_metaID);
					meta2pubtator_matched_Pair_Citation_PMIDCountList.add(PMID_count);
					meta2pubtator_matched_Pair_Citation_CitationCountList.add(total_citation_count_per_year);
					current_metaID = matched_Pair_meta_IDList.get(a)+"";
					PMID_count = 1;	
				
					pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(a)];
					if(pub_year >= 2018){
						pub_year = 2017;
					}
					
					total_citation_count_per_year = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)]/(2018-pub_year);
					total_citation_count_per_query_topic = total_citation_count_per_query_topic + total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)];
				}

			}//
			

			System.out.print(".[total citaion]="+total_citation_count_per_query_topic+"..");
			out_total_citation_Count_per_query_topic.write("total_citaion=" + total_citation_count_per_query_topic);
			out_total_citation_Count_per_query_topic.newLine();
			
			
			

			//prepare file ---- metaID to metaical name mentions	 			
	  		File metaical2pubtator_with_metaID_inputFile = new File(fileDirAndName_metaical2pubtator_with_metaID);

	  		temp = null;
	  		token_string = null;
	  		if (metaical2pubtator_with_metaID_inputFile.exists()){
				
				BufferedReader in_c = new BufferedReader(new FileReader(metaical2pubtator_with_metaID_inputFile));

				while ((temp = in_c.readLine()) != null) {
					token_string = temp.split("\t");
					if(token_string.length == 3){	
						meta2pubtator_meta_ID_dataList.add(token_string[0]);
						meta2pubtator_HMDB_ID_dataList.add(token_string[1]);
						meta2pubtator_meta_name_dataList.add(token_string[2]);
					}
				}
				
				if(in_c!=null){
		  			in_c.close();
		  		}

	  		}
	  			
			
		} catch ( IOException e) {
			e.printStackTrace();
		}
		
		
		int index_metaID = -1;
		int index_meta_name = -1;
		temp= null;
		String meta_ID = "-";
		String HMDB_ID = "-";
		String meta_Name = "-";
		
		
		//P is the set of publications linked to a particular
		//protein in all studies
		
		//F is the set of all publications linked to any
		//proteins in any topics, containing all PMIDs in the
		//Gene2PubMed file within a particular taxonomy 
		
		//T+P is the set of publications linked to a
		//particular protein within a queried topic.
		
		//calculate Big T, where T is the set of publications that are linked to any protein
		//within a particular taxonomy and that are retrieved from a
		//queried topic
		
		
		double PartI = 0.00;
		double PartII = 0.00;
		double PartIII = 0.00;
		

		for(int i =0; i < meta2pubtator_matched_Pair_Citation_metaIDList .size(); i++){

			index_meta_name = meta2pubtator_meta_ID_dataList.indexOf(meta2pubtator_matched_Pair_Citation_metaIDList.get(i));
			if(index_meta_name != -1){
				meta_ID = meta2pubtator_meta_ID_dataList.get(index_meta_name);
				HMDB_ID = meta2pubtator_HMDB_ID_dataList.get(index_meta_name);
				meta_Name = meta2pubtator_meta_name_dataList.get(index_meta_name);
			}
			else{
				meta_ID = "-";
				HMDB_ID = "-";
				meta_Name = "-";
			}
			
			
			index_metaID = meta2pubtator_metaIDList.indexOf(meta2pubtator_matched_Pair_Citation_metaIDList .get(i));
			if(index_metaID != -1){
				count_Big_P = meta2pubtator_CountList.get(index_metaID);		
			}
			else{
				//System.out.println(meta2pubtator_matched_Pair_Citation_metaIDList .get(i));
			}
			count_T_intersect_P = meta2pubtator_matched_Pair_Citation_PMIDCountList.get(i);
			
			
			//PartI = 1+log10(TP)
			if(count_T_intersect_P == 1){
				PartI = 1;
			}
			else{
				PartI = 1+Math.log10(count_T_intersect_P);
			}
			
			//PartII = log10((Cit/yr+1)/10)
			PartII = (meta2pubtator_matched_Pair_Citation_CitationCountList.get(i)+1)*0.1;
			if(PartII <= 1.0){
				PartII = 0.0;
			}
			else{
				PartII = Math.log10(PartII);
			}

			//PartIII = (1+log10(F/T)+log10(F/P))
			if(count_Big_F > count_Big_P && count_Big_F > count_Big_T){
				//System.out.println(count_Big_P);
				PartIII = 1 + Math.log10(count_Big_F/count_Big_T)+Math.log10(count_Big_F/count_Big_P);
			}
			else{
				PartIII = 1;
			}
			

			double PURPOSE = (PartI+PartII)*PartIII;	
				
			String PURPOSE_s = new DecimalFormat("#0.000").format(PURPOSE);
			String PartI_s = new DecimalFormat("#0.000").format(PartI);
			String PartII_s = new DecimalFormat("#0.000").format(PartII);
			String PartIII_s = new DecimalFormat("#0.000").format(PartIII);
			

			meta_result_List.add(
					new meta_data( 
							QueryTerm,
							meta_ID,
							HMDB_ID,
							meta_Name,
							Double.parseDouble(PURPOSE_s),
							count_Big_P,
							count_T_intersect_P,
							meta2pubtator_matched_Pair_Citation_CitationCountList.get(i),
							Double.parseDouble(PartI_s),
							Double.parseDouble(PartII_s),
							Double.parseDouble(PartIII_s))
			);
		

		}//for loop
		
		
		System.out.print(".");
		
		Collections.sort(meta_result_List, new Comparator<meta_data>() {
	        @Override
	        public int compare(meta_data PS1, meta_data PS2)
	        {
	        	if( PS1.getPURPOSE_Score() > PS2.getPURPOSE_Score()){
	        		return  -1;
	        	}
	        	if( PS1.getPURPOSE_Score() < PS2.getPURPOSE_Score()){
	        		return 1;
	        	}
	        	return 0;
	        }
	    });
		
		
		}//if(matched_PMIDList.size() > 0) {
		
		
		if(matched_PMIDList.size() > 0) {
		
		
		Vector<Integer> PURPOSE_TOP100_meta_vector = new Vector();
		//Vector<String> PURPOSE_TOP100_total_unique_PMIDs_vector = new Vector();
		Vector<Integer> PURPOSE_TOP100_meta_Sorted_vector = new Vector();
		
		
		try{
		
			//write to File
			String file_Output_DirAndName 
			= root_directory+QueryTerm+"_PURPOSE[metabolite]";
			
			File outputFile = new File(file_Output_DirAndName);
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			
			
			out.write("Big_T" + "\t" + count_Big_T + "\n");
			out.write("Big_F" + "\t" + count_Big_F + "\n");
			
			out.write(
					"HMDB_ID" + "\t" +
					"metabolite_Name" + "\t" +
					"PURPOSE_Score" + "\t" +
					"Big_P" + "\t" +
					"T+P" + "\t" +
					"Citation" + "\t" +
					"1+Log(T+P)" + "\t" +
					"log10((Cit/yr+1)/10)" + "\t" +
					"1+log10(F/T)+log10(F/P)"
					);
			out.newLine();
			
			int count100 = 0;
			int index_metaID = -1;
			for(int j = 0; j < meta_result_List.size(); j++){
	
				//input_meta_ID_vector.add(meta_result_List.get(j).getmeta_ID());
				//input_meta_Name_vector.add(meta_result_List.get(j).getmeta_Name());
				
				//input_meta_PS_vector.add(meta_result_List.get(j).getPURPOSE_Score());
				//input_meta_PS_bigP_vector.add(meta_result_List.get(j).getCount_Big_P());
				//input_meta_PS_intsT_P_vector.add(meta_result_List.get(j).getCount_T_intersect_P() );
				//input_meta_Citation_vector.add(meta_result_List.get(j).getTotal_citation_count());
				//input_meta_LOG_intsT_P_vector.add(meta_result_List.get(j).getPartI_value());
				//input_meta_LOG_T_div_P_vector.add(meta_result_List.get(j).getPartII_value()); 
				//input_meta_LOG_Citation_vector.add(meta_result_List.get(j).getPartIII_value()); 

				
				if(!meta_result_List.get(j).getmeta_ID().equals("-")){
					
					if(count100 < 100){
						count100++;
						PURPOSE_TOP100_meta_vector.add(Integer.parseInt(meta_result_List.get(j).getmeta_ID()));
						PURPOSE_TOP100_meta_Sorted_vector.add(Integer.parseInt(meta_result_List.get(j).getmeta_ID()));
					}
					
					out.write(
							meta_result_List.get(j).getHMDB_ID()
							+ "\t" + meta_result_List.get(j).getmeta_Name()
							+ "\t" + meta_result_List.get(j).getPURPOSE_Score() 
							+ "\t" + meta_result_List.get(j).getCount_Big_P() 
							+ "\t" + meta_result_List.get(j).getCount_T_intersect_P()
							+ "\t" + meta_result_List.get(j).getTotal_citation_count()
							+ "\t" + meta_result_List.get(j).getPartI_value()
							+ "\t" + meta_result_List.get(j).getPartII_value()
							+ "\t" + meta_result_List.get(j).getPartIII_value()
							);
					out.newLine();
				}
			}
			
			if(out != null){
				out.close();
			}
			
			
			
			
			long endTime = System.currentTimeMillis();
			long duration_in_ms = (endTime -  startTime);  
			
			long sec =  duration_in_ms/1000;
			
			String exection_time_string = "Finished!---> Execucution:"
					+ sec + " seconds"; // and " + duration_in_ms%1000 + " milliseconds.";
			System.out.println(exection_time_string);
			

			// Sort TOP100 proteins in PURPOSE score
			
			Collections.sort(PURPOSE_TOP100_meta_Sorted_vector, new Comparator<Integer>() {
		        @Override
		        public int compare(Integer meta_ID1, Integer meta_ID2)
		        {
		        	if( meta_ID1 < meta_ID2){
		        		return  -1;
		        	}
		        	if( meta_ID1 > meta_ID2){
		        		return 1;
		        	}
		        	return 0;
		        }
		    });
			
			
			//write to File
			double total_citation_count_per_year_Filter_Value = 5.0;
			
			//System.out.println("PURPOSE_TOP100_meta_Sorted_vector.size()  = " + PURPOSE_TOP100_meta_Sorted_vector.size()); 
	
			int current_index = 0;
			int curent_metaID = 0;
			if(current_index < PURPOSE_TOP100_meta_Sorted_vector.size()){
				PURPOSE_TOP100_meta_Sorted_vector.get(current_index);
			}
			int pub_year = 0;
			double total_citation_count_per_year = 0;
		
			for(int g = 0; g < matched_Pair_meta_IDList.size(); g++){	
				if(curent_metaID == matched_Pair_meta_IDList.get(g) ){
					// filter Citation/Yr here
					pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(g)];
					if(pub_year >= 2018){
						pub_year = 2017;
					}
					total_citation_count_per_year = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(g)]/(2018-pub_year);
					
					if(total_citation_count_per_year >= total_citation_count_per_year_Filter_Value){
						matched_Pair_metaIDList_TOP100.add(matched_Pair_meta_IDList.get(g));
						matched_Pair_PMIDList_TOP100.add(matched_Pair_PMIDList.get(g));
					}
				}
				else if( (matched_Pair_meta_IDList.get(g) > curent_metaID) && current_index < 99){
					current_index++;//pick the next metaID of TOP100
					if(current_index < PURPOSE_TOP100_meta_Sorted_vector.size()){
						curent_metaID = PURPOSE_TOP100_meta_Sorted_vector.get(current_index);
					}
					//System.out.println("current_index #" + current_index + "\t" + curent_metaID);
				}
				
			}//for
			
			//System.out.println("***** Run Extract TOP100 PMID for view publication *********");

			long startTime_pmid = System.currentTimeMillis();
			
			ExtractTOP100PMIDCitation(root_directory, QueryTerm, fileDirAndName_metaical2pubtator_with_metaID);
			
			long endTime_pmid = System.currentTimeMillis();
			long duration_in_ms_pmid = (endTime_pmid -  startTime_pmid);  
			
			long sec_pmid =  duration_in_ms_pmid/1000;
			
			String exection_time_string_ExtractTOP100PMIDCitation = "Finished!---> Execucution:" 
					+ sec_pmid + " seconds"; //+ duration_in_ms_pmid%1000 + " milliseconds.";
			System.out.println(exection_time_string_ExtractTOP100PMIDCitation);
			
			//free memory
			total_unique_PMID_Array = new int[0];//free memory of Array
			total_unique_PMID_Citation_Array = new int[0];//free memory of Array
			total_unique_PMID_PubDate_Array = new int[0];//free memory of Array
			
			

		} catch ( IOException e) {
			e.printStackTrace();
		}	
		
		
		}//if(matched_PMIDList.size() > 0) {
		

  }
	
	
	public void ExtractTOP100PMIDCitation(String root_directory, String QueryTerm, 
			String fileDirAndName_metaical2pubtator_with_metaID){
		
		ArrayList<Integer> loop_unique_PMIDsList = new ArrayList<Integer>();
		ArrayList<Integer> ESumm_citationList = new ArrayList<Integer>();
		ArrayList<Integer> ESumm_PubDateList = new ArrayList<Integer>();
		ArrayList<String> ESumm_AuthorList = new ArrayList<String>();
		ArrayList<String> ESumm_TitleList = new ArrayList<String>();
		ArrayList<String> ESumm_FullJournalNameList = new ArrayList<String>();
	
		try {
			
			String uid_query_string = "";
			
			int total_PMID = matched_Pair_PMIDList_TOP100.size();
			System.out.print("Extracting PMIDs [query="+QueryTerm+ "]..PMID#="+total_PMID);
			
			//System.out.println("total_PMID = " + total_PMID);
			
			int loop_count = 0;
			int num_query_size = 200; //<----2018-03-06 Changed from 800 to 200 by Michael Lee
			//System.out.println("num_query_size = " + num_query_size);
			
			if(total_PMID%num_query_size == 0){
				loop_count = total_PMID/num_query_size;
			}
			else{
				loop_count = (total_PMID/num_query_size)+1;
			}
					
			//System.out.println("loop_count = " + loop_count);
			
			
			int end_number = -1;
			URL obj  = null;
			HttpURLConnection con = null;
			int start_i = 1;
			BufferedReader in = null;
			
			for(int i = start_i; i <= loop_count; i++){
				
				uid_query_string = "";
				
				if(i*num_query_size > total_PMID){
					end_number = total_PMID;
				}
				else{
					end_number = i*num_query_size;
				}
				
				System.out.print(".");
				//System.out.println("(#"+i+") --- number_PMIDs = " + end_number);
				
				for(int j = (i-1)*num_query_size; j < end_number ; j++){

					//loop_unique_PMIDsList.add(total_unique_PMIDsList.get(j));
					if(uid_query_string.equals("")){
						uid_query_string = matched_Pair_PMIDList_TOP100.get(j)+"";
					}
					else{
						uid_query_string = uid_query_string + "," + matched_Pair_PMIDList_TOP100.get(j)+"";
					}
				}

				//uid_query_string = "20467650";
				
				//System.out.print("-----uid_query_string.length() = "+uid_query_string.length());
				
				
				if(uid_query_string.length() > 0 ){
					
					String query_url 
							= "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?"+
									"db=pubmed"+ "&id=" + uid_query_string;
			
					obj = new URL(query_url);
					con = (HttpURLConnection) obj.openConnection();
			
					// optional default is GET
					con.setRequestMethod("POST");
			
					//add request header
					con.setRequestProperty("User-Agent", "UTF-8");
					
					
					in = new BufferedReader(
					        new InputStreamReader(con.getInputStream()));
					
					String inputLine = "";
					String temp_PMID = null;
					String temp_year = null;
					String temp_author = null;
					//String temp_title = null;
					String escape_string = "";
					int escape_start = -1;
					int escape_end = -1;

	        	  	char c = ' ';
	        	  	
	        	  	final int BUFFER_SIZE = 2048;
	        	  	CharBuffer buffer = CharBuffer.allocate(BUFFER_SIZE);
	        	  	
	        	  	while ( in.read(buffer) > 0) {
						
					    buffer.flip();
					    
					    while (buffer.hasRemaining()) {	  
					    	c = buffer.get();
					    	//System.out.print(c);

				    		  if(c=='\n'){
				        	  	//System.out.println(inputLine);
				    			escape_string = "";
								
								if(inputLine.indexOf("&") > 0){
									
									//System.out.println(inputLine);
									
									escape_start = inputLine.indexOf("&", -1);
									escape_end = inputLine.indexOf(";", escape_start);
									
									while(escape_start > 0 && escape_end > 0 && (escape_end-escape_start < 5)){
										
										escape_string = inputLine.substring(escape_start, escape_end+1);
										
										if(escape_string.equals("&amp;")){
											inputLine = inputLine.replaceAll("&amp;", "&");
										}
										else if(escape_string.equals("&lt;")){
											escape_end = inputLine.indexOf("&gt;", escape_start);
											if(escape_end > 0){
												inputLine = inputLine.substring(0, escape_start) + inputLine.substring(escape_end+4);
											}
											else{
												inputLine = inputLine.replaceAll("&lt;", "<");
											}
										}
										else if(escape_string.equals("&gt;")){
											inputLine = inputLine.replaceAll("&gt;", ">");
										}
										else{
											inputLine = inputLine.substring(0, escape_start) + inputLine.substring(escape_end);
										}

										escape_start = inputLine.indexOf("&", -1);
										escape_end = inputLine.indexOf(";", escape_start);
									}
								} 
			    			  
								//System.out.println(inputLine);
				        	  	
				        	  	if(inputLine.indexOf("<Id>")!=-1){
									temp_PMID = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Id>"));
									loop_unique_PMIDsList.add(Integer.parseInt(temp_PMID));
									temp_author= null;
									temp_year = null;
									//System.out.println(temp_PMID);
								}
								
								else if(inputLine.indexOf("<Item Name=\"PubDate\"")!=-1){
									temp_year = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf(">")+5);
									try{
									
										ESumm_PubDateList.add(Integer.parseInt(temp_year));
										
									} catch ( NumberFormatException e) {
										inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
										temp_year = inputLine.substring(inputLine.indexOf(" ")+1, inputLine.indexOf(" ")+5);
										//System.out.println(temp_year);
										ESumm_PubDateList.add(Integer.parseInt(temp_year));
									}
									
								}
								else if(inputLine.indexOf("<Item Name=\"Author\"") != -1 ){
									if(temp_author == null){
										temp_author = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
										ESumm_AuthorList.add(temp_author);
									}
								}
								else if(inputLine.indexOf("<Item Name=\"CollectiveName\"") != -1 ){
									if(temp_author == null){
										temp_author = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
										ESumm_AuthorList.add(temp_author);
									}
								}
								else if(inputLine.indexOf("<Item Name=\"Title\"") != -1 ){
									
									if(temp_author == null){
										ESumm_AuthorList.add("-");
									}
									
									if(inputLine.indexOf("</Item>") != -1 ){
										inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
									}
									else{
										inputLine = in.readLine();
									}
									inputLine = inputLine.replaceAll("\\[", "");
									inputLine = inputLine.replaceAll("\\]", "");
									ESumm_TitleList.add(inputLine);
								}
								else if(inputLine.indexOf("<Item Name=\"FullJournalName\"") != -1 ){
									if(inputLine.indexOf("</Item>") != -1 ){
										inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
									}
									else{
										inputLine = in.readLine();
									}
									
									if(inputLine.indexOf(":") != -1 ){
										inputLine = inputLine.substring(0, inputLine.indexOf(":"));
									}
									if(inputLine.indexOf("(") != -1 ){
										inputLine = inputLine.substring(0, inputLine.indexOf("(")-1);
									}
									if(inputLine.indexOf("&amp;") != -1 ){
										inputLine = inputLine.replaceAll("&amp;", "&");
									}
									ESumm_FullJournalNameList.add(inputLine);
								
								}
								else if(inputLine.indexOf("<Item Name=\"PmcRefCount\"")!=-1){
									inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
									ESumm_citationList.add(Integer.parseInt(inputLine));
									//System.out.println(inputLine);
								}
								
				        	  	inputLine = "";
				        	  	
				          }//if(c=='\n'){
				          else{
				        	  inputLine = inputLine + c;
				          }  
					    }//while (buffer.hasRemaining()) {	
					    buffer.clear();
	        	  	}//while ((in.read(buffer) > 0) {
	        	  	
	        	  	if(in!=null){
	        	  		in.close();
	        	  	}
					

				}//if(uid_query_string.length() > 0 )
				
				
			}//for loop
			//======================================================
			
			
			//writer
			File file_TOP100_view_publication 
			= new File(root_directory+QueryTerm+"_TOP100[metabolite]");
			BufferedWriter out_TOP100_view_publication = new BufferedWriter(new FileWriter(file_TOP100_view_publication));
			
			int index_PMID = -1;
			int index_meta_name = -1;
			
			
			
			// ArrayList for metaical2pubtator metabolite names and HMDB ID
			ArrayList<Integer> meta2pubtator_metaID_dataList = new ArrayList<Integer>();
			ArrayList<String> meta2pubtator_meta_name_dataList = new ArrayList<String>();
			ArrayList<String> meta2pubtator_HMDB_ID_dataList = new ArrayList<String>();
			
			//prepare file ---- metaID to metabolite name mentions	 			
	  		File metaical2pubtator_with_metaID_inputFile = new File(fileDirAndName_metaical2pubtator_with_metaID);

	  		String temp = null;
	  		String[] token_string = null;
	  		if (metaical2pubtator_with_metaID_inputFile.exists()){
				
				BufferedReader in_c = new BufferedReader(new FileReader(metaical2pubtator_with_metaID_inputFile));
				temp = in_c.readLine();//skip title line
				while ((temp = in_c.readLine()) != null) {
					token_string = temp.split("\t");
					if(token_string.length == 3){	
						meta2pubtator_metaID_dataList.add(Integer.parseInt(token_string[0]));
						meta2pubtator_HMDB_ID_dataList.add(token_string[1]);
						meta2pubtator_meta_name_dataList.add(token_string[2]);
					}
				}
				
				if(in_c!=null){
		  			in_c.close();
		  		}

	  		}
	  			
	  		//String HMDB_ID = "-";
	  		String meta_Name = "-";
			for(int m = 0; m < matched_Pair_metaIDList_TOP100.size(); m++){
				index_meta_name = meta2pubtator_metaID_dataList.indexOf(matched_Pair_metaIDList_TOP100.get(m));
				if(index_meta_name != -1){
					//HMDB_ID = meta2pubtator_HMDB_ID_dataList.get(index_meta_name);
					meta_Name = meta2pubtator_meta_name_dataList.get(index_meta_name);
				}

				index_PMID = loop_unique_PMIDsList.indexOf(matched_Pair_PMIDList_TOP100.get(m));
				if(index_PMID!=-1){
					//System.out.println(token_string[1]);
					out_TOP100_view_publication.write(
						matched_Pair_PMIDList_TOP100.get(m) + "\t" +
								meta_Name + "\t" +
						ESumm_AuthorList.get(index_PMID) + "\t" +
						ESumm_TitleList.get(index_PMID) + "\t" +
						ESumm_FullJournalNameList.get(index_PMID) + "\t" +
						ESumm_PubDateList.get(index_PMID) + "\t" +
						ESumm_citationList.get(index_PMID) 
						+ "\n");
				}

			}
			
			if(out_TOP100_view_publication!= null){
				out_TOP100_view_publication.close();
			}
			

		} catch ( IOException e) {
			e.printStackTrace();
			
		}
		

	}
	


	public static void main(String[] args) throws IOException  {
		
		String query_string = "";
		String species = "";
		if(args.length > 0){
			
			species = args[args.length-1];
			
			for(int i = 0; i < args.length-1; i++){
				if(i==0){
					query_string = args[i];
				}
				else{
					if(args[i].toLowerCase().equals("or")){
						query_string = query_string + "+" + species + "+" + args[i];
					}
					else{
						query_string = query_string + "+" + args[i];
					}
				}
			}
			
			
			if(query_string.equals("") || query_string == null || query_string.isEmpty()){
				System.out.println("Sorry, the query term is empty!");
			}
			else{
				PURPOSE_meta2pubtator obj = new PURPOSE_meta2pubtator(query_string, species);
			}
			
		}
		else{
			Scanner in = new Scanner(System.in);
			System.out.print("Query Term + space + species(ex. brain human/mouse/rat/fly/worm/yeast) [-1 to exit]:");
			query_string = in.nextLine();
			
			String[] query_array = null;
			String species_s = "";
			
			while(!query_string.equals("-1")){
				if(query_string.equals("") || query_string == null || query_string.isEmpty()){
					System.out.println("Sorry, the query term is empty!");
				}
				else{
					query_array = query_string.split(" ");
					species_s = query_array[query_array.length-1];
					
					for(int i = 0; i < query_array.length-1; i++){
						if(i==0){
							query_string = query_array[i];
						}
						else{
							if(query_array[i].toLowerCase().equals("or")){
								query_string = query_string + "+" + species_s + "+" + query_array[i];
							}
							else{
								query_string = query_string + "+" + query_array[i];
							}
							
						}
					}
					PURPOSE_meta2pubtator obj = new PURPOSE_meta2pubtator(query_string, species_s);
				}
				
				System.out.print("Query Term + space + species(ex. brain human/mouse/rat/fly/worm/yeast) [-1 to exit]:");
				query_string = in.nextLine();
			}
		}
		
		

	}//end of main method

	










}//end of class









	










class metaID_to_PMID_data{
	
	private int meta_ID;
	private int PMID;
	
	public metaID_to_PMID_data(int meta_ID, int PMID){
		this.meta_ID = meta_ID;
		this.PMID = PMID;
	}
	
	public int getMetaID() {
		return meta_ID;
	}

	public void setMetaID(int meta_ID) {
		this.meta_ID = meta_ID;
	}

	public int getPMID() {
		return PMID;
	}

	public void setPMID(int pMID) {
		PMID = pMID;
	}
	
}


class PMID_to_metaID_data{
	
	private int PMID;
	private int meta_ID;
	
	public PMID_to_metaID_data(int PMID, int meta_ID){
		this.PMID = PMID;
		this.meta_ID = meta_ID;
	}
	
	public int getmeta_ID() {
		return meta_ID;
	}

	public void setGeneID(int meta_ID) {
		this.meta_ID = meta_ID;
	}

	public int getPMID() {
		return PMID;
	}

	public void setPMID(int pMID) {
		PMID = pMID;
	}
	
}



class meta_data{
	
	private String disease_topic;
	private String meta_ID;
	private String HMDB_ID;
	private String  meta_Name;
	private double PURPOSE_score;
	private int count_Big_P;
	private int count_T_intersect_P;
	private int total_citation_count;
	private double PartI_value;
	private double PartII_value;
	private double PartIII_value;

	public meta_data(
			String disease_topic,
			String meta_ID,
			String HMDB_ID,
			String  meta_Name, 
			double PURPOSE_score, 
			int count_Big_P,  
			int count_T_intersect_P, 
			int total_citation_count, 
			double PartI_value, 
			double PartII_value, 
			double PartIII_value){
		this.disease_topic= disease_topic;
		this.meta_ID = meta_ID;
		this.HMDB_ID = HMDB_ID;
		this.meta_Name = meta_Name;
		this.PURPOSE_score = PURPOSE_score;
		this.count_Big_P = count_Big_P;
		this.count_T_intersect_P = count_T_intersect_P;
		this.total_citation_count = total_citation_count;
		this.PartI_value = PartI_value;
		this.PartII_value = PartII_value;
		this.PartIII_value = PartIII_value;
	}

	
	public String getDisease_topic() {
		return disease_topic;
	}

	public void setDisease_topic(String disease_topic) {
		this.disease_topic = disease_topic;
	}
	
	public String getmeta_ID() {
		return meta_ID;
	}

	public void setmeta_ID(String meta_ID) {
		this.meta_ID = meta_ID;
	}
	
	public String getHMDB_ID() {
		return HMDB_ID;
	}

	public void setHMDB_ID(String HMDB_ID) {
		this.HMDB_ID = HMDB_ID;
	}

	public String getmeta_Name() {
		return meta_Name;
	}

	public void setmeta_Name(String meta_Name) {
		this.meta_Name = meta_Name;
	}
	
	public double getPURPOSE_Score() {
		return PURPOSE_score;
	}

	public int getCount_Big_P() {
		return count_Big_P;
	}

	public void setCount_Big_P(int count_Big_P) {
		this.count_Big_P = count_Big_P;
	}
	
	public int getCount_T_intersect_P() {
		return count_T_intersect_P;
	}

	public void setCount_T_intersect_P(int count_T_intersect_P) {
		this.count_T_intersect_P = count_T_intersect_P;
	}

	public int getTotal_citation_count() {
		return total_citation_count;
	}

	public void setTotal_citation_count(int total_citation_count) {
		this.total_citation_count = total_citation_count;
	}
	
	public double getPartI_value() {
		return PartI_value;
	}

	public void setPartI_value(double partI_value) {
		PartI_value = partI_value;
	}

	public double getPartII_value() {
		return PartII_value;
	}

	public void setPartII_value(double partII_value) {
		PartII_value = partII_value;
	}

	public double getPartIII_value() {
		return PartIII_value;
	}

	public void setPartIII_value(double partIII_value) {
		PartIII_value = partIII_value;
	}

	
}
