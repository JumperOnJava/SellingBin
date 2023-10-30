package dev.v4lk.sellingbin.client;

import dev.v4lk.sellingbin.ConfigSynchronizer;
import dev.v4lk.sellingbin.Trade;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellingBinModClient implements ClientModInitializer {
    public static List<Trade> trades = new ArrayList<>();
    private static Map<Identifier,Trade> getTradeMap(){
        var tradeMap = new HashMap<Identifier, Trade>();
        trades.forEach(t->tradeMap.put(new Identifier(t.getName()),t));
        return tradeMap;
    }
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.INIT.register(ConfigSynchronizer::client);
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            var tmap = getTradeMap();
            var trade = tmap.get(Registries.ITEM.getId(stack.getItem()));
            if(trade==null)
                return;
            lines.add(Text.translatable("selling-bin.tooltip.selling").setStyle(Style.EMPTY.withColor(trade.getColor())));
        });
    }
}
