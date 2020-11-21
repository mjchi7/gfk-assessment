package posmy.interview.boot.converter;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An AttributeConverter that converts a List of String into Comma Delimitered String,
 * and vice-versa
 */
public class ListToStringConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        if (strings.size() < 1) {
            // must return null to prevent it from returning an empty string.
            // which will cause exception during libraryUser#getAuthority, because
            // new SimpleGrantedAuthority("") will be an error.
            return null;
        }
        return String.join(",", strings);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        if (s == null || s.isBlank()) {
            return Collections.emptyList();
        }
        String[] values = s.split(",");
        return Arrays.asList(values);
    }
}
