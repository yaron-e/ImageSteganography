import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
	public static void main(String [] args) throws IOException {
		String imageFile = "C:/Users/YARON/Pictures/black.png";
		//createFile("file1.txt", "Hello world.");
		
		writeToImage(imageFile, "h");
		

		/*String hello = "hello";
		byte[] b = hello.getBytes();

		for (int i = 0; i < 1; i++) {
			System.out.println(b[i]);
			byte curr = b[i];
			for (int j = 7; j >= 0; j --) {
				int bit = ((curr & 0xff) >> j) & 0x1;
				if (bit == 0 )
					System.out.print(0);
				else 
					System.out.print(1);
			}
			System.out.println();
		}*/
	}
	
	public static void writeToImage(final String path, String msg ) throws IOException {
		/*BufferedImage originalImage = ImageIO.read(new File(path));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( originalImage, "jpg", baos );
		baos.flush();
		byte[] imageInByte = Base64.getEncoder(baos.toString("UTF-8"));//.getBytes("UTF-8");//toByteArray();
		baos.close();
		*/
		
		byte [] msgB = msg.getBytes("UTF-8");
		File f = new File(new File(path).getParent() + "/newFile.png");
		f.createNewFile();
		FileInputStream fis = new FileInputStream(new File(path));	
		System.err.println(f.getPath());
		
		FileOutputStream fos = new FileOutputStream(f, false);
		int read = 0;		
		int msgSize = msgB.length;
		
		for (int i = 0;(read = fis.read()) != -1; i++) {
			if (i < msgSize*8 && i > 8) {
				int x = getByteFromMsg(msgB, i,read);
				fos.write(x);
				System.out.print(read + " " + x);
			} else {
				fos.write(read);
				System.out.print(read);
			}
			fos.flush();
			System.out.println();
		}

		
		fos.close();
		fis.close();

	}
	
	public static int getByteFromMsg(byte [] msg, int i, int image) {
		 byte b = msg[i/8];
		 int msb = ((b & 0xFF) >> 7) & 0x1;
		 msg[i/8] = (byte) (b << 1);
		 
		 image = (((image & 0x1) & 0) | msb ) | (image & 0xFE);
		 
		//int msgIndex = b.length / 8;
		return image;
	}

	public static void createFile(String name, String content) {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			fw = new FileWriter(name);
			bw = new BufferedWriter(fw);
			bw.write(content);
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
