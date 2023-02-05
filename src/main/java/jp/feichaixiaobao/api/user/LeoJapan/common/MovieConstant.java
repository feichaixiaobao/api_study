package jp.feichaixiaobao.api.user.LeoJapan.common;

/**
 * @Description: 定数(常量类)
 * @version v1.0
 * @author LeoJapan
 * @date 2023年2月3日 
 */
public class MovieConstant {
	
	/** JSON数据源名 */
	public static final String JSON_DATA_RESOUECE_NAME ="data.json";
	/** LUCENE索引保存目录 */
	public static final String LUCENE_INDEX_PATH ="lucene/indexDir/";
	
	/** 电影信息 标题字段(title) */
	public static final String KEY_TITLE ="title";
	/** 电影信息 标题字段(title) */
	public static final String KEY_ORIG_TITLE ="original_title";
	/** 电影信息 标题字段(title) */
	public static final String KEY_ROLE_DESC ="role_desc";
	/** 电影信息 全文字段(title,original_title,role_desc) */
	public static final String[] KEY_FULL_FIELD = {KEY_TITLE,KEY_ORIG_TITLE,KEY_ROLE_DESC};
	
}
