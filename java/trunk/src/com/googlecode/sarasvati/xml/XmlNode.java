/*
    This file is part of Sarasvati.

    Sarasvati is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    Sarasvati is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with Sarasvati.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2008 Paul Lorenz
 */

package com.googlecode.sarasvati.xml;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.googlecode.sarasvati.JoinType;
import com.googlecode.sarasvati.load.LoadException;
import com.googlecode.sarasvati.load.definition.ArcDefinition;
import com.googlecode.sarasvati.load.definition.NodeDefinition;
import com.googlecode.sarasvati.load.properties.DOMToObjectLoadHelper;
import com.googlecode.sarasvati.util.SvUtil;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlNode implements NodeDefinition, Comparable<XmlNode>
{
  @XmlAttribute(name = "name", required = true)
  protected String               name;

  @XmlAttribute(name = "joinType", required = false)
  protected XmlJoinType          joinType;

  @XmlAttribute(name="joinParam", required=false)
  protected String joinParam;

  @XmlAttribute(name = "type", required = false)
  protected String               type;

  @XmlAttribute(name = "isStart", required = false)
  protected Boolean              start;

  @XmlAttribute(name = "x", required = false)
  protected Integer              x;

  @XmlAttribute(name = "y", required = false)
  protected Integer              y;

  @XmlElement(name = "guard", required = false)
  protected String               guard;

  @XmlElement(name = "arc", required = false)
  protected List<XmlArc>         arcs         = new ArrayList<XmlArc>();

  @XmlElement(name = "custom")
  protected XmlCustom            custom;

  @Override
  public String getName ()
  {
    return name;
  }

  public void setName (String name)
  {
    this.name = name;
  }

  @Override
  public JoinType getJoinType ()
  {
    return joinType == null ? JoinType.OR : joinType.getJoinType();
  }

  public void setJoinType (XmlJoinType joinType)
  {
    this.joinType = joinType;
  }

  @Override
  public String getJoinParam ()
  {
    return joinParam;
  }

  public void setJoinParam (String joinParam)
  {
    this.joinParam = joinParam;
  }

  public String getType ()
  {
    return type;
  }

  public void setType (String type)
  {
    this.type = type;
  }

  public boolean isStart ()
  {
    return start == null ? false : start;
  }

  public void setStart (Boolean start)
  {
    this.start = start;
  }

  public Integer getX ()
  {
    return x;
  }

  public void setX (Integer x)
  {
    this.x = x;
  }

  public Integer getY ()
  {
    return y;
  }

  public void setY (Integer y)
  {
    this.y = y;
  }

  @Override
  public String getGuard ()
  {
    return guard;
  }

  public void setGuard (String guard)
  {
    this.guard = guard;
  }

  @Override
  public List<XmlArc> getArcs ()
  {
    return arcs;
  }

  public void setArcs (List<XmlArc> arcs)
  {
    this.arcs = arcs;
  }

  @Override
  public XmlCustom getCustom ()
  {
    return custom;
  }

  public void setCustom (XmlCustom custom)
  {
    this.custom = custom;
  }

  public void addToDigest (final MessageDigest digest)
    throws LoadException
  {
    if ( !SvUtil.isBlankOrNull( name ) )
    {
      digest.update( name.getBytes() );
    }

    if ( !SvUtil.isBlankOrNull( type ) )
    {
      digest.update( type.getBytes() );
    }

    if ( joinType != null )
    {
      digest.update( (byte)joinType.getJoinType().ordinal() );
    }

    if ( !SvUtil.isBlankOrNull( joinParam ) )
    {
      digest.update( joinParam.getBytes() );
    }

    if ( !SvUtil.isBlankOrNull( guard ) )
    {
      digest.update( guard.getBytes() );
    }

    digest.update( (byte)(isStart() ? 1 : 0) );

    Map<String, String> customProps = new TreeMap<String, String>();
    DOMToObjectLoadHelper.loadCustomIntoMap( custom, customProps );

    for ( Entry<String, String> entry : customProps.entrySet() )
    {
      digest.update( entry.getKey().getBytes() );
      if ( !SvUtil.isBlankOrNull( entry.getValue() ) )
      {
        digest.update( entry.getValue().getBytes() );
      }
    }

    Collections.sort( arcs );

    if ( arcs != null )
    {
      for ( XmlArc arc : arcs )
      {
        arc.addToDigest( digest );
      }
    }
  }

  @Override
  public int compareTo (XmlNode o)
  {
    if ( o == null )
    {
      return 1;
    }
    return SvUtil.compare( name, o.getName() );
  }

  @Override
  public String toString ()
  {
    StringBuilder buf = new StringBuilder();
    buf.append( "<node name=\"" );
    buf.append( name );
    buf.append( "\" joinType=\"" );
    buf.append( getJoinType() );
    buf.append( "\" type=\"" );
    buf.append( type );
    buf.append( "\" isStart=\"" );
    buf.append( isStart() );

    if (x != null)
    {
      buf.append( "\" x=\"" );
      buf.append( getX() );
    }
    if (y != null)
    {
      buf.append( "\" y=\"" );
      buf.append( y );
    }

    buf.append( "\">\n" );

    for (ArcDefinition arc : arcs)
    {
      buf.append( arc );
      buf.append( "\n" );
    }

    if (custom != null)
    {
      buf.append( custom );
      buf.append( "\n" );
    }

    buf.append( "</node>" );
    return buf.toString();
  }
}
