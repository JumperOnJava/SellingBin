package dev.v4lk.sellingbin;

import dev.v4lk.multitooltip.Match;
import dev.v4lk.multitooltip.TextItemTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class Trade implements Match, TooltipData{
    private String name;
    private String currency;
    private int sellPrice;
    private int sellAmount;
    private String color = Integer.toString(Formatting.DARK_GRAY.getColorIndex(),16).toUpperCase();
    private static final String DEFAULT_COLOR = "FF555555";
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(int sellAmount) {
        this.sellAmount = sellAmount;
    }

    public int getColor() {
            if(this.color.equals("8"))
                this.color = DEFAULT_COLOR;
            return Integer.parseUnsignedInt(this.color,16);
    }
    public void setColor(int color) {
        this.color = Integer.toHexString(color);
    }

    @Override
    public TooltipData data() {
        return this;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).equals(new Identifier(this.name));
    }

    public TooltipComponent getTooltipComponent() {
        return new TextItemTooltipComponent(
                Text.translatable("selling-bin.tooltip.selling", String.valueOf(sellAmount)).setStyle(Style.EMPTY.withColor(this.getColor())),
                new ItemStack(Registries.ITEM.get(new Identifier(this.currency)), this.sellPrice)
        );
    }
}