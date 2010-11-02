--------------------------------------------------------------------------
--  Crypto Chip
--  Copyright (C) 1999, Projektgruppe WS98/99
--  University of Stuttgart / Department of Computer Science / IFI-RA
--------------------------------------------------------------------------
-- Designers : Thomas Schwarz
-- Group     : RSA
--------------------------------------------------------------------
-- Design Unit Name : Montgomery Multiplizierer
-- Purpose :  Part of the RSA-module-core for the cryptochip "pg99"
-- 
-- File Name :  montgomery.vhd
--------------------------------------------------------------------
-- Simulator : SYNOPSYS VHDL System Simulator (VSS) Version 3.2.a
--------------------------------------------------------------------
-- Date            | Changes
--                 | 
--                 |
-----------------------------------------------------------------------

--------------------------------------------------------------------------
--  Was implementiert wird
--  Modulare Multiplikation nach Montgomery und SRT-Division.
--  Das Schaltnetz fuehrt jeweils einen Iterationsschritt der beiden
--  Algorithmen durch, und speichert das Zwischenergenis in einer
--  Carry-Save Darstellung. In JEDEM Takt wird ein Iterationsschritt
--  berechnet.
--
--  
--------------------------------------------------------------------------


-- Kodierung fuer die SELDATA (1 downto 0) Leitung:
-- 00 M;
-- 01 X;
-- 10 E;
-- 11 frei, evtl. verwendbar, um A direkt zu laden...

library IEEE;
  use IEEE.std_logic_1164.all;
  use IEEE.std_logic_arith.all;
  use IEEE.std_logic_unsigned.all;


-- Kopfkommentar muss noch eingefuegt werden

entity rsamodul is
end rsamodul;




architecture BEHAV of rsamodul is

  constant NumBits : integer := 768;
  signal Y : std_logic_vector((NumBits-1) downto 0);
  signal Z : std_logic_vector((NumBits-1) downto 0);
  signal X : std_logic_vector((NumBits-1) downto 0);
  signal M : std_logic_vector((NumBits-1) downto 0);
  signal R : std_logic_vector((NumBits-1) downto 0);
  signal ETEST : std_logic_vector((NumBits-1) downto 0);
  signal A : std_logic_vector((NumBits-1) downto 0);
  signal B : std_logic_vector((NumBits-1) downto 0);
  signal MULTGO : std_logic := '0';
  signal MULTRDY : std_logic := '0';
  signal EXPGO : std_logic := '0';
  signal EXPRDY : std_logic := '0';

