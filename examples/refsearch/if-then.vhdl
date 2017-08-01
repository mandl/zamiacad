entity IF_THEN is end entity;
architecture Arch of IF_THEN is
	signal a,b,c,d: integer;
begin
	process begin
		if A > B then
			C <= 11;
		end if;
		D <= C;
	end process;
	
end architecture Arch;
