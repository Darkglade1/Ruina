package ruina.powers.act2;

import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.act2.knight.Sword;
import ruina.multiplayer.MessengerListener;
import ruina.powers.AbstractUnremovablePower;
import spireTogether.networkcore.P2P.P2PManager;
import spireTogether.util.SpireHelp;

import static ruina.util.Wiz.atb;

public class Worthless extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Worthless.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean hasTriggered = false;

    public Worthless(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (info.owner == owner && damageAmount == 0 && info.type == DamageInfo.DamageType.NORMAL) {
            onSpecificTrigger();
            if (RuinaMod.isMultiplayerConnected()) {
                P2PManager.SendData(MessengerListener.request_swordCommittedSuicide, SpireHelp.Gameplay.CreatureToUID(owner), SpireHelp.Gameplay.GetMapLocation());
            }
        }
    }

    @Override
    public void onSpecificTrigger() {
        if (owner instanceof Sword && !hasTriggered) {
            flash();
            hasTriggered = true;
            ((Sword) owner).wasFullBlocked = true;
            atb(new SuicideAction((Sword) owner));
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
