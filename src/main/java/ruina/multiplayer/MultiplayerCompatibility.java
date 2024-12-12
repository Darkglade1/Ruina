package ruina.multiplayer;

import spireTogether.SpireTogetherMod;

public class MultiplayerCompatibility {
    public static void addSubscribers() {
        SpireTogetherMod.subscribe(new NoMultiplyPowerList());
        SpireTogetherMod.subscribe(new MultiplayerSubscriber());
        SpireTogetherMod.subscribe(new MessengerListener());
    }
}
