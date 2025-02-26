package ru.smak.movie;

import kotlin.Pair;
import ru.smak.graphics.FractalPainter;
import ru.smak.graphics.Plane;
import ru.smak.gui.GraphicsPanel;
import ru.smak.gui.MainWindow;
import ru.smak.gui.Scaler;
import ru.smak.gui.UndoRedoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MovieWindow extends JFrame {
    private JPanel controlPanel;
    private final Dimension minSz = new Dimension(600, 500);
    private JButton AddFile, OK, Play;
    private JSpinner FPS, Duration;
    private JLabel FPSlbl, Durationlbl;
    private ArrayList<FractalPainter> frames;
    private JPanel container;
    private int fps, duration;
    private MovieMaker movie;
    private Scaler scaler;
    private UndoRedoManager undoRedoManager;
    private JProgressBar progressbar;
    private JOptionPane optionPane;
    public MovieWindow(MainWindow mainWindow){
        progressbar = new JProgressBar();
        optionPane = new JOptionPane();
        container = new JPanel();
        GridLayout layout = new GridLayout(1,0,5,12);
        container.setBackground(Color.WHITE);
        container.setLayout(layout);
        controlPanel = new JPanel();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setMinimumSize(minSz);
        GroupLayout gl = new GroupLayout(getContentPane());
        GroupLayout glcp = new GroupLayout(controlPanel);
        SpinnerNumberModel mdlFPS = new SpinnerNumberModel(30, 1, 1000, 1);
        SpinnerNumberModel mdlDuration = new SpinnerNumberModel(30, 1, 1000, 1);
        AddFile = new JButton("Добавить кадр");
        FPS = new JSpinner(mdlFPS);
        Duration = new JSpinner(mdlDuration);
        FPSlbl = new JLabel("FPS");
        Durationlbl = new JLabel("Duration");
        OK = new JButton("OK");
        setLayout(gl);
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setLayout(glcp);
        frames = new ArrayList<FractalPainter>();
        progressbar.setStringPainted(true);

        AddFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //добавление кадра в список ключевых кадров
                frames.add(new FractalPainter((FractalPainter)mainWindow.getMainPanel().getAllPainters("class ru.smak.graphics.FractalPainter").get(0)));
                GraphicsPanel moviePanel = new GraphicsPanel();
                moviePanel.setBackground(Color.WHITE);
                FractalPainter fp = new FractalPainter((FractalPainter)mainWindow.getMainPanel().getAllPainters("class ru.smak.graphics.FractalPainter").get(0));
                for (FractalPainter fractalPainter:
                     frames) {
                    scaler = new Scaler(fractalPainter.getPlane());
                    fractalPainter.getPlane().setWidth(container.getWidth()/frames.size());
                    fractalPainter.getPlane().setHeight(container.getHeight());
                    fractalPainter.getPlane().setXEdges(new Pair<>(scaler.getXMin(),scaler.getXMax()));
                    fractalPainter.getPlane().setYEdges(new Pair<>(scaler.getYMin(),scaler.getYMax()));
                    scaler.scale();
                    moviePanel.addPainter(fractalPainter);
                }

                //удаление кадра из списка ключевых кадров
                moviePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        frames.remove(frames.get(frames.size()-1));
                        container.remove(moviePanel);
                        container.revalidate();
                        container.repaint();
                    }
                });
                container.add(moviePanel);
                container.revalidate();
            }
        });

        OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressbar.setValue(0);
                new Thread(()-> {
                    if (frames.size() != 0) {
                        duration = (int) (Duration.getValue());
                        fps = (int) (FPS.getValue());
                        movie = new MovieMaker(frames, duration, fps);
                        movie.create();
                        progressbar.setValue(movie.getPercent());
                    }
                }).start();
                optionPane.showMessageDialog(null, progressbar);
            }
        });

        container.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for (FractalPainter fractalPainter:
                        frames) {
                    scaler = new Scaler(fractalPainter.getPlane());
                    fractalPainter.getPlane().setWidth(container.getWidth()/frames.size());
                    fractalPainter.getPlane().setHeight(container.getHeight());
                    scaler.scale();
                }
                container.repaint();
            }
        });

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addGroup(gl.createParallelGroup()
                        .addComponent(container, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(controlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                )
                .addGap(8)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(container, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
                .addComponent(controlPanel, 70,70,70)
                .addGap(8)
        );

        glcp.setHorizontalGroup(glcp.createSequentialGroup()
                .addGap(8)
                .addComponent(AddFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
                .addComponent(FPSlbl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
                .addComponent(FPS, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
                .addComponent(Durationlbl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
                .addComponent(Duration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
                .addComponent(OK, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
        );

        glcp.setVerticalGroup(glcp.createSequentialGroup()
                .addGap(8)
                .addGroup(glcp.createParallelGroup()
                        .addComponent(AddFile, GroupLayout.Alignment.CENTER)
                        .addGap(8)
                        .addComponent(FPSlbl, GroupLayout.Alignment.CENTER)
                        .addGap(8)
                        .addComponent(FPS, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGap(8)
                        .addComponent(Durationlbl, GroupLayout.Alignment.CENTER)
                        .addGap(8)
                        .addComponent(Duration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGap(8)
                        .addComponent(OK, GroupLayout.Alignment.CENTER)
                        .addGap(8)
                )
                .addGap(8)
        );
    }
}
