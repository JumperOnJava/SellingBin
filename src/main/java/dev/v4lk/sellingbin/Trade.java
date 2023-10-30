package dev.v4lk.sellingbin;

import net.minecraft.util.Formatting;

public class Trade {
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
}