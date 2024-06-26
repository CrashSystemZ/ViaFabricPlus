/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.protocols.v1_14_3to1_14_4.packet.ClientboundPackets1_14_4;
import com.viaversion.viaversion.protocols.v1_14_4to1_15.Protocol1_14_4To1_15;
import com.viaversion.viaversion.protocols.v1_14_4to1_15.rewriter.EntityPacketRewriter1_15;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.entitydata.EntityDataFilter;
import de.florianmichael.viafabricplus.fixes.viaversion.WolfHealthTracker1_14_4;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityPacketRewriter1_15.class, remap = false)
public abstract class MixinEntityPacketRewriter1_15 extends EntityRewriter<ClientboundPackets1_14_4, Protocol1_14_4To1_15> {

    public MixinEntityPacketRewriter1_15(Protocol1_14_4To1_15 protocol) {
        super(protocol);
    }

    @Redirect(method = "registerRewrites", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/rewriter/entitydata/EntityDataFilter$Builder;removeIndex(I)V"))
    private void removeAndTrackHealth(EntityDataFilter.Builder instance, int index) {
        instance.handler((event, meta) -> {
            final int metaIndex = event.index();
            if (metaIndex == index) {
                event.user().get(WolfHealthTracker1_14_4.class).setWolfHealth(event.entityId(), meta.value());
                event.cancel();
            } else if (metaIndex > index) {
                event.setIndex(metaIndex - 1);
            }
        });
    }

}
