entity LEVEL2 is port (A, in2, B: integer); end entity;
architecture Arch of LEVEL2 is
	signal LEVEL2, LEVEL22, L2: integer;
begin
	LEVEL22 <= LEVEL2;
	LEVEL2 <= in2;
	L2 <= in2;
end architecture;

entity LEVEL1 is port (in1: integer); end entity;
architecture stimuli of LEVEL1 is
begin
 	U2: LEVEL2 port map (1, in1, 2);
end stimuli;

package PKG1 is
	subtype T1 is integer;
end package;

entity LEVEL0 is end;
use WORK.PKG1.all;
architecture stimuli of LEVEL0 is
	signal TOP: integer; -- signal TOP: WORK.PKG1.T1;
begin
	U1: LEVEL1 port map (TOP);
end stimuli;

