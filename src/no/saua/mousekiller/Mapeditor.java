package no.saua.mousekiller;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import no.saua.mousekiller.Tileset.Tiletype;

public class Mapeditor extends JFrame implements MouseListener, MouseMotionListener {
	public static final Color bgcolor = new Color(0,0,0);
	public static int TILESIZE = 32;
	
	private class Tilechooser extends JPanel implements MouseListener {
		Tileset tileset;
		byte selected = -1;

		public Tilechooser() {
			setVisible(true);
			addMouseListener(Tilechooser.this);
		}
		
		public void setTileset(Tileset c) {
			setPreferredSize(new Dimension((c.tiletypes.length / 2 + 1) * 35, 70));
			tileset = c;
			repaint();
		}
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g; 
			g2.translate(0, 2);
			if (tileset != null) {
				for (int i = 0; i < tileset.tiletypes.length; i++) {
					Tiletype tt = tileset.tiletypes[i];
					int x = (i / 2) * (TILESIZE + 3);
					int y = (i % 2) * (TILESIZE + 3);
					int ix = tt.image_x * TILESIZE;
					int iy = tt.image_y * TILESIZE;
					g2.translate(3 + x + TILESIZE / 2, y  + TILESIZE / 2);
					
					g2.rotate(-Math.toRadians(tt.rotation));
					g2.drawImage(tileset.tileimage, -16, -16, 16, 16, ix, iy, ix + TILESIZE, iy + TILESIZE, null);
					g2.rotate(Math.toRadians(tt.rotation));
					if (i == selected) {
						g2.setColor(Color.red);
						g2.fillOval(-TILESIZE / 8, -TILESIZE / 8, TILESIZE / 4, TILESIZE / 4);
					}
					g2.translate(-3 - x - TILESIZE / 2, -y - TILESIZE / 2);
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			int tx = (e.getX() - 3) / (TILESIZE + 3);
			int ty = e.getY() / (TILESIZE + 3);
			
			byte s = (byte) (tx * 2 + ty);
			System.out.println(s);
			if (tileset.tiletypes.length > s) {
				selected = s;
			} else {
				selected = -1;
			}
			repaint();
		}
		
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
	}
	
	private class Maprenderer extends JPanel {
		Map map;
		boolean grid;
		public Maprenderer() {
			setVisible(true);
		}
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			if (map != null) {
				g2.setColor(bgcolor);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.translate(0, getHeight());
				g2.scale(1, -1);
				
				// Draw all tiles regardless of height
				if (!only_show_height) {
					for (int tilez = 0; tilez < map.sizez; tilez++) {
						for (int tiley = 0; tiley < map.sizey; tiley++) {
							for (int tilex = 0; tilex < map.sizex; tilex++) {
								drawTile(g2, map.tiles[tilez][tiley][tilex], tilex, tiley);
							}	
						}
					}
				} else { // Draw only a selected height
					System.out.println("Not drawing everything");
					for (int tiley = 0; tiley < map.sizey; tiley++) {
						for (int tilex = 0; tilex < map.sizex; tilex++) {
							drawTile(g2, map.tiles[selected_height][tiley][tilex], tilex, tiley);
						}	
					}
				}
				
				if (grid) {
					g2.setColor(Color.black);
					for (int tilex = 0; tilex < map.sizex; tilex++) {
						g2.drawLine(tilex * TILESIZE, 0, tilex * TILESIZE, getHeight());
					}
					for (int tiley = 0; tiley < map.sizey; tiley++) {
						g2.drawLine(0, tiley * TILESIZE, getWidth(), tiley * TILESIZE);
					}
				}
			}
		}
		private void drawTile(Graphics2D g, int tileId, int tilex, int tiley) {
			if (tileId == -1) return; // Draw a background color?
			Tiletype tt = map.tileset.tiletypes[tileId];
			int ix = tt.image_x * TILESIZE;
			int iy = tt.image_y * TILESIZE;
			
			g.translate(tilex * TILESIZE + TILESIZE / 2, tiley * TILESIZE + TILESIZE / 2);
			g.scale(1, -1);
			g.rotate(-Math.toRadians(tt.rotation));
			g.drawImage(map.tileset.tileimage, -16, -16, 16, 16, ix, iy, ix + TILESIZE, iy + TILESIZE, null);
			g.rotate(Math.toRadians(tt.rotation));
			g.scale(1, -1);
			g.translate(-tilex * TILESIZE - TILESIZE / 2, -tiley * TILESIZE - TILESIZE / 2);
		}
		public void setMap(Map map) {
			this.map = map;
			setPreferredSize(new Dimension(TILESIZE * map.sizex,TILESIZE * map.sizey));			
		}
	}
	
	Maprenderer maprenderer;
	Tilechooser tilechooser;
	boolean pressed;
	
	int selected_height;
	boolean only_show_height;
	
	public Mapeditor() throws IOException {
		super("Mapeditor");
		
		setLayout(new BorderLayout());
		maprenderer = new Maprenderer();
		tilechooser = new Tilechooser();
		
		maprenderer.addMouseListener(this);
		maprenderer.addMouseMotionListener(this);
		
		Tileset defaultTs = Tileset.load(new File("tiletypes"));
		maprenderer.setMap(new Map(defaultTs, 20, 20));
		tilechooser.setTileset(defaultTs);
		
		add(maprenderer, BorderLayout.CENTER);
		add(tilechooser, BorderLayout.SOUTH);
		
		JPanel side = new JPanel();
		side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		side.add(save);
		add(side, BorderLayout.WEST);
		
		JTextField height = new JTextField("0");
		height.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				if (Character.isDigit(arg0.getKeyChar())) {
					set_height(Integer.parseInt(String.valueOf(arg0.getKeyChar())));
				}
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});
		side.add(height);
		final JCheckBox onlyshowheight = new JCheckBox("Only show selected height");
		onlyshowheight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				set_only_show_height(onlyshowheight.isSelected());
			}
		});
		side.add(onlyshowheight);
		
		JButton erase = new JButton("Erase");
		erase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tilechooser.selected = -1;
				tilechooser.repaint();
			}
		});
		side.add(erase);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}
	
	public void set_only_show_height(boolean b) {
		only_show_height = b;
		System.out.println(b);
		maprenderer.repaint();
	}
	
	public void set_height(int height) {
		selected_height = height;
		maprenderer.repaint();
	}
	
	public void save() {
		try {
			maprenderer.map.save(new File("map"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		JFrame frame = new Mapeditor();
		frame.setVisible(true);
	}
	
	public void changeTileToSelected(int x, int y) {
//		if (tilechooser.selected == -1) return;
		
		int ny = maprenderer.getHeight() - y;
		int tx = x / TILESIZE;
		int ty = ny / TILESIZE;
		int tz = selected_height;
		
		System.out.println(x + ", " + y + " => " + ny +  ", " + maprenderer.getHeight() + " : " + tx + ", " + ty + ", " + tz);
		maprenderer.map.tiles[tz][ty][tx] = tilechooser.selected;
		maprenderer.repaint();
	}

	public void mouseClicked(MouseEvent e) {
		changeTileToSelected(e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e) {
		changeTileToSelected(e.getX(), e.getY());
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}
