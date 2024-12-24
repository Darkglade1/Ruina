package ruina.multiplayer;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import ruina.monsters.AbstractRuinaMonster;
import spireTogether.networkcore.objects.entities.NetworkIntent;
import spireTogether.networkcore.objects.entities.NetworkMonster;

import java.io.Serializable;
import java.util.ArrayList;

public class NetworkRuinaMonster extends NetworkMonster implements Serializable {
    static final long serialVersionUID = 1L;
    public NetworkIntent networkMove;
    public ArrayList<Byte> moveHistory;
    public boolean firstMove;
    public int phase;
    public static String request_monsterUpdateMainIntent = "ruina_monsterUpdateMainIntent";
    public static String request_monsterUpdateFirstMove = "ruina_monsterUpdateFirstMove";
    public static String request_monsterUpdatePhase = "ruina_monsterUpdatePhase";

    protected NetworkRuinaMonster(AbstractRuinaMonster mo) {
        super(mo);
        EnemyMoveInfo move = ReflectionHacks.getPrivate(mo, AbstractMonster.class, "move");
        this.networkMove = new NetworkIntent(move);
        this.moveHistory = mo.moveHistory;
        this.firstMove = mo.firstMove;
        this.phase = mo.phase;
    }

    @Override
    public void preMonsterPrepare(AbstractMonster monster) {
        super.preMonsterPrepare(monster);
        AbstractRuinaMonster mo = (AbstractRuinaMonster)monster;
        ReflectionHacks.setPrivate(mo, AbstractMonster.class, "move", networkMove.toStandard());
        mo.moveHistory = this.moveHistory;
        mo.firstMove = this.firstMove;
        mo.phase = this.phase;
    }
}
