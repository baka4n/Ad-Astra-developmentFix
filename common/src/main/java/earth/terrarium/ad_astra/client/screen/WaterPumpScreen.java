package earth.terrarium.ad_astra.client.screen;

import earth.terrarium.ad_astra.AdAstra;
import earth.terrarium.ad_astra.common.block.machine.entity.WaterPumpBlockEntity;
import earth.terrarium.ad_astra.common.screen.menu.WaterPumpMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class WaterPumpScreen extends AbstractMachineScreen<WaterPumpBlockEntity, WaterPumpMenu> {

    public static final int INPUT_TANK_LEFT = 80;
    public static final int INPUT_TANK_TOP = 30;
    public static final int ENERGY_LEFT = 146;
    public static final int ENERGY_TOP = 30;
    private static final ResourceLocation TEXTURE = new ResourceLocation(AdAstra.MOD_ID, "textures/gui/screens/water_pump.png");

    public WaterPumpScreen(WaterPumpMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title, TEXTURE);
        this.imageWidth = 177;
        this.imageHeight = 180;
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);

        GuiUtil.drawEnergy(graphics, this.leftPos + ENERGY_LEFT, this.topPos + ENERGY_TOP, this.menu.getEnergyAmount(), this.machine.getMaxCapacity());
        GuiUtil.drawFluidTank(graphics, this.leftPos + INPUT_TANK_LEFT, this.topPos + INPUT_TANK_TOP, this.machine.getFluidContainer().getTankCapacity(0), this.menu.getFluids().get(0));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        if (GuiUtil.isHovering(this.getEnergyBounds(), mouseX, mouseY)) {
            GuiUtil.drawEnergyTooltip(graphics, this.menu.getEnergyAmount(), this.machine.getMaxCapacity(), mouseX, mouseY);
        }

        if (GuiUtil.isHovering(this.getInputTankBounds(), mouseX, mouseY)) {
            GuiUtil.drawTankTooltip(graphics, this.menu.getFluids().get(0), this.machine.getFluidContainer().getTankCapacity(0), mouseX, mouseY);
        }
    }

    public Rectangle getInputTankBounds() {
        return GuiUtil.getFluidTankBounds(this.leftPos + INPUT_TANK_LEFT, this.topPos + INPUT_TANK_TOP);
    }

    public Rectangle getEnergyBounds() {
        return GuiUtil.getEnergyBounds(this.leftPos + ENERGY_LEFT, this.topPos + ENERGY_TOP);
    }

    @Override
    public int getTextColour() {
        return 0x2C282E;
    }
}