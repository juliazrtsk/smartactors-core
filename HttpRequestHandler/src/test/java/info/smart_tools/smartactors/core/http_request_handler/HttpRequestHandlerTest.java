package info.smart_tools.smartactors.core.http_request_handler;

import info.smart_tools.smartactors.core.IDeserializeStrategy;
import info.smart_tools.smartactors.core.bootstrap.Bootstrap;
import info.smart_tools.smartactors.core.channel_handler_netty.ChannelHandlerNetty;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.exceptions.DeserializationException;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.i_add_request_parameters_to_iobject.IAddRequestParametersToIObject;
import info.smart_tools.smartactors.core.i_add_request_parameters_to_iobject.exception.AddRequestParametersToIObjectException;
import info.smart_tools.smartactors.core.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.core.ienvironment_handler.exception.RequestHandlerDataException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by sevenbits on 05.09.16.
 */
public class HttpRequestHandlerTest {

    IDeserializeStrategy deserializeStrategy;
    IKey mockedKey;
    IAddRequestParametersToIObject requestParametersToIObject;
    IObject httpResponse;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        deserializeStrategy = mock(IDeserializeStrategy.class);
        mockedKey = mock(IKey.class);
        requestParametersToIObject = mock(IAddRequestParametersToIObject.class);
        httpResponse = mock(IObject.class);
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);

        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );

        IOC.register(Keys.getOrAdd(IFieldName.class.getCanonicalName()), new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );

        IOC.register(Keys.getOrAdd("EmptyIObject"), new CreateNewInstanceStrategy(
                        args -> new DSObject()
                )
        );

        IOC.register(Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()), new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            if (args[0].equals(mockedKey)) {
                                return deserializeStrategy;
                            } else {
                                return requestParametersToIObject;
                            }
                        }
                )
        );


        IOC.register(Keys.getOrAdd("http_request_key_for_deserialize"), new SingletonStrategy(mockedKey));

//        IOC.register(
//                Keys.getOrAdd(IDeserializeStrategy.class.getCanonicalName()),
//                new SingletonStrategy(requestParametersToIObject)
//        );

        IOC.register(Keys.getOrAdd(ChannelHandlerNetty.class.getCanonicalName()), new CreateNewInstanceStrategy(
                        (args) -> {
                            IChannelHandler channelHandler = new ChannelHandlerNetty();
                            channelHandler.init(args[0]);
                            return channelHandler;
                        }
                )
        );
    }

    @Test
    public void testDeserialization() throws Exception {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        FullHttpRequest request = mock(FullHttpRequest.class);
        IObject message = new DSObject("{\"hello\": \"world\"}");
        when(deserializeStrategy.deserialize(request)).thenReturn(message);
        HttpRequestHandler requestHandler = new HttpRequestHandler(ScopeProvider.getCurrentScope(), null, null, null);
        IObject environment = requestHandler.getEnvironment(ctx, request);
        assertEquals(environment.getValue(new FieldName("message")), message);
    }

    @Test(expected = RequestHandlerDataException.class)
    public void testBadRequestBodyException() throws Exception {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        FullHttpRequest request = mock(FullHttpRequest.class);
        IObject message = new DSObject("{\"hello\": \"world\"}");
        when(deserializeStrategy.deserialize(request)).thenThrow(DeserializationException.class);
        HttpRequestHandler requestHandler = new HttpRequestHandler(ScopeProvider.getCurrentScope(), null, null, null);
        IOC.register(Keys.getOrAdd("HttpPostParametersToIObjectException"), new SingletonStrategy(
                        new DSObject("{\"statusCode\": 200}")
                )
        );
        IObject environment = requestHandler.getEnvironment(ctx, request);
        verify(ctx.writeAndFlush(any()));
    }

    @Test(expected = RequestHandlerDataException.class)
    public void testBadRequestUriException() throws Exception {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        FullHttpRequest request = mock(FullHttpRequest.class);
        IObject message = new DSObject("{\"hello\": \"world\"}");
        doThrow(new AddRequestParametersToIObjectException("exception")).when(requestParametersToIObject).extract(any(), any());
        HttpRequestHandler requestHandler = new HttpRequestHandler(ScopeProvider.getCurrentScope(), null, null, null);
        IOC.register(Keys.getOrAdd("HttpRequestParametersToIObjectException"), new SingletonStrategy(
                        new DSObject("{\"statusCode\": 200}")
                )
        );
        IObject environment = requestHandler.getEnvironment(ctx, request);
    }

}