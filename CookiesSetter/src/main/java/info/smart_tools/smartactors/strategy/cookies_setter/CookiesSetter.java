package info.smart_tools.smartactors.strategy.cookies_setter;

import info.smart_tools.smartactors.core.icookies_extractor.ICookiesSetter;
import info.smart_tools.smartactors.core.icookies_extractor.exceptions.CookieSettingException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.util.ArrayList;
import java.util.List;


/**
 * Cookies setter for {@link FullHttpResponse}
 * This implementation extract cookies from context of the environment and set them into response
 * Cookies should presents as {@link List<IObject>}
 * <pre>
 *     "cookies": [
 *         {
 *             "name": "nameOfTheCookie",
 *             "value": "valueOfTheCookie",
 *             "maxAge": "10"
 *         }
 *     ]
 * </pre>
 * If there is no maxAge, then cookie set as discard
 */
public class CookiesSetter implements ICookiesSetter {
    @Override
    public void set(final Object response, final IObject environment) throws CookieSettingException {
        FullHttpResponse httpResponse = (FullHttpResponse) response;
        IField contextField;
        IField cookiesField;
        IFieldName cookieName;
        IFieldName cookieValue;
        IFieldName maxAgeFieldName;
        try {
            contextField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "context");
            cookiesField = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "cookies");
            cookieName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "name");
            cookieValue = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "value");
            maxAgeFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "maxAge");
        } catch (ResolutionException e) {
            throw new CookieSettingException("Failed to resolve fieldName", e);
        }
        IObject context = null;
        List<IObject> cookies = null;
        try {
            context = contextField.in(environment, IObject.class);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new CookieSettingException("Failed to get context from environment", e);
        }
        try {
            cookies = cookiesField.in(context, List.class);
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new CookieSettingException("Failed to get cookies from context", e);
        }
        List<Cookie> cookiesList = new ArrayList<>();
        for (IObject cookieObject : cookies) {
            try {
                Cookie cookie = new DefaultCookie(
                        cookieObject.getValue(cookieName).toString(),
                        cookieObject.getValue(cookieValue).toString());
                Integer maxCookieAge = (Integer) cookieObject.getValue(maxAgeFieldName);
                if (maxCookieAge != null) {
                    cookie.setDiscard(false);
                    cookie.setMaxAge(maxCookieAge);
                }
                cookiesList.add(cookie);
            } catch (ReadValueException | InvalidArgumentException e) {
                throw new CookieSettingException("Failed to resolve cookie", e);
            }
        }
        httpResponse.headers().set(HttpHeaders.Names.SET_COOKIE,
                ServerCookieEncoder.encode(cookiesList));
    }
}
