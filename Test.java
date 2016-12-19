import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Dimensions:
 * 16px/in
 * table - 96 inx50in
 * puck - 3.25in/2.5in ; 58.5 px/45 px
 * mallet - <4 1/16 in ; 72.75 px
 */
//TODO - Offcenter pucks, add level colors, fix collision bugs, pvp, mouse movement options

abstract class Physical {
	int rad, mapMove;
	double maxVel;
	double x, y, velX, velY, centX, centY;
	void wallClimb(String s){
		
	}
	void setPosition(double setXPos, double setYPos){
		x = setXPos;
		y = setYPos;
		centX = x+rad;
		centY = y+rad;
	}
	void setVelocity(double setX, double setY){
		velX = setX;
		velY = setY;
		double velLength = Math.sqrt(velX*velX+velY*velY);
		if(velLength>maxVel){
			velX *=maxVel/velLength;
			velY *=maxVel/velLength;
		}
	}
	void collide(Physical ob){
		System.out.println(this);
	}

	void draw(Graphics2D g2d, double scale){
		
	}
	void move(){
		centX += velX;
		centY += velY;
	}
	void checkPosition(){
		//y = 2x+1136-5rad/sqrt5
		//bottom left
		if((centY+velY-1128+5*rad/Math.sqrt(5))>2*(centX+velX)){
			wallClimb("bot left");
			
		}
		
		//bottom right
		// y = -2x+2736-5rad/sqrt5
		else if((centY+velY-2728+5*rad/Math.sqrt(5))>-2*(centX+velX)){
			//System.out.println(centX + " " + centY + " " + x + " " + y + " " + -2*(centX+a) + " " + (centY+b-2000));
			wallClimb("bot right");
		}

		//top left
		//y = -2x+400+5rad/sqrt5
		else if((centY+velY-408-5*rad/Math.sqrt(5))<-2*(centX+velX)){
			if(mapMove!=1){
				wallClimb("top left");
			}
		}
		
		//top right
		//y = 2x-1200+2rad
		else if((centY+velY+1192-5*rad/Math.sqrt(5))<2*(centX+velX)){
			if(mapMove!=1){
				wallClimb("top right");
			}
		}
		if(centX-rad+velX<0){
			wallClimb("left");
		}
		else if(centX+rad+velX>800){
			wallClimb("right");
			
		}
		if (centY+rad+velY>1536){
			wallClimb("bot");
			
		}
		else if(centY-rad+velY<0){
			wallClimb("top");

		}

		if(Test.pvp&&(Test.gameState.equals("levelStart")||Test.gameState.equals("pointStart"))){
			if(mapMove==0){
				double centDist = Math.sqrt(Math.pow(centX+velX-400, 2)+Math.pow(centY+velY-300, 2));
				if(centDist>(100-rad)){
					wallClimb("top circle");
					System.out.println("top circle");
				}
			}
			else if(mapMove==1){
				double centDist = Math.sqrt(Math.pow(centX+velX-400, 2)+Math.pow(centY+velY-1236, 2));
				if(centDist>(100-rad)){
					wallClimb("bot circle");
					System.out.println("bot circle");
				}
			}
		}

		else if (mapMove==0){
			if(centY+rad+velY>766){
				velY = 768-rad-centY;
			}
		}
		else if (mapMove==1){
			if(centY-rad+velY<770){
				velY = 768+rad-centY;
			}
		}
		move();

		x = centX-rad;
		y = centY-rad;
	}
	
}

class Puck extends Physical{
	int noCollide = 10;
	Puck(int a, int b, double m){
		x = a;
		y = b;
		rad = 26;
		centX = x+rad;
		centY = y+rad;
		mapMove = -1;
		maxVel = m;
	}
	@Override
	void wallClimb(String s){
		if (s.equals("bot left")){
			double t = (centY-1128+5*rad/Math.sqrt(5)-2*centX)/(2*velX-velY);
			if(t>1){
				System.out.println("bot left: t>= 1 " + t + " (x,y) (" + centX + ", " + centY +") (velX, velY) "+ velX + ", " + velY + ")" + Test.currentTime);
			}
			centX += velX*t;
			centY += velY*t;
			collide(new Mallet(centX-50-32, centY+25-32, -1, 1));
		}
		else if (s.equals("bot right")){
			double t = (centY-2728+5*rad/Math.sqrt(5)+2*centX)/(2*velX+velY)*-1;
			if(t>1){
				System.out.println("bot left: t>= 1 " + t + " (x,y) (" + centX + ", " + centY +") (velX, velY) "+ velX + ", " + velY + ")" + Test.currentTime);
			}
			centX += velX*t;
			centY += velY*t;
			collide(new Mallet(centX+50-32, centY+25-32, -1, 1));
		}
		else if (s.equals("top left")){
			double t = (centY-408-5*rad/Math.sqrt(5)+2*centX)/(2*velX+velY)*-1;
			if(t>1){
				System.out.println("bot left: t>= 1 " + t + " (x,y) (" + centX + ", " + centY +") (velX, velY) "+ velX + ", " + velY + ")" + Test.currentTime);
			}
			centX += velX*t;
			centY += velY*t;
			collide(new Mallet(centX-50-32, centY-25-32, -1, 1));
		}
		else if (s.equals("top right")){
			double t = (centY+1192-5*rad/Math.sqrt(5)-2*centX)/(2*velX-velY);
			if(t>1){
				System.out.println("bot left: t>= 1 " + t + " (x,y) (" + centX + ", " + centY +") (velX, velY) "+ velX + ", " + velY + ")" + Test.currentTime);
			}
			centX += velX*t;
			centY += velY*t;
			collide(new Mallet(centX+50-32, centY-25-32, -1, 1));
		}
		else if (s.equals("right")){
			centY += (800-centX-rad)/velX * velY;
			centX = 800-rad;
			collide(new Mallet(centX+rad, centY-32, -1, 1));
			//System.out.println(s);
		}
		else if (s.equals("left")){
			centY += (-centX+rad)/velX * velY;
			centX = rad;
			collide(new Mallet(centX-rad-64, centY-32, -1, 1));
		}
		else if (s.equals("bot")){
			/*
			centX += (1536-centY-rad)/velY * velX;
			centY = 1536-rad;
			collide(new Mallet(centX-32, centY+rad, -1));
			*/
			if (centY>=1562){
				
				if(Test.gameState.equals("On")){
					System.out.println((Test.puck1.centY+" "+Test.puck1.centX));
					System.out.println((Test.puck1.velY+" "+Test.puck1.velX +" pLoss" + " " +((double)(Test.currentTime/100))/10.0));
					if(Test.pLives>1){
						for (Physical object : Test.objects){
							object.setVelocity(0,0);
							if (object.mapMove==1){
								Test.player.setPosition(368, 1236-object.rad);
							}
							else if (object.mapMove==0){
								Test.computer.setPosition(368, 302-object.rad);
							}
							else if(object instanceof Puck){
								if(!Test.pvp){
									Test.puck1.setPosition(374, 1000);
								}
								else{
									Test.puck1.setPosition(373, 742);
								}
							}
						}
						Test.gameState = "pointStart";
						
						if(Test.pvp){
							
							try{
								Test.levelLoad(null);
							}catch(Exception ex){
								System.out.println("loading failed.");
							}
						}

						
					}
				}
				Test.pLives--;

			}
		}
		else if (s.equals("top")){
			/*
			centX += (-centY+rad)/velY * velX;
			centY = rad;
			collide(new Mallet(centX-32, centY-rad-64, -1));
			*/
			if (centY<=-26){
	
				if(Test.gameState.equals("On")){
					System.out.println((Test.puck1.centY+" "+Test.puck1.centX));
					System.out.println((Test.puck1.velY+" "+Test.puck1.velX + " cLoss" + " " +((double)(Test.currentTime/100))/10.0));
					if(!Test.pvp){
						for (Physical object : Test.objects){

							object.setVelocity(0,0);
							if (object.mapMove==1){
								object.setPosition(368, 1236-object.rad);
							}
							else if (object.mapMove==0){
								object.setPosition(368, 302-object.rad);
							}
							else if(object instanceof Puck){
								if(!Test.pvp){
									object.setPosition(374, 1000);
								}
								else{
									object.setPosition(373, 742);
								}
							}
						}
					}
					if(Test.cLives>1){
						
						if(!Test.pvp){
							Test.gameState = "pointStart";
						}
						else{
							try{
								Test.levelLoad(null);
							}catch(Exception ex){
								System.out.println("loading failed.");
							}
						}
					}
				}
				Test.cLives--;
				
			}
		}
	}

