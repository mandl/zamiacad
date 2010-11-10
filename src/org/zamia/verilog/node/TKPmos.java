/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;
import org.zamia.SourceFile;

@SuppressWarnings("nls")
public final class TKPmos extends Token
{
    public TKPmos(int line, int pos, SourceFile sf)
    {
        super ("pmos", line, pos, sf);
    }

    @Override
    public Object clone()
    {
      return new TKPmos(getLine(), getPos(), getSource());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTKPmos(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TKPmos text.");
    }
}