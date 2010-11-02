-------------------------------------------------------------------------------
-- Title      : manikaltera
-- Project    : 
-------------------------------------------------------------------------------
-- File       : manikaltera.vhd
-- Author     : 
-- Company    : 
-- Created    : 2006-02-28
-- Last update: 2006-02-28
-- Platform   : 
-- Standard   : VHDL'87
-------------------------------------------------------------------------------
-- Description: Contains ALTERA specific component declaration. Generated by
--              ALTERA's generator
-------------------------------------------------------------------------------
-- Copyright (c) 2006 
-------------------------------------------------------------------------------
-- Revisions  :
-- Date        Version  Author  Description
-- 2006-02-28  1.0      sdutta	Created
-------------------------------------------------------------------------------

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;
use IEEE.STD_LOGIC_1164.all;

library std;
use STD.textio.All;

library UNISIM;
use UNISIM.vcomponents.all;

use work.manikconfig.all;
use work.manikpackage.all;

package manikaltera is
    
    component altsyncram
        generic (address_aclr_a                     : string  := "NONE";
                 address_aclr_b                     : string  := "NONE";
                 address_reg_b                      : string  := "CLOCK1";
                 byte_size                          : natural := 8;
                 byteena_aclr_a                     : string  := "UNUSED";
                 byteena_aclr_b                     : string  := "NONE";
                 byteena_reg_b                      : string  := "CLOCK1";
                 clock_enable_input_a               : string  := "NORMAL";
                 clock_enable_input_b               : string  := "NORMAL";
                 clock_enable_output_a              : string  := "NORMAL";
                 clock_enable_output_b              : string  := "NORMAL";
                 intended_device_family             : string  := Altera_Family;
                 implement_in_les                   : string  := "OFF";
                 indata_aclr_a                      : string  := "NONE";
                 indata_aclr_b                      : string  := "NONE";
                 indata_reg_b                       : string  := "CLOCK1";
                 init_file                          : string  := "../../synth/quartus/zeroinit.hex";
                 init_file_layout                   : string  := "PORT_A";
                 maximum_depth                      : natural := 0;
                 numwords_a                         : natural := 2048;
                 numwords_b                         : natural := 2048;
                 operation_mode                     : string  := "BIDIR_DUAL_PORT";
                 outdata_aclr_a                     : string  := "CLEAR0";
                 outdata_aclr_b                     : string  := "CLEAR1";
                 outdata_reg_a                      : string  := "CLOCK0";
                 outdata_reg_b                      : string  := "CLOCK1";
                 power_up_uninitialized             : string  := "FALSE";
                 ram_block_type                     : string  := "AUTO";
                 rdcontrol_aclr_b                   : string  := "NONE";
                 rdcontrol_reg_b                    : string  := "CLOCK1";
                 read_during_write_mode_mixed_ports : string  := "DONT_CARE";
                 width_a                            : natural := 9;
                 width_b                            : natural := 9;
                 width_byteena_a                    : natural := 1;
                 width_byteena_b                    : natural := 1;
                 widthad_a                          : natural := 11;
                 widthad_b                          : natural := 11;
                 wrcontrol_aclr_a                   : string  := "NONE";
                 wrcontrol_aclr_b                   : string  := "NONE";
                 wrcontrol_wraddress_reg_b          : string  := "CLOCK1";
                 lpm_hint                           : string  := "UNUSED";
                 lpm_type                           : string  := "altsyncram");
        port(aclr0          : in  std_logic                                    := '0';
             aclr1          : in  std_logic                                    := '0';
             address_a      : in  std_logic_vector(widthad_a-1 downto 0);
             address_b      : in  std_logic_vector(widthad_b-1 downto 0)       := (others => '1');
             addressstall_a : in  std_logic                                    := '0';
             addressstall_b : in  std_logic                                    := '0';
             byteena_a      : in  std_logic_vector(width_byteena_a-1 downto 0) := (others => '1');
             byteena_b      : in  std_logic_vector(width_byteena_b-1 downto 0) := (others => '1');
             clock0         : in  std_logic                                    := '1';
             clock1         : in  std_logic                                    := '1';
             clocken0       : in  std_logic                                    := '1';
             clocken1       : in  std_logic                                    := '1';
             data_a         : in  std_logic_vector(width_a-1 downto 0)         := (others => '1');
             data_b         : in  std_logic_vector(width_b-1 downto 0)         := (others => '1');
             q_a            : out std_logic_vector(width_a-1 downto 0);
             q_b            : out std_logic_vector(width_b-1 downto 0);
             rden_b         : in  std_logic                                    := '1';
             wren_a         : in  std_logic                                    := '0';
             wren_b         : in  std_logic                                    := '0');
    end component;

    component apll
        port (inclk0 : IN  STD_LOGIC := '0';
              c0     : OUT STD_LOGIC);
    end component;
    
    component alt3pram
        generic (intended_device_family : STRING;
                 width                  : NATURAL;
                 widthad                : NATURAL;
                 ram_block_type		: STRING;
                 indata_reg             : STRING;
                 write_reg              : STRING;
                 rdaddress_reg_a        : STRING;
                 rdaddress_reg_b        : STRING;
                 rdcontrol_reg_a        : STRING;
                 rdcontrol_reg_b        : STRING;
                 outdata_reg_a          : STRING;
                 outdata_reg_b          : STRING;
                 indata_aclr            : STRING;
                 write_aclr             : STRING;
                 rdaddress_aclr_a       : STRING;
                 rdaddress_aclr_b       : STRING;
                 rdcontrol_aclr_a       : STRING;
                 rdcontrol_aclr_b       : STRING;
                 outdata_aclr_a         : STRING;
                 outdata_aclr_b         : STRING;
                 lpm_type               : STRING);
        port (qa          : OUT STD_LOGIC_VECTOR (31 DOWNTO 0);
              qb          : OUT STD_LOGIC_VECTOR (31 DOWNTO 0);
              wren        : IN  STD_LOGIC;
              inclock     : IN  STD_LOGIC;
              data        : IN  STD_LOGIC_VECTOR (31 DOWNTO 0);
              rdaddress_a : IN  STD_LOGIC_VECTOR (3 DOWNTO 0);
              wraddress   : IN  STD_LOGIC_VECTOR (3 DOWNTO 0);
              rdaddress_b : IN  STD_LOGIC_VECTOR (3 DOWNTO 0));
    end component;

    component lpm_add_sub
        generic (lpm_width     : NATURAL := 32;
                 lpm_direction : STRING  := "UNUSED";
                 lpm_type      : STRING  := "LPM_ADD_SUB";
                 lpm_hint      : STRING  := "ONE_INPUT_IS_CONSTANT=NO,CIN_USED=YES");
        port (dataa   : IN  STD_LOGIC_VECTOR (31 DOWNTO 0);
              add_sub : IN  STD_LOGIC;
              datab   : IN  STD_LOGIC_VECTOR (31 DOWNTO 0);
              cin     : IN  STD_LOGIC;
              cout    : OUT STD_LOGIC;
              result  : OUT STD_LOGIC_VECTOR (31 DOWNTO 0));
    end component;

    component LPM_FF
        generic (lpm_width  : natural;
                 lpm_avalue : string := "UNUSED";
                 lpm_svalue : string := "UNUSED";
                 lpm_pvalue : string := "UNUSED";
                 lpm_fftype : string := "DFF";
                 lpm_type   : string := "LPM_FF";
                 lpm_hint   : string := "UNUSED");
        port (data   : in  std_logic_vector(lpm_width-1 downto 0) := (OTHERS => '1');
              clock  : in  std_logic;
              enable : in  std_logic                              := '1';
              aclr   : in  std_logic                              := '0';
              aset   : in  std_logic                              := '0';
              aload  : in  std_logic                              := '0';
              sclr   : in  std_logic                              := '0';
              sset   : in  std_logic                              := '0';
              sload  : in  std_logic                              := '0';
              q      : out std_logic_vector(lpm_width-1 downto 0));        
    end component;

    component dcfifo
        generic (lpm_width               : natural;
                 lpm_widthu              : natural;
                 lpm_numwords            : natural;
                 delay_rdusedw           : natural := 1;
                 delay_wrusedw           : natural := 1;
                 rdsync_delaypipe        : natural := 3;
                 wrsync_delaypipe        : natural := 3;
                 intended_device_family  : string;
                 lpm_showahead           : string;
                 underflow_checking      : string;
                 overflow_checking       : string;
                 clocks_are_synchronized : string;
                 use_eab                 : string;
                 add_ram_output_register : string;
                 add_width               : natural := 1;
                 lpm_hint                : string  := "USE_EAB=OFF";
                 lpm_type                : string);
        port (data    : in  std_logic_vector(lpm_width-1 downto 0);
              rdclk   : in  std_logic;
              wrclk   : in  std_logic;
              aclr    : in  std_logic := '0';
              rdreq   : in  std_logic;
              wrreq   : in  std_logic;
              rdfull  : out std_logic;
              wrfull  : out std_logic;
              rdempty : out std_logic;
              wrempty : out std_logic;
              rdusedw : out std_logic_vector(lpm_widthu-1 downto 0);
              wrusedw : out std_logic_vector(lpm_widthu-1 downto 0);
              q       : out std_logic_vector(lpm_width-1 downto 0));
    end component;
    
    component mux41
        port (data3x : IN  STD_LOGIC_VECTOR (31 DOWNTO 0);
              data2x : IN  STD_LOGIC_VECTOR (31 DOWNTO 0);
              data1x : IN  STD_LOGIC_VECTOR (31 DOWNTO 0);
              data0x : IN  STD_LOGIC_VECTOR (31 DOWNTO 0);
              sel    : IN  STD_LOGIC_VECTOR (1 DOWNTO 0);
              result : OUT STD_LOGIC_VECTOR (31 DOWNTO 0));
    end component;

    
end manikaltera;
