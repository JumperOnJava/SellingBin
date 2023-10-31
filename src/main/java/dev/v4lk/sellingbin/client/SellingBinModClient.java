package dev.v4lk.sellingbin.client;

import dev.v4lk.sellingbin.ConfigSynchronizer;
import dev.v4lk.sellingbin.Trade;
import io.github.jumperonjava.multitooltipapi.MutliTooltipApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class SellingBinModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new MutliTooltipApi().initialize();
        ClientPlayConnectionEvents.INIT.register(ConfigSynchronizer::client);
        TooltipComponentCallback.EVENT.register(data -> {
            if(data instanceof Trade trade) {
                return trade.getTooltipComponent();
            }
            return null;
        });
    }
}
