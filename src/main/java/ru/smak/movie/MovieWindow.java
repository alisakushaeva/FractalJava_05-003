package ru.smak.movie;

import ru.smak.graphics.FractalPainter;
import ru.smak.graphics.Plane;
import ru.smak.gui.GraphicsPanel;
import ru.smak.gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    public MovieWindow(MainWindow mainWindow){
        container = new JPanel();
        GridLayout layout = new GridLayout(2,0,5,12);
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

        AddFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //добавление кадра в список ключевых кадров
                frames.add(new FractalPainter((FractalPainter)mainWindow.getMainPanel().getAllPainters("class ru.smak.graphics.FractalPainter").get(0)));
                GraphicsPanel moviePanel = new GraphicsPanel();
                moviePanel.setBackground(Color.WHITE);
                FractalPainter fp = new FractalPainter((FractalPainter)mainWindow.getMainPanel().getAllPainters("class ru.smak.graphics.FractalPainter").get(0));
                moviePanel.addPainter(fp);

                //BufferedImage img = moviePanel.get
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
                //видео начинает создаваться
                if (frames.size() != 0) {
                    duration = (int) (Duration.getValue());
                    fps = (int) (FPS.getValue());
                    movie = new MovieMaker(frames, duration, fps);
                    movie.create();
                }
            }
        });

        Play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //видео запускается (кнопка доступна только после того, как видео создано)
                movie.show();
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
