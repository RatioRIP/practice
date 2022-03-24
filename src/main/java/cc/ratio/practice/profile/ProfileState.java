package cc.ratio.practice.profile;

public enum ProfileState {

    LOBBY,
    QUEUE,
    PLAYING,
    SPECTATING;

    public boolean isLobby() {
        return this == LOBBY || this == QUEUE || this == SPECTATING;
    }
}
