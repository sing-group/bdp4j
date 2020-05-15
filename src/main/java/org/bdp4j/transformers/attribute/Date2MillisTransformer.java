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

package org.bdp4j.transformers.attribute;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.bdp4j.types.Transformer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bdp4j.util.DateTimeIdentifier;

/**
 * Trasform an input from String, that represents a Data to Double
 *
 * @author María Novo
 */
public class Date2MillisTransformer extends Transformer {

    private String transformerListValues;

    /**
     * Transform an input, that represents a Date to Double
     *
     * @param input A string to transform in Double
     * @return A double value that represents a Date
     */
    @Override
    public double transform(Object input) {
           
        if (input != null && !input.equals("null")) {
            try {
                LocalDateTime date = DateTimeIdentifier.getDefault().checkDateTime(input.toString());
                return date.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
            } catch (Exception ex) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public String getTransformerListValues() {
        return transformerListValues;
    }
    
    @Override
    public Class<?> getInputType() {
        return Date.class;
    }

    /**
     * Get a List who contains the values
     *
     * @return List who contains the values
     */
    @Override
    public List<Integer> getListValues() {
        return new ArrayList<>();
    }
}
