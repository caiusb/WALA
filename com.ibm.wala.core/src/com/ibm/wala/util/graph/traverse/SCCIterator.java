/*******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.util.graph.traverse;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.ReverseIterator;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.impl.GraphInverter;

/**
 * This class computes strongly connected components for a Graph (or a subset of
 * it). It does not store the SCCs in any lookaside structure, but rather simply
 * generates an enumeration of them. See Cormen, Leiserson, Rivest Ch. 23 Sec. 5
 * 
 * @author Julian Dolby
 * 
 * @see Graph
 * 
 */
public class SCCIterator<T> implements Iterator<Set<T>> {
  /**
   * The second DFS (the reverse one) needed while computing SCCs
   */
  final private DFSFinishTimeIterator<T> rev;

  /**
   * Construct an enumeration across the SCCs of a given graph.
   * 
   * @param G
   *          The graph over which to construct SCCs
   * @throws NullPointerException  if G is null
   */
  @SuppressWarnings({ "unchecked", "cast" })
  public SCCIterator(Graph<T> G) throws NullPointerException {
    this(G, (Iterator<T>) G.iterator());
  }

  /**
   * Construct an enumeration of the SCCs of the subset of a given graph
   * determined by starting at a given set of nodes.
   */
  public SCCIterator(Graph<T> G, Iterator<T> nodes) {
    Iterator<T> reverseFinishTime = ReverseIterator.reverse(DFS.iterateFinishTime(G, nodes));

    rev = DFS.iterateFinishTime(GraphInverter.invert(G), reverseFinishTime);
  }

  /**
   * Determine whether there are any more SCCs remaining in this enumeration.
   */
  public boolean hasNext() {
    return rev.hasNext();
  }

  /**
   * Find the next SCC in this enumeration
   */
  public Set<T> next() throws NoSuchElementException {
    Set<T> currentSCC = HashSetFactory.make();

    T v = rev.next();
    currentSCC.add(v);

    while (rev.hasNext() && !rev.isEmpty()) {
      currentSCC.add(rev.next());
    }

    return currentSCC;
  }


  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

}
