package com.util.xgb;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.imageio.ImageIO;


public class CalculateGreenProportion {

    // 计算各hsv分类像素所占比例，传入参数为图片的文件路径
    public static String[] calculateGreen(String image) throws Exception {

        File file = new File(image);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //长宽
        int width = bi.getWidth();
        int height = bi.getHeight();

        //横纵坐标起始点
        int minx = bi.getMinX();
        int miny = bi.getMinY();

        //hsv划分类别像素点个数数组 216种情况
        long[] hsvPartitionCount = new long[216];


        int[] rgb = new int[3];// 定义RGB空间
        float[] hsv = new float[3];// 定义HSV空间

        // 开始遍历所有像素点
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {

                // 当前像素点
                int pixel = bi.getRGB(i, j);

                // 获取RGB各值
                rgb[0] = (pixel & 0xff0000) >> 16;//R
                rgb[1] = (pixel & 0xff00) >> 8;//G
                rgb[2] = (pixel & 0xff);//B

                // rgb转hsv
                Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsv);

                //这里有问题 hPartition[0]恒等于24了
                int[] hPartition = hsvPartition(hsv);
                //System.out.println(Arrays.toString(hPartition));
                int caculPartitionNum = (hPartition[0] - 1) * 9 + (hPartition[1] - 1) * 3 + hPartition[2] - 1; //下标从0开始
                //对应类别累计计数
                hsvPartitionCount[caculPartitionNum]++;
            }
        }

        // 总像素点个数
        long totalPixelNumber = width * height;

        // 获取浮点数表示的占比
        String[] pixelProportion = new String[216];
        for (int i = 0;i < 216 ;i++){
            pixelProportion[i] = translateDoubleIntoPercent((double) hsvPartitionCount[i] / totalPixelNumber);
        }
        //String[][]

        // 返回百分制字符串
        return pixelProportion;
    }

    /**
     * 将浮点数转换为百分制
     * @param d
     * @return
     */
    public static String translateDoubleIntoPercent(double d) {
        BigDecimal bDecimal = new BigDecimal(d);
        bDecimal = bDecimal.setScale(4, BigDecimal.ROUND_HALF_UP);
        DecimalFormat dFormat = new DecimalFormat("0.00%");
        String result = dFormat.format(bDecimal.doubleValue());
        return result;
    }
    /**
     * 根据hsv的值划分hsv类别
     * @param hsv
     * @return
     */
    public static int[] hsvPartition(float[] hsv) {
        int[] hsvPartitionArr = new int[3];
        for (int i = 0;i < 24 ;i++){
            float value1 =  ((float)i * 15 /360);
            float value2 =  ((float)(i + 1) * 15 /360);
            if (hsv[0] >= value1 && hsv[0] < value2){
                hsvPartitionArr[0] = i + 1;
                hsvPartitionArr[1] = ((hsv[1] <= 0.33 ? 0 : 1) + (hsv[1] <= 0.67 ? 0 : 1) + (hsv[1] <= 1 ? 0 : 1)) + 1;
                hsvPartitionArr[2] = ((hsv[2] <= 0.33 ? 0 : 1) + (hsv[2] <= 0.67 ? 0 : 1) + (hsv[2] <= 1 ? 0 : 1)) + 1;
            }
        }
        return  hsvPartitionArr;
    }

    public static void main(String[] args) throws IOException {
        String[] probability = new String[216];
        try {
            probability = calculateGreen("C:\\Users\\90584\\Desktop\\软著证书.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("数组长度:" + probability.length);
        System.out.println("hsv各类别所占比例:");
        int k = 0;
        for (int i = 0;i < 24;i++){
            for (int j = 0;j < 9;j++){
                if (j <=7){
                    System.out.print(probability[k] + '\t');
                }else {
                    System.out.print(probability[k]);
                }
                k++;
            }
            System.out.println();
        }

    }

}
