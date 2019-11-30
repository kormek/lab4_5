package bsu.rfe.java.group10.lab4.anufriev;
import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;

    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showNet = true;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;

    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;

    private Font axisFont;

    public GraphicsDisplay () {
        setBackground(Color.white);

        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,10.0f,null,0.0f);

        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[] {4,1,1,1,1,1}, 0.0f);

        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        axisFont = new Font("Serif", Font.BOLD, 36);
      //  addMouseMotionListener(new MouseMotionHandler());
        //addMouseListener(new MouseHandler());

    }

    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void setShowNet(boolean showNet) {
        this.showNet = showNet;
        repaint();
    }

    public void zoomToRegion(double x1,double y1,double x2,double y2)	{
        this.minX=x1;
        this.minY=y1;
        this.maxX=x2;
        this.maxY=y2;
        this.repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(graphicsData==null || graphicsData.length==0) return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);
        //scale = 100;

        if (scale==scaleX) {
            double yIncrement = (getSize().getHeight()/scale - (maxY - minY))/2;

            maxY += yIncrement;
            minY -= yIncrement;
        }

        if (scale==scaleY) {
            double xIncrement = (getSize().getWidth()/scale - (maxX - minX))/2;

            maxX += xIncrement;
            minX -= xIncrement;
        }
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);

        paintGraphics(canvas);




        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);

        if(showNet) paintNet(canvas);
    }

    protected void paintNet(Graphics2D canvas){
        canvas.setStroke(new BasicStroke(1));
        canvas.setColor(Color.GREEN);int i = 0;
        double temp=0.1*(maxY-minY);
        while(i<12){
        canvas.draw(new Line2D.Double(xyToPoint(minX, minY+i*temp), xyToPoint(maxX,minY+i*temp)));i++;
        }
        //temp = 0.1*(maxX-minX);
        i=0;
        while(i<25){
            canvas.draw(new Line2D.Double(xyToPoint(0+i, maxY), xyToPoint(0+i,minY)));
            canvas.draw(new Line2D.Double(xyToPoint(0-i, maxY), xyToPoint(0-i,minY)));
            i++;
            //canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            //canvas.draw(new Line2D.Double(xyToPoint(minX, i), xyToPoint(maxX, 0)));
        }
        /*double pos = graphicsData[0][0];;
        double step = (graphicsData[1][0] - graphicsData[0][0])/10;

        while (pos < graphicsData[1][0]){
            canvas.draw(new Line2D.Double(xyToPoint(pos, graphicsData[0][1]), xyToPoint(pos, graphicsData[1][1])));
            pos += step;
        }
        canvas.draw(new Line2D.Double(xyToPoint(graphicsData[1][0],graphicsData[0][1]), xyToPoint(graphicsData[1][0],graphicsData[1][1])));

        pos = graphicsData[1][1];
        step = (graphicsData[0][1] - graphicsData[1][1]) / 10;
        while (pos < graphicsData[0][1]){
            canvas.draw(new Line2D.Double(xyToPoint(graphicsData[0][0], pos), xyToPoint(graphicsData[1][0], pos)));
            pos=pos + step;
        }
        canvas.draw(new Line2D.Double(xyToPoint(graphicsData[0][0],graphicsData[0][1]), xyToPoint(graphicsData[1][0],graphicsData[0][1])));*/


    }
    protected void paintGraphics(Graphics2D canvas){
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);

        GeneralPath graphics = new GeneralPath();
        for (int i=0; i<graphicsData.length; i++) {
// Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i>0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }

    }

    protected void paintMarkers(Graphics2D canvas) {

        canvas.setStroke(markerStroke);

        canvas.setColor(Color.RED);

        canvas.setPaint(Color.RED);




        for (Double[] point: graphicsData) {

            GeneralPath marker = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1]);
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX(),marker.getCurrentPoint().getY()-5);
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX(),marker.getCurrentPoint().getY()+5);
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX()-5,marker.getCurrentPoint().getY());
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX()+5,marker.getCurrentPoint().getY());
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX()+5,marker.getCurrentPoint().getY()-5);
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX()+5,marker.getCurrentPoint().getY()+5);
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX()-5,marker.getCurrentPoint().getY()-5);
            marker.moveTo(center.getX(),center.getY());
            marker.lineTo(marker.getCurrentPoint().getX()-5,marker.getCurrentPoint().getY()+5);
            marker.moveTo(center.getX(),center.getY());
            canvas.draw(marker);
            canvas.fill(marker);
           // Ellipse2D.Double marker = new Ellipse2D.Double();

            if((int)(center.getX()%10 - (center.getX()%100)/10) == 1 || (int)(center.getY()%10 - (center.getY()%100)/10) == 1 /*|| (int)center.getX()%2 == 0*/){
                canvas.setPaint(Color.GREEN);
                canvas.setColor(Color.GREEN);
                canvas.draw(marker);
                canvas.fill(marker);
                canvas.setPaint(Color.RED);
            }

            //Point2D.Double center = xyToPoint(point[0], point[1]);

            //Point2D.Double corner = shiftPoint(center, 3, 3);

          //  marker.setFrameFromCenter(center, corner);
            //canvas.draw(marker); // Начертить контур маркера
            //canvas.fill(marker); // Залить внутреннюю область маркера
        }

    }

    protected void paintAxis(Graphics2D canvas) {
// Установить особое начертание для осей
        canvas.setStroke(axisStroke);
// Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
// Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
// Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
// Создать объект контекста отображения текста - для получения



                FontRenderContext context = canvas.getFontRenderContext();
// Определить, должна ли быть видна ось Y на графике
        if (minX<=0.0 && maxX>=0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
                    xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5,
                    arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10,
                    arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);

            canvas.drawString("y", (float) labelPos.getX() + 10,
                    (float) (labelPos.getY() - bounds.getY()));
        }

        if (minY<=0.0 && maxY>=0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                    arrow.getCurrentPoint().getY()-5);
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY()+10);

            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float)(labelPos.getX() -
                    bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
        }
    }

    protected Point2D.Double xyToPoint(double x, double y) {
// Вычисляем смещение X от самой левой точки (minX)
        double deltaX = x - minX;
// Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();

        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}
