package info.smart_tools.smartactors.plugin.datetime_formatter_strategy;

import info.smart_tools.smartactors.core.bootstrap.Bootstrap;
import info.smart_tools.smartactors.core.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.plugin.ioc_keys.PluginIOCKeys;
import info.smart_tools.smartactors.plugin.ioc_simple_container.PluginIOCSimpleContainer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class PluginDateTimeFormatterTest {

    @BeforeClass
    public static void prepareIOC() throws PluginException, ProcessExecutionException, InvalidArgumentException {

        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new PluginDateTimeFormatter(bootstrap).load();
        bootstrap.start();
    }

    @Test
    public void ShouldReturnFormatter() throws Exception {

        LocalDateTime now = LocalDateTime.of(2016, 8, 18, 0, 0);
        DateTimeFormatter formatter = IOC.resolve(Keys.getOrAdd("datetime_formatter"));
        assertEquals(formatter.format(now), "08-18-2016 00:00:00");
    }
}