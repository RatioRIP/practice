package cc.ratio.practice.arena;

import cc.ratio.practice.Practice;
import cc.ratio.practice.util.Repository;
import com.google.common.reflect.TypeToken;
import me.lucko.helper.serialize.GsonStorageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArenaRepository implements Repository<Arena, String> {

    public final List<Arena> arenas;
    public final GsonStorageHandler<List<Arena>> storageHandler;

    public ArenaRepository() {
        this.storageHandler = new GsonStorageHandler(
                "arenas",
                ".json",
                Practice.instance.getDataFolder(),
                new TypeToken<List<Arena>>(){}
        );

        this.arenas = this.storageHandler.load().orElseGet(() -> new ArrayList());
    }

    @Override
    public boolean put(Arena arena) {
        boolean result = this.arenas.add(arena);

        this.save();

        return result;
    }

    @Override
    public boolean remove(Arena arena) {
        boolean result = this.arenas.remove(arena);

        this.save();

        return result;
    }

    @Override
    public Optional<Arena> find(String identifier) {
        return this.arenas.stream().filter(arena -> arena.name.equalsIgnoreCase(identifier)).findFirst();
    }

    public void save() {
        this.storageHandler.save(this.arenas);
    }
}
