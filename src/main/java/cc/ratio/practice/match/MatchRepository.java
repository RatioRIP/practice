package cc.ratio.practice.match;

import cc.ratio.practice.util.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class MatchRepository implements Repository<Match, UUID> {

    public final ArrayList<Match> matches;

    public MatchRepository() {
        this.matches = new ArrayList<>();
    }

    @Override
    public boolean put(final Match match) {
        return this.matches.add(match);
    }

    @Override
    public boolean remove(final Match match) {
        return this.matches.remove(match);
    }

    @Override
    public Optional<Match> find(final UUID identifier) {
        return this.matches.stream().filter(match -> match.uuid == identifier).findFirst();
    }
}
