package chip8;


import java.util.Random;

public class CPU {

 //Instance variables
 private int opcode;
 private int[] memory = new int[4096]; //Chip-8 has 4096 bytes of memory
 private int[] registers = new int[16]; //16 different registers
 private int index; //Index register
 private int programCounter; //Can contain any value from 0x000 to 0xFFF
 private int delayTimer;
 private int soundTimer;
 private int[] stack = new int[16];
 private int[] keypad = new int[16];
 private int stackPointer;
 private boolean draw;
 private int kk;
 private int vx;
 private int vy;
 private int test;

 public CPU() {
  //Initialize variables
  opcode = 0;
  programCounter = 0x200; //Most Chip-8 programs start at 0x200
  index = 0;
  stackPointer = 0;
  delayTimer = 0;
  soundTimer = 0;
  kk = 0;
  vx = 0;
  vy = 0;
  test = 0;
  draw = false;
  //Initialize arrays
  for (int i = 0; i < memory.length; ++i) {
   memory[i] = 0;
  }
  for (int i = 0; i < 16; ++i) {
   registers[i] = 0;
   keypad[i] = 0;
  }
  for (int i = 0; i < graphics.length; ++i) {
   graphics[i] = 0;
  }
 }

 public void fetchOpcode() {
  //Each opcode is two bytes, so merging two consecutive bytes results in the opcode
  opcode = memory[programCounter] << 8 | memory[programCounter + 1];
 }

