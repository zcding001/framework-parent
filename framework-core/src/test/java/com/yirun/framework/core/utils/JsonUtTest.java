package com.yirun.framework.core.utils;



import com.yirun.framework.core.utils.json.JsonUtils;
import org.codehaus.jackson.type.TypeReference;

import java.lang.reflect.Type;
import java.util.List;

import static org.springframework.util.ReflectionUtils.findMethod;


public class JsonUtTest {

    public static void main(String[] args) {
        JsonUtTest jsonUtTest = new JsonUtTest();
        jsonUtTest.test2();
    }

    public List<A> testA(){
        return null;}


    public<T> void test2(){
        T t = JsonUtils.json2GenericObject("[{\"age\":12}]", new TypeReference<T>() {
            @Override
            public Type getType() {
                return findMethod(JsonUtTest.class, "testA").getGenericReturnType();
            }
        }, "yyyy-mm-dd");
        System.out.println(t);
    }

}

class A{
    String name;
    Integer age;

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
        return "A{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}