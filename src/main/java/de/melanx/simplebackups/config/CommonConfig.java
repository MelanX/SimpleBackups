package de.melanx.simplebackups.config;

import de.melanx.simplebackups.StorageSize;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Deflater;

public class CommonConfig {

    public static final ForgeConfigSpec CONFIG;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    static {
        init(BUILDER);
        CONFIG = BUILDER.build();
    }

    private static ForgeConfigSpec.BooleanValue enabled;
    private static ForgeConfigSpec.EnumValue<BackupType> backupType;
    private static ForgeConfigSpec.IntValue fullBackupTimer;
    private static ForgeConfigSpec.IntValue backupsToKeep;
    private static ForgeConfigSpec.IntValue timer;
    private static ForgeConfigSpec.IntValue compressionLevel;
    private static ForgeConfigSpec.BooleanValue sendMessages;
    private static ForgeConfigSpec.ConfigValue<String> maxDiskSize;
    private static ForgeConfigSpec.ConfigValue<String> outputPath;

    private static ForgeConfigSpec.BooleanValue mc2discord;

    public static void init(ForgeConfigSpec.Builder builder) {
        enabled = builder.comment("If set false, no backups are being made.")
                .define("enabled", true);
        backupType = builder.comment("Defines the backup type.\n- FULL_BACKUPS - always creates full backups\n- MODIFIED_SINCE_LAST - only saves the files which changed since last (partial) backup\n- MODIFIED_SINCE_FULL - saves all files which changed after the last full backup")
                .defineEnum("backupType", BackupType.FULL_BACKUPS);
        fullBackupTimer = builder.comment("How often should a full backup be created if only modified files should be saved? This creates a full backup when x minutes are over and the next backup needs to be done. Once a year is default.")
                .defineInRange("fullBackupTimer", 525960, 1, 5259600);
        backupsToKeep = builder.comment("The max amount of backup files to keep.")
                .defineInRange("backupsToKeep", 10, 1, Short.MAX_VALUE);
        timer = builder.comment("The time between two backups in minutes", "5 = each 5 minutes", "60 = each hour", "1440 = each day")
                .defineInRange("timer", 120, 1, Short.MAX_VALUE);
        compressionLevel = builder.comment("The compression level, 0 is no compression (less cpu usage) and takes a lot of space, 9 is best compression (most cpu usage) and takes less space. -1 is default")
                .defineInRange("compressionLevel", Deflater.DEFAULT_COMPRESSION, Math.min(Deflater.DEFAULT_COMPRESSION, Deflater.NO_COMPRESSION), Deflater.BEST_COMPRESSION);
        sendMessages = builder.comment("Should message be sent when backup is in the making?")
                .define("sendMessages", true);
        maxDiskSize = builder.comment("The max size of storage the backup folder. If it takes more storage, old files will be deleted.",
                        "Needs to be written as <number><space><storage type>",
                        "Valid storage types: B, KB, MB, GB, TB")
                .define("maxDiskSize", "25 GB");
        outputPath = builder.comment("Used to define the output path.")
                .define("outputPath", "simplebackups");

        builder.push("mod_compat");
        mc2discord = builder.comment("Should backup notifications be sent to Discord by using mc2discord? (needs to be installed)")
                .define("mc2discord", true);
        builder.pop();
    }

    public static boolean isEnabled() {
        return enabled.get();
    }

    public static int getBackupsToKeep() {
        return backupsToKeep.get();
    }

    // converts config value from milliseconds to minutes
    public static int getTimer() {
        return timer.get() * 60 * 1000;
    }

    // converts config value from milliseconds to minutes
    public static int getFullBackupTimer() {
        return fullBackupTimer.get() * 60 * 1000;
    }

    public static int getCompressionLevel() {
        return compressionLevel.get();
    }

    public static long getMaxDiskSize() {
        String s = maxDiskSize.get();
        if (s.split(" ").length != 2) {
            s = "25 GB";
        }

        return StorageSize.getBytes(s);
    }

    public static Path getOutputPath() {
        try {
            return Paths.get(outputPath.get()).toRealPath();
        } catch (IOException e) {
            return Paths.get(outputPath.get());
        }
    }

    public static BackupType backupType() {
        return backupType.get();
    }

    public static boolean sendMessages() {
        return sendMessages.get();
    }

    public static boolean mc2discord() {
        return mc2discord.get();
    }
}
