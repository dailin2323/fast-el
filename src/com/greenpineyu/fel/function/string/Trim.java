package com.greenpineyu.fel.function.string;

import com.greenpineyu.fel.common.FunctionUtil;
import com.greenpineyu.fel.function.CommonFunction;

/**
 * 包名				com.datanew.excel.script.function.string
 * 类名				Trim.java
 * 创建日期				Oct 28, 20103:02:34 PM
 * 作者				张鑫
 * 版权				杭州数新软件
 */
public class Trim extends CommonFunction {
	
	private static final Trim trim;
	
	static {
		trim = new Trim();
	}
	
	private Trim(){}
	
	public static Trim getInstance(){
		return trim;
	}

	public Object call(Object[] arguments) {
		if(arguments != null && arguments.length == 1){
			String str = FunctionUtil.toString(arguments[0]);
			return str.trim();
		}
		throw new NullPointerException("传入参数数组为空或者参数个数不正确!");
	}

	public String getName() {
		return "trim";
	}

}