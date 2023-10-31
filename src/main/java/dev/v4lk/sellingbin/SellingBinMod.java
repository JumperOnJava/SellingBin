package dev.v4lk.sellingbin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.v4lk.sellingbin.bins.diamond.DiamondBinBlock;
import dev.v4lk.sellingbin.bins.diamond.DiamondBinBlockEntity;
import dev.v4lk.sellingbin.bins.iron.IronBinBlock;
import dev.v4lk.sellingbin.bins.iron.IronBinBlockEntity;
import dev.v4lk.sellingbin.bins.wooden.WoodenBinBlock;
import dev.v4lk.sellingbin.bins.wooden.WoodenBinBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static net.minecraft.server.command.CommandManager.literal;

public class SellingBinMod implements ModInitializer {
    public static String defaultConfig = "{\n" +
            "\t\"minecraft:cobblestone\": {\n" +
            "\t  \"currency\": \"minecraft:iron_ingot\",\n" +
            "\t  \"sellPrice\": 1,\n" +
            "\t  \"sellAmount\": 64,\n" +
            "\t  \"color\": \"FFAAAAAA\"//color stored in argb hex format (first two symbols is alpha) \n" +
            "\t},\n" +
            "\t\"minecraft:glowstone\": {\n" +
            "\t  \"currency\": \"minecraft:iron_ingot\",\n" +
            "\t  \"sellPrice\": 3,\n" +
            "\t  \"sellAmount\": 16,\n" +
            "\t  \"color\": \"FFFFFF33\"\n" +
            "\t},\n" +
            "\t\"minecraft:wheat_seeds\": {\n" +
            "\t  \"currency\": \"minecraft:iron_ingot\",\n" +
            "\t  \"sellPrice\": 1,\n" +
            "\t  \"sellAmount\": 64\n" +
            "\t}\n" +
            "}";

    public static File configFile = new File("config/selling-bin.json");
    public static final Logger LOGGER = LoggerFactory.getLogger("selling-bin");
    public static ArrayList<Trade> trades = new ArrayList<>();
    private static Gson gson = new Gson();

    public static final Block WOODEN_BIN_BLOCK;
    public static final BlockItem WOODEN_BIN_BLOCK_ITEM;
    public static final BlockEntityType<WoodenBinBlockEntity> WOODEN_BIN_BLOCK_ENTITY;
    public static final Identifier WOODEN_BIN = new Identifier("selling-bin", "wooden_bin");

    public static final Block IRON_BIN_BLOCK;
    public static final BlockItem IRON_BIN_BLOCK_ITEM;
    public static final BlockEntityType<IronBinBlockEntity> IRON_BIN_BLOCK_ENTITY;
    public static final Identifier IRON_BIN = new Identifier("selling-bin", "iron_bin");

    public static final Block DIAMOND_BIN_BLOCK;
    public static final BlockItem DIAMOND_BIN_BLOCK_ITEM;
    public static final BlockEntityType<DiamondBinBlockEntity> DIAMOND_BIN_BLOCK_ENTITY;
    public static final Identifier DIAMOND_BIN = new Identifier("selling-bin", "diamond_bin");
    public static final PlayerInventoryManager inventoryManager = new PlayerInventoryManager();
    public static final File inventoryFile = new File("config/selling-bin.dat");


    static {
        WOODEN_BIN_BLOCK = Registry.register(Registries.BLOCK, WOODEN_BIN, new WoodenBinBlock(FabricBlockSettings.copyOf(Blocks.CHEST).requiresTool()));
        WOODEN_BIN_BLOCK_ITEM = Registry.register(Registries.ITEM, WOODEN_BIN, new BlockItem(WOODEN_BIN_BLOCK, new Item.Settings()));
        WOODEN_BIN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, WOODEN_BIN, FabricBlockEntityTypeBuilder.create(WoodenBinBlockEntity::new, WOODEN_BIN_BLOCK).build(null));

        IRON_BIN_BLOCK = Registry.register(Registries.BLOCK, IRON_BIN, new IronBinBlock(FabricBlockSettings.copyOf(Blocks.CHEST).requiresTool()));
        IRON_BIN_BLOCK_ITEM = Registry.register(Registries.ITEM, IRON_BIN, new BlockItem(IRON_BIN_BLOCK, new Item.Settings()));
        IRON_BIN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, IRON_BIN, FabricBlockEntityTypeBuilder.create(IronBinBlockEntity::new, IRON_BIN_BLOCK).build(null));

        DIAMOND_BIN_BLOCK = Registry.register(Registries.BLOCK, DIAMOND_BIN, new DiamondBinBlock(FabricBlockSettings.copyOf(Blocks.CHEST).requiresTool()));
        DIAMOND_BIN_BLOCK_ITEM = Registry.register(Registries.ITEM, DIAMOND_BIN, new BlockItem(DIAMOND_BIN_BLOCK, new Item.Settings()));
        DIAMOND_BIN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, DIAMOND_BIN, FabricBlockEntityTypeBuilder.create(DiamondBinBlockEntity::new, DIAMOND_BIN_BLOCK).build(null));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.add(WOODEN_BIN_BLOCK_ITEM);
            content.add(IRON_BIN_BLOCK_ITEM);
            content.add(DIAMOND_BIN_BLOCK_ITEM);
        });


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("reloadbinconfig")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(context -> {
                    reload();
                    context.getSource().sendMessage(Text.literal("Config reloaded."));
                    return 1;
                })));
    }


    @Override
    public void onInitialize() {
        if (!inventoryFile.exists()) {
            try {
                inventoryFile.createNewFile();

                inventoryManager.save(inventoryFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            inventoryManager.load(inventoryFile);
        }

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                inventoryManager.save(inventoryFile);
            }
        }, 0, 10 * 60 * 1000);

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        if (!(configFile.exists())) {
            try {
                configFile.createNewFile();

                FileWriter fileWriter = new FileWriter(configFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(defaultConfig);
                bufferedWriter.close();

                LOGGER.info("Default config has been written to the file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            JsonObject json = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
            for (String key : json.keySet()) {
                JsonElement tradeElement = json.get(key);
                Trade trade = gson.fromJson(tradeElement, Trade.class);
                trade.setName(key);
                trades.add(trade);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ServerPlayConnectionEvents.INIT.register(ConfigSynchronizer::server);
    }

    public static void reload() {
        if (!(configFile.exists())) {
            try {
                configFile.createNewFile();

                FileWriter fileWriter = new FileWriter(configFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(defaultConfig);
                bufferedWriter.close();

                LOGGER.info("Default config has been written to the file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        trades = new ArrayList<>();

        try {
            JsonObject json = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
            for (String key : json.keySet()) {
                JsonElement tradeElement = json.get(key);
                Trade trade = gson.fromJson(tradeElement, Trade.class);
                trade.setName(key);
                trades.add(trade);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
