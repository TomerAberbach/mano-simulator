# Mano Simulator Examples

## Change `RESULT` variable to `!X`

```Assembly
LDA X
CMA
STA RESULT
HLT

X, HEX F0F0
RESULT, HEX 0000
```

`RESULT` is `0F0F`

## `X` * `Y` multiplication

```Assembly
LDA X
SZA
BUN Y_0_CHECK
BUN END

Y_0_CHECK, LDA Y
SZA
BUN MUL
BUN END

MUL, LDA RESULT
ADD X
STA RESULT
LDA MUL_COUNTER
INC
STA MUL_COUNTER
CMA
AND Y
SZA
BUN MUL

END, HLT
X, DEC 1
Y, DEC 5
MUL_COUNTER, DEC 0
RESULT, HEX 0000
```

`RESULT` is `5`

## `X` + `Y`

```Assembly
LDA X
ADD Y
STA RESULT
HLT

X, DEC 10
Y, DEC 123
RESULT, DEC 0
```

`RESULT` is 133 (HEX 85)

## `X` AND `Y`

```Assembly
LDA X
AND Y
STA RESULT
HLT

X, HEX FF0F
Y, HEX 00F0
RESULT, HEX 0000
```

`RESULT` is 0

## `X`, `Y` variables XNOR Code

```Assembly
LDA X
AND Y
CMA
STA T
LDA X
CMA
STA W
LDA Y
CMA
AND W
CMA
AND T
CMA
STA RESULT
HLT

X, HEX FFFF
Y, HEX FFFF
T, HEX 0000
W, HEX 0000
RESULT, HEX 0000
```

`RESULT` is `FFFF`