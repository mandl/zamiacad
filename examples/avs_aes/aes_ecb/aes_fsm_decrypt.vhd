-------------------------------------------------------------------------------
-- This file is part of the project  MYPROJECTNAME
-- see: MYPROJECTURL
--
-- description:
-- Statemachine controlling the decryption datapath within aes_core.vhd does no
-- dataprocessing itself but only set enables and multiplexer selector ports
--
-- Author(s):
--	   Thomas Ruschival -- ruschi@opencores.org (www.ruschival.de)
--
--------------------------------------------------------------------------------
-- Copyright (c) 2009, Thomas Ruschival
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without modification,
-- are permitted provided that the following conditions are met:
--    * Redistributions of source code must retain the above copyright notice,
--    this list of conditions and the following disclaimer.
--    * Redistributions in binary form must reproduce the above copyright notice,
--    this list of conditions and the following disclaimer in the documentation
--    and/or other materials provided with the distribution.
--    * Neither the name of the  nor the names of its contributors
--    may be used to endorse or promote products derived from this software without
--    specific prior written permission.
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
-- ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
-- LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
-- OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
-- SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
-- INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
-- CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
-- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
-- THE POSSIBILITY OF SUCH DAMAGE
-------------------------------------------------------------------------------
-- version management:
-- $Author$
-- $Date$
-- $Revision$			
-------------------------------------------------------------------------------


library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

library aes_ecb_lib;
use aes_ecb_lib.aes_ecb_pkg.all;

entity aes_fsm_decrypt is
	generic (
		NO_ROUNDS : NATURAL := 10);		-- number of rounds
	port (
		clk				: in  STD_LOGIC;  -- System clock
		data_stable		: in  STD_LOGIC;  -- flag valid data/activate the process 
		-- interface for keygenerator
		key_ready		: in  STD_LOGIC;  -- flag valid roundkeys
		round_index_out : out NIBBLE;	-- address for roundkeys memory
		-- Result of Process
		finished		: out STD_LOGIC;  -- flag valid result
		-- Control ports for the Core
		round_type_sel	: out STD_LOGIC_VECTOR(1 downto 0)	-- selector for mux around mixcols
		);
end entity aes_fsm_decrypt;

--
architecture Arch1 of AES_FSM_DECRYPT is
	-- types for the FSM
	type AESstates is (WAIT_KEY, WAIT_DATA, INITIAL_ROUND,DO_ROUND, FINAL_ROUND);

	-- FSM signals
	signal FSM		: AESstates;		-- current state
	signal next_FSM : AESstates;		-- combinational next state

	-- Round Counter & address for keygenerate
	signal round_index		: NIBBLE;	-- currently processed round
	signal next_round_index : NIBBLE;	-- next round, index for keygenerate
	
begin
	---------------------------------------------------------------------------
	-- assign internal values to interface ports
	---------------------------------------------------------------------------
	round_index_out <= next_round_index;		-- roundkey address

	-- purpose: combinational generation of next state for encrytion FSM
	-- type	  : sequential
	-- inputs : FSM, data_stable, key_ready, round_index
	-- outputs: next_FSM
	gen_next_fsm : process (FSM, data_stable, key_ready, round_index) is
	begin  -- process gen_next_fsm			
		case FSM is
			when WAIT_KEY =>
				if key_ready = '1' then
					next_FSM <= WAIT_DATA;
				else
					next_FSM <= WAIT_KEY;
				end if;
			when WAIT_DATA =>
				if data_stable = '1' then
					next_FSM <= INITIAL_ROUND;
				else
					next_FSM <= WAIT_DATA;
				end if;
			when INITIAL_ROUND =>
				next_FSM <= DO_ROUND;
			when DO_ROUND =>
				if round_index = X"1" then  
					next_FSM <= FINAL_ROUND;
				else
					next_FSM <= DO_ROUND;
				end if;
			when FINAL_ROUND =>
				next_FSM <= WAIT_DATA;
				-- pragma synthesis_off 
			when others =>
				report "FSM in strange state - aborting" severity failure;
				-- pragma synthesis_on	
		end case;

		-- Default behaviour in case key is invalid
		if key_ready = '0' then
			next_FSM <= WAIT_KEY;
		end if;
		
	end process gen_next_fsm;


	-- purpose: assign outputs for decryption
	-- type	  : combinational
	-- inputs : FSM
	com_output_assign : process (FSM, round_index) is
	begin  -- process com_output_assign
		-- save defaults for decrypt_FSM
		round_type_sel	 <= "00";		-- signal initial_round
		next_round_index <= round_index;
		finished		 <= '0';

		case FSM is
			when WAIT_KEY =>
				-- start at last index
				next_round_index <= STD_LOGIC_VECTOR(to_unsigned(NO_ROUNDS,4));
			when WAIT_DATA =>
				next_round_index <= STD_LOGIC_VECTOR(to_unsigned(NO_ROUNDS,4));
			when INITIAL_ROUND =>
				round_type_sel	 <= "00";  -- use Data_in for Addkey and pass
										   -- result directly to Inverse Shiftrow
				next_round_index <= STD_LOGIC_VECTOR(UNSIGNED(round_index)-1);
			when DO_ROUND =>
				round_type_sel	 <= "01";
				next_round_index <= STD_LOGIC_VECTOR(UNSIGNED(round_index)-1);
			when FINAL_ROUND =>
				round_type_sel	 <= "01";
				finished		 <= '1';
			when others =>
				null;
		end case;
	end process com_output_assign;

	-- purpose: clocked FSM for decryption
	-- type	  : sequential
	-- inputs : clk, res_n
	clocked_FSM : process (clk) is
	begin  -- process clocked_FSM
		if rising_edge(clk) then		-- rising clock edge
			FSM			<= next_FSM;
			round_index <= next_round_index;
		end if;
	end process clocked_FSM;

end architecture Arch1;
