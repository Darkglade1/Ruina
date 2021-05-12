package ruina.cards.ABNO;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import ruina.RuinaMod;
import ruina.cards.AbstractRuinaCard;
import ruina.cards.EGO.act2.*;
import ruina.cards.EGO.act3.*;
import ruina.monsters.uninvitedGuests.normal.argalia.rolandCards.CHRALLY_GUN;
import ruina.util.TexLoader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import static ruina.RuinaMod.makeImagePath;
import static ruina.util.Wiz.adp;

public abstract class AbstractAbnormalityCard extends AbstractRuinaCard {

    public AbstractAbnormalityCard(final String cardID, final int cost, final CardType type, final CardTarget target) { super(cardID, cost, type, CardRarity.RARE, target, RuinaMod.AbnoEnums.ANGELA_LOR_ABNO , makeImagePath("cards/" + CHRALLY_GUN.class.getSimpleName() + ".png")); }

}