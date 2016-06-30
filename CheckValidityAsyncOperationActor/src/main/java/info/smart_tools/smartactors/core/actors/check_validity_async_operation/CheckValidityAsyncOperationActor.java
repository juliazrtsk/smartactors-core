package info.smart_tools.smartactors.core.actors.check_validity_async_operation;

import info.smart_tools.smartactors.core.actors.check_validity_async_operation.exception.InvalidAsyncOperationIdException;
import info.smart_tools.smartactors.core.actors.check_validity_async_operation.wrapper.CheckValidityMessage;
import info.smart_tools.smartactors.core.actors.check_validity_async_operation.wrapper.Session;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.List;

/**
 * The Actor for comparing of two identifiers of asynchronous operations:
 * one came from the client, and the list of remaining is stored in session.
 * If there is no coincidence, then throw new CheckException
 */
public class CheckValidityAsyncOperationActor {

    /**
     * Constructor for CheckValidityAsyncOperationActor
     * @param config contains any params
     */
    public CheckValidityAsyncOperationActor(final IObject config){
    }

    /**
     * Check validity asynchronous operation, if invalid - throw exception
     * @param message input message
     * @throws InvalidAsyncOperationIdException
     */
    public void check(final CheckValidityMessage message) throws InvalidAsyncOperationIdException {
        try {
            Session curSession = message.getSession();
            List<String> validIds = curSession.getIdentifiers();
            if (validIds == null || validIds.get(0) == null) {
                throw new ReadValueException("List of valid id is empty");
            }
            if (!validIds.contains(message.getAsyncOperationId())) {
                throw new InvalidAsyncOperationIdException("List with all identifiers not contains Id from client.");
            }
        } catch (ReadValueException | ChangeValueException e) {
            e.printStackTrace();
        }

    }

}
