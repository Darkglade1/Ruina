package ruina.events;

import basemod.ReflectionHacks;
import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import ruina.BetterSpriterAnimation;
import ruina.RuinaMod;
import ruina.monsters.AbstractRuinaMonster;

import static ruina.RuinaMod.makeMonsterPath;

public class NeowAngela extends NeowEvent {

    public static final String ID = RuinaMod.makeID(NeowAngela.class.getSimpleName());
    private final AbstractAnimation angela;

    //this variable is here to stop the other guy's mod from crashing
    private int bossCount;

    public NeowAngela() {
        angela = new BetterSpriterAnimation(makeMonsterPath("Angela/Spriter/Angela.scml"));
        // turns out the other guy's mod was secretly also permNeow :upside_down:
        if (Loader.isModLoaded("Lobotomy")) {
            int parentBossCount = ReflectionHacks.getPrivate(this, NeowEvent.class, "bossCount");
            if (parentBossCount < 1) {
                ReflectionHacks.setPrivate(this, NeowEvent.class, "bossCount", 1);
            }
        }
    }

    @SpireOverride
    protected void playSfx() {
        AbstractRuinaMonster.playSound("FingerSnap", 0.7f);
    }

    @Override
    public void render(SpriteBatch sb) {
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        angela.renderSprite(sb, 1334.0F * Settings.xScale, AbstractDungeon.floorY - 10.0F * Settings.yScale);
    }

}
