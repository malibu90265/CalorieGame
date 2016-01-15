import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XmlOpen
{ 
   XMLReader xml; 
   Node blockGameNode;
   
   Node blockNode; 
   Node userNode;
   Node gameItemNode;
   Node panelNode;
   
   NodeList userNodeList;
   NodeList blockNodeList;
   NodeList gameItemNodeList;
   NodeList panelNodeList;
  
   ArrayList<LoadEnemy> LoadEnemyList = null;  //�������� ��  ����Ʈ 
   ArrayList<Image> gameItemList = null;  //���� ������ �̹��� ����Ʈ 
   ArrayList<Panel> panelList = null;  //�ǳ� ����Ʈ 
   
   public XmlOpen()
   {
      xml = new XMLReader("block.xml");  //Xml������ ���� ��ü ����
      blockGameNode = xml.getBlockGameElement();
     
      blockNode = XMLReader.getNode(blockGameNode, XMLReader.E_BLOCK);   //�������� �̹��� 
      userNode =  XMLReader.getNode(blockGameNode, XMLReader.E_USER);   //���� �̹��� 
      gameItemNode  =  XMLReader.getNode(blockGameNode, XMLReader.E_ITEM);  //���� ������ �̹��� 
      panelNode =  XMLReader.getNode(blockGameNode, XMLReader.E_PANEL);  //���� �ǳ� �̹���  
      
      blockNodeList = blockNode.getChildNodes();
      userNodeList = userNode.getChildNodes();
      gameItemNodeList = gameItemNode.getChildNodes();
      panelNodeList = panelNode.getChildNodes();
  
   
   }
   
   public ArrayList<LoadEnemy> itemReader()
   {
	  
      LoadEnemyList = new ArrayList<LoadEnemy>(); 
  
      for(int i=0; i<blockNodeList.getLength(); i++)
      {
         Node node = blockNodeList.item(i);
         if(node.getNodeType() != Node.ELEMENT_NODE)
            continue;
         if(node.getNodeName().equals(XMLReader.E_OBJ))
         {
         
            int kcal = Integer.parseInt(XMLReader.getAttr(node, "kcal"));  //Į�θ� ������ 
            int itemType = Integer.parseInt(XMLReader.getAttr(node, "type"));  //Ÿ�� 
            ImageIcon imgicon = new ImageIcon(XMLReader.getAttr(node, "img"));
            Image img  = imgicon.getImage();
            LoadEnemy en = new LoadEnemy(img,kcal,itemType);
            LoadEnemyList.add(en);
         
         }
   
      }
        return LoadEnemyList;
   
   }
   public Image userReader()
   {
	   Image img = null;
	   ImageIcon imgIcon = null;
	   
	   for(int i=0; i<userNodeList.getLength(); i++)
	      {
	         Node node = userNodeList.item(i);
	         if(node.getNodeType() != Node.ELEMENT_NODE)
	            continue;
	         if(node.getNodeName().equals(XMLReader.E_OBJ))
	         {
	         
	        	imgIcon = new ImageIcon(XMLReader.getAttr(node, "img"));
	            img  = imgIcon.getImage();
	         }
	   
	      }
	   
	   return img;
	
   }
   
   public ArrayList<Image> gameItemReader()
   {
	   gameItemList = new ArrayList<Image>();
	   
	   
	   for(int i=0; i<gameItemNodeList.getLength(); i++)
	      {
	         Node node = gameItemNodeList.item(i);
	         if(node.getNodeType() != Node.ELEMENT_NODE)
	            continue;
	         if(node.getNodeName().equals(XMLReader.E_OBJ))
	         {
	         
	        	ImageIcon imgIcon = new ImageIcon(XMLReader.getAttr(node, "img"));
	            Image img  = imgIcon.getImage();
	            
	            gameItemList.add(img);
	         }
	   
	      }
	   
	   return gameItemList;
	  
	}
   
   public ArrayList<Panel> panelReader()
   {
	   
	   panelList = new ArrayList<Panel>(); 
	  
	   
	   for(int i=0; i<panelNodeList.getLength(); i++)
	      {
	         Node node = panelNodeList.item(i);
	         if(node.getNodeType() != Node.ELEMENT_NODE)
	            continue;
	         if(node.getNodeName().equals(XMLReader.E_OBJ))
	         {
	         
	        	ImageIcon imgIcon = new ImageIcon(XMLReader.getAttr(node, "img"));
	            Image img  = imgIcon.getImage();
	            int x=  Integer.parseInt(XMLReader.getAttr(node, "x"));
	            int y=  Integer.parseInt(XMLReader.getAttr(node, "y"));
	            int w=  Integer.parseInt(XMLReader.getAttr(node, "w"));
	            int h=  Integer.parseInt(XMLReader.getAttr(node, "h"));
	            
	            panelList.add(new Panel(x,y,w,h,img));
	            
	          
	         }
	   
	      }
	   
	   return panelList;
	  
	}
   
   
   
   }


