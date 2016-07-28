package info.smart_tools.smartactors.plugin.immutable_receiver_chain;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.receiver_chain.ImmutableReceiverChainResolutionStrategy;

/**
 *
 */
public class PluginReceiverChain implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginReceiverChain(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            BootstrapItem receiverChainItem = new BootstrapItem("receiver_chain");

            receiverChainItem
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd(IReceiverChain.class.getCanonicalName()),
                                    new ImmutableReceiverChainResolutionStrategy());
                        } catch (ResolutionException | RegistrationException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(receiverChainItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}