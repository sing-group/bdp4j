/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

/**
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package org.bdp4j.pipe;

import org.bdp4j.types.Instance;


public interface PipeOutputAccumulator {

    // "subPipe" is either the iteratedPipe in IteratingPipe or one
    // of the parallel pipes in ParallelPipe.
    void pipeOutputAccumulate(Instance carrier, AbstractPipe subPipe);

    // This must not simply raise UnsupportedOperationException!
    PipeOutputAccumulator clonePipeOutputAccumulator();
}