	void loseEnergy(double loss){
		velX *= (1-loss);
		velY *= (1-loss);
	}
	@Override
	void move(){
		centX += velX;
		centY += velY;
		loseEnergy(Test.friction);
	}
	@Override
	void collide(Physical ob){
		double dx = ob.centX-centX, dy = ob.centY-centY;
		double distance = Math.sqrt(dx*dx+dy*dy);
		double minDist = rad+ob.rad;
		if(distance<= minDist){
			double relVX = velX-ob.velX, relVY = velY-ob.velY;
			double relV = Math.sqrt(relVX*relVX+relVY*relVY);
			if(relV==0){
				System.out.println("Relv = 0. Impact velocity: (" + ob.velX + ", " + ob.velY + ") Puck position: ("  + centX + ", " + centY + ") Ob position: (" + ob.centX + ", " + ob.centY + ")");
			}
			double dot = ((relVX*dx+relVY*dy)/relV)/(distance);
			if (dot>1){
				dot = 1;
			}
			else if(dot<0){
				dot =0.000000000000001;
			}
			assert dot!=0 : "dot";
			double theta = Math.acos(dot);
			assert theta>=0 : "theta " + theta + " dot " + dot + " relV " + relV;
			if (theta>=90){
				return;
			}
			double projVX =  relV*Math.cos(theta)/distance*dx;
			double projVY =  relV*Math.cos(theta)/distance*dy;
			//System.out.println(projVX);
			assert projVX!=0||projVY!=0 : "proj";
			double newVX = relVX-2*projVX;
			double newVY = relVY-2*projVY;
			//System.out.println(newVX + " " + newVY);
			assert newVX!=0||newVY!=0 : "new";
			setVelocity(newVX+ob.velX, newVY+ob.velY);
			if(ob instanceof Mallet){
				if(ob.mapMove==-1){
					loseEnergy(Test.energyLoss);
				}
			}
			
		}
	}
	@Override
	void draw(Graphics2D g2d, double scale){
        g2d.setColor(new Color(193, 10, 3));
        g2d.fillOval((int)x, (int)y, (int)(52*scale), (int)(52*scale));
        g2d.setColor(Color.black);
        g2d.drawOval((int)x, (int)y, (int)(52*scale), (int)(52*scale));
	}
	
	
}
class Mallet extends Physical{
	final int XTRAVEL = 4, YTRAVEL = 9;
	double targetX=-100, targetY;
	Mallet(double a, double b, int c, double s){
		x = a;
		y = b;
		rad = 32;
		centX = x+rad;
		centY = y+rad;
		maxVel = s;
		mapMove = c;
		//System.out.println(x + " " + y);
	}
	@Override
	void wallClimb(String s){
		if(velX==0&&velY==0){
			return;
		}
		double[] target = {400, 800};
		//System.out.println((Test.startTime-System.currentTimeMillis()) + " " + s);
		int xTarget = (int)(velX+centX);
		int yTarget = (int)(velY+centY);
		//bottom left
		//y = 2x+1136-2rad
		if(s.equals("bot left")){
			target = getPlace(10000, 39999, xTarget, yTarget);
		}
		//bottom right
		// y = -2x+2736-2rad
		else if(s.equals("bot right")){
			target = getPlace(70000, 19999, xTarget, yTarget);
		}
		//top left
		//y = -2x+400+2rad
		else if(s.equals("top left")){
			target = getPlace(30000, 59999, xTarget, yTarget);
		}

		//top right
		//y = 2x-1200+2rad
		else if(s.equals("top right")){
			target = getPlace(50000, 79999, xTarget, yTarget);
			//System.out.println(x, xTarget, yTarget);
			//System.out.println(target[0] + " " + target[1], xTarget, yTarget);
		}
		else if(s.equals("left")){
			if(mapMove==1){
				target = getPlace(20000, 39999, xTarget, yTarget);
			}
			else if(mapMove==0){
				target = getPlace(30000, 49999, xTarget, yTarget);
			}
			else{
				target = getPlace(20000, 49999, xTarget, yTarget);
			}
		}
		else if(s.equals("right")){
			if(mapMove==1){
				target = getPlace(70000, 9999, xTarget, yTarget);
			}
			else if(mapMove==0){
				target = getPlace(60000, 79999, xTarget, yTarget);
			}
			else{
				target = getPlace(60000, 9999, xTarget, yTarget);
			}
		}
		else if(s.equals("bot")){
			target = getPlace(0, 29999, xTarget, yTarget);
		}
		else if(s.equals("top")){
			target = getPlace(40000, 69999, xTarget, yTarget);
			//System.out.println(x, xTarget, yTarget);
		}
		else if(s.equals("top circle")){
			Point approachTarget = new Point(xTarget, yTarget);
			int start = 0, end = 9999;
			double[] low, high;
			double lowDist=0, highDist=0;
			int range = Math.abs(end-start);
			while(range>0){
				low = calcPoint((start+range/2)*-1);
				high = calcPoint((start+range/2+1)*-1);
				lowDist = Math.sqrt(Math.pow((low[0]-approachTarget.x),2)+Math.pow((low[1]-approachTarget.y),2));
				highDist = Math.sqrt(Math.pow((high[0]-approachTarget.x),2)+Math.pow((high[1]-approachTarget.y),2));
				if(lowDist<highDist){
					end = start+range/2;
				}
				else if(highDist<lowDist){
					start = start+range/2+1;
				}
				else{
					assert 1==0 : "Equal distance in mallet calcs? " + lowDist + " " + highDist + " " + low + " " + high + " " + start + " " + end + " " + approachTarget.x + " " +approachTarget.y; 
				}
				range = Math.abs(end-start);
			}
			target = calcPoint((start)*-1);
		}
		else if(s.equals("bot circle")){
			Point approachTarget = new Point(xTarget, yTarget);
			int start = 10000, end = 19999;
			double[] low, high;
			double lowDist=0, highDist=0;
			int range = Math.abs(end-start);
			while(range>0){
				low = calcPoint((start+range/2)*-1);
				high = calcPoint((start+range/2+1)*-1);
				lowDist = Math.sqrt(Math.pow((low[0]-approachTarget.x),2)+Math.pow((low[1]-approachTarget.y),2));
				highDist = Math.sqrt(Math.pow((high[0]-approachTarget.x),2)+Math.pow((high[1]-approachTarget.y),2));
				if(lowDist<highDist){
					end = start+range/2;
				}
				else if(highDist<lowDist){
					start = start+range/2+1;
				}
				else{
					assert 1==0 : "Equal distance in mallet calcs? " + lowDist + " " + highDist + " " + low + " " + high + " " + start + " " + end + " " + approachTarget.x + " " +approachTarget.y; 
				}
				range = Math.abs(end-start);
			}
			target = calcPoint((start)*-1);
		}
		double xMove = (target[0]-centX);
		double yMove = (target[1]-centY);
		setVelocity(xMove, yMove);
	}
	public double[] getPlace(int s, int e, int tx, int ty){
		Point approachTarget = new Point(tx, ty);
		int start = s, end = e;
		double[] low, high;
		double lowDist=0, highDist=0;
		while(end<=start){
			end+=80000;
		}
		int range = Math.abs(end-start);
		while(range>0){
			
			low = calcPoint(start+range/2);
			high = calcPoint(start+range/2+1);
			lowDist = Math.sqrt(Math.pow((low[0]-approachTarget.x),2)+Math.pow((low[1]-approachTarget.y),2));
			highDist = Math.sqrt(Math.pow((high[0]-approachTarget.x),2)+Math.pow((high[1]-approachTarget.y),2));
			if(lowDist<highDist){
				end = start+range/2;
			}
			else if(highDist<lowDist){
				start = start+range/2+1;
			}
			else{
				assert 1==0 : "Equal distance in mallet calcs? " + lowDist + " " + highDist + " " + low + " " + high + " " + start + " " + end + " " + approachTarget.x + " " +approachTarget.y; 
			}
			range = Math.abs(end-start);
		}
		return calcPoint(start);
	}
	public double[] calcPoint(int t){
		if(t>=0){
			t%=80000;
			assert (t>=0&&t<80000) : "Error: calcPoint(i) out of bounds - " + t;
			double[] octagonX = {800-rad, (-1200+(5/Math.sqrt(5)-1)*rad)/-2, (rad-400-Math.sqrt(5)*rad)/-2, rad, rad, (rad-400-Math.sqrt(5)*rad)/-2, (-1200+(5/Math.sqrt(5)-1)*rad)/-2, 800-rad};
			double[] octagonY = {-2*(800-rad)+2736-5*rad/Math.sqrt(5), 1536-rad, 1536-rad , -2*(800-rad)+2736-5*rad/Math.sqrt(5), -2*rad+400+Math.sqrt(5)*rad, rad, rad, -2*rad+400+Math.sqrt(5)*rad};
			double xPoint = 0, yPoint = 0;
			int l = t-t/10000*10000;
			// y = -2x+2736-5rad/sqrt5
			if(t<10000){
				xPoint = octagonX[0]+(octagonX[1]-octagonX[0])/10000*l;
				yPoint = octagonY[0]+(octagonY[1]-octagonY[0])/10000*l; 
			}
			else if(t<20000){
				xPoint = octagonX[1]+(octagonX[2]-octagonX[1])/10000*l;
				yPoint = octagonY[1]+(octagonY[2]-octagonY[1])/10000*l; 
			}
			else if(t<30000){
				xPoint = octagonX[2]+(octagonX[3]-octagonX[2])/10000*l;
				yPoint = octagonY[2]+(octagonY[3]-octagonY[2])/10000*l; 
			}
			else if(t<40000){
				xPoint = octagonX[3]+(octagonX[4]-octagonX[3])/10000*l;
				yPoint = octagonY[3]+(octagonY[4]-octagonY[3])/10000*l; 
			}
			//y = -2x+400+5rad/sqrt5
			else if(t<50000){
				xPoint = octagonX[4]+(octagonX[5]-octagonX[4])/10000*l;
				yPoint = octagonY[4]+(octagonY[5]-octagonY[4])/10000*l; 
			}
			else if(t<60000){
				xPoint = octagonX[5]+(octagonX[6]-octagonX[5])/10000*l;
				yPoint = octagonY[5]+(octagonY[6]-octagonY[5])/10000*l; 
			}
			else if(t<70000){
				xPoint = octagonX[6]+(octagonX[7]-octagonX[6])/10000*l;
				yPoint = octagonY[6]+(octagonY[7]-octagonY[6])/10000*l; 
			}
			else{
				xPoint = octagonX[7]+(octagonX[0]-octagonX[7])/10000*l;
				yPoint = octagonY[7]+(octagonY[0]-octagonY[7])/10000*l; 
			}
			double[] netPoint = {xPoint, yPoint};
			return netPoint;
		}
		else{
			int l;
			if(t<=-10000){
				l = (-1*t)%10000;
			}
			else{
				l = -1*t;
			}
			double angle = Math.PI/5000*l;
			double testX = (100-rad)*Math.cos(angle);
			double testY = (100-rad)*Math.sin(angle);
			double[] netPoint = {testX+400, testY+300};
			if(t<=-10000){
				netPoint[1] = testY+1236;
			}
			return netPoint;
		}
	}
	void draw(Graphics2D g2d, double scale){
		g2d.setColor(Test.colors[Test.level][3]);
		if(Test.pvp){
			g2d.setColor(mapMove==1?new Color(173, 0, 207):new Color(255, 131, 0));
		}
		g2d.fillOval((int)x, (int)y, (int)(64*scale), (int)(64*scale));
		g2d.setColor(Test.colors[Test.level][2]);
		g2d.drawOval((int)x, (int)y, (int)(64*scale), (int)(64*scale));
        g2d.drawOval((int)x+18, (int)y+18, (int)(29*scale), (int)(29*scale));
        
        //double[] mover = getPlace(0, 80000);
        
        //g2d.drawOval((int)mover[0]-2, (int)mover[1]-2, 4, 4);
	}
	
}
class Reset extends AbstractAction{
	PrintWriter printer;
	Pause pause;
	Reset(PrintWriter print, Pause p){
		printer = print;
		pause = p;
	}
	public void actionPerformed(ActionEvent e){
		Test.skipTimer = 0;
		for (Physical object : Test.objects){
			object.setVelocity(0,0);
			if (object.mapMove==1){
				object.setPosition(368, 1236-object.rad);
			}
			else if (object.mapMove==0){
				object.setPosition(368, 302-object.rad);
			}
			else if(object instanceof Puck){
				object.setPosition(374, 742);
			}
		}
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "");
		Test.log(printer, "                             RESET                          ");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.startTime = System.currentTimeMillis();
		pause.pauseTime = 0;
	}
}



