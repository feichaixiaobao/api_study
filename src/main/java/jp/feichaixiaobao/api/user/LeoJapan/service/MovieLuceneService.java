package jp.feichaixiaobao.api.user.LeoJapan.service;

import java.util.List;

import jp.feichaixiaobao.api.user.LeoJapan.entity.MovieInfoEntity;

/**
 * @Description: 电影信息检查Service类(Lucene方式)
 * @version v1.0
 * @author LeoJapan
 * @date 2023年2月3日 
 */
public interface MovieLuceneService {

	/**
	 * @Description: 电影信息检索索引创建
	 * @param oStMVJsonData 电影信息Json字符串
	 * @return void
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月3日 
	 **/
	public void createMovieIndex(String oStMVJsonData);
	
	/**
	 * @Description: 以单个字段为条件，进行模糊查询
	 * @param fieldName 查询字段的名称
	 * @param keywords 查询字段文本内容
	 * @return mvList 电影信息查询结果
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月3日 
	 **/
	public List<MovieInfoEntity> searchMovieByFiled(String fieldName, String keywords);
	
	/**@Description: 以单个字段为条件，进行区间查询
	 * @param fieldName 查询字段的名称
	 * @param minVal 最小值
	 * @param maxVal 最大值
	 * @return 电影信息查询结果
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月7日 
	 **/
	public List<MovieInfoEntity> searchMovieByRange(String fieldName, String minVal, String maxVal);
	
	/**
	 * @Description: 关键字查询(全文查询) 
	 * @param keyWords 关键字
	 * @return mvList 电影信息查询结果
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月3日 
	 **/
	public List<MovieInfoEntity> searchMovieByFullField(String keyWords);
	
}
