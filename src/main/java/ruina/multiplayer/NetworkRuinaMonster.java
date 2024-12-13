package ruina.multiplayer;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractRuinaMonster;
import spireTogether.networkcore.objects.entities.NetworkMonster;

import java.io.Serializable;

public class NetworkRuinaMonster extends NetworkMonster implements Serializable {
    static final long serialVersionUID = 1L;
    public boolean firstMove;
    public static String request_monsterUpdateFirstMove = "ruina_monsterUpdateFirstMove";

    protected NetworkRuinaMonster(AbstractRuinaMonster mo) {
        super(mo);
        this.firstMove = mo.firstMove;
    }

    public void postMonsterPrepare(AbstractMonster monster) {
        super.postMonsterPrepare(monster);
        AbstractRuinaMonster mo = (AbstractRuinaMonster)monster;
        mo.firstMove = this.firstMove;
    }
}