class Pause extends AbstractAction{
	long pauseTime = 0;
	long pauseStart;
	Pause(){
		;
	}
	public void actionPerformed(ActionEvent e){
		if(Test.gameState.equals("On")){
			pauseStart = System.currentTimeMillis();
			System.out.println("Paused: " + (System.currentTimeMillis()-Test.startTime) + " Pausetime: " + pauseTime);
			Test.gameState = "Paused";
		}
		else if(Test.gameState.equals("Paused")){
			pauseTime += System.currentTimeMillis()-pauseStart;
			System.out.println("Unpaused: " + (System.currentTimeMillis()-Test.startTime));
			Test.gameState = "On";
		}
		
	}
}

class PanelButton{
	private int x, y, width, height, textX, textY;
	private String text;
	public int action;
	public PanelButton(int x, int y, int w, int h, int a, String t, int tx, int ty){
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		text = t;
		action = a;
		textX = tx;
		textY = ty;
	}
	public void draw(Graphics2D g2d){
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouse, Test.game);
		g2d.setColor(new Color(0,0,0, 170));
		if(action == 3){
			g2d.setColor(new Color(110, 110, 110));
		}
		else if(action==6){
			g2d.setColor(new Color(0,0,0, 0));
		}
		if((mouse.x>x&&mouse.x<x+width)&&(mouse.y>y&&mouse.y<y+height)){
			if(action!=3){
				g2d.setColor(new Color(0,0,0, 120));
			}
			else{
				g2d.setColor(new Color(150, 150, 150));
			}
			Test.currentButton = this;
			if(action==6){
				return;
			}
		}
		if(action!=3&&action!=4){
			g2d.fillRoundRect(x, y, width, height, 130, 100);
			g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 58));
		}
		else{
			g2d.fillRoundRect(x, y, width, height, 120, 90);
			g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 52));
		}
		g2d.setColor(Color.white);
		if(action==3){
			g2d.setColor(Color.black);
		}
		g2d.drawString(text, textX, textY);
	}

}

