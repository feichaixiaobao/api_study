package jp.feichaixiaobao.api.user.LeoJapan.service.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import jp.feichaixiaobao.api.user.LeoJapan.common.MovieConstant;
import jp.feichaixiaobao.api.user.LeoJapan.entity.MovieInfoEntity;
import jp.feichaixiaobao.api.user.LeoJapan.service.MovieLuceneService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 电影信息检查Service类(Lucene方式)
 * @version v1.0
 * @author LeoJapan
 * @date 2023年2月3日 
 */
@Service
@Slf4j
public class MovieLuceneServiceImpl implements MovieLuceneService{

	/**
	 * @Description: 电影信息检索索引创建
	 * @param oStMVJsonData 电影信息Json字符串
	 * @return void
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月3日 
	 **/
	public void createMovieIndex(String oStMVJsonData){
		
		// Json字符串转换为电影信息实体类
		List<MovieInfoEntity> mvList = createMVList(oStMVJsonData);
		
		// 创建文档的集合
        Collection<Document> docs = new ArrayList<>();
        
        for(MovieInfoEntity movieInfo : mvList) {
        	// 创建文档对象
        	Document doc = new Document();
        	
        	//StringField会创建索引，但是不会被分词，TextField，即创建索引又会被分词。
        	doc.add(new StringField("id", movieInfo.getId(), Field.Store.YES));
        	doc.add(new TextField("title", movieInfo.getTitle(), Field.Store.YES));
        	doc.add(new TextField("original_title", movieInfo.getOriginalTitle(), Field.Store.YES));
        	doc.add(new TextField("year", movieInfo.getYear(), Field.Store.YES));
        	doc.add(new TextField("role_desc", movieInfo.getRoleDesc(), Field.Store.YES));
        	
        	docs.add(doc);
        }
        
		try {
	        // 索引目录类,指定索引的位置
	        Directory directory = FSDirectory.open(Paths.get(MovieConstant.LUCENE_INDEX_PATH));
	        // 引入分词器
	        Analyzer analyzer = new StandardAnalyzer();
	        // 索引写出工具的配置对象
	        IndexWriterConfig config = new IndexWriterConfig(analyzer);
	        // 设置打开方式：OpenMode.APPEND 会在索引库的基础上追加新索引。
	        //OpenMode.CREATE会先清空原来数据，再提交新的索引
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			// 创建索引的写出工具类。参数：索引的目录和配置信息
			IndexWriter indexWriter = new IndexWriter(directory, config);
			// 把文档集合交给IndexWriter
			indexWriter.addDocuments(docs);	
	        indexWriter.commit();      
	        indexWriter.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * @Description: 以单个字段为条件，进行模糊查询
	 * @param fieldName 查询字段的名称
	 * @param keywords 查询字段文本内容
	 * @return mvList 电影信息查询结果
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月3日 
	 **/
	public List<MovieInfoEntity> searchMovieByFiled(String fieldName, String keywords) {
		
		//电影信息查询结果初始化
		List<MovieInfoEntity> mvList = new ArrayList<>();
		
		try {
			
			Directory directory = FSDirectory.open(Paths.get(MovieConstant.LUCENE_INDEX_PATH));
			
			// 索引读取工具
			IndexReader reader = DirectoryReader.open(directory);
			
			// 索引搜索工具
			IndexSearcher searcher = new IndexSearcher(reader);
			
			//标准分词器，会自动去掉空格啊，is a the等单词
	        Analyzer analyzer = new StandardAnalyzer();
	        
	        //查询解析器
	        QueryParser parser = new QueryParser(fieldName, analyzer);
	        
	        // 创建查询对象
	        Query query = parser.parse(keywords);
	        
	        //开始查询，查询前1000条数据，将记录保存在docs中
	        TopDocs topDocs = searcher.search(query, 1000);
	        
	        //高亮显示初始化
	        Highlighter highlighter = initHighlighter(query);
	        
	        // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        
	        //电影信息查询结果编辑
	        for (ScoreDoc scoreDoc : scoreDocs) {
            
	            // 根据编号去找文档
	            Document doc = searcher.doc(scoreDoc.doc);
	            MovieInfoEntity mvInfoEntity = new MovieInfoEntity();
	            mvInfoEntity.setId(doc.get("id"));
	            
	            //处理高亮字段显示         
	            //电影中文名Title
	            mvInfoEntity.setTitle(handleHighlighter(highlighter,
	            										analyzer,
	            										doc,
	            										MovieConstant.KEY_TITLE,
	            										fieldName));
	            
	            //电影英文名OriginalTitle
	            mvInfoEntity.setOriginalTitle(handleHighlighter(highlighter,
	            												analyzer,
	            												doc,
	            												MovieConstant.KEY_ORIG_TITLE,
	            												fieldName));
	            
	            //电影角色简介RoleDesc
	            mvInfoEntity.setRoleDesc(handleHighlighter(highlighter,
	            										   analyzer,
	            										   doc,
	            										   MovieConstant.KEY_ROLE_DESC,
	            										   fieldName));
	            mvList.add(mvInfoEntity);
	        }
        
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return mvList;
	}
	
	/**
	 * @Description: 关键字查询(全文查询) 
	 * @param keyWords 关键字
	 * @return mvList 电影信息查询结果
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月3日 
	 **/
	public List<MovieInfoEntity> searchMovieByFullField(String keyWords) {
		
		//电影信息查询结果初始化
		List<MovieInfoEntity> mvList = new ArrayList<>();
		
		try {
			
			Directory directory = FSDirectory.open(Paths.get(MovieConstant.LUCENE_INDEX_PATH));
			
			// 索引读取工具
			IndexReader reader = DirectoryReader.open(directory);
			
			// 索引搜索工具
			IndexSearcher searcher = new IndexSearcher(reader);
			
			//标准分词器，会自动去掉空格啊，is a the等单词
	        Analyzer analyzer = new StandardAnalyzer();
	        
	        // 创建多字段查询解析器
	        MultiFieldQueryParser parser = new MultiFieldQueryParser(MovieConstant.KEY_FULL_FIELD, analyzer);
	        
	        // 创建查询对象
	        Query query = parser.parse(keyWords);
	        
	        //开始查询，查询前1000条数据，将记录保存在docs中
	        TopDocs topDocs = searcher.search(query, 1000);
	        
	        log.info("本次关键字全文查询搜索共找到" + topDocs.totalHits + "条数据");
	        
	        //高亮显示
	        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=red>", "</font></b>");
	        QueryScorer scorer = new QueryScorer(query);
	        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
	        //根据这个得分计算出一个片段
	        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
	        highlighter.setTextFragmenter(fragmenter);
	        
	        // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
	        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	        
	        //电影信息查询结果编辑
	        for (ScoreDoc scoreDoc : scoreDocs) {
            
	            // 根据编号去找文档
	            Document doc = searcher.doc(scoreDoc.doc);
	            MovieInfoEntity mvInfoEntity = new MovieInfoEntity();
	            mvInfoEntity.setId(doc.get("id"));
	            
	            //处理高亮字段显示
	            //电影中文名Title
	            mvInfoEntity.setTitle(handleHighlighter(highlighter,
	            										analyzer,
	            										doc,
	            										MovieConstant.KEY_TITLE));
	            
	            //电影英文名OriginalTitle
	            mvInfoEntity.setOriginalTitle(handleHighlighter(highlighter,
	            												analyzer,
	            												doc,
	            												MovieConstant.KEY_ORIG_TITLE));
	            
	            //电影角色简介RoleDesc
	            mvInfoEntity.setRoleDesc(handleHighlighter(highlighter,
	            										   analyzer,
	            										   doc,
	            										   MovieConstant.KEY_ROLE_DESC));
	            mvList.add(mvInfoEntity);
	        }
        
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return mvList;
	}
	
	/**
	 * @Description: Json字符串转为电影信息实体类
	 * @param oStJsonData Json字符串
	 * @return　oAlMovieInfo 电影信息实体类集合
	 * @return List<MovieInfoEntity>
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年1月25日
	 **/
	private List<MovieInfoEntity> createMVList(String oStJsonData) {
		
		Gson gson = new GsonBuilder()
			    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			    .create();
		Type type = new TypeToken<List<MovieInfoEntity>>(){}.getType();
		
		List<MovieInfoEntity> oAlMovieInfo  = gson.fromJson(oStJsonData, type);
				
		return oAlMovieInfo;
	}
	
	/**
	 * @Description: 高亮显示初始化
	 * @param query 查询对象
	 * @return Highlighter 高亮显示
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月5日 
	 **/
	private Highlighter initHighlighter(Query query) {
		//高亮显示
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=red>", "</font></b>");
        
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
        
        //根据这个得分计算出一个片段
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        highlighter.setTextFragmenter(fragmenter);
        
        return highlighter;
	}
	
	/**
	 * @Description: 高亮处理
	 * @param highlighter 高亮处理对象
	 * @param analyzer 查询解析器
	 * @param doc 文档对象
	 * @param fieldName 字段名称
	 * @return 高亮处理后的字符串
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 * @return String
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月5日 
	 **/
	private String handleHighlighter(Highlighter highlighter,
									 Analyzer analyzer,
									 Document doc,
									 String fieldName) throws IOException, InvalidTokenOffsetsException {
		//字段的初始值(未高亮)
		String originalResult = doc.get(fieldName);
		
		//处理高亮字段显示
        String highlighterResult = highlighter.getBestFragment(analyzer,
        													   fieldName,
        													   originalResult);
        if(highlighterResult==null){
        	return originalResult;
        }
        
        return highlighterResult;
	}
	
	/**
	 * @Description: 高亮处理(单字段查询使用)
	 * @param highlighter 高亮处理对象
	 * @param analyzer 查询解析器
	 * @param doc 文档对象
	 * @param fieldName 字段名称
	 * @param targetFieldName 需要高亮处理的目标字段
	 * @return 高亮处理后的字符串
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 * @return String
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月5日 
	 **/
	private String handleHighlighter(Highlighter highlighter,
			 Analyzer analyzer,
			 Document doc,
			 String fieldName,
			 String targetFieldName) throws IOException, InvalidTokenOffsetsException {
		
		if(fieldName.equals(targetFieldName)) {
			return handleHighlighter(highlighter,analyzer,doc,fieldName);
		}
		//非目标高亮对象，返回原文
		return doc.get(fieldName);
	}
}
