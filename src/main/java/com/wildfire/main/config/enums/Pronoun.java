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

package com.wildfire.main.config.enums;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum Pronoun {
	// Normative
	SHE("she", "her"),
	HE("he", "him"),

	// Normative-ish,
	THEY("they", "them"),
	IT("it", "its"),

	;

	//Broken and unused
//	public static final IntFunction<Pronoun> BY_ID = ValueLists.createIdToValueFunction(Pronoun::ordinal, values(), ValueLists.OutOfBoundsHandling.ZERO);

	public final String subjective;
	public final String objective;

	Pronoun(String subjective, String objective) {
		this.subjective = subjective;
		this.objective = objective;
	}

	@Override
	public String toString() {
		return subjective + "/" + objective;
	}

	public static Text local(Pronoun pronoun, Boolean subjective) {
		if (pronoun == null) return null;
		Text text;
		if (subjective) text = switch (pronoun) {
			case SHE -> Text.translatable("wildfire_gender.pronoun.subjective.she");
			case HE -> Text.translatable("wildfire_gender.pronoun.subjective.he");
			case THEY -> Text.translatable("wildfire_gender.pronoun.subjective.they");
			case IT -> Text.translatable("wildfire_gender.pronoun.subjective.it");
		};
		else text = switch (pronoun) {
			case SHE -> Text.translatable("wildfire_gender.pronoun.objective.she");
			case HE -> Text.translatable("wildfire_gender.pronoun.objective.he");
			case THEY -> Text.translatable("wildfire_gender.pronoun.objective.they");
			case IT -> Text.translatable("wildfire_gender.pronoun.objective.it");
		};
		return text;
	}

	public static @Nullable String format(List<Pronoun> pronouns) {
		if(pronouns.isEmpty()) return null;
		var first = pronouns.getFirst();
		var second = pronouns.size() == 2 ? pronouns.getLast() : null;
		return switch(pronouns.size()) {
			case 1 -> first.toString();
			case 2 -> first.subjective + "/" + second.subjective;
			default -> throw new UnsupportedOperationException();
		};
	}

	public static MutableText localFormat(List<Pronoun> pronouns) {
		if (pronouns.isEmpty()) return  null;
		Pronoun first = pronouns.getFirst();
		Pronoun second = pronouns.size() == 2 ? pronouns.getLast() : null;
		return switch (pronouns.size()) {
			case 1 -> Text.translatable("wildfire_gender.pronoun.subjective." + first.subjective).append("/").append(Text.translatable("wildfire_gender.pronoun.objective." + first.subjective));
			case 2 -> Text.translatable("wildfire_gender.pronoun.subjective." + first.subjective).append("/").append(Text.translatable("wildfire_gender.pronoun.subjective." + second.subjective));
			default -> throw new UnsupportedOperationException();
		};
	}

	public static Pronoun next(Pronoun pronoun) {
		return switch (pronoun) {
			case SHE -> HE;
			case HE -> THEY;
			case THEY -> IT;
			case IT -> null;
			case null -> SHE;
		};
	}
}