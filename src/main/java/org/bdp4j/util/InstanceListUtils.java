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


package org.bdp4j.util;

import org.bdp4j.types.Instance;

import java.util.Iterator;
import java.util.List;


public class InstanceListUtils {

    public static List<Instance> dropInvalid(List<Instance> l) {
        Iterator<Instance> listIt = l.iterator();
        while (listIt.hasNext()) {
            Instance i = listIt.next();
            if (!i.isValid()) listIt.remove();
        }
        return l;
    }

}
