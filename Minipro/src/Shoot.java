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
  
   ArrayList<LoadEnemy> LoadEnemyList = null;  //떨어지는 것  리스트 
   ArrayList<Image> gameItemList = null;  //게임 아이템 이미지 리스트 
   ArrayList<Panel> panelList = null;  //판넬 리스트 
   
   public XmlOpen()
   {
      xml = new XMLReader("block.xml");  //Xml파일을 열고 객체 생성
      blockGameNode = xml.getBlockGameElement();
     
      blockNode = XMLReader.getNode(blockGameNode, XMLReader.E_BLOCK);   //떨어지는 이미지 
      userNode =  XMLReader.getNode(blockGameNode, XMLReader.E_USER);   //유저 이미지 
      gameItemNode  =  XMLReader.getNode(blockGameNode, XMLReader.E_ITEM);  //게임 아이템 이미지 
      panelNode =  XMLReader.getNode(blockGameNode, XMLReader.E_PANEL);  //게임 판넬 이미지  
      
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
         
            int kcal = Integer.parseInt(XMLReader.getAttr(node, "kcal"));  //칼로리 정보랑 
            int itemType = Integer.parseInt(XMLReader.getAttr(node, "type"));  //타입 
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
   private ArrayList<Ms> msList = null;//미사일 
   private ArrayList<Enemy> enList = null;//음식.운동 아이템들
   private ArrayList<LoadEnemy> LoadEnemyList = null; //불러온 데이터   
  
   private Image userImage = null; 
   private ArrayList<Image> gameItemList = null;
   private ArrayList<Panel> panelList = null;
   
   private int totalKcal=0;

   
   private boolean left = false, right = false, up = false, down = false,
         fire = false;
   private boolean start = false, end = false;
   private int w = 1000, h = 600, x = 500, y = 450, xw = 20, xh = 20;
         // w랑 h는 화면 가로 세로 크기구요...x랑 y는 user 처음 위치...xw랑 xh는 미사일 크기인듯! 
   public Shoot() {
      
      bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      msList = new ArrayList<Ms>();//미사일 발사할때 저장될 arraylist
      enList = new ArrayList<Enemy>();//음식, 운동 아이템 떨어지는거 저장될 arraylist

      XmlOpen xo = new XmlOpen();
     
       LoadEnemyList = xo.itemReader();  //아이템 불러옴 
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
            Thread.sleep(1);//내꺼 움직이는 속도
            if (start) 
            {
               if (enCnt > 2200) {//적 생기는 속도 1.1초로 설정
                  enCreate();
                  enCnt = 0;
               }
               if (msCnt > 80) {//미사일 발사될때 개수 세나봐여! 80개 넘어가면 초기화되는듯!
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
         //double rx = Math.random() * (800 - xw)+200;//음식 내려오는 공간 설정 (가로 200px~800px까지만 내려오게함)
         int j = rd.nextInt(9); 
         double rx = x[j];
         double ry = Math.random() * 50;
         int select = rd.nextInt(40);                //여기서 랜덤으로 enList에 5개 넣음 
         LoadEnemy le = LoadEnemyList.get(select);   //총불러온 아이템에서 랜덤으로 선택해서 뿌려줄꺼 선택 
         Enemy en = new Enemy((int) rx, (int) ry, le.img, le.kcal,le.itemType);
         
         enList.add(en);
      }
   }

   public void crashChk() {
      Graphics g = this.getGraphics();
      Polygon p = null;
      //여기는 총알이랑 음식이랑 맞는 위치? 정하는거 같구여
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
                  totalKcal += e.kcal;  //음식이면 칼로리 추가 
               else
               {
                  totalKcal -= e.kcal;  //운동 이면 칼로리 감소 
                  if(totalKcal<=0 )
                     totalKcal=0;
               }
               }
         }
      }
      //여기는 음식이랑 user가 맞는 위치? 정하는것같아여!
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
    
      
     
      //왼쪽 배경 
      gs.setColor(Color.gray);
      gs.fillRect(0, 0, 200, 600);
      
   
      //오른쪽 배경
      gs.setColor(Color.white);
      gs.fillRect(200, 0, w, h);
      
      //왼쪽 화면-게임 정보 출력
      gs.setColor(Color.black);
      gs.drawString("▶ 게임 시작 : Enter", 20, 50);
      gs.setColor(Color.BLUE);
      gs.setFont(new Font("고딕체",Font.BOLD,20));
      gs.drawString("현재 칼로리 : "+totalKcal+"kcal",20, 100);
      
      //Panel
  
      gs.drawImage(itemPan.img,itemPan.x,itemPan.y,itemPan.w,itemPan.h,this);
      gs.drawImage(kcalPan.img,kcalPan.x,kcalPan.y,kcalPan.w,kcalPan.h,this);
      gs.drawImage(missionPan.img,missionPan.x,missionPan.y,missionPan.w,missionPan.h,this);
      
      gs.drawImage(gameItemList.get(1),10,20,50,50,this);
      gs.drawImage(gameItemList.get(1),65,20,50,50,this);
      gs.drawImage(gameItemList.get(1),120,20,50,50,this);
    
      //게임 종료시
      if (end) {
         gs.setColor(Color.red);
         gs.drawString("G A M E  O V E R", 500, 250);
         gs.drawString("다시 시작하려면 Enter키를 누르세요.",500,270);
      }
      
      //user Icon 생성
     
      Image user = userImage;
      gs.drawImage(user,x,y,70,150,this);      
     

      //미사일 생성
      for (int i = 0; i < msList.size(); i++) {
         Ms m = (Ms) msList.get(i);
         gs.setColor(Color.red);
         gs.fillOval(m.x, m.y, 10, 10);//10은 미사일 지름
         if (m.y < 0)
            msList.remove(i);
         m.moveMs();
      }
      
      //음식,운동 아이템 설정
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
            //up = true;  좌우만 이동하게 해야하므로..
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