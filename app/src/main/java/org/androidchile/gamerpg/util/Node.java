/*    
    Copyright (C) 2012 http://software-talk.org/ (developer@software-talk.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.androidchile.gamerpg.util;

import org.androidchile.gamerpg.model.Character;
import org.androidchile.gamerpg.model.Move;

import static org.androidchile.gamerpg.App.GRASS_SIZE_PIXEL_X;
import static org.androidchile.gamerpg.App.GRASS_SIZE_PIXEL_Y;
import static org.androidchile.gamerpg.App.GRASS_SIZE_X_DIVIDER;
import static org.androidchile.gamerpg.App.GRASS_SIZE_Y_DIVIDER;

/**
 * A simple Example implementation of a Node only overriding the sethCosts
 * method; uses manhatten method.
 */
public class Node extends AbstractNode {

        public Node(int xPosition, int yPosition) {
            super(xPosition, yPosition);
            // do other init stuff
        }

        public void sethCosts(AbstractNode endNode) {
            this.sethCosts((absolute(this.getxPosition() - endNode.getxPosition())
                    + absolute(this.getyPosition() - endNode.getyPosition()))
                    * BASICMOVEMENTCOST);
        }

        private int absolute(int a) {
            return a > 0 ? a : -a;
        }

    public static Move calculateMoveFromPixel(int currentX, int currentY, int touchX, int touchY){
        Move move = new Move();

        // Calculate node fromX
        for(int i = 0; i <= GRASS_SIZE_PIXEL_X; i++){
            if(i != 0) {
                int node = i * GRASS_SIZE_PIXEL_X;
                if(currentX < node && currentX > (node - GRASS_SIZE_PIXEL_X)){
                    move.setFromXNode(i); break;
                }
            }else{
                if(currentX < GRASS_SIZE_PIXEL_X){
                    move.setFromXNode(i); break;
                }
            }
        }

        // Calculate node fromY
        for(int i = 0; i <= GRASS_SIZE_PIXEL_Y; i++){
            if(i != 0) {
                int node = i * GRASS_SIZE_PIXEL_Y;
                if(currentY < node && currentY > (node - GRASS_SIZE_PIXEL_Y)){
                    move.setFromYNode(i); break;
                }
            }else{
                if(currentY < GRASS_SIZE_PIXEL_Y){
                    move.setFromYNode(i); break;
                }
            }
        }

        // Calculate node toX
        for(int i = 0; i < GRASS_SIZE_X_DIVIDER; i++){
            if(i != 0) {
                int node = i * GRASS_SIZE_PIXEL_X;
                if(touchX > (node - GRASS_SIZE_PIXEL_X) && touchX < (node + GRASS_SIZE_PIXEL_X)){
                    move.setToXNode(i); break;
                }
            }else{
                if(touchX < GRASS_SIZE_PIXEL_X){
                    move.setToXNode(i); break;
                }
            }
        }

        // Calculate node toY
        for(int i = 0; i < GRASS_SIZE_Y_DIVIDER; i++){
            if(i != 0) {
                int node = i * GRASS_SIZE_PIXEL_Y;
                if(touchY > (node - GRASS_SIZE_PIXEL_Y) && touchY < (node + GRASS_SIZE_PIXEL_Y)){
                    move.setToYNode(i); break;
                }
            }else{
                if(touchY < GRASS_SIZE_PIXEL_Y){
                    move.setToYNode(i); break;
                }
            }
        }

        return move;
    }

}
