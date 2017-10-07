package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannel;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.IOutboundConnectionChannelListener;
import info.smart_tools.smartactors.endpoint_interfaces.ioutbound_connection_channel.exceptions.OutboundChannelListenerException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * Handler that creates and registers a {@link IOutboundConnectionChannel outbound connection channel} for a channel
 * where a event occurs.
 *
 * @param <TChannel> type of channel
 * @param <TDst>     type of destination message, usually {@link Void}
 */
public class OutboundChannelCreator<TChannel extends Channel, TDst>
        implements IBypassMessageHandler<IDefaultMessageContext<TChannel, TDst, TChannel>> {
    private final IEndpointPipeline<IDefaultMessageContext<IObject, Void, TChannel>> pipeline;
    private final IFunction0<?> channelIdProvider;
    private final IBiFunction<
            ? super TChannel,
            IEndpointPipeline<IDefaultMessageContext<IObject, Void, TChannel>>,
            IOutboundConnectionChannel>
                outboundChannelProvider;
    private final IOutboundConnectionChannelListener channelListener;

    private final ChannelFutureListener closeFutureListener;

    /**
     * THe constructor.
     *
     * @param pipeline                pipeline processing outbound messages
     * @param channelIdProvider       function creating outbound channel identifiers
     * @param outboundChannelProvider function constructing outbound channels for netty channels
     * @param channelListener         channel listener that should be notified on channel creation and destruction
     */
    public OutboundChannelCreator(
            final IEndpointPipeline<IDefaultMessageContext<IObject, Void, TChannel>> pipeline,
            final IFunction0<?> channelIdProvider,
            final IBiFunction<
                    ? super TChannel,
                    IEndpointPipeline<IDefaultMessageContext<IObject, Void, TChannel>>,
                    IOutboundConnectionChannel> outboundChannelProvider,
            final IOutboundConnectionChannelListener channelListener) {
        this.pipeline = pipeline;
        this.channelIdProvider = channelIdProvider;
        this.outboundChannelProvider = outboundChannelProvider;
        this.channelListener = channelListener;

        closeFutureListener = cf -> {
            Object id = cf.channel().attr(ChannelAttributes.OUTBOUND_CHANNEL_ID_KEY).getAndRemove();
            channelListener.onDisconnect(id);
        };
    }

    @Override
    public void handle(
        final IMessageHandlerCallback<IDefaultMessageContext<TChannel, TDst, TChannel>> next,
        final IDefaultMessageContext<TChannel, TDst, TChannel> context)
            throws MessageHandlerException {
        try {
            TChannel nettyChannel = context.getConnectionContext();
            IOutboundConnectionChannel channel = outboundChannelProvider.execute(nettyChannel, pipeline);
            Object id = channelIdProvider.execute();

            channelListener.onConnect(id, channel);

            nettyChannel.attr(ChannelAttributes.OUTBOUND_CHANNEL_ID_KEY).set(id);

            nettyChannel.closeFuture().addListener(closeFutureListener);
        } catch (FunctionExecutionException | InvalidArgumentException | OutboundChannelListenerException e) {
            throw new MessageHandlerException(e);
        }
    }
}
