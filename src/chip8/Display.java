package chip8;

import javax.swing.*;
import java.awt.*;

public class Display {
	public int scaler = 12;
	JFrame frame = new JFrame();

public Display() {
	boolean[][] screenBool = new boolean[Chip8Main.WIDTH][Chip8Main.HEIGHT];
	frame.setSize(new Dimension(Chip8Main.WIDTH * scaler,Chip8Main.HEIGHT * scaler));
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.add(new JPanel(){
		public void paintComponent(Graphics gfx){
			gfx.setColor(Color.BLACK);
			gfx.fillRect(0, 0,Chip8Main.WIDTH * scaler, Chip8Main.HEIGHT * scaler);
			for (int y = 0; y < Chip8Main.WIDTH; y++) {
				for (int x = 0; y < Chip8Main.WIDTH; x++) {
					if(screenBool[y][x])gfx.fillRect(y * scaler, x * scaler, scaler, scaler);
				
		}
		}
	}
});
	frame.setVisible(true);
}

public void repaint() {
	frame.repaint();

}
	

	
	
}
