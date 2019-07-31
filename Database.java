package lab3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.util.Pair;

public class Database {

	public MongoDatabase ConnToDatabase(String path, int port)
	{
		MongoClient client = new MongoClient(path, port);
		MongoDatabase database = client.getDatabase("riw");
		return database;
	}

	
	public void InsertDb(HashMap <String, HashMap<String, Integer> > directIndex, String CollectionName, MongoDatabase db)
	{
		MongoCollection<Document> dbCollection = db.getCollection(CollectionName);
		
		ArrayList<Document> documents = new ArrayList<>();
		
		for (Map.Entry<String, HashMap<String, Integer>> kv : directIndex.entrySet())
		{
			Document document1 = new Document();
			document1.put("key", kv.getKey());
			
			HashMap<String,Integer> tempMap = kv.getValue();
			ArrayList<Document> tempDocs = new ArrayList<>();
			for (Map.Entry<String, Integer> strInt : tempMap.entrySet())
			{
				Document tdoc = new Document();
				tdoc.put("key", strInt.getKey());
				tdoc.put("value", strInt.getValue());
				tempDocs.add(tdoc);
			}
			
			document1.put("values", tempDocs);
			documents.add(document1);
		}
		dbCollection.insertMany(documents);


	}
	
	public void InsertWeights(HashMap<Pair<String, Double>, HashMap<String, Double> > weights,MongoDatabase db)
	{
		MongoCollection<Document> dbCollection = db.getCollection("Weights");
		
		ArrayList<Document> documents = new ArrayList<>();
		
		for (Map.Entry<Pair<String, Double>, HashMap<String, Double>> kv : weights.entrySet())
		{
			Document document1 = new Document();
			Pair<String, Double> name_length = kv.getKey();
			document1.put("key", name_length.getKey());
			document1.put("len", name_length.getValue());
			
			HashMap<String,Double> tempMap = kv.getValue();
			ArrayList<Document> tempDocs = new ArrayList<>();
			for (Map.Entry<String, Double> strDouble : tempMap.entrySet())
			{
				Document tdoc = new Document();
				tdoc.put("key", strDouble.getKey());
				tdoc.put("value", strDouble.getValue());
				tempDocs.add(tdoc);
			}
			
			document1.put("values", tempDocs);
			documents.add(document1);
		}
		dbCollection.insertMany(documents);
	}
	
	public List<Document> GetCollection(MongoDatabase db, String coll)
	{
		MongoCollection<Document> collection = db.getCollection(coll);
		List<Document> weights = (List<Document>)collection.find().into(new ArrayList<Document>());
		return weights;
	}
}
