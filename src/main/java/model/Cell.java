package model;

import Item.Item;
import actor.Actor;
/**
 * Represents a single cell on the board.
 * A cell has a fixed position and type, and may optionally contain
 * an item or an actor standing on it.
 */
public class Cell {
    private final Position pos;
    private final CellType title;
    private Item item;
    private Actor occupant;

    public Cell(Position pos, CellType title){
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("CellType cannot be null");
        }

        this.pos = pos;
        this.title = title;
        this.item = null;
        this.occupant = null;
    }
    public Position getPos(){ return pos;}
    public CellType getTitle(){ return title;}

    //Item
    public boolean hasItem(){ return item != null;}
    public Item getItem() { return item;}
    public void setItem(Item item){
        this.item = item;
    }
    public void clearItem(){
        this.item = null;
    }


    //Occupant
    public boolean hasOccupant(){ return occupant != null;}
    public Actor getOccupant(){ return occupant;}
    public void setOccupant(Actor occupant){
        this.occupant = occupant;
    }
    public void clearOccupant() {
        this.occupant = null;
    }
}
