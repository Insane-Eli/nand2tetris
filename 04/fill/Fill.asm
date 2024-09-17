// In this program, when a key is pressed,
// the screen will go entirely black.
// if a key is not pressed, the screen
// will go entirely white.

// 1. Check to see if a key is currently
//		being pressed (check M[KBD])
// (Retrieve the value in M[KBD] and see
// if it is not zero)
(STEP1)
@KBD
D=M

// 2. If a key is being pressed
//		go to the drawBlackScreen step (5)
@DRAWBLACKSCREEN
D;JNE

// 3. Otherwise 
//		go to the drawWhiteScreen step (4)
@DRAWWHITESCREEN
0;JMP


// 4. drawWhiteScreen...
//	4b. Go to step 1
// 5. drawBlackScreen..
//	5b. Go to step 1

// DRAWBLACKSCREEN
(DRAWBLACKSCREEN)
// 1. Set counter to 16384 (SCREEN)
@SCREEN
D=A
@R1
M=D

// 2. Set Memory at counter to -1
(DBS_STEP2)
@R1
AD=M
M=-1

// 3. Increment counter
D=D+1

// 4. If counter <24576 (KBD)
//		go to step 2
@R1		// Temporarily store D into R1
M=D
@KBD	// @24576
D=A-D
@DBS_STEP2
D;JGT


// 5. otherwise, leave drawBlackScreen 
//		(aka goto step 1)
@STEP1
0;JMP

// DRAWWHITESCREEN
// 1. Set counter to 16384 (SCREEN)
// 2. Set Memory at counter to 0
// 3. Increment counter
// 4. If counter <24576 (KBD)
//		go to step 2
// 5. otherwise, leave drawWhiteScreen
(DRAWWHITESCREEN)
// 1. Set counter to 16384 (SCREEN)
@SCREEN
D=A
@R1
M=D

// 2. Set Memory at counter to 0
(DWS_STEP2)
@R1
AD=M
M=0

// 3. Increment counter
D=D+1

// 4. If counter <24576 (KBD)
//		go to step 2
@R1		// Temporarily store D into R1
M=D
@KBD	// @24576
D=A-D
@DWS_STEP2
D;JGT


// 5. otherwise, leave drawBlackScreen 
//		(aka goto step 1)
@STEP1
0;JMP