package ruina.multiplayer;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.util.AdditionalIntent;
import spireTogether.networkcore.objects.entities.NetworkMonster;

import java.io.Serializable;
import java.util.ArrayList;

@AutoAdd.Ignore
public class NetworkMultiIntentMonster extends NetworkMonster implements Serializable {
    static final long serialVersionUID = 1L;
    public ArrayList<EnemyMoveInfo> additionalMoves;
    public ArrayList<ArrayList<Byte>> additionalMovesHistory;
    public ArrayList<AdditionalIntent> additionalIntents;
    public static String request_monsterUpdateAdditionalIntents = "ruina_monsterUpdateAdditionalIntents";

    protected NetworkMultiIntentMonster(AbstractMultiIntentMonster mo) {
        super(mo);
        this.additionalMoves = mo.additionalMoves;
        this.additionalMovesHistory = mo.additionalMovesHistory;
        this.additionalIntents = mo.additionalIntents;
    }

    public void postMonsterPrepare(AbstractMonster monster) {
        super.postMonsterPrepare(monster);
        AbstractMultiIntentMonster mo = (AbstractMultiIntentMonster)monster;
        mo.additionalMoves = this.additionalMoves;
        mo.additionalMovesHistory = this.additionalMovesHistory;
        mo.additionalIntents = this.additionalIntents;
    }
}
