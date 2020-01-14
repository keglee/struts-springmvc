package com.iversonx.struts_springmvc.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/14 15:34
 */
public class StringToDateConverter implements Converter<String, Date> {
    private static final String[] parsePatterns = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm",
            "yyyy.MM"};

    @Override
    public Date convert(String value) {
        return doConvertToDate(value);
    }

    private Date doConvertToDate(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Date result = null;
        try {
            result = DateUtils.parseDate(value, parsePatterns);
            if (result == null && StringUtils.isNumeric(value)) {
                result = new Date(Long.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
