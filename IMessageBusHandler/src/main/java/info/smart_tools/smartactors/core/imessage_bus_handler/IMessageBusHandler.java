package info.smart_tools.smartactors.core.imessage_bus_handler;

import info.smart_tools.smartactors.core.imessage_bus_handler.exception.MessageBusHandlerException;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * MessageBusHandler interface for represent handler for processing sent message to the MessageBus
 */
public interface IMessageBusHandler {

    /**
     * Method for handle message
     * @param message the message for processing
     * @throws MessageBusHandlerException if any errors occurred
     */
    void handle(final IObject message)
            throws MessageBusHandlerException;

    /**
     * Method for handle message
     * @param message the message for processing
     * @param chainName the name of specific chain for processing message
     * @throws MessageBusHandlerException if any errors occurred
     */
    void handle(final IObject message, final Object chainName)
            throws MessageBusHandlerException;
}
