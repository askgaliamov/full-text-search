package demo;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchService {

    private static final String CATCH_ALL_INDEX = "catch-all";
    private static final String DATA_JSON_FILE = "data.json";

    private final List<Language> languages = new ArrayList<>();

    private final IndexSearcher searcher;

    public SearchService() throws IOException {

        readJSONData();

        final Directory directory = new RAMDirectory();
        buildIndexesIn(directory);

        IndexReader reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    public List<Language> search(final String query) throws IOException {

        if (StringUtils.isBlank(query)) {
            return Collections.emptyList();
        }

        QueryParser parser = new QueryParser(CATCH_ALL_INDEX, new WhitespaceAnalyzer());
        try {

            Query searchQuery = parser.parse(query);

            TopDocs searchResult = searcher.search(searchQuery, languages.size());

            ScoreDoc[] hits = searchResult.scoreDocs;

            return getSearchResultAsList(hits);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<Language> getSearchResultAsList(ScoreDoc[] hits) {
        List<Language> result = new ArrayList<>(hits.length);
        for (ScoreDoc hit : hits) {
            Language language = languages.get(hit.doc);
            result.add(language);
        }
        return result;
    }

    private void readJSONData() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(DATA_JSON_FILE));
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(bytes);
        jsonParser.nextToken();
        ObjectMapper mapper = new ObjectMapper();
        while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
            Language foobar = mapper.readValue(jsonParser, Language.class);
            this.languages.add(foobar);
        }
    }

    private void buildIndexesIn(final Directory directory) throws IOException {
        PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer());
        IndexWriterConfig config = new IndexWriterConfig(analyzerWrapper);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        for (Language language : languages) {
            Document doc = new Document();
            doc.add(new TextField(CATCH_ALL_INDEX, language.getAll(), Field.Store.YES));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }


}