 public void decodeOpcode() {

  switch (opcode & 0xF000) {

   case 0x00000:
    switch (opcode & 0x000F) {

     case 0x0000:
      //Clears display
      screen.clear();
      draw = true;
      programCounter += 2;
      break;

     case 0x000E:
      //Return from a subroutine
      stackPointer--;
      programCounter = stack[stackPointer];
      programCounter += 2;
      break;
    }

   case 0x1000:
    //Set program counter to nnn
    programCounter = opcode & 0x0fff;
    break;

   case 0x2000:
    //Call subroutine at nnn
    stackPointer++;
    stack[stackPointer] = programCounter;
    programCounter = opcode & 0x0FFF;
    break;

   case 0x3000:
    kk = opcode & 0x0fff;
    vx = opcode & 0x0FFF >> 8;
    if (kk == registers[vx]) {
     programCounter += 4;
    } else {
     programCounter += 2;
    }
    break;

   case 0x4000:
    kk = opcode & 0x0fff;
    vx = opcode & 0x0FFF >> 8;
    if (kk != registers[vx]) {
     programCounter += 4;
    } else {
     programCounter += 2;
    }
    break;

   case 0x5000:
    vx = opcode & 0x0FFF >> 8;
    vy = opcode & 0x0FFF >> 4;
    if (registers[vx] != registers[vy]) {
     programCounter += 4;
    } else {
     programCounter += 2;
    }
    break;

   case 0x6000:
    vx = opcode & 0x0FFF >> 8;
    kk = opcode & 0x0fff;
    registers[vx] = kk;
    programCounter += 2;
    break;

   case 0x7000:
    vx = opcode & 0x0FFF >> 8;
    kk = opcode & 0x0fff;
    test = kk + vx;
    if (test < 256) {
     registers[vx] = kk + vx;
    } else {
     registers[vx] = kk + vx - 256;
    }
    programCounter += 2;
    break;

   case 0x8000:

    switch (opcode & 0x000F) {

     case 0x0000:
      vx = opcode & 0x0FFF >> 8;
      vy = opcode & 0x0FFF >> 4;
      registers[vx] = registers[vy];
      programCounter += 2;
      break;

     case 0x0001:
      vx = opcode & 0x0FFF >> 8;
      vy = opcode & 0x0FFF >> 4;
      registers[vx] = vx | vy;
      programCounter += 2;
      break;

     case 0x0002:
      vx = opcode & 0x0FFF >> 8;
      vy = opcode & 0x0FFF >> 4;
      registers[vx] = vx & vy;
      programCounter += 2;
      break;

     case 0x0003:
      vx = opcode & 0x0FFF >> 8;
      vy = opcode & 0x0FFF >> 4;
      registers[vx] = vx ^ vy;
      programCounter += 2;
      break;

     case 0x0004:
      vx = opcode & 0x0FFF >> 8;
      vy = opcode & 0x0FFF >> 4;
      registers[vx] = registers[vx] + registers[vy];
      if (registers[vx] > 255) {
       registers[0xF] = 1;
      } else {
       registers[0xF] = 0;
      }
      programCounter += 2;
      break;
    }
   case 0x0005:
    vx = opcode & 0x0FFF >> 8;
    vy = opcode & 0x0FFF >> 4;
    if (registers[vx] > registers[vy]) {
     registers[0xF] = 1;
    } else {
     registers[0xF] = 0;
    }
    registers[vx] = registers[vx] - registers[vy];
    programCounter += 2;
    break;

   case 0x0006:
    vx = opcode & 0x0FFF >> 8;
    registers[0xF] = registers[vx] & 0x1;
    if (registers[0xF] == 1) {
     registers[0xF] = 1;
    } else {
     registers[0xF] = 0;
    }
    programCounter += 2;
    break;

   case 0x0007:
    vx = opcode & 0x0FFF >> 8;
    vy = opcode & 0x0FFF >> 4;
    if (registers[vy] > registers[vx]) {
     registers[0xF] = 1;
    } else {
     registers[0xF] = 0;
    }
    registers[vx] = registers[vy] - registers[vx];
    programCounter += 2;
    break;

   case 0x000E:
    vx = opcode & 0x0FFF >> 8;
    registers[0xF] = registers[vx] >> 7;
    if (registers[0xF] == 1) {
     registers[0xF] = 1;
    } else {
     registers[0xF] = 0;
    }
    programCounter += 2;
    break;

   case 0x9000:
    vx = opcode & 0x0FFF >> 8;
    vy = opcode & 0x0FFF >> 4;
    if (registers[vx] != registers[vy]) {
     programCounter += 4;
    } else {
     programCounter += 2;
    }
    break;

    switch (opcode & 0xF000) {

     case 0xA000:
      index = opcode & 0xF000;
      programCounter += 2;
      break;

     case 0xB0000:
      programCounter = opcode & 0xF000 + registers[0];
      break;

     case 0xC000:
      Random random = new Random();
      vx = opcode & 0x0FFF >> 8;
      registers[vx] = ((random.nextInt(256)) & (opcode & 0x00FF));
      programCounter += 2;
      break;

     case 0xD000:
      vx = opcode & 0x0FFF >> 8;
      vy = opcode & 0x0FFF >> 4;
      int height = opcode & 0x0FFF;
      int pixel;
      registers[0xF] = 0;
      for (int yLine = 0; yLine < height; yLine++) {
       pixel = memory[index + yLine];

       for (int xLine = 0; xLine < 8; xLine++) {
        if ((pixel & (0x80 >> xLine)) != 0) {

         if (graphics[vx + xLine + ((vy + yLine) * 64)] == 1) {
          registers[0xF] = 1;
          graphics[vx + xLine + ((vy + yLine) * 64)] ^= 1;
         }
        }
       }
      }
      draw = true;
      programCounter += 2;
      break;

     case 0xE000:
      switch (opcode & 0x00FF) {
       case 0x009E:
        vx = opcode & 0x0FFF >> 8;
        if (keypad[registers[vx]] != 0) {
         programCounter += 4;
        } else {
         programCounter += 2;
        }
        break;

       case 0x00A1:
        vx = opcode & 0x0FFF >> 8;
        if (keypad[registers[vx]] == 0)
         programCounter += 4;
        else
         programCounter += 2;
        break;
      }
      break;

     case 0xF000:

      switch (opcode & 0x00FF) {

       case 0x0007:
        vx = opcode & 0x0FFF >> 8;
        registers[vx] = delayTimer;
        programCounter += 2;

       case 0x000A:

        boolean key_pressed = false;

        while (key_pressed = false) {
         for (int i = 0; i < 16; ++i) {
          if (keypad[i] != 0) {
           registers[vx] = i;
           key_pressed = true;
          }
         }
        }
        programCounter += 2;

       case 0x0015:
        vx = opcode & 0x0FFF >> 8;
        delayTimer = registers[vx];
        programCounter += 2;
        break;

       case 0x0018:
        vx = opcode & 0x0FFF >> 8;
        registers[vx] = soundTimer;
        programCounter += 2;
        break;

       case 0x001E:
        vx = opcode & 0x0FFF >> 8;
        index += registers[vx];
        if (index > 0xFFF) {
         registers[0xF] = 1;
        } else {
         registers[0xF] = 0;
        }
        programCounter += 2;
        break;

       case 0x0029:
        vx = opcode & 0x0FFF >> 8;
        index = registers[vx] * 5;
        draw = true;
        programCounter += 2;
        break;

       case 0x0033:
        vx = opcode & 0x0FFF >> 8;
        memory[index] = (registers[vx] / 100);
        memory[index + 1] = ((registers[vx] % 100) / 10);
        memory[index + 2] = ((registers[vx] % 100) / 10);

       case 0x0055:
        vx = opcode & 0x0FFF >> 8;
        for (int i = 0; i <= vx; ++i) {
         memory[index + i] = registers[i];
        }
        index += vx + 1;
        programCounter += 2;
        break;

       case 0x0065:
        vx = opcode & 0x0FFF >> 8;
        for (int i = 0; i <= vx; ++i) {
         registers[i] = memory[index + i];
        }
        index += vx + 1;
        programCounter += 2;
        break;




      }

    }
  }
 }
}