class Goto extends AbstractAction{
	boolean gotoBox = false;
	Scanner timeEntry = new Scanner(System.in);
	Scanner logScan;
	static int t;

	public void actionPerformed(ActionEvent e){
		if(Test.gameState.equals("On")){
			Test.pPress.pauseStart = System.currentTimeMillis();
			Test.gameState.equals("Paused");
			t = timeEntry.nextInt();
			timeTravel();
		}
		else if(Test.gameState.equals("Paused")){
			t = timeEntry.nextInt();
			timeTravel();
		}
	}
	public void timeTravel(){
		try{
			logScan = new Scanner(new FileReader("log.txt"));
		}
		catch(IOException e){
			System.out.println("IOException");
		}
		while(logScan.hasNext()){
			String check = logScan.next();
			if(check.charAt(0)!='['){
				continue;
			}
			int checkInt = Integer.parseInt(check.substring(1, check.length()-1));
			System.out.println(checkInt + " " + t);
			if(checkInt+11>=t){
				for (Physical object : Test.objects){
					logScan.next();
					//System.out.println(logScan.next());
					object.x = Double.parseDouble(logScan.next());
					object.centX = object.x+object.rad;
					object.y = Double.parseDouble(logScan.next());
					object.centY = object.y+object.rad;
					if (object instanceof Puck){
						object.velX = Double.parseDouble(logScan.next());
						object.velY = Double.parseDouble(logScan.next());
					}
					else{
						logScan.next();
						logScan.next();
					}
				}
				break;
			}
		}
	}
}

class Arrow extends AbstractAction{
	Goto gotoT;
	int mod;
	public Arrow(int c, Goto g){
		gotoT = g;
		mod = c;
	}
	public void actionPerformed(ActionEvent e){
		if(Test.gameState.equals("Paused")){
			Goto.t += mod;
			gotoT.timeTravel();
		}
	}
}

class MoveAction extends AbstractAction{
	static int[] keys = new int[8];

	int index;
	static int ddx, ddy;
	boolean isOn;
	//Mallet player;
	public MoveAction(Mallet p, int i, boolean on){
		index = i;
		isOn = on;
		//player = p;
		
	}

	public void actionPerformed(ActionEvent e){
		keys[index] = isOn?1:0;
	}
	static void newVelocity(){
		if (Test.pvp){
			ddx = 0;
			ddy = 0;
			if (keys[0]==1){
				ddx += 10;
			}
			if (keys[1]==1){
				ddx -= 10;
			}
			if (keys[2]==1){
				ddy += 10;
			}
			if(keys[3]==1){
				ddy -= 10;
			}
			
			if (ddx!=0&&ddy!=0){
				ddx *=7;
				ddx /= 10;
				ddy *=7;
				ddy /= 10;
			}
			
			Test.player.setVelocity(ddx*10, ddy*10);
			ddx = 0;
			ddy = 0;
			if (keys[4]==1){
				ddx += 10;
			}
			if (keys[5]==1){
				ddx -= 10;
			}
			if (keys[6]==1){
				ddy += 10;
			}
			if(keys[7]==1){
				ddy -= 10;
			}
			
			if (ddx!=0&&ddy!=0){
				ddx *=7;
				ddx /= 10;
				ddy *=7;
				ddy /= 10;
			}
			
			Test.computer.setVelocity(ddx*10, ddy*10);
		
		}
		else{
			Point mouse = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(mouse, Test.game);
			//System.out.println(" " + mouse);
			double vecX = mouse.x-Test.player.centX, vecY = mouse.y-Test.player.centY;
//			double length = Math.sqrt(vecX*vecX+vecY*vecY);
////			if (length>20){
////				vecX *= 20/length;
////				vecY *= 20/length;
////			}
			Test.player.setVelocity(vecX, vecY);
	    	//System.out.println((mouse.x-Test.player.x) + " " + (mouse.y-Test.player.y));
		}
	}
}

class KeyBind{
	static void bind(String s, JComponent c, Action a){
		c.getInputMap().put(KeyStroke.getKeyStroke(s), s+ " action");
		c.getActionMap().put(s+ " action", a);
	}
	static void bind(char s, JComponent c, Action a){
		c.getInputMap().put(KeyStroke.getKeyStroke(s), s+ " action");
		c.getActionMap().put(s+ " action", a);
	}
	static void bind(int s, JComponent c, Action a, String n){
		c.getInputMap().put(KeyStroke.getKeyStroke(s, 0), n+ " action");
		c.getActionMap().put(n+ " action", a);
	}
}


public class Test extends JPanel implements MouseListener{
	static Color[][] colors = {
			{new Color(224, 224, 226), Color.blue, Color.black, Color.green}, //white, blue, black, green
			{Color.yellow, new Color(15, 15, 15), Color.black, new Color(10, 96, 255)}, //(yellow-green)
			{new Color(246, 151, 96), new Color(110, 110, 110), Color.black, new Color(96, 244, 247)}, // (red orange)
			{new Color(209, 255, 209), new Color(84, 155, 255), Color.black, Color.white}, //(yellow gray)/ (blue black)
			{new Color(130, 47, 0), new Color(0, 68, 14), Color.black, new Color(242, 216, 142)}, //green brown black cream
			{new Color(1, 188, 126), new Color(86, 0 ,147), Color.black, new Color(0, 59, 255)}, //light teal, purple, black, blue
			{new Color(15, 15, 15), new Color(190, 190, 190), Color.white, new Color(80, 80, 80)} //black, light gray, white, dark gray
	};
	static double[] maxVels = {3, 4, 6, 9, 14, 20, 30};
	static int[] scores = new int[15];
	static int score = 0;
	static String[] names = new String[15];
	static double friction = .008;
	static double energyLoss = .05;
	static PanelButton currentButton = null;
	static Test game = new Test();
    static long startTime = System.currentTimeMillis(), currentTime;
    static int cLives = 3, pLives = 5;
    static int level = 0;
    static int skipTimer = 0;
    static int victory = 0;
    static PanelButton[] menu = new PanelButton[4];
    static Puck puck1 = new Puck(374, 742, 1000);
    static Mallet player = new Mallet(368, 1236-32, 1, 10), computer = new Mallet(368, 300-32, 0, 30);
    static ArrayList<Physical> objects = new ArrayList<Physical>();
    static Pause pPress = new Pause();
    static int fadeIndex = 0, circIndex = 0;
    static boolean pvp = false;
    static int pPuckDir = 0, cPuckDir = 0;
    static double bonus = 0;
    static double startTimer= 0;
    static double pXTarget, pYTarget;
    static double cXTarget, cYTarget;
    static int startMode = 0;
    static boolean toPrint = true;
    static PanelButton pointStarter = new PanelButton(365, 1201, 70, 70, 6, "", 0, 0);
    static PanelButton back = new PanelButton(250, 1400, 300, 100, 4, "Back", 340, 1467);
    static String gameState = "Menu"; //Menu, Controls, Scores, On, Off, levelStart
    static String oldState="";
    static int cCenterX = 400;
    static int cCenterY = 300;
    static int pCenterY = 1236;
    static int timer = 0;
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseClicked(MouseEvent e){
    	if(pvp&&gameState.equals("pointStart")){
    		try{
				levelLoad(null);
			}catch(Exception ex){
				System.out.println("loading failed.");
			}
    	}
    	else if(currentButton!=null){
    		switch(currentButton.action){
    			case 0: 
    					friction = .0175;
    					energyLoss = .23;
    					try{
    						levelLoad(null);
    					}catch(Exception ex){
    						System.out.println("loading failed.");
    					}
    					puck1.setVelocity(Math.random()*40-20, Math.random()*20);
    					break;
    			case 1: pvp = true;
    					pLives = 5;
    					cLives = 5;
    					friction = .015;
    					energyLoss = .1;
    					player = new Mallet(368, 1204, 1, 15);
    					computer = new Mallet(368, 268, 0, 15);
    					for (Physical object : Test.objects){
    						object.setVelocity(0,0);
    						if (object.mapMove==1){
    							object.setPosition(368, 1236-object.rad);
    						}
    						else if (object.mapMove==0){
    							object.setPosition(368, 302-object.rad);
    						}
    						else if(object instanceof Puck){
    							if(!pvp){
    								puck1.setPosition(374, 1000);
    							}
    							else{
    								puck1.setPosition(373, 742);
    							}
    						}
    					}
    					gameState = "pointStart";
    					break;
    			case 2: gameState = "Controls";
    					break;
    			case 3: break;
    			case 4: gameState = "Menu";
    					break;
    			case 5: gameState = "Scores";
    					break;
    			case 6: gameState = "On";
    					startTime = System.currentTimeMillis();
    					pPress.pauseTime = 0;
    					puck1.setVelocity(0, 0);
    					break;
    			
    		}
    	}
    	
    }
    public void fadeIn(int i, Graphics2D g2d, String s, Font font, int a, int b, int c, int x, int y) throws InterruptedException{
    	g2d.setColor(new Color(a, b, c, i));
    	g2d.setFont(font);
    		g2d.drawString(s, x, y);
    	
    }
    @Override
    public void paintComponent(Graphics g){
    	//System.out.println("b4");
    	super.paintComponent(g);
        //System.out.println("after");
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(4));
        
