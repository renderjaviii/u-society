package usociety.manager.domain.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        return !CollectionUtils.isEmpty(list) ? String.join(",", list) : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        return !StringUtils.isEmpty(joined) ? new ArrayList<>(Arrays.asList(joined.split(","))) : null;
    }

}
