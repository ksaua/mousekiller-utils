package no.saua.mousekiller;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class TiletypeEditor {
	public static void main(String[] args) throws IOException {
		File f = new File("tiletypes");
		DataOutputStream o = new DataOutputStream(new FileOutputStream(f));

		o.writeUTF("tiles1.png");
		o.writeByte(20);

		/*
		 * byte num_tiletypes
		 * 
		 * For each tiletype
		 *   byte tile_image_x
		 *   byte tile_image_y
		 *   byte rotation
		 *   boolean walkable
		 *   boolean placeable
		 */
		
		// Road 0
		o.writeByte(1);
		o.writeByte(1);
		o.writeByte(0);
		o.writeBoolean(true);
		o.writeBoolean(false);

		// Grass 1
		o.writeByte(0);
		o.writeByte(0);
		o.writeByte(0);
		o.writeBoolean(false);
		o.writeBoolean(false);
		
		// Grass Top 1,2,3,4
		for (int i = 0; i < 4; i++) {
			o.writeByte(1);
			o.writeByte(0);
			o.writeByte(i);
			o.writeBoolean(false);
			o.writeBoolean(false);
		}
		
		// Grass Top-Left 5,6,7,8
		for (int i = 0; i < 4; i++) {
			o.writeByte(2);
			o.writeByte(0);
			o.writeByte(i);
			o.writeBoolean(false);
			o.writeBoolean(false);
		}
		
		// Grass No-bottom 9, 10, 11, 12
		for (int i = 0; i < 4; i++) {
			o.writeByte(3);
			o.writeByte(0);
			o.writeByte(i);
			o.writeBoolean(false);
			o.writeBoolean(false);
		}
		
		// Grass Top-Bottom 13, 14
		for (int i = 0; i < 2; i++) {
			o.writeByte(0);
			o.writeByte(1);
			o.writeByte(i);
			o.writeBoolean(false);
			o.writeBoolean(false);
		}
		
		// Tunnel opening 16, 17, 18, 19
		for (int i = 0; i < 4; i++) {
			o.writeByte(2);
			o.writeByte(1);
			o.writeByte(i);
			o.writeBoolean(true);
			o.writeBoolean(false);
		}
	}
}
