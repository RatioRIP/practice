package cc.ratio.practice.profile;

import cc.ratio.practice.util.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class ProfileRepository implements Repository<Profile, UUID> {

    public final ArrayList<Profile> profiles;

    public ProfileRepository() {
        this.profiles = new ArrayList<>();
    }

    @Override
    public boolean put(Profile profile) {
        return this.profiles.add(profile);
    }

    @Override
    public boolean remove(Profile profile) {
        return this.profiles.remove(profile);
    }

    @Override
    public Optional<Profile> find(UUID identifier) {
        return this.profiles.stream().filter(profile -> profile.uuid == identifier).findFirst();
    }

}
