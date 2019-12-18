/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cs2620.imageprocessing;

import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyImage {

    BufferedImage bufferedImage;
    Pixel[] pallette16 = new Pixel[16];
    TreeSet<Pixel> pixelList;
    //    PriorityQueue<Pixel> pixelList;
    //    ArrayList<Pixel> pixelList = new ArrayList<Pixel>();
    int blueRange, greenRange, redRange;
    int minBlue = 255;
    int minGreen = 255;
    int minRed = 255;
    int maxRed = 0;
    int maxGreen = 0;
    int maxBlue = 0;
    
    int[][] darkestSquare = new int[][]{
        {1,0,1,0,1},
        {0,1,1,1,0},
        {1,1,1,1,1},
        {0,1,1,1,0},
        {1,0,1,0,1}
    };
    
    int[][] darkSquare = new int[][]{
        {0,0,0,0,0},
        {0,1,1,1,0},
        {0,1,1,1,0},
        {0,1,1,1,0},
        {0,0,0,0,0}
    };
    
    int[][] medSquare = new int[][] {
        {0,0,0,0,0},
        {0,0,1,0,0},
        {0,1,1,1,0},
        {0,0,1,0,0},
        {0,0,0,0,0}
    };
    
    int[][] lightSquare = new int[][] {
        {0,0,0,0,0},
        {0,0,0,0,0},
        {0,0,1,0,0},
        {0,0,0,0,0},
        {0,0,0,0,0}
    };
    
    int[][] lightestSquare = new int[][] {
        {0,0,0,0,0},
        {0,0,0,0,0},
        {0,0,0,0,0},
        {0,0,0,0,0},
        {0,0,0,0,0}
    };
    
    int[][] massiveDark = new int[][] {
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,1,1,1,1,0,0,0},
        {0,0,1,1,1,1,1,1,0,0},
        {0,1,1,1,1,1,1,1,1,0},
        {0,1,1,1,1,1,1,1,1,0},
        {0,1,1,1,1,1,1,1,1,0},
        {0,1,1,1,1,1,1,1,1,0},
        {0,0,1,1,1,1,1,1,0,0},
        {0,0,0,1,1,1,1,0,0,0},
        {0,0,0,0,0,0,0,0,0,0}
    };
    
    int[][] massiveMed = new int[][] {
        {0,0,0,0,0,0,0,0,0,0},
        {0,1,1,1,0,0,1,1,1,0},
        {0,1,1,1,0,0,1,1,1,0},
        {0,1,1,1,0,0,1,1,1,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,1,1,1,0,0,1,1,1,0},
        {0,1,1,1,0,0,1,1,1,0},
        {0,1,1,1,0,0,1,1,1,0},
        {0,0,0,0,0,0,0,0,0,0}
    };
    
    int[][] massiveLight = new int[][] {
        {0,0,0,0,0,0,0,0,0,0},
        {0,1,1,0,1,1,0,1,1,0},
        {0,1,1,0,1,1,0,1,1,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,1,1,0,1,1,0,1,1,0},
        {0,1,1,0,1,1,0,1,1,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,1,1,0,1,1,0,1,1,0},
        {0,1,1,0,1,1,0,1,1,0},
        {0,0,0,0,0,0,0,0,0,0}
    };

    /**
     * Create a new image instance from the given file
     *
     * @param filename The file to load
     */
    public MyImage(String filename) {
      try {
        bufferedImage = ImageIO.read(new File(filename));
      } catch (IOException ex) {
        Logger.getLogger(MyImage.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    public MyImage(int w, int h) {
      bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
    }

    /**
     * Run a pixel operation on each pixel in the image
     *
     * @param pi The pixel operation to run
     */
    public void all(PixelInterface pixelInterface) {

      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          int color_int = bufferedImage.getRGB(x, y);

          Pixel p = new Pixel(color_int);

          pixelInterface.PixelMethod(p);

          bufferedImage.setRGB(x, y, p.getColor().getRGB());

        }
      }

    }

    /**
     * Save the file to the given location
     *
     * @param filename The location to save to
     */
    public void save(String filename) {

      try {
        ImageIO.write(bufferedImage, "PNG", new File(filename));
      } catch (IOException ex) {
        Logger.getLogger(MyImage.class.getName()).log(Level.SEVERE, null, ex);
      }

    }

    public InputStream getInputStream() throws IOException {

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", os);
      InputStream is = new ByteArrayInputStream(os.toByteArray());
      return is;

    }

    public int[] getGrayscaleHistogram() {
      int[] histogram = new int[256];

      //Bin each pixel in the histogram
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          int color_int = bufferedImage.getRGB(x, y);

          Pixel p = new Pixel(color_int);

          int grayscale = (int) (p.getValue() * 255);
          histogram[grayscale]++;

        }
      }

      return histogram;
    }

    public MyImage getGrayscaleHistogramImage() {
      int[] histogram = getGrayscaleHistogram();
      //Find the biggest bin
      int max = 0;
      for (int h = 0; h < 256; h++) {
        if (histogram[h] > max) {
          max = histogram[h];
        }
      }

      System.out.println("The biggest histogram value is " + max);

      MyImage toReturn = new MyImage(256, 50);

      //Go across and create the histogram
      for (int x = 0; x < 256; x++) {
        int localMax = histogram[x] * 50 / max;
        for (int y = 0; y < 50; y++) {
          int localY = 50 - y;

          if (histogram[x] == 0) {
            toReturn.bufferedImage.setRGB(x, y, Color.RED.getRGB());
          } else {

            if (localY < localMax) {
              toReturn.bufferedImage.setRGB(x, y, new Pixel(x, x, x).getColor().getRGB());
            } else {
              toReturn.bufferedImage.setRGB(x, y, new Pixel((x + 128) % 255, (x + 128) % 255, (x + 128) % 255).getRGB());
            }
          }

        }
      }

      return toReturn;

    }

    public void simpleAdjustForExposure() {

      int[] histogram = getGrayscaleHistogram();
      int firstIndexWithValue = 255;
      int lastIndexWithValue = 0;
      for (int i = 0; i < 256; i++) {
        if (histogram[i] != 0) {
          if (i > lastIndexWithValue) {
            lastIndexWithValue = i;
          }
          if (i < firstIndexWithValue) {
            firstIndexWithValue = i;
          }
        }
      }

      System.out.println("The first histogram bin with a non-zero value is: " + firstIndexWithValue);
      System.out.println("The last histogram bin with a non-zero value is: " + lastIndexWithValue);

      //Now stretch the pixels to fill the whole image.
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          int color_int = bufferedImage.getRGB(x, y);

          Pixel p = new Pixel(color_int);

          int grayscale = (int) (p.getValue() * 255);
          float newGrayscale = (grayscale - firstIndexWithValue) / (float) (lastIndexWithValue - firstIndexWithValue);
          p.setValue(newGrayscale);

          bufferedImage.setRGB(x, y, p.getColor().getRGB());

        }
      }

      histogram = getGrayscaleHistogram();
      firstIndexWithValue = 255;
      lastIndexWithValue = 0;
      for (int i = 0; i < 256; i++) {
        if (histogram[i] != 0) {
          if (i > lastIndexWithValue) {
            lastIndexWithValue = i;
          }
          if (i < firstIndexWithValue) {
            firstIndexWithValue = i;
          }
        }
      }

      System.out.println("After adjusting for exposure, the first histogram bin with a non-zero value is: " + firstIndexWithValue);
      System.out.println("After adjusting for exposure, the last histogram bin with a non-zero value is: " + lastIndexWithValue);

    }

    public void autoAdjustForExposure() {

      int numPixels = bufferedImage.getWidth() * bufferedImage.getHeight();
      float idealPixels = numPixels / 256.0f;
      int[] currentHistogram = getGrayscaleHistogram();

      int[] finalHistogram = new int[256];
      int[] map = new int[256];
      int finalIndex = 0;
      float currentOverflow = 0;

      for (int i = 0; i < 256; i++) {

        int finalSize = finalHistogram[finalIndex];
        int currentValues = currentHistogram[i];
        int newFinalSize = finalSize + currentValues;

        if (newFinalSize < idealPixels) {
          finalHistogram[finalIndex] = newFinalSize;
          map[i] = finalIndex;
        } else {
          finalHistogram[finalIndex] = newFinalSize;
          map[i] = finalIndex;
          currentOverflow += (finalHistogram[finalIndex] - idealPixels);
          finalIndex++;
          while (currentOverflow > idealPixels) {
            finalIndex++;
            currentOverflow -= idealPixels;
          }

        }
      }

      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          int color_int = bufferedImage.getRGB(x, y);

          Pixel p = new Pixel(color_int);

          int grayscale = (int) (p.getValue() * 255);
          float newGrayscale = map[grayscale] / 256.0f;
          p.setValue(newGrayscale);

          bufferedImage.setRGB(x, y, p.getColor().getRGB());

        }
      }

    }

    void applyKernelEdge() {
      float[][] kernel = new float[3][3];

      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          kernel[y][x] = 0;
          if (x == 1 && y == 0) {
            kernel[y][x] = -1.0f;
          }
          if (x == 1 && y == 2) {
            kernel[y][x] = -1.0f;
          }
          if (x == 0 && y == 1) {
            kernel[y][x] = -1.0f;
          }
          if (x == 2 && y == 1) {
            kernel[y][x] = -1.0f;
          }
          if (x == 1 && y == 1) {
            kernel[y][x] = 4.0f;

          }
        }
      }

      doKernel(kernel);

    }
    
    void applyKernelOutline() {
      float[][] kernel = new float[3][3];

      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          kernel[y][x] = 0;
          if (x == 1 && y == 0) {
            kernel[y][x] = -1.0f;
          }
          if (x == 1 && y == 2) {
            kernel[y][x] = -1.0f;
          }
          if (x == 0 && y == 1) {
            kernel[y][x] = -1.0f;
          }
          if (x == 2 && y == 1) {
            kernel[y][x] = -1.0f;
          }
          if (x == 1 && y == 1) {
            kernel[y][x] = 4.0f;

          }
        }
      }

      doKernelOutline(kernel);

    }

    void applyKernelBlur() {
      float[][] kernel = new float[3][3];

      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          kernel[y][x] = 1 / 9.0f;

        }
      }

      doKernel(kernel);

    }

    void applyKernelSharp() {
      float[][] kernel = new float[3][3];

      for (int y = 0; y < 3; y++) {
        for (int x = 0; x < 3; x++) {
          kernel[y][x] = 0;
          if (x == 1 && y == 0) {
            kernel[y][x] = -1.0f;
          }
          if (x == 1 && y == 2) {
            kernel[y][x] = -1.0f;
          }
          if (x == 0 && y == 1) {
            kernel[y][x] = -1.0f;
          }
          if (x == 2 && y == 1) {
            kernel[y][x] = -1.0f;
          }
          if (x == 1 && y == 1) {
            kernel[y][x] = 5.0f;

          }
        }
      }

      doKernel(kernel);

    }

    private void doKernel(float[][] kernel) {
      BufferedImage temp = BufferedImageCloner.clone(bufferedImage);
      for (int y = 0; y < temp.getHeight(); y++) {
        for (int x = 0; x < temp.getWidth(); x++) {
          int color_int = temp.getRGB(x, y);

          Pixel p = new Pixel(color_int);
          p.toGrayscale();
          temp.setRGB(x, y, p.getColor().getRGB());
        }
      }

      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          float sum = 0;
          for (int ky = -1; ky <= 1; ky++) {
            for (int kx = -1; kx <= 1; kx++) {
              int color_int = 0;

              int px = x + kx;
              int py = y + ky;
              if (px >= 0 && px < bufferedImage.getWidth() && py >= 0 && py < bufferedImage.getHeight()) {
                color_int = temp.getRGB(px, py);
              }
              Pixel p = new Pixel(color_int);
              p.toGrayscale();
              float grayscale = p.getRed();
              float kernelValue = kernel[kx + 1][ky + 1];
              sum += (grayscale * kernelValue);

            }
          }
          int intSum = (int) (sum);

          bufferedImage.setRGB(x, y, new Pixel(intSum, intSum, intSum).getRGB());
        }
      }
    }
    
    private void doKernelOutline(float[][] kernel) {
      BufferedImage temp = BufferedImageCloner.clone(bufferedImage);
      for (int y = 0; y < temp.getHeight(); y++) {
        for (int x = 0; x < temp.getWidth(); x++) {
          int color_int = temp.getRGB(x, y);

          Pixel p = new Pixel(color_int);
          p.toGrayscale();
          temp.setRGB(x, y, p.getColor().getRGB());
        }
      }

      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
          float sum = 0;
          for (int ky = -1; ky <= 1; ky++) {
            for (int kx = -1; kx <= 1; kx++) {
              int color_int = 0;

              int px = x + kx;
              int py = y + ky;
              if (px >= 0 && px < bufferedImage.getWidth() && py >= 0 && py < bufferedImage.getHeight()) {
                color_int = temp.getRGB(px, py);
              }
              Pixel p = new Pixel(color_int);
              p.toGrayscale();
              float grayscale = p.getRed();
              float kernelValue = kernel[kx + 1][ky + 1];
              sum += (grayscale * kernelValue);

            }
          }
          int intSum = (int) (sum);
          
          if (intSum <= 20) 
          {
            bufferedImage.setRGB(x,y, new Pixel(0,0,0).getRGB());
          }
          else
          {
            bufferedImage.setRGB(x, y, new Pixel(255, 255, 255).getRGB());
          }
        }
      }
    }
    
    void outline() {
        ColorConversion16();
        BufferedImage temp = BufferedImageCloner.clone(bufferedImage);
        
        applyKernelOutline();
        
        for (int y = 0; y < temp.getHeight(); y++) {
            for (int x = 0; x < temp.getWidth(); x++) {
                Pixel p = new Pixel(bufferedImage.getRGB(x,y));
                if (p.getRed() == 0 || p.getGreen() == 0 || p.getBlue() == 0)
                {
                    bufferedImage.setRGB(x, y, new Pixel(temp.getRGB(x,y)).getRGB());
                }
                else
                {
                    bufferedImage.setRGB(x, y, new Pixel(0,0,0).getRGB());
                }
        }
      }
    }
    
    void dotHalftone5Dot() {
        BufferedImage temp = BufferedImageCloner.clone(bufferedImage);
        
        for (int y = 0; y < temp.getHeight() + 5; y+=5) {
            for (int x = 0; x < temp.getWidth() + 5; x+=5) {
                float valueTotal = 0f;
                int totalPixels = 0;
                
                for (int i = x; i < x + 5; i++) {
                    for (int j = y; j < y + 5; j++) {
                        if (i < temp.getWidth() && j < temp.getHeight()) {
                            Pixel p = new Pixel(bufferedImage.getRGB(i,j));
                            valueTotal += p.getValue();
                            totalPixels++;
                        }
                    }
                }
                
                float valueAvg = valueTotal / totalPixels;
                
                if (valueAvg > .9f) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (lightestSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else if (valueAvg > .75f) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (lightSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else if (valueAvg > .25f) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (medSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else if (valueAvg > .1f) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (medSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (darkestSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    void dotHalftone3Dot() {
        BufferedImage temp = BufferedImageCloner.clone(bufferedImage);
        
        for (int y = 0; y < temp.getHeight() + 5; y+=5) {
            for (int x = 0; x < temp.getWidth() + 5; x+=5) {
                float valueTotal = 0f;
                int totalPixels = 0;
                
                for (int i = x; i < x + 5; i++) {
                    for (int j = y; j < y + 5; j++) {
                        if (i < temp.getWidth() && j < temp.getHeight()) {
                            Pixel p = new Pixel(bufferedImage.getRGB(i,j));
                            valueTotal += p.getValue();
                            totalPixels++;
                        }
                    }
                }
                
                float valueAvg = valueTotal / totalPixels;
                
                if (valueAvg > .75f) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (lightSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else if (valueAvg > .25f) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (medSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (darkSquare[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    void massiveHalftone() {
        BufferedImage temp = BufferedImageCloner.clone(bufferedImage);
        
        for (int y = 0; y < temp.getHeight() + 10; y+=10) {
            for (int x = 0; x < temp.getWidth() + 10; x+=10) {
                float valueTotal = 0f;
                int totalPixels = 0;
                
                for (int i = x; i < x + 10; i++) {
                    for (int j = y; j < y + 10; j++) {
                        if (i < temp.getWidth() && j < temp.getHeight()) {
                            Pixel p = new Pixel(bufferedImage.getRGB(i,j));
                            valueTotal += p.getValue();
                            totalPixels++;
                        }
                    }
                }
                
                float valueAvg = valueTotal / totalPixels;
                
                if (valueAvg > .75f) {
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (massiveLight[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else if (valueAvg > .25f) {
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (massiveMed[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (i + x < temp.getWidth() && j + y < temp.getHeight()) {
                                if (massiveDark[i][j] == 0) {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(255,255,255).getRGB());
                                } else {
                                    bufferedImage.setRGB(i + x, j + y, new Pixel(0,0,0).getRGB());
                                }
                                
                            }
                        }
                    }
                }
            }
        }
    }

    void crop() {
      BufferedImage temp = new BufferedImage(bufferedImage.getWidth() / 4, bufferedImage.getHeight() / 4, BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < temp.getHeight(); y++) {
        for (int x = 0; x < temp.getWidth(); x++) {
          int color_int = bufferedImage.getRGB(x + bufferedImage.getWidth() / 2 - bufferedImage.getWidth() / 8, y + bufferedImage.getHeight() / 2 - bufferedImage.getHeight() / 8);

          Pixel p = new Pixel(color_int);
          temp.setRGB(x, y, p.getColor().getRGB());
        }
      }

      bufferedImage = temp;
    }

    void scaleLinear() {
      BufferedImage temp = new BufferedImage(bufferedImage.getWidth() * 4, bufferedImage.getHeight() * 4, BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < temp.getHeight(); y++) {
        for (int x = 0; x < temp.getWidth(); x++) {
          int color_int = bufferedImage.getRGB((int) (x / 4.0f), (int) (y / 4.0f));

          Pixel p = new Pixel(color_int);
          temp.setRGB(x, y, p.getColor().getRGB());
        }
      }

      bufferedImage = temp;
    }

    void scaleBilinear() {
      BufferedImage temp = new BufferedImage(bufferedImage.getWidth() * 4, bufferedImage.getHeight() * 4, BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < temp.getHeight(); y++) {
        for (int x = 0; x < temp.getWidth(); x++) {

          float landX = x / 4.0f;
          float landY = y / 4.0f;

          int lesserX = (int) landX;
          int greaterX = lesserX + 1;
          int lesserY = (int) landY;
          int greaterY = lesserY + 1;

          int[][] coordsX = new int[2][2];
          int[][] coordsY = new int[2][2];

          coordsX[0][0] = lesserX;
          coordsY[0][0] = lesserY;

          coordsX[0][1] = greaterX;
          coordsY[0][1] = lesserY;

          coordsX[1][0] = lesserX;
          coordsY[1][0] = greaterY;

          coordsX[1][1] = greaterX;
          coordsY[1][1] = greaterY;

          int[][] color_ints = new int[2][2];
          for (int y2 = 0; y2 < 2; y2++) {
            for (int x2 = 0; x2 < 2; x2++) {
              int getX = coordsX[y2][x2];
              int getY = coordsY[y2][x2];
              if (getX >= bufferedImage.getWidth()) {
                getX = bufferedImage.getWidth() - 1;
              }
              if (getY >= bufferedImage.getHeight()) {
                getY = bufferedImage.getHeight() - 1;
              }
              color_ints[y2][x2] = bufferedImage.getRGB(getX, getY);
            }
          }

          //Now that I have my colors, calculate my final color.
          Pixel color_top = Pixel.interpolate(color_ints[0][0], color_ints[0][1], landX - lesserX);
          Pixel color_bottom = Pixel.interpolate(color_ints[1][0], color_ints[1][1], landX - lesserX);

          Pixel finalPixel = Pixel.interpolate(color_top, color_bottom, landY - lesserY);

          temp.setRGB(x, y, finalPixel.getColor().getRGB());
        }
      }

      bufferedImage = temp;
    }

    void sliceRebuild(int start) {

      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {

          int color_int = bufferedImage.getRGB(x, y);

          Pixel finalPixel = new Pixel(color_int);

          int[] slices = new int[8];

          for (int i = 0; i < 8; i++) {
            slices[i] = finalPixel.getSlice(i);
          }
          int rebuild = 0;
          for (int i = start; i < 8; i++) {
            int toAdd = slices[i] == 255 ? 1 : 0;
            rebuild += toAdd << (i);

          }

          bufferedImage.setRGB(x, y, new Pixel(rebuild, rebuild, rebuild).getColor().getRGB());
        }
      }

    }

    void flipHorizontal() {
      BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() , BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < newImage.getHeight(); y++) {
        for (int x = 0; x < newImage.getWidth(); x++) {


          //Update here to implement method
          int toGetX = x; //This dosen't change
          int toGetY = newImage.getHeight() - 1 - y;
            //System.out.println(toGetY);;



          int color = bufferedImage.getRGB(toGetX, toGetY);        
          newImage.setRGB(x, y, color);              
        }
      }

      bufferedImage = newImage;
    }

    void flipVertical() {
      BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() , BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < newImage.getHeight(); y++) {
        for (int x = 0; x < newImage.getWidth(); x++) {


          //Update here to implement method
          int toGetX = newImage.getWidth() - 1 - x;
          int toGetY = y; // This doesn't change



          int color = bufferedImage.getRGB(toGetX, toGetY);        
          newImage.setRGB(x, y, color);              
        }
      }

      bufferedImage = newImage;
    }

    void rotateClockwise() {
      BufferedImage newImage = new BufferedImage(bufferedImage.getHeight(), bufferedImage.getWidth() , BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < newImage.getHeight(); y++) {
        for (int x = 0; x < newImage.getWidth(); x++) {


          //Update here to implement method
          int toGetX = y;
          int toGetY = bufferedImage.getHeight() - 1 - x;



          int color = bufferedImage.getRGB(toGetX, toGetY);        
          newImage.setRGB(x, y, color);              
        }
      }

      bufferedImage = newImage;
    }

    void rotateCounterClockwise() {
      BufferedImage newImage = new BufferedImage(bufferedImage.getHeight(), bufferedImage.getWidth() , BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < newImage.getHeight(); y++) {
        for (int x = 0; x < newImage.getWidth(); x++) {


          //Update here to implement method
          int toGetX = bufferedImage.getWidth() - 1 - y;
          int toGetY = x;



          int color = bufferedImage.getRGB(toGetX, toGetY);        
          newImage.setRGB(x, y, color);              
        }
      }

      bufferedImage = newImage;
    }

    void rotateArbitrary(float degrees) {
      BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() , BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < newImage.getHeight(); y++) {
        for (int x = 0; x < newImage.getWidth(); x++) {


          //Update here to implement method
          int toGetX = x;
          int toGetY = y;

          //Move into rotation space
          int xrs = x - newImage.getWidth()/2;
          int yrs = y - newImage.getHeight()/2;

          //Get the length of the hypotenus
          double hypotenus = Math.sqrt(xrs * xrs + yrs * yrs);

          //Calculate the angle
          double currentAngle = Math.atan2(yrs, xrs);

          //Angle in pre-rotated image
          double originalAngle = currentAngle - Math.PI/4.0;

          //Move back into Euclidean space
          double xrs_orginal = Math.cos(originalAngle) * hypotenus;
          double yrs_orginal = Math.sin(originalAngle) * hypotenus;

          //Move into screen/image space
          double x_original = xrs_orginal + newImage.getWidth()/2;
          double y_original = yrs_orginal + newImage.getHeight()/2;

          int color;
          //Check to see if I'm in bounds
          if(x_original < 0 || y_original < 0 || x_original >= newImage.getWidth() || y_original >= newImage.getHeight())
          {
              color = Color.PINK.getRGB();            
          }
          else{
              color = bufferedImage.getRGB((int)x_original, (int)y_original);
          }


          newImage.setRGB(x, y, color);              
        }
      }

      bufferedImage = newImage;
    }
    
    public void generate16ColorPalletteWithMedianCut() {
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                Pixel pixel = new Pixel(bufferedImage.getRGB(j, i));
                
                if (pixel.getRed() < minRed) {
                    minRed = pixel.getRed();
                }
                if (pixel.getRed() > maxRed) {
                    maxRed = pixel.getRed();
                }
                if (pixel.getGreen() < minGreen) {
                    minGreen = pixel.getGreen();
                }
                if (pixel.getGreen() > maxGreen) {
                    maxGreen = pixel.getGreen();
                }
                if (pixel.getBlue() < minBlue) {
                    minBlue = pixel.getBlue();
                }
                if (pixel.getBlue() > maxBlue) {
                    maxBlue = pixel.getBlue();
                }
            }
        }
        
        blueRange = maxBlue - minBlue;
        greenRange = maxGreen - minGreen;
        redRange = maxRed - minRed;
      
        if (blueRange >= greenRange && blueRange >= redRange) {
//            pixelList = new PriorityQueue<Pixel>(bufferedImage.getHeight() * bufferedImage.getWidth(), new PixelCompBlue());
            pixelList = new TreeSet<Pixel>(new PixelCompBlue());
            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                for (int j = 0; j < bufferedImage.getWidth(); j++) {
                    pixelList.add(new Pixel(bufferedImage.getRGB(j, i)));
                }
            }
        }
        if (greenRange >= blueRange && greenRange >= redRange) {
//            pixelList = new PriorityQueue<Pixel>(bufferedImage.getHeight() * bufferedImage.getWidth(), new PixelCompGreen());
            pixelList = new TreeSet<Pixel>(new PixelCompGreen());
            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                for (int j = 0; j < bufferedImage.getWidth(); j++) {
                    pixelList.add(new Pixel(bufferedImage.getRGB(j, i)));
                }
            }
        }
        if (redRange >= blueRange && redRange >= greenRange) {
//            pixelList = new PriorityQueue<Pixel>(bufferedImage.getHeight() * bufferedImage.getWidth(), new PixelCompRed());
            pixelList = new TreeSet<Pixel>(new PixelCompRed());
            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                for (int j = 0; j < bufferedImage.getWidth(); j++) {
                    pixelList.add(new Pixel(bufferedImage.getRGB(j, i)));
                }
            }
        }
        
        int split = pixelList.size() / 16;
        int start = 0;
        
        for (int i = 0; i < 16; i++) {
//            int colorTotal = 0;
            int redTotal = 0;
            int greenTotal = 0;
            int blueTotal = 0;
            
            for (int j = start; j < split; j++) {
//                Pixel pixel = pixelList.poll();
                Pixel pixel = pixelList.pollFirst();
                if (pixel != null) {
                    redTotal += pixel.getRed();
                    greenTotal += pixel.getGreen();
                    blueTotal += pixel.getBlue();
//                    colorTotal += pixel.getOriginalInt();
                } else {
                    break;
                }
            }
            
            pallette16[i] = new Pixel(redTotal / split, greenTotal / split, blueTotal / split);
        }    
    }
     
    public void ColorConversion16() {
        System.out.println("Generating Pallette");
        generate16ColorPalletteWithMedianCut();
        System.out.println("Finished pallette");
        
        for (int i = 0; i < pallette16.length; i++) {
            System.out.println("R: " + pallette16[i].getRed() + " G: " + pallette16[i].getGreen() + " B: " + pallette16[i].getBlue());
        }
        
        for (int i = 0; i < bufferedImage.getWidth(); i ++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                Pixel pixel = new Pixel(bufferedImage.getRGB(i, j));
                
                int index = 0;
                double distance = EuclideanDistance(pixel, pallette16[index]);
                
                for (int k = 1; k < pallette16.length; k++) {
                    if (EuclideanDistance(pixel, pallette16[k]) < distance) {
                        index = k;
                    }
                }
                
                bufferedImage.setRGB(i, j, pallette16[index].getColor().getRGB());
            }
        }
    }
    
    public void euclideanThreshold() {
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                int color_int = bufferedImage.getRGB(i, j);
                
                Pixel p = new Pixel(color_int);
                
                double blackDistance = EuclideanDistance(new Pixel(0,0,0), p);
                double whiteDistance = EuclideanDistance(new Pixel(255,255,255), p);
                
                if (blackDistance < whiteDistance) {
                    bufferedImage.setRGB(i,j, new Pixel(0,0,0).getColor().getRGB());
                } else {
                    bufferedImage.setRGB(i,j, new Pixel(255,255,255).getColor().getRGB());
                }
            }
        }
    }
    
    public double EuclideanDistance(Pixel p1, Pixel p2) {
        return Math.sqrt(Math.pow(p1.getRed() - p2.getRed(), 2) + Math.pow(p1.getGreen() - p2.getGreen(), 2) + Math.pow(p1.getBlue() - p2.getBlue(), 2));
    }
    
    public void runThresholdMap64(boolean color) {
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                int color_int = bufferedImage.getRGB(i, j);
                
                Pixel p = new Pixel(color_int);
                
                if (color) {
                    p.colorThresholdMap64Dither(i, j);
                } else {
                    p.thresholdMap64Dither(i, j);
                }
                
                
                bufferedImage.setRGB(i, j, p.getColor().getRGB());
            }
        }
    }
    
    public void runThresholdMap9() {
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                int color_int = bufferedImage.getRGB(i, j);
                
                Pixel p = new Pixel(color_int);
                
                p.thresholdMap9Dither(i,j);
                
                bufferedImage.setRGB(i, j, p.getColor().getRGB());
            }
        }
    }
    
    public void errorDiffusion1D() { 
        int threshold = 255 / 2;
        int error = 0;
        
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                int color_int = bufferedImage.getRGB(j, i);
                
                Pixel p = new Pixel(color_int);
                p.toGrayscaleAverage();
                
                if (p.getRed() - error < threshold) {
                    p.setPixelChannels(0,0,0);
                    bufferedImage.setRGB(j, i, p.getColor().getRGB());
                } else {
                    p.setPixelChannels(255,255,255);
                    bufferedImage.setRGB(j, i, p.getColor().getRGB());
                }
                
                int diff = p.getRed() - threshold;
                error += diff;
                
                
            }
        }
    }
    
    public void errorDiffusion1DColor() { 
        int threshold = 20;
        int errorRed = 0;
        int errorGreen = 0;
        int errorBlue = 0;
        
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                int color_int = bufferedImage.getRGB(j, i);
                
                Pixel p = new Pixel(color_int);
                
                if (p.getRed() - errorRed < threshold && p.getGreen() - errorGreen < threshold && p.getBlue() - errorBlue < threshold) {
                    p.setPixelChannels(0,0,0);
                } else if (p.getRed() - errorRed > threshold && p.getGreen() - errorGreen > threshold && p.getBlue() - errorBlue > threshold){
                    p.setPixelChannels(255,255,255);
                } else if (p.getRed() - errorRed < threshold && p.getGreen() - errorGreen > threshold && p.getBlue() - errorBlue > threshold) {
                    p.setPixelChannels(0,p.getGreen(),p.getBlue());
                } else if (p.getRed() - errorRed > threshold && p.getGreen() - errorGreen < threshold && p.getBlue() - errorBlue > threshold) {
                    p.setPixelChannels(p.getRed(),0,p.getBlue());
                } else if (p.getRed() - errorRed > threshold && p.getGreen() - errorGreen > threshold && p.getBlue() - errorBlue < threshold) {
                    p.setPixelChannels(p.getRed(),p.getGreen(),0);
                } else if (p.getRed() - errorRed < threshold && p.getGreen() - errorGreen < threshold && p.getBlue() - errorBlue > threshold) {
                    p.setPixelChannels(0,0,p.getBlue());
                } else if (p.getRed() - errorRed < threshold && p.getGreen() - errorGreen > threshold && p.getBlue() - errorBlue < threshold) {
                    p.setPixelChannels(0,p.getGreen(),0);
                } else if (p.getRed() - errorRed > threshold && p.getGreen() - errorGreen < threshold && p.getBlue() - errorBlue < threshold) {
                    p.setPixelChannels(p.getRed(),0,0);
                }
                
                bufferedImage.setRGB(j, i, p.getColor().getRGB());
                
                int diffRed = p.getRed() - threshold;
                int diffGreen = p.getGreen() - threshold;
                int diffBlue = p.getBlue() - threshold;
                errorRed += diffRed;
                errorGreen += diffGreen;
                errorBlue += diffBlue;
                
                
                
            }
        }
    }
    
    public void errorDiffusion2DColor() {
        BufferedImage temp = BufferedImageCloner.clone(bufferedImage);

        int threshold = 90;
        int errorRed = 0;
        int errorGreen = 0;
        int errorBlue = 0;
        
        for (int i = 0; i < temp.getHeight(); i++) {
            for (int j = 0; j < temp.getWidth(); j++) {
                Pixel p = new Pixel(temp.getRGB(j, i));
                
                if (p.getRed() < threshold && p.getGreen() < threshold && p.getBlue() < threshold) {
                    p.setPixelChannels(0,0,0);
                } else if (p.getRed() > threshold && p.getGreen() > threshold && p.getBlue() > threshold){
                    p.setPixelChannels(255,255,255);
                } else if (p.getRed() < threshold && p.getGreen() > threshold && p.getBlue() > threshold) {
                    p.setPixelChannels(0,p.getGreen(),p.getBlue());
                } else if (p.getRed() > threshold && p.getGreen() < threshold && p.getBlue() > threshold) {
                    p.setPixelChannels(p.getRed(),0,p.getBlue());
                } else if (p.getRed() > threshold && p.getGreen() > threshold && p.getBlue() < threshold) {
                    p.setPixelChannels(p.getRed(),p.getGreen(),0);
                } else if (p.getRed() < threshold && p.getGreen() < threshold && p.getBlue() > threshold) {
                    p.setPixelChannels(0,0,p.getBlue());
                } else if (p.getRed() < threshold && p.getGreen() > threshold && p.getBlue() < threshold) {
                    p.setPixelChannels(0,p.getGreen(),0);
                } else if (p.getRed() > threshold && p.getGreen() < threshold && p.getBlue() < threshold) {
                    p.setPixelChannels(p.getRed(),0,0);
                }
                
                bufferedImage.setRGB(j, i, p.getColor().getRGB());
               
                int diffRed = p.getRed() - threshold;
                int diffGreen = p.getGreen() - threshold;
                int diffBlue = p.getBlue() - threshold;
                errorRed += diffRed;
                errorGreen += diffGreen;
                errorBlue += diffBlue;
                
                if (i < temp.getHeight() - 1) {
                    Pixel _p = new Pixel(temp.getRGB(j, i + 1));
                    _p.setPixelChannels(_p.getRed() - errorRed / 2, _p.getGreen() - errorGreen / 2, _p.getBlue() - errorBlue / 2);

                    temp.setRGB(j, i + 1, _p.getColor().getRGB());
                }
                
                if (j < temp.getWidth() - 1) {
                    Pixel _p = new Pixel(temp.getRGB(j + 1, i));
                    _p.setPixelChannels(_p.getRed() - errorRed / 2, _p.getGreen() - errorGreen / 2, _p.getBlue() - errorBlue / 2);

                    temp.setRGB(j + 1, i, _p.getColor().getRGB());
                } 
            }
        }
    }
    
    public void errorDiffusion2D() {
        BufferedImage temp = BufferedImageCloner.clone(bufferedImage);
        for (int y = 0; y < temp.getHeight(); y++) {
            for (int x = 0; x < temp.getWidth(); x++) {
                int color_int = temp.getRGB(x, y);

                Pixel p = new Pixel(color_int);
                p.toGrayscaleAverage();
                temp.setRGB(x, y, p.getColor().getRGB());
            }
        }
        
        int threshold = 255 / 2;
        int error = 0;
        
        for (int i = 0; i < temp.getHeight(); i++) {
            for (int j = 0; j < temp.getWidth(); j++) {
                Pixel p = new Pixel(temp.getRGB(j, i));
                
                if (p.getRed() < threshold) {
                    p.setPixelChannels(0,0,0);
                    bufferedImage.setRGB(j, i, p.getColor().getRGB());
                } else {
                    p.setPixelChannels(255,255,255);
                    bufferedImage.setRGB(j, i, p.getColor().getRGB());
                }
                
                int diff = p.getRed() - threshold;
                error += diff;
                
                if (i < temp.getHeight() - 1) {
                    Pixel _p = new Pixel(temp.getRGB(j, i + 1));
                    _p.setPixelChannels(Math.max(0, Math.min(255, _p.getRed() - error / 2)), Math.max(0, Math.min(255, _p.getRed() - error / 2)), Math.max(0, Math.min(255, _p.getRed() - error / 2)));

                    temp.setRGB(j, i + 1, _p.getColor().getRGB());
                }
                
                if (j < temp.getWidth() - 1) {
                    Pixel _p = new Pixel(temp.getRGB(j + 1, i));
                    _p.setPixelChannels(Math.max(0, Math.min(255, _p.getRed() - error / 2)), Math.max(0, Math.min(255, _p.getRed() - error / 2)), Math.max(0, Math.min(255, _p.getRed() - error / 2)));

                    temp.setRGB(j + 1, i, _p.getColor().getRGB());
                } 
            }
        }
    }
    
    void colorReduce() {
        reduceToColors(new Pixel[]{
            new Pixel(0,0,0), 
            new Pixel(255, 255, 255), 
            new Pixel(255, 0, 0), 
            new Pixel(255,255,0),
            new Pixel(0, 255, 0), 
            new Pixel(0, 0, 255)
        });  
    }
    
    void colorReduceVGA() {
        reduceToColors(new Pixel[]{
            new Pixel(0,0,0),
            new Pixel(0,0,170),
            new Pixel(0,170,0),
            new Pixel(0,170,170),
            new Pixel(170,0,0),
            new Pixel(170,0,170),
            new Pixel(170,85,0),
            new Pixel(170,170,170),
            new Pixel(85,85,85),
            new Pixel(85,85,255),
            new Pixel(85,255,85),
            new Pixel(85,255,255),
            new Pixel(255,85,85),
            new Pixel(255,85,255),
            new Pixel(255,255,85),
            new Pixel(255,255,255)
        });
    }
    
    void colorReduceHTML() {
        reduceToColors(new Pixel[]{
            new Pixel(0xFFFFFF),
            new Pixel(0xC0C0C0),
            new Pixel(0x808080),
            new Pixel(0x000000),
            new Pixel(0xFF0000),
            new Pixel(0x800000),
            new Pixel(0xFFFF00),
            new Pixel(0x808000),
            new Pixel(0x00FF00),
            new Pixel(0x008000),
            new Pixel(0x00FFFF),
            new Pixel(0x008080),
            new Pixel(0x0000FF),
            new Pixel(0x000080),
            new Pixel(0xFF00FF),
            new Pixel(0x800080)
        });
    }
    
    void colorReduceML() {
        int numberOfColors = 23;
        Pixel[] means = new Pixel[numberOfColors];
        
        for (int i = 0; i < means.length; i++) {
            means[i] = new Pixel((int) Math.random() * 255, (int) Math.random() * 255, (int) Math.random() * 255);
        }
        int tries = 0;
        while(tries < 10) {
        
            // Now have numberOfColors seeds
            int[][] assignmentArray = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    Pixel p = new Pixel(bufferedImage.getRGB(x, y));
                    float minDistance = Float.MAX_VALUE;
                    int minIndex = -1;
                    for (int i = 0; i < means.length; i++) {
                        Pixel color = means[i];
                        float distance = p.distanceL1(color);
                        if (distance < minDistance) {
                            minDistance = distance;
                            minIndex = i;
                        }
                    }
                    assignmentArray[x][y] = minIndex;
                }
            }

            for (int m = 0; m < means.length; m++) {
                Pixel mean = means[m];
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                int count = 0;

                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    for (int x = 0; x < bufferedImage.getWidth(); x++) {
                        if (assignmentArray[x][y] == m) {
                            Pixel p = new Pixel(bufferedImage.getRGB(x,y));
                            sumR += p.getRed();
                            sumG += p.getGreen();
                            sumB += p.getBlue();
                            count++;
                        }
                    }
                }
                if (count == 0) {
                    mean.setValue(new Pixel((int) Math.random() * 255, (int) Math.random() * 255, (int) Math.random() * 255));
                } else {
                    mean.setValue(new Pixel(sumR/count, sumG/count, sumB/count));
                }
            }
            tries++;
        }
        reduceToColors(means);
    
  }
  
  void reduceToColors(Pixel[] colors){
//      all(p->{
//          float minDistance = Float.MAX_VALUE;
//          Pixel closestColor = null;
//          for (int i = 0; i < colors.length; i++) {
//              Pixel color = colors[i];
//              float distance = p.distanceL1(color);
//              if (distance < minDistance) {
//                  minDistance = distance;
//                  closestColor = color;
//              }
//          }
//          p.setValue(closestColor);
//      });
    all(p->p.setValue(Arrays.stream(colors).reduce((a,b)->p.distanceL1(a) < p.distanceL1(b) ? a:b).get()));
  }
  
  void greenScreen() {
      MyImage other = new MyImage("images/to_green_screen.jpg");
      
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
          for (int y = 0; y < bufferedImage.getHeight(); y++) {
            int color_int = bufferedImage.getRGB(x, y);
                
            Pixel p = new Pixel(color_int);
            
            if (p.isGreenDominant() && x < other.bufferedImage.getWidth() && y < other.bufferedImage.getHeight()) {
                int other_color_int = other.bufferedImage.getRGB(x,y);
                p.setValue(new Pixel(other_color_int));
                bufferedImage.setRGB(x, y, p.getColor().getRGB());
            }
                
          }
      }
  }
  
  void to332() {
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
          for (int y = 0; y < bufferedImage.getHeight(); y++) {
            int color_int = bufferedImage.getRGB(x, y);
                
            Pixel p = new Pixel(color_int);
            
//            p = p.to332(color_int);
            float minDistance = Float.MAX_VALUE;
            Pixel closestPixel = null;

//            for (int i = 0; i < 248; i += 31) {
//                for (int j = 0; j < 248; j += 31) {
//                    for (int k = 0; k < 189; k += 63) {
            for (int i = 0; i <= 255; i += 36) {
                for (int j = 0; j <= 255; j += 36) {
                    for (int k = 0; k <= 255; k += 85) {
                        Pixel pixelCandidate = new Pixel(i,j,k);
                        float distance = p.distanceL1(pixelCandidate);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestPixel = pixelCandidate;
                        }
                    }
                }
            }
            
            bufferedImage.setRGB(x,y, closestPixel.getColor().getRGB());
                
          }
      }
  }
  
  void threshold() {
    BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    for (int y = 0; y < newImage.getHeight(); y++) {
      for (int x = 0; x < newImage.getWidth(); x++) {

        int color = bufferedImage.getRGB(x, y);
        Pixel p = new Pixel(color);
        p.toGrayscale();
        
        int bw = p.getRed();
        if (bw < 128) {
            p.setValue(new Pixel(0,0,0));
        } else {
            p.setValue(new Pixel(255,255,255));
        }
        newImage.setRGB(x, y, p.getRGB());
      }
    }

    bufferedImage = newImage;
  }
  
   void ditherBW() {
    BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    for (int y = 0; y < newImage.getHeight(); y++) {
      int error = 0;
      for (int x = 0; x < newImage.getWidth(); x++) {

        int color = bufferedImage.getRGB(x, y);
        Pixel p = new Pixel(color);
        p.toGrayscale();
        
        int bw = p.getRed();
        int adjusted = bw + error;
        
        if (adjusted < 128) {
            p.setValue(new Pixel(0,0,0));
            error += bw - 0;
        } else {
            p.setValue(new Pixel(255,255,255));
            error += bw - 255;
        }
        

        newImage.setRGB(x, y, p.getRGB());
      }
    }

    bufferedImage = newImage;
  }
   
   void ditherBW2D() {
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        int[][] errorArray = new int[newImage.getWidth()][newImage.getHeight()];
        
        for (int y = 0; y < newImage.getHeight(); y++) {
          //int error = 0;
          for (int x = 0; x < newImage.getWidth(); x++) {
            int error = errorArray[x][y];
            
            int color = bufferedImage.getRGB(x, y);
            Pixel p = new Pixel(color);
            p.toGrayscale();

            int bw = p.getRed();
            int adjusted = bw + error;
            
            

            if (adjusted < 128) {
                p.setValue(new Pixel(0,0,0));
                error += bw - 0;
            } else {
                p.setValue(new Pixel(255,255,255));
                error += bw - 255;
            }
            
            // error now holds error to be spread
            
            if (x + 1 < newImage.getWidth()) {
                errorArray[x + 1][y] += error * (7/16.0);
            }
            if (y + 1 < newImage.getHeight()) {
                if (x > 0) {
                    errorArray[x - 1][y + 1] += error * (3/16.0);
                }
                errorArray[x][y + 1] += error * (5/16.0);
                if (x + 1 < newImage.getWidth()) {
                    errorArray[x + 1][y + 1] += error * (1/16.0);
                }
            }
            
            


            newImage.setRGB(x, y, p.getRGB());
          }
        }

        bufferedImage = newImage;
   }
   
  void ditherColor() {
    Pixel[] myColors = new Pixel[]{
     new Pixel(0,0,0), 
     new Pixel(255, 255, 255), 
     new Pixel(255, 0, 0), 
     new Pixel(0, 255, 0), 
     new Pixel(0, 0, 255),
     new Pixel(255, 255, 0), 
     new Pixel(0, 255, 255), 
     new Pixel(255, 0, 255)
     };
    
    BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    for (int y = 0; y < newImage.getHeight(); y++) {
      
      int errorR = 0;
      int errorG = 0;
      int errorB = 0;
      
      for (int x = 0; x < newImage.getWidth(); x++) {

        int color = bufferedImage.getRGB(x, y);
        Pixel p = new Pixel(color);
        
        Pixel adjusted = new Pixel(p.getRed() + errorR, p.getGreen() + errorG, p.getBlue() + errorB, false);
        
        Pixel chosenColor = Arrays.stream(myColors).reduce((a,b)->a.distanceL1(adjusted) < b.distanceL1(adjusted) ? a : b).get();

        errorR = adjusted.getRed() - chosenColor.getRed();
        errorG = adjusted.getGreen() - chosenColor.getGreen();
        errorB = adjusted.getBlue() - chosenColor.getBlue();
        
        newImage.setRGB(x, y, chosenColor.getRGB());
      }
    }

    bufferedImage = newImage;
  }
  
  void lookingGlass() {
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
          for (int x = 0; x < bufferedImage.getWidth() / 2; x++) {
              bufferedImage.setRGB(bufferedImage.getWidth() - 1 - x,y, bufferedImage.getRGB(x,y));
          }
        }
  }
  
  void rotateFilter(float degrees) {
      BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() , BufferedImage.TYPE_4BYTE_ABGR);
      for (int y = 0; y < newImage.getHeight(); y++) {
        for (int x = 0; x < newImage.getWidth(); x++) {


          //Update here to implement method
          int toGetX = x;
          int toGetY = y;

          //Move into rotation space
          int xrs = x - newImage.getWidth()/2;
          int yrs = y - newImage.getHeight()/2;

          //Get the length of the hypotenus
          double hypotenus = Math.sqrt(xrs * xrs + yrs * yrs);

          //Calculate the angle
          double currentAngle = Math.atan2(y, x);

          //Angle in pre-rotated image
          double originalAngle = currentAngle - Math.PI/4.0;

          //Move back into Euclidean space
          double xrs_original = Math.cos(currentAngle) * hypotenus;
          double yrs_original = Math.sin(currentAngle) * hypotenus;

          //Move into screen/image space
          double x_original = xrs_original + newImage.getWidth()/2;
          double y_original = yrs_original + newImage.getHeight()/2;

          int color;
          //Check to see if I'm in bounds
          if(x_original < 0 || y_original < 0 || x_original >= newImage.getWidth() || y_original >= newImage.getHeight())
          {
              color = Color.PINK.getRGB();            
          }
          else{
              color = bufferedImage.getRGB((int)x_original, (int)y_original);
          }


          newImage.setRGB(x, y, color);              
        }
      }

      bufferedImage = newImage;
    }

}
