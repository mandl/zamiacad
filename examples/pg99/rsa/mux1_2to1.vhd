--------------------------------------------------------------------------
--  Crypto Chip
--  Copyright (C) 1999, Projektgruppe WS98/99
--  University of Stuttgart / Department of Computer Science / IFI-RA
--------------------------------------------------------------------------
-- Designers : Arno Wacker
-- Group     : RSA
--------------------------------------------------------------------
-- Design Unit Name : 1 Bit 2-zu-1 Multiplexer
-- Purpose :  Part of the RSA-module-core for the cryptochip "pg99"
-- 
-- File Name : mux1_2to1.vhd
--------------------------------------------------------------------
-- Simulator : SYNOPSYS VHDL System Simulator (VSS) Version 3.2.a
--------------------------------------------------------------------
-- Date            | Changes
-- 21.12.98        | 21.12.98
--                 |
-----------------------------------------------------------------------

--------------------------------------------------------------------------
--  Was implementiert wird
--  Es wird ein 1 Bit 2  zu 1 Multiplexer implementiert
--------------------------------------------------------------------------

library IEEE;
  use IEEE.std_logic_1164.all;
  use IEEE.std_logic_arith.all;
  use IEEE.std_logic_unsigned.all;



entity MUX1_2to1 is
  port (  A	: in  	std_logic; 
	  B	: in 	std_logic;
          Sel	: in 	std_logic;
	  Q	: out   std_logic
	);
end MUX1_2to1;



architecture BEHAV of MUX1_2to1 is

  begin

    Q <= A when Sel='0' else
         B ;			-- Sel='1'

  end BEHAV;
