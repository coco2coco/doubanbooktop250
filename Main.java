package com.bug;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // 地址
    private static final String URL = "https://book.douban.com/top250?start=";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "(https|http)://img3\\.doubanio\\.com/(.*?)[^>]*?jpg";

    private static final String NAME_REG="(https|http)://book\\.douban\\.com/subject/(.*?)title=\"(.*)\"";


    public static void main(String[] args) {
        try {
            Main cm=new Main();
            //获得html文本内容
            String HTML = cm.getHtml(URL);
            //获取图片标签
            List<String> imgUrl = cm.getImageUrl(HTML);
            //获取图片src地址
            Map<String,String> imgSrc = cm.getImageSrcMap(imgUrl);
            //下载图片
            cm.Download(imgSrc);

        }catch (Exception e){
            System.out.println("发生错误");
        }

    }

    //获取HTML内容
    private String getHtml(String url)throws Exception{
        StringBuffer sb=new StringBuffer();
        URL url1;
        for(int i=0;i<250;i+=25) {

            url1 = new URL(url + i);

            URLConnection connection=url1.openConnection();
            InputStream in=connection.getInputStream();
            InputStreamReader isr=new InputStreamReader(in);
            BufferedReader br=new BufferedReader(isr);
            String line;
            while((line=br.readLine())!=null){
                sb.append(line,0,line.length());
                sb.append('\n');
            }
            br.close();
            isr.close();
            in.close();
        }
        return sb.toString();

    }

    //获取ImageUrl地址
    private List<String> getImageUrl(String html){
        List<String>listimgurl=new ArrayList<String>();
        while(html.contains("table")&&html.indexOf("table")>0){
            String table = html.substring(html.indexOf("table"),html.indexOf("</table>")+8);
            html=html.substring(html.indexOf(table)+table.length()+1);
            listimgurl.add(table);
        }
        return listimgurl;
    }

       private Map<String,String> getImageSrcMap(List<String> listimageurl){
        Map<String,String> mapImageSrc=new HashMap<>();
        for (String image:listimageurl){
            Matcher matcher=Pattern.compile(IMGSRC_REG).matcher(image);
            Matcher matcherName= Pattern.compile(NAME_REG).matcher(image);
            while (matcher.find()&&matcherName.find()){
                mapImageSrc.put(matcherName.group(3),matcher.group());
            }
        }
        return mapImageSrc;
    }


    private void Download(Map<String,String> srcMap) {
        try {
            //开始时间
            Date begindate = new Date();
            for (Map.Entry map : srcMap.entrySet()) {
                //开始时间
                Date begindate2 = new Date();
                String imageName = (String) map.getKey();
                URL uri = new URL((String)map.getValue());
                InputStream in = uri.openStream();
                FileOutputStream fo = new FileOutputStream(new File("src/book/"+imageName+".jpg"));
                byte[] buf = new byte[1024];
                int length = 0;
                System.out.println("开始下载:" + map.getKey());
                while ((length = in.read(buf, 0, buf.length)) != -1) {
                    fo.write(buf, 0, length);
                }
                in.close();
                fo.close();
                System.out.println(imageName + "下载完成");
                //结束时间
                Date overdate2 = new Date();
                double time = overdate2.getTime() - begindate2.getTime();
                System.out.println("耗时：" + time / 1000 + "s");
            }
            Date overdate = new Date();
            double time = overdate.getTime() - begindate.getTime();
            System.out.println("总耗时：" + time / 1000 + "s");
        } catch (Exception e) {
            System.out.println("下载失败");
        }
    }
}
