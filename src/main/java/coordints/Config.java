package coordints;

import com.electronwill.nightconfig.core.EnumGetMethod;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.DoubleStream;

@SuppressWarnings("unused")
public final class Config {
    private static final Pattern BRACKETS_PATTERN = Pattern.compile("[\\[\\]()]");
    private static final Pattern COORDS_PATTERN = Pattern.compile("-?\\d+\\s-?\\d+,-?\\d+\\s-?\\d+");

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_COORDS_STRINGS = BUILDER
            .comment("", "A list of coordinates that the player can't share. Format: \"[startX endX],[startZ endZ]\".",
                    "For example, blacklistedCoords=[\"[0 200],[300 1000]\"] will block the player from sharing coordinates between x=0 and x=200, and z=300 and z=1000.")
            .defineListAllowEmpty(Collections.singletonList("blacklistedCoords"), Collections::emptyList, Config::validateCoords);
    private static final ForgeConfigSpec.EnumValue<Action> BLACKLIST_ACTION_STRING = BUILDER
            .comment("", "The mode that the mod uses when it detects blacklisted coordinates.",
                    "\"Redact\" will replace the coordinates with a random amount of Xs",
                    "\"Randomise\" will replace the coordinates with irrelevant random coordinates to confuse players",
                    "\"Block\" will prevent the message from being sent altogether.")
            .defineEnum("blacklistAction", Action.REDACT, EnumGetMethod.NAME_IGNORECASE, Action.values());
    public static final IConfigSpec<ForgeConfigSpec> SPEC = BUILDER.build();

    static boolean USE_BLACKLIST;
    static double[] BLACKLISTED_COORDS;
    static Action BLACKLIST_ACTION;

    private static boolean validateCoords(final Object obj) {
        return obj instanceof final String coords && COORDS_PATTERN.matcher(BRACKETS_PATTERN.matcher(coords).replaceAll("")).matches();
    }
    public static void onLoad(final ModConfig event) {
        USE_BLACKLIST = !BLACKLISTED_COORDS_STRINGS.get().isEmpty();

        if (USE_BLACKLIST) {
            final DoubleArrayList tmpXCoords = new DoubleArrayList();
            final DoubleArrayList tmpZCoords = new DoubleArrayList();

            for (final var coords : BLACKLISTED_COORDS_STRINGS.get()) {
                final String[] coordsStr = BRACKETS_PATTERN.matcher(coords).replaceAll("").split(",", 2);
                final double[] xCoordsRange = Arrays.stream(coordsStr[0].split(" ", 2)).mapToDouble(Double::parseDouble).toArray();
                final double[] zCoordsRange = Arrays.stream(coordsStr[1].split(" ", 2)).mapToDouble(Double::parseDouble).toArray();

                for (final double coord : xCoordsRange)
                    tmpXCoords.add(coord);

                for (final double coord : zCoordsRange)
                    tmpZCoords.add(coord);
            }

            BLACKLISTED_COORDS = DoubleStream.concat(tmpXCoords.doubleStream(), tmpZCoords.doubleStream()).toArray();
        } else {
            BLACKLISTED_COORDS = new double[0];
        }

        BLACKLIST_ACTION = BLACKLIST_ACTION_STRING.get();
    }
}