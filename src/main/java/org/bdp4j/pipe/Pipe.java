package org.bdp4j.pipe;

import java.util.Collection;
import java.util.Iterator;
// import org.ski4spam.ia.util.PropertyList;

import org.bdp4j.ia.types.Instance;


/**
 * The abstract superclass of all Pipes, which transform one data type to another.
 * Pipes are most often used for feature extraction.
 * <p>
 * A pipe operates on an {@link org.bdp4j.ia.types.Instance}, which is a carrier of data.
 * A pipe reads from and writes to fields in the Instance when it is requested
 * to process the instance. It is up to the pipe which fields in the Instance it
 * reads from and writes to, but usually a pipe will read its input from and write
 * its output to the "data" field of an instance.
 * <p>
 * Pipes can be hierachically composed. In a typical usage, a SerialPipe is created which
 * holds instances of other pipes in an ordered list. Piping
 * in instance through a SerialPipe means piping the instance through the child pipes
 * in sequence.
 * <p>
 * A pipe holds onto two separate Alphabets: one for the symbols (feature names)
 * encountered in the data fields of the instances processed through the pipe,
 * and one for the symbols encountered in the target fields.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 * @modified Jos� Ram�n M�ndez Reboredo
 */
public abstract class Pipe {
    Pipe parent = null;
    boolean isLast = false;

    /**
     * Construct a pipe with no data and target dictionaries
     */
    public Pipe() {
        //this((Class)null, (Class)null);
    }

    /**
     * Process an Instance.  This method takes an input Instance,
     * destructively modifies it in some way, and returns it.
     * This is the method by which all pipes are eventually run.
     * <p>
     * One can create a new concrete subclass of Pipe simply by
     * implementing this method.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    public abstract Instance pipe(Instance carrier);

    /**
     * Pipe all instances from a Collection
     * @param carriers Collection of instances to pipe
     * @return The collection of instances after being processed
     */
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        Iterator<Instance> it=carriers.iterator();
        while(it.hasNext()) {
            pipe(it.next());
            isLast=!it.hasNext(); 
        };
        return carriers;
    }

    public Pipe getParent() {
        return parent;
    }

    // Note: This must be called *before* this Pipe has been added to
    // the parent's collection of pipes, otherwise in
    // DictionariedPipe.setParent() we will simply get back this Pipe's
    // Alphabet information.
    public void setParent(Pipe p) {
        if (parent != null)
            throw new IllegalStateException("Parent already set.");

        parent = p;
    }

    public Pipe getParentRoot() {

        if (parent == null)

            return this;

        Pipe p = parent;

        while (p.parent != null)
            p = p.parent;

        return p;
    }
    
    /**
     * Say whether the current Instance is the last being processed
     * @return true if the current Instance is the last being processed
     */
    public boolean isLast(){
        return isLast;
    }

    /**
     * Return the output type included the data attribute of a Instance
     * @return the output type for the data attribute of the Instances processed
     */
    public abstract Class<?> getInputType();

    /**
     * Say datatype expected in the data attribute of a Instance
     * @return the datatype expected in the data attribute of a Instance
     */
    public abstract Class<?> getOutputType();
}
