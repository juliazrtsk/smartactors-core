package info.smart_tools.smartactors.actors.validate_form_data;

import info.smart_tools.smartactors.actors.validate_form_data.exception.ValidateFormException;
import info.smart_tools.smartactors.actors.validate_form_data.wrapper.ValidateFormDataMessage;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.field.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IOC.class, Keys.class})
public class ValidateFormDataActorTest {
    ValidateFormDataActor actor;
    Field rulesF = mock(Field.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(IOC.class);
        mockStatic(Keys.class);

        IKey fieldKey = mock(IKey.class);
        when(Keys.getOrAdd(IField.class.getCanonicalName())).thenReturn(fieldKey);
        when(IOC.resolve(eq(fieldKey), any())).thenReturn(rulesF);

        actor = new ValidateFormDataActor(mock(IObject.class));
    }

    @Test
    public void shouldNotThrowExceptions() throws Exception {
        IObject serverForm = mock(IObject.class);
        IObject clientForm = mock(IObject.class);

        ValidateFormDataMessage message = mock(ValidateFormDataMessage.class);
        when(message.getFormFromRequest()).thenReturn(clientForm);
        when(message.getForm()).thenReturn(serverForm);

        Iterator serverIterator = mock(Iterator.class);
        when(serverForm.iterator()).thenReturn(serverIterator);
        Map.Entry entry = mock(Map.Entry.class);
        IFieldName fieldFieldName = mock(IFieldName.class);
        when(serverIterator.next()).thenReturn(entry, null);
        when(serverIterator.hasNext()).thenReturn(false);
        when(entry.getKey()).thenReturn(fieldFieldName);

        IObject fieldIObject = mock(IObject.class);
        when(serverForm.getValue(fieldFieldName)).thenReturn(fieldIObject);

        List<IObject> rules = new ArrayList<>();
        IObject rule = mock(IObject.class);
        rules.add(rule);
        when(rulesF.in(fieldIObject)).thenReturn(rules);
        when(rulesF.in(rule)).thenReturn("обязательное");

        when(clientForm.getValue(fieldFieldName)).thenReturn("valueFromClient");

        actor.validate(message);
    }

    @Test(expected = ValidateFormException.class)
    public void shouldThrowExceptionWhenFieldsIsInvalid() throws Exception {
        IObject serverForm = mock(IObject.class);
        IObject clientForm = mock(IObject.class);

        ValidateFormDataMessage message = mock(ValidateFormDataMessage.class);
        when(message.getFormFromRequest()).thenReturn(clientForm);
        when(message.getForm()).thenReturn(serverForm);

        Iterator serverIterator = mock(Iterator.class);
        when(serverForm.iterator()).thenReturn(serverIterator);
        Map.Entry entry = mock(Map.Entry.class);
        IFieldName fieldFieldName = mock(IFieldName.class);
        when(serverIterator.next()).thenReturn(entry, null);
        when(serverIterator.hasNext()).thenReturn(false);
        when(entry.getKey()).thenReturn(fieldFieldName);

        IObject fieldIObject = mock(IObject.class);
        when(serverForm.getValue(fieldFieldName)).thenReturn(fieldIObject);
        when(rulesF.in(fieldIObject)).thenReturn("обязательное");

        when(clientForm.getValue(fieldFieldName)).thenReturn("");

        actor.validate(message);
    }
}