public class Shoot extends JFrame implements Runnable, KeyListener {
   private BufferedImage bi = null;
   private ArrayList<Ms> msList = null;//�̻��� 
   private ArrayList<Enemy> enList = null;//����.� �����۵�
   private ArrayList<LoadEnemy> LoadEnemyList = null; //�ҷ��� ������   
  
   private Image userImage = null; 
   private ArrayList<Image> gameItemList = null;
   private ArrayList<Panel> panelList = null;
   
   private int totalKcal=0;

   
   private boolean left = false, right = false, up = false, down = false,
         fire = false;
   private boolean start = false, end = false;
   private int w = 1000, h = 600, x = 500, y = 450, xw = 20, xh = 20;
         // w�� h�� ȭ�� ���� ���� ũ�ⱸ��...x�� y�� user ó�� ��ġ...xw�� xh�� �̻��� ũ���ε�! 
   public Shoot() {
      
      bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      msList = new ArrayList<Ms>();//�̻��� �߻��Ҷ� ����� arraylist
      enList = new ArrayList<Enemy>();//����, � ������ �������°� ����� arraylist

      XmlOpen xo = new XmlOpen();
     
       LoadEnemyList = xo.itemReader();  //������ �ҷ��� 
       userImage =xo.userReader();
       gameItemList  = xo.gameItemReader();
       panelList = xo.panelReader();
       
      this.addKeyListener(this);
      this.setSize(w, h); 
      this.setTitle("Shooting Game");
      this.setResizable(false);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setVisible(true);
   }

