package ie.tcd.vishwaka.LuceneSearchEngine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
public class QueryIndex
{
	public final static String INDEX_DIRECTORY = "../index";
	
    public void queryDoc() throws Exception
    {
    	Directory inDirectory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        DirectoryReader inReader = DirectoryReader.open(inDirectory);
        IndexSearcher inSearcher = new IndexSearcher(inReader);
        inSearcher.setSimilarity(new BM25Similarity());
        
		Document doc =null;
		
		File file = new File("../query/cran.qry.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
 		String currentLine = br.readLine();
		FileWriter fileWriter = new FileWriter("../result/filename.txt");
		BufferedWriter bw = new BufferedWriter(fileWriter);
		
		int index=0;
		boolean flag = false;
		StringBuilder search = new StringBuilder();
		while(currentLine != null)
		{
	        if (currentLine.matches("(.I)( )(\\d*)"))
	        {
	            if(index !=0)
	            {
					TopDocs topDocs = SearchByAll(search.toString().replaceAll("[^a-z A-Z 0-9]", ""), inSearcher);
		            for (ScoreDoc sd : topDocs.scoreDocs)
		            {
		            	doc = inSearcher.doc(sd.doc);
						
						bw.write(index+ " "+ "Q0"+ " " + doc.get("DocId") + " 0 "+ sd.score + "\t"+"STANDARD");
						bw.newLine();
					}
	            }
	            index=index+1;
	            flag = false;
	        }
	        else if(currentLine.matches("(.W)"))
	        {
	            search = new StringBuilder();
	            flag = true;
	        }
	        else {
	            if (flag)
	            {
	                search.append(currentLine + " ");
	            }
	        }
	        currentLine = br.readLine();
		}
		
		bw.close();
		fileWriter.close();
		inReader.close();
    }
    
    private static TopDocs SearchByAll(String search, IndexSearcher searcher) throws Exception
    {
        MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[]{"DocId", "Title", "Author", "Bibliography", "Work"}, new EnglishAnalyzer());
        Query query = qp.parse(search);
        TopDocs hits = searcher.search(query, 50);
        return hits;
    }
}
