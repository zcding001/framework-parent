package com.yirun.framework.core.utils;

import com.yirun.framework.core.exception.ExcelException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * @Description   : excel导入导出工具类
 * @Project       : finance-roster-service
 * @Program Name  : com.yirun.finance.roster.service.impl.ExcelUtil.java
 * @Author        : zhichaoding@hongkun.com zc.ding
 */

public class ExcelUtil {

	private ExcelUtil(){}
	
	/**
	 *  @Description    : 解析excel中所有数据
	 *  @Method_Name    : getDataList
	 *  @param filePath 文件路径
	 *  @return         : List<List<String>>
	 *  @Creation Date  : 2017年10月18日 上午11:20:44 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static List<List<String>> getDataList(String filePath){
		List<List<String>> dataList = new ArrayList<>();
		try(
				Workbook wb = WorkbookFactory.create(new File(filePath));
				){
			int sheets = wb.getNumberOfSheets();
			//遍历所有sheet
			for(int i = 0; i < sheets; i++){
				Sheet sheet = wb.getSheetAt(i);
				int rows = sheet.getPhysicalNumberOfRows();
				int columnIndex = sheet.getRow(0).getLastCellNum();
				//遍历所有行(除第一行)
				for(int j = 1; j < rows; j++){
					List<String> list = new ArrayList<>();
					Row row = sheet.getRow(j);
					//遍历所有列
					for(int z = 0; z < columnIndex; z++){
						String msg = "";
						Cell cell = row.getCell(z);
						if(cell != null){
							switch (cell.getCellTypeEnum()) {
							case NUMERIC:
								if(HSSFDateUtil.isCellDateFormatted(cell)){
									if(cell.getCellStyle().getDataFormat() == 22){
										msg = new SimpleDateFormat(DateUtils.DATE_HH_MM_SS).format(cell.getDateCellValue());
									}else if(cell.getCellStyle().getDataFormat() == 14){
										msg = new SimpleDateFormat(DateUtils.DATE).format(cell.getDateCellValue());
									}
								}else{
									DecimalFormat df = new DecimalFormat("0");
									msg = df.format(cell.getNumericCellValue());
								}
								break;
							case STRING:
								msg = cell.getStringCellValue();
								break;
							case FORMULA:
								msg = cell.getCellFormula();
								break;
							case BOOLEAN:
								msg = String.valueOf(cell.getBooleanCellValue());
								break;
							default:
								break;
							}
						}
						list.add(msg.trim());
					}
					//每一行的数据都是空，标识此sheet已经遍历结束
					if(list.stream().filter(StringUtils::isNotBlank).count() <= 0){
					    break;
                    }
					dataList.add(list);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}

	/**
	 * 
	 *  @Description    : 获得excel模板
	 *  @Method_Name    : getExcel
	 *  @param list	列名集合，比填列以*开头
	 *  @param dataList	导出数据集合
	 *  @return         : Workbook
	 *  @Creation Date  : 2017年10月18日 上午11:29:46 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static Workbook getExcel(List<String> list, List<List<String>> dataList){
		Workbook workbook = new HSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		Sheet sheet = workbook.createSheet("template");
		int start = 0;
		if(CommonUtils.isEmpty(dataList)){
			Row descRow = sheet.createRow(start++);
			descRow.createCell(0).setCellValue(createHelper.createRichTextString("PS: [*]开头为必填项,上传时删除此行备注!"));
		}
		Row row = sheet.createRow(start);
		//存储header信息
		list.forEach(e -> row.createCell(list.indexOf(e)).setCellValue(createHelper.createRichTextString(e)));
		int index = sheet.getLastRowNum() + 1;
		if(CommonUtils.isNotEmpty(dataList)){
			dataList.forEach(l -> {
				Row r = sheet.createRow(dataList.indexOf(l) + index);
				//存储每行导出的数据
				l.forEach(e -> r.createCell(l.indexOf(e)).setCellValue(createHelper.createRichTextString(e)));
			});
		}
		return workbook;
	}
	
	public static void main(String[] args) throws Exception {
		String filePath = "C:/liuxuhui/test.xlsx";
//		String filePath = "C:/yrtz/git/git_framework/finance/hk-management-services/src/main/webapp/WEB-INF/upload/20171018190859.xlsx";
//		Workbook wb = ExcelUtil.getExcel(Arrays.asList("*姓名", "手机号"), null);
//		Workbook wb = ExcelUtil.getExcel(Arrays.asList("*姓名", "手机号"), Arrays.asList(Arrays.asList("zc.ding", "15701230895"), Arrays.asList("peng.wu", "15701230896")));
//		FileOutputStream fileOut = new FileOutputStream(filePath);
//		wb.write(fileOut);
//		fileOut.close();
//		wb.close();
//		List<List<String>> list = ExcelUtil.getDataList(filePath);
//		list.forEach(l -> l.forEach(e -> System.out.println(e)));
//		System.out.println("okok...");
//		FileOutputStream fileOut = new FileOutputStream(filePath);
		List<Person> dataList = new ArrayList<Person>();
		Person p1 = new Person("a","135555555",11);
		Person p2 = new Person("李四","136666666",12);
		Person p3 = new Person("王麻子","137777777",13);
		dataList.add(p1);
		dataList.add(p2);
		dataList.add(p3);
		LinkedHashMap<String,String> fieldMap = new LinkedHashMap<String,String>();
		fieldMap.put("name", "姓名");
		fieldMap.put("tel", "手机号");
		fieldMap.put("age", "年龄");
		exportExcel("exceltest1", "test", dataList, fieldMap, 100, new FileOutputStream(filePath));
	}
	static class Person{
		private String name;
		private String tel;
		private Integer age;
		public Person(String name,String tel,Integer age){
			this.name = name;
			this.tel = tel;
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
	}
	
	/**
	 * 
	 *  @Description    : 导出excel 到浏览器
	 *  @Method_Name    : exportExcel
	 *  @param fileName  :文件名
	 *  @param sheetName  
	 *  @param list      :数据源
	 *  @param fieldMap  :列名和数据源字段对应关系
	 *  @param sheetSize :sheet自定义大小
	 *  @param response  :
	 *  @throws ExcelException
	 *  @throws UnsupportedEncodingException
	 *  @return         : void
	 *  @Creation Date  : 2018年1月15日 下午2:26:37 
	 *  @Author         : xuhuiliu@honghun.com.cn 刘旭辉
	 */
	@SuppressWarnings("hiding")
	public static  <T>  void  exportExcel ( String fileName,String sheetName,List<T> list,LinkedHashMap<String,String> fieldMap,
	            int sheetSize,HttpServletResponse response)throws ExcelException, UnsupportedEncodingException{
	        //设置默认文件名为当前时间：年月日时分秒
		 	if(StringUtils.isBlank(fileName)){
				fileName = initDefaultTableName(1);
			}
	        //设置response头信息
	        response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
	        //创建工作簿并发送到浏览器
	        try {
	            OutputStream out=response.getOutputStream();
	            exportExcel(fileName, sheetName, list, fieldMap, sheetSize, out);
	        } catch (Exception e) {
	            e.printStackTrace();
	            if(e instanceof ExcelException){
	                throw (ExcelException)e;
	            }else{
	                throw new ExcelException("导出Excel失败");
	            }
	        }
	    }

	 
	/**
	 * 
	 *  @Description    : 导出excel 工具类
	 *  @Method_Name    : exportExcel
	 *  @param fileName  表名
	 *  @param sheetName  sheet名称
	 *  @param dataList   数据源
	 *  @param fieldMap   列名和数据源中字段对应关系
	 *  @param sheetSize  sheet页最大记录数
	 *  @return         : Workbook
	 *  @throws Exception 
	 *  @Creation Date  : 2018年1月12日 下午5:12:50 
	 *  @Author         : xuhuiliu@honghun.com.cn 刘旭辉
	 */
	public static <T> void exportExcel(String fileName,String sheetName,List<T> dataList,LinkedHashMap<String,String> fieldMap,
			int sheetSize,OutputStream out)throws ExcelException{
		if(CommonUtils.isEmpty(dataList)){
			throw new ExcelException("=====exportExcel dataList is empty");
		}
		if(StringUtils.isBlank(fileName)){
			fileName = initDefaultTableName(1);
		}
		if(StringUtils.isBlank(sheetName)){
			sheetName = initDefaultTableName(2);
		}
		if(sheetSize>65535 || sheetSize<1){
            sheetSize=65535;
        }
		Workbook workBook = new HSSFWorkbook();
		//sheet 分页
		int sheetNum = dataList.size()%sheetSize>0?dataList.size()/sheetSize + 1 :dataList.size()/sheetSize;
		for(int i=1;i<=sheetNum;i++){
			int start = sheetSize*(i-1)+1;
			int end = dataList.size()>= sheetSize*i?sheetSize*i:dataList.size();
			Sheet sheet = workBook.createSheet(sheetName);
			fillSheet(sheet, dataList, fieldMap, start, end);
		}
		try {
			workBook.write(out);
			workBook.close();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExcelException("=====exportExcel error");
		}
	}
   /**
    * 
    *  @Description    :  初始化table名称和sheet名称
    *  @Method_Name    : initDefaultTableName
    *  @param type     1：文件/表格名称      2：sheet名称
    *  @return
    *  @return         : String
    *  @Creation Date  : 2018年1月15日 下午2:25:33 
    *  @Author         : xuhuiliu@honghun.com.cn 刘旭辉
    */
   private static String initDefaultTableName(int type) {
	   if(type==1){
		   return DateUtils.format(new Date(), "yyyyMMddhhmmss");
	   }
	   if(type==2){
		   return "sheet";
	   }
	   return null;
	}
   /**
    * 
    *  @Description    : 根据字段名获取字段值
    *  @Method_Name    : getFieldValueByName
    *  @param fieldName 字段名
    *  @param o
    *  @return
    *  @throws ExcelException
    *  @return         : Object
    *  @Creation Date  : 2018年1月15日 下午2:25:16 
    *  @Author         : xuhuiliu@honghun.com.cn 刘旭辉
    */
	private static  Object getFieldValueByName(String fieldName, Object o) throws ExcelException{
	        Object value=null;
	        Field field=getFieldByName(fieldName, o.getClass());

	        if(field !=null){
	            field.setAccessible(true);
	            try {
					value=field.get(o);
				} catch (Exception e) {
					throw new ExcelException("解析字段异常：(fieldName:"+fieldName+",class:"+o.getClass().getSimpleName()+")");
				}
	        }else{
	            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
	        }

	        return value;
	    }

