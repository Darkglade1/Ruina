package ruina.multiplayer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.AdditionalIntent;
import spireTogether.networkcore.objects.entities.NetworkIntent;
import spireTogether.networkcore.objects.rooms.NetworkLocation;
import spireTogether.other.RoomDataManager;
import spireTogether.subscribers.TiSNetworkMessageSubscriber;
import spireTogether.util.NetworkMessage;

import java.util.ArrayList;

public class MessengerListener implements TiSNetworkMessageSubscriber {
    @Override
    public void onMessageReceive(NetworkMessage networkMessage, String s, Object o, Integer integer) {
        if (networkMessage.request.equals(NetworkMultiIntentMonster.request_monsterUpdateAdditionalIntents)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            ArrayList<NetworkIntent> additionalMoves = (ArrayList<NetworkIntent>)dataIn[0];
            ArrayList<ArrayList<Byte>> additionalMovesHistory= (ArrayList<ArrayList<Byte>>)dataIn[1];
            String monsterID = (String)dataIn[2];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[3];
            NetworkMultiIntentMonster mo = (NetworkMultiIntentMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.additionalMoves = additionalMoves;
                mo.additionalMovesHistory = additionalMovesHistory;
            }

            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof AbstractMultiIntentMonster) {
                        ((AbstractMultiIntentMonster)m).additionalMoves.clear();
                        ((AbstractMultiIntentMonster)m).additionalIntents.clear();
                        for (NetworkIntent intent : additionalMoves) {
                            ((AbstractMultiIntentMonster)m).additionalMoves.add(intent.toStandard());
                            AdditionalIntent additionalIntent = new AdditionalIntent(m, intent.toStandard());
                            ((AbstractMultiIntentMonster)m).additionalIntents.add(additionalIntent);
                        }
                        ((AbstractMultiIntentMonster)m).additionalMovesHistory = additionalMovesHistory;
                    }
                }
            }
        }
        if (networkMessage.request.equals(NetworkRuinaMonster.request_monsterUpdateFirstMove)) {
            Object[] dataIn = (Object[]) networkMessage.object;
            boolean firstMove = (boolean)dataIn[0];
            String monsterID = (String)dataIn[1];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[2];
            NetworkRuinaMonster mo = (NetworkRuinaMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.firstMove = firstMove;
            }

            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof AbstractRuinaMonster) {
                        ((AbstractRuinaMonster)m).firstMove = firstMove;
                    }
                }
            }
        }
    }
}
