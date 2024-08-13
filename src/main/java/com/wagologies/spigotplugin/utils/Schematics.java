package com.wagologies.spigotplugin.utils;

import com.google.common.collect.Maps;
import com.sk89q.jnbt.*;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.DataFixer;
import com.wagologies.spigotplugin.dungeon.SpawnerInfo;
import com.wagologies.spigotplugin.mob.MobType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Schematics {

    public static Clipboard LoadSchematic(String schematicName) {
        File schematicFile = GetSchematicFile(schematicName);

        Clipboard clipboard;

        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
            clipboard = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return clipboard;
    }

    public static void SaveSchematic(World world, Region region, String schematicName) {
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(world);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(worldEditWorld, region, clipboard, region.getMinimumPoint());
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        File schematicFile = new File(GetSchematicsFolder(), schematicName + ".schem");
        try(ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematicFile))) {
            writer.write(clipboard);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void PasteSchematic(Vector origin, Clipboard clipboard, World world) throws WorldEditException {
        PasteSchematic(origin, clipboard, new AffineTransform(), world);
    }
    public static void PasteSchematic(Vector origin, Clipboard clipboard, EditSession editSession) throws WorldEditException {
        PasteSchematic(origin, clipboard, new AffineTransform(), editSession);
    }

    public static void PasteSchematic(Vector origin, Clipboard clipboard, Transform transform, World world) throws WorldEditException {
        com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(world);
        try(EditSession editSession = WorldEdit.getInstance().newEditSession(worldEditWorld)) {
            PasteSchematic(origin, clipboard, transform, editSession);
        }
    }

    public static void PasteSchematic(Vector origin, Clipboard clipboard, Transform transform, EditSession editSession) throws WorldEditException {
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
        clipboardHolder.setTransform(transform);
        Operation operation = clipboardHolder
                .createPaste(editSession)
                .to(BlockVector3.at(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()))
                .copyEntities(true)
                .build();
        Operations.complete(operation);
    }

    public static List<SpawnerInfo> ReadSchematic(String schematicName) throws IOException {
        File schematicFile = GetSchematicFile(schematicName);

        NBTInputStream nbtInputStream = new NBTInputStream(new GZIPInputStream(new FileInputStream(schematicFile)));
        NamedTag rootTag = nbtInputStream.readNamedTag();
        CompoundTag schematicTag = (CompoundTag)rootTag.getTag();
        Map<String, Tag> schematic = schematicTag.getValue();
        if (schematic.size() == 1 && schematic.containsKey("Schematic")) {
            schematicTag = requireTag(schematic, "Schematic", CompoundTag.class);
            schematic = schematicTag.getValue();
        }

        if(!schematic.containsKey("BlockEntities")) {
            return new ArrayList<>();
        }
        ListTag tileEntities = requireTag(schematic, "BlockEntities", ListTag.class);

        List<Map<String, Tag>> tileEntityTags = tileEntities.getValue().stream().map((tag) -> (CompoundTag)tag).map(CompoundTag::getValue).toList();

        List<SpawnerInfo> spawnerInfos = new ArrayList<>();

        for (Map<String, Tag> tileEntity : tileEntityTags) {
            String id = requireTag(tileEntity, "Id", StringTag.class).getValue();
            if(!id.equals("minecraft:structure_block")) {
                continue;
            }
            String mobName = requireTag(tileEntity, "name", StringTag.class).getValue().toUpperCase();
            if(!mobName.startsWith("RPG:")) {
                continue;
            }
            MobType mobType;
            try {
                mobType = MobType.valueOf(mobName.substring("RPG:".length()));
            } catch (IllegalArgumentException e) {
                continue;
            }
            int[] pos = requireTag(tileEntity, "Pos", IntArrayTag.class).getValue();
            Vector spawnerLocation = new Vector(pos[0], pos[1], pos[2]);
            int spawnCount = requireTag(tileEntity, "posX", IntTag.class).getValue();

            spawnerInfos.add(new SpawnerInfo(spawnerLocation, mobType, spawnCount));
        }

        return spawnerInfos;
    }

    public static File GetSchematicFile(String schematicName) {
        File schematicFile = new File(GetSchematicsFolder(), schematicName + ".schem");
        if(!schematicFile.exists()) {
            throw new IllegalStateException("Schematic file " + schematicFile + " does not exist");
        }
        if(!schematicFile.canRead()) {
            throw new IllegalStateException("Schematic file " + schematicFile + " is not readable");
        }
        return schematicFile;
    }

    protected static <T extends Tag> T requireTag(Map<String, Tag> items, String key, Class<T> expected) throws IOException {
        if (!items.containsKey(key)) {
            throw new IOException("Schematic file is missing a \"" + key + "\" tag of type " + expected.getName());
        } else {
            Tag tag = items.get(key);
            if (!expected.isInstance(tag)) {
                throw new IOException(key + " tag is not of tag type " + expected.getName() + ", got " + tag.getClass().getName() + " instead");
            } else {
                return expected.cast(tag);
            }
        }
    }

    public static File GetSchematicsFolder() {
        return new File(Bukkit.getWorldContainer(), "schematics");
    }
}
