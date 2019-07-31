package lab3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Indexes {
	public String direct_idx_loc = "E:\\AC-CTI\\An4Sem2\\RIW-me\\ReverseIndex\\direct_index";
	public String reverse_idx_loc = "E:\\AC-CTI\\An4Sem2\\RIW-me\\ReverseIndex\\reverse_index";
	public int idx_in_file ;
	
	public Indexes(String di_idx_loc,String rev_idx_loc)
	{
		direct_idx_loc = di_idx_loc;
		reverse_idx_loc = rev_idx_loc;
	}

	public void IdxToFile(HashMap <String, HashMap<String, Integer> > Word_in_files, String folderLoc, int idx_per_file) throws IOException
	{
		int actual_idx_in_file = 0;
		int created_files = 0;
		StringBuilder sb = new StringBuilder();
		BufferedWriter bw_map = new BufferedWriter (new FileWriter(folderLoc+"\\map.txt"));
		BufferedWriter bw = new BufferedWriter (new FileWriter(folderLoc+"\\di"+created_files + ".txt"));
		for ( Map.Entry<String, HashMap<String, Integer> > entry: Word_in_files.entrySet() )
		{
				if (actual_idx_in_file == idx_per_file)
				{
					bw.close();
					actual_idx_in_file = 0;
					created_files ++;
					bw = new BufferedWriter (new FileWriter(folderLoc+"\\di"+created_files + ".txt"));
				}
				
				
				sb.append(entry.getKey());
				sb.append("  :: ");
				
				for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()  )
				{
					sb.append(entry2.getKey());
					sb.append("  - ");
					sb.append(entry2.getValue());
					sb.append("   ");
				}
				bw.write(sb.toString());
				sb.delete(0, sb.length());
				actual_idx_in_file ++;
				bw_map.write(entry.getKey() + "  :  " + folderLoc+"/di"+created_files + ".txt\n");
		}
		bw.close();
		bw_map.close();
	}
	
	public String Check_Reverse_idx(String word, HashMap <String, HashMap<String, Integer> > Reverse_idx_map )
	{
		for ( Map.Entry<String, HashMap<String, Integer> > entry: Reverse_idx_map.entrySet() )
		{
			if (word.equals(entry.getKey()))
			{
				return entry.getKey();
			}
		}
		return null;
	}
	
	public HashMap <String, HashMap<String, Integer>> Direct_To_Reverse ( HashMap <String, HashMap<String, Integer> > Word_in_files )
	{ 
		HashMap<String, HashMap<String, Integer> > ReverseIdx  = new HashMap<String, HashMap<String, Integer> >();
		
		for (Map.Entry<String, HashMap<String, Integer> > entry: Word_in_files.entrySet())
		{			
			for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()  )
			{
				String key = Check_Reverse_idx(entry2.getKey(), ReverseIdx);
				if ( key != null )
				{
					HashMap<String, Integer> file_app = ReverseIdx.get(key);
					file_app.put(entry.getKey(), entry2.getValue());
				}
				else
				{
					HashMap<String, Integer> file_app = new HashMap<String, Integer>();
					file_app.put(entry.getKey(), entry2.getValue());
					ReverseIdx.put(entry2.getKey(), file_app);
				}
			}
		}
		return ReverseIdx;
	}

}
