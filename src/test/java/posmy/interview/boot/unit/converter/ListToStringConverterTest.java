package posmy.interview.boot.unit.converter;

import org.junit.jupiter.api.Test;
import posmy.interview.boot.converter.ListToStringConverter;
import posmy.interview.boot.unit.BaseTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListToStringConverterTest extends BaseTest {

    private ListToStringConverter converter = new ListToStringConverter();

    @Test
    public void convertToDatabaseColumn_hasSingleValue() {
        List<String> listOfStrings = Collections.singletonList("item");
        String dbColumnValue = converter.convertToDatabaseColumn(listOfStrings);
        assertEquals("item", dbColumnValue);
    }

    @Test
    public void convertToDatabaseColumn_hasMultipleValues() {
        List<String> listOfStrings = Arrays.asList("item1", "item2");
        String dbColumnValue = converter.convertToDatabaseColumn(listOfStrings);
        assertEquals("item1,item2", dbColumnValue);
    }

    @Test
    public void convertToDatabaseColumn_isNull() {
        List<String> listOfStrings = null;
        String dbColumnValue = converter.convertToDatabaseColumn(listOfStrings);
        assertEquals(null, dbColumnValue);
    }

    @Test
    public void convertToDatabaseColumn_isEmptyList() {
        List<String> listOfStrings = Collections.emptyList();
        String dbColumnValue = converter.convertToDatabaseColumn(listOfStrings);
        assertEquals(null, dbColumnValue);
    }

    @Test
    public void convertToEntityAttribute_isNull() {
        String s = null;
        List<String> entityValue = converter.convertToEntityAttribute(s);
        assertEquals(Collections.emptyList(), entityValue);
    }

    @Test
    public void convertToEntityAttribute_isEmpty() {
        String s = "";
        List<String> entityValue = converter.convertToEntityAttribute(s);
        assertEquals(Collections.emptyList(), entityValue);
    }

    @Test
    public void convertToEntityAttribute_isSingleValue() {
        String s = "item";
        List<String> entityValue = converter.convertToEntityAttribute(s);
        assertEquals(Arrays.asList("item"), entityValue);
    }

    @Test
    public void convertToEntityAttribute_isMultipleValues() {
        String s = "item1,item2,item3";
        List<String> entityValue = converter.convertToEntityAttribute(s);
        assertEquals(Arrays.asList("item1", "item2", "item3"), entityValue);
    }
}
