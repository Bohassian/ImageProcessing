/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cs2620.imageprocessing;

import java.awt.Color;


public class Pixel {
    private static final float RED_WEIGHT = .21f;
    private static final float GREEN_WEIGHT = .72f;
    private static final float BLUE_WEIGHT = .07f;

    private static final float SATURATION_UP = 1f;
    private static final float SATURATION_DOWN = .3f;

    private static final int CHANNEL_FACTOR = 50;
    private static final int DITHER_THRESHOLD = 127;
    
    private static final int[][] DITHER_THRESHOLD_MAP_9 = new int[][]{
        {0, 7, 3},
        {6, 5, 2},
        {4, 1, 8}
    };
    
    private static final int[][] DITHER_THRESHOLD_MAP_64 = new int[][]{
        { 0, 48, 12, 60,  3, 51, 15, 63},
        {32, 16, 44, 28, 35, 19, 47, 31},
        { 8, 56,  4, 52, 11, 59,  7, 55},
        {40, 24, 36, 20, 43, 27, 39, 23},
        { 2, 50, 14, 62,  1, 49, 13, 61},
        {34, 18, 46, 30, 33, 17, 45, 29},
        {10, 58,  6, 54,  9, 57,  5, 53},
        {42, 26, 38, 22, 41, 25, 37, 21}
    };

    static Pixel interpolate(int colorOne, int colorTwo, float f) {
      Pixel one  = new Pixel(colorOne);
      Pixel two = new Pixel(colorTwo);

      return Pixel.interpolate(one, two, f);
    }

    static Pixel interpolate(Pixel one, Pixel two, float f) {
     //Now interpolate between the two colors based on f.
      //If f is close to 0, we want more of colorOne. 
      //If f is close to 1, we want more of colorTwo

      int r = (int) (one.r * (1 - f) + two.r * f);
      int g = (int) (one.g * (1 - f) + two.g * f);
      int b = (int) (one.b * (1 - f) + two.b * f);

      return new Pixel(r, g, b);
    }

    private int r, g, b;
    private int original_int;
    
    public Pixel(int r, int g, int b){
        this.r = r;
        this.g = g;
        this.b = b;
        
        clip();
    }
    
    public Pixel(int i){  
        int r = (i >> 16) & 0xff;
        int g = (i >> 8) & 0xff;
        int b = i & 0xff;
        
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    protected Pixel(int r, int g, int b, boolean clip){
    this.r = r;
    this.g = g;
    this.b = b;
    
    if(clip)
      clip();
    
  }
    
    public Pixel to332(int i){
        this.r = r / 8 * 7;
        this.g = g / 8 * 7;
        this.b = b / 64 * 63;
        
        return new Pixel(r,g,b);
    }
    
    private void clip(){
        if(this.r > 255) this.r = 255;
        if(this.g > 255) this.g = 255;
        if(this.b > 255) this.b = 255;
        
        
        if(this.r < 0) this.r = -this.r;
        if(this.g < 0) this.g = -this.g;
        if(this.b < 0) this.b = -this.b;
        
        
    }

    public void isolateRed() {
        if (!isRedDominant()) {
            toGrayscaleAverage();
        }
    }

    public void isolateGreen() {
        if (!isGreenDominant()) {
            toGrayscaleAverage();
        }
    }

    public void isolateBlue() {
        if (!isBlueDominant()) {
            toGrayscaleAverage();
        }
    }
    
    public void toGrayscaleLightness() {
        grayscale(grayscaleLightness());
    }

    public void toGrayscaleAverage() {
        grayscale(grayscaleAverage());
    }

    public void toGrayscaleLuminosity() {
        grayscale(grayscaleLuminosity());
    }
    
    public void setPixelChannels(int r, int g, int b) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
    }
    
    public void thresholdDither() {
        toGrayscaleAverage();
        if (r >= DITHER_THRESHOLD) {
            setPixelChannels(255,255,255);
        } else {
            setPixelChannels(0,0,0);
        }
    }
    
