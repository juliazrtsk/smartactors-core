package info.smart_tools.smartactors.core.cached_collection.wrapper;

import info.smart_tools.smartactors.core.cached_collection.wrapper.get_item.DateToMessage;
import info.smart_tools.smartactors.core.cached_collection.wrapper.get_item.EQMessage;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

public interface CriteriaCachedCollectionQuery {
    void setStartDateTime(DateToMessage query) throws ReadValueException, ChangeValueException;
    void setIsActive(EQMessage query) throws ReadValueException, ChangeValueException;
    void setKey(EQMessage query) throws ReadValueException, ChangeValueException;// TODO: write custom name for field
    void wrapped() throws ReadValueException, ChangeValueException;
}