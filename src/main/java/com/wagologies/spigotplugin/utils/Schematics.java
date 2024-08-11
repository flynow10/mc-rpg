package com.wagologies.spigotplugin.utils;

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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Schematics {

    public static Clipboard LoadSchematic(String schematicName) {
        File schematicFile = new File(GetSchematicsFolder(), schematicName + ".schem");
        if(!schematicFile.exists()) {
            throw new IllegalStateException("Schematic file " + schematicFile + " does not exist");
        }
        if(!schematicFile.canRead()) {
            throw new IllegalStateException("Schematic file " + schematicFile + " is not readable");
        }

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

    public static File GetSchematicsFolder() {
        return new File(Bukkit.getWorldContainer(), "schematics");
    }
}
