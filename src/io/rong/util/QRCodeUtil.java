package io.rong.util;

import java.awt.Color;  
import java.awt.Graphics2D;  
import java.awt.Image;  
import java.awt.image.BufferedImage;  
import java.io.File;  
import java.io.IOException;  
import java.util.HashMap;  
import java.util.Map;  
  
import javax.imageio.ImageIO;  
  
import jp.sourceforge.qrcode.QRCodeDecoder;  
import jp.sourceforge.qrcode.exception.DecodingFailedException;  
  
import com.google.zxing.BarcodeFormat;  
import com.google.zxing.Binarizer;  
import com.google.zxing.BinaryBitmap;  
import com.google.zxing.EncodeHintType;  
import com.google.zxing.LuminanceSource;  
import com.google.zxing.MultiFormatReader;  
import com.google.zxing.MultiFormatWriter;  
import com.google.zxing.NotFoundException;  
import com.google.zxing.WriterException;  
import com.google.zxing.common.BitMatrix;  
import com.google.zxing.common.HybridBinarizer;  
import com.swetake.util.Qrcode;  
  
/** 
 * 二维码生成工具类 
 * @author Cloud 
 * @data   2016-12-15 
 * QRCode 
 */  
  
public class QRCodeUtil {  
      
    //二维码颜色  
    private static final int BLACK = 0xFF000000;  
    //二维码颜色  
    private static final int WHITE = 0xFFFFFFFF;  
  
    /** 
     * <span style="font-size:18px;font-weight:blod;">ZXing 方式生成二维码</span> 
     * @param text    <a href="javascript:void();">二维码内容</a> 
     * @param width    二维码宽 
     * @param height    二维码高 
     * @param outPutPath    二维码生成保存路径 
     * @param imageType        二维码生成格式 
     */  
    public static void zxingCodeCreate(String text, int width, int height, String outPutPath, String imageType){  
        Map<EncodeHintType, String> his = new HashMap<EncodeHintType, String>();  
        //设置编码字符集  
        his.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        try {  
            //1、生成二维码  
            BitMatrix encode = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, his);  
              
            //2、获取二维码宽高  
            int codeWidth = encode.getWidth();  
            int codeHeight = encode.getHeight();  
              
            //3、将二维码放入缓冲流  
            BufferedImage image = new BufferedImage(codeWidth, codeHeight, BufferedImage.TYPE_INT_RGB);  
            for (int i = 0; i < codeWidth; i++) {  
                for (int j = 0; j < codeHeight; j++) {  
                    //4、循环将二维码内容定入图片  
                    image.setRGB(i, j, encode.get(i, j) ? BLACK : WHITE);  
                }  
            }  
            File outPutImage = new File(outPutPath);  
            //如果图片不存在创建图片  
            if(!outPutImage.exists())  
                outPutImage.createNewFile();  
            //5、将二维码写入图片  
            ImageIO.write(image, imageType, outPutImage);  
        } catch (WriterException e) {  
            e.printStackTrace();  

        } catch (IOException e) {  
            e.printStackTrace();  

        }  
    }  
      
    /** 
     * <span style="font-size:18px;font-weight:blod;">二维码解析</span> 
     * @param analyzePath    二维码路径 
     * @return 
     * @throws IOException 
     */  
