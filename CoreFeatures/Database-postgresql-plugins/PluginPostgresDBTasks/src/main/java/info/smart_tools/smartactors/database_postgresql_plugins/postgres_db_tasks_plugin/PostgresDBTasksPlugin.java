package info.smart_tools.smartactors.database_postgresql_plugins.postgres_db_tasks_plugin;

import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.database.database_storage.utils.CollectionName;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.database.interfaces.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.IStorageConnection;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.database_postgresql.postgres_count_task.CountMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_count_task.PostgresCountTask;
import info.smart_tools.smartactors.database_postgresql.postgres_create_task.CreateCollectionMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_create_task.PostgresCreateTask;
import info.smart_tools.smartactors.database_postgresql.postgres_delete_task.DeleteMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_delete_task.PostgresDeleteTask;
import info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task.GetByIdMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_getbyid_task.PostgresGetByIdTask;
import info.smart_tools.smartactors.database_postgresql.postgres_insert_task.InsertMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_insert_task.PostgresInsertTask;
import info.smart_tools.smartactors.database_postgresql.postgres_search_task.PostgresSearchTask;
import info.smart_tools.smartactors.database_postgresql.postgres_search_task.SearchMessage;
import info.smart_tools.smartactors.database_postgresql.postgres_upsert_task.PostgresUpsertTask;
import info.smart_tools.smartactors.database_postgresql.postgres_upsert_task.UpsertMessage;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.uuid_nextid_strategy.UuidNextIdStrategy;

/**
 * Plugin with IOC-strategies for database tasks
 */
public class PostgresDBTasksPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public PostgresDBTasksPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            BootstrapItem item = new BootstrapItem("PostgresDBTasksPlugin");
            item
