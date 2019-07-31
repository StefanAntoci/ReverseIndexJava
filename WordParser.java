package lab3;

import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class WordParser {


	
	public HashMap<String, Integer> WordCounter;
	public String[] exceptions ={"we", "ye", "lady", "he", "it", "sir"};
	public String[] stop_words = {"the", "it", "i","ye" ,"and", "he", "or", "not", "is"};
	
	
	public WordParser()
	{
		WordCounter = new  HashMap<String, Integer> ();	
	}
	
	public HashMap<String, Integer> WordCounter(String url)
	{
		try 
		{
			FileReader reader = new FileReader(url);
			BufferedReader rBuffer = new BufferedReader(reader);
			String line;
			String delimiters = ",.()?!\":; \t";
			String check_sent_final = "";
			while ( ( line = rBuffer.readLine() ) != null )
			{
				StringTokenizer st = new StringTokenizer(line, delimiters);
				
				while ( st.hasMoreTokens() )
				{
					String word = st.nextToken();
					
					word = word.toLowerCase();
					
					boolean isException = Arrays.stream(exceptions).anyMatch(word::equals);
					boolean isStop_Word= Arrays.stream(stop_words).anyMatch(word::equals);
					
					if (isException || !isStop_Word)
					{
						Porter porter = new Porter();
						String new_word = porter.stripAffixes(word);
						//System.out.println(new_word);
						UpdateHashMap(new_word);
/*						if (word.endsWith("\'"))
						{
							word = word.substring(0, word.length() - 2);
						}
						else if (word.contains("\'"))
						{
							Apostrophe(word);
						}
						else	
						{
							UpdateHashMap(word);
						}*/
					}
				}
			}
			
			
		}
		catch(IOException e)
		{
			System.out.println(url);
			System.out.println("No file found");
		}
		
		return WordCounter;
	}
	
	public void Apostrophe(String word)
	{
		String[] pronouns = {"I", "you", "he", "she" , "we", "they", "it", "don", "doesn", "didn"};
		
		
		
		if (word.equals("ain't"))
		{
			UpdateHashMap("am");
			UpdateHashMap("not");
		}
		else
		{
			String[] splitted_by_aps = word.split("\'");
			
			boolean contains = Arrays.stream(pronouns).anyMatch(splitted_by_aps[0]::equals);
			
			if (!contains)
			{
				UpdateHashMap(splitted_by_aps[0]);
			}
			else if (splitted_by_aps[1].equals("ll"))
			{
				UpdateHashMap(splitted_by_aps[0]);
				UpdateHashMap("will");
			}
			else if (splitted_by_aps[1].equals("t"))
			{
				String final_word = splitted_by_aps[0].substring(0, splitted_by_aps[0].length() - 1);
				UpdateHashMap( final_word );				
				UpdateHashMap("not");
			}
			else if (splitted_by_aps[1].equals("re"))
			{
				UpdateHashMap(splitted_by_aps[0]);				
				UpdateHashMap("are");
			}
			else if (splitted_by_aps[1].equals("s"))
			{
				UpdateHashMap(splitted_by_aps[0]);				
				UpdateHashMap("is");
			}
			else if (splitted_by_aps[1].equals("m"))
			{
				UpdateHashMap(splitted_by_aps[0]);				
				UpdateHashMap("am");
			}
		}
		
		
	}
	
	public void UpdateHashMap(String word)
	{	
		WordCounter.put(word, WordCounter.containsKey(word) ? WordCounter.get(word) + 1 : 1);
	}
	
	public void PrintHashMap ()
	{
		for (Map.Entry<String, Integer> entry: WordCounter.entrySet())
		{
			System.out.println("Word:  " + entry.getKey() + "  Appearances:  " + entry.getValue());
		}
		
	}
	
}
