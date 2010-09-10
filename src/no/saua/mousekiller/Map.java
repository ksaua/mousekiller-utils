package no.saua.mousekiller;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Map {
	Tileset tileset;
	byte[][] tiles;
	int sizex;
	int sizey;
	
	public Map(Tileset tileset, int sizex, int sizey) {
		this.sizex = sizex;
		this.sizey = sizey;
		this.tileset = tileset;
		tiles = new byte[sizey][sizex];
		
		// Fill tiles with 1
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length; j++)
				tiles[i][j] = 1;
	}
	
	
	public void setTile(int x, int y, byte tileId) {
		tiles[y][x] = tileId;
	}


	public void save(File file) throws IOException {
		DataOutputStream o = new DataOutputStream(new FileOutputStream(file));
		o.writeUTF("tileset1");
		
		o.writeByte(sizex);
		o.writeByte(sizey);
		
		for (int y = 0; y < sizey; y++) {
			for (int x = 0; x < sizex; x++) {
				o.writeByte(tiles[y][x]);
			}	
		}
	}
}
