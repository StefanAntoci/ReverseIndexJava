package lab3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class BoolSearch {
	public String filename;
	public BoolSearch(String FileName) throws FileNotFoundException
	{
		filename = FileName;
	}
	
	public ArrayList<String> SearchWord (String word) throws IOException
	{
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String delimiters = ":,= ";
		String line;
		ArrayList<String> documents = new ArrayList<String>();
		while ( ( line = br.readLine() ) != null )
		{
			int occurence = line.indexOf(' ');
			String word_in_file = line.substring(0, occurence);
			if (word_in_file.equals(word))
			{
				String[] words_in_file = line.split(" ");
				for (int i = 1; i < words_in_file.length; i++)
				{
					if (!delimiters.contains(words_in_file[i]))
					{
						try 
						{
							int number = Integer.parseInt(words_in_file[i]);
						}
						catch(NumberFormatException nte)
						{
							documents.add(words_in_file[i]);
						}
					}
				}
				break;
			}			
		}
		br.close();
		fr.close();
		
		return documents;
	}

	public ArrayList<String> AndOperation(ArrayList<String> DocumentsWords1, ArrayList<String> DocumentsWords2) throws IOException
	{	
		ArrayList<String> AndResult = new ArrayList<String>();
		for (int i = 0; i < DocumentsWords1.size(); i++)
		{
			String word = DocumentsWords1.get(i);
			if (DocumentsWords2.contains(word) )
			{
				AndResult.add(word);
			}
		}
		
		return AndResult;
	}
	
	public ArrayList<String> OrOperation(ArrayList<String> DocumentsWords1, ArrayList<String> DocumentsWords2) throws IOException
	{
		//ArrayList<String> DocumentsWords1 = SearchWord(word1, "E:\\AC-CTI\\An4Sem2\\RIW-me\\ReverseIndex\\reverse_index\\text1.txt");
		//ArrayList<String> DocumentsWords2 = SearchWord(word2, "E:\\AC-CTI\\An4Sem2\\RIW-me\\ReverseIndex\\reverse_index\\text1.txt");
		
		ArrayList<String> OrResult = new ArrayList<String>();
		
		for (int i = 0; i < DocumentsWords1.size(); i++)
		{
			String word = DocumentsWords1.get(i);
				OrResult.add(word);
		}
		
		for (int i = 0; i < DocumentsWords2.size(); i++) 
		{
			String word = DocumentsWords2.get(i);
			if (!DocumentsWords1.contains(word))
			{
				OrResult.add(word);
			}
		}
		return OrResult;
	}
	
	public ArrayList<String> NotOperation(ArrayList<String> DocumentsWords1, ArrayList<String> DocumentsWords2) throws IOException
	{
		//ArrayList<String> DocumentsWords1 = SearchWord(word1, "E:\\AC-CTI\\An4Sem2\\RIW-me\\ReverseIndex\\reverse_index\\text1.txt");
		//ArrayList<String> DocumentsWords2 = SearchWord(word2, "E:\\AC-CTI\\An4Sem2\\RIW-me\\ReverseIndex\\reverse_index\\text1.txt");
		
		ArrayList<String> NotResult = new ArrayList<String>();
		
		
		for (int i = 0; i < DocumentsWords1.size(); i++) 
		{
			String word = DocumentsWords1.get(i);
			if (!DocumentsWords2.contains(word))
			{
				NotResult.add(word);
			}
		}
		return NotResult;
	}
	
	public ArrayList<String> ExprEvaluation(String expression) throws IOException
	{
		String[] BoolOperators = {"and", "or", "not"};
		String delimiter = " ";
		String operand = "";
		Boolean firstSearch = false;
		Boolean secondSearch = false;
		ArrayList<String> DocumentWords1 = new ArrayList<String>();
		ArrayList<String> DocumentWords2 = new ArrayList<String>();
		ArrayList<String> ExpressionResult = new ArrayList<String>();
		StringTokenizer WordSplitter = new StringTokenizer(expression, delimiter);
		
		while (WordSplitter.hasMoreTokens())
		{
			String word = WordSplitter.nextToken();
			
			boolean isOperator = Arrays.stream(BoolOperators).anyMatch(word::equals);
			
			if (isOperator)
			{
				operand = word;
			}
			else
			{
				if (!firstSearch)
				{
					DocumentWords1 = SearchWord(word);
/*					for (int i = 0; i < DocumentWords1.size(); i++)
					{
						System.out.println(DocumentWords1.get(i));
					}*/
					firstSearch = true;
				}
				else if (!secondSearch)
				{
					DocumentWords2 = SearchWord(word);
/*					for (int i = 0; i < DocumentWords2.size(); i++)
					{
						System.out.println(DocumentWords2.get(i));
					}*/
					secondSearch = true;
				}
			}
			//System.out.println("operand = " + operand);
			if (firstSearch && secondSearch && !operand.equals(""))
			{
				
				switch(operand)
				{
					case "and":
						ExpressionResult = AndOperation(DocumentWords1, DocumentWords2);
						break;
					case "or":
						ExpressionResult = OrOperation(DocumentWords1, DocumentWords2);
						break;
					case "not":
						ExpressionResult = NotOperation(DocumentWords1, DocumentWords2);
						break;
				}
				
				//System.out.println(ExpressionResult.size());
/*				for (int i = 0; i < ExpressionResult.size(); i++)
				{
					System.out.println(ExpressionResult.get(i));
				}*/
				DocumentWords1 = ExpressionResult;
				secondSearch = false;
			}
		}
		return ExpressionResult;
	}
}