begin

  TEST : process
  variable DATA : std_logic_vector((NumBits-1) downto 0);
  variable CIPH : std_logic_vector((NumBits-1) downto 0);
  begin
    EXPGO <= '0';
    -- Testdaten einlesen
    -- p    = 15895511695482343347343640916899085648996169095265213007693949535406097316800854227950703119861664343991830006526031
    -- q    = 30008600851858759042821697199043026029520161148581919454359707071894426173096545011064119065719780400960213102319061
    -- M    = 477002065805782315837344081746709723444093031537071434199686079076074948203659995059792493039826357535188349099061344319089689284115423144303419479719835693408061615551338548507433833541577045150536823463696844073817743970363976891
    -- SK   = 346802819874206783394997254763159270453869871580477688056443895090870676953243957181684193196526355328430581200183002349097450706421715916116179723013294929464794141258446012770197189080098160796148326664244403376144057240180900643
    -- PK   = 100735831014177128159963977558114340408691412879932859890422364283232205469877001540612804400906307325409965796616065094844080589774631447151936918221907824222335373577046422181462508180952616021425933119075845705951931300038624307
    -- DATA = 219092924103710526484223683029256486983044768243547132495079791644139609108847397662933699695172600234490851785675639560672451631368352798715660888779040787837625606319886526339907864413601339394429736778201365130581996742232618930
    -- CIPH = 309472560801941404577073001759350780719034532254923342274912858677037550464284244834378530253006607333680784996662168575722163634316186755263969553995529684874430210625630904984179971253313278578455519475487756575944548281728757682

    M <= "010011101010011110001101000101010000100100001011011110001111100101101111110011111111011010111000000110000111100001111000100101011001000111011110011111111101000110101100011010110111001101001111110111111011001010001011111111100000110111010101100001000011010010101000010011110000101000111000110000011100101001011101111011010111111000011100110101101000100110111011001011011100000101011000001100100001011000111011111110100011111101011111100111111110111110001100110111111001001110000111100011101000011011001000101011110101010001000111100010100111111000100100111111110100111000001110001110100111000110001000100010011101100001001101100000100100001001010111111010011010001001010101010011000100100111111010011110101100011001111010001100000111110010111001110001110010110010111011";
    ETEST <= "001110010010111101111100110111011001010010001001101100111011000011000001010011110011110001011111100100100110001100010100011101101011000010001010101000001111001101101101110101110011000000111111111100011101001011001000110011111010100010100000101111001010011001101101010010100010100111100010010111100101000010000000001011000100101011001001010011101110111010010100111100100001011101101111001111110011110100000101001100101110010011111101110111100100110001011000100110110110110111010111010010000010011001101010100010011010110101110100110111110000100011101001011111010010110100010001010001110011010111101011001100100011101011111001110011101100110000001101010011010101100101001010000100110001100111111101010000001111110011011001001010011111001111111001000011100100001100100011";
    DATA := "001001000010000010000001101100101010111010010011000111000110010100011010010010011001001101011101000011110100111101000110110110100100001010100011110111101101011101101001111011000110100110011010110000111001000100111111101010111010101001011110100000001011101000011110111011010010110010110000011101111011001010101011110001100110101111000001101110110001010101001000011011001000001001011010011010011010110100110011100101111111010100001010110110000010001010110010001000110000100010011010111110011111100100101010001101111101111110001000110111111010011111101010011011110011101001010011110110010111100110100100000010111011001000000010110111110011010000000000100000100000101001101011000111110100100110100100000001101000110000000100001001000101001111111101000111011011011110110010";
    CIPH := "001100110000011110101101000111110101011001110000100001010000101101011010110000101110010101011001110111100101110110010000110001101111110011101001011100011011111011111000000101011101110001011110001011011101000011011111101110111000101110100110110011101010110011111010000010111011000100010100000100010011110001100011010001111001100000110000110101011100010011111000011000010000011110010011011100100101101101110111011110110011110110011111110010111100110000010101110011010111010000001101001101100011010101101000010100110101000011011000100101111110100000101011100101001000111100101000110011110110010100001110100110111010111011011000100010011111100110111110000111011000100101000111111100011000101000100011011101010100011110010100001000100110000001001110110000011110111110110010";
    X <= DATA;
    wait for 10 ns;
    -- Potenzierung starten
    EXPGO <= '1';
    wait until EXPRDY'event and EXPRDY='1';
    -- Ergebnis auswerten
    assert Y = CIPH;
    EXPGO <= '0';
    wait for 10 ns;

    wait;
    
    -- und wieder dekodieren!
    ETEST <= "000100001001110001010101001010101001000011001010111100010100001011111110000100110101111100111111011011010001011101011011100101010011101010101100000011111001010111101001011111001111111000101010111011111110000111110111000101101000011001010100110110010101010000010111100000011101000111010111101001100101001001110001101110110001100110001111100000011010111000101111110010101111011010000110110010010010100001111011000111110010000001000000101110010110110010101011100100101100000101110001101010100110101011110111111001111001110011010111011111100000110111101110101111001000111001010101000001111011110110000100100001011010010011111110010111110000001110100010101100110100111010100000111001100110101011111110111110000111100100000001000110111110110001100000111001001100010000110011";
    X <= CIPH;
    wait for 10 ns;
    -- Potenzierung starten
    EXPGO <= '1';
    wait until EXPRDY'event and EXPRDY='1';
    -- Ergebnis auswerten
    assert Y = DATA;
    EXPGO <= '0';
    wait for 10 ns;




    -- Testdaten einlesen!
    -- p = 12309467399481529069134276473829095425260919043471573483481259999271638170790627228606332909396974122098647464495949
    -- q = 36298127639191934806564768239538542284497256221334257019274877925120157345635692299740519442848652996129364764082573
    -- M = 446810618836852559818559630692293258140650132731050869318783345152110492110152691482197197011452487968134298373524928192065198626714985696715849989981143351580649266449096418557439231626751395290776609979783806860972438426859996777
    -- SK = 297359193457480480198908762851090963920817338252252726618255979063224448101662761930075455643851634563079822588757348231336049266800699482070487838528682148092541019795443467042964057138342050971128327719913863237613131887324343459
    -- PK = 87644022143073640505836996495873428086134529681104001512674703102929758510986775836743463905934518097722471081749452655420376928296064452263563811870033992327547742372987995088578867024033789318718023139071830546992714991323329051
    -- DATA = 49125638771757543689231651211475084292931082335710381100108244376759589022532353420831575508961550216671250762605928048001761626157546981278903127869446332879692676809182701430112620682053577493500883150222623214163154024006505456
    -- CIPH = 193299506650123888962773221125643949081405108594259321679655201006551705926095882430046907150110842748303573870322978868271701513628007126048756609673737544409824827945001989216218876579419508187695382705718893774501487951602684861

    M <= "010010011010110100010110100101100010110111110101001111110000101111011011011001000100111011001001110000111110001010001110110001010011011101000011111010101011101100000111111001100011010010110110001000100111100101100101101010100111111101111110111101100000110001101101011111000000000100100001100010011000011100111101110101011000100001110001111010111011110110011101110000111111000101111011011001100001011000110100110101100101011100010110101101110110010001111100011111101110011001010100110101001011101100000011001010110101111110010111110110101100001111101010111101000100000110100001000101110010001001011001111100101010001011001010110101001001111110001010111111110001110001010010110110111110001010100110010111010000010100100111100101000000011011111101100111100000011001101001";
    ETEST <= "001100010000100001010110100100100000000110100000100100110111110101011011101000011010011101101111011001111000101101111001001011101010101010001111111111001110011101111101111000011101000110101101011101011110110111101001101010000010000000101110101100110101110100010010111000001100100101101010000111110000001001010101010011011001110111110101011110101100001110110000110111110111101111010010010000011110000111000110000010000111010100100010100011010111001110001101001000101101101000001110000110001011100011000111111100100100101111101111010101100011010001101100000111110010110011010100100000111010111010001101101111100101111110001010100110111011010101001110111101010110100010011000001111010111100100011101111110010011001010001111101110110000000111110110011000101010110010100011";
    DATA := "000010000001100110111001111110011110100101111110100000100101000000010111000011110010010011001101110010111001001010010011001001011001110000101100111001101010000111101101010100000101100100000010010100100001001110110101101101010101101101010000110000010101000010000101011111111010000100010100100100001010110011011010001010010001100111110000100010101100111100101000010001101100111101100010011110100001010111111001101011001101001010111000111000001011011001010101110011111010011100010111000101101110001010101111010010001100000001100001100011110001110001010011000101010100001110010110100011110001000000010101101100110110001100011100111000001100000010000000011111010001101011010110110000001110010000101101110100111111010100110001010001110000001101101111100011010101001111110000";
    CIPH := "000111111101111110110010010011101101010100011011100000110000010000110001000010100010011101111011001010111011111011111111110001010001101110101011110011101000000001010010001001000110100001111111011100011001110010000011111011100100011001011011001000101100100000110110101010111110011101000011000101011101010011011010101101011000011110100011111000110001110111111111000001101001100111011011111111100100000111001011111010011101010101001000001111111010100111110110000011011100110001111000010111101110110111110001100000000011101101011001111000011010100010000100110100010101101010101110110101111101010000000110110011011000001111111100110111100101100100011001101111001001000000110011001001000110101110000111101000101000001110111111001010111001111000110000111000101001001110111101";
    X <= DATA;
    wait for 10 ns;
    -- Potenzierung starten
    EXPGO <= '1';
    wait until EXPRDY'event and EXPRDY='1';
    -- Ergebnis auswerten
    assert Y = CIPH;
    EXPGO <= '0';
    wait for 10 ns;


    -- und wieder dekodieren!
    ETEST <= "000011100111001110110001001000010110010001001011000110011111010010110000101111011100111100101100111000011001101110101110010001110001110101101110001000000111000101011100100110111101010101111010011010000001000011101010111001101101010100011110100111011001011011011110100010111111110011010010100011100100100010011001101000010011000111100011111001111010000011010010101110110111000110010101101010011011001100111011011110101010111010110111111111000110000011101111110001010101000111100000010001000110110101100000101101010010100000110000011001101111011100100101100000101000100101111110001001110011101010110110111011001001001000111100111100000011111011011101111110000001000011010110101110010101000101001100101010011111001011011000101001110010101010010010000100000011001000011011";
    X <= CIPH;
    wait for 10 ns;
    -- Potenzierung starten
    EXPGO <= '1';
    wait until EXPRDY'event and EXPRDY='1';
    -- Ergebnis auswerten
    assert Y = DATA;
    EXPGO <= '0';
    wait for 10 ns;




    -- Testdaten einlesen
