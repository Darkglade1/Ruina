package ruina.monsters.angela.Abnormalities.WhiteNight;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import ruina.monsters.AbstractRuinaMonster;
import ruina.monsters.angela.Abnormalities.WhiteNight.vfx.WhiteNightAura;

import static ruina.RuinaMod.makeID;
import static ruina.RuinaMod.makeMonsterPath;

public class WhiteNight extends AbstractRuinaMonster {
    public static final String ID = makeID(WhiteNight.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] DIALOG = monsterStrings.DIALOG;

    private static final byte NONE = 0;
    public static final TextureAtlas.AtlasRegion Ring;
    public static final TextureAtlas.AtlasRegion LightAura;
    public static final TextureAtlas.AtlasRegion AuraPin;

    public WhiteNight(){ this(0, 0); }
    public WhiteNight(float x, float y) {
        super(NAME, ID, 999, 0.0F, 0.0F, 500.0F, 600.0F, null, x, y);
        loadAnimation(makeMonsterPath("AbnormalityContainer/AbnormalityUnit/Spriter/WhiteNight/WhiteNight.atlas"), makeMonsterPath("AbnormalityContainer/AbnormalityUnit/Spriter/WhiteNight/WhiteNight.json"), 1.5F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "normal", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.state.setTimeScale(1.0F);
        this.flipHorizontal = true;
        //this.type = AbstractMonster.EnemyType.BOSS;
    }

    public void usePreBattleAction() {
    }

    protected void getMove(int i) {
        setMove(NONE, Intent.NONE);
    }

    public void takeTurn() {

    }

    static {
        TextureAtlas atlas = new TextureAtlas(makeMonsterPath("AbnormalityContainer/AbnormalityUnit/Spriter/WhiteNight/backEffect.atlas"));
        Ring = atlas.findRegion("0407");
        LightAura = atlas.findRegion("0408");
        AuraPin = atlas.findRegion("0400");
    }
}