//    @SuppressWarnings({ "rawtypes", "unchecked" })  
//    public static Object zxingCodeAnalyze(String analyzePath) throws Exception{  
//        MultiFormatReader formatReader = new MultiFormatReader();  
//        Object result = null;  
//        try {  
//            File file = new File(analyzePath);  
//            if (!file.exists())  
//            {  
//                return "二维码不存在";  
//            }  
//            BufferedImage image = ImageIO.read(file);  
//            LuminanceSource source = new LuminanceSourceUtil(image);  
//            Binarizer binarizer = new HybridBinarizer(source);    
//            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);  
//            Map hints = new HashMap();  
//            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  
//            result = formatReader.decode(binaryBitmap, hints);  
//        } catch (NotFoundException e) {  
//            e.printStackTrace();  
//        }    
//        return result;  
//    }  
      
    /** 
     * <span style="font-size:18px;font-weight:blod;">QRCode 方式生成二维码</span> 
     * @param content    二维码内容 
     * @param imgPath    二维码生成路径 
     * @param version    二维码版本 
     * @param isFlag    是否生成Logo图片    为NULL不生成 
     */  
    public static void QRCodeCreate(String content, String imgPath, int version, String logoPath){  
         try {    
            Qrcode qrcodeHandler = new Qrcode();    
            //设置二维码排错率，可选L(7%) M(15%) Q(25%) H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小      
            qrcodeHandler.setQrcodeErrorCorrect('L');    
            //N代表数字,A代表字符a-Z,B代表其他字符    
            qrcodeHandler.setQrcodeEncodeMode('B');    
            //版本1为21*21矩阵，版本每增1，二维码的两个边长都增4；所以版本7为45*45的矩阵；最高版本为是40，是177*177的矩阵    
            qrcodeHandler.setQrcodeVersion(version);  
            //根据版本计算尺寸  
            int imgSize = 67 + 12 * (version - 1) ;    
            byte[] contentBytes = content.getBytes("utf-8");
            BufferedImage bufImg = new BufferedImage(imgSize , imgSize ,BufferedImage.TYPE_INT_RGB);    
            Graphics2D gs = bufImg.createGraphics();    
            gs.setBackground(Color.WHITE);    
            gs.clearRect(0, 0, imgSize , imgSize);    
            // 设定图像颜色 > BLACK  
            gs.setColor(Color.BLACK);  
            // 设置偏移量 不设置可能导致解析出错    
            int pixoff = 2;  
            // 输出内容 > 二维码    
            if (contentBytes.length > 0 && contentBytes.length < 130) {  
                boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);  
                for (int i = 0; i < codeOut.length; i++) {  
                    for (int j = 0; j < codeOut.length; j++) {  
                        if (codeOut[j][i]) {    
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);  
                        }    
                    }    
                }    
            } else {    
                //System.err.println("QRCode content bytes length = " + contentBytes.length + " not in [ 0,130 ]. ");    
            }  
           /* 判断是否需要添加logo图片 */  
            if(logoPath != null){  
                File icon = new File(logoPath);  
                if(icon.exists()){  
                    int width_4 = imgSize / 4;  
                    int width_8 = width_4 / 2;  
                    int height_4 = imgSize / 4;  
                    int height_8 = height_4 / 2;  
                    Image img = ImageIO.read(icon);  
                    gs.drawImage(img, width_4 + width_8, height_4 + height_8,width_4,height_4, null);  
                    gs.dispose();  
                    bufImg.flush();  
                }else{  

                }  
  
            }  
  
  
            gs.dispose();  
            bufImg.flush();  
            //创建二维码文件  
            File imgFile = new File(imgPath);  
            if(!imgFile.exists())  
                imgFile.createNewFile();  
            //根据生成图片获取图片  
            String imgType = imgPath.substring(imgPath.lastIndexOf(".") + 1, imgPath.length());  
            // 生成二维码QRCode图片    
            ImageIO.write(bufImg, imgType, imgFile);    
         } catch (Exception e) {    
             e.printStackTrace();    
         }  
    }  
      
    /** 
     * <span style="font-size:18px;font-weight:blod;">QRCode二维码解析</span> 
     * @param codePath    二维码路径 
     * @return    解析结果 
     */  
//    public static String QRCodeAnalyze(String codePath) {  
//        File imageFile = new File(codePath);  
//        BufferedImage bufImg = null;    
//        String decodedData = null;    
//        try {  
//            if(!imageFile.exists())  
//                return "二维码不存在";  
//            bufImg = ImageIO.read(imageFile);  
//            
//            QRCodeDecoder decoder = new QRCodeDecoder();    
//            decodedData = new String(decoder.decode(new ImageUtil(bufImg)), "gb2312");    
//        } catch (IOException e) {    
//
//            e.printStackTrace();    
//        } catch (DecodingFailedException dfe) {    
//
//            dfe.printStackTrace();    
//        }  
//        return decodedData;  
//    }  
    public static void main(String[] args) {
    
             //QRcode 二维码生成测试 
             QRCodeUtil.QRCodeCreate("http://blog.csdn.net/u014266877", "images/qr.jpg", 15, "images/notice.png"); 
           
	}
  
}  