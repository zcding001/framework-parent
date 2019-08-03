package com.yirun.framework.core.utils;


import com.yirun.framework.core.annotation.Union;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description :
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.web.utils.BeanSpiltUtilsTest
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class BeanPropertiesUtilTest {


    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
//        Source source = new Source();
//        source.setName("aa");
//        source.setAge(123);
//
//        C1 c1 = new C1();
//        C2 c5 = new C2();
//
//        //进行属性的拆分
//
//        BeanPropertiesUtil.splitProperties(source, c1, c5);
//        System.out.println(c1);
//        System.out.println(c5);
//        System.out.println("-------------------");
//        //进行属性合并
//        source = new Source();
//        c1.setName("opopop");
//        BeanPropertiesUtil.mergeProperties(source, c1);
//        C3 c3=new C3();
//        c3.setName("pp");
//        //BeanPropertiesUtil.mergeProperties(source, c2);
//        BeanPropertiesUtil.mergeProperties(source, c3);
//        System.out.println(source);


//        Source2 source2 = new Source2();
//        source2.setName("KK");
//
//
//        List<?> limitConditions = BeanPropertiesUtil.getLimitConditions(source2, Target2.class, (target2Obj) -> {
//            System.out.println("---->" + target2Obj.getName());
//            return Arrays.asList(5, 6, 3);
//        });
//        System.out.println(limitConditions);

//        Source2 source2=new Source2();
//        source2.setName("wrewre");
//        source2.setAge(123);
//
//        Source2 source3=new Source2();
//        source3.setName("wrewre");
//        source3.setAge(123);
//
//        Source2 source4=new Source2();
//        source4.setName("wrewre");
//        source4.setAge(123);
//
//
//        System.out.println(AppResultUtil.mapOf(source2, 2001, "查询成功", null));
//        System.out.println(AppResultUtil.mapOf(source2, 2001, "查询成功", "name"));
//        source2.setName(null);
//        System.out.println(AppResultUtil.mapOfNullable(source2, 2001, "查询成功",false, null));
//
//        System.out.println(AppResultUtil.mapOfList(Arrays.asList(source2, source3, source4), 2001, null,null));
        int a=0;
        PropertyDescriptor a1 = BeanUtils.getPropertyDescriptor(SOU.class, "a");
        SOU sou = new SOU();
        System.out.println(BeanPropertiesUtil.getBeanMap(sou,(x,y)->true,true));
        System.out.println(AppResultUtil.successOf(sou));

    }

   static  class SOU{
        int a;
        Integer b;
        int[] c;
        Integer[] d;
        List e;

       private String k = "3243";

       public int getA() {
           return a;
       }

       public void setA(int a) {
           this.a = a;
       }

       public Integer getB() {
           return b;
       }

       public void setB(Integer b) {
           this.b = b;
       }

       public int[] getC() {
           return c;
       }

       public void setC(int[] c) {
           this.c = c;
       }

       public Integer[] getD() {
           return d;
       }

       public void setD(Integer[] d) {
           this.d = d;
       }

       public List getE() {
           return e;
       }

       public void setE(List e) {
           this.e = e;
       }

       public String getK() {
           return k;
       }

       public void setK(String k) {
           this.k = k;
       }
   }
    static class Source2{
        @Union(forLimitQuery = true)
        private String name;

        @Union(forLimitQuery = true)
        private Integer age;

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

        @Override
        public String toString() {
            return "Source2{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    static  class Target2{
        private String name;

        private Integer age;

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

        @Override
        public String toString() {
            return "Target2{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    static class Source {
        @Union(changeAbleIfHasValue = false)
        //@Union(bind = {"C1","C2"})
        //@Union(bind = {"C1", "C2"},mergeKey = "C2")
        private String name;

        @Union(reNameTo = "age2")
        private Integer age;

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

        @Override
        public String toString() {
            return "Source{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    static class C1 {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "C1{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    static class C3 {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    static class C2 {
        private String name;
        private Integer age2;

        public Integer getAge2() {
            return age2;
        }

        public void setAge2(Integer age2) {
            this.age2 = age2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "C2{" +
                    "name='" + name + '\'' +
                    ", age2=" + age2 +
                    '}';
        }
    }

}