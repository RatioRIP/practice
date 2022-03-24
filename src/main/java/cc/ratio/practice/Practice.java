package cc.ratio.practice;

import cc.ratio.practice.arena.ArenaRepository;
import cc.ratio.practice.command.arena.ArenaCommandsModule;
import cc.ratio.practice.command.env.EnvCommandsModule;
import cc.ratio.practice.command.kit.KitCommandsModule;
import cc.ratio.practice.event.environment.EnvironmentListener;
import cc.ratio.practice.event.lobby.LobbyListener;
import cc.ratio.practice.event.match.MatchListener;
import cc.ratio.practice.kit.KitRepository;
import cc.ratio.practice.match.MatchRepository;
import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import cc.ratio.practice.queue.QueueRepository;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;
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

        this.provideService(ProfileRepository.class, new ProfileRepository());
        this.provideService(QueueRepository.class, new QueueRepository());
        this.provideService(KitRepository.class, new KitRepository());
        this.provideService(MatchRepository.class, new MatchRepository());
        this.provideService(ArenaRepository.class, new ArenaRepository());

        this.bindModule(new KitCommandsModule());
        this.bindModule(new ArenaCommandsModule());
        this.bindModule(new EnvCommandsModule());

        this.bindModule(new EnvironmentListener());
        this.bindModule(new LobbyListener());
        this.bindModule(new MatchListener());

        {
            ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();
            Schedulers.async().runRepeating(() -> {

                profileRepository.profiles.forEach(Profile::scoreboardUpdate);

            }, 20L, 20L);
        }
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
