package chip8;

public class Chip8Main {

CPU runChip8 = new CPU();

public final static int WIDTH = 64;
public final static int HEIGHT = 32;
static private byte[] graphics = new byte [64*32]; //Chip 8 has 2048 pixels

//Graphics
//Input function here

runChip8.init();
runchip8.loadROM();

for () {
	//One Chip 8 cycle
	runChip8.cycle();
	
	//Update screen if boolean is true
	if(runChip8.drawflag) {
		createPixel();
	}
	runChip8.storeKeys(;)
}
	


}
