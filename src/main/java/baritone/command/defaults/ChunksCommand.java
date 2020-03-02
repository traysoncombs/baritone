/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.command.defaults;

import baritone.api.IBaritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.goals.GoalStrictDirectionEnds;
import baritone.api.command.Command;
import baritone.api.command.exception.CommandException;
import baritone.api.command.argument.IArgConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.lang.Math;


public class ChunksCommand extends Command {
    private boolean XtoZ;
    private int[][] closest;
    private int change = 4;
    private boolean up;
    private int counter = 2;
    private int countTest = 1;
    public ChunksCommand(IBaritone baritone) {
        super(baritone, "chunks");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(4);
        int chunk1X = args.get().getAs(int);
        int chunk1Z = args.get().getAs(int);
        int chunk2X = args.get().getAs(int);
        int chunk2Z = args.get().getAs(int);
        if (Math.abs(chunk1X-chunk2X) >= Math.abs(chunk1Z-chunk2Z)){
            XtoZ = true;  // used to determine whether to go most of the way along x or z
            closest = closestChunk(new int[][]{new int[]{chunk1X, chunk1Z}, new int[]{chunk2X, chunk2Z}});  // finds chunk nearest player
            boolean LargerZDirection = Math.abs(closest[0][1]) < Math.abs(closest[1][1]) ? true:false;  // determines whether to go up along z axis or down
            boolean smallerXDirection = Math.abs(closest[0][0] > closest[1][0]) ? 1 : -1;  // determine whether to go in the negative x direction
        }
        else{
            XtoZ = false;
            closest = closestChunk(new int[][]{new int[]{chunk1X, chunk1Z}, new int[]{chunk2X, chunk2Z}});
            boolean largerXDirection = Math.abs(closest[0][0]) < Math.abs(closest[1][0]) ? true:false;  // determines whether to go up along x axis or down
            boolean smallerZDirection = Math.abs(closest[0][1] > closest[1][1]) ? true : false;  // determines
        }
        baritone.getCustomGoalProcess().setGoalAndPath(new goalXZ(closest[0][0]*16, closest[0][1]*16));
        int[][] queue = new int[][];
        int chunkZ = closest[1][1];
        int chunkX = closest[1][0];
        int tmpChunkX = chunkX;
        int tmpChunkX = chunkZ;
        while (true){
            
            if (XtoZ){
                int yaw;
                if (counter == 2) {  // runs on first run
                    change = largerZDirection ? increaseCoord(chunkZ, 4) : decreaseCoord(chunkZ, 4);  // determine whether to go positive or negative z direction
                }
                if(counter % 2 == 0){  // this should happen each time we go along longer axis, X. This might be redundant idk
                    
                    // I am an autist, all i need to do is add/sub four to/from the closest and furthest chunk and switch between the two
                    tmpChunkX = smallerXDirection > 0 ? decreaseCoords(closest[0][0], 4) : increaseCoords(closest[0][0], 4);
                    smallerXDirection *= -1;  // used to check which direction to go
                }
                else{  // runs as we go along shorter axis, Z
                    tmpChunkX = largerZDirection ? increaseCoord(tmpChunkX, 8) : decreaseCoord(tmpChunkX, 8);
                }
                queue.add(new int[]{tmpChunkX, chunkZ+change, });  // need to add direction
                change = largerZDirection ? increaseCoord(change, 8) : decreaseCoord(change, 8);
            }
            else{
                if (counter == 2) {  // runs on first run
                    change = largerXDirection ? increaseCoord(chunkX, 4) : decreaseCoord(chunkX, 4);
                }
                if(counter % 2 == 0){  // this should happen each time we go along longer axis, X. This might be redundant idk
                    
                    // I am an autist, all i need to do is add/sub four to/from the closest and furthest chunk and switch between the two
                    tmpChunkZ = smallerZDirection > 0 ? decreaseCoords(closest[0][0], 4) : increaseCoords(closest[0][0], 4);
                    smallerZDirection *= -1;  // used to check which direction to go
                }
                else{  // runs as we go along shorter axis, Z
                    tmpChunkZ = largerXDirection ? increaseCoord(tmpChunkZ, 8) : decreaseCoord(tmpChunkZ, 8);
                }
                queue.add(new int[]{chunkX+change, tmpChunkZ, });  // need to add direction
                change = largerZDirection ? increaseCoord(change, 8) : decreaseCoord(change, 8);
            }
            counter++;
        }
        
        Goal goal = new GoalStrictDirectionEnds(
                ctx.playerFeet(),
                ctx.player().getHorizontalFacing()
        );
        baritone.getCustomGoalProcess().setGoalAndPath(goal);
        logDirect(String.format("Goal: %s", goal.toString()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }
    
    private int[][] closestCorner(int[][] chunks){ // returns the closest chunk as first array in array
        //probably wont work
        
        int chunk1 = Math.abs(chunks[0][0])-Math.abs(ctx.player().posX)+ Math.abs(chunks[0][1])-Math.abs(ctx.player().posZ);
        int chunk2 = Math.abs(chunks[1][0])-Math.abs(ctx.player().posX)+ Math.abs(chunks[1][1])-Math.abs(ctx.player().posZ);
        int[][] closestFirst = new int[][];
        if (chunk1 < chunk2){
            closestFirst[0] = chunks[0];
        }
        else{
            closestFirst[1] = chunks[0];
        }
        return closestFirst;
        
    }
    
    private int increaseCoord(int coord, int increase) {
        return coord < 0 ? coord - increase : coord + increase;
    }
    private int decreaseCoord(int coord, int decrease) {
        return coord < 0 ? coord + decrease : coord - decrease;
    }
    
    
    private int[] chunkToCoords(int x, int z){
        return new int[]{x*16, z*16};
    }

    @Override
    public String getShortDesc() {
        return "Explore chunks between two corner chunks.";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The chunks command",
                "",
                "Usage:",
                "> chunks [x coord for first chunk] [z coord for first chunk] [x coord for second chunk] [z coord for second chunk]"
        );
    }
}

