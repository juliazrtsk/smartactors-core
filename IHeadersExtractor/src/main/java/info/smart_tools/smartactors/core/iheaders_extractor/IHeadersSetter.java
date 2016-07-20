package info.smart_tools.smartactors.core.iheaders_extractor;


import info.smart_tools.smartactors.core.iobject.IObject;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * Interface for extracting http headers from environment
 */
public interface IHeadersSetter {
    /**
     * Method for setting http headers from environment
     *
     * @param response Response object in which headers will add
     * @param environment Environment of the message processor
     *
     */
    void set(Object response, IObject environment);

}