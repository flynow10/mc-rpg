/* Much of the spell casting logic is taken from here https://github.com/M0rica/EpicSpellsPlugin/blob/main/src/main/java/epicspellsplugin/spellcasting/Spellcaster.java */
package com.wagologies.spigotplugin.spell;

import com.wagologies.spigotplugin.player.RPGPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellCast {

    public static int MIN_SEQUENCE_LENGTH = 4;
    private final RPGPlayer player;
    private final Location spellCastLocation;
    private final Vector normalizedPlane;
    private final List<Location> castingPoints = new ArrayList<>();

    public SpellCast(RPGPlayer player, Location spellCastLocation) {
        this.player = player;
        this.spellCastLocation = spellCastLocation;
        this.normalizedPlane = getNormalizedPlane();
    }

    private Vector getNormalizedPlane() {
        return player.getPlayer().getEyeLocation().getDirection().normalize().multiply(-1);
    }

    private Location getPlayerViewSpherePoint() {
        Player bukkitPlayer = player.getPlayer();
        return bukkitPlayer.getEyeLocation().add(bukkitPlayer.getEyeLocation().getDirection().normalize().multiply(3));
    }

    public void tick() {
        Location castingPoint = getPlayerViewSpherePoint();
        if(castingPoints.isEmpty()) {
            castingPoints.add(castingPoint);
            return;
        }
        Location planarCastingPoint = projectPointOnToNormalPlane(castingPoint);
        Location lastPoint = castingPoints.getLast();

        if(planarCastingPoint.distanceSquared(lastPoint) > 0.01) {
            Location interpolatedAverage = lastPoint.clone().add(planarCastingPoint).multiply(0.5);
            castingPoints.add(interpolatedAverage);
            castingPoints.add(planarCastingPoint);
        }

        for (Location point : castingPoints) {
            player.getPlayer().spawnParticle(Particle.ELECTRIC_SPARK, point, 0, 0, 0, 0);
        }
    }

    public List<SpellLine> getPatternLines() {
        return filterPointsToSpellLines(getTransformedCastingPoints(), MIN_SEQUENCE_LENGTH);
    }

    private List<Location> getTransformedCastingPoints() {
        List<Location> transformedPoints = new ArrayList<>();
        if(castingPoints.isEmpty()) {
            return transformedPoints;
        }
        double angle = Math.acos(normalizedPlane.dot(new Vector(0, 0, 1))/ normalizedPlane.length());
        boolean mirror = false;
        if(angle > Math.PI / 2) {
            angle -= Math.PI;
            mirror = true;
        }

        Vector axis = normalizedPlane.getCrossProduct(new Vector(0, 0, 1)).normalize();
        Location firstPoint = castingPoints.getFirst();
        for (int i = 1; i < castingPoints.size(); i++) {
            Vector temp = castingPoints.get(i).toVector().subtract(firstPoint.toVector());
            temp.rotateAroundAxis(axis, angle);
            temp.setZ(0);
            if(mirror) {
                temp.rotateAroundY(Math.PI);
            }
            Location transformedPoint = firstPoint.clone().add(temp);
            transformedPoints.add(transformedPoint);
        }
        return transformedPoints;
    }

    private List<SpellLine> filterPointsToSpellLines(List<Location> points, int minSequenceLength) {
        List<SpellLine> currentPartialSequence = new ArrayList<>();
        List<SpellLine> fullSequence = new ArrayList<>();
        if(points.isEmpty()) {
            return fullSequence;
        }
        for (int i = 1; i < points.size(); i++) {
            Vector temp = points.get(i).toVector().subtract(points.get(i-1).toVector()).normalize();
            double[] dimensions = new double[] {temp.getX(), temp.getY()};
            int[] directionVec = new int[2];
            for (int j = 0; j < 2; j++) {
                double dimension = dimensions[j];
                double direction;

                // Adjusted rounding values for better diagonal line detection
                if(dimension > 0) {
                    direction = Math.floor(dimension + 0.7);
                } else {
                    direction = Math.ceil(dimension - 0.7);
                }

                directionVec[j] = (int) Math.round(direction);
            }

            SpellLine line = SpellLine.getSpellFromDirection(directionVec);
            if(currentPartialSequence.isEmpty() || currentPartialSequence.getFirst().equals(line)) {
                if(!fullSequence.isEmpty() && fullSequence.getLast().equals(line)) {
                    continue;
                }
                currentPartialSequence.add(line);
            } else {
                if(currentPartialSequence.size() >= minSequenceLength) {
                    fullSequence.add(currentPartialSequence.getFirst());
                }
                currentPartialSequence.clear();
                currentPartialSequence.add(line);
            }
        }
        if(currentPartialSequence.size() >= minSequenceLength) {
            fullSequence.add(currentPartialSequence.getFirst());
        }

        return fullSequence;
    }

    public Location projectPointOnToNormalPlane(Location point) {
        double distance = point.toVector().subtract(castingPoints.getFirst().toVector()).dot(normalizedPlane);
        return point.clone().subtract(normalizedPlane.clone().multiply(distance));
    }

    public Location getSpellCastLocation() {
        return spellCastLocation;
    }

    public List<Location> getCastingPoints() {
        return castingPoints;
    }

    public enum SpellLine {
        UP(new int[]{0, 1}),
        DOWN(new int[]{0, -1}),
        LEFT(new int[]{-1, 0}),
        RIGHT(new int[]{1, 0}),
        UP_LEFT(new int[]{-1, 1}),
        UP_RIGHT(new int[]{1, 1}),
        DOWN_LEFT(new int[]{-1, -1}),
        DOWN_RIGHT(new int[]{1, -1});

        private final int[] directionVec;
        SpellLine(int[] directionVec) {
            this.directionVec = directionVec;
        }

        public int[] getDirectionVec() {
            return directionVec;
        }

        public static SpellLine getSpellFromDirection(int[] direction) {
            return Arrays.stream(values()).filter(line -> Arrays.equals(direction, line.directionVec)).findAny().orElseThrow();
        }
    }
}
