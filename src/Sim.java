import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Sim{
	public static ArrayList<Particle> list = new ArrayList<Particle>();
	public static long t;
	public static final int width = 800;
	public static final int height = 600;
	public static BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	public static int[] pixels;
	public static double scale = 6.6844919786096256684491978609626e-10;
	public static double xoffset = 300;
	public static double yoffset = 200;
	public static boolean isDragging;
	public static Particle follow;
	public static DecimalFormat format = new DecimalFormat(",###.##");
	public static DecimalFormat lformat = new DecimalFormat(",###");
	public static class MouseListener extends MouseAdapter{
		static double xPress;
		static double yPress;
		public void mouseWheelMoved(MouseWheelEvent e){
			double currScale = 1 + e.getPreciseWheelRotation() / 10D;
			scale /= currScale;
			xoffset = (xoffset - e.getX()) / currScale + e.getX();
			yoffset = (yoffset - e.getY()) / currScale + e.getY();
		}
		public void mouseReleased(MouseEvent e){
			if(e.getButton() == 1){
				isDragging = false;
			}
		}
		public void mouseDragged(MouseEvent e){
			if(isDragging){
				xoffset = e.getX() - xPress;
				yoffset = e.getY() - yPress;
			}
		}
		public void mousePressed(MouseEvent e){
			if(e.getButton() == 1){
				xPress = e.getX() - xoffset;
				yPress = e.getY() - yoffset;
				isDragging = true;
			}
		}
	}
	public static void main(String[] args){
		Frame frame = new Frame("Gravity Simulation");
		Canvas canvas = new Canvas();
		canvas.addMouseListener(new MouseListener());
		canvas.addMouseMotionListener(new MouseListener());
		canvas.addMouseWheelListener(new MouseListener());
		canvas.setPreferredSize(new Dimension(width - 10, height - 10));
		frame.add(canvas);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		frame.setVisible(true);
		try{
			pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			Random random = new Random(0);
			/*for(int i = 0; i < 1000; i ++){
				list.add(new Particle(149600000000d * random.nextDouble(), 149600000000d * random.nextDouble(), 30000 * random.nextDouble(), 0, 5.97219e33 * random.nextDouble(), 80781000d * random.nextDouble()));
			}*/
			list.add(new Particle(0, 0, 0, 0, 1.9891e30, 695500000));
			Particle earth = new Particle(0, 149600000000d, 30000, 0, 5.97219e24, 6378100);
			follow = earth;
			list.add(earth);
			list.add(new Particle(0, 149215600000d, 31023, 0, 7.34767309e22, 1737400));
			Graphics gCanvas = canvas.getGraphics();
			Graphics2D g = (Graphics2D)image.getGraphics();
			while(true){
				g.clearRect(0, 0, width, height);
				for(int i = 0; i < 10; i ++){

				}
				g.setColor(new Color(255, 255, 255));
				for(int i = 0; i < 10000; i ++){
					render(g, i % 10000 == 0);
				}
				int w = 100;
				g.setColor(new Color(0, 255, 0));
				g.drawString(lformat.format(t) + " s", 10, 15);
				g.drawLine(10, height - 10, 10 + w, height - 10);
				g.drawLine(10, height - 20, 10, height - 10);
				g.drawLine(10 + w, height - 20, 10 + w, height - 10);
				FontMetrics fm = g.getFontMetrics();
				String text = getString(w);
				g.drawString(text, 10 + w / 2 - fm.stringWidth(text) / 2, height - 15);
				gCanvas.drawImage(image, 0, 0, null);
				//Thread.sleep(1);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	public static String getString(int pixels){
		double length = pixels / scale;
		if(length < 1000){
			return format.format(length) + " m";
		}
		else if(length < 299792458){
			return format.format(length / 1000) + " km";
		}
		else if(length < 17987547480d){
			return format.format(length / 299792458) + " ls";
		}
		else if(length < 1079252848800d){
			return format.format(length / 17987547480d) + " lm";
		}
		else if(length < 25902068371200d){
			return format.format(length / 1079252848800d) + " lh";
		}
		else if(length < 9454254955488000d){
			return format.format(length / 25902068371200d) + " ld";
		}
		return format.format(length / 9454254955488000d) + " ly";
	}
	public static void render(Graphics2D g, boolean draw){
		for(int i = 0; i < list.size(); i++){
			for(int j = i + 1; j < list.size(); j ++){
				list.get(i).attract(list.get(j));
			}
		}
		for(int i = 0; i < list.size(); i++){
			list.get(i).tick();
		}
		t++;
		if(draw){
			double fx = 0;
			double fy = 0;
			if(follow != null){
				fx = follow.x;
				fy = follow.y;
			}
			for(int i = 0; i < list.size(); i++){
				Particle p = list.get(i);
				int x = (int)((p.x - fx) * scale + xoffset);
				int y = (int)((p.y - fy) * scale + yoffset);
				int radius = (int)(p.radius * scale);
				if(radius < 1)
					radius = 1;
				g.fillOval(x - radius, y - radius, radius + radius, radius + radius);
			}
		}
	}
}
