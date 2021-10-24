package lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryIndex {
	public final static String INDEX_DIRECTORY = "../index";

	public void queryDoc() throws Exception {
		//Connecting to index directory
		Directory inDirectory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		//To open the index directory
		DirectoryReader inReader = DirectoryReader.open(inDirectory);
		//create searcher for the reader
		IndexSearcher inSearcher = new IndexSearcher(inReader);
		//BM25 and Classic similarity
		inSearcher.setSimilarity(new BM25Similarity());
		//inSearcher.setSimilarity(new ClassicSimilarity());
		//inSearcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7)); //MAp score 0.2517

		Document doc = null;
		//To read query
		File file = new File("../query/cran.qry.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String currentLine = br.readLine();
		FileWriter fileWriter = new FileWriter("../result/filename.txt");
		BufferedWriter bw = new BufferedWriter(fileWriter);
		//Reading data from the file and searching for each query
		int index = 0;
		boolean flag = false;
		StringBuilder search = new StringBuilder();
		while (currentLine != null) {
			if (currentLine.matches("(.I)( )(\\d*)")) {
				if (index != 0) {
					TopDocs topDocs = SearchByAll(search.toString().replaceAll("[^a-z A-Z 0-9]", ""), inSearcher);
					for (ScoreDoc sd : topDocs.scoreDocs) {
						doc = inSearcher.doc(sd.doc);

						bw.write(index + " " + "Q0" + " " + doc.get("DocId") + " 0 " + sd.score + "\t" + "STANDARD");
						bw.newLine();
					}
				}
				index = index + 1;
				flag = false;
			} else if (currentLine.matches("(.W)")) {
				search = new StringBuilder();
				flag = true;
			} else {
				if (flag) {
					search.append(currentLine + " ");
				}
			}
			currentLine = br.readLine();
		}

		bw.close();
		fileWriter.close();
		inReader.close();
	}
	//Added Sto pwords
	private static TopDocs SearchByAll(String search, IndexSearcher searcher) throws Exception {
		List<String> stopWordList = Arrays.asList("a", "about", "above", "after", "again", "against", "all", "am", "an",
				"and", "any", "are", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both",
				"but", "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from",
				"further", "had", "has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's",
				"hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if",
				"in", "into", "is", "it", "it's", "its", "itself", "let's", "me", "more", "most", "my", "myself", "nor",
				"of", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own",
				"same", "she", "she'd", "she'll", "she's", "should", "so", "some", "such", "than", "that", "that's",
				"the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd",
				"they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up",
				"very", "was", "we", "we'd", "we'll", "we're", "we've", "were", "what", "what's", "when", "when's",
				"where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "would", "you",
				"you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves");
		CharArraySet stopWordSet = new CharArraySet(stopWordList, true);

		//MultiFieldQueryParser qp = new MultiFieldQueryParser(
				//new String[] { "DocId", "Title", "Author", "Bibliography", "Work" }, new StandardAnalyzer(stopWordSet));
		MultiFieldQueryParser qp = new MultiFieldQueryParser(
				new String[] { "DocId", "Title", "Author", "Bibliography", "Work" }, new EnglishAnalyzer(stopWordSet));
		
		Query query = qp.parse(search);
		TopDocs hits = searcher.search(query, 50);
		return hits;
	}
}