//                .after("IOC")
//                .after("IFieldPlugin")
//                .after("iobject")
                .process(() -> {
                    try {
                        registerCreateTask();
                        registerNextIdStrategy();
                        registerUpsertTask();
                        registerInsertTask();
                        registerGetByIdTask();
                        registerSearchTask();
                        registerDeleteTask();
                        registerCountTask();
                    } catch (ResolutionException e) {
                        throw new ActionExecuteException("Can't resolve fields for db task.", e);
                    } catch (InvalidArgumentException e) {
                        throw new ActionExecuteException("Can't create strategy for db task.", e);
                    } catch (RegistrationException e) {
                        throw new ActionExecuteException("Can't register strategy for db task.", e);
                    }
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't get BootstrapItem.", e);
        }
    }

    private void registerCreateTask() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        IField optionsField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "options");

        IOC.register(
                Keys.getOrAdd(CreateCollectionMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new CreateCollectionMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IObject getOptions() throws ReadValueException {
                                    try {
                                        return (IObject) optionsField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd("db.collection.create"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                IObject options = null;
                                if (args.length > 2) {
                                     options = (IObject) args[2];
                                }
                                IDatabaseTask task = new PostgresCreateTask(connection);

                                IObject query = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

                                collectionNameField.out(query, collectionName);
                                optionsField.out(query, options);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve create db task.", e);
                            }
                        }
                )
        );
    }

    private void registerNextIdStrategy() throws RegistrationException, ResolutionException {
        IOC.register(
                Keys.getOrAdd("db.collection.nextid"),
                new UuidNextIdStrategy()
        );
    }

    private void registerUpsertTask() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        IField documentField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "document");

        registerNextIdStrategy();
        IOC.register(
                Keys.getOrAdd(UpsertMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new UpsertMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IObject getDocument() throws ReadValueException {
                                    try {
                                        return (IObject) documentField.in(message);
                                    } catch (InvalidArgumentException e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd("db.collection.upsert"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                IObject document = (IObject) args[2];
                                IDatabaseTask task = new PostgresUpsertTask(connection);

                                IObject query = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

                                collectionNameField.out(query, collectionName);
                                documentField.out(query, document);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve upsert db task.", e);
                            }
                        }
                )
        );
    }

    private void registerGetByIdTask() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        IField idField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "id");
        IField callbackField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "callback");

        IOC.register(
                Keys.getOrAdd(GetByIdMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new GetByIdMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public Object getId() throws ReadValueException {
                                    try {
                                        return idField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IAction<IObject> getCallback() throws ReadValueException {
                                    try {
                                        return (IAction<IObject>) callbackField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd("db.collection.getbyid"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                Object id = args[2];
                                IAction<IObject> callback = (IAction<IObject>) args[3];
                                IDatabaseTask task = new PostgresGetByIdTask(connection);

                                IObject query = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

                                collectionNameField.out(query, collectionName);
                                idField.out(query, id);
                                callbackField.out(query, callback);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve getbyid db task.", e);
                            }
                        }
                )
        );
    }

    private void registerSearchTask() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        IField criteriaField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "criteria");
        IField callbackField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "callback");

        IOC.register(
                Keys.getOrAdd(SearchMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new SearchMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IObject getCriteria() throws ReadValueException {
                                    try {
                                        return criteriaField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IAction<IObject[]> getCallback() throws ReadValueException {
                                    try {
                                        return (IAction<IObject[]>) callbackField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd("db.collection.search"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                IObject criteria = (IObject) args[2];
                                IAction<IObject[]> callback = (IAction<IObject[]>) args[3];
                                IDatabaseTask task = new PostgresSearchTask(connection);

                                IObject query = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

                                collectionNameField.out(query, collectionName);
                                criteriaField.out(query, criteria);
                                callbackField.out(query, callback);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve search db task.", e);
                            }
                        }
                )
        );
    }

    private void registerDeleteTask() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        IField documentField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "document");

        IOC.register(
                Keys.getOrAdd(DeleteMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new DeleteMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IObject getDocument() throws ReadValueException {
                                    try {
                                        return documentField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd("db.collection.delete"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                IObject document = (IObject) args[2];
                                IDatabaseTask task = new PostgresDeleteTask(connection);

                                IObject query = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

                                collectionNameField.out(query, collectionName);
                                documentField.out(query, document);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve delete db task.", e);
                            }
                        }
                )
        );
    }

    private void registerInsertTask() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        IField documentField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "document");

        IOC.register(
                Keys.getOrAdd(InsertMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new InsertMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IObject getDocument() throws ReadValueException {
                                    try {
                                        return (IObject) documentField.in(message);
                                    } catch (InvalidArgumentException e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd("db.collection.insert"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                IObject document = (IObject) args[2];
                                IDatabaseTask task = new PostgresInsertTask(connection);

                                IObject query = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

                                collectionNameField.out(query, collectionName);
                                documentField.out(query, document);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve insert db task.", e);
                            }
                        }
                )
        );
    }

    private void registerCountTask() throws RegistrationException, ResolutionException, InvalidArgumentException {
        IField collectionNameField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "collectionName");
        IField criteriaField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "criteria");
        IField callbackField = IOC.resolve(
                Keys.getOrAdd(IField.class.getCanonicalName()), "callback");

        IOC.register(
                Keys.getOrAdd(CountMessage.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            IObject message = (IObject) args[0];
                            return new CountMessage() {
                                @Override
                                public CollectionName getCollectionName() throws ReadValueException {
                                    try {
                                        return (CollectionName) collectionNameField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IObject getCriteria() throws ReadValueException {
                                    try {
                                        return criteriaField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                                @Override
                                public IAction<Long> getCallback() throws ReadValueException {
                                    try {
                                        return (IAction<Long>) callbackField.in(message);
                                    } catch (Exception e) {
                                        throw new ReadValueException(e);
                                    }
                                }
                            };
                        }
                )
        );
        IOC.register(
                Keys.getOrAdd("db.collection.count"),
                //TODO:: use smth like ResolveByNameStrategy, but this caching strategy should call prepare always
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                IStorageConnection connection = (IStorageConnection) args[0];
                                CollectionName collectionName = CollectionName.fromString(String.valueOf(args[1]));
                                IObject criteria = (IObject) args[2];
                                IAction<Long> callback = (IAction<Long>) args[3];
                                IDatabaseTask task = new PostgresCountTask(connection);

                                IObject query = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.iobject.IObject"));

                                collectionNameField.out(query, collectionName);
                                criteriaField.out(query, criteria);
                                callbackField.out(query, callback);

                                task.prepare(query);
                                return task;
                            } catch (Exception e) {
                                throw new RuntimeException("Can't resolve count db task.", e);
                            }
                        }
                )
        );
    }

}