    public void thresholdMap9Dither(int x, int y){
        toGrayscaleAverage();
        int ditherThreshold = DITHER_THRESHOLD_MAP_9[x % 3][y % 3];
        if ((r % 9) > ditherThreshold) {
            setPixelChannels(255,255,255);
        } else {
            setPixelChannels(0,0,0);
        }
    }
    
    public void thresholdMap64Dither(int x, int y){
        toGrayscaleAverage();
        int ditherThreshold = DITHER_THRESHOLD_MAP_64[x % 8][y % 8];
        if ((r % 64) > ditherThreshold) {
            setPixelChannels(255,255,255);
        } else {
            setPixelChannels(0,0,0);
        }
        
    }
    
    public void colorThresholdMap64Dither(int x, int y){
        int ditherThreshold = DITHER_THRESHOLD_MAP_64[x % 8][y % 8];
        int scaledR = r % 64;
        int scaledG = g % 64;
        int scaledB = b % 64;
        
        if (scaledR > ditherThreshold) {
            scaledR = ditherThreshold;
        }
        if (scaledG > ditherThreshold) {
            scaledG = ditherThreshold;
        }
        if (scaledB > ditherThreshold) {
            scaledB = ditherThreshold;
        }
        
        setPixelChannels(scaledR * 4, scaledG * 4, scaledB * 4);
    }

    public void burnGray() {
        if (r == g && g == b) {
            setPixelChannels(0, 0 ,0);
        }
    }

    public int grayscaleLightness() {
        return (maxChannel() + minChannel()) / 2;
    }

    public int grayscaleAverage() {
        return (r + g + b) / 3;
    }

    public int grayscaleLuminosity() {
        return (int) ((RED_WEIGHT * r) + (GREEN_WEIGHT * g) + (BLUE_WEIGHT * b));
    }
    
    public void toGrayscaleRed() {
        grayscale(r);
    }

    public void toGrayscaleGreen() {
        grayscale(g);
    }

    public void toGrayscaleBlue() {
        grayscale(b);
    }
    
    public void saturateUp() {
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);

        float h = hsb[0];
        float s = SATURATION_UP;
        float v = hsb[2];

        int i = Color.HSBtoRGB(h, s, v);

        int r = (i >> 16) & 0xff;
        int g = (i >> 8) & 0xff;
        int b = i & 0xff;

