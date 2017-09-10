import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
	public static void main(String [] args) throws IOException {
		String imageFile = "C:/Users/YARON/Pictures/img.jpg";
		//createFile("file1.txt", "Hello world.");

		String pathToNewImage = writeToImage(imageFile, "hello world, i am you master. bwhaaaaaahaaaaahaaa");
		String msg = readMsgFromFile(pathToNewImage);
		System.out.println(msg);

	}

	private static String readMsgFromFile(String pathToNewImage) throws IOException {
		FileInputStream fis = new FileInputStream(new File(pathToNewImage));	
		
		byte[] b = null;
		int read = 0; 
		int size = 0;
		read = fis.read(new byte[107]);
		for (int i = 108; (read = fis.read()) != -1; i++)  {
			if (i <= 107) {//Header
				//Do nothing
			} else if (i > 107 && i < 112) {//Message size
				size = fis.read() ;
				size += fis.read() << 8;
				size += fis.read() << 16;
				size += fis.read() << 24;
				
				System.out.println("Size = " + size);
				i = 111;
			} else if (i < size*8 + 112 && i >= 112) {//Message body
				if (b == null)
					b = new byte[size];

				
				for (int j = 0; j < 8; j++) {
					b[(i-112)/8]= (byte) (b[(i-112)/8] << 1);
					b[(i-112)/8] = (byte) ((read & 0x1) | ((byte)b[(i-112)/8]));
					if (j != 7)
						read = fis.read();
				}
				//System.out.println(new String(b[(i-112)/8]+""));
				i +=7;
			} else {
				break;
				//Don't do anything
			}
		}
		fis.close();
		
		return new String(b);
	}

	public static String writeToImage(final String path, String msg ) throws IOException {
		byte [] msgB = msg.getBytes("UTF-8");
		File f = new File(new File(path).getParent() + "/newFile.png");
		f.createNewFile();
		FileInputStream fis = new FileInputStream(new File(path));	
		//System.err.println(f.getPath());

		FileOutputStream fos = new FileOutputStream(f, false);
		int read = 0;		
		int msgSize = msgB.length;
		System.out.println("Real size = " + msgSize);
		for (int i = 0;(read = fis.read()) != -1; i++) {
			if (i > 107 && i < 112) {//Store the message size
				byte [] b = allocateBytesForMsgSize(msgSize);
				fos.write(b);
				i = 111;
				fis.read(new byte[3]);
			} else if (i < msgSize*8 + 112 && i >= 112) {
				int x = getByteFromMsg(msgB, i,read);
				fos.write(x);
				//System.out.print(read + " " + x);
			} else {
				fos.write(read);
				//System.out.print(read);
			}
			fos.flush();
			//System.out.println();
		}


		fos.close();
		fis.close();
		
		return f.getPath();
	}

	private static byte[] allocateBytesForMsgSize(int msgSize) {
		byte [] b= new byte[4];
		
		b[0] = (byte) (0xFF & msgSize);
		b[1] = (byte) ((byte) (0xFF & msgSize) >> 8);
		b[2] = (byte) ((byte) (0xFFF & msgSize) >> 16);
		b[3] = (byte) ((byte) (0xFFFF & msgSize) >> 24);
		
		return b;
	}

	public static int getByteFromMsg(byte [] msg, int i, int image) {
		byte b = msg[(i-112)/8];
		int msb = ((b & 0xFF) >> 7) & 0x1;
		msg[(i-112)/8] = (byte) (b << 1);

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
