package info.smart_tools.smartactors.message_processing_interfaces.message_processing;

import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;

/**
 * Interface for a object responding for processing of a single message.
 */
public interface IMessageProcessor {
    /**
     * Returns the message object.
     *
     * @return the message being processed
     */
    IObject getMessage();

    /**
     * Returns the context object of the message being processed. Context contains all the non-serializable data related
     * to the message.
     *
     * @return the context of message processing
     */
    IObject getContext();

    /**
     * Returns the response object being formed for the processed message. The response object will be sent to the
     * sender of the message being processed (and may be serialized in process of sending) so should contain only
     * serializable data.
     *
     * @return the response for the message
     */
    IObject getResponse();

    /**
     * Returns the {@link IMessageProcessingSequence} managing order of receivers receiving this message.
     *
     * @return the sequence of receivers that should receive the message
     */
    IMessageProcessingSequence getSequence();

    /**
     * Returns the message environment object. The environment object should contain at least the following fields:
     * - "message" - the message itself (as instance of {@link IObject})
     * - "context" - the {@link IObject} containing non-serializable objects related to processing of the message
     * - "response" - the {@link IObject} containing fields that will be sent as a response to the processed message
     * - "sequence" - the {@link IMessageProcessingSequence} instance associated with the message
     * - "arguments" - the arguments {@link IObject} passed from {@link IReceiverChain}
     *
     * @return the environment object
     */
    IObject getEnvironment();

    /**
     * Set a global configuration object to use for processing of next message.
     *
     * @param config    the configuration object
     * @throws InvalidArgumentException if {@code config} is {@code null}
     */
    void setConfig(IObject config) throws InvalidArgumentException;

    /**
     * Start processing new message using this instance of {@link IMessageProcessor}.
     *
     * @param message    the message to process
     * @param context    the context of message processing
     * @throws InvalidArgumentException if message is {@code null}
     * @throws InvalidArgumentException if context is {@code null}
     * @throws ResolutionException if fails to resolve any dependency
     * @throws ChangeValueException if modification of internal environment object fails
     */
    void process(IObject message, IObject context)
            throws InvalidArgumentException, ResolutionException, ChangeValueException;

    /**
     * Notify this message processor that currently called message receiver started a asynchronous operation and the
     * processor should not call any following receivers until it is completed.
     *
     * @throws AsynchronousOperationException if called outside of {@link IMessageReceiver#receive} call after completion
     *                                             of all asynchronous operations
     */
    void pauseProcess() throws AsynchronousOperationException;

    /**
     * Notify this message processor that a asynchronous operation (on start of which the message processor was notified
     * by call of {@link #pauseProcess()}) is completed.
     *
     * @param e    the exception occurred during asynchronous operation or {@code null} if operation completed successful
     * @throws AsynchronousOperationException if all asynchronous operations are already completed i.e. number of
     *                                             calls to #continueProcess is greater than number of calls to {@link
     *                                             #pauseProcess()} since start of execution of last receiver
     */
    void continueProcess(Throwable e) throws AsynchronousOperationException;

    /**
     * Send a signal to this message processor.
     *
     * A exception will be resolved using given name and processed by the message processor as soon as possible (usually after completion of
     * current step) just as if it was thrown on the step being currently executed.
     *
     * If there is no chain handling signal exception then processing of the message will be completed silently.
     *
     * Signal exception should extend {@link Signal}.
     *
     * @param signalName    name of the signal
     * @throws InvalidArgumentException if {@code signalName} is {@code null}
     * @throws InvalidArgumentException if {@code signalName} is not a valid signal name
     */
    void signal(String signalName) throws InvalidArgumentException;
}
