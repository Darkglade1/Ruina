package ruina.monsters.blackSilence.blackSilence4.memories.blue;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import ruina.monsters.blackSilence.blackSilence4.BlackSilence4;
import ruina.powers.AbstractLambdaPower;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.applyToTarget;

@AutoAdd.Ignore
public class Blue2 extends Blue {
    public final static String ID = makeID(Blue2.class.getSimpleName());

    public static final String POWER_ID = makeID("Sorrow");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public Blue2(BlackSilence4 parent) {
        super(parent);
        this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
        this.initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) { }

    @Override
    public void upp() { }

    public void onChoseThisOption(){
        applyToTarget(parent, parent, new AbstractLambdaPower(POWER_NAME, POWER_ID, AbstractPower.PowerType.BUFF, false, parent, -1) {

            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                this.flash();
                action.exhaustCard = true;
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }
}