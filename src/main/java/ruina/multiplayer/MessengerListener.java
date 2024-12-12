package ruina.multiplayer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.util.AdditionalIntent;
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
            ArrayList<EnemyMoveInfo> additionalMoves = (ArrayList<EnemyMoveInfo>)dataIn[0];
            ArrayList<ArrayList<Byte>> additionalMovesHistory= (ArrayList<ArrayList<Byte>>)dataIn[1];
            ArrayList<AdditionalIntent> additionalIntents = (ArrayList<AdditionalIntent>)dataIn[2];
            String monsterID = (String)dataIn[3];
            NetworkLocation requestLocation = (NetworkLocation)dataIn[4];
            NetworkMultiIntentMonster mo = (NetworkMultiIntentMonster) RoomDataManager.GetMonsterForLocation(monsterID, requestLocation);
            if (mo != null) {
                mo.additionalMoves = additionalMoves;
                mo.additionalMovesHistory = additionalMovesHistory;
                mo.additionalIntents = additionalIntents;
            }

            if (requestLocation.isSameRoomAndAction()) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    if (m instanceof AbstractMultiIntentMonster) {
                        ((AbstractMultiIntentMonster)m).additionalMoves = additionalMoves;
                        ((AbstractMultiIntentMonster)m).additionalMovesHistory = additionalMovesHistory;
                        ((AbstractMultiIntentMonster)m).additionalIntents = additionalIntents;
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
