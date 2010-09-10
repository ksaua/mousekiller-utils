package no.saua.mousekiller;

import java.awt.Image;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tileset {
	public static class Tiletype {
		byte image_x;
		byte image_y;
		int rotation;
	}
	
	Image tileimage;
	Tiletype[] tiletypes;
	
	public Image getImage() {
		return tileimage;
	}
	public static Tileset load(File file) throws IOException { 
		Tileset ts = new Tileset();
		DataInputStream i = new DataInputStream(new FileInputStream(file));
		
		ts.tileimage = ImageIO.read(new File(i.readUTF()));
		ts.tiletypes = new Tiletype[i.readByte()];
		
		for (int j = 0; j < ts.tiletypes.length; j++) {
			Tiletype tt = new Tiletype();
			tt.image_x = i.readByte();
			tt.image_y = i.readByte();
			tt.rotation = i.readByte() * 90;
			i.readBoolean(); i.readBoolean();
			ts.tiletypes[j] = tt;
		}
		
		return ts;
	}
}