  	/**
  	 * 
  	 *  @Description    : 根据字段名获取字段
  	 *  @Method_Name    : getFieldByName
  	 *  @param fieldName  字段名
  	 *  @param clazz
  	 *  @return
  	 *  @return         : Field
  	 *  @Creation Date  : 2018年1月15日 下午2:25:08 
  	 *  @Author         : xuhuiliu@honghun.com.cn 刘旭辉
  	 */
    private static Field getFieldByName(String fieldName, Class<?>  clazz){
        //拿到本类的所有字段
        Field[] selfFields=clazz.getDeclaredFields();

        //如果本类中存在该字段，则返回
        for(Field field : selfFields){
            if(field.getName().equals(fieldName)){
                return field;
            }
        }

        //否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz=clazz.getSuperclass();
        if(superClazz!=null  &&  superClazz !=Object.class){
            return getFieldByName(fieldName, superClazz);
        }

        //如果本类和父类都没有，则返回空
        return null;
    }

   /**
    * 
    *  @Description    : excel导出sheet赋值
    *  @Method_Name    : fillSheet
    *  @param sheet    
    *  @param list        数据集合
    *  @param fieldMap    数据字段和表格中展示的标题名称对应关系
    *  @param firstIndex  开始下标（从list的第几条开始）
    *  @param lastIndex   结束下标（到list的第几条结束）
    *  @throws ExcelException
    *  @return         : void
    *  @Creation Date  : 2018年1月15日 下午2:24:41 
    *  @Author         : xuhuiliu@honghun.com.cn 刘旭辉
    */
    @SuppressWarnings("hiding")
	private static <T> void fillSheet(Sheet sheet,List<T> list,LinkedHashMap<String,String> fieldMap,
            int firstIndex,int lastIndex)throws ExcelException{

        //定义存放英文字段名和中文字段名的数组
        String[] enFields=new String[fieldMap.size()];
        String[] cnFields=new String[fieldMap.size()];

        //填充数组
        int count=0;
        for(Entry<String,String> entry:fieldMap.entrySet()){
            enFields[count]=entry.getKey();
            cnFields[count]=entry.getValue();
            count++;
        }
        //填充表头
        Row row = sheet.createRow(0);
        for(int i=0;i<cnFields.length;i++){
        	row.createCell(i).setCellValue(cnFields[i]);
        }

        //填充内容
        int rowNo=1;
        for(int index=firstIndex;index<=lastIndex;index++){
            //获取单个对象
            T item=list.get(index-1);
            Row r = sheet.createRow(rowNo);
            for(int i=0;i<enFields.length;i++){
                Object objValue= getFieldValueByName(enFields[i], item);
                String fieldValue=objValue==null ? "" : objValue.toString();
                r.createCell(i).setCellValue(fieldValue);
            }
            rowNo++;
        }
    }
}
