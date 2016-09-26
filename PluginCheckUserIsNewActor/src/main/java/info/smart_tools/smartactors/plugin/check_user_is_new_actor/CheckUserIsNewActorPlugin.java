package info.smart_tools.smartactors.plugin.check_user_is_new_actor;


import info.smart_tools.smartactors.actors.check_user_is_new.CheckUserIsNewActor;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Arrays;

/**
 * Plugin for load IOC-strategy for check user is new actor
 */
public class CheckUserIsNewActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public CheckUserIsNewActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("CheckUserIsNewActorPlugin");

            item
                    .after("IOC")
                    .before("starter")
                    .process(() -> {
                        try {
                            IKey checkUserIsNewActorKey = Keys.getOrAdd(CheckUserIsNewActor.class.getCanonicalName());
                            IOC.register(checkUserIsNewActorKey,
                                    new CreateNewInstanceStrategy(
                                            (args) -> {
                                                try {
                                                    return new CheckUserIsNewActor((IObject) args[0]);
                                                } catch (ArrayIndexOutOfBoundsException e) {
                                                    throw new RuntimeException(
                                                            "Can't get args: args must contain one or more elements " +
                                                                    "and first element must be IObject",
                                                            e);
                                                } catch (InvalidArgumentException e) {
                                                    throw new RuntimeException(
                                                            "Can't create actor with this args: "
                                                                    + Arrays.toString(args),
                                                            e);
                                                }
                                            }
                                    )
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("CheckUserIsNewActor plugin can't load: can't get CheckUserIsNewActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("CheckUserIsNewActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("CheckUserIsNewActor plugin can't load: can't register new strategy", e);
                        }
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't get BootstrapItem from one of reason", e);
        }
    }
}
