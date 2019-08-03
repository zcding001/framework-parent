package com.yirun.framework.core.utils;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yirun.framework.core.annotation.Union;
import com.yirun.framework.core.model.StateList;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.*;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description : Bean分割器或者合并器，可以将一个bean中的属性分割到多个bean之中
 * 或者将多个bean的值合并到一个值之中
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.web.utils.BeanSpiltUtils
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public abstract class BeanPropertiesUtil extends BeanUtils {
    /**
     * 拆分对应的属性到不同的类中,前提条件已经有初始化对象
     *
     * @param source
     * @param targets
     */
    public static void splitProperties(Object source, Object... targets) {
        Assert.notNull(source, "源对象不能为null！");
        Assert.notNull(targets, "目标对象不能为null！");
        Assert.notEmpty(targets, "处理拆分，必须至少含有一个目标！");
        extensibleCopyProperties(source, transformObjectToClass(targets), true, null);
    }


    /**
     * 合并bean的属性
     *
     * @param target
     * @param sources
     */
    public static void mergeProperties(Object target, Object... sources) {
        Assert.notNull(target, "目标对象不能为null！");
        Assert.notNull(sources, "源标对象不能为null！");
        Assert.notEmpty(sources, "处理聚合，必须至少含有一个源！");
        extensibleCopyProperties(target, transformObjectToClass(sources), false, null);

    }

    /**
     * 合并bean的属性，并且目标对象
     *
     * @param target
     * @param sources
     */
    public static <T> T mergeAndReturn(T target, Object... sources) {
        Assert.notNull(target, "目标对象不能为null！");
        Assert.notNull(sources, "源标对象不能为null！");
        Assert.notEmpty(sources, "处理聚合，必须至少含有一个源！");
        extensibleCopyProperties(target, transformObjectToClass(sources), false, null);
        return target;
    }

    /**
     * @param obj
     * @param params : params中找到的属性全部设置为null
     * @return : void
     * @Description : 将对象中的属性设置为null
     * @Method_Name : setNull
     * @Creation Date  : 2018年1月2日 上午11:20:22
     * @Author : zhichaoding@hongkun.com zc.ding
     */
    public static void setNull(Object obj, String... params) {
        doSetNull(obj, true, params);
    }

    /**
     * @param obj
     * @param params : 除了params中的属性其余属性全部设置为null
     * @return : void
     * @Description : 将对象中的属性设置为null
     * @Method_Name : setNullExtend
     * @Creation Date  : 2018年1月2日 上午11:20:52
     * @Author : zhichaoding@hongkun.com zc.ding
     */
    public static void setNullExtend(Object obj, String... params) {
        doSetNull(obj, false, params);
    }


    /**
     * 处理聚合查询中限制查询条件
     *
     * @param sourceObj            取值源对象
     * @param targetQueryClass     需要生成的查询目标对象
     * @param queryServiceFunction 查询函数
     * @param <T>
     * @return
     */
    public static <T, K> StateList getLimitConditions(T sourceObj, Class<K> targetQueryClass, Function<K, List<?>> queryServiceFunction) {
        Assert.notNull(sourceObj, "源对象不能为null！");
        //扫描源对象的内容获取限制字段
        Class<?> actualEditable = sourceObj.getClass();
        Map<String, Object> unAssginValues = new ConcurrentHashMap<>(16);
        ergodicProperties(sourceObj,
                (singProperty) -> {
                    try {
                        PropertyDescriptor property = (PropertyDescriptor) singProperty;
                        Union union = actualEditable.getDeclaredField(property.getName()).getAnnotation(Union.class);
                        Object pValue;
                        if (union.forLimitQuery() && (pValue = extractValueFormReadMethod(sourceObj, property.getReadMethod())) != null
                                && org.apache.commons.lang.StringUtils.isNotEmpty(String.valueOf(pValue))) {
                            //如果是为了去其他服务限制查询的属性
                            //获取属性值,如果有值，记录该值
                            unAssginValues.put(property.getName(), pValue);
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                });

        if (CollectionUtils.isEmpty(unAssginValues)) {
            return new StateList().proceed();
        }
        K targetQueryObj;
        //生成目标查询对象
        targetQueryObj = ClassUtilsExtend.newInstance(targetQueryClass);
        //复制有值的属性到目标查询对象
        Set<String> unAssginKeys = unAssginValues.keySet();
        ergodicProperties(targetQueryObj, (singPro) -> {
            PropertyDescriptor sp = (PropertyDescriptor) singPro;
            String pdName = sp.getName();
            if (unAssginKeys.contains(pdName)) {
                Method writeMethod = sp.getWriteMethod();
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                try {
                    writeMethod.invoke(targetQueryObj, new Object[]{unAssginValues.get(pdName)});
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });


        //执行查询
        return new StateList(queryServiceFunction.apply(targetQueryObj)).haltProceed();

    }


    /**
     * 把一个对象转变为Map
     *
     * @param source           源对象
     * @param ignoreProperties 忽略对象中的属性
     * @return
     */
    public static Map<String, Object> transObj2Map(Object source, Integer resStatus, Object regMsg, String... ignoreProperties) {
        Assert.notNull(source, "源对象不能为null！");
        Class actualEditable = source.getClass();
        //如果有指定了忽略属性，把这些属性置成null
        if (!ArrayUtils.isEmpty(ignoreProperties)) {
            setNull(source, ignoreProperties);
        }
        PropertyDescriptor[] sourcePds = getPropertyDescriptors(actualEditable);
        //抽取值
        Map<String, Object> resultMap = new HashMap<>(16);
        //状态码和信息
        resultMap.put("regMsg", regMsg);
        resultMap.put("resStatus", resStatus);
        Arrays.stream(sourcePds).forEach(propertyDescriptor -> {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = extractValueFormReadMethodExceptionHasCatch(source, propertyDescriptor.getReadMethod());
            if (!"class".equals(propertyName) && propertyValue != null) {
                resultMap.put(propertyName, propertyValue);
            }

        });
        return resultMap;
    }


    /**
     * 处理源对象和目标对象
     *
     * @param source
     * @param mappedTargets
     * @param ignoreProperties
     */
    private static void extensibleCopyProperties(Object source,
                                                 Map<String, ClassWithSourcePair> mappedTargets,
                                                 boolean isSpiltProcess/*是否是分散过程*/,
                                                 String... ignoreProperties) {

        List ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
        ergodicProperties(source,
                (singProperty) -> processUnioAnotation(source.getClass(), source, ignoreList, (PropertyDescriptor) singProperty, mappedTargets, isSpiltProcess));

    }


    /**
     * 遍历各个属性，然后做指定的操作
     *
     * @param source
     * @param processLogic
     */
    protected static void ergodicProperties(Object source, Consumer processLogic) {
        Class actualEditable = source.getClass();
        PropertyDescriptor[] sourcePds = getPropertyDescriptors(actualEditable);

        //遍历源对象的各个属性
        Arrays.stream(sourcePds).forEach((sourcePd) -> processLogic.accept(sourcePd));
    }


    /**
     * 处理属性分散过程
     *
     * @param actualEditable
     * @param source
     * @param sourcePd
     * @param mappedTargets  @return
     * @param isSpiltProcess
     */
    private static void processUnioAnotation(Class actualEditable,
                                             Object source,
                                             List igls,
                                             PropertyDescriptor sourcePd,
                                             Map<String, ClassWithSourcePair> mappedTargets,
                                             boolean isSpiltProcess) {
        /**
         * step 1:获取属性上的注解
         */
        Union union = null;
        try {
            union = actualEditable.getDeclaredField(sourcePd.getName()).getAnnotation(Union.class);

            if (isSpiltProcess) {
                //处理分散属性
                processSpilt(source, igls, sourcePd, mappedTargets, union);
                return;
            }
            //处理聚合过程
            processUnio(source, igls, sourcePd, mappedTargets, union);

        } catch (NoSuchFieldException e) {
            //do nothing
        }


    }

    /**
     * 处理聚合过程
     *
     * @param targetObj
     * @param igls
     * @param targetProperty
     * @param mappedTargets
     * @param union
     */
    private static void processUnio(Object targetObj, List igls, PropertyDescriptor targetProperty, Map<String, ClassWithSourcePair> mappedTargets, Union union) {

        Class findKey = null;
        //属性名称
        String finalPropertyKey = targetProperty.getName();

        Function<Object, Class> assignFindKey = (e) -> {
            ClassWithSourcePair classWithSourcePair = mappedTargets.get(e);
            return classWithSourcePair != null ? classWithSourcePair.getTargetClass() : null;
        };
        /**
         * step 1:指定了Union的情况
         */
        //指示是否有限定寻找key的范围
        boolean limitSearchKeys = false;

        if (union != null) {
            //判断该属性是否能够被改变
            if (!union.changeAble()) {
                return;
            }
            //判断有值时候是否能被改变
            try {
                //判断对象此时是否有值
                Object propertyVlue = extractValueFormReadMethod(targetObj, targetProperty.getReadMethod());
                //如果有值并且不能改变，掐断方法
                if (propertyVlue != null && !union.changeAbleIfHasValue()) {
                    return;
                }
            } catch (Exception e) {
                //do nothing...
            }

            //如果需要更名，那么进行属性更名
            if (StringUtils.hasLength(StringUtils.trimWhitespace(union.reNameTo()))) {
                finalPropertyKey = StringUtils.trimWhitespace(union.reNameTo());
            }

            String mergeKey = null;
            if (StringUtils.hasLength(StringUtils.trimWhitespace(union.mergeKey()))) {
                limitSearchKeys = true;
                mergeKey = StringUtils.trimWhitespace(union.mergeKey());
                findKey = assignFindKey.apply(mergeKey);
            }
            //如果没有指定mergekey,继续寻找bind数组
            if (findKey == null && !ArrayUtils.isEmpty(union.bind())) {
                limitSearchKeys = true;
                int current = 0;
                int bindlenth = union.bind().length;
                String[] binds = StringUtils.trimArrayElements(union.bind());
                while (findKey == null && current < bindlenth && !org.apache.commons.lang.StringUtils.equals(binds[current], mergeKey)/*剔除已经处理过的mergeKey*/) {
                    findKey = assignFindKey.apply(binds[current++]);
                }
            }

            if (findKey != null) {
                //取出目标对象
                Object sourceValueObject = mappedTargets.get(findKey.getSimpleName()).getTarget();/*这里mappedTargets.get(findKey.getSimpleName())不可能为空*/
                doAssignPd(sourceValueObject, targetObj, igls, finalPropertyKey, targetProperty.getWriteMethod());
            }
            //如果指定了，但是找完mergeKey和Bind数组之后依然还是没有找到，这种情况不能继续往下走了
            if (limitSearchKeys) {
                return;
            }
            //没指定，继续往下找

        }

        /**
         * step 2:未指定注解的情况
         */

        //没找到取值目标，两种情况，一种是指定了，但是没有传入这种类型的,那么上面已经截断了，还有一种是没指定，只对没指定的进行覆盖式的搜寻
        String finalUseKey = finalPropertyKey;
        mappedTargets.forEach((String key, ClassWithSourcePair vaule) -> doAssignPd(vaule.getTarget(), targetObj, igls, finalUseKey, targetProperty.getWriteMethod()));
    }


    /**
     * 处理属性分散过程
     *
     * @param source
     * @param igls
     * @param sourcePd
     * @param mappedTargets
     * @param union
     */
    private static void processSpilt(Object source, List igls, PropertyDescriptor sourcePd, Map<String, ClassWithSourcePair> mappedTargets, Union union) {
        Set<MethodAndSourcePair> MethodAndSourcePairs;
        //取到Class数组
        Function<Map<String, ClassWithSourcePair>, Object[]> finClassArr = (mpts) ->
                mpts.values().stream().map(ClassWithSourcePair::getTargetClass).filter((e) -> union == null ? true : ArrayUtils.contains(StringUtils.trimArrayElements(union.bind()), e.getSimpleName())).collect(Collectors.toList()).toArray();
        MethodAndSourcePairs = findTargetWriteMethods(sourcePd, mappedTargets, (union == null) ? null : finClassArr.apply(mappedTargets), (union == null) ? null : StringUtils.trimWhitespace(union.reNameTo()), union == null ? false : union.bind().length > 0);
        //如果等于null说明没有相关的写方法或者指定的赋值类不是此类
        if (!CollectionUtils.isEmpty(MethodAndSourcePairs)) {
            //执行属性填充复制操作
            MethodAndSourcePairs.stream()
                                .forEach((methodPair) -> doAssignPd(
                                        source,
                                        mappedTargets.get(methodPair.getWriteMethod().getDeclaringClass().getSimpleName()).getTarget()/*待赋值对象*/,
                                        Arrays.asList(igls),
                                        methodPair.getSourceName(),
                                        methodPair.getWriteMethod()/*目标写方法*/));
        }
    }


    /**
     * 找到写方法
     *
     * @param sourcePd
     * @param targetMap
     * @param target
     * @param newPName
     * @return
     */
    private static Set<MethodAndSourcePair> findTargetWriteMethods(PropertyDescriptor sourcePd, Map<String, ClassWithSourcePair> targetMap, Object[] target, String newPName, boolean hasAssignBind) {
        Set<Class> targetClasses = targetMap.values().stream().map(ClassWithSourcePair::getTargetClass).collect(Collectors.toSet());
        return targetClasses.stream().map((e) -> {
            if ((!hasAssignBind/*没有指定类型*/) || ArrayUtils.contains(target, e)/*或者指定了类型也包含本类*/) {
                PropertyDescriptor tmp = getPropertyDescriptor(e, (StringUtils.hasText(newPName)) ? newPName : sourcePd.getName()  /*判断是否指定名称*/);
                if (tmp != null) {
                    return new MethodAndSourcePair(tmp.getWriteMethod(), sourcePd.getName());
                }
            }
            return (MethodAndSourcePair) null;
        }).filter((k) -> k != null).collect(Collectors.toSet());
    }


    /**
     * 执行赋值操作
     *
     * @param source
     * @param target
     * @param ignoreList
     * @param sourcePdName
     * @param writeMethod
     */
    private static void doAssignPd(Object source, Object target, List ignoreList, String sourcePdName, Method writeMethod) {
        if (writeMethod != null && (ignoreList == null || !ignoreList.contains(sourcePdName))) {
            PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), sourcePdName);
            if (sourcePd != null) {
                Method sourcePdReadMethod = sourcePd.getReadMethod();
                if (sourcePdReadMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], sourcePdReadMethod.getReturnType())) {
                    try {
                        Object ex = extractValueFormReadMethod(source, sourcePdReadMethod);
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }
                        writeMethod.invoke(target, new Object[]{ex});
                    } catch (Throwable var15) {
                        throw new FatalBeanException("Could not copy property \'" + sourcePdName + "\' from source to bind", var15);
                    }
                }
            }
        }
    }

    /**
     * 从目标对象的读方法中提取值
     *
     * @param source
     * @param sourcePdReadMethod
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected static Object extractValueFormReadMethod(Object source, Method sourcePdReadMethod) throws IllegalAccessException, InvocationTargetException {
        if (!Modifier.isPublic(sourcePdReadMethod.getDeclaringClass().getModifiers())) {
            sourcePdReadMethod.setAccessible(true);
        }
        return sourcePdReadMethod.invoke(source, new Object[0]);
    }

    /**
     * 从目标对象的读方法中提取值，已经catch异常
     *
     * @param source
     * @param sourcePdReadMethod
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected static Object extractValueFormReadMethodExceptionHasCatch(Object source, Method sourcePdReadMethod) {
        if (!Modifier.isPublic(sourcePdReadMethod.getDeclaringClass().getModifiers())) {
            sourcePdReadMethod.setAccessible(true);
        }
        try {
            return sourcePdReadMethod.invoke(source, new Object[0]);
        } catch (IllegalAccessException e) {
            //do nothing...
        } catch (InvocationTargetException e) {
            //do nothing...
        }
        return null;
    }

    /**
     * 映射所有的对象到Class
     *
     * @param targets
     * @return
     */
    private static Map<String, ClassWithSourcePair> transformObjectToClass(Object[] targets) {
        Map<String, ClassWithSourcePair> collectedTargets = Arrays.stream(targets)
                                                                  .collect(Collectors.toMap((e) -> e.getClass().getSimpleName(), (e) -> new ClassWithSourcePair(e.getClass(), e)));
        if (collectedTargets.size() != targets.length) {
            throw new IllegalStateException("目标对象中存在重复对象实例！");
        }
        return collectedTargets;

    }

    /**
     * 获取一个BeanMap,并且决定要不要对属性设置初始值
     *
     * @param source
     * @param setDefaultValue
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> getBeanMap(T source, BiFunction<String, Object, Boolean> decideFunction, boolean setDefaultValue) {
        Map<String, Object> beanMap = new HashMap<>(16);
        ergodicProperties(source, (property) -> {
            PropertyDescriptor pd = (PropertyDescriptor) property;
            if (!"class".equals(pd.getName())) {
                try {
                    //抽取属性值
                    Object finalValue = extractValueFormReadMethod(source, pd.getReadMethod());
                    //判断决定函数
                    if (decideFunction.apply(pd.getName(), finalValue)) {
                        //抽取属性值
                        if (finalValue == null && setDefaultValue) {
                            //给该属性一个初始值，只处理原生类型和String类型
                            //判断是否是原生类型或者原生类型的包装类
                            Class<?> propertyType = pd.getPropertyType();
                            boolean primitiveArray = (ClassUtils.isPrimitiveArray(propertyType) || ClassUtils.isPrimitiveWrapperArray(propertyType));
                            if (ClassUtils.isPrimitiveOrWrapper(propertyType) || primitiveArray) {
                                //原生类型或者原生数组类型
                                finalValue = initPrimitiveTypeOrTypeArr(propertyType, primitiveArray);
                            } else if (String.class.equals(propertyType)) {
                                //String 类型
                                finalValue = "";
                            } else if (ClassUtils.isAssignable(List.class, propertyType)) {
                                //List 类型
                                finalValue = Collections.EMPTY_LIST;
                            } else if (ClassUtils.isAssignable(Set.class, propertyType)) {
                                //Set 类型
                                finalValue = Collections.EMPTY_SET;
                            } else if (ClassUtils.isAssignable(Map.class, propertyType)) {
                                //Map 类型
                                finalValue = Collections.EMPTY_MAP;
                            }

                        }
                        beanMap.put(pd.getName(), finalValue);
                    }
                } catch (Exception e) {
                    //do nothing...
                }
            }

        });
        return beanMap;
    }

    /**
     * 原生类型对应的默认值
     */
    private static final Map<Class<?>, Object> primitiveTypeDefalutValue = new IdentityHashMap<>(16);
    private static final Map<Class<?>, Object> primitiveTypeArrDefalutValue = new IdentityHashMap<>(16);

    static {
        //原生类型
        primitiveTypeDefalutValue.put(Boolean.class, false);
        primitiveTypeDefalutValue.put(Byte.class, 0);
        primitiveTypeDefalutValue.put(Character.class, 0);
        primitiveTypeDefalutValue.put(Double.class, 0);
        primitiveTypeDefalutValue.put(Float.class, 0);
        primitiveTypeDefalutValue.put(Integer.class, 0);
        primitiveTypeDefalutValue.put(Long.class, 0);
        primitiveTypeDefalutValue.put(Short.class, 0);

        primitiveTypeDefalutValue.put(boolean.class, false);
        primitiveTypeDefalutValue.put(byte.class, 0);
        primitiveTypeDefalutValue.put(char.class, 0);
        primitiveTypeDefalutValue.put(double.class, 0);
        primitiveTypeDefalutValue.put(float.class, 0);
        primitiveTypeDefalutValue.put(int.class, 0);
        primitiveTypeDefalutValue.put(long.class, 0);
        primitiveTypeDefalutValue.put(short.class, 0);

        //原生类型
        primitiveTypeArrDefalutValue.put(Boolean.class, new Boolean[0]);
        primitiveTypeArrDefalutValue.put(Byte.class, new Byte[0]);
        primitiveTypeArrDefalutValue.put(Character.class, new Character[0]);
        primitiveTypeArrDefalutValue.put(Double.class, new Double[0]);
        primitiveTypeArrDefalutValue.put(Float.class, new Float[0]);
        primitiveTypeArrDefalutValue.put(Integer.class, new Integer[0]);
        primitiveTypeArrDefalutValue.put(Long.class, new Long[0]);
        primitiveTypeArrDefalutValue.put(Short.class, new Short[0]);

        primitiveTypeArrDefalutValue.put(boolean.class, new boolean[0]);
        primitiveTypeArrDefalutValue.put(byte.class, new byte[0]);
        primitiveTypeArrDefalutValue.put(char.class, new char[0]);
        primitiveTypeArrDefalutValue.put(double.class, new double[0]);
        primitiveTypeArrDefalutValue.put(float.class, new float[0]);
        primitiveTypeArrDefalutValue.put(int.class, new int[0]);
        primitiveTypeArrDefalutValue.put(long.class, new long[0]);
        primitiveTypeArrDefalutValue.put(short.class, new short[0]);


    }


    /**
     * 初始化原生类型的值
     *
     * @param propertyType
     * @param isArray
     */
    protected static Object initPrimitiveTypeOrTypeArr(Class<?> propertyType, boolean isArray) {
        if (isArray) {
            propertyType = propertyType.getComponentType();
            return primitiveTypeArrDefalutValue.get(propertyType);
        }
        return primitiveTypeDefalutValue.get(propertyType);
    }


    /**
     * 对应方法和source名称的对
     */
    static class MethodAndSourcePair {
        private Method writeMethod;
        private String sourceName;

        public MethodAndSourcePair() {
            /**
             * empty
             */

        }

        public MethodAndSourcePair(Method writeMethod, String sourceName) {
            this.writeMethod = writeMethod;
            this.sourceName = sourceName;
        }

        public Method getWriteMethod() {
            return writeMethod;
        }

        public void setWriteMethod(Method writeMethod) {
            this.writeMethod = writeMethod;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }
    }

    /**
     * Class和目标对象的对
     */
    static class ClassWithSourcePair {
        private Class targetClass;
        private Object target;

        public ClassWithSourcePair(Class targetClass, Object target) {
            this.targetClass = targetClass;
            this.target = target;
        }

        public ClassWithSourcePair() {
        }

        public Class getTargetClass() {
            return targetClass;
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }
    }


    private static void doSetNull(Object obj, boolean flag, String... params) {
        if (params != null && params.length > 0 && obj != null) {
            ReflectionUtils.doWithFields(obj.getClass(), new FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    boolean find = Arrays.asList(params).stream().filter(o -> field.getName().equalsIgnoreCase(o)).findAny().isPresent();
                    if ((flag && find) || (!flag && !find) && !"serialVersionUID".equals(field.getName())) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        if ("int".equals(field.getType().getName()) ||
                                "long".equals(field.getType().getName()) ||
                                "float".equals(field.getType().getName()) ||
                                "double".equals(field.getType().getName())) {
                            field.set(obj, 0);
                        } else {
                            field.set(obj, null);
                        }
                        if (!accessible) {
                            field.setAccessible(false);
                        }
                    }
                }
            });
        }
    }
}
