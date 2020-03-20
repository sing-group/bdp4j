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

import java.lang.reflect.InvocationTargetException;

import org.codehaus.janino.ExpressionEvaluator;

/**
 *
 * @author María Novo
 */
public class RegularExpressionEvaluator {

    /**
     * Default constructor
     */
    public RegularExpressionEvaluator() {
    }

    /**
     *
     * @param expression Expression to evaluate
     * @param expressionType The type of the result of evaluating the expression
     * @param parameterNames The name of the parameters to evaluate
     * @param parameterTypes The type of the parameters to evaluate
     * @param parameterValues The values of parameters to evaluate
     * @return THe evaluated expression
     * @throws Exception add to a Compile, Parse or Runtime Exception
     */
    public Object evaluateExpression(String expression, Class expressionType, String[] parameterNames,
            Class[] parameterTypes, Object[] parameterValues) throws Exception  {
        
        ExpressionEvaluator ee = new ExpressionEvaluator(expression, expressionType, parameterNames, parameterTypes);

         try {
            return ee.evaluate(parameterValues);
        } catch ( InvocationTargetException e) {
            return null;
        } catch(Exception ex){
            return null;
        }

    }

    /**
     * Format a expression deleting all non alphanumeric characters
     *
     * @param expression Expression to format
     * @param parameterNames Array with parameter names needed to change all
     * parameter names in expression
     * @return The formatted expression
     */
    public String formatExpression(String expression, String[] parameterNames) {
        String formattedExpression = expression;
        for (int i = 0; i < parameterNames.length; i++) {
            String currentName = parameterNames[i];
            formattedExpression = formattedExpression.replaceAll(parameterNames[i], currentName.replaceAll("[^a-zA-Z0-9]", ""));
        }
        return formattedExpression;
    }

    /**
     * Format a list of parameter names deleting all non alphanumeric characters
     *
     * @param parameterNames Array with parameter names to format
     * @return The formatted parameter names
     */
    public String[] formatParameterNames(String[] parameterNames) {
        String[] formattedParameterNames = new String[parameterNames.length];
        for (int i = 0; i < parameterNames.length; i++) {
            formattedParameterNames[i] = parameterNames[i].replaceAll("[^a-zA-Z0-9]", "");
        }
        return formattedParameterNames;
    }
}
