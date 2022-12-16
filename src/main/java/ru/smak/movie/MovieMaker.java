package ru.smak.movie;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Rational;
import org.w3c.dom.events.Event;
import ru.smak.graphics.Colorizer;
import ru.smak.graphics.FractalPainter;
import ru.smak.graphics.Plane;
import ru.smak.gui.GraphicsPanel;
import ru.smak.math.fractals.Fractal;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.util.ArrayList;

public class MovieMaker {
    /**
     * Длительность видео в секундах.
     */
    int time;
    /**
     * Количество сменяемых кадров за одну секунду.
     */
    int fps;
    int N,width,height;//общее количество добавляемых кадров, ширина и высота
    double K;//суммарное изменение фрактала(его площади)
    /**
     * Массив кадров, передаваемых пользователем.
     */
    private Colorizer color;
    private Fractal fractal;
    private int percent;
    /**
     * Массив кадров, передаваемых пользователем.
     */
    private ArrayList<FractalPainter> keyFrames;//массив ключевых кадров
    private ArrayList<Double> coefficients;//массив коэффициентов
    private ArrayList<Integer> countOfFrames;//массив количества добавляемых кадров между соседними ключевыми кадрами

    private ArrayList<FractalPainter> frames;//массив всех кадров
    private ArrayList<BufferedImage> images;

    public MovieMaker(ArrayList<FractalPainter> keyFrames, int time, int fps){
        this.keyFrames = keyFrames;
        this.frames = new ArrayList<>();
        this.images = new ArrayList<BufferedImage>();
        this.time = time;
        this.fps = fps;
        this.color = keyFrames.get(0).getColorFunc();
        this.fractal = keyFrames.get(0).getFractal();
        this.width = keyFrames.get(0).getWidth();
        this.height = keyFrames.get(0).getHeight();
        this.coefficients = (ArrayList<Double>)(getCoefficients().clone());
        this.N = numberOfFrames();
        this.K = sumCoeff();
    }

    public void create(){
        this.countOfFrames = (ArrayList<Integer>)(getCountOfFrames().clone());
        for (int i = 0; i < keyFrames.size()-1; i++){
            frames.add(new FractalPainter(keyFrames.get(i)));
            int k = 1;
            double deltaXMin = keyFrames.get(i+1).getPlane().getXMin() - keyFrames.get(i).getPlane().getXMin();
            double deltaXMax = keyFrames.get(i).getPlane().getXMax() - keyFrames.get(i+1).getPlane().getXMax();
            double deltaYMin = keyFrames.get(i+1).getPlane().getYMin() - keyFrames.get(i).getPlane().getYMin();
            double deltaYMax = keyFrames.get(i).getPlane().getYMax() - keyFrames.get(i+1).getPlane().getYMax();
            for (int j = 0; j < countOfFrames.get(i); j++){
                //добавляем кадры
                double xMin = keyFrames.get(i).getPlane().getXMin() + k*deltaXMin/countOfFrames.get(i);
                double xMax = keyFrames.get(i).getPlane().getXMax() - k*deltaXMax/countOfFrames.get(i);
                double yMin = keyFrames.get(i).getPlane().getYMin() + k*deltaYMin/countOfFrames.get(i);
                double yMax = keyFrames.get(i).getPlane().getYMax() - k*deltaYMax/countOfFrames.get(i);
                k++;
                fractal = keyFrames.get(i).getFractal();
                color = keyFrames.get(i).getColorFunc();
                Plane p = new Plane(xMin, xMax, yMin, yMax, width, height);
                frames.add(new FractalPainter(p,fractal, color));
            }
        }
        frames.add((new FractalPainter(keyFrames.get(keyFrames.size()-1))));
        for (int i = 0; i<frames.size();i++){
            images.add(frames.get(i).getBufferedImage());
        }
        //создание видео:
        AWTSequenceEncoder encoder = null;
        try {
            encoder = AWTSequenceEncoder.createSequenceEncoder(new File("video.mp4"), fps);
            for (BufferedImage image : images) {
                encoder.encodeImage(image);
            }
            encoder.finish();
        } catch (Exception e) {
            System.out.println("Fail to generate video!");
        }
    }


    //метод, который возвращает коэффициент - во сколько раз изменилась плоскость
    public double getCoeff(FractalPainter p1, FractalPainter p2){
        return Math.abs((p1.getPlane().getXMax()-p1.getPlane().getXMin())*(p1.getPlane().getYMax()-p1.getPlane().getYMin())
                /(p2.getPlane().getXMax()-p2.getPlane().getXMin())/(p2.getPlane().getYMax()-p2.getPlane().getYMin()));
    }
    //массив коэффициентов
    public ArrayList<Double> getCoefficients(){
        ArrayList<Double> c = new ArrayList<>();
        for (int i = 0; i < keyFrames.size()-1; i++){
            c.add(getCoeff(keyFrames.get(i), keyFrames.get(i+1)));
        }
        return c;
    }
    //сумма коэффициентов К
    public double sumCoeff(){
        double res = 0;
        for (int i = 0; i < this.coefficients.size(); i++){
            res += this.coefficients.get(i);
        }
        return res;
    }

    //массив количества кадров
    public ArrayList<Integer> getCountOfFrames(){
        ArrayList<Integer> n = new ArrayList<Integer>();
        for(int i = 0; i < keyFrames.size()-1; i++){
            n.add((int)(coefficients.get(i)*(double)N/K));
        }
        return n;
    }
    //общее число добавляемых кадров
    public int numberOfFrames(){
        return fps*time-keyFrames.size();
    }
    public int getFps() {
        return fps;
    }
    public void setFps(int fps){
        this.fps = fps;
    }

    public int getTime() {
        return time;
    }
    public void setTime(int time){
        this.time = time;
    }

    public int getPercent() {
        return (int)((double)frames.size()*100/time/fps);
    }
}
