package com.itheima.lucene;

import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneTest {
	@Test
	public void createIndex() throws IOException {
		// TODO Auto-generated method stub
		Directory directory = FSDirectory.open(new File("F:\\test\\lucene"));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		
		File fileDir = new File("E:\\框架二\\lucene&solr\\00.参考资料\\searchsource");		
		for (File file: fileDir.listFiles()) {
			String fileName=file.getName();
			String filePath=file.getPath();
			String fileContent = FileUtils.readFileToString(file);
			long fileSize=FileUtils.sizeOf(file);
			Field fileNameField = new TextField("name",fileName,Store.YES);
			Field filePathField = new TextField("path",filePath,Store.YES);
			Field fileContentField = new TextField("content",fileContent,Store.YES);
			Field fileSizeField = new TextField("size",fileSize+"",Store.YES);
			Document document=new Document();
			document.add(fileNameField);
			document.add(filePathField);
			document.add(fileContentField);
			document.add(fileSizeField);
			
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	@Test
	public void searchIndex() throws IOException{
		Directory directory = FSDirectory.open(new File("F:\\test\\lucene"));
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		Query query = new TermQuery(new Term("content", "java"));
		TopDocs topDocs = indexSearcher.search(query, 10);
		System.out.println(topDocs.totalHits);
		System.out.println(topDocs.scoreDocs.length);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.getField("name"));
			System.out.println(document.getField("path"));
			//System.out.println(document.getField("content"));
			System.out.println(document.getField("size"));
			
		}
		indexReader.close();
	}
	@Test
	public void testAnalyzer() throws IOException{
		//Analyzer analyzer = new StandardAnalyzer();
		//Analyzer analyzer = new  CJKAnalyzer();
		//Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		
		TokenStream tokenStream = analyzer.tokenStream("test","美联英语对话,急速提升英语口语能力,4人小班授课,30天突破英语口语.美联英语轻松活跃的英文环境,全面提高你的口语听力,让你不知不觉开口说英语.");
		tokenStream.reset();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);		
		OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
		
		while (tokenStream.incrementToken()) {
			//System.out.println(offsetAttribute.startOffset());
			System.out.println(charTermAttribute);	
			//System.out.println(offsetAttribute.endOffset());
		}
		tokenStream.close();
	
	}
	
	
	@Test
	public void addDocument() throws IOException {
		// TODO Auto-generated method stub
		Directory directory = FSDirectory.open(new File("F:\\test\\lucene"));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig); 
		
		
		Field fileNameField = new TextField("name","身份证号信息保存文档.txt",Store.YES);
		Field filePathField = new StoredField("path","F:\\test\\身份证号信息保存文档.txt");
		Field fileIdField=new StringField("id", "342224156565555566",Store.YES);
		Field fileContentField = new TextField("content","王迪说到底是多少地方大幅度辅导费十多天发生过分过分的规范大哥发的规范",Store.YES);
		Field fileSizeField = new LongField("size",1024L,Store.YES);
		
		Document document=new Document();
		document.add(fileNameField);
		document.add(filePathField);
		document.add(fileContentField);
		document.add(fileIdField);
		document.add(fileSizeField);
		
		indexWriter.addDocument(document);
		
		indexWriter.close();
	}
	
	
}
