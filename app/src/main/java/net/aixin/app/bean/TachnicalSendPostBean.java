package net.aixin.app.bean;

import java.util.ArrayList;

/**
 * 发帖类型请求的Bean
 * Created by gzp on 2015/12/8.
 */
public class TachnicalSendPostBean {
    public ArrayList<TachnicalSendPostData> rs;
    public class TachnicalSendPostData{
        public String id;
        public String n;
    }
}
