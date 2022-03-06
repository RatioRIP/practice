package cc.ratio.practice;

import cc.ratio.practice.arena.ArenaRepository;
import cc.ratio.practice.command.arena.ArenaCommandsModule;
import cc.ratio.practice.command.kit.KitCommandsModule;
import cc.ratio.practice.event.environment.EnvironmentListener;
import cc.ratio.practice.event.lobby.LobbyListener;
import cc.ratio.practice.kit.KitRepository;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.mongo.Mongo;
import me.lucko.helper.mongo.MongoDatabaseCredentials;
import me.lucko.helper.mongo.MongoProvider;
import me.lucko.helper.mongo.plugin.HelperMongo;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.configuration.ConfigurationSection;

public class Practice extends ExtendedJavaPlugin implements MongoProvider {

    public static Practice instance;

    private MongoDatabaseCredentials credentials;
    private Mongo dataSource;

    @Override
    protected void enable() {
        instance = this;

        this.saveDefaultConfig();

        this.credentials = MongoDatabaseCredentials.fromConfig(this.getConfig());
        this.dataSource = this.getMongo(this.credentials);
        this.dataSource.bindWith(this);

        this.provideService(MongoProvider.class, this);
        this.provideService(MongoDatabaseCredentials.class, this.credentials);
        this.provideService(Mongo.class, this.dataSource);

        this.provideService(ConfigurationSection.class, this.getConfig());

        this.provideService(KitRepository.class, new KitRepository());
        this.provideService(ArenaRepository.class, new ArenaRepository());
        this.provideService(ProfileRepository.class, new ProfileRepository());
        this.provideService(QueueRepository.class, new QueueRepository());

        this.bindModule(new KitCommandsModule());
        this.bindModule(new ArenaCommandsModule());

        this.bindModule(new EnvironmentListener());
        this.bindModule(new LobbyListener());
    }

    @Override
    public Mongo getMongo() {
        return this.dataSource;
    }

    @Override
    public Mongo getMongo(final MongoDatabaseCredentials credentials) {
        return new HelperMongo(credentials);
    }

    @Override
    public MongoDatabaseCredentials getGlobalCredentials() {
        return this.credentials;
    }
}
