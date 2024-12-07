package ruina.CustomIntent;

import actlikeit.RazIntent.CustomIntent;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.RuinaMod;

import static ruina.RuinaMod.makeUIPath;

public class CounterAttackIntent extends CustomIntent {

    public static final String ID = RuinaMod.makeID("CounterAttackIntent");

    private static final UIStrings uiStrings;
    private static final String[] TEXT;


    public CounterAttackIntent() {
        super(IntentEnums.COUNTER_ATTACK, TEXT[0],
                makeUIPath("counterIntent_L.png"),
                makeUIPath("counterIntent.png"));
    }

    @Override
    public String description(AbstractMonster mo) {
        String result = TEXT[1];
        result += mo.getIntentDmg();
        int hitCount;
        if ((Boolean) ReflectionHacks.getPrivate(mo, AbstractMonster.class, "isMultiDmg")) {
            hitCount = (Integer) ReflectionHacks.getPrivate(mo, AbstractMonster.class, "intentMultiAmt");
            result += TEXT[3];
            result += hitCount + TEXT[4];
        } else {
            result += TEXT[2];
        }
        return result;
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(ID);
        TEXT = uiStrings.TEXT;
    }
}