        //Game board
        g2d.setColor(Test.colors[Test.level][0]);
        g2d.fillRect(0, 0, 800, 1536);
        g2d.setColor(Test.colors[Test.level][2]);
        g2d.drawLine(0, 768, 800, 768);
        g2d.drawOval(250, 618, 300, 300);
        //g2d.drawLine(400,0,400,1536);
        
        //Pieces
        player.draw(g2d, 1);
        computer.draw(g2d, 1);
        puck1.draw(g2d, 1);
        
        //Boundaries
        g2d.setColor(Test.colors[Test.level][1]);
        
        g2d.fillPolygon(new int[]{200, 0 ,0}, new int[]{1536, 1536, 1136},3);
        g2d.fillPolygon(new int[]{600, 800 , 800}, new int[]{1536, 1536, 1136},3);
        g2d.fillPolygon(new int[]{200, 0 ,0}, new int[]{0, 0, 400},3);
        g2d.fillPolygon(new int[]{600, 800, 800}, new int[]{0, 0, 400},3);
        
        g2d.setColor(Test.colors[Test.level][2]);
        g2d.drawLine(200, 1536, 0, 1136);
        g2d.drawLine(600, 1536, 800, 1136);
        g2d.drawLine(200, 0, 0, 400);
        g2d.drawLine(600, 0, 800, 400);
        
        //Time
        g.setFont(new Font("Arial", Font.PLAIN, 30)); 
        g2d.setColor(Color.black);
        //g2d.drawString("t: " +((double)(currentTime/100))/10.0, 10, 450);
        
