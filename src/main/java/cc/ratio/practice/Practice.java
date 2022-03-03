package cc.ratio.practice;

import cc.ratio.practice.command.kit.KitCommandsModule;
import cc.ratio.practice.event.environment.EnvironmentListener;
import cc.ratio.practice.kit.KitRepository;
import cc.ratio.practice.profile.ProfileRepository;
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
        this.dataSource = getMongo(this.credentials);
        this.dataSource.bindWith(this);

        this.provideService(MongoProvider.class, this);
        this.provideService(MongoDatabaseCredentials.class, this.credentials);
        this.provideService(Mongo.class, this.dataSource);

        this.provideService(ConfigurationSection.class, this.getConfig());

        this.provideService(KitRepository.class, new KitRepository());
        this.provideService(ProfileRepository.class, new ProfileRepository());

        this.bindModule(new EnvironmentListener());
        this.bindModule(new KitCommandsModule());
    }

    @Override
    public Mongo getMongo() {
        return this.dataSource;
    }

    @Override
    public Mongo getMongo(MongoDatabaseCredentials credentials) {
        return new HelperMongo(credentials);
    }

    @Override
    public MongoDatabaseCredentials getGlobalCredentials() {
        return this.credentials;
    }
}
