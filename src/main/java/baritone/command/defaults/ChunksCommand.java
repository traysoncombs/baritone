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
            XtoZ = true;
            closest = closestChunk(new int[][]{new int[]{chunk1X, chunk1Z}, new int[]{chunk2X, chunk2Z}});
            up = Math.abs(closest[0][0]) < Math.abs(closest[1][0]) ? true:false;
        }
        else{
            XtoZ = false;
            closest = closestChunk(new int[][]{new int[]{chunk1X, chunk1Z}, new int[]{chunk2X, chunk2Z}});
            up = Math.abs(closest[0][1]) < Math.abs(closest[1][1]) ? true:false;
        }
        baritone.getCustomGoalProcess().setGoalAndPath(new goalXZ(closest[0][0]*16, closest[0][1]*16));
        int[][] queue = new int[][];
        while (true){
            if (XtoZ){
                queue.add(new int[]{});
                change = up ? (chunkX < 0 ? change - 8: change + 8 ) : (chunkX < 0 ? change + 8: change - 8);
            }
            else{
                
            }
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
        
        int chunk1 = Math.abs(chunks[0][0])-Math.abs(ctx.player().posX)+ Math.abs(chunks[0][1])-Math.abs(ctx.player().posZ)
        int chunk2 = Math.abs(chunks[1][0])-Math.abs(ctx.player().posX)+ Math.abs(chunks[1][1])-Math.abs(ctx.player().posZ)
        int[][] closestFirst = new int[][];
        if (chunk1 < chunk2){
            closestFirst[0] = chunks[0];
        }
        else{
            closestFirst[1] = chunks[0];
        }
        return closestFirst;
        
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

public class chunk{
    public int x;
    public int z;
    public int xCoord;
    public int zCoord;
    public chunk(int x, int z){
        this.xCoord = x;
        this.zCoord = z;
        this.x = Math.floor(x/16);
        this.z = Math.floor(z/16);
    }
    
}
