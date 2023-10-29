package dev.v4lk.sellingbin;

import java.io.Serializable;

public class PlayerInventory implements Serializable {
    private ImplementedInventory woodenBin = ImplementedInventory.ofSize(9);
    private ImplementedInventory ironBin = ImplementedInventory.ofSize(18);
    private ImplementedInventory diamondBin = ImplementedInventory.ofSize(27);

    public ImplementedInventory getWoodenBin() {
        return woodenBin;
    }

    public ImplementedInventory getIronBin() {
        return ironBin;
    }

    public ImplementedInventory getDiamondBin() {
        return diamondBin;
    }
}
