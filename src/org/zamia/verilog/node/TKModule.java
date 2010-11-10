/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;
import org.zamia.SourceFile;

@SuppressWarnings("nls")
public final class TKModule extends Token
{
    public TKModule(int line, int pos, SourceFile sf)
    {
        super ("module", line, pos, sf);
    }

    @Override
    public Object clone()
    {
      return new TKModule(getLine(), getPos(), getSource());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTKModule(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TKModule text.");
    }
}