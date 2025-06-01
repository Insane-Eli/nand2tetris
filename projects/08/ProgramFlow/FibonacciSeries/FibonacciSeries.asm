// writerInit
@256
D=A
@SP
M=D
// push argument 1
@1
D=A
@ARG
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D
// pop pointer 1
@SP
AM=M-1
D=M
@THAT
M=D
// push constant 0
@0
D=A
@SP
M=M+1
A=M-1
M=D
// pop that 0
@SP
AM=M-1
D=M
@R13
M=D
@0
D=A
@THAT
A=D+M
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
// push constant 1
@1
D=A
@SP
M=M+1
A=M-1
M=D
// pop that 1
@SP
AM=M-1
D=M
@R13
M=D
@1
D=A
@THAT
A=D+M
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
// push argument 0
@0
D=A
@ARG
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D
// push constant 2
@2
D=A
@SP
M=M+1
A=M-1
M=D
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// pop argument 0
@SP
AM=M-1
D=M
@R13
M=D
@0
D=A
@ARG
A=D+M
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
// writeLabel LOOP
(FibonacciSeries$LOOP)
// push argument 0
@0
D=A
@ARG
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D
// writeIf COMPUTE_ELEMENT
@SP
AM=M-1
D=M
@FibonacciSeries$COMPUTE_ELEMENT
D;JNE
// writeGoto END
@FibonacciSeries$END
0;JMP
// writeLabel COMPUTE_ELEMENT
(FibonacciSeries$COMPUTE_ELEMENT)
// push that 0
@0
D=A
@THAT
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D
// push that 1
@1
D=A
@THAT
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D
// add
@SP
AM=M-1
D=M
A=A-1
M=D+M
// pop that 2
@SP
AM=M-1
D=M
@R13
M=D
@2
D=A
@THAT
A=D+M
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
// push pointer 1
@THAT
D=M
@SP
M=M+1
A=M-1
M=D
// push constant 1
@1
D=A
@SP
M=M+1
A=M-1
M=D
// add
@SP
AM=M-1
D=M
A=A-1
M=D+M
// pop pointer 1
@SP
AM=M-1
D=M
@THAT
M=D
// push argument 0
@0
D=A
@ARG
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D
// push constant 1
@1
D=A
@SP
M=M+1
A=M-1
M=D
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// pop argument 0
@SP
AM=M-1
D=M
@R13
M=D
@0
D=A
@ARG
A=D+M
D=A
@R14
M=D
@R13
D=M
@R14
A=M
M=D
// writeGoto LOOP
@FibonacciSeries$LOOP
0;JMP
// writeLabel END
(FibonacciSeries$END)
