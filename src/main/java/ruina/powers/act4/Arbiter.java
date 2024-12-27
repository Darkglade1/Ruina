package ruina.powers.act4;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import ruina.RuinaMod;
import ruina.monsters.AbstractCardMonster;
import ruina.monsters.AbstractMultiIntentMonster;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.uninvitedGuests.normal.elena.Binah;
import ruina.powers.AbstractUnremovablePower;

import java.util.ArrayList;

import static ruina.util.Wiz.atb;

public class Arbiter extends AbstractUnremovablePower {
    public static final String POWER_ID = RuinaMod.makeID(Arbiter.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Arbiter(AbstractCreature owner) {
        super(NAME, POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        AbstractCreature enemy = target;
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (!enemy.hasPower(StunMonsterPower.POWER_ID)) {
                    if (enemy instanceof AbstractMultiIntentMonster) {
                        ((AbstractMultiIntentMonster) enemy).additionalIntents.clear();
                        ((AbstractMultiIntentMonster) enemy).additionalMoves.clear();
                    }
                    if (enemy instanceof AbstractCardMonster) {
                        ArrayList<AbstractCard> cards = ((AbstractCardMonster) enemy).cardsToRender;
                        if (cards.size() > 1) {
                            cards.remove(cards.size() - 1);
                        }
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        AbstractCreature newTarget = action.target;
        if (newTarget instanceof AbstractRuinaMonster && !newTarget.isDeadOrEscaped() && owner instanceof Binah) {
            ((Binah) owner).target = (AbstractRuinaMonster) action.target;
            AbstractDungeon.onModifyPower();
        }
    }

    @Override
    public void updateDescription() {
        description = POWER_DESCRIPTIONS[0];
    }
}
