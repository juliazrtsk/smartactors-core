package info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper;

import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

import java.util.Map;

/**
 * Interface of wrapepr for {@link info.smart_tools.smartactors.feature_management.feature_manager_actor.FeatureManagerActor}
 */
public interface OnFeatureStepCompletedWrapper {

    /**
     * Gets feature from the message
     * @return the instance of {@link IFeature}
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    IFeature getFeature()
            throws ReadValueException;

    /**
     * Gets current message processor from the message
     * @return the instance of {@link IMessageProcessor}
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    IMessageProcessor getMessageProcessor()
            throws ReadValueException;

    /**
     * Gets map of frozen feature message processors
     * @return the map of frozen feature message processors
     * @throws ReadValueException if any errors occurred on wrapper reading
     */
    Map<IMessageProcessor, IFeature> getFeatureProcess()
            throws ReadValueException;

}
