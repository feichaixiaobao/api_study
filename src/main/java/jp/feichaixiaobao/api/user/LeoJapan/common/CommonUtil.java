package jp.feichaixiaobao.api.user.LeoJapan.common;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Resources;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 共通工具类
 * @version v1.0
 * @author LeoJapan
 * @date 2023年1月25日 
 */
@Slf4j
public class CommonUtil {
	
	/**
	 * @Description: 空字符串初始化处理
	 * @param val 处理字符串
	 * @return String 空值orNull值时，返回默认初始值0
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月7日 
	 **/
	public static String initEmptyValue(String val) {
		return Strings.isNullOrEmpty(val) ? "0" : val;
	}
	
	/**
	 * @Description: 文件读入，转为JSON字符串
	 * @param oStFileName 文件名
	 * @return oStFileName JSON字符串
	 * @version v1.0
	 * @author LeoJapan
	 * @date 2023年2月3日 
	 **/
	public static String readJsonData(String oStFileName) {
		
		String result = null;
		
		try {
			URL url = Resources.getResource(oStFileName);			
			result = Resources.toString(url, Charsets.UTF_8);
			
			log.info("读入的Json字符串="+result);
		} catch (IOException e) {			
			log.error(e.getMessage());
		}
		
		return result;
	}
}