--    M <= "";
--    X <= "";
--    ETEST <= "";
--    VGL := "";
    wait for 10 ns;
    -- Potenzierung starten
    EXPGO <= '1';
    wait until EXPRDY'event and EXPRDY='1';
    -- Ergebnis auswerten
    assert Y = DATA;
    EXPGO <= '0';
    wait for 10 ns;

    wait;
  end process;




  EXP : process
  variable FIRSTONE : std_logic;
  begin
    -- Warten auf Potenzierauftrag
    wait until EXPGO'event and EXPGO='1';
    EXPRDY <= '0';
    FIRSTONE := '1';
    MULTGO <= '0';
    Z <= X;
    wait for 10 ns;

    -- Potenzierschleife
    for j in 0 to (NumBits-1)
    loop
      if j > 0 then
        -- quadrieren Z = Z * Z
        A <= Z;
        B <= Z;
        MULTGO <= '1';
        wait until MULTRDY'event and MULTRDY='1';

        Z <= R;
        MULTGO <= '0';
        wait for 10 ns;
      end if;
      if ETEST(j) = '1' then
        -- Z auf Y draufmultiplizieren
        if FIRSTONE = '1' then
          -- das erste Mal ist Y=1, d.h. kopieren reicht
          Y <= Z;
          FIRSTONE := '0';
          wait for 10 ns;
        else
        -- Y = Y * Z
          A <= Y;
          B <= Z;
          MULTGO <= '1';
          wait until MULTRDY'event and MULTRDY='1';

          Y <= R;
          MULTGO <= '0';
          wait for 10 ns;
        end if;
      end if;
    end loop;

    -- Ergebnis ausgeben
    wait for 10 ns;
    EXPRDY <= '1';
  end process;



  MULT : process
  variable REST : std_logic_vector(NumBits downto 0);
  variable TEMP : std_logic_vector(NumBits downto 0);
  begin
    -- Warten auf Multiplikationsauftrag
    wait until MULTGO'event and MULTGO='1';
    MULTRDY <= '0';
    REST := conv_std_logic_vector(0,NumBits+1);
    R <= REST((NumBits-1) downto 0);
    wait for 10 ns;

    for i in (NumBits-1) downto 0
    loop
      -- schieben
      REST := REST((NumBits-1) downto 0) & '0';
      TEMP := REST - ('0'&M);
      if TEMP(NumBits)='0' then
        -- TEMP > 0 => REST > M => M konnte abgezogen werden
        REST := TEMP;
      end if;
      if A(i)='1' then
        -- B dazuaddieren und wieder auf M pruefen
        REST := REST + ('0'&B);
        TEMP := REST - ('0'&M);
        if TEMP(NumBits)='0' then
          -- TEMP > 0 => REST > M => M konnte abgezogen werden
          REST := TEMP;
        end if;
      end if;
    end loop;

    -- Ergebnis ausgeben
    wait for 10 ns;
    R <= REST((NumBits-1) downto 0);
    wait for 10 ns;
    MULTRDY <= '1';
  end process;

end BEHAV;
