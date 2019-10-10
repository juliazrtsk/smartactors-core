package info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;

/**
 *
 */
public class PluginIOCKeys implements IPlugin {
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginIOCKeys(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> iocKeysItem = IOC.resolve(
                    Keys.getKeyByName("bootstrap item"),
                    "ioc_keys"
            );

            iocKeysItem
                    .after("ioc_container")
                    .before("IOC")
                    .process(() -> {
                        try {
                            IOC.register(
                                    IOC.getKeyForKeyByNameStrategy(),
                                    new ResolveByNameIocStrategy()
                            );
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("IOCKeys plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            IOC.unregister(IOC.getKeyForKeyByNameStrategy());
                        } catch(DeletionException e) {
                            System.out.println("[WARNING] Deregistration of IOC key for key by name strategy failed.");
                        }
                    });

            bootstrap.add(iocKeysItem);
        } catch (ResolutionException e) {
            throw new PluginException(e);
        }
    }
}