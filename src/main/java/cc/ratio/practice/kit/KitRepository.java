package cc.ratio.practice.kit;

import cc.ratio.practice.util.Repository;
import me.lucko.helper.Services;
import me.lucko.helper.mongo.Mongo;
import me.lucko.helper.mongo.external.morphia.Datastore;

import java.util.ArrayList;
import java.util.Optional;

public class KitRepository implements Repository<Kit, String> {

    public final ArrayList<Kit> kits;

    private Mongo mongo = Services.get(Mongo.class).get();
    private Datastore datastore = mongo.getMorphiaDatastore();

    public KitRepository() {
        this.kits = new ArrayList<>();

        this.load();
    }

    public void load() {
        this.datastore.find(Kit.class).forEach(this.kits::add);
    }

    @Override
    public boolean put(Kit kit) {
        boolean result = this.kits.add(kit);

        this.datastore.save(kit);

        return result;
    }

    @Override
    public boolean remove(Kit kit) {
        boolean result = this.kits.remove(kit);

        this.datastore.delete(kit);

        return result;
    }

    @Override
    public Optional<Kit> find(String identifier) {
        return this.kits.stream().filter(kit -> kit.name.equalsIgnoreCase(identifier)).findFirst();
    }

    public void save(Kit kit) {
        this.datastore.save(kit);
    }
}