        if(gameState.equals("On")){
        	if(!pvp){
        		for(int i = 0; i<pLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i<cLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        	}
        	else{
        		for(int i = 0; i <Math.min(pLives-1,3); i++){
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<pLives-4; i++){
        			new Puck(10+50*i, 1430, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i <Math.min(cLives-1,3); i++){
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<cLives-4; i++){
        			new Puck(10+50*i, 65, 1).draw(g2d, .8);
        		}
        	}
        	if(!pvp){
        		g2d.setColor(Test.colors[Test.level][0]);
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 50));
        		g2d.drawString("Bonus", 658, 1465);
        		g2d.drawString(""+((int)(bonus/10))*10, 668, 1520);
        		g2d.drawString("Score", 658, 55);
        		g2d.drawString(""+Test.score, 771-(""+Test.score).length()*24, 110);
        	}
        }
        else if(Test.gameState.equals("Paused")){
        	if(!pvp){
        		for(int i = 0; i<pLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i<cLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        	}
        	else{
        		for(int i = 0; i <Math.min(pLives-1,3); i++){
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<pLives-4; i++){
        			new Puck(10+50*i, 1430, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i <Math.min(cLives-1,3); i++){
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<cLives-4; i++){
        			new Puck(10+50*i, 65, 1).draw(g2d, .8);
        		}
        	}
        	if(!pvp){
        		g2d.setColor(Test.colors[Test.level][0]);
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 50));
        		g2d.drawString("Bonus", 658, 1465);
        		g2d.drawString(""+((int)(bonus/10))*10, 668, 1520);
        		g2d.drawString("Score", 658, 55);
        		g2d.drawString(""+Test.score, 771-(""+Test.score).length()*24, 110);
        	}
        	g2d.setColor(new Color(0, 0, 0, 100));
        	g2d.fillRect(0, 0, 3000, 3000);
        	g2d.setColor(Color.black);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 150));
        	g2d.drawString("PAUSED", 145, 520);
        	
        }
        else if(Test.gameState.equals("levelStart")){
        	if(!pvp){
        		for(int i = 0; i<pLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i<cLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        	}
        	else{
        		for(int i = 0; i <Math.min(pLives-1,3); i++){
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<pLives-4; i++){
        			new Puck(10+50*i, 1430, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i <Math.min(cLives-1,3); i++){
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<cLives-4; i++){
        			new Puck(10+50*i, 65, 1).draw(g2d, .8);
        		}
        	}
        	if(!pvp){
        		if(startMode==0){
        			startTimer+= 2;
        		}
        		g2d.setColor(new Color(Test.colors[Test.level][2].getRed(), Test.colors[Test.level][2].getGreen(), 
        				Test.colors[Test.level][2].getBlue(), startTimer>255?255:(int)startTimer));
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 200));
        		g2d.drawString("Level " + (level+1), 100, 560);
        		g2d.setColor(Test.colors[Test.level][0]);
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 50));
        		g2d.drawString("Bonus", 658, 1465);
        		g2d.drawString(""+((int)(bonus/10))*10, 668, 1520);
        		g2d.drawString("Score", 658, 55);
        		g2d.drawString(""+Test.score, 771-(""+Test.score).length()*24, 110);
        	}
        	else{
        		g2d.drawOval(300, 200, 200, 200);
        		g2d.drawOval(300, 1136, 200, 200);
        		startTimer+= 10;
        		g2d.setColor(new Color(0,0,0,255));
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 200));
        		g2d.drawString("" + (3-(int)(startTimer/1000)), 340, 575);
        	}
        }
        else if (Test.gameState.equals("pointStart")){
        	if(!pvp){
        		for(int i = 0; i<pLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i<cLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        	}
        	else{
        		for(int i = 0; i <Math.min(pLives-1,3); i++){
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<pLives-4; i++){
        			new Puck(10+50*i, 1430, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i <Math.min(cLives-1,3); i++){
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<cLives-4; i++){
        			new Puck(10+50*i, 65, 1).draw(g2d, .8);
        		}
        	}
        	currentButton = null;
        	pointStarter.draw(g2d);
        	if(!pvp){
        		g2d.setColor(Test.colors[Test.level][0]);
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 50));
        		g2d.drawString("Bonus", 658, 1465);
        		g2d.drawString(""+((int)(bonus/10))*10, 668, 1520);
        		g2d.drawString("Score", 658, 55);
        		g2d.drawString(""+Test.score, 771-(""+Test.score).length()*24, 110);
        	}
        	else{
        		g2d.setColor(Test.colors[Test.level][2]);
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 100));
        		g2d.drawString("Click to start", 105, 545);
        		g2d.drawOval(300, 200, 200, 200);
        		g2d.drawOval(300, 1136, 200, 200);
        	}
        }
        else if(gameState.equals("Off")){
        	if(!pvp){
        		for(int i = 0; i<pLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i<cLives-1; i++){
        			//System.out.println("go");
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        	}
        	else{
        		for(int i = 0; i <Math.min(pLives-1,3); i++){
        			new Puck(10+50*i, 1480, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<pLives-4; i++){
        			new Puck(10+50*i, 1430, 1).draw(g2d, .8);
        		}
        		for(int i = 0; i <Math.min(cLives-1,3); i++){
        			new Puck(10+50*i, 15, 1).draw(g2d, .8);
        		}
        		for (int i = 0; i<cLives-4; i++){
        			new Puck(10+50*i, 65, 1).draw(g2d, .8);
        		}
        	}
        	if(!pvp){
        		g2d.setColor(Test.colors[Test.level][0]);
        		g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 50));
        		g2d.drawString("Bonus", 658, 1465);
        		g2d.drawString(""+((int)(bonus/10))*10, 668, 1520);
        		g2d.drawString("Score", 658, 55);
        		g2d.drawString(""+Test.score, 771-(""+Test.score).length()*24, 110);
        	}
        	g2d.setColor(Color.black);
        	circIndex++;
        	if(Math.random()>.5){
        		circIndex++;
        	}
        	g2d.fillOval(400-5*circIndex, 768-5*circIndex, 10*circIndex, 10*circIndex);
        	if(circIndex>70){
        		try{
        			switch(victory){
        			case 1: fadeIn(fadeIndex, g2d, "YOU DIED", new Font("Eras Light ITC", Font.BOLD, 150), 145, 0, 0, 40, 768);
        					break;
        			case 2: fadeIn(fadeIndex, g2d, "VICTORY", new Font("Eras Light ITC", Font.BOLD, 150), 255, 208, 40, 90, 691);
        					fadeIn(fadeIndex, g2d, "ACHIEVED", new Font("Eras Light ITC", Font.BOLD, 150), 255, 208, 40, 28, 845);
        					break;
        			case 3: fadeIn(fadeIndex, g2d, "Orange", new Font("Eras Light ITC", Font.BOLD, 150), 255, 131, 0, 125, 691);
        					fadeIn(fadeIndex, g2d, "Wins", new Font("Eras Light ITC", Font.BOLD, 150), 255, 131, 0, 213, 845);
							break;
        			case 4: fadeIn(fadeIndex, g2d, "Purple", new Font("Eras Light ITC", Font.BOLD, 150), 173, 0, 207, 170, 691);
        					fadeIn(fadeIndex, g2d, "Wins", new Font("Eras Light ITC", Font.BOLD, 150), 173, 0, 207, 213, 845);		
        					break;
        			}
        		}
        		catch(Exception e){
        			System.out.println("Interrupted Exception @paintComponent");
        		}
        		if(fadeIndex<254){
        			fadeIndex+=2;
        		}
        		if(circIndex>300){
        			victory = 0;
        			level = 0;
					Test.startTime = System.currentTimeMillis();
					pPress.pauseTime = 0;
					cLives = 3;
					pLives = 5;
					toPrint = true;
					circIndex = 0;
					startTimer = 0;
					fadeIndex = 0;
					gameState = "Menu";
        		}
        	}
        }
        else if(gameState.equals("Menu")){
        	g2d.setColor(new Color(255, 255, 255, 190));
        	g2d.fillRect(0, 0, 800, 1536);
        	currentButton = null;
        	for(PanelButton button : Test.menu){
        		button.draw(g2d);
        	}
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 200));
        	g2d.setColor(Color.black);
        	g2d.drawString("Puckle", 100, 280);
        	
        }
        else if(gameState.equals("Controls")){
        	g2d.setColor(new Color(255, 255, 255, 190));
        	g2d.fillRect(0, 0, 800, 1536);
        	currentButton = null;
        	back.draw(g2d);
        	g2d.setColor(Color.black);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 130));
        	g2d.drawString("Controls", 150, 155);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 60));
        	g2d.drawString("Single Player", 50, 300);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 42));
        	g2d.drawString("Click your mallet to start the game.", 50, 380);
        	g2d.drawString("Use your mouse to move.", 55, 450);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 60));
        	g2d.drawString("Two Player", 50, 600);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 42));
        	g2d.drawString("Click to start the countdown.", 50, 680);
        	String[] names1 = {"W", "S", "A", "D"};
        	String[] names2 = {"I", "K", "J", "L"};
        	String[] names0 = {"Up", "Down", "Left", "Right"};
        	g2d.drawString("Player 1", 200, 750);
        	g2d.drawString("Player 2", 500, 750);
        	for(int i = 0; i<4; i++){
        		g2d.drawString(names0[i], 50, 820+70*i);
        		g2d.drawString(names1[i], 250, 820+ 70*i);
        		g2d.drawString(names2[i], 550, 820+70*i);
        	}
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 60));
        	g2d.drawString("Use P to pause/unpause.", 50, 1200);
        }
        else if(gameState.equals("Scores")){
        	g2d.setColor(new Color(255, 255, 255, 190));
        	g2d.fillRect(0, 0, 800, 1536);
        	currentButton = null;
        	back.draw(g2d);
        	g2d.setColor(Color.black);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 130));
        	g2d.drawString("High Scores", 70, 155);
        	g2d.setFont(new Font("Bauhaus 93", Font.PLAIN, 50));
        	for(int i = 0; i<15; i++){
        		if(names[i].equals("---")){
        			break;
        		}
        		g2d.drawString((i+1) + ".\t " + names[i], 70, 300+70*i);
        		g2d.drawString(""+scores[i], 600, 300+70*i);
        	}
        }
    }
    public static void log(PrintWriter p, String s){
    	p.println(s);
    }
    public static void paiCalc(Mallet given){
		if(puck1.centY<=768){
			pYTarget = pCenterY;
		}
		else{
			if(puck1.centY+given.rad>given.centY){
				pYTarget = puck1.centY+puck1.rad+given.rad;
			}
			else{
				pYTarget = puck1.centY+puck1.rad;
			}
		}
		if(timer%10==0){
			pXTarget = puck1.centX;
		}
		timer++;
		double newVelX = pXTarget-given.centX;
		double newVelY = pYTarget-given.centY;
		given.setVelocity(newVelX, newVelY);
		pPuckDir = (int)Math.round(puck1.velX/Math.abs(puck1.velX));
	}
    public static void caiCalc(Mallet given){
    	if(puck1.centY>=768){
			cYTarget = cCenterY;
		}
		else{
			if(puck1.centY-given.rad<given.centY){
				cYTarget = puck1.centY-puck1.rad-given.rad;
			}
			else{
				cYTarget = puck1.centY-puck1.rad;
			}
		}
		if(timer%10==0){
			cXTarget = puck1.centX;
		}
		double newVelX = cXTarget-given.centX;
		double newVelY = cYTarget-given.centY;
		given.setVelocity(newVelX, newVelY);
	}
    public static void levelLoad(PrintWriter printer )throws InterruptedException{
    	if(level==0){
    		if(!pvp){
    			player = new Mallet(368, 1236-32, 1, 40);
    			objects.set(0, player);
    		}

    	}
    	score += ((int)(bonus/10))*10;
    	bonus = 1000;
		startTimer = 0;
		if(!pvp){
			friction -= .0015;
			energyLoss -= .03;
		}
		if(!pvp){
			computer = new Mallet(368, 268, 0, maxVels[level]);
			objects.set(1, computer);
		}
		//fuck ben
		skipTimer = 0;
		Test.startTime = System.currentTimeMillis();
		pPress.pauseTime = 0;
		if(!pvp){
			cLives = 3;
		}
		gameState = "levelStart";
		if(!(pvp&&puck1.y==742)){
			for (Physical object : Test.objects){
				object.setVelocity(0,0);
				if (object.mapMove==1){
					player.setPosition(368, 1236-player.rad);
				}
				else if (object.mapMove==0){
					computer.setPosition(368, 302-computer.rad);
				}
				else if(object instanceof Puck){
					if(!pvp){
						puck1.setPosition(374, 1000);
					}
					else{
						puck1.setPosition(373, 742);
					}
				}
			}
		
		}
		if(printer==null){
			return;
		}
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "");
		Test.log(printer, "                      LEVEL " + level + "                            ");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		Test.log(printer, "____________________________________________________________");
		
		
    }
    public static void aiCalc(Mallet given){
		double newVelX = 0, newVelY = 0;
		double minDist = 10000;
		Physical nearest = Test.objects.get(2);
		boolean isApproaching = false;
		for (Physical ob : Test.objects){
			if (ob instanceof Puck){
				double distance = Math.sqrt(Math.pow(ob.centX-computer.centX, 2)+Math.pow(ob.centY-computer.centY, 2));
				boolean approaching = ob.velY>0?false : true;
				if(distance<minDist || (approaching&&!isApproaching)){
					minDist = distance;
					nearest = ob;
					isApproaching = approaching;
				}
			}
		}
		if(nearest.velY==0&&nearest.velX==0){
			return;
		}
		else if((nearest.centY+nearest.rad)<768){
			given.targetX=-100;
			double edgeDistX = nearest.centX-nearest.rad-given.centX-given.rad;
			double edgeDistY = nearest.centY-nearest.rad-given.centY-given.rad;
			if(nearest.centY<=given.rad){
				newVelX = nearest.centX-given.centX;
				newVelY = nearest.centY-given.centY;
			}
			else if(edgeDistY<=-nearest.rad){
				newVelX = (Math.abs(given.centX-nearest.centX)/(given.centX-nearest.centX))*(20+nearest.rad)+nearest.centX-given.centX;
				newVelY = nearest.centY-nearest.rad-given.rad-given.centY;
			}
			else if (edgeDistX<20){
				newVelX = nearest.centX-given.centX;
				newVelY = nearest.centY-given.centY-nearest.rad;
			}
			else{
				newVelX = nearest.centX-given.centX;
				newVelY = (nearest.centY-given.centY-given.rad-nearest.rad)/2;
			}
		}
		else{
			double targetDist = Math.sqrt(Math.pow((given.targetX-given.centX),2)+Math.pow((given.targetY-given.centY),2));
			if(given.targetX==-100||targetDist<given.maxVel){
				given.targetX = (nearest.centX+400)/2;
				given.targetY = 300; //given.centY-Math.pow(Math.random(),2)*(given.targetX-given.centX);
				if(given.targetY<100){
					given.targetY=100;
				}
			}
			newVelX = given.targetX-given.centX;
			newVelY = given.targetY-given.centY;
		}
		given.setVelocity(newVelX, newVelY);
	}
	public static void main(String[] args) throws InterruptedException, IOException {

    	PrintWriter printer = new PrintWriter("log.txt");
    	javax.swing.UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 25));
		javax.swing.UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 25));
		javax.swing.UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 25));
        //Create pieces
        objects.add(player);
        objects.add(computer);
        objects.add(puck1);
    	//Make board
        JFrame frame = new JFrame("Puckle!");
        game.setPreferredSize(new Dimension(800, 1536));
        game.addMouseListener(game);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        Scanner scoreScan = new Scanner(new File("scores.txt"));
        scoreScan.useDelimiter("`");
        for(int i = 0; i<15; i++){
        	names[i] = scoreScan.next();
        	scores[i]= scoreScan.nextInt();
        }
        scoreScan.close();
        //Adding key inputs
        KeyBind.bind("pressed P", game, pPress);
        
        Reset bPress = new Reset(printer, pPress);
        KeyBind.bind("pressed B", game, bPress);
   
        Goto gPress = new Goto();
        KeyBind.bind("pressed G", game, gPress);
        
        Arrow leftPress = new Arrow(-10, gPress);
        Arrow rightPress = new Arrow(10, gPress);
        KeyBind.bind(KeyEvent.VK_LEFT, game, leftPress, "left");
        KeyBind.bind(KeyEvent.VK_RIGHT, game, rightPress, "right");
        
        MoveAction wPress = new MoveAction(Test.player, 3, true);
        MoveAction sPress = new MoveAction(Test.player, 2, true);
        MoveAction aPress = new MoveAction(Test.player, 1, true);
        MoveAction dPress = new MoveAction(Test.player, 0, true);
        MoveAction wRelease = new MoveAction(Test.player, 3, false);
        MoveAction sRelease = new MoveAction(Test.player, 2, false);
        MoveAction aRelease = new MoveAction(Test.player, 1, false);
        MoveAction dRelease = new MoveAction(Test.player, 0, false);
        KeyBind.bind("pressed W", game, wPress);
        KeyBind.bind("pressed S", game, sPress);
        KeyBind.bind("pressed A", game, aPress);
        KeyBind.bind("pressed D", game, dPress);
        KeyBind.bind("released W", game, wRelease);
        KeyBind.bind("released S", game, sRelease);
        KeyBind.bind("released A", game, aRelease);
        KeyBind.bind("released D", game, dRelease);
        
        MoveAction iPress = new MoveAction(Test.player, 7, true);
        MoveAction kPress = new MoveAction(Test.player, 6, true);
        MoveAction jPress = new MoveAction(Test.player, 5, true);
        MoveAction lPress = new MoveAction(Test.player, 4, true);
        MoveAction iRelease = new MoveAction(Test.player, 7, false);
        MoveAction kRelease = new MoveAction(Test.player, 6, false);
        MoveAction jRelease = new MoveAction(Test.player, 5, false);
        MoveAction lRelease = new MoveAction(Test.player, 4, false);
        KeyBind.bind("pressed I", game, iPress);
        KeyBind.bind("pressed K", game, kPress);
        KeyBind.bind("pressed J", game, jPress);
        KeyBind.bind("pressed L", game, lPress);
        KeyBind.bind("released I", game, iRelease);
        KeyBind.bind("released K", game, kRelease);
        KeyBind.bind("released J", game, jRelease);
        KeyBind.bind("released L", game, lRelease);
        
        
        
        //Creating buttons
        String[] displays = {"Single Player", "Two Player", "Controls", "Scores"};
        int[] fontXPos = {235, 260, 290, 322};
        int[] actions = {0, 1, 2, 5};
        for(int i = 0; i<4; i++){
        	menu[i] = new PanelButton(150, 820+150*i, 500, 120, actions[i], displays[i], fontXPos[i], 897+150*i);
        }

        
        puck1.setVelocity(Math.random()*20-10, Math.random()*20-10);
        frame.setVisible(true);
        while (true) {
    		if(!oldState.equals(gameState)){
    			System.out.println(gameState);
    			currentButton = null;
    		}
    		oldState = gameState;
        	//loopStart = System.currentTimeMillis()-startTime;
        	//for (int i = 0; i<10; i++){
        	if(toPrint){
        		Test.log(printer, "____________________________________________________________");
    			Test.log(printer, "____________________________________________________________");
    			Test.log(printer, "____________________________________________________________");
    			Test.log(printer, "____________________________________________________________");
    			Test.log(printer, "");
    			Test.log(printer, "                          NEW GAME                          ");
    			Test.log(printer, "____________________________________________________________");
    			Test.log(printer, "____________________________________________________________");
    			Test.log(printer, "____________________________________________________________");
    			Test.log(printer, "____________________________________________________________");
    			toPrint = false;
        	}
        	
        	//Logs
    		currentTime = System.currentTimeMillis()-startTime-pPress.pauseTime;
    		log(printer, "[" + currentTime + "] ");
    		log(printer, "\t Player:   " + ((double)Math.round(objects.get(0).x*10000)/10000) + " " + ((double)Math.round(objects.get(0).y*10000)/10000) + "\t" + ((double)Math.round(objects.get(0).velX*10000)/10000) + " " + ((double)Math.round(objects.get(0).velY*10000)/10000));
    		log(printer, "\t Computer: " + ((double)Math.round(objects.get(1).x*10000)/10000) + " " + ((double)Math.round(objects.get(1).y*10000)/10000) + "\t" + ((double)Math.round(objects.get(1).velX*10000)/10000) + " " + ((double)Math.round(objects.get(1).velY*10000)/10000));
    		log(printer, "\t Puck:     " + ((double)Math.round(objects.get(2).x*10000)/10000) + " " + ((double)Math.round(objects.get(2).y*10000)/10000) + "\t" + ((double)Math.round(objects.get(2).velX*10000)/10000) + " " + ((double)Math.round(objects.get(2).velY*10000)/10000));	

        	if(gameState.equals("On")||gameState.equals("Menu")||gameState.equals("Scores")||gameState.equals("Controls")||(pvp&&(gameState.equals("levelStart")||gameState.equals("pointStart")))){
        		//Position calculations
        		player.checkPosition();
        		computer.checkPosition();
        		puck1.collide(player);
        		puck1.collide(computer);
        		if(!gameState.equals("levelStart")&&!gameState.equals("pointStart")){
        			puck1.checkPosition();
        		}

        	}
        	if(gameState.equals("Menu")||gameState.equals("Scores")||gameState.equals("Controls")){
        		computer.maxVel = 5;
        		player.maxVel = 5;
        		paiCalc(player);       		
        		caiCalc(computer);
        		game.repaint();
        		Thread.sleep(10);
        		if(pLives<5){
        			puck1.setPosition(374, 742);
        			puck1.setVelocity(Math.random()*20-10, Math.random()*20-10);
        			pLives++;
        		}
        		else if (cLives<3){
        			puck1.setPosition(374, 742);
        			puck1.setVelocity(Math.random()*20-10, Math.random()*20-10);
        			cLives++;
        		}
        	}
        	else if (gameState.equals("pointStart")){
        		if(pvp){
        			MoveAction.newVelocity();

        		}
        		game.repaint();
        		Thread.sleep(10);
        	}
        	else if (gameState.equals("levelStart")){
        		if(pvp){
        			MoveAction.newVelocity();
        		}
        		game.repaint();
        		Thread.sleep(10);
        		if(!pvp&&startTimer>350){
        			System.out.println("Starting game.");
        			gameState = "pointStart";
        		}
        		else if(pvp&&startTimer>2980){
        			System.out.println("Starting game.");
        			gameState = "On";
        		}
        		
        	}
        	else if (gameState.equals("On")){
        		MoveAction.newVelocity(); //Recalculate key totals
        		if(!pvp){
        			aiCalc(computer);
        			if(puck1.centY>768&&puck1.centY+puck1.velY<=768){
        				score += 10+10*((int)Math.pow(level, 1.5));
        			}
        			if(bonus>0){
        				bonus -= .2;
        			}
        		}
        		if(pLives>0&&cLives>0){
            		game.repaint();
            		Thread.sleep(10);
            	}
            	else if(pLives==0){
            		//Enemy victory
            		gameState = "Off";
            		victory = pvp?3:1;
            	}
            	else if(level<6&&!pvp){
            		level++;
            		levelLoad(printer);
            	}
            	else {
            		//Player victory, beats game
            		gameState = "Off";
            		victory = pvp?4:2;
            	}
        	}
        	else if(gameState.equals("Paused")){
        		game.repaint();
        		Thread.sleep(100);
        	}
        	else if(gameState.equals("Off")){
        		Thread.sleep(500);
        		while(gameState.equalsIgnoreCase("Off")){
        			game.repaint();
        			Thread.sleep(20);
        		}
        		
        		bonus = 0;
        		if(!pvp){
        			int scoreIndex = 15;
        			for(int i = 0; i<scores.length;i++){
        				if(scores[i]<=score){
        					scoreIndex = i;
        					break;
        				}
        			}
        			
        			if(scoreIndex<15){
        				System.out.println("Highscore.");
        				String youAreBelow = "A new dog's in town.";
        				if(scoreIndex>0){
        					youAreBelow = "You are just below " + names[scoreIndex-1] + ", who scored " + scores[scoreIndex-1] + ".";
        				}
        				String[] adjs = new String[15];
        				adjs[0] = "1st";
        				adjs[1] = "2nd";
        				adjs[2] = "3rd";
        				for(int i = 3; i<12; i++){
        					adjs[i] = (i+1) + "th";
        				}
        				adjs[12] = "the unlucky 13th";
        				adjs[13] = "14th";
        				adjs[14] = "a close 15th";
        				String name = "";
        				boolean isLong = false;
        				while(name==null||name.equals("")||isLong){
        					if(!isLong){
        						name = JOptionPane.showInputDialog(null, "Your score was " + score + ", putting you in " + adjs[scoreIndex] + " place! \n" + youAreBelow + " \nEnter the name you want to be remembered by...", "Highscore!", JOptionPane.PLAIN_MESSAGE);
        					}
        					else{
        						name = JOptionPane.showInputDialog(null, "Your name has to be less than 15 characters. \nEnter a different name...", "Stop that Man", JOptionPane.PLAIN_MESSAGE);
        					}
        					if(name==null){
        						continue;
        					}
        					if(name.length()>15){
        						isLong = true;
        					}
        					else{
        						isLong = false;
        					}
        				}
        				int oldScore = scores[scoreIndex];
        				scores[scoreIndex] = score;
        				int olderScore = 0;
        				if(scoreIndex<14){
        					olderScore = scores[scoreIndex+1];
        				}
        				for (int i = scoreIndex+1; i<14; i++){
        					scores[i] = oldScore;
        					oldScore = olderScore;
        					olderScore = scores[i+1];
        				}
        				scores[14]=oldScore;
        				String oldName = names[scoreIndex];
        				names[scoreIndex] = name;
        				String olderName = "";
        				if(scoreIndex< 14){
        					olderName = names[scoreIndex+1];
        				}
        				for (int i = scoreIndex+1; i<14; i++){
        					names[i] = oldName;
        					oldName = olderName;
        					olderName = names[i+1];
        				}
        				names[14] = oldName;
        				PrintWriter scoreWriter = new PrintWriter("scores.txt");
        				for(int i =0; i<15;i++){
        					scoreWriter.print(names[i] + "`" + scores[i] + "`");
        				}
        				scoreWriter.close();
        				score = 0;
        				bonus = 0;
        			}
        			else{
        				System.out.println("No highscore");
        				JOptionPane.showMessageDialog(null, "You failed to get a high score...", "How are you so bad", JOptionPane.PLAIN_MESSAGE);
        			}
        		}
        		else{
        			pvp = false;
        			
        			Thread.sleep(1200);
        		}
        		score = 0;
        	}
        	

        	

        }
        //System.out.println("LOOP ENDED???");
        
    }
}
