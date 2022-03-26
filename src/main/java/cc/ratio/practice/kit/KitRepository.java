package cc.ratio.practice.kit;

import cc.ratio.practice.queue.Queue;
import cc.ratio.practice.queue.QueueRepository;
import cc.ratio.practice.util.Repository;
import cc.ratio.practice.util.Tuple;
import me.lucko.helper.Services;
import me.lucko.helper.mongo.Mongo;
import me.lucko.helper.mongo.external.morphia.Datastore;

import java.util.ArrayList;
import java.util.Optional;

public class KitRepository implements Repository<Kit, String> {

    public final ArrayList<Kit> kits;

    private final Mongo mongo = Services.get(Mongo.class).get();
    private final Datastore datastore = this.mongo.getMorphiaDatastore();

    private static final QueueRepository queueRepository = Services.get(QueueRepository.class).get();

    public KitRepository() {
        this.kits = new ArrayList<>();

        this.load();
    }

    public void load() {
        this.datastore.find(Kit.class).forEach(kit -> {
            this.kits.add(kit);
            this.createQueue(kit);
        });
    }

    @Override
    public boolean put(Kit kit) {
        boolean result = this.kits.add(kit);

        this.datastore.save(kit);
        this.createQueue(kit);

        return result;
    }

    @Override
    public boolean remove(Kit kit) {
        boolean result = this.kits.remove(kit);

        this.datastore.delete(kit);
        this.deleteQueue(kit);

        return result;
    }

    private void deleteQueue(Kit kit) {
        this.deleteQueue(kit, true);
        this.deleteQueue(kit, false);
    }

    private void deleteQueue(Kit kit, boolean ranked) {
        Optional<Queue> optionalQueue = queueRepository.find(new Tuple<>(kit, ranked));

        optionalQueue.ifPresent(queueRepository::remove);
    }

    private void createQueue(Kit kit) {
        Queue rankedQueue = new Queue(kit, true);
        Queue unrankedQueue = new Queue(kit, false);

        queueRepository.put(rankedQueue);
        queueRepository.put(unrankedQueue);
    }

    @Override
    public Optional<Kit> find(String identifier) {
        return this.kits.stream().filter(kit -> kit.name.equalsIgnoreCase(identifier)).findFirst();
    }

    public void save(Kit kit) {
        this.datastore.save(kit);
    }
}
