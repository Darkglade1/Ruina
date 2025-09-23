//package ruina.multiplayer;
//
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;
//
//import java.io.Serializable;
//
//public class NetworkYesod extends NetworkRuinaMonster implements Serializable {
//    static final long serialVersionUID = 1L;
//    public float currentDamageBonus;
//
//    public static String request_updateYesod = "ruina_updateYesod";
//
//    protected NetworkYesod(Yesod mo) {
//        super(mo);
//        this.currentDamageBonus = mo.currentDamageBonus;
//    }
//
//    @Override
//    public void preMonsterPrepare(AbstractMonster monster) {
//        super.preMonsterPrepare(monster);
//        Yesod mo = (Yesod)monster;
//        mo.currentDamageBonus = this.currentDamageBonus;
//    }
//}
