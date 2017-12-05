package com.innowave.mahaulb.formula.common;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {
	
	static ClassPathXmlApplicationContext ctx;
	
	static {
		if(ctx == null) {
			synchronized (SpringUtil.class) {
				ctx = new ClassPathXmlApplicationContext("configCTX.xml");
			}
			
		}
	}
	
	public static  <T> T getService(Class<T> type){
			
		T t = ctx.getBean(type);
		return t;
	}
	
	public static  <T> T getService(String name, Class<T> type){
		Object obj = ctx.getBean(name);
		T t = ctx.getBean(name, type);
		return t;
	}

}
