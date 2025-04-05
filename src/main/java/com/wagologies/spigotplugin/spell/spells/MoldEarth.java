package com.wagologies.spigotplugin.spell.spells;

import com.wagologies.spigotplugin.entity.RPGEntity;
import com.wagologies.spigotplugin.spell.BaseSpell;
import com.wagologies.spigotplugin.spell.SpellManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.*;

public class MoldEarth extends BaseSpell {

    public static final Material[] MOLDABLE_MATERIALS = {
            Material.DIRT,
            Material.DIRT_PATH,
            Material.COARSE_DIRT,
            Material.ROOTED_DIRT,
            Material.GRASS_BLOCK,
            Material.STONE,
            Material.COBBLESTONE,
            Material.DIORITE,
            Material.GRANITE,
            Material.ANDESITE,
            Material.CRACKED_STONE_BRICKS,
            Material.STONE_SLAB,
            Material.COBBLESTONE_SLAB,
            Material.COBBLESTONE_STAIRS,
            Material.STONE_STAIRS,
            Material.SNOW_BLOCK,
            Material.OBSIDIAN,
            Material.CRYING_OBSIDIAN,
            Material.WHITE_WOOL,
            Material.WHITE_CONCRETE,
            Material.WHITE_CONCRETE_POWDER,
            Material.ICE,
            Material.PACKED_ICE,
            Material.BLUE_ICE,

    };
    public static final Material[] NON_BLOCKING_MATERIALS = {
            Material.SHORT_GRASS,
            Material.TALL_GRASS,
            Material.FLOWERING_AZALEA,
            Material.ROSE_BUSH,
            Material.CORNFLOWER,
            Material.POPPY,
            Material.OXEYE_DAISY,
            Material.DANDELION,
            Material.BLUE_ORCHID,
            Material.FERN,
            Material.PEONY,
            Material.LARGE_FERN,
    };
    public static final int RISE_TICK_INTERVAL = 2;
    public static final int INNER_RADIUS = 3;
    public static final int OUTER_RADIUS = 5;
    public static final int MAX_HEIGHT = 4;

    private final ModifiedBlockList activeBlocks = new ModifiedBlockList();
    private final List<Block> groundBlocks = new ArrayList<>();
    private int buildLevel = 0;

    public MoldEarth(SpellManager spellManager, RPGEntity spellCaster) {
        super(spellManager, spellCaster);
        List<Block> blocks = getBlocksMoldableBlocksAround(spellCaster.getLocation());
        groundBlocks.addAll(blocks);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount >= 200) {
            if (buildLevel >= 1) {
                if (tickCount % RISE_TICK_INTERVAL == 0) {
                    World world = spellCaster.getWorld();
                    for (Block groundBlock : groundBlocks) {
                        Block newLocation = world.getBlockAt(groundBlock.getX(), groundBlock.getY() + buildLevel,
                                groundBlock.getZ());
                        newLocation.setType(Material.AIR);
                    }
                    buildLevel--;
                }
            } else {
                endSpell();
            }
        } else if (tickCount % RISE_TICK_INTERVAL == 0 && buildLevel < MAX_HEIGHT) {
            buildLevel++;
            placeWallAtHeight();
        }
    }

    private void placeWallAtHeight() {
        World world = spellCaster.getWorld();
        Random random = new Random();

        for (Block groundBlock : groundBlocks) {
            int pilarHeight = buildLevel;
            if (buildLevel == MAX_HEIGHT && random.nextFloat() > 0.5) {
                pilarHeight = buildLevel - 1;
            }
            for (int y = 0; y < pilarHeight; y++) {
                Block riseBlock = world.getBlockAt(groundBlock.getX(), groundBlock.getY() - y, groundBlock.getZ());
                Block newLocation = world.getBlockAt(groundBlock.getX(), groundBlock.getY() + pilarHeight - y,
                        groundBlock.getZ());
                activeBlocks.add(new ModifiedBlock(newLocation));
                if (Arrays.stream(MOLDABLE_MATERIALS).anyMatch(type -> type == riseBlock.getType())) {
                    newLocation.setBlockData(riseBlock.getBlockData());
                } else {
                    newLocation.setBlockData(groundBlock.getBlockData());
                }
            }
        }
    }

    @Override
    public void endSpell() {
        super.endSpell();
        activeBlocks.revertBlocks();
    }

    private List<Block> getBlocksMoldableBlocksAround(Location location) {
        List<Block> blocks = new ArrayList<>();
        World world = location.getWorld();
        assert world != null;
        Vector eyeDirection = location.getDirection();

        for (int x = -MoldEarth.OUTER_RADIUS; x < MoldEarth.OUTER_RADIUS; x++) {
            for (int z = -MoldEarth.OUTER_RADIUS; z < MoldEarth.OUTER_RADIUS; z++) {
                if (eyeDirection.setY(0).dot(new Vector(x, 0, z)) < 0) continue;
                if (x * x + z * z > MoldEarth.OUTER_RADIUS * MoldEarth.OUTER_RADIUS || x * x + z * z <= MoldEarth.INNER_RADIUS * MoldEarth.INNER_RADIUS)
                    continue;

                for (int y = 1; y >= -MoldEarth.OUTER_RADIUS; y--) {
                    Location blockLocation = location.clone().add(x, y, z);
                    Block block = world.getBlockAt(blockLocation);
                    if (block.isEmpty()) continue;
                    if(!world.getBlockAt(blockLocation.add(0, 1, 0)).isEmpty()) break;
                    if (Arrays.stream(NON_BLOCKING_MATERIALS).anyMatch(type -> type == block.getType())) continue;
                    if (Arrays.stream(MOLDABLE_MATERIALS).anyMatch(type -> type == block.getType())) {
                        blocks.add(block);
                    }
                    break;
                }
            }
        }

        return blocks;
    }

    private static class ModifiedBlockList extends ArrayList<ModifiedBlock> {
        @Override
        public boolean add(ModifiedBlock modifiedBlock) {
            if (!this.contains(modifiedBlock)) {
                return super.add(modifiedBlock);
            }
            return false;
        }

        public void revertBlocks() {
            for (ModifiedBlock modifiedBlock : this) {
                modifiedBlock.revert();
            }
            this.clear();
        }
    }

    private static class ModifiedBlock {
        private final Block block;
        private final BlockData initialBlockData;

        public ModifiedBlock(Block block) {
            this.block = block;
            this.initialBlockData = block.getBlockData();
        }

        public void revert() {
            block.setBlockData(initialBlockData, false);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ModifiedBlock that)) return false;
            return Objects.equals(block, that.block);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(block);
        }
    }
}
