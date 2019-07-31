package lab3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.bson.Document;

import javafx.util.*;

public class CosSimilarity {
	
	public String path_to_file_weights;
	public String path_to_file_length;
	
	public CosSimilarity()
	{
		
	}
	
	public CosSimilarity(String _path_to_file_weights, String _path_to_file_length)
	{
		path_to_file_weights = _path_to_file_weights;	
		path_to_file_length = _path_to_file_length;
	}
	
	public HashMap<Pair<String,Double>, HashMap<String, Double >> DocumentToVector(HashMap <String, HashMap<String, Integer> > Word_in_files, HashMap <String, HashMap<String, Integer> > Reverse_idx_map) throws IOException
	{
		int nr_of_files = Word_in_files.size();
		
		HashMap<Pair<String,Double>, HashMap<String, Double >> weights_per_file = new HashMap<Pair<String,Double>, HashMap<String, Double >>();
		//BufferedWriter length_writer = new BufferedWriter( new FileWriter(path_to_file_length) );
		for (Map.Entry<String, HashMap<String, Integer> > entry: Word_in_files.entrySet())
		{
			int lastIndex = entry.getKey().lastIndexOf("\\");
			String filename = entry.getKey().substring(lastIndex+1);
			Double file_length = 0.0;
			int words_in_file = entry.getValue().size();
			
			HashMap<String, Double > weights = new HashMap<String,Double>();
			
			for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet() )
			{
				String key = entry2.getKey();
				int AppInFile = entry2.getValue();
				int DocumentsNr = 0;
				
				for (Map.Entry<String, HashMap<String, Integer> > rev_entry: Reverse_idx_map.entrySet())
				{
					if (rev_entry.getKey() == key)
					{
						DocumentsNr = rev_entry.getValue().size();
						break;
					}
				}
				
				double tf = tf_term(AppInFile, nr_of_files);
				double idf = idf_term(nr_of_files, DocumentsNr);
				
				double value = tf*idf;
				if (value < 0.01)
				{
					value = 0.0;
				}
				file_length += value*value;
				weights.put(key,  value);
			}
			file_length = Math.sqrt(file_length);

			Pair<String, Double> pair = new Pair<>(filename, file_length);
			weights_per_file.put(pair, (HashMap<String, Double>)weights.clone());
			weights.clear();
		}
		//length_writer.close();
		return weights_per_file;
	}
	
	public void WriteToFile(HashMap<String, HashMap<String, Double>> hash_with_weights) throws IOException
	{
		for (Map.Entry<String, HashMap<String, Double> > entry: hash_with_weights.entrySet())
		{
			String file_path = path_to_file_weights + "\\" + entry.getKey();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file_path) );
			
			for (Map.Entry<String, Double> entry2 : entry.getValue().entrySet() )
			{
				writer.append(entry2.getKey());
				writer.append(": ");
				writer.append(entry2.getValue().toString());
				writer.newLine();
			}
			writer.close();
		}
	}
	
	public double tf_term(int app, int words_in_file)
	{
		return (double)app/(double)words_in_file;
	}
	
	public double idf_term(int doc_nr, int term_doc_nr)
	{
		double expression = (double)doc_nr/ (double)1 + (double)term_doc_nr;
		
		return Math.log(expression);
	}
	
	public void EvalExpr(List<Document>dir_idx, List<Document>rev_idx, List<Document>file_weights, String query)
	{
		ArrayList<String> can_form_query = GetQueryCannonicForm(query);
		//System.out.println(can_form_query);
		int nrDocs = dir_idx.size();
		HashMap<String, Integer> query_map = new HashMap<String, Integer>();
		
		for (String word : can_form_query)
		{
			Boolean word_found = false;
			for (Map.Entry<String, Integer> entry : query_map.entrySet())
			{
				if (word.equals(entry.getKey()))
				{
					int value = entry.getValue();
					value = value++;
					query_map.put(entry.getKey(), value);
					word_found = true;
					break;
				}
			}
			if (!word_found)
			{
				query_map.put(word, 1);
			}
		}
		
		HashMap<String, Double> query_weights = new HashMap<String, Double>();
		
		for (Map.Entry<String, Integer> entry: query_map.entrySet())
		{
			double tf = tf_term(entry.getValue(), query_map.size());
			int nr_word_specific_docs = 0;
			
			for (int i = 0; i < rev_idx.size(); i++)
			{
				if (rev_idx.get(i).getString("key").equals(entry.getKey() ) )
				{
					List<Document> values = (List<Document>)rev_idx.get(i).get("values");
					nr_word_specific_docs = values.size();
					break;
				}
			}
			double idf = idf_term(nrDocs, nr_word_specific_docs);
			
			query_weights.put(entry.getKey(), tf*idf);
		}
		
		//System.out.println(query_weights);
		
		
	}
	
	public ArrayList<String> GetQueryCannonicForm(String query)
	{
		String delimiters = ",.()?!\":; \t";
		StringTokenizer st = new StringTokenizer(query, delimiters);
		Porter porter = new Porter();
		ArrayList<String> cannonic_form_term = new ArrayList<String>();
		while (st.hasMoreTokens())
		{
			String word = st.nextToken();
			word = porter.stripAffixes(word);
			cannonic_form_term.add(word);			
		}
		return cannonic_form_term;
	}
}
