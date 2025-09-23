//package ruina.multiplayer;
//
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//import ruina.monsters.act3.Twilight;
//
//import java.io.Serializable;
//
//public class NetworkTwilight extends NetworkRuinaMonster implements Serializable {
//    static final long serialVersionUID = 1L;
//
//    public int dmgTaken;
//    public boolean bigEggBroken;
//    public boolean smallEggBroken;
//    public boolean longEggBroken;
//
//    public static String request_updateTwilight = "ruina_updateTwilight";
//
//    protected NetworkTwilight(Twilight mo) {
//        super(mo);
//        this.dmgTaken = mo.dmgTaken;
//        this.bigEggBroken = mo.bigEggBroken;
//        this.smallEggBroken = mo.smallEggBroken;
//        this.longEggBroken = mo.longEggBroken;
//    }
//
//    @Override
//    public void preMonsterPrepare(AbstractMonster monster) {
//        super.preMonsterPrepare(monster);
//        Twilight mo = (Twilight)monster;
//        mo.dmgTaken = this.dmgTaken;
//        mo.bigEggBroken = this.bigEggBroken;
//        mo.smallEggBroken = this.smallEggBroken;
//        mo.longEggBroken = this.longEggBroken;
//    }
//}
