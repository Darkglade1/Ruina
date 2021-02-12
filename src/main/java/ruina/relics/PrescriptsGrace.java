package ruina.relics;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.adp;
import static ruina.util.Wiz.atb;

public class PrescriptsGrace extends AbstractEasyRelic {
    public static final String ID = makeID(PrescriptsGrace.class.getSimpleName());

    private boolean used = false;
    private AbstractCard.CardType cardType;
    private String chosenTypeDescription;

    public PrescriptsGrace() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void atPreBattle() {
        used = false;
        int type = AbstractDungeon.relicRng.random(1, 3);
        if (type == 1) {
            cardType = AbstractCard.CardType.ATTACK;
            chosenTypeDescription = DESCRIPTIONS[1] + DESCRIPTIONS[2];
        } else if (type == 2) {
            cardType = AbstractCard.CardType.SKILL;
            chosenTypeDescription = DESCRIPTIONS[1] + DESCRIPTIONS[3];
        } else {
            cardType = AbstractCard.CardType.POWER;
            chosenTypeDescription = DESCRIPTIONS[1] + DESCRIPTIONS[4];
        }
        fixDescription();
        atb(new TalkAction(true, chosenTypeDescription, 2.0F, 2.0F));
    }

    @Override
    public void onVictory() {
        chosenTypeDescription = null;
        fixDescription();
    }

    private void fixDescription() {
        description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void obtain() {
        if (adp().hasRelic(Prescript.ID)) {
            int relicIndex = -1;
            for (int i = 0; i < adp().relics.size(); i++) {
                if (adp().relics.get(i) instanceof Prescript) {
                    relicIndex = i;
                    break;
                }
            }
            if (relicIndex >= 0) {
                this.instantObtain(adp(), relicIndex, false);
            } else {
                super.obtain();
            }
        } else {
            super.obtain();
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!card.purgeOnUse && !used && card.type == cardType) {
            this.flash();
            AbstractMonster m = null;

            if (action.target != null) {
                m = (AbstractMonster) action.target;
            }

            GameActionManager.queueExtraCard(card, m);
            used = true;
            chosenTypeDescription = null;
            fixDescription();
        }
    }

    @Override
    public String getUpdatedDescription() {
        if (chosenTypeDescription != null) {
            return DESCRIPTIONS[0] + chosenTypeDescription;
        } else {
            return DESCRIPTIONS[0];
        }
    }
}