   public void run() {
      try {
         int msCnt = 0;
         int enCnt = 0;
         while (true) {
            Thread.sleep(1);//���� �����̴� �ӵ�
            if (start) 
            {
               if (enCnt > 2200) {//�� ����� �ӵ� 1.1�ʷ� ����
                  enCreate();
                  enCnt = 0;
               }
               if (msCnt > 80) {//�̻��� �߻�ɶ� ���� ��������! 80�� �Ѿ�� �ʱ�ȭ�Ǵµ�!
                  fireMs();
                  msCnt = 0;
               }
               msCnt += 10;
               enCnt += 10;
               keyControl();
               crashChk();
            }
            draw();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void fireMs() {
      if (fire) {
         if (msList.size() < 100) {
            Ms m = new Ms(this.x, this.y);
            msList.add(m);
         }
      }
   }

   public void enCreate() {
      Random rd = new Random();
      
      for (int i = 0; i < 3; i++) {
         double x[] ={ 240, 320, 400, 480, 560,640,720,800,880 };
         //double rx = Math.random() * (800 - xw)+200;//���� �������� ���� ���� (���� 200px~800px������ ����������)
         int j = rd.nextInt(9); 
         double rx = x[j];
         double ry = Math.random() * 50;
         int select = rd.nextInt(40);                //���⼭ �������� enList�� 5�� ���� 
         LoadEnemy le = LoadEnemyList.get(select);   //�Ѻҷ��� �����ۿ��� �������� �����ؼ� �ѷ��ٲ� ���� 
         Enemy en = new Enemy((int) rx, (int) ry, le.img, le.kcal,le.itemType);
         
         enList.add(en);
      }
   }

   public void crashChk() {
      Graphics g = this.getGraphics();
      Polygon p = null;
      //����� �Ѿ��̶� �����̶� �´� ��ġ? ���ϴ°� ������
      for (int i = 0; i < msList.size(); i++) {
         Ms m = (Ms) msList.get(i);         
         for (int j = 0; j < enList.size(); j++) {
            Enemy e = (Enemy) enList.get(j);
            int[] xpoints = { m.x, (m.x + m.w), (m.x + m.w), m.x };
            int[] ypoints = { m.y, m.y, (m.y + m.h), (m.y + m.h) };
            p = new Polygon(xpoints, ypoints, 4);
            if (p.intersects((double) e.x, (double) e.y, (double) e.w+80,(double) e.h+80)) {
               msList.remove(i);
               enList.remove(j);
               
               if(e.itemType == 1)
                  totalKcal += e.kcal;  //�����̸� Į�θ� �߰� 
               else
               {
                  totalKcal -= e.kcal;  //� �̸� Į�θ� ���� 
                  if(totalKcal<=0 )
                     totalKcal=0;
               }
               }
         }
      }
      //����� �����̶� user�� �´� ��ġ? ���ϴ°Ͱ��ƿ�!
      for (int i = 0; i < enList.size(); i++) {
         Enemy e = (Enemy) enList.get(i);
         int[] xpoints = { x, (x + xw), (x + xw), x };
         int[] ypoints = { y, y, (y + xh), (y + xh) };
         p = new Polygon(xpoints, ypoints, 4);

         if (p.intersects((double) e.x, (double) e.y, (double) e.w,(double) e.h)) {
            enList.remove(i);
            start = false;
            end = true;
         }
      }
   }

   public void draw() {
      Graphics gs = bi.getGraphics();
      //Panel
      Panel itemPan = panelList.get(0);  //itempan
      Panel kcalPan = panelList.get(1);
      Panel missionPan = panelList.get(2);
    
      
     
      //���� ��� 
      gs.setColor(Color.gray);
      gs.fillRect(0, 0, 200, 600);
      
   
      //������ ���
      gs.setColor(Color.white);
      gs.fillRect(200, 0, w, h);
      
      //���� ȭ��-���� ���� ���
      gs.setColor(Color.black);
      gs.drawString("�� ���� ���� : Enter", 20, 50);
      gs.setColor(Color.BLUE);
      gs.setFont(new Font("���ü",Font.BOLD,20));
      gs.drawString("���� Į�θ� : "+totalKcal+"kcal",20, 100);
      
      //Panel
  
      gs.drawImage(itemPan.img,itemPan.x,itemPan.y,itemPan.w,itemPan.h,this);
      gs.drawImage(kcalPan.img,kcalPan.x,kcalPan.y,kcalPan.w,kcalPan.h,this);
      gs.drawImage(missionPan.img,missionPan.x,missionPan.y,missionPan.w,missionPan.h,this);
      
      gs.drawImage(gameItemList.get(1),10,20,50,50,this);
      gs.drawImage(gameItemList.get(1),65,20,50,50,this);
      gs.drawImage(gameItemList.get(1),120,20,50,50,this);
    
      //���� �����
      if (end) {
         gs.setColor(Color.red);
         gs.drawString("G A M E  O V E R", 500, 250);
         gs.drawString("�ٽ� �����Ϸ��� EnterŰ�� ��������.",500,270);
      }
      
      //user Icon ����
     
      Image user = userImage;
      gs.drawImage(user,x,y,70,150,this);      
     

      //�̻��� ����
      for (int i = 0; i < msList.size(); i++) {
         Ms m = (Ms) msList.get(i);
         gs.setColor(Color.red);
         gs.fillOval(m.x, m.y, 10, 10);//10�� �̻��� ����
         if (m.y < 0)
            msList.remove(i);
         m.moveMs();
      }
      
      //����,� ������ ����
      for (int i = 0; i < enList.size(); i++) {
         Enemy e = enList.get(i);
         gs.drawImage(e.img,e.x,e.y,80,80,this);
         
         
        if (e.y > h)
            enList.remove(i);
         e.moveEn();
      }
     

      Graphics ge = this.getGraphics();
      ge.drawImage(bi, 0, 0, w, h, this);
   }

   public void keyControl() {
      if (200 < x) {
         if (left)
            x -= 3;
      }
      if (w > x + xw) {
         if (right)
            x += 3;
      }
      if (25 < y) {
         if (up)
            y -= 3;
      }
      if (h > y + xh) {
         if (down)
            y += 3;
      }
   }

   public void keyPressed(KeyEvent ke) {
         switch (ke.getKeyCode()) {
         case KeyEvent.VK_LEFT:
            left = true;
            break;
         case KeyEvent.VK_RIGHT:
            right = true;
            break;
         case KeyEvent.VK_UP:
            up = false;
            //up = true;  �¿츸 �̵��ϰ� �ؾ��ϹǷ�..
            break;
         case KeyEvent.VK_DOWN:
            down = false;
            //down = true;
            break;
         case KeyEvent.VK_SPACE:
            fire = true;
            break;
         case KeyEvent.VK_ENTER:
            start = true;
            end = false;
            break;
         }
      }

   public void keyReleased(KeyEvent ke) {
      switch (ke.getKeyCode()) {
      case KeyEvent.VK_LEFT:
         left = false;
         break;
      case KeyEvent.VK_RIGHT:
         right = false;
         break;
      case KeyEvent.VK_UP:
         up = false;
         break;
      case KeyEvent.VK_DOWN:
         down = false;
         break;
      case KeyEvent.VK_A:
         fire = false;
         break;
      case KeyEvent.VK_SPACE:
          fire =false;
          break;
      }
   }

   public void keyTyped(KeyEvent ke) {
   }

   public static void main(String[] args) {
      Thread t = new Thread(new Shoot());
      t.start();
   }
}

class Ms {
   int x;
   int y;
   int w = 40;
   int h = 40;

   public Ms(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void moveMs() {
      y--;
   }
}
class LoadEnemy
{
   Image img;
   int kcal;
   int itemType;
   
   public LoadEnemy(Image img, int kcal,int itemType)
   {
      this.img = img;
      this.kcal = kcal;
      this.itemType = itemType;
   }

}

class Enemy {
   int x;
   int y;
   int w = 10;
   int h = 10;
   Image img; 
   int kcal;
   int itemType; 

   public Enemy(int x, int y,Image img,int kcal,int itemType) {
      this.x = x;
      this.y = y;
      this.img = img;
      this.kcal = kcal;
      this.itemType = itemType; 
   }

   public void moveEn() {
      y++;
   }
}

class Panel
{
	int x;
	int y;
	int w;
	int h;
	Image img;
	
	Panel(int x, int y,int w, int h, Image img)
	{
		this.x =x;
		this.y =y;
		this.w =w;
		this.h =h;
		this.img = img;
		
	
	}
	
	
}