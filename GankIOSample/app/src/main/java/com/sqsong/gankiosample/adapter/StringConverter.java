package com.sqsong.gankiosample.adapter;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 青松 on 2016/11/25.
 */

public class StringConverter implements PropertyConverter<List<String>, String> {

    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) return null;
        List<String> entityProperty = Arrays.asList(databaseValue.split(","));
        return entityProperty;
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null) return null;

        StringBuilder sb= new StringBuilder();
        for(String property:entityProperty){
            sb.append(property);
            sb.append(",");
        }
        return sb.toString();
    }

}
