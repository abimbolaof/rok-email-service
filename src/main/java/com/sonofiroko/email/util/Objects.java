package com.sonofiroko.email.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created By: Olusegun Abimbola Oct 1, 2017
 **/
public class Objects {

    private static final Logger logger = LoggerFactory.getLogger(Objects.class);

    private final static int FIELD_PROBE_DEPTH = 10;
    private static final Map<Class, Map<String, Field>> fieldLookup = new HashMap<>();

//    //Warm-up the fieldLookup cache
//    static {
//        try {
//            getAllFields(new Media(), "com.rok");
//            getAllFields(new SeriesMedia(), "com.rok");
//            getAllFields(new MediaResponse(), "com.rok");
//
//            //logger.info("fieldLookup loaded: " + fieldLookup.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

	private Objects() {
	}

	public static void copyFields(Object source, Object dest, String... fields)
            throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		List<Field> allFields = null;

		if (fields == null || fields.length < 1) {
            allFields = getAllFields(source, "com.sonofiroko");//source.getClass().getDeclaredFields();
		}

		if (allFields == null) {
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i];
				Field field = getField(source, fieldName);
                if (!isNotNullorStatic(field))
                    continue;
				Object value = field.get(source);
				// Ignore if field value is null
				if (value == null)
					continue;

                Field destField = getField(dest, field.getName());
                if (destField != null){
                    destField.set(dest, value);
                }
			}
		} else {
			for (int i = 0; i < allFields.size(); i++) {
				Field field = allFields.get(i);
                if (!isNotNullorStatic(field))
                    continue;
                Object value = field.get(source);
                // Ignore if field value is null
                if (value == null)
                    continue;

                Field destField = getField(dest, field.getName());
                if (destField != null){
                    destField.set(dest, value);
                }
			}
		}
	}

	private static boolean isNotNullorStatic(Field field){
        if (field == null)
            return false;
        int modifiers = field.getModifiers();
        if ((modifiers & Modifier.STATIC) == Modifier.STATIC){
            return false;
        }
        return true;
    }

	private static Field getField(Object object, String name) {
		int s = 0;
		Field f = null;
		Class<?> clazz = object.getClass();

        Map<String, Field> cache = fieldLookup.get(clazz);
        if (cache != null) {
            f = cache.get(name);
            if (f != null) {
                //logger.info("ObjectUtils: Field Cache Hit: Field=" + name + ", Class=" + clazz.getName());
                return f;
            }
        }

        while (clazz != null) {
            try {
                f = clazz.getDeclaredField(name);
            } catch (Exception e) {
            }
            if (f == null)
                clazz = clazz.getSuperclass();
            else {
                //Add to cache...and return field
                Map<String, Field> c = fieldLookup.get(clazz);
                if (c == null) {
                    c = new HashMap<>();
                }
                f.setAccessible(true);
                c.put(name, f);
                fieldLookup.put(clazz, c);
                return f;
            }
        }
		return null;
	}

    /**
     * @param object
     * @param packagePrefix
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static List<Field> getAllFields(Object object, String packagePrefix) throws InvocationTargetException, IllegalAccessException {

        Class<?> clazz = object.getClass();
        Map<String, Field> result = fieldLookup.get(clazz);
        if (result != null){
            return new ArrayList<Field>(result.values());
        }else {
            result = new HashMap<>();
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods){
            if (method.getDeclaringClass().getName().startsWith(packagePrefix)){// && !method.getName().equals("getClass")
                if (method.getName().startsWith("get") || method.getName().startsWith("is")){
                    String name = fieldNameFromGetter(method.getName());
                    Field field = getField(object, name);
                    if (field != null){
                        result.put(name, field);
                    }
                }
            }
        }
        if (result.size() > 0) {
            fieldLookup.put(clazz, result);
        }
        return new ArrayList<Field>(result.values());
    }

    /**
     * Consider checking declaringClassname of method that it matches a supplied regex or class/package name,
     * this helps to eliminate methods like getClass() in the field resolution method (fieldNameFromGetter)
     */
    private static String fieldNameFromGetter(String methodName){
        String start = methodName.startsWith("get") ? "get" : "is";
        String result = methodName.substring(start.length());
        return String.valueOf(result.charAt(0)).toLowerCase() + result.substring(1);
    }

	public static String[] combineArrays(String[] array1, String[] array2) {

		String[] result = new String[array1.length + array2.length];
		for (int i = 0; i < array1.length; i++) {
			result[i] = array1[i];
		}
		for (int i = 0; i < array2.length; i++) {
			result[array1.length + i] = array2[i];
		}
		return result;
	}

	public static String toJSON(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }

    public static  <T> T fromJSON(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

	@SuppressWarnings("unchecked")
	public static <T> T clone(T source) throws IOException {
		if (source == null)
			return null;
		ObjectMapper mapper = new ObjectMapper();
		return (T) mapper.readValue(mapper.writeValueAsString(source), source.getClass());
	}

	
	public static <T> List<T> cloneList(List source, Class<T> classOnWhichArrayIsDefined)
			throws IOException, ClassNotFoundException {
		if (source == null)
			return null;
		T[] objects = cloneCollectionToArray(source, classOnWhichArrayIsDefined);
		return Arrays.asList(objects);
	}

    public static <T> Iterable<T> cloneIterable(Iterable source, Class<T> classOnWhichArrayIsDefined)
            throws IOException, ClassNotFoundException {
        if (source == null)
            return null;
        T[] objects = cloneCollectionToArray(source, classOnWhichArrayIsDefined);
        return Arrays.asList(objects);
    }
	
	public static <T> Set<T> cloneSet(Set source, Class<T> classOnWhichArrayIsDefined)
			throws IOException, ClassNotFoundException {
		if (source == null)
			return null;
		T[] objects = cloneCollectionToArray(source, classOnWhichArrayIsDefined);
		return Arrays.stream(objects).collect(Collectors.toSet());
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] cloneCollectionToArray(Object source, Class<T> classOnWhichArrayIsDefined) throws ClassNotFoundException, JsonParseException, JsonMappingException, JsonProcessingException, IOException{
		if (source == null)
			return null;
		ObjectMapper mapper = new ObjectMapper();
		Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + classOnWhichArrayIsDefined.getName() + ";");
		return mapper.readValue(mapper.writeValueAsString(source), arrayClass);
	}
	
	public static boolean equals(Object x, Object y){
		return x == y || ( x != null && y != null && x.equals( y ) );
	}
}
