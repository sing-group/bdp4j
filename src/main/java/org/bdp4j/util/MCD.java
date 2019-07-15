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
