package info.smart_tools.smartactors.core.db_task.search_by_id.psql;

import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
public class QueryStatementBuilderTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(IOC.class);
        QueryStatement queryStatement = new QueryStatement();
        IKey queryStatementKey = mock(IKey.class);
        when(Keys.getOrAdd(QueryStatement.class.toString())).thenReturn(queryStatementKey);
        when(IOC.resolve(eq(queryStatementKey))).thenReturn(queryStatement);
    }

    @Test
    public void buildQueryStatementTest() throws Exception {
        String collection = "testCollection";
        String validationStr = "SELECT * FROM " + collection + " WHERE id=?;";

        QueryStatement queryStatement = QueryStatementBuilder
                .create()
                .withCollection(collection)
                .build();

        assertNotEquals(queryStatement, null);
        assertEquals(queryStatement.getBodyWriter().toString(), validationStr);

        Field templateSizeField = QueryStatementBuilder.class.getDeclaredField("TEMPLATE_SIZE");
        templateSizeField.setAccessible(true);
        int expectedTemplateSize = (int) templateSizeField.get(null) + collection.length();
        int actualTemplateSize = queryStatement.getBodyWriter().toString().length();

        assertEquals(expectedTemplateSize, actualTemplateSize);
    }

    @Test(expected = BuildingException.class)
    public void should_ThrowsException_WithReason_CollectionFieldNotSet() throws BuildingException {
        QueryStatementBuilder
                .create()
                .build();
    }
}
