package com.yirun.framework.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description   : javaBean 和  map 之间的相互转换
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.JavaMapUtils.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class JavaMapUtils {

	    public static Object mapToObject(Map<String, String> map, Class<?> beanClass) throws Exception {    
	        if (map == null)  
	            return null;    
	  
	        Object obj = beanClass.newInstance();  
	  
	        Field[] fields = obj.getClass().getDeclaredFields();   
	        for (Field field : fields) {    
	            int mod = field.getModifiers();    
	            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){    
	                continue;    
	            }    
	            field.setAccessible(true);    
	            //判读当前对象数据类型
	            String type = field.getType().getName();
	            Object value = stringToObject(type, map.get(field.getName()));
	            field.set(obj, value);   
	        }   
	  
	        return obj;    
	    }    
	  
	    public static Map<String, String> objectToMap(Object obj) throws Exception {    
	        if(obj == null){    
	            return null;    
	        }   
	  
	        Map<String, String> map = new HashMap<String, String>();    

	        Field[] declaredFields = obj.getClass().getDeclaredFields();    
	        for (Field field : declaredFields) {    
	            field.setAccessible(true);  
	            String type = field.getType().getName();
	            String s = objectToString(type,field.get(obj));
	            map.put(field.getName(), s);  
	        }    
	  
	        return map;  
	    }   
	    /**
	      *  @Description    : 将String类型转化为某个属性的对应数据类型
	      *  @Method_Name    : objectTrans
	      *  @param @param type 预期结果数据类型
	      *  @param @param value 参数值
	      *  @return         : Object   
	      *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	      *  @date			  ：2017年3月20日
	     */
	    private static Object stringToObject(String type, String value){   
	    	   if (value == null||"null".equalsIgnoreCase(value)) 
	    	        return null;   
	    	    try{   
	    	       Object[] oo = new Object[1];   
	    	       if ("java.lang.String".equals(type)){   
	    	             oo[0] = value;   
	    	       }else if ("java.lang.Integer".equals(type)||"int".equals(type)){   
	    	         if (value.length() > 0) oo[0] = Integer.valueOf(value);   
	    	       }else if ("java.lang.Float".equals(type)||"float".equals(type)){   
	    	          if (value.length() > 0) oo[0] = Float.valueOf(value);   
	    	       }else if ("java.lang.Double".equals(type)||"double".equals(type)){   
	    	          if (value.length() > 0) oo[0] = Double.valueOf(value);   
	    	       }else if ("java.math.BigDecimal".equals(type)){   
	    	          if (value.length() > 0) oo[0] = new BigDecimal(value);   
	    	       }else if("java.util.Date".equals(type)){   
	    	          if (value.length() > 0) oo[0] = DateUtils.parse(value,DateUtils.DATE_HH_MM_SS);
	    	      }else if("java.sql.Timestamp".equals(type)){   
	    	          if (value.length() > 0) oo[0] = DateUtils.parse(value,DateUtils.DATE_HH_MM_SS);
	    	      }else if ("java.lang.Boolean".equals(type)||"boolean".equals(type)){   
	    	         if (value.length() > 0) oo[0] = Boolean.valueOf(value);   
	    	      }else if("java.lang.Long".equals(type)||"long".equals(type)){   
	    	         if(value.length() > 0) oo[0] = Long.valueOf(value);   
	    	      }   
	    	       return oo[0];
    	       }catch(Exception e){   
    	            e.printStackTrace();   
    	       }   
	    	   return null;
	 }   
	    
	    /**
	      *  @Description    : 将object类型转化为string类型进行存储（主要针对日期类型进行转换）
	      *  @Method_Name    : objectToString
	      *  @param @param type
	      *  @param @param value
	      *  @param @return
	      *  @return         : String   
	      *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	      *  @date			  ：2017年3月20日
	     */
	    private static String objectToString(String type, Object value){   
	    	if (value == null){
	    		return null;   
	    	} 
	    	String result = null;
		    try{   
			    if("java.util.Date".equals(type)){   
			    	result = DateUtils.format((Date)value,DateUtils.DATE_HH_MM_SS);
			    }else if("java.sql.Timestamp".equals(type)){   
			    	result = DateUtils.format((Date)value,DateUtils.DATE_HH_MM_SS);
			    }else{
			    	result = String.valueOf(value);
			    }
	       }catch(Exception e){   
	            e.printStackTrace();   
	       }   
		   return result;
	 }   
}
