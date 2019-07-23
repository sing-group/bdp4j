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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.util;

/**
 *
 * @author José Ramón Méndez
 */
public class MCD {

    public static int mcd(int n1, int n2) {
        int a = Math.max(n1, n2);
        int b = Math.min(n1, n2);

        int res;
        do {
            res = b;
            b = a % b;
            a = res;
        } while (b != 0);

        return res;
    }

    public static int mcd(int... n) {
        switch (n.length) {
            case 1:
                return n[0];
            case 0:
                return 1;
            default:
                int sub[] = new int[n.length - 1];
                System.arraycopy(n, 2, sub, 1, sub.length - 1);
                sub[0] = mcd(n[0], n[1]);
                return mcd(sub);
        }
    }

    public static void main(String args[]) {
        int x = 25000;
        int y = 4356;

        System.out.println("el mcd es: " + mcd(x, y));
        System.out.println("el mcd es: " + mcd(20, 30, 50, 70));
    }

}
