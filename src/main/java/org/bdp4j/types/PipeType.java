/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */



package org.bdp4j.types;

import java.lang.annotation.Annotation;

import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.pipe.TargetAssigningPipe;
import org.bdp4j.pipe.TeePipe;
import org.bdp4j.pipe.TransformationPipe;

/**
 * An enumeration with the Pipe types to count the types of a pipe
 * @author Yeray Lage
 */
public enum PipeType {
    TRANSFORMATION_PIPE(TransformationPipe.class),
    TARGET_ASSIGNING_PIPE(TargetAssigningPipe.class),
    PROPERTY_COMPUTING_PIPE(PropertyComputingPipe.class),
    TEE_PIPE(TeePipe.class);

    private Class<?> type;

    PipeType(Class<?> type) {
        this.type = type;
    }

    public Class<?> typeClass() {
        return type;
    }
}
