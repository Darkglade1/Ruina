package ruina.powers.act4;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.uninvitedGuests.normal.eileen.Yesod;
import ruina.powers.AbstractUnremovablePower;

import static ruina.util.Wiz.adp;

public class DarkBargain extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(DarkBargain.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DarkBargain(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
        updateDescription();
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (target == adp() && damageAmount <= 0 && info.type == DamageInfo.DamageType.NORMAL && owner instanceof Yesod) {
            Yesod yesod = (Yesod)owner;
            onSpecificTrigger();
//            if (RuinaMod.isMultiplayerConnected()) {
//                P2PManager.SendData(MessengerListener.request_yesodGainedDamage, SpireHelp.Gameplay.CreatureToUID(owner), SpireHelp.Gameplay.GetMapLocation());
//                P2PManager.SendData(NetworkYesod.request_updateYesod, yesod.currentDamageBonus, SpireHelp.Gameplay.CreatureToUID(owner), SpireHelp.Gameplay.GetMapLocation());
//                NetworkMonster m = RoomDataManager.GetMonsterForCurrentRoom((AbstractMonster) owner);
//                if (m instanceof NetworkYesod) {
//                    ((NetworkYesod)m).currentDamageBonus = yesod.currentDamageBonus;
//                }
//            }
        }
    }

    @Override
    public void onSpecificTrigger() {
        if (owner instanceof Yesod) {
            flash();
            Yesod yesod = (Yesod)owner;
            yesod.currentDamageBonus += yesod.currDamageGrowth;
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        if (owner instanceof Yesod) {
            Yesod yesod = (Yesod)owner;
            description = POWER_DESCRIPTIONS[0] + yesod.currentDamageBonus + POWER_DESCRIPTIONS[1] + yesod.currDamageGrowth + POWER_DESCRIPTIONS[2];
        }
    }
}
