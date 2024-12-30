package ruina.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import ruina.RuinaMod;

import static ruina.util.Wiz.*;

public class VulnerableMod extends AbstractCardModifier {

    public static final String ID = RuinaMod.makeID(VulnerableMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private final int amount;
    private final int DAMAGE_AMT = 5;

    public VulnerableMod(int amount) {
        this.amount = amount;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new VulnerableMod(amount);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        applyToTarget(adp(), adp(), new VulnerablePower(adp(), amount, false));
        if (AbstractDungeon.ascensionLevel >= 19) {
            atb(new DamageAction(adp(), new DamageInfo(adp(), DAMAGE_AMT, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.POISON));
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (AbstractDungeon.ascensionLevel >= 19) {
            return rawDescription + TEXT[0] + amount + TEXT[1] + TEXT[2] + DAMAGE_AMT + TEXT[3];
        } else {
            return rawDescription + TEXT[0] + amount + TEXT[1];
        }
    }
}
