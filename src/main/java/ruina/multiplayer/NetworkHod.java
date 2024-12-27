package ruina.multiplayer;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.uninvitedGuests.normal.greta.Hod;

import java.io.Serializable;

public class NetworkHod extends NetworkRuinaMonster implements Serializable {
    static final long serialVersionUID = 1L;

    public int stance;
    public byte slashMove;
    public byte pierceMove;
    public byte guardMove;

    public static String request_updateHod = "ruina_updateHod";
    public static String request_changeStanceHod = "ruina_changeStanceHod";

    protected NetworkHod(Hod mo) {
        super(mo);
        this.stance = mo.stance;
        this.slashMove = mo.slashMove;
        this.pierceMove = mo.pierceMove;
        this.guardMove = mo.guardMove;
    }

    @Override
    public void preMonsterPrepare(AbstractMonster monster) {
        super.preMonsterPrepare(monster);
        Hod mo = (Hod)monster;
        mo.stance = this.stance;
        mo.slashMove = this.slashMove;
        mo.pierceMove = this.pierceMove;
        mo.guardMove = this.guardMove;
    }
}
