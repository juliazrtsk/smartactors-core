package info.smart_tools.smartactors.plugin.get_header_from_request_rule;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.transformation_rules.get_header_from_request.GetHeaderFromRequestRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, GetHeaderFromRequestRulePlugin.class, CreateNewInstanceStrategy.class})
@RunWith(PowerMockRunner.class)
public class GetHeaderFromRequestRulePluginTest {
    private GetHeaderFromRequestRulePlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new GetHeaderFromRequestRulePlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey ruleKey = mock(IKey.class);
        when(Keys.getOrAdd("getHeaderFromRequestRule")).thenReturn(ruleKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd("getHeaderFromRequestRule");

        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor = ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);

        verifyStatic();
        IOC.register(eq(ruleKey), createNewInstanceStrategyArgumentCaptor.capture());

        IObject arg = mock(IObject.class);

        GetHeaderFromRequestRule rule = mock(GetHeaderFromRequestRule.class);
        whenNew(GetHeaderFromRequestRule.class).withNoArguments().thenReturn(rule);

        assertTrue("Objects must return correct object", createNewInstanceStrategyArgumentCaptor.getValue().resolve(arg) == rule);

        verifyNew(GetHeaderFromRequestRule.class).withNoArguments();
    }

    @Test
    public void MustInCorrectLoadNewIBootstrapItemThrowException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenThrow(new InvalidArgumentException(""));

        try {
            plugin.load();
        } catch (PluginException e) {

            verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenKeysThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        when(Keys.getOrAdd("getHeaderFromRequestRule")).thenThrow(new ResolutionException(""));

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {
            verifyStatic();
            Keys.getOrAdd("getHeaderFromRequestRule");
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey ruleKey = mock(IKey.class);
        when(Keys.getOrAdd("getHeaderFromRequestRule")).thenReturn(ruleKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        doThrow(new RegistrationException("")).when(IOC.class);
        IOC.register(eq(ruleKey), any());

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(mock(CreateNewInstanceStrategy.class));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd("getHeaderFromRequestRule");

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            verifyStatic();
            IOC.register(eq(ruleKey), any(CreateNewInstanceStrategy.class));

            IObject arg = mock(IObject.class);

            GetHeaderFromRequestRule rule = mock(GetHeaderFromRequestRule.class);

            whenNew(GetHeaderFromRequestRule.class).withNoArguments().thenReturn(rule);

            assertTrue("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{arg}) == rule);

            verifyNew(GetHeaderFromRequestRule.class).withNoArguments();
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenNewCreateInstanceThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey ruleKey = mock(IKey.class);
        when(Keys.getOrAdd("getHeaderFromRequestRule")).thenReturn(ruleKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenThrow(new RegistrationException(""));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd("getHeaderFromRequestRule");

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            IObject arg = mock(IObject.class);

            GetHeaderFromRequestRule actor = mock(GetHeaderFromRequestRule.class);
            whenNew(GetHeaderFromRequestRule.class).withNoArguments().thenReturn(actor);

            assertTrue("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{arg}) == actor);

            verifyNew(GetHeaderFromRequestRule.class).withNoArguments();
            return;
        }
        assertTrue("Must throw exception", false);
    }
}
