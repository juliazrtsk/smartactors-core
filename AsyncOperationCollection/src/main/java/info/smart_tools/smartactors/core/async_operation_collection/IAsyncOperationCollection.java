package info.smart_tools.smartactors.core.async_operation_collection;

import info.smart_tools.smartactors.core.async_operation_collection.exception.CompleteAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.exception.CreateAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.exception.DeleteAsyncOperationException;
import info.smart_tools.smartactors.core.async_operation_collection.exception.GetAsyncOperationException;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Interface for AsyncOperationCollection
 */
public interface IAsyncOperationCollection {

    /**
     * Get asynchronous operation
     * @param token operation unique token
     * @return operation object
     * @throws GetAsyncOperationException if error during read is occurred
     */
    IObject getAsyncOperation(final String token) throws GetAsyncOperationException;

    /**
     * Create async operation in db
     * @param data the async operation data
     * @param token guid token for operation
     * @throws CreateAsyncOperationException 123
     */
    void createAsyncOperation(final IObject data, final String token) throws CreateAsyncOperationException;

    /**
     * Completes asynchronous operation
     * @param asyncOperation operation for complete
     * @throws CompleteAsyncOperationException if error during complete is occurred
     */
    void complete(IObject asyncOperation) throws CompleteAsyncOperationException;

    /**
     * Deletes asynchronous operation
     * @param token operation unique token
     * @throws DeleteAsyncOperationException if error during delete is occurred
     */
    void delete(String token) throws DeleteAsyncOperationException;
}
