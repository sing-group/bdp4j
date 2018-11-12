/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

/**
 *
 * @author María Novo
 */
public abstract class Transformer<T> {
    public abstract double transform(T input);
}
