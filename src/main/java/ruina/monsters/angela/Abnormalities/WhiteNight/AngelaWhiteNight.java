package ruina.monsters.angela.Abnormalities.WhiteNight;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.actions.UsePreBattleActionAction;
import ruina.monsters.act3.Bloodbath;
import ruina.monsters.angela.*;
import ruina.monsters.angela.Abnormalities.WhiteNight.vfx.WhiteNightAura;

import static ruina.RuinaMod.makeID;
import static ruina.util.Wiz.*;

public class AngelaWhiteNight extends AbnormalityContainer
{
    private static final String ABNOID = "WhiteNight";
    private static final String WARNING = "ALEPH";
    public static final String ID = makeID(AngelaWhiteNight.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    private static final byte NONE = 0;
    private final int HEAL = calcAscensionSpecial(100);
    public AngelaWhiteNight(){
        this(0, 275);
    }
    public AngelaWhiteNight(final float x, final float y) {
        super(NAME, ID, 80, -5.0F, 0, 230.0f, 225.0f, null, x, y, ABNOID, WARNING);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 9) {
            setHp(1);
        } else {
            setHp(1);
        }
    }

    protected void setupAbnormality() {
        abnormalityBG = new AbnormalityBackground(abnoID);
        abnormalityBG.drawX = adp().drawX;
        abnormalityEncyclopedia = new AbnormalityEncyclopedia(abnoID);
        abnormalityEncyclopedia.drawX = adp().drawX;
        abnormalityWarning = new AbnormalityWarning(warningTier);
        abnormalityWarning.drawX = adp().drawX;
        staticDischarge = new Static();
        staticDischarge.drawX = adp().drawX;
    }

    @Override
    protected void getAbnormality(int timesBreached) {
        prepareBreach();
        WhiteNight whiteNight = new WhiteNight();
        atb(new SpawnMonsterAction(whiteNight, true));
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.effectsQueue.add(new WhiteNightAura(whiteNight.hb));
                att(new UsePreBattleActionAction(whiteNight));
                isDone = true;
            }
        });
        whiteNight.rollMove();
        whiteNight.createIntent();
    }

    @Override
    public void takeTurn() {
    }

    @Override
    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }
}