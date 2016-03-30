package com.ai.baas.storm.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 业务异常类,封装业务编码
 * @author majun
 *
 */
public class BusinessException extends Exception {
	private static final long serialVersionUID = -7520148406412937919L;
	private String code; //错误编码
	
	public BusinessException(String code, String msg){
		super(msg);
		this.code = code;
	}
	
	public BusinessException(String code, String msg, Throwable cause){
		super(msg,cause);
		this.code = code;
	}
	
	public BusinessException(String code, Throwable cause){
		super(cause);
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getStrStackTrace(){
		StringWriter sw = new StringWriter();
		printStackTrace(new PrintWriter(sw, true));
		return sw.toString();
	}
	
}
