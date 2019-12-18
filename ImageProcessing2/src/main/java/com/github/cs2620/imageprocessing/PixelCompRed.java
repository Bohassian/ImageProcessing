/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cs2620.imageprocessing;

import java.util.Comparator;

/**
 *
 * @author jacob.lee
 */
public class PixelCompRed implements Comparator<Pixel>{
    public int compare(Pixel p1, Pixel p2) {
        if (p1.getRed() < p2.getRed()) {
            return 1;
        } else {
            return -1;
        }
    }
}
