package lab3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.bson.Document;

import javafx.util.Pair;

public class Search {
	
	public CosSimilarity cs;
	
	public Search(CosSimilarity _cs)
	{
		cs = _cs;
	}
	
	public ArrayList<Pair<String, Double> >  EvalExpr(List<Document>dir_idx, List<Document>rev_idx, List<Document>file_weights, String query)
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
		double query_length = 0.0;
		for (Map.Entry<String, Integer> entry: query_map.entrySet())
		{
			double tf = cs.tf_term(entry.getValue(), query_map.size());
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
			double idf = cs.idf_term(nrDocs, nr_word_specific_docs);
			
			query_weights.put(entry.getKey(), tf*idf);
			query_length += (tf*idf);
		}		
		
		HashMap<String, Double> result = new HashMap<String, Double>();
		
		for (Document doc : file_weights)
		{
			List<Document> values = (List<Document>)doc.get("values");
			double query_mult_doc = 0.0;
			for (Map.Entry<String, Double> entry: query_weights.entrySet())
			{
				String word = entry.getKey();
				for (Document value : values)
				{
					if (value.getString("key").equals(word))
					{
						query_mult_doc += ( value.getDouble("value")*entry.getValue() );
						break;
					}
				}
			}			
			
			result.put(doc.getString("key"), query_mult_doc/ (doc.getDouble("len")*query_length) );
		}
		
		ArrayList<Pair<String, Double> > sort_map = SortMap(result);
		//System.out.println(result);
		return sort_map;
	}
	
	public ArrayList<Pair<String, Double> > SortMap(HashMap<String, Double> map)
	{
		ArrayList<Pair<String, Double> > sorted_result = new ArrayList<Pair<String, Double> >(); 
		//HashMap<String, Double> sorted_result = new HashMap<String, Double>();
		List<Double> results = new ArrayList<>(map.values());
		Collections.sort(results);
		//System.out.println(results);
		for ( int i = results.size()-1; i>=0; i-- )
		{
			//System.out.println(results.get(i));
			for (Map.Entry<String, Double> entry : map.entrySet())
			{
				if (entry.getValue().equals( results.get(i) ) )
				{
					Pair<String, Double> pair = new Pair<>(entry.getKey(), entry.getValue());
					//sorted_result.put( entry.getKey(), entry.getValue() );
					//System.out.println(sorted_result);
					sorted_result.add(pair);
				}
			}
		}

		return sorted_result;
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
