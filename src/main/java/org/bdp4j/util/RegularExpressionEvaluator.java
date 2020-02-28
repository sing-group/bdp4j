/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2020 SING Group (University of Vigo)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.util;

import org.codehaus.janino.ExpressionEvaluator;
/**
 *
 * @author María Novo
 */
public class RegularExpressionEvaluator {
    public RegularExpressionEvaluator() {
    } 

    public Object evaluateExpression(String expression, Class expressionType, String[] parameterNames,
            Class[] parameterTypes, Object[] parameterValues) throws Exception {
               
                ExpressionEvaluator ee = new ExpressionEvaluator(expression,
                expressionType,
                parameterNames,
                parameterTypes
        );
        
        try {
            return ee.evaluate(parameterValues);
        } catch (Exception e) {
            System.out.println("Sale por exception");
            e.printStackTrace();
            return null;
        }
    }

   
}
