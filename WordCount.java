package lab3;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class WordCount {
	
	public static HashMap <String, HashMap<String, Integer> > Word_in_files;
	public static HashMap <String, HashMap<String, Integer> > Reverse_idx_map;	
	
	public WordCount()
	{
		Word_in_files = new HashMap <String, HashMap<String, Integer> > ();

	}
	
	public static void listFilesForFolder( File folder) {
	    for ( File fileEntry : folder.listFiles()) {
	    	//System.out.println(fileEntry.getAbsolutePath());
	        if (fileEntry.isDirectory()) {
	        	
	            listFilesForFolder(fileEntry);
	        }
	        else 
	        {
	        	WordParser wd = new WordParser();
	            HashMap<String, Integer> appearance_in_file = wd.WordCounter( fileEntry.getAbsolutePath());
	        	//System.out.println("hashmap " + appearance_in_file);	            
	            Word_in_files.put(fileEntry.getAbsolutePath(), appearance_in_file);	            
	        }
	    }
	}
	
	public static void PrintHashMap ()
	{

		for (Map.Entry<String, HashMap<String, Integer> > entry: Word_in_files.entrySet())
		{
			System.out.println("file:  " + entry.getKey());
			System.out.println("hash map:  " + entry.getValue());
		}
		
	}
	

	
	public static void main(String[] args) throws IOException {		
		// TODO Auto-generated method stub
		String current_dir = System.getProperty("user.dir");
		FileReader fr = new FileReader(current_dir + "\\src\\lab3\\config2.ini");
		BufferedReader br = new BufferedReader(fr);
		String line;
		String files_path = "";
		String dir_idx_path = "";
		String rev_idx_path = "";
		String database_path = "";
		String database_port = "";
		while ( ( line = br.readLine() ) != null )
		{
			String[] parameters = line.split("=");
			
			if (parameters[0].equals("files_path") )
			{
				files_path = parameters[1];
			}
			else if (parameters[0].equals("dir_idx_path") )
			{
				dir_idx_path = parameters[1];
			}
			else if (parameters[0].equals("rev_idx_path") )
			{
				rev_idx_path = parameters[1];
			}
			else if (parameters[0].equals("database_path") )
			{
				database_path = parameters[1];
			}
			else if (parameters[0].equals("database_port") )
			{
				database_port = parameters[1];
			}
		}
		
		System.out.println(files_path);
		System.out.println(dir_idx_path);
		System.out.println(rev_idx_path);
		System.out.println(database_port);
		System.out.println(database_path);
		
		File file = new File(files_path);
		WordCount wc = new WordCount();
		wc.listFilesForFolder(file);

		Indexes ind = new Indexes(dir_idx_path, rev_idx_path);
		HashMap<String, HashMap<String, Integer>> ReverseIdx = ind.Direct_To_Reverse(Word_in_files);
	
		Database mongodb = new Database();
		MongoDatabase mongo = mongodb.ConnToDatabase(database_path, Integer.parseInt(database_port));
		mongodb.InsertDb(Word_in_files, "DirectIndex", mongo);
		mongodb.InsertDb(ReverseIdx, "ReverseIndex", mongo);	
		
		CosSimilarity cs = new CosSimilarity();
		HashMap<Pair<String, Double>, HashMap<String, Double> > weights2 = cs.DocumentToVector(Word_in_files, ReverseIdx);
		mongodb.InsertWeights(weights2, mongo);
	

		
		
		//cs.EvalExpr(direct_idx, reverse_idx, weights, "extended resolution affiliate");
		
		Search searchQuery = new Search(cs);
		
		Boolean option = true;
		
		while(option)
		{
			
			List<Document> weights = mongodb.GetCollection(mongo, "Weights");
			List<Document> direct_idx = mongodb.GetCollection(mongo, "DirectIndex");
			List<Document> reverse_idx = mongodb.GetCollection(mongo, "ReverseIndex");
			
			System.out.println("Introduce query to search");
			Scanner in = new Scanner(System.in);
			String query = in.nextLine();
			ArrayList<Pair<String, Double> >  result = searchQuery.EvalExpr(direct_idx, reverse_idx, weights, query);
			
			System.out.println("Result for your query is: ");
			for (Pair<String, Double> pair : result)
			{
				System.out.println(pair.getKey() + "  " + pair.getValue());
			}
			
			System.out.println("Print 1 for another search, another number to quit");
			int opt = in.nextInt();
			if (opt == 1)
				option = true;
			else
				option = false;
		}
	}
	


}
