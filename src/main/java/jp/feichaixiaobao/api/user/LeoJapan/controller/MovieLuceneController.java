package jp.feichaixiaobao.api.user.LeoJapan.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.feichaixiaobao.api.user.LeoJapan.common.CommonUtil;
import jp.feichaixiaobao.api.user.LeoJapan.common.MovieConstant;
import jp.feichaixiaobao.api.user.LeoJapan.entity.MovieInfoEntity;
import jp.feichaixiaobao.api.user.LeoJapan.service.MovieLuceneService;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class MovieLuceneController {
	
	@Autowired
	private MovieLuceneService movieLuceneService;
	
	@RequestMapping("/createMovieIndex")
	@ResponseBody
	public String createMovieIndex() {
		
		//从外部文件data.json读入电影信息Json字符串
		String oStJsonData = CommonUtil.readJsonData(MovieConstant.JSON_DATA_RESOUECE_NAME);
		//电影信息检索索引创建
		movieLuceneService.createMovieIndex(oStJsonData);
		
		return "success";
	}
	
	@RequestMapping("/searchMovieByTitle")
	public String searchMovieByTitle(String title, Map<String, Object> result) throws IOException, ParseException, InvalidTokenOffsetsException  {
		
		log.info("电影名（模糊查询）开始，标题="+title);
		
		//以电影名标题为条件，进行查询
		List<MovieInfoEntity> mvList = movieLuceneService.searchMovieByTitle(title);

        result.put("movieList", mvList);
        
        log.info("电影名（模糊查询）结束");
        
		return "movieSearchResult";
	}
	
}
