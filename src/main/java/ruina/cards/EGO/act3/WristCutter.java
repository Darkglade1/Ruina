package ruina.cards.EGO.act3;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.actions.IncreaseCostAction;
import ruina.cardmods.RetainMod;
import ruina.cards.EGO.AbstractEgoCard;

import java.util.ArrayList;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.atb;
import static ruina.util.Wiz.att;

public class WristCutter extends AbstractEgoCard {
    public final static String ID = makeID(WristCutter.class.getSimpleName());
    public static final int COST = 2;
    public static final int WOUND = 1;
    public static final int ENERGY = 2;
    public WristCutter() {
        super(ID, COST, CardType.SKILL, CardTarget.ALL_ENEMY);
        magicNumber = baseMagicNumber = WOUND;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
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