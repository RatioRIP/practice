package cc.ratio.practice.match.team;

import cc.ratio.practice.profile.Profile;
import cc.ratio.practice.profile.ProfileRepository;
import me.lucko.helper.Services;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team extends ArrayList<UUID> {

    private static final ProfileRepository profileRepository = Services.get(ProfileRepository.class).get();

    public Team(UUID... uuids) {
        Collections.addAll(this, uuids);
    }

    public List<Profile> toProfiles() {
        return this.stream()
                .map(uuid -> profileRepository.find(uuid).get())
                .collect(Collectors.toList());
    }

    public String formatName(ChatColor color) {
        return this.toProfiles()
                .stream()
                .map(Profile::toPlayer)
                .map(Player::getName)
                .map(s -> color + s)
                .collect(Collectors.joining(ChatColor.GRAY + ", "));
    }
}
