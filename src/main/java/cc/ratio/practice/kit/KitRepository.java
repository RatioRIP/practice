package cc.ratio.practice.kit;

import cc.ratio.practice.Practice;
import cc.ratio.practice.util.Repository;
import com.google.common.reflect.TypeToken;
import me.lucko.helper.serialize.GsonStorageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KitRepository implements Repository<Kit, String> {

    public final List<Kit> kits;
    public final GsonStorageHandler<List<Kit>> storageHandler;

    public KitRepository() {
        this.storageHandler = new GsonStorageHandler(
                "kits",
                ".json",
                Practice.instance.getDataFolder(),
                new TypeToken<List<Kit>>(){}
        );

        this.kits = this.storageHandler.load().orElseGet(() -> new ArrayList());
    }

    @Override
    public boolean put(Kit kit) {
        boolean result = this.kits.add(kit);

        this.storageHandler.save(this.kits);
        this.save();

        return result;
    }

    @Override
    public boolean remove(Kit kit) {
        boolean result = this.kits.remove(kit);

        this.storageHandler.save(this.kits);
        this.save();

        return result;
    }

    @Override
    public Optional<Kit> find(String identifier) {
        return this.kits.stream().filter(kit -> kit.name.equalsIgnoreCase(identifier)).findFirst();
    }

    public void save() {
        this.storageHandler.save(this.kits);
    }
}
