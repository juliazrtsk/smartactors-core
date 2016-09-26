package info.smart_tools.smartactors.core.db_task.insert.psql.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 *  The IObject wrapper for a message to insert documents to DB.
 */
public interface InsertMessage {

    /**
     * Get document-object
     * @return returns document-object to be used
     * @throws ChangeValueException
     * @throws ReadValueException
     */
    IObject getDocuments() throws ChangeValueException, ReadValueException;

    /**
     * Get collection name
     * @return returns name of current collection
     * @throws ChangeValueException
     * @throws ReadValueException
     */
    String getCollectionName() throws ChangeValueException, ReadValueException;

}
