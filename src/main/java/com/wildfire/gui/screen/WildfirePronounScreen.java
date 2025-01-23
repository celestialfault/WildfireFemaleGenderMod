/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wildfire.gui.screen;

import com.wildfire.gui.GuiUtils;
import com.wildfire.gui.WildfireButton;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.WildfireLocalization;
import com.wildfire.main.config.Configuration;
import com.wildfire.main.config.GlobalConfig;
import com.wildfire.main.config.enums.Pronoun;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class WildfirePronounScreen extends BaseWildfireScreen {
    private static final Identifier BACKGROUND = Identifier.of(WildfireGender.MODID, "textures/gui/pronouns_bg.png");

    protected WildfirePronounScreen(Screen parent, UUID uuid) {
		super(Text.translatable("wildfire_gender.pronoun.settings"), parent, uuid);
	}
    Pronoun pronoun;
//    private Configuration cfg;

    @Override
    public void init() {
        int x = this.width / 2;
		int y = this.height / 2;
		int yPos = y - 47;
		int xPos = x - 156 / 2 - 1;
         String pronouns = String.valueOf(Configuration.PRONOUNS);
        this.addDrawableChild(new WildfireButton(xPos, yPos, 157, 20,
            Text.translatable("wildfire_gender.pronoun.status", Pronoun.SHE, Objects.equals(pronouns, "SHE") ? WildfireLocalization.ENABLED : WildfireLocalization.DISABLED),
            button -> {
                //Doesn't do anything
            }));
        this.addDrawableChild(new WildfireButton(xPos, yPos + 20, 157, 20,
            Text.translatable("wildfire_gender.pronoun.status", Pronoun.HE, Objects.equals(pronouns, "HE") ? WildfireLocalization.ENABLED : WildfireLocalization.DISABLED),
            button -> {
                //Doesn't do anything
            }));
        this.addDrawableChild(new WildfireButton(xPos, yPos + 40, 157, 20,
            Text.translatable("wildfire_gender.pronoun.status", Pronoun.THEY, Objects.equals(pronouns, "THEY") ? WildfireLocalization.ENABLED : WildfireLocalization.DISABLED),
            button -> {
                //Doesn't do anything
            }));
        this.addDrawableChild(new WildfireButton(xPos, yPos + 60, 157, 20,
            Text.translatable("wildfire_gender.pronoun.status", Pronoun.IT, Objects.equals(pronouns, "IT") ? WildfireLocalization.ENABLED : WildfireLocalization.DISABLED),
            button -> {
                //Doesn't do anything
            }));
        this.addDrawableChild(new WildfireButton(xPos, yPos + 97, 157, 25,
            Text.translatable("wildfire_gender.pronoun.swap"),
            button -> {
                //Doesn't do anything, supposed to do swap the pronouns (she/he -> he/she)
        }));

        super.init();
    }

    @Override
	public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
		this.renderInGameBackground(ctx);
		ctx.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, (this.width - 172) / 2, (this.height - 124) / 2, 0, 0, 172, 144, 256, 256);
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (client == null || client.world == null) return;
        super.render(ctx, mouseX, mouseY, delta);

        int x = this.width / 2;
        int y = this.height / 2;
        y -= 47;

        GuiUtils.drawScrollableTextWithoutShadow(GuiUtils.Justify.LEFT, ctx, textRenderer, getTitle(),
				x - 79, y - 12, x - 79 + 141, y - 11 + 10, 4473924);
    }

    @Override
	public void close() {
		GlobalConfig.INSTANCE.save();
		super.close();
	}
}