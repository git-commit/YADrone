package sample;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.shigeodayo.ardrone.ARDrone;
import com.shigeodayo.ardrone.navdata.AttitudeListener;
import com.shigeodayo.ardrone.navdata.BatteryListener;
import com.shigeodayo.ardrone.video.ImageListener;

public class EagleEyeTest extends JFrame{
	private ARDrone drone;
	private JPanel p_south;
	private JPanel p_north;
	private JLabel l_pitch;
	private JLabel l_roll;
	private JLabel l_yaw;
	private JLabel l_altitude;
	private JLabel l_battery;
	private AbstractButton b_reset;
	private AbstractButton b_tmp;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EagleEyeTest() {
		//AR Drone Initialization
		drone = new ARDrone("192.168.1.1");
		System.out.println("connect drone controller");
		drone.connect();
		System.out.println("connect drone navdata");
		drone.connectNav();
		System.out.println("connect drone video");
		//drone.connectVideo();
		System.out.println("start drone");
		drone.start();
		
		/*final VideoPanel pan = new VideoPanel();
		drone.addImageUpdateListener(new ImageListener(){
			@Override
			public void imageUpdated(BufferedImage image) {
				if(pan !=null){
					pan.setImage(image);
					pan.repaint();
				}
			}
		});
		*/
		//Frame initialization
		BorderLayout l = new BorderLayout();
		this.getContentPane().setLayout(l);
		//this.getContentPane().add(pan,BorderLayout.CENTER);
		
		//buttons for starting and landing
		JButton b_start = new JButton("Start");
		JButton b_land = new JButton("Land");
		b_reset = new JButton("Reset");
		b_tmp = new JButton("xyz");
		
		b_start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				drone.takeOff();
			}
		});
		
		b_land.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				drone.landing();
			}
		});
		
		b_reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				drone.reset();
			}
		});
		
		
		p_south = new JPanel();
		getContentPane().add(p_south, BorderLayout.SOUTH);
		p_south.add(b_start);
		p_south.add(b_land);
		p_south.add(b_reset);
		p_south.add(b_tmp);
		
		
		p_north = new JPanel();
		getContentPane().add(p_north, BorderLayout.NORTH);
		l_altitude = new JLabel(" altitude: N/A           ");
		l_pitch = new JLabel(" pitch: N/A           ");
		l_roll = new JLabel(" roll: N/A           ");
		l_yaw = new JLabel(" yaw: N/A            ");
		l_battery = new JLabel("  battery%:  N/A           ");
		p_north.add(l_altitude);
		p_north.add(l_pitch);
		p_north.add(l_roll);
		p_north.add(l_yaw);
		p_north.add(l_battery);
		
		b_tmp.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				
				// x = 10 -> vorwärts, -10 -> rueckwärts
				// y = 10 -> links, -10 rechts
				// z = -10 -> steigt!, 10 -> faellt, wert = geschw.
				
				System.out.println(e.getKeyCode());
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					drone.reset();
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					drone.stop();
				if (e.getKeyCode() == KeyEvent.VK_A)
					drone.goLeft(100);
				if (e.getKeyCode() == KeyEvent.VK_D)
					drone.goRight(100);
				if (e.getKeyCode() == KeyEvent.VK_W)
					drone.forward(100);
				if (e.getKeyCode() == KeyEvent.VK_S)
					drone.backward(100);
				if (e.getKeyCode() == KeyEvent.VK_UP)
					drone.up(100);
				if (e.getKeyCode() == KeyEvent.VK_DOWN)
					drone.down(100);
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					drone.spinLeft(100);
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					drone.spinRight(100);
			}
		});
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//pan.setPreferredSize(new Dimension(640,360));
		this.pack();
		
		drone.addAttitudeUpdateListener(new AttitudeListener() {
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw, int altitude) {
				l_altitude.setText("  altitude: "+altitude);
				l_pitch.setText("  pitch: "+pitch);
				l_roll.setText("  roll: "+roll);
				l_yaw.setText("  yaw: "+yaw);
			}
		});
		
		drone.addBatteryUpdateListener(new BatteryListener() {
			
			@Override
			public void batteryLevelChanged(int percentage) {
				l_battery.setText("  battery%: "+percentage);
			}
		});
}

	public static void main(String[] args) {
		EagleEyeTest ea = null;
		try{
			ea = new EagleEyeTest();
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			ea.emergency();
		}
	}
	
	private void emergency() {
		drone.landing();
	}

	private class VideoPanel extends JPanel{
		private static final long serialVersionUID = -7635284252404123776L;

		/** ardrone video image */
		private BufferedImage image=null;
		
		public void setImage(BufferedImage image){
			this.image=image;
		}
		
		public void paint(Graphics g){
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if(image!=null)
				g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
	}
}
