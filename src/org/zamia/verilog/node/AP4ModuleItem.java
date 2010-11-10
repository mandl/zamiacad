/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AP4ModuleItem extends PModuleItem
{
    private PContinuousAssign _continuousAssign_;

    public AP4ModuleItem()
    {
        // Constructor
    }

    public AP4ModuleItem(
        @SuppressWarnings("hiding") PContinuousAssign _continuousAssign_)
    {
        // Constructor
        setContinuousAssign(_continuousAssign_);

    }

    @Override
    public Object clone()
    {
        return new AP4ModuleItem(
            cloneNode(this._continuousAssign_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAP4ModuleItem(this);
    }

    public PContinuousAssign getContinuousAssign()
    {
        return this._continuousAssign_;
    }

    public void setContinuousAssign(PContinuousAssign node)
    {
        if(this._continuousAssign_ != null)
        {
            this._continuousAssign_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._continuousAssign_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._continuousAssign_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._continuousAssign_ == child)
        {
            this._continuousAssign_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._continuousAssign_ == oldChild)
        {
            setContinuousAssign((PContinuousAssign) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}