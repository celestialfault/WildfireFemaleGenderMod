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

package com.wildfire.render;

import com.wildfire.api.IBreastArmorTexture;
import com.wildfire.api.IGenderArmor;
import com.wildfire.main.WildfireGender;
import com.wildfire.main.entitydata.EntityConfig;
import com.wildfire.render.WildfireModelRenderer.BreastModelBox;
import com.wildfire.resources.GenderArmorResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class GenderArmorLayer<T extends LivingEntity, M extends BipedEntityModel<T>> extends GenderLayer<T, M> {

	private final SpriteAtlasTexture armorTrimsAtlas;
	protected BreastModelBox lBoobArmor, rBoobArmor;
	protected static final BreastModelBox lTrim, rTrim;
	private EntityConfig entityConfig;
	private @NotNull IBreastArmorTexture textureData = IBreastArmorTexture.DEFAULT;

	static {
		// apply a very slight delta to fix z-fighting with the armor
		lTrim = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 4, 0.001F, false);
		rTrim = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 4, 0.001F, false);
	}

	public GenderArmorLayer(FeatureRendererContext<T, M> render, BakedModelManager bakery) {
		super(render);
		armorTrimsAtlas = bakery.getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
		lBoobArmor = new BreastModelBox(64, 32, 16, 17, -4F, 0.0F, 0F, 4, 5, 3, 0.0F, false);
		rBoobArmor = new BreastModelBox(64, 32, 20, 17, 0, 0.0F, 0F, 4, 5, 3, 0.0F, false);
	}

	public @NotNull Identifier getArmorResource(@NotNull ArmorItem item) {
		return GenderArmorResourceManager.get(item.getMaterial())
				.map(IGenderArmor::texture)
				.map(IBreastArmorTexture::texture)
				.orElseGet(() -> takeAGuessAtTheIdentifier(item));
	}

	// I was hoping I'd never need to do this again :(
	private Identifier takeAGuessAtTheIdentifier(@NotNull ArmorItem item) {
		String material = item.getMaterial().getName();
		String namespace = "minecraft";
		int namespaceDelim = material.indexOf(':');
		if(namespaceDelim >= 0) {
			namespace = material.substring(0, namespaceDelim);
			material = material.substring(namespaceDelim + 1);
		}
		return new Identifier(namespace, "textures/models/armor/" + material + "_layer_1.png");
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, @NotNull T ent, float limbAngle,
	                   float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player == null) {
			// TODO is it possible to remove this check? does anything this invoke still check
			//		the client player or world?
			return;
		}

		final ItemStack chestplate = ent.getEquippedStack(EquipmentSlot.CHEST);
		// If the entity has no armor to render, just immediately give up
		// Note that we have to be very fast at abandoning rendering here, as this class is also attached to armor stands
		if(chestplate.isEmpty() || !(chestplate.getItem() instanceof ArmorItem)) return;
		// And similarly just entirely give up if the item has a renderer registered with Fabric API
		// This will likely result in the player's breasts sticking out through the armor layer unless the mod in question
		// implements an IGenderArmor to prevent them from rendering entirely, but oh well; at least we won't be
		// rendering a pink box.
		//noinspection UnstableApiUsage
		if(ArmorRendererRegistryImpl.get(chestplate.getItem()) != null) return;

		try {
			entityConfig = EntityConfig.getEntity(ent);

			if(!setupRender(ent, entityConfig, partialTicks)) return;
			if(ent instanceof ArmorStandEntity && !genderArmor.armorStandsCopySettings()) return;

			final var armorItem = (ArmorItem) chestplate.getItem();
			final var material = armorItem.getMaterial();
			final boolean glint = chestplate.hasGlint();

			// I did not miss having to do this kind of nonsense :(
			final AtomicInteger r = new AtomicInteger(255),
					g = new AtomicInteger(255),
					b = new AtomicInteger(255);
			if(armorItem instanceof DyeableArmorItem dyeableItem) {
				int color = dyeableItem.getColor(armorStack);
				r.set(color >> 16 & 255);
				g.set(color >> 8 & 255);
				b.set(color & 255);
			}

			renderSides(ent, getContextModel(), matrixStack, side -> {
				renderBreastArmor(getArmorResource(armorItem), matrixStack, vertexConsumerProvider, light, side, r.get() / 255f, g.get() / 255f, b.get() / 255f, glint);
				ArmorTrim.getTrim(ent.getWorld().getRegistryManager(), chestplate).ifPresent(trim -> {
					renderArmorTrim(material, matrixStack, vertexConsumerProvider, light, trim, glint, side);
				});
			});
		} catch(Exception e) {
			WildfireGender.LOGGER.error("Failed to render breast armor", e);
		}
	}

	@Override
	protected void resizeBox(float breastSize) {
		if(genderArmor == null || Objects.equals(textureData, genderArmor.texture())) {
			return;
		}

		textureData = genderArmor.texture();
		var texSize = textureData.textureSize();
		var lUV = textureData.leftUv();
		var dim = textureData.dimensions();
		lBoobArmor = new BreastModelBox(texSize.x(), texSize.y(), lUV.x(), lUV.y(), -4F, 0.0F, 0F, dim.x(), dim.y(), 3, 0.0F, false);
		var rUV = textureData.rightUv();
		rBoobArmor = new BreastModelBox(texSize.x(), texSize.y(), rUV.x(), rUV.y(), 0, 0.0F, 0F, dim.x(), dim.y(), 3, 0.0F, false);
	}

	@Override
	protected void setupTransformations(T entity, M model, MatrixStack matrixStack, BreastSide side) {
		super.setupTransformations(entity, model, matrixStack, side);
		if((entity instanceof AbstractClientPlayerEntity player && player.isPartVisible(PlayerModelPart.JACKET)) ||
				(entity instanceof ArmorStandEntity && entityConfig.hasJacketLayer())) {
			matrixStack.translate(0, 0, -0.015f);
			matrixStack.scale(1.05f, 1.05f, 1.05f);
		}
		matrixStack.translate(side.isLeft ? 0.001f : -0.001f, 0.015f, -0.015f);
		matrixStack.scale(1.05f, 1, 1);
	}

	// TODO eventually expose some way for mods to override this, maybe through a default impl in IGenderArmor or similar
	protected void renderBreastArmor(Identifier texture, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
	                                 int light, BreastSide side, float r, float g, float b, boolean glint) {
		if(MinecraftClient.getInstance().getTextureManager().getTexture(texture) == MissingSprite.getMissingSpriteTexture()) {
			return;
		}

		BreastModelBox armor = side.isLeft ? lBoobArmor : rBoobArmor;
		RenderLayer armorType = RenderLayer.getArmorCutoutNoCull(texture);
		VertexConsumer armorVertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, armorType, false, glint);
		renderBox(armor, matrixStack, armorVertexConsumer, light, OverlayTexture.DEFAULT_UV, r, g, b, 1f);
	}

	protected void renderArmorTrim(ArmorMaterial material, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
	                               int packedLightIn, ArmorTrim trim, boolean hasGlint, BreastSide side) {
		BreastModelBox trimModelBox = side.isLeft ? lTrim : rTrim;
		Sprite sprite = this.armorTrimsAtlas.getSprite(trim.getGenericModelId(material));
		VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumerProvider.getBuffer(TexturedRenderLayers.getArmorTrims()));
		// Render the armor trim itself
		renderBox(trimModelBox, matrixStack, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
		// The enchantment glint however requires special handling; due to how Minecraft's enchant glint rendering works, rendering
		// it at the same time as the trim itself results in the glint not rendering in sync with the rest of the armor.
		// We *also* can't simply render the glint for both the trim and armor at the same time, due to the slight delta we apply
		// to fix z-fighting between the trim and armor - and as such - a glint has to be rendered for each respective layer.
		if(hasGlint) {
			renderBox(trimModelBox, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getArmorEntityGlint()),
					packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
		}
	}
}
