package org.openmrs.mobile.utilities;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import org.openmrs.mobile.models.retrofit.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

public class ResourceSerializer implements JsonSerializer<Resource> {
    @Override
    public JsonElement serialize(Resource src, Type typeOfSrc, JsonSerializationContext context) {
        Gson myGson = getGson();
        JsonObject srcJson = new JsonObject();
        Field[] declaredFields = src.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getAnnotation(Expose.class) != null) {
                field.setAccessible(true);
                if (Resource.class.isAssignableFrom(field.getType())) {
                    try {
                        srcJson.add(field.getName(), serializeField((Resource) field.get(src), context));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    try {
                        Collection collection = ((Collection) field.get(src));
                        if (collection != null && !collection.isEmpty()) {
                            if (isResourceCollection(collection)) {
                                JsonArray jsonArray = new JsonArray();
                                for (Object resource : collection) {
                                    jsonArray.add(serializeField((Resource) resource, context));
                                }
                                srcJson.add(field.getName(), jsonArray);
                            } else {
                                JsonArray jsonArray = new JsonArray();
                                for (Object object : collection) {
                                    jsonArray.add(myGson.toJsonTree(object));
                                }
                                srcJson.add(field.getName(), jsonArray);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        srcJson.add(field.getName(), myGson.toJsonTree(field.get(src)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return srcJson;
    }

    @NonNull
    private Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    private JsonElement serializeField(Resource src, JsonSerializationContext context) {
        if (src.getUuid() != null) {
            return new JsonPrimitive(src.getUuid());
        } else {
            return context.serialize(src);
        }
    }


    private boolean isResourceCollection(Collection collection) {
        return Resource.class.isAssignableFrom(collection.toArray()[0].getClass());
    }
}