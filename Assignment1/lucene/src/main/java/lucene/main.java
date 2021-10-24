package lucene;

public class main {

	public static void main(String[] args) throws Exception {
		if (args.length <= 0)
        {
            System.out.println("Expected corpus as input");
            System.exit(1);            
        }

		CreateIndex ci = new CreateIndex();
		QueryIndex qi = new QueryIndex();
        try {
        	//index created for input
			ci.createIndex(args[0]);
			//search doc for query
			qi.queryDoc();
		} catch (Exception e) {
			throw e;
		}
	}
}
