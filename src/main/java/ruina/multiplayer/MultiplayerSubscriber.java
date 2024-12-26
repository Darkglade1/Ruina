package ruina.multiplayer;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.act3.Twilight;
import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;
import spireTogether.networkcore.objects.NetworkObject;
import spireTogether.networkcore.objects.entities.NetworkMonster;
import spireTogether.subscribers.TiSCustomSerializationSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

public class MultiplayerSubscriber implements TiSCustomSerializationSubscriber {
    @Override
    public HashMap<Class<?>, Class<? extends NetworkObject<?>>> getCustomSerializers() {
        return null;
    }

    @Override
    public HashMap<Class<? extends AbstractMonster>, Class<? extends NetworkMonster>> getCustomMonsterSerializers() {
        HashMap<Class<? extends AbstractMonster>, Class<? extends NetworkMonster>> map = new HashMap<>();
        map.put(AbstractMultiIntentMonster.class, NetworkMultiIntentMonster.class);
        map.put(AbstractRuinaMonster.class, NetworkRuinaMonster.class);
        map.put(Twilight.class, NetworkTwilight.class);
        map.put(Yesod.class, NetworkYesod.class);
        return map;
    }

    @Override
    public ArrayList<Class<?>> getDefaultSerializableClasses() {
        return new ArrayList<>();
    }
}
