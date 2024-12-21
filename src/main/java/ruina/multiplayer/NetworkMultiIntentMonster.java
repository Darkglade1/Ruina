package ruina.multiplayer;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.util.AdditionalIntent;
import spireTogether.networkcore.objects.entities.NetworkIntent;

import java.io.Serializable;
import java.util.ArrayList;

public class NetworkMultiIntentMonster extends NetworkRuinaMonster implements Serializable {
    static final long serialVersionUID = 1L;
    public NetworkIntent networkMove;
    public ArrayList<Byte> moveHistory;
    public ArrayList<NetworkIntent> additionalMoves;
    public ArrayList<ArrayList<Byte>> additionalMovesHistory;
    public static String request_monsterUpdateAdditionalIntents = "ruina_monsterUpdateAdditionalIntents";

    protected NetworkMultiIntentMonster(AbstractMultiIntentMonster mo) {
        super(mo);
        EnemyMoveInfo move = ReflectionHacks.getPrivate(mo, AbstractMonster.class, "move");
        networkMove = new NetworkIntent(move);
        moveHistory = mo.moveHistory;
        this.additionalMoves = new ArrayList<>();
        for (EnemyMoveInfo info : mo.additionalMoves) {
            additionalMoves.add(new NetworkIntent(info));
        }
        this.additionalMovesHistory = mo.additionalMovesHistory;
    }

    public void postMonsterPrepare(AbstractMonster monster) {
        super.postMonsterPrepare(monster);
        AbstractMultiIntentMonster mo = (AbstractMultiIntentMonster)monster;
        ReflectionHacks.setPrivate(mo, AbstractMonster.class, "move", networkMove.toStandard());
        mo.moveHistory = moveHistory;
        mo.additionalMoves.clear();
        mo.additionalIntents.clear();
        for (NetworkIntent intent : this.additionalMoves) {
            mo.additionalMoves.add(intent.toStandard());
            AdditionalIntent additionalIntent = new AdditionalIntent(mo, intent.toStandard());
            mo.additionalIntents.add(additionalIntent);
        }
        mo.additionalMovesHistory = this.additionalMovesHistory;
    }
}
