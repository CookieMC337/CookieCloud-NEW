package de.cookiemc.node;

import de.cookiemc.IdentifiableClassLoader;
import de.cookiemc.common.VersionInfo;
import de.cookiemc.common.collection.NamedThreadFactory;
import de.cookiemc.common.function.ExceptionallyConsumer;
import de.cookiemc.common.function.ExceptionallyRunnable;
import de.cookiemc.common.logging.LogLevel;
import de.cookiemc.common.logging.Logger;
import de.cookiemc.common.logging.formatter.ColoredMessageFormatter;
import de.cookiemc.common.logging.handler.HandledAsyncLogger;
import de.cookiemc.common.logging.handler.LogEntry;
import de.cookiemc.common.misc.FileUtils;
import de.cookiemc.common.misc.StringUtils;
import de.cookiemc.common.task.Task;
import de.cookiemc.common.util.Validation;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.DriverEnvironment;
import de.cookiemc.driver.commands.ICommandManager;
import de.cookiemc.driver.commands.parameter.defaults.NodeParamType;
import de.cookiemc.driver.commands.parameter.defaults.PlayerParamType;
import de.cookiemc.driver.commands.parameter.defaults.ServiceParamType;
import de.cookiemc.driver.commands.parameter.defaults.TaskParamType;
import de.cookiemc.driver.commands.sender.ConsoleCommandSender;
import de.cookiemc.driver.commands.sender.defaults.DefaultCommandSender;
import de.cookiemc.driver.console.Console;
import de.cookiemc.driver.console.screen.Screen;
import de.cookiemc.driver.console.screen.ScreenManager;
import de.cookiemc.driver.database.IDatabaseManager;
import de.cookiemc.driver.database.SectionedDatabase;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.event.defaults.driver.DriverLogEvent;
import de.cookiemc.http.api.HttpServer;
import de.cookiemc.http.impl.NettyHttpServer;
import de.cookiemc.driver.message.IChannelMessenger;
import de.cookiemc.driver.module.IModuleManager;
import de.cookiemc.driver.networking.NetworkComponent;
import de.cookiemc.driver.networking.PacketProvider;
import de.cookiemc.http.ProtocolAddress;
import de.cookiemc.driver.networking.protocol.codec.buf.IBufferObject;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverLoggingPacket;
import de.cookiemc.driver.networking.protocol.packets.defaults.DriverUpdatePacket;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.node.INodeManager;
import de.cookiemc.driver.node.config.DefaultNodeConfig;
import de.cookiemc.driver.node.packet.NodeCycleDataPacket;
import de.cookiemc.driver.permission.PermissionChecker;
import de.cookiemc.driver.player.ICloudPlayerManager;
import de.cookiemc.driver.player.executor.PlayerExecutor;
import de.cookiemc.driver.player.impl.DefaultCloudOfflinePlayer;
import de.cookiemc.driver.services.ICloudServer;
import de.cookiemc.driver.services.ICloudServiceManager;
import de.cookiemc.driver.services.IProcessCloudServer;
import de.cookiemc.driver.services.fallback.SimpleFallback;
import de.cookiemc.driver.services.task.ICloudServiceTaskManager;
import de.cookiemc.driver.services.task.IServiceTask;
import de.cookiemc.driver.services.task.UniversalServiceTask;
import de.cookiemc.driver.services.task.bundle.DefaultTaskGroup;
import de.cookiemc.driver.services.task.bundle.ITaskGroup;
import de.cookiemc.driver.services.template.ITemplate;
import de.cookiemc.driver.services.template.ITemplateManager;
import de.cookiemc.driver.services.template.ITemplateStorage;
import de.cookiemc.driver.services.template.def.CloudTemplate;
import de.cookiemc.driver.services.utils.ServiceShutdownBehaviour;
import de.cookiemc.driver.services.utils.SpecificDriverEnvironment;
import de.cookiemc.driver.services.utils.version.ServiceVersion;
import de.cookiemc.driver.setup.SetupControlState;
import de.cookiemc.driver.storage.INetworkDocumentStorage;
import de.cookiemc.driver.sync.ISyncedNetworkPromise;
import de.cookiemc.driver.sync.SyncedObjectType;
import de.cookiemc.driver.uuid.IdentificationCache;
import de.cookiemc.node.cache.NodeIdentificationCache;
import de.cookiemc.node.cache.NodeSyncedNetworkPromise;
import de.cookiemc.node.commands.impl.*;
import de.cookiemc.node.console.ConsoleCommandEventAdapter;
import de.cookiemc.node.console.NodeCommandCompleter;
import de.cookiemc.node.console.NodeCommandInputHandler;
import de.cookiemc.node.console.NodeScreenManager;
import de.cookiemc.node.console.log4j.EmptyAppenderSkeleton;
import de.cookiemc.node.commands.NodeCommandManager;
import de.cookiemc.node.commands.impl.*;
import de.cookiemc.node.config.ConfigManager;
import de.cookiemc.node.config.MainConfiguration;
import de.cookiemc.node.config.NodeNetworkDocumentStorage;
import de.cookiemc.node.database.config.DatabaseConfiguration;
import de.cookiemc.node.database.config.DatabaseType;
import de.cookiemc.node.database.DefaultDatabaseManager;
import de.cookiemc.node.handler.http.V1PingRouter;
import de.cookiemc.node.handler.http.V1StatusRouter;
import de.cookiemc.node.handler.packet.normal.*;
import de.cookiemc.node.handler.packet.remote.*;
import de.cookiemc.node.handler.packet.normal.*;
import de.cookiemc.node.handler.packet.remote.*;
import de.cookiemc.node.message.NodeChannelMessenger;
import de.cookiemc.node.module.NodeModuleManager;
import de.cookiemc.node.node.BaseNode;
import de.cookiemc.node.node.NodeBasedClusterExecutor;
import de.cookiemc.node.node.NodeNodeManager;
import de.cookiemc.node.player.NodePlayerManager;
import de.cookiemc.node.setup.NodeRemoteSetup;
import de.cookiemc.node.setup.NodeSetup;
import de.cookiemc.node.setup.database.MongoDBSetup;
import de.cookiemc.node.setup.database.MySqlSetup;
import de.cookiemc.node.service.NodeServiceManager;
import de.cookiemc.node.service.NodeServiceTaskManager;
import de.cookiemc.node.service.helper.NodeServiceQueue;
import de.cookiemc.node.service.template.LocalTemplateStorage;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class NodeDriver extends CloudDriver<INode> {

    public static final File NODE_FOLDER = new File("cloud/");
    public static final File CONFIG_FILE = new File(NODE_FOLDER, "config.json");
    public static final File LOG_FOLDER = new File(NODE_FOLDER, "logs/");
    public static File MODULE_FOLDER;

    public static final File STORAGE_FOLDER = new File(NODE_FOLDER, "storage/");
    public static final File STORAGE_VERSIONS_FOLDER = new File(STORAGE_FOLDER, "versions/");
    public static final File STORAGE_TEMP_FOLDER = new File(STORAGE_FOLDER, "tmp-" + UUID.randomUUID().toString().substring(0, 5) + "/");
    public static final File TEMPLATES_DIR = new File(STORAGE_FOLDER, "templates/");

    public static final File SERVICE_DIR = new File(NODE_FOLDER, "services/");
    public static final File SERVICE_DIR_STATIC = new File(SERVICE_DIR, "permanent/");
    public static final File SERVICE_DIR_DYNAMIC = new File(SERVICE_DIR, "temporary/");

    /**
     * The static instance to override the cloud instance
     */
    @Getter
    private static NodeDriver instance;

    /**
     * The current configManager to read
     * and save the node config containing
     * important data for this Driver-Instance
     */
    private ConfigManager configManager;

    /**
     * The console instance to implement the
     * ScreenSystem and listen for command-input
     * Very complex system. Do not touch anything
     * because it is highly interconnected with the
     * Logging-System and if one method is not used the right
     * way the whole console-system might not work anymore
     */
    private Console console;

    /**
     * The current commandManager to work with the
     * provided console-system and logging-system
     */
    private ConsoleCommandSender commandSender;

    /**
     * The java executor service to schedule updating tasks etc.
     */
    private final ScheduledExecutorService scheduledExecutor;

    /**
     * The node-object that belongs to this NodeDriver-Instance
     */
    private INode node;

    /**
     * The WebServer for our built-in Rest-API
     */
    private HttpServer webServer;

    /**
     * The cluster executor to manage the cluster
     * and accept new connections
     */
    private NodeBasedClusterExecutor networkExecutor;

    /**
     * The service queue to provide all groups & tasks
     * with enough services of their kind
     */
    private NodeServiceQueue serviceQueue;


    public NodeDriver(Logger logger, Console console, boolean devMode, String modulePath) throws Exception {
        super(logger, DriverEnvironment.NODE);
        instance = this;

        this.scheduledExecutor = Executors.newScheduledThreadPool(4, new NamedThreadFactory("Scheduler"));
        this.running = false;

        ((HandledAsyncLogger) logger).addHandler(entry -> this.getProviderRegistry().getUnchecked(IEventManager.class).callEventGlobally(new DriverLogEvent(entry)));

        this.console = console;

        MODULE_FOLDER = new File(modulePath);
        //setting node screen manager
        this.providerRegistry.setProvider(ScreenManager.class, new NodeScreenManager());
        this.providerRegistry.setProvider(ICommandManager.class, new NodeCommandManager());

        logger.info("Configured ScreenManager & CommandManager!");

        ICommandManager commandManager = CloudDriver.getInstance().getProviderRegistry().getUnchecked(ICommandManager.class);
        commandManager.registerCommands(console);
        commandManager.registerEventAdapter(new ConsoleCommandEventAdapter());

        ScreenManager screenManager = this.providerRegistry.getUnchecked(ScreenManager.class);
        Screen screen = screenManager.registerScreen("console", true);

        //registering screen values
        screen.registerInputHandler(new NodeCommandInputHandler());
        screen.registerTabCompleter(new NodeCommandCompleter());
        screen.join();

        if (!(Thread.currentThread().getContextClassLoader() instanceof IdentifiableClassLoader)) {
            System.out.println("Changed ClassLoader");
            Thread.currentThread().setContextClassLoader(new IdentifiableClassLoader(new URL[]{CloudDriver.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL()}));
        }
        System.out.println(Thread.currentThread().getContextClassLoader().getClass());

        Task.runAsync(() -> {
            if (running) {
                return;
            }
            screen.clear();
            this.running = true;

            //loading config
            this.configManager = new ConfigManager();
            try {
                this.configManager.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.logger.setMinLevel(this.configManager.getConfig().getLogLevel());
            this.logger.debug("Set LogLevel to {}", this.logger.getMinLevel().getName());

            this.commandSender = new DefaultCommandSender(
                    this.configManager.getConfig().getNodeConfig().getNodeName(),
                    this.console
            ).forceFunction((ExceptionallyConsumer<String>) s -> console.forceWrite(ColoredMessageFormatter.format(LogEntry.forMessage(LogLevel.INFO, s))));

            //storage managing
            this.providerRegistry.setProvider(INetworkDocumentStorage.class, new NodeNetworkDocumentStorage());
            this.providerRegistry.getUnchecked(INetworkDocumentStorage.class).fetch();

            //checking if setup required
            if (!this.configManager.isDidExist()) {
                this.startSetup();
                return;
            } else {
                this.logger.trace("Setup already done ==> Skipping...");
            }

            //activating entering commands
            this.providerRegistry.getUnchecked(ICommandManager.class).setActive(true);

            if (devMode) {
                this.logger.debug("DevMode is activated!");
                //in dev mode player "CookieMC337" has every permission
                this.providerRegistry.setProvider(PermissionChecker.class, (playerUniqueId, permission) -> playerUniqueId.toString().equalsIgnoreCase("82e8f5a2-4077-407b-af8b-e8325cad7191"));
            }

            //avoid log4j errors
            org.apache.log4j.BasicConfigurator.configure(new EmptyAppenderSkeleton());

            logger.info("Configured 'org.apache.log4j.BasicConfigurator'!");

            //print header and start node-cluster
            this.console.printHeader();
            this.node = new BaseNode(configManager);

            //starting web-server
            this.webServer = new NettyHttpServer();
            this.logger.info("Setting up HttpListeners...");
            for (ProtocolAddress address : configManager.getConfig().getHttpListeners()) {
                this.webServer.addListener(address.toHttp());
            }

            //registering default web api handlers
            this.webServer.getHandlerRegistry().registerHandlers("v1", new V1PingRouter(), new V1StatusRouter());

            this.logger.info("Setting up NetworkListeners...");
            this.networkExecutor = new NodeBasedClusterExecutor(this.configManager.getConfig());

            //managing database
            this.providerRegistry.setProvider(
                    IDatabaseManager.class,
                    new DefaultDatabaseManager(
                            MainConfiguration.getInstance().getDatabaseConfiguration().getType(),
                            MainConfiguration.getInstance().getDatabaseConfiguration()
                    )
            );

            IDatabaseManager databaseManager = this.providerRegistry.getUnchecked(IDatabaseManager.class);

            {
                SectionedDatabase db = databaseManager.getDatabase();
                db.registerSection("players", DefaultCloudOfflinePlayer.class);
                db.registerSection("tasks", UniversalServiceTask.class);
                db.registerSection("groups", DefaultTaskGroup.class);

                this.providerRegistry.setProvider(ICloudServiceTaskManager.class, new NodeServiceTaskManager());
                this.providerRegistry.setProvider(ICloudServiceManager.class, new NodeServiceManager());
                this.providerRegistry.setProvider(ICloudPlayerManager.class, new NodePlayerManager());
                this.providerRegistry.setProvider(IChannelMessenger.class, new NodeChannelMessenger());
                this.providerRegistry.setProvider(IModuleManager.class, new NodeModuleManager());
                this.providerRegistry.setProvider(INodeManager.class, new NodeNodeManager());
                this.logger.info("§8");

                if (node.getConfig().getClusterAddresses() != null && node.getConfig().getClusterAddresses().length > 0) {
                    node.getConfig().setRemote();
                }

                if (this.node.getConfig().isRemote()) {
                    this.networkExecutor.connectToAllOtherNodes(node.getName(), node.getConfig().getClusterAddresses()).syncUninterruptedly(); //wait till complete
                } else {
                    this.logger.info("§7This Node is a HeadNode §7and boots up the Cluster...");
                }

                this.providerRegistry.setProvider(IdentificationCache.class, new NodeIdentificationCache()).onTaskSucess(cache -> {
                    cache.setEnabled(MainConfiguration.getInstance().isUniqueIdCaching());
                    cache.loadAsync()
                            .onTaskSucess(uuids -> {
                                logger.info("Loaded {} UUIDs from cache!", uuids.size());
                            });
                });


                //creating needed files
                this.logger.trace("Creating needed folders...");
                NodeDriver.NODE_FOLDER.mkdirs();

                NodeDriver.STORAGE_FOLDER.mkdirs();
                NodeDriver.STORAGE_VERSIONS_FOLDER.mkdirs();

                NodeDriver.SERVICE_DIR.mkdirs();
                NodeDriver.SERVICE_DIR_STATIC.mkdirs();
                NodeDriver.SERVICE_DIR_DYNAMIC.mkdirs();
                this.logger.trace("Required folders created!");


                //checking if directories got deleted meanwhile
                for (ITaskGroup parent : this.providerRegistry.getUnchecked(ICloudServiceTaskManager.class).getAllCachedTaskGroups()) {

                    //creating templates
                    for (ITemplate template : parent.getTemplates()) {
                        ITemplateStorage storage = template.getStorage();
                        if (storage != null) {
                            storage.createTemplate(template);
                        }
                    }
                }

                FileUtils.setTempDirectory(Paths.get(".temp"));

                //registering template storage
                this.providerRegistry.getUnchecked(ITemplateManager.class).registerStorage(new LocalTemplateStorage());

                //copying files
                this.logger.trace("§7Copying files§8...");
                try {
                    FileUtils.copyResource("/impl/remote.jar", STORAGE_VERSIONS_FOLDER + "/remote.jar", getClass());
                    FileUtils.copyResource("/impl/plugin.jar", STORAGE_VERSIONS_FOLDER + "/plugin.jar", getClass());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.logger.trace("Registering Commands & ArgumentParsers...");

                this.providerRegistry
                        .getUnchecked(ICommandManager.class)
                        .getParamTypeRegistry()
                        .register(
                                new PlayerParamType(),
                                new TaskParamType(),
                                new ServiceParamType(),
                                new NodeParamType()
                        );

                this.providerRegistry
                        .getUnchecked(ICommandManager.class)
                        .registerCommandsSeperately(
                                new ClearCommand(),
                                new ShutdownCommand(),
                                new LoggerCommand(),
                                new ServiceCommand(),
                                new TickCommand(),
                                new PlayerCommand(),
                                new ClusterCommand(),
                                new NodeCommand(),
                                new TaskCommand(),
                                new ModuleCommand()
                        );

                this.logger.trace("§8");

                this.providerRegistry.getUnchecked(INetworkDocumentStorage.class).set("cloud::messages", this.configManager.getConfig().getMessages());
                this.providerRegistry.getUnchecked(INetworkDocumentStorage.class).update();

                //registering packet handlers
                this.logger.trace("Registering Packets & Handlers...");
                this.networkExecutor.registerPacketHandler(new NodeRedirectPacketHandler());
                this.networkExecutor.registerPacketHandler(new NodeDataCycleHandler());
                this.networkExecutor.registerPacketHandler(new NodeOfflinePlayerPacketHandler());
                this.networkExecutor.registerPacketHandler(new NodeModulePacketHandler());
                this.networkExecutor.registerPacketHandler(new NodeModuleControllerPacketHandler());
                this.networkExecutor.registerPacketHandler(new NodeStoragePacketHandler());
                this.networkExecutor.registerPacketHandler(new NodeLoggingPacketHandler());
                this.networkExecutor.registerPacketHandler(new NodeServiceShutdownHandler());
                this.networkExecutor.registerPacketHandler(new NodePlayerCommandHandler());
                this.networkExecutor.registerPacketHandler(new NodeServiceConfigureHandler());
                this.networkExecutor.registerPacketHandler(new NodeSyncPacketHandler());

                //remote packet handlers
                this.networkExecutor.registerUniversalHandler(new NodeRemoteShutdownHandler());
                this.networkExecutor.registerUniversalHandler(new NodeRemoteServerStartHandler());
                this.networkExecutor.registerUniversalHandler(new NodeRemoteServerStopHandler());
                this.networkExecutor.registerUniversalHandler(new NodeRemoteLoggingHandler());
                this.networkExecutor.registerRemoteHandler(new NodeRemoteCacheHandler());

                this.logger.trace("Registered " + PacketProvider.getRegisteredPackets().size() + " Packets & " + this.networkExecutor.getRegisteredPacketHandlers().size() + " PacketHandlers.");
                this.logger.trace("§8");

                //heart-beat execution for time out checking
                TimeOutChecker check = new TimeOutChecker();
                scheduledExecutor.scheduleAtFixedRate(check, 1, 1, TimeUnit.SECONDS);


                logger.info("§8");
                //managing and loading modules
                IModuleManager moduleManager = providerRegistry.getUnchecked(IModuleManager.class);
                moduleManager.setModulesDirectory(MODULE_FOLDER.toPath());
                moduleManager.resolveModules();
                logger.info("§8");
                moduleManager.loadModules();
                logger.info("§8");
                moduleManager.enableModules();
                logger.info("§8");

                // print finish successfully message
                this.logger.info("§8");
                this.logger.info("§8");
                this.logger.info("§8");
                this.logger.info("This Node has successfully booted up and is now ready for use!");
                this.logger.info("=> Thanks for using CookieCloud [Version: " + VersionInfo.getCurrentVersion() + "]");
                this.logger.info("§8");
                this.logger.info("§8");
                this.logger.info("§8");

                //starting service queue
                this.serviceQueue = new NodeServiceQueue();

                //add node cycle data
                scheduledExecutor.scheduleAtFixedRate(this::updateThisSidesClusterParticipant, 1_000, NODE_PUBLISH_INTERVAL, TimeUnit.MILLISECONDS);
                scheduledExecutor.scheduleAtFixedRate(() -> this.networkExecutor.getClient("Application").ifPresent(DriverUpdatePacket::publishUpdate), 1_000, 1, TimeUnit.SECONDS);

                // add a shutdown hook for fast closes
                Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
            }
        });
    }

    private void startSetup() {
        new NodeSetup().start((setup, setupControlState) -> {

            if (setupControlState != SetupControlState.FINISHED) return;
            switch (setup.getDatabaseType()) {
                case FILE:
                    initConfigs(setup, null, null);
                    break;
                case MYSQL:
                    new MySqlSetup(NodeDriver.getInstance().getConsole()).start((mySqlSetup, setupControlState1) -> {
                        if (setupControlState1 != SetupControlState.FINISHED) return;
                        initConfigs(setup, mySqlSetup, null);
                    });
                    break;
                case MONGODB:
                    new MongoDBSetup(NodeDriver.getInstance().getConsole()).start((mongoDBSetup, setupControlState1) -> {
                        if (setupControlState1 != SetupControlState.FINISHED) return;
                        initConfigs(setup, null, mongoDBSetup);
                    });
                    break;
            }

        });
    }

    @Override
    public void logToExecutor(NetworkComponent component, String message, Object... args) {
        message = StringUtils.formatMessage(message, args);
        if (component.matches(this.node)) {
            this.logger.info(message, args);
            return;
        }
        DriverLoggingPacket packet = new DriverLoggingPacket(component, message);
        this.networkExecutor.sendPacketToAll(packet);
    }

    @Override
    public @NotNull INode thisSidesClusterParticipant() {
        return this.node;
    }


    private void initConfigs(NodeSetup setup, MySqlSetup mySqlSetup, MongoDBSetup mongoDBSetup) throws IOException {
        MainConfiguration config = configManager.getConfig();
        DatabaseConfiguration databaseConfiguration = config.getDatabaseConfiguration();
        DefaultNodeConfig nodeConfig = config.getNodeConfig();

        String nodeName = setup.getName();
        String host = setup.getHost();
        int port = setup.getPort();
        boolean remote = setup.isRemote();

        ExceptionallyRunnable finish = () -> {

            nodeConfig.setNodeName(nodeName);
            nodeConfig.setAddress(new ProtocolAddress(host, port));
            nodeConfig.setMemory(setup.getMemory());
            nodeConfig.setRemote(false);

            config.setNodeConfig(nodeConfig);

            DatabaseType databaseType = setup.getDatabaseType();
            String databaseHost = "127.0.0.1";
            int databasePort = 3306;
            String databaseUser = null;
            String databasePassword = "local";
            String databaseName = null;
            String authDatabase = null;
            switch (databaseType) {
                case FILE:
                    databaseHost = "127.0.0.1";
                    databasePort = 4518;
                    databaseName = "cloud";
                    break;
                case MYSQL:
                    databaseHost = mySqlSetup.getDatabaseHost();
                    databasePort = mySqlSetup.getDatabasePort();
                    databaseUser = mySqlSetup.getDatabaseUser();
                    databasePassword = mySqlSetup.getDatabasePassword();
                    databaseName = mySqlSetup.getDatabaseName();
                    authDatabase = "";
                    break;
                case MONGODB:
                    databaseHost = mongoDBSetup.getDatabaseHost();
                    databasePort = mongoDBSetup.getDatabasePort();
                    databaseUser = mongoDBSetup.getDatabaseUser();
                    databasePassword = mongoDBSetup.getDatabasePassword();
                    databaseName = mongoDBSetup.getDatabaseName();
                    authDatabase = mongoDBSetup.getAuthDatabase();
                    break;
            }

            databaseConfiguration.setHost(databaseHost);
            databaseConfiguration.setPort(databasePort);
            databaseConfiguration.setUser(databaseUser);
            databaseConfiguration.setPassword(databasePassword);
            databaseConfiguration.setDatabase(databaseName);
            databaseConfiguration.setAuthDatabase(authDatabase);
            databaseConfiguration.setType(databaseType);
            config.setDatabaseConfiguration(databaseConfiguration);

            configManager.setConfig(config);
            configManager.save();

            if (setup.isDefaultTasks()) {

                String[] args = new String[]{
                        "-XX:+UseG1GC",
                        "-XX:+ParallelRefProcEnabled",
                        "-XX:MaxGCPauseMillis=200",
                        "-XX:+UnlockExperimentalVMOptions",
                        "-XX:+DisableExplicitGC",
                        "-XX:+AlwaysPreTouch",
                        "-XX:G1NewSizePercent=30",
                        "-XX:G1MaxNewSizePercent=40",
                        "-XX:G1HeapRegionSize=8M",
                        "-XX:G1ReservePercent=20",
                        "-XX:G1HeapWastePercent=5",
                        "-XX:G1MixedGCCountTarget=4",
                        "-XX:InitiatingHeapOccupancyPercent=15",
                        "-XX:G1MixedGCLiveThresholdPercent=90",
                        "-XX:G1RSetUpdatingPauseTimePercent=5",
                        "-XX:SurvivorRatio=32",
                        "-XX:+PerfDisableSharedMem",
                        "-XX:MaxTenuringThreshold=1",
                        "-Dusing.aikars.flags=https://mcflags.emc.gs",
                        "-Daikars.new.flags=true",
                        "-XX:-UseAdaptiveSizePolicy",
                        "-XX:CompileThreshold=100",
                        "-Dio.netty.recycler.maxCapacity=0",
                        "-Dio.netty.recycler.maxCapacity.default=0",
                        "-Djline.terminal=jline.UnsupportedTerminal"
                };

                this.providerRegistry.setProvider(IDatabaseManager.class, new DefaultDatabaseManager(databaseType, new DatabaseConfiguration(databaseType, databaseHost, databasePort, databaseName, authDatabase, databaseUser, databasePassword))).ifPresent(databaseManager -> {

                    SectionedDatabase database = databaseManager.getDatabase();
                    database.registerSection("tasks", UniversalServiceTask.class);
                    database.registerSection("groups", DefaultTaskGroup.class);


                    NodeServiceTaskManager taskManager = new NodeServiceTaskManager();

                    DefaultTaskGroup proxyGroup = new DefaultTaskGroup("Proxy", SpecificDriverEnvironment.PROXY, ServiceShutdownBehaviour.DELETE, args, new ArrayList<>(), Collections.singleton(new CloudTemplate("Proxy", "default", "local", true)));
                    DefaultTaskGroup lobbyGroup = new DefaultTaskGroup("Lobby", SpecificDriverEnvironment.MINECRAFT, ServiceShutdownBehaviour.DELETE, args, new ArrayList<>(), Collections.singleton(new CloudTemplate("Lobby", "default", "local", true)));

                    IServiceTask proxyTask = new UniversalServiceTask("Proxy", proxyGroup.getName(), Collections.singletonList(config.getNodeConfig().getNodeName()), "Default CookieCloud Service", "", 1024, 250, 1, -1, 0, true, -1, new SimpleFallback(false, "", 0), ServiceVersion.BUNGEECORD, new ArrayList<>());
                    IServiceTask lobbyTask = new UniversalServiceTask("Lobby", lobbyGroup.getName(), Collections.singletonList(config.getNodeConfig().getNodeName()), "Default CookieCloud Service", "", 512, 50, 1, -1, 1, true, -1, new SimpleFallback(true, "", 1), ServiceVersion.SPIGOT_1_8_8, new ArrayList<>());
                    lobbyTask.setProperty("gameServer", true);

                    proxyTask.setProperty("onlineMode", true);
                    proxyTask.setProperty("proxyProtocol", false);

                    taskManager.registerTaskGroup(proxyGroup);
                    taskManager.registerTaskGroup(lobbyGroup);

                    taskManager.registerTask(lobbyTask);
                    taskManager.registerTask(proxyTask);

                    this.logger.info("Created default Proxy & Lobby ServiceTasks!");
                    this.logger.info("§7You §acompleted §7the NodeSetup§8!");
                    this.logger.info("Please reboot the Node now to apply all changes!");
                    System.exit(0);
                });
                return;
            }

            this.logger.info("§7You §acompleted §7the NodeSetup§8!");
            this.logger.info("Please reboot the Node now to apply all changes!");
            System.exit(0);
        };

        if (remote) {
            config.setHttpListeners(new ProtocolAddress[0]);
            new NodeRemoteSetup(NodeDriver.getInstance().getConsole()).start((setup1, state) -> {
                if (state == SetupControlState.FINISHED) {
                    String host1 = setup1.getHost();
                    int port1 = setup1.getPort();
                    String authKey = setup1.getAuthKey();

                    nodeConfig.setAuthKey(authKey);
                    nodeConfig.setClusterAddresses(new ProtocolAddress[]{new ProtocolAddress(host1, port1, authKey)});
                    finish.run();
                }
            });
        } else {
            finish.run();
        }


    }

    /**
     * Migrates from the Node with the provided name to this node
     * and transfers all the data from the old node to this node instance
     *
     * @param disconnectedNodeName the name of the old head node that left
     */
    public void markAsHeadNode(String disconnectedNodeName) {
        this.node.getConfig().setRemote(); //making this node head node

        this.updateThisSidesClusterParticipant();
    }

    @Override
    public void updateThisSidesClusterParticipant() {
        this.networkExecutor.sendPacketToAll(
                new NodeCycleDataPacket(
                        this.node.getConfig().getNodeName(),
                        this.node.getLastCycleData()
                )
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends IBufferObject> ISyncedNetworkPromise<E> getSyncedNetworkObject(SyncedObjectType<E> type, String queryParameters) {
        NodeSyncedNetworkPromise<E> promise = new NodeSyncedNetworkPromise<>();
        switch (type.getId()) {
            case 0x00:
                ICloudPlayerManager pm = this.providerRegistry.getUnchecked(ICloudPlayerManager.class);

                //check if uuid or name provided
                if (Validation.UNIQUEID.matches(queryParameters)) {
                    UUID uniqueId = UUID.fromString(queryParameters);
                    promise.setObject((E) pm.getCloudPlayerByUniqueIdOrNull(uniqueId));
                } else {
                    promise.setObject((E) pm.getCloudPlayerByNameOrNull(queryParameters));
                }
                break;
            case 0x01:
                ICloudServiceManager sm = this.providerRegistry.getUnchecked(ICloudServiceManager.class);

                //check if uuid or name provided
                if (Validation.UNIQUEID.matches(queryParameters)) {
                    UUID uniqueId = UUID.fromString(queryParameters);
                    promise.setObject((E) sm.getService(queryParameters));
                } else {
                    promise.setObject((E) sm.getService(queryParameters));
                }
                break;

            case 0x02:
                ICloudServiceTaskManager tm = this.providerRegistry.getUnchecked(ICloudServiceTaskManager.class);
                promise.setObject((E) tm.getTaskOrNull(queryParameters));
                break;

            case 0x03:
                ICloudServiceTaskManager tm1 = this.providerRegistry.getUnchecked(ICloudServiceTaskManager.class);
                promise.setObject((E) tm1.getTaskGroupOrNull(queryParameters));
                break;

            case 0x04:
                INodeManager nm = this.providerRegistry.getUnchecked(INodeManager.class);
                promise.setObject((E) nm.getNodeByNameOrNull(queryParameters));
                break;
        }

        return promise;
    }

    @Override
    public @NotNull <E extends IBufferObject> Task<ISyncedNetworkPromise<E>> getSyncedNetworkObjectAsync(SyncedObjectType<E> type, String queryParameters) {
        return Task.callAsync(() -> getSyncedNetworkObject(type, queryParameters));
    }

    @Override
    public void shutdown() {
        if (!this.running) {
            return;
        }

        INodeManager nodeManager = providerRegistry.getUnchecked(INodeManager.class);

        if (nodeManager.isHeadNode() && nodeManager.getAllCachedNodes().size() > 1) {
            this.logger.warn("§eThis Node is the §cHeadNode §eright now and it's not possible for HeadNodes to shutdown because the migration of SubNodes to HeadNodes is not finished yet!");
            this.logger.warn("Make sure to shutdown every other Node first and then shutdown this Node!");
            return;
        }

        this.running = false;
        this.providerRegistry.getUnchecked(ICommandManager.class).setActive(false);


        this.logger.info("§7Trying to terminate the §cCloudsystem§8...");
        PlayerExecutor.forAll().disconnect("§cThe network was shut down!");

        Task.runTaskLater(() -> {


            //shutting down servers
            for (ICloudServer service : new ArrayList<>(this.providerRegistry.getUnchecked(ICloudServiceManager.class).getAllCachedServices())) {
                IProcessCloudServer cloudServer = ((IProcessCloudServer) service);
                Process process = cloudServer.getProcess();
                if (process != null) {
                    process.destroyForcibly();

                }
            }

            IModuleManager moduleManager = providerRegistry.getUnchecked(IModuleManager.class);

            moduleManager.disableModules();
            moduleManager.unregisterModules();

            this.webServer.shutdown();

            logger.info("Terminating in §8[§c3§8]");
            Task.runTaskLater(() -> logger.info("Terminating in §8[§c2§8]"), TimeUnit.SECONDS, 1);
            Task.runTaskLater(() -> logger.info("Terminating in §8[§c1§8]"), TimeUnit.SECONDS, 2);

            //Shutting down networking and database
            Task.multiTasking(this.networkExecutor.shutdown(), this.providerRegistry.getUnchecked(IDatabaseManager.class).shutdown()).registerListener(wrapper -> {
                Task.runTaskLater(() -> {
                    FileUtils.delete(NodeDriver.SERVICE_DIR_DYNAMIC.toPath());
                    FileUtils.delete(NodeDriver.STORAGE_TEMP_FOLDER.toPath());

                    logger.info("§aSuccessfully exited the CloudSystem§8!");
                    System.exit(0);
                }, TimeUnit.SECONDS, 3);
            });
        }, TimeUnit.SECONDS, 1);
    }

    public String getBaseUrl() {
        VersionInfo newestVersion = VersionInfo.getNewestVersion();

        String urlString = "https://github.com/CookieMC337/CookieCloud-NEW/releases/download/v{version}";
        urlString = urlString.replace("{version}", String.valueOf(newestVersion.getVersion()));
        urlString = urlString.replace("{type}", String.valueOf(newestVersion.getType()));

        return urlString;
    }
}
