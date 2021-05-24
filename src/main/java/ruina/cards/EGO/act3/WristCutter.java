package ruina.cards.EGO.act3;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;
import ruina.cardmods.RetainMod;
import ruina.cards.EGO.AbstractEgoCard;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class WristCutter extends AbstractEgoCard {
    public final static String ID = makeID(WristCutter.class.getSimpleName());
    public static final int COST = 0;
    public static final int WOUND = 1;
    public static final int ENERGY = 2;
    public WristCutter() {
        super(ID, COST, CardType.SKILL, CardTarget.NONE);
        magicNumber = baseMagicNumber = WOUND;
        cardsToPreview = new Wound();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (Settings.FAST_MODE) {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.1F));
        } else {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.5F));
        }
        atb(new GainEnergyAction(ENERGY));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractCard wound = new Wound();
                CardModifierManager.addModifier(wound, new RetainMod());
                att(new MakeTempCardInHandAction(wound, magicNumber));
                isDone = true;
            }
        });
    }

    @Override
    public void upp() { selfRetain = true; }
}