package ruina.monsters.day49.sephirahMeltdownFlashbacks.Abnormalities.WhiteNight;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;
import static ruina.util.Wiz.adp;

public class WhiteNight extends AbstractRuinaMonster {
    public static final String ID = makeID(WhiteNight.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte NONE = 0;

    public WhiteNight(){ this(0, 125); }
    public WhiteNight(float x, float y) {
        super(NAME, ID, 999, 0.0F, 0.0F, 500.0F, 600.0F, null, x, y);
        loadAnimation(makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/WhiteNight/skeleton.atlas"), makeMonsterPath("Day49/AbnormalityContainer/AbnormalityUnit/Spriter/WhiteNight/skeleton.json"), 1.5F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "0_Default_outsidde", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("0_Default_outsidde", "1_Default_outsidde_special", 1F);
        this.stateData.setMix("0_Default_outsidde", "Dead", 1F);
        drawX = adp().drawX + 480.0F * Settings.scale;;
        halfDead = true;
        this.flipHorizontal = true;
        hideHealthBar();
        this.type = AbstractMonster.EnemyType.BOSS;
    }

    public void usePreBattleAction() {
    }

    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }

    public void takeTurn() {

    }


}