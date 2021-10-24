package ie.tcd.vishwaka.LuceneSearchEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CreateIndex {

	public final static String INDEX_DIRECTORY = "../index";
	
	public void createIndex(String arg) throws IOException {
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
//		/stopWordSet
		Analyzer analyzer = new EnglishAnalyzer();

		ArrayList<Document> documents = new ArrayList<Document>();

		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setSimilarity(new BM25Similarity());

		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iwriter = new IndexWriter(directory, config);

		String flag = "";
		String info = "";

		Document doc = new Document();
		BufferedReader br =  new BufferedReader(new FileReader(arg));
		String line;
		while (( line = br.readLine() ) != null) {
			if (line.startsWith(".I")) {
				if (flag != "None") {
					doc.add(new TextField(flag, info, Field.Store.YES));
					info = "";
					flag = "None";
					documents.add(doc);
				}
				doc = new Document();
				doc.add(new StringField("DocId", line.substring(2), Field.Store.YES));
				info = "";
				flag = "None";
			} else {
				if (line.equals(".T")) {
					if (flag ==  "Work") {
						continue;
					}
					if (flag != "None") {
						doc.add(new StringField(flag, info, Field.Store.YES));
						info = "";
						flag = "None";
					}
					flag = "Title";
				} else if (line.equals(".A")) {
					if (flag == "Work") {
						continue;
					}
					if (flag != "None") {
						doc.add(new StringField(flag, info, Field.Store.YES));
						info = "";
						flag = "None";
					}
					flag = "Author";
				} else if (line.equals(".W")) {
					if (flag ==  "Work") {
						continue;
					}
					if (flag != "None") {
						doc.add(new StringField(flag, info, Field.Store.YES));
						info = "";
						flag = "None";
					}
					flag =  "Work";
				} else if (line.equals(".B")) {
					if (flag ==  "Work") {
						continue;
					}
					if (flag != "None") {
						doc.add(new StringField(flag, info, Field.Store.YES));
						info = "";
						flag = "None";
					}
					flag = "Bibliography";
				} else {
					info = info + " " + line;
				}
			}
		}
		doc.add(new TextField(flag, info, Field.Store.YES));
		info = "";
		flag = "None";

		documents.add(doc);
		System.out.println(doc);

		iwriter.addDocuments(documents);

		iwriter.close();
		directory.close();
	}
}
