package com.keluokeda.basecommomadapter;


import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AdapterFactory {
    private static final Map<String, Class<? extends BaseCommonAdapter>> sMap = new TreeMap<>();

    private AdapterFactory() {
        throw new RuntimeException(AdapterFactory.class.getSimpleName() + "can not instance");
    }

    @SuppressWarnings("unchecked")
    public static <T> BaseCommonAdapter<T> createAdapter(Class<T> itemClass, List<T> items) {
        BaseCommonAdapter<T> baseCommonAdapter = null;
        try {

            String fullName = itemClass.getName();
            Class clazz = sMap.get(fullName);
            if (clazz == null) {
                clazz = Class.forName(fullName + "_Adapter");
                sMap.put(fullName, clazz);
            }


            Constructor constructor = clazz.getConstructor(List.class);
            baseCommonAdapter = (BaseCommonAdapter<T>) constructor.newInstance(items);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return baseCommonAdapter;


    }
}
