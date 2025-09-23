//package ruina.multiplayer;
//
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
//import ruina.monsters.AbstractMultiIntentMonster;
//import ruina.util.AdditionalIntent;
//import spireTogether.networkcore.objects.entities.NetworkIntent;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//
//public class NetworkMultiIntentMonster extends NetworkRuinaMonster implements Serializable {
//    static final long serialVersionUID = 1L;
//    public ArrayList<NetworkIntent> additionalMoves;
//    public ArrayList<ArrayList<Byte>> additionalMovesHistory;
//    public static String request_monsterUpdateAdditionalIntents = "ruina_monsterUpdateAdditionalIntents";
//
//    protected NetworkMultiIntentMonster(AbstractMultiIntentMonster mo) {
//        super(mo);
//        this.additionalMoves = new ArrayList<>();
//        for (EnemyMoveInfo info : mo.additionalMoves) {
//            additionalMoves.add(new NetworkIntent(info));
//        }
//        this.additionalMovesHistory = mo.additionalMovesHistory;
//    }
//
//    @Override
//    public void preMonsterPrepare(AbstractMonster monster) {
//        super.preMonsterPrepare(monster);
//        AbstractMultiIntentMonster mo = (AbstractMultiIntentMonster)monster;
//        mo.additionalMoves.clear();
//        mo.additionalIntents.clear();
//        for (NetworkIntent intent : this.additionalMoves) {
//            mo.additionalMoves.add(intent.toStandard());
//            AdditionalIntent additionalIntent = new AdditionalIntent(mo, intent.toStandard());
//            mo.additionalIntents.add(additionalIntent);
//        }
//        mo.additionalMovesHistory = this.additionalMovesHistory;
//    }
//}