        setPixelChannels(r, g, b);
    }

    public void saturateDown() {
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);

        float h = hsb[0];
        float s = SATURATION_DOWN;
        float v = hsb[2];

        int i = Color.HSBtoRGB(h, s, v);

        int r = (i >> 16) & 0xff;
        int g = (i >> 8) & 0xff;
        int b = i & 0xff;

        setPixelChannels(r, g, b);
    }

    public void swapDominantColor() {
        if (isRedDominant()) {
            if (g == minChannel()) {
                setPixelChannels(g, r, b);
            } else {
                setPixelChannels(b, g, r);
            }
        } else if (isGreenDominant()) {
            if (r == minChannel()) {
                setPixelChannels(g, r, b);
            } else {
                setPixelChannels(r, b, g);
            }
        } else if (isBlueDominant()) {
            if (r == minChannel()) {
                setPixelChannels(b, g, r);
            } else {
                setPixelChannels(r, b, g);
            }
        }
    }

    public void rampRed() {
        setPixelChannels(clamp(r + CHANNEL_FACTOR), g, b);
    }

    public void rampGreen() {
        setPixelChannels(r, clamp(g + CHANNEL_FACTOR), b);
    }

    public void rampBlue() {
        setPixelChannels(r, g, clamp(b + CHANNEL_FACTOR));
    }

    public void rampAll() {
        setPixelChannels(clamp(r + CHANNEL_FACTOR), clamp(g + CHANNEL_FACTOR), clamp(b + CHANNEL_FACTOR));
    }
    
    private float[] getHSB(){
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
        return hsb;
    }
    
    public float getHue(){
        float[] hsb = getHSB();
        return hsb[0];
    }
    
    public float getSaturation(){
        float[] hsb = getHSB();
        return hsb[1];
    }
    
    public float getValue(){
        float[] hsb = getHSB();
        return hsb[2];
    }
    
    public void setValue(float newValue) {
        float[] hsb = getHSB();
        hsb[2] = newValue;
        
        int i = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        
        int r = (i >> 16) & 0xff;
        int g = (i >> 8) & 0xff;
        int b = i & 0xff;
        
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public void setValue(Pixel other){
      this.r = other.r;
      this.g = other.g;
      this.b = other.b;
    }

    public boolean isRedDominant() {
        return r >= g && r >= b;
    }

    public boolean isGreenDominant() {
        return g >= r && g >= b;
    }

    public boolean isBlueDominant() {
        return b >= r && b >= g;
    }

    private int clamp(int value) {
        return Math.min(255, Math.max(0, value));
    }

    private int maxChannel() {
        int max = r;

        if (max < g) {
            max = g;
        }

        if (max < b) {
            max = b;
        }

        return max;
    }

    private int minChannel() {
        int min = r;

        if (min < g) {
            min = g;
        }

        if (min < b) {
            min = b;
        }

        return min;
    }
    

    public Pixel toGrayscale() {
        int gray = (r + g + b) / 3;
        grayscale(gray);
        return this;

    }
    
    public int getSlice(int bit){
       int gray = (r + g + b) / 3;
        
      int o;
      int power = (int)(Math.pow(2,bit));
      if((power& gray) > 0){
        o = 255;
      }
      else{
        o = 0;
      }
      return o;
    }
    
    public Pixel slice(int bit){
     int o = getSlice(bit);
      grayscale(o);
      return this;
      
    }
    
    public void lessSaturated(){
        
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
        
        float h = hsb[0];//All values 0-1
        float s = hsb[1];
        float v = hsb[2];
        
        s = .5f;
        
        int i = Color.HSBtoRGB(h, s, v);
        
        int r = (i >> 16) & 0xff;
        int g = (i >> 8) & 0xff;
        int b = i & 0xff;
        
        this.r = r;
        this.g = g;
        this.b = b;
        
        
    }
     
    public void moreSaturated(){
        
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
        
        float h = hsb[0];//All values 0-1
        float s = hsb[1];
        float v = hsb[2];
        
        s = 1;
        
        int i = Color.HSBtoRGB(h, s, v);
        
        int r = (i >> 16) & 0xff;
        int g = (i >> 8) & 0xff;
        int b = i & 0xff;
        
        this.r = r;
        this.g = g;
        this.b = b;
        
        
    }

//    public void toGrayscaleRed() {
//        grayscale(r);
//    }
//
//    public void toGrayscaleGreen() {
//        grayscale(g);
//    }
//
//    public void toGrayscaleBlue() {
//        grayscale(b);
//    }

    private void grayscale(int i) {
        r = i;
        g = i;
        b = i;
    }

    public Color getColor() {
        return new Color(r, g, b);
    }
    
    public int getRed(){
        return r;
    }
    
    public int getGreen(){
        return g;
    }
    
    public int getBlue(){
        return b;
    }
    
//    private float[] getHSB(){
//        float[] hsb = new float[3];
//        Color.RGBtoHSB(r, g, b, hsb);
//        return hsb;
//    }
//    
//    public float getHue(){
//        float[] hsb = getHSB();
//        return hsb[0];
//    }
//    
//    public float getSaturation(){
//        float[] hsb = getHSB();
//        return hsb[1];
//    }
//    
//    public float getValue(){
//        float[] hsb = getHSB();
//        return hsb[2];
//    }
    
    public float distanceL1(Pixel other){
      return Math.abs(this.r - other.r) + Math.abs(this.g - other.g) + Math.abs(this.b - other.b);
    }
    
    public int getRGB(){
        int toReturn = ((0xff) << 24) + (r << 16) + (g << 8) + b;
        
        return toReturn;
    }

}
