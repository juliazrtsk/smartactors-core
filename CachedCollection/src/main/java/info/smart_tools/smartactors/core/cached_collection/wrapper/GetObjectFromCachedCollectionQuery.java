package info.smart_tools.smartactors.core.cached_collection.wrapper;

import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.search.wrappers.ISearchQuery;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.stream.Stream;

public interface GetObjectFromCachedCollectionQuery extends ISearchQuery {
    String getKey() throws ReadValueException, ChangeValueException;// TODO: write custom name for field
    void setCollectionName(CollectionName collectionName) throws ReadValueException, ChangeValueException;
    void setKey(String key) throws ReadValueException, ChangeValueException;
    IObject wrapped() throws ReadValueException, ChangeValueException;
    Stream<IObject> getSearchResult() throws ReadValueException, ChangeValueException;
}