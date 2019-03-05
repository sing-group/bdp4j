package org.bdp4j.types;

import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.pipe.TargetAssigningPipe;
import org.bdp4j.pipe.TeePipe;
import org.bdp4j.pipe.TransformationPipe;

public enum PipeType {
    TRANSFORMATION_PIPE(TransformationPipe.class),
    TARGET_ASSIGNING_PIPE(TargetAssigningPipe.class),
    PROPERTY_COMPUTING_PIPE(PropertyComputingPipe.class),
    TEE_PIPE(TeePipe.class);

    private Class type;

    PipeType(Class type) {
        this.type = type;
    }

    public Class typeClass() {
        return type;
    }
}
