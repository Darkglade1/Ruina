package ruina.relics.d49;

import basemod.AutoAdd;
import basemod.DevConsole;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import ruina.powers.InvisiblePermanentDrawReductionPower;
import ruina.relics.AbstractEasyRelic;
import ruina.relics.Book;
import ruina.util.TexLoader;

import static ruina.RuinaMod.*;
import static ruina.RuinaMod.getModID;
import static ruina.util.Wiz.adp;
import static ruina.util.actionShortcuts.doDraw;
import static ruina.util.actionShortcuts.doPow;

@AutoAdd.Ignore
public class Director extends AbstractEasyRelic {
    public static final String ID = RunicPyramid.ID;

    public static final int DRAW = 3;
    public static final int DRAW_DOWN = 4;

    public Director() {
        super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
        img = TexLoader.getTexture(makeRelicPath("Book.png"));
        outlineImg = TexLoader.getTexture(makeRelicPath("BookOutline.png"));
    }

    @Override
    public void atBattleStart(){
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
    }
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void instantObtain()
    {
        super.instantObtain();
        DisableConsole();
    }

    @Override
    public void obtain()
    {
        super.obtain();
        DisableConsole();
    }

    @Override
    public void instantObtain(AbstractPlayer p, int slot, boolean callOnEquip)
    {
        super.instantObtain(p, slot, callOnEquip);
        DisableConsole();
    }

    @Override
    public void update()
    {
        super.update();
        DisableConsole();
    }
}