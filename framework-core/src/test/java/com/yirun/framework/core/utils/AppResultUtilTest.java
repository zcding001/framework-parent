package com.yirun.framework.core.utils;


import java.math.BigDecimal;
import java.util.*;

import static com.yirun.framework.core.utils.AppResultUtil.addTransfer;

public class AppResultUtilTest {
    public static void main(String[] args) {
//        Source source1 = new Source("AA",12);
//        Source source2 = new Source("BB",null);
//        Source source3= new Source(null,14);
//
//        //mapOf
//        System.out.println("--------------mapOf------------------");
//        System.out.println(AppResultUtil.mapOf(source1, 2001, "查询成功", null));
//        System.out.println(AppResultUtil.mapOf(source1, 2001, "查询成功", "name"));
//        System.out.println(AppResultUtil.mapOf(source1, 2001, "查询成功", "name", "age"));
//        System.out.println("-------------mapOfInProperties-------------------");
//        //mapOfInProperties
//        System.out.println(AppResultUtil.mapOfInProperties(source1, 2001, "查询成功", null));
//        System.out.println(AppResultUtil.mapOfInProperties(source1, 2001, "查询成功", "name"));
//        System.out.println(AppResultUtil.mapOfInProperties(source1, 2001, "查询成功", "name", "age"));
//
//        System.out.println("----------------mapOfNullable----------------");
//        //mapOfNullable
//        System.out.println(AppResultUtil.mapOfNullable(source2, 2001, "查询成功", false, null));
//        System.out.println(AppResultUtil.mapOfNullable(source2, 2001, "查询成功", false, "name"));
//        System.out.println(AppResultUtil.mapOfNullable(source2, 2001, "查询成功", false, "name", "age"));
//
//
//        System.out.println("-------------mapOfList-------------------");
//        List<Source> sources = Arrays.asList(source1, source2, source3);
//        //mapOfList
//        AppResultUtil.ExtendMap e = AppResultUtil.mapOfList(sources, 2001, "查询成功", null);
//        System.out.println(e);
//        System.out.println(AppResultUtil.mapOfList(sources, 2001, "查询成功",  "name"));
//        System.out.println(AppResultUtil.mapOfList(sources, 2001, "查询成功",  "name", "age"));
//
//        System.out.println("---------------mapOfListInProperties-----------------");
//        //mapOfListInProperties
//        System.out.println(AppResultUtil.mapOfListInProperties(sources, 2001, "查询成功",  null));
//        System.out.println(AppResultUtil.mapOfListInProperties(sources, 2001, "查询成功",  "name"));
//        System.out.println(AppResultUtil.mapOfListInProperties(sources, 2001, "查询成功",  "name", "age"));
//
//        System.out.println("---------------mapOfListNullable-----------------");
//        //mapOfListNullable
//        System.out.println(AppResultUtil.mapOfListNullable(sources, 2001, "查询成功",  false,null));
//        System.out.println(AppResultUtil.mapOfListNullable(sources, 2001, "查询成功",  false,"name"));
//        System.out.println(AppResultUtil.mapOfListNullable(sources, 2001, "查询成功",  false,"name", "age"));
//
//        System.out.println("---------------mapOfListInPropertiesNullable-----------------");
//        //mapOfListInPropertiesNullable
//        System.out.println(AppResultUtil.mapOfListInPropertiesNullable(sources, 2001, "查询成功",  false,null));
//        System.out.println(AppResultUtil.mapOfListInPropertiesNullable(sources, 2001, "查询成功",  false,"name"));
//        System.out.println(AppResultUtil.mapOfListInPropertiesNullable(sources, 2001, "查询成功",  false,"name", "age"));
//
//
//        System.out.println("--------------------------------");
//        //mapOfListInPropertiesNullable
//        System.out.println(AppResultUtil.successOfList(sources, null));
//        System.out.println(AppResultUtil.errorOf(source1,"操作有误"));
//        System.out.println(AppResultUtil.errorOfMsg("操作有误"));
//        System.out.println(AppResultUtil.SUCCESS_MAP);
//        System.out.println(AppResultUtil.ERROR_MAP);
//
//        System.out.println("--------------------------------");
//
//        AppResultUtil.ExtendMap extendMap = AppResultUtil.successOfList(sources).addResParameter("extendParamter", "extendValue")
//                                                         .addResParameter("extendParamter1", source2).removeResParameter("extendParamter");
//        System.out.println(extendMap);
//
//        System.out.println("--------------------------------");
//        Map map = new HashMap(){
//            {
//                put("aa", 343);
//                put("a2", 343);
//                put("a3a", 343);
//            }
//        };
//        System.out.println(AppResultUtil.successOf(map,"a2"));

        System.out.println("--------------------------------");
        //mapOfListInPropertiesNullable
//        System.out.println(AppResultUtil.successOfList(sources, null));
//        System.out.println(AppResultUtil.errorOf(source1,"操作有误"));
        System.out.println(AppResultUtil.errorOfMsg("操作有误"));
        System.out.println(AppResultUtil.SUCCESS_MAP);
        System.out.println(AppResultUtil.ERROR_MAP);

        System.out.println("--------------------------------");

//        AppResultUtil.ExtendMap extendMap = AppResultUtil.successOfList(sources).addResParameter("extendParamter", "extendValue")
//                                                         .addResParameter("extendParamter1", source2).removeResParameter("extendParamter");
//        System.out.println(extendMap);
        Map<String,Object> s = new HashMap();
        Source b1 = new Source();
        b1.setAge(1);
        b1.setName("1111");
        Source b2 = new Source();
        b2.setAge(2);
        b2.setName("2222");
        s.put("b1", b1);
        s.put("b2", b2);
       
        System.out.println("--------------------------------"+ AppResultUtil.mapOfObjectInPropertiesNullable(s, 1000, "成功", true, "name"));
        Map map = new HashMap(){
            {
                put("aa", 343);
                put("a2", 343);
                put("a3a", 343);
            }
        };
        System.out.println(AppResultUtil.successOf(map,"a2"));
        AppResultUtil.addTransfer("name", null, null, (value) -> value + "12345");
        AppResultUtil.ExtendMap penn = AppResultUtil.successOf(new Source("penn", 23, new BigDecimal(0.3)));
        System.out.println(penn);
        
        List<Map<String, Object>> list = Arrays.asList(new HashMap<String, Object>(){{put("aa", 1); put("bb", 2);}}, new HashMap<String, Object>(){{put("aa", 1); put("bb", 2);}}, new HashMap<String, Object>(){{put("aa", 1); put("bb", 2);}});
        System.out.println(AppResultUtil.mapOfListInProperties(list, 1, "","aa"));
        System.out.println(AppResultUtil.mapOfList(list, 1, "","aa"));
    }
    

 
    static class Source {
        private String name;

        private Integer age;

        private BigDecimal bigDecimal;


        private static Source source  = new Source("A-BB", 19);


        public Source(String name, Integer age, BigDecimal bigDecimal) {
            this.name = name;
            this.age = age;
            this.bigDecimal = bigDecimal;
        }

        public Source() {
        }

        public Source(String name, Integer age) {
            this.name = name;
            this.age = age;

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }


        public Source getSource() {
            return source;
        }

        public void setSource(Source source) {
            this.source = source;
        }

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }

        public void setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        @Override
        public String toString() {
            return "Source{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", bigDecimal=" + bigDecimal +
                    '}';
        }
    }



}