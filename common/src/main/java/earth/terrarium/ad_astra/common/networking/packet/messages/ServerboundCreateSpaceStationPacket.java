package earth.terrarium.ad_astra.common.networking.packet.messages;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.ad_astra.AdAstra;
import earth.terrarium.ad_astra.common.recipe.SpaceStationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public record ServerboundCreateSpaceStationPacket(
    ResourceLocation targetWorld) implements Packet<ServerboundCreateSpaceStationPacket> {

    public static final ResourceLocation ID = new ResourceLocation(AdAstra.MOD_ID, "create_space_station");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ServerboundCreateSpaceStationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<ServerboundCreateSpaceStationPacket> {
        private static boolean hasItem(Player player, Ingredient ingredient, int count) {
            int found = 0;
            for (ItemStack stack : player.getInventory().items) {
                if (ingredient.test(stack)) {
                    found += stack.getCount();
                }
            }
            return found >= count;
        }

        @Override
        public void encode(ServerboundCreateSpaceStationPacket packet, FriendlyByteBuf buf) {
            buf.writeResourceLocation(packet.targetWorld());
        }

        @Override
        public ServerboundCreateSpaceStationPacket decode(FriendlyByteBuf buf) {
            return new ServerboundCreateSpaceStationPacket(buf.readResourceLocation());
        }

        @Override
        public PacketContext handle(ServerboundCreateSpaceStationPacket packet) {
            return (player, level) -> {
                if (!player.isCreative() && !player.isSpectator()) {
                    for (SpaceStationRecipe recipe : SpaceStationRecipe.getRecipes(player.level())) {
                        for (int i = 0; i < recipe.getIngredients().size(); i++) {
                            if (!hasItem(player, recipe.getIngredients().get(i), recipe.getHolders().get(i).count())) {
                                return;
                            }
                        }
                    }
                    Inventory inventory = player.getInventory();
                    SpaceStationRecipe.getRecipes(player.level()).forEach(recipe -> {
                        for (int i = 0; i < recipe.getIngredients().size(); i++) {
                            inventory.clearOrCountMatchingItems(recipe.getIngredients().get(i), recipe.getHolders().get(i).count(), inventory);
                        }
                    });
                }

                if (level instanceof ServerLevel serverWorld) {
                    ServerLevel targetWorld = serverWorld.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, packet.targetWorld));

                    if (targetWorld == null) return;

                    // Create the Space Station from the nbt file
                    StructureTemplate structure = targetWorld.getStructureManager().getOrCreate(new ResourceLocation(AdAstra.MOD_ID, "space_station"));
                    BlockPos pos = BlockPos.containing((player.getX() - (structure.getSize().getX() / 2.0f)), 100, (player.getZ() - (structure.getSize().getZ() / 2.0f)));
                    targetWorld.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos), 1, pos);
                    structure.placeInWorld(targetWorld, pos, pos, new StructurePlaceSettings(), targetWorld.random, 2);
                }
            };
        }
    }
}
