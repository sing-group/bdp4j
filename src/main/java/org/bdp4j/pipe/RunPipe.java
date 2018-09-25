package org.bdp4j.pipe;


import org.bdp4j.ia.types.Instance;


public class RunPipe {

    Instance[] instances = null;

    Pipe thePipe = null;


    public Instance[] getInstances() {
        return instances;
    }


    public void setInstances(Instance[] instances) {
        this.instances = instances;
    }

    public Pipe getThePipe() {
        return thePipe;
    }


    public void setThePipe(Pipe thePipe) {
        this.thePipe = thePipe;
    }


    public Instance[] pipe() {
        Instance[] result = new Instance[instances.length];
        for (int i = 0; i < instances.length; i++)
            result[i] = thePipe.pipe(instances[i]);
        return result;
    }


}
