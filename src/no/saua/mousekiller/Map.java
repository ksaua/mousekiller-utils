package no.saua.mousekiller;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Map {
	Tileset tileset;
	byte[][][] tiles;
	int sizex;
	int sizey;
	int sizez = 2;
	
	public Map(Tileset tileset, int sizex, int sizey) {
		this.sizex = sizex;
		this.sizey = sizey;
		this.tileset = tileset;
		tiles = new byte[sizez][sizey][sizex];
		
		// Set tiles to none
		for (int z = 0; z < tiles.length; z++)
			for (int y = 0; y < tiles[z].length; y++)
				for (int x = 0; x < tiles[z][y].length; x++)
					tiles[z][y][x] = -1;
	}
	
	
	public void setTile(int x, int y, int z, byte tileId) {
		tiles[z][y][x] = tileId;
	}


	public void save(File file) throws IOException {
		DataOutputStream o = new DataOutputStream(new FileOutputStream(file));
		o.writeUTF("tileset1");
		o.writeByte(sizex);
		o.writeByte(sizey);
		o.writeByte(sizez);
		
		for (int z = 0; z < sizez; z++) {
			for (int y = 0; y < sizey; y++) {
				for (int x = 0; x < sizex; x++) {
					o.writeByte(tiles[z][y][x]);
				}	
			}
		}
	}
}
