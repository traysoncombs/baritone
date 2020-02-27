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
import baritone.api.pathing.goals.GoalStrictDirectionEnds;
import baritone.api.command.Command;
import baritone.api.command.exception.CommandException;
import baritone.api.command.argument.IArgConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.lang.Math;


public class TunnelCommand extends Command {

    public TunnelCommand(IBaritone baritone) {
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
            
        }
        int[][] queue = new int[][];
        
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
    
    private int coordsToChunk(int x, int z){
        return 
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
