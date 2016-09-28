package info.smart_tools.smartactors.plugin.configuration_manager;

import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.configuration_manager.ConfigurationManager;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;

/**
 *
 */
public class PluginConfigurationManager implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginConfigurationManager(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            // "configuration_manager" - create and register in IOC instance of ConfigurationManager
            IBootstrapItem<String> configurationManagerItem = new BootstrapItem("configuration_manager");

            configurationManagerItem
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()),
                                    new SingletonStrategy(new ConfigurationManager()));
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("ConfigurationManager plugin can't load: can't get ConfigurationManager key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("ConfigurationManager plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("ConfigurationManager plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(configurationManagerItem);

            // Two barrier items between which core configuration sections strategies should be registered
            bootstrap.add(new BootstrapItem("config_sections:start")
                    .process(() -> { }).after("configuration_manager"));
            bootstrap.add(new BootstrapItem("config_sections:done")
                    .process(() -> { }).after("config_sections:start"));

